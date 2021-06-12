package family;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FeatureIDEfamilyConverter implements FamilyConverter {

	/**
	 * loads the list of products generated through FeatureIDE
	 * 
	 * @param currentdir
	 * @param filename
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@Override
	public Family importFamily(String filename) throws ParserConfigurationException, SAXException, IOException
	{	
		Set<String> features=parseFeatures(filename);
		String[][] eq = detectDuplicates(filename);
		for (int i=0;i<eq.length;i++)
		{
			if (features.contains(eq[i][0])&&features.contains(eq[i][1]))
				features.remove(eq[i][1]);
		}

		
		File folder = new File(filename.substring(0, filename.lastIndexOf(File.separator))+File.separator+"products"+File.separator);
		
		List<File> listOfFiles = Arrays.asList(folder.listFiles());

		Set<Product> setprod=listOfFiles.parallelStream()
				.map(f->{
					if (f.isFile()&&f.getName().contains("config"))
						return f.getAbsolutePath();//no sub-directory on products
					if (f.isDirectory())
					{
						File[] ff = f.listFiles();
						if (ff!=null && ff.length>0 && ff[0]!=null && ff[0].isFile()&&ff[0].getName().contains("config"))//each product has its own sub-directory
							return ff[0].getAbsolutePath();//this condition is never satisfied in the tests
					}
					return "";	
				})
				.filter(s->s.length()>0)
				.map(s->{
					try {
						return Files.readAllLines(Paths.get(s), Charset.forName("ISO-8859-1"));//required features
					} catch (IOException e) {
						IllegalArgumentException iae = new IllegalArgumentException();
						iae.initCause(e);
						throw iae;
					}
				})
				.map(l->{
					return new Product(features.parallelStream()
							.filter(s->l.contains(s))//required
							.map(s->new Feature(s))
							.collect(Collectors.toSet()),
							features.parallelStream()
							.filter(s->!l.contains(s))//forbidden
							.map(s->new Feature(s))
							.collect(Collectors.toSet()));})
				.collect(Collectors.toSet());
		
			return new Family(generateProducts(setprod,features));

	}
	
	//TODO  avoid generation of super-products below, use the orchestration of the paired MSCA as plant, 
	//	    discard features not present in the aut, as well as products requiring those features. 
	//		order the remaining products only by considering forbidden actions.
	/* 
	 * given two products p1 p2 identical but for a feature f activated in one 
	 * and deactivated in the other, a super product (a.k.a. sub-family) is generated such that f is left unresolved. 
	 * This method generates all possible super products. 
	 * It is required that all super products are such that the corresponding feature model formula is satisfied. 
	 * This condition holds for the method.
	 * Indeed, assume the feature model formula is in CNF, it is never the case that f is the only literal of a 
	 * disjunct (i.e. a truth value must be assigned to f); otherwise either p1 or p2 
	 * is not a valid product (p1 if f is negated in the disjunct, p2 otherwise).
	 */
	private Set<Product> generateProducts(Set<Product> setprod, Set<String> features){
		return Stream.iterate(setprod, s->!s.isEmpty(), sp->{
			Map<Product,Set<Product>> map = features.stream()
					.map(f->sp.parallelStream()
							.collect(Collectors.groupingByConcurrent(p->p.removeFeature(new Feature(f)), Collectors.toSet())))
					.reduce(new ConcurrentHashMap<Product,Set<Product>>(),(x,y)->{x.putAll(y); return x;});	
			return map.entrySet().parallelStream()
					.filter(e->e.getValue().size()>1)
					.map(Entry::getKey)
					.collect(Collectors.toSet());})
		.reduce(new HashSet<Product>(),(x,y)->{x.addAll(y); return x;});
	}
	private Set<String> parseFeatures(String filename) throws ParserConfigurationException, SAXException, IOException
	{
		File inputFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();

		NodeList nodeList = (NodeList) doc.getElementsByTagName("feature");

		Set<String> features=new HashSet<>();
		for (int i = 0; i < nodeList.getLength(); i++) 
		{
			Node nNode = nodeList.item(i);
			if ((nNode.getNodeType() == Node.ELEMENT_NODE))//&&(nNode.getNodeName()=="mxCell")) {
				features.add(((Element) nNode).getAttribute("name"));    
		}
		return features;		
	}

	/**
	 * reads all iff constraints (eq node) and returns a table such that forall i table[i][0] equals table[i][1]
	 */
	private String[][] detectDuplicates(String filename) throws ParserConfigurationException, SAXException, IOException
	{
		File inputFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();

		NodeList nodeList = (NodeList) doc.getElementsByTagName("eq");

		String[][] table= new String[nodeList.getLength()][2]; //exact length

		int ind =0;
		for (int i = 0; i < nodeList.getLength(); i++) 
		{
			Node nNode = nodeList.item(i);
			if ((nNode.getNodeType() == Node.ELEMENT_NODE))//&&(nNode.getNodeName()=="mxCell")) {
			{
				NodeList childs = (NodeList) nNode.getChildNodes();
				Node first = childs.item(1);
				Node second = childs.item(3);
				table[ind][0]= first.getTextContent();    
				table[ind][1]= second.getTextContent();          
				ind++;
			}       
		}
		return table;		
	}



	@Override
	public void exportFamily(String filename, Family fam) throws IOException {
		throw new UnsupportedOperationException();
	}

}
