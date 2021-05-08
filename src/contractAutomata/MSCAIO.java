package contractAutomata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Input/Output 
 * 
 * @author Davide Basile
 *
 */
public class MSCAIO {

	/**
	 * print the description of the CA to a file
	 * 
	 * @throws FileNotFoundException 
	 */
	public static void printToFile(String filename, MSCA aut) throws FileNotFoundException
	{
		if (filename=="")
			throw new IllegalArgumentException("Empty file name");

		PrintWriter pr = new PrintWriter(filename+".data"); 
		pr.print(aut.toString());
		pr.close();
	}


	/**
	 * load a MSCA described in a text file,  
	 * it also loads the must transitions but it does not load the states
	 * 
	 * @param  fileName the name of the file
	 * @return	the MSCA described in the textfile
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static MSCA load(String fileName) throws NumberFormatException, IOException
	{
		// Open the file
		FileInputStream fstream;
		if (fileName.endsWith(".data"))
			fstream = new FileInputStream(fileName);
		else
			throw new IllegalArgumentException("Not a .data format");

		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		int rank=0;
		int[] initial = new int[1];
		int[][] fin = new int[1][];
		Set<MSCATransition> tr = new HashSet<MSCATransition>();
		Set<CAState> states = new HashSet<CAState>();
		Map<Integer,Set<BasicState>> mapBasicStates = new HashMap<>();

		String strLine;
		while ((strLine = br.readLine()) != null)   
		{
			if (strLine.length()>0)
			{
				switch(strLine.substring(0,1))
				{
				case "R":  //Rank Line
				{
					rank = Integer.parseInt(strLine.substring(6));
					break;
				}
				case "I": //Initial state
				{
					initial=Arrays.stream(strLine.split("[\\[\\],]"))
							.map(String::trim)
							.filter(s->  {try { 
								Integer.parseInt(s); 
							} catch(NumberFormatException e) { 
								return false; 
							} return true;})
							.mapToInt(Integer::parseInt)
							.toArray();

					break;
				}
				case "F": //Final state
				{
					fin=Arrays.stream(strLine.split("]"))
							.map(sar->Arrays.stream(sar.split("[,|\\[]"))
									.map(String::trim)
									.filter(s->  {try { 
										Integer.parseInt(s); 
									} catch(NumberFormatException e) { 
										return false; 
									} return true;})
									.mapToInt(Integer::parseInt)
									.toArray())
							.toArray(int[][]::new);

					break;
				}
				case "(": //a may transition
				{
					tr.add(loadTransition(strLine,rank, MSCATransition.Modality.PERMITTED, states,mapBasicStates,initial,fin));
					break;
				}
				case "!": //a must transition
				{
					String stype= strLine.substring(1,2);
					MSCATransition.Modality type=null;
					switch (stype)
					{
					case "U": type=MSCATransition.Modality.URGENT;break;
					case "L": type=MSCATransition.Modality.LAZY;break;
					}
					tr.add(loadTransition(strLine,rank,type,states,mapBasicStates,initial,fin));
					break;
				}
				}
			}
		}
		br.close();

		return new MSCA(tr);
	}

	private static MSCATransition loadTransition(String str, int rank, MSCATransition.Modality type, Set<CAState> states,Map<Integer,Set<BasicState>> mapBasicStates,int[] initial, int[][] fin) throws IOException
	{
		int[] sourcestate=new int[1];
		int[] statestransition = new int[rank];
		String offer=null, request = null;
		Integer offerer=null,requester=null;
		int stateOrLabel=0;

		for (String el : str.split("]"))
		{
			int princ=0;
			statestransition = new int[rank];
			Scanner s = new Scanner(el);
			s.useDelimiter(",|\\[| ");
			while (s.hasNext())
			{
				if (princ>=rank)
				{
					s.close();
					throw new IOException("Ill-formed transitions, different ranks");
				}

				if (stateOrLabel==1)//label
				{
					String action=s.next();
					if (action.contains(CALabel.idle)) 
						princ++;
					else 
						if (action.contains(CALabel.offer))
						{
							offerer=princ;
							offer=action.substring(action.indexOf(CALabel.offer));
							princ++;
						}
						else if (action.contains(CALabel.request))
						{
							requester=princ;
							request=action.substring(action.indexOf(CALabel.request));
							princ++;
						}
				}
				else //source or target
				{
					if (s.hasNextInt())
					{
						statestransition[princ]=s.nextInt();
						princ++;
					}
					else 
						s.next();
				}
			}
			s.close();
			if (princ!=rank)
				throw new IOException("Ill-formed transitions, different ranks");

			if (stateOrLabel==2)//target state
				break;
			else if (stateOrLabel==0)
				sourcestate=statestransition; //source state
			stateOrLabel++;

		}

		CAState source = createOrLoadState(states,mapBasicStates,sourcestate,initial, fin);//source
		CAState target = createOrLoadState(states,mapBasicStates,statestransition,initial, fin);//target

		CALabel label;
		if (offerer!=null&&requester!=null&&offer!=null&&offer.startsWith(CALabel.offer))
			label = new CALabel(source.getRank(),offerer,requester,offer);
		else if (offerer!=null&&requester==null&&offer!=null&&offer.startsWith(CALabel.offer))
			label = new CALabel(source.getRank(),offerer, offer);
		else if (offerer==null&&requester!=null&&request!=null&&request.startsWith(CALabel.request))
			label = new CALabel(source.getRank(),requester,request);
		else
			throw new IOException("Problem in loading a label");

		return new MSCATransition(source,label,target,type); 
	}

	private static CAState createOrLoadState(Set<CAState> states,Map<Integer,Set<BasicState>> mapBasicStates, int[] state,int[] initial, int[][] fin) throws IOException {

		return states.stream()
				.filter(cs->IntStream.range(0, cs.getState().size())
						.allMatch(i->Integer.parseInt(cs.getState().get(i).getLabel())==state[i])) 
				.findAny()
				.orElseGet(()->{
					CAState temp= new CAState(
							IntStream.range(0, state.length)
							.mapToObj(i->{
								Set<BasicState> l = mapBasicStates.get(i);
								if (l==null || l.stream().allMatch(bs->Integer.parseInt(bs.getLabel())!=state[i]))
								{
									BasicState bs=new BasicState(state[i]+"",
											state[i]==initial[i],
											Arrays.stream(fin[i]).anyMatch(id->id==state[i]));
									if (l==null)
										mapBasicStates.put(i, new HashSet<BasicState>(Arrays.asList(bs)));
									else
										l.add(bs);
									return (BasicState) bs;
								} else
									return (BasicState) l.stream()
											.filter(bs->Integer.parseInt(bs.getLabel())==state[i])
											.findFirst()
											.orElseThrow(RuntimeException::new);
							}).collect(Collectors.toList())
							,0,0); 							
					states.add(temp); return temp;});
	}



	/**
	 * parse the mxGraphModel XML description (used by the mxGraph) into an MSCA object
	 * 
	 * @param filename  the XML file name
	 * @return the MSCA parsed from the XML
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public static MSCA parseXMLintoMSCA(String filename) throws ParserConfigurationException, SAXException, IOException
	{
		File inputFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();
		NodeList nodeList = (NodeList) doc.getElementsByTagName("mxCell");

		Map<Integer,Set<BasicState>> princ2bs = new HashMap<>();
		Map<Integer, CAState> id2castate = new HashMap<>();
		Set<MSCATransition> transitions= new HashSet<MSCATransition>();

		//first read all basic states and castates, then all the edges.
		for (int i = 0; i < nodeList.getLength(); i++) 
		{
			Node nNode = nodeList.item(i);
			if ((nNode.getNodeType() == Node.ELEMENT_NODE))
			{
				Element eElement = (Element) nNode;
				if (Integer.parseInt(eElement.getAttribute("id"))>1 && !eElement.hasAttribute("edge"))
				{
					String value = eElement.getAttribute("value");
					if (value.startsWith("principal"))//basic state
					{
						Integer principal = Integer.parseInt(value.substring(value.indexOf("=")+1, value.indexOf(",")));//first entry is principal
						BasicState bs = BasicState.readCSV(value.substring(value.indexOf(",")+1));
							
						if (princ2bs.containsKey(principal))
							princ2bs.get(principal).add(bs);
						else 
							princ2bs.put(principal, new HashSet<BasicState>(Arrays.asList(bs)));
					}
					else {//castate
						Element geom= (Element) (NodeList)eElement.getElementsByTagName("mxGeometry").item(0);
						int[] st=Arrays.stream(eElement.getAttribute("value").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(","))
								.mapToInt(Integer::parseInt)
								.toArray();
						//\forall i. \exists bs \in princ2bs(i). bs==st[i]

						List<BasicState> lbs = IntStream.range(0, st.length)
						.mapToObj(ind->princ2bs.get(ind).stream()
								.filter(bs->bs.getLabel().equals(st[ind]+""))
								.findFirst()
								.orElseThrow(RuntimeException::new))
						.collect(Collectors.toList());
						CAState castate = new CAState(lbs, 
								Float.parseFloat(geom.getAttribute("x")),
										Float.parseFloat(geom.getAttribute("y")));
								//useful when not morphing (e.g. adding handles to edges)					
								
						if (castate.isFinalstate()!=eElement.getAttribute("style").contains("terminate.png"))
							throw new IOException("Problems with final states in .mxe");

						if (id2castate.put(Integer.parseInt(eElement.getAttribute("id")), castate)!=null)
							throw new IOException("Duplicate states!");
					}
				}
			}
		}

		if (id2castate.isEmpty())
			throw new IOException("No states!");

//		Set<CAState> castates = id2castate.entrySet().stream()
//				.map(Entry::getValue)
//				.collect(Collectors.toSet());

		//transitions
		for (int i = 0; i < nodeList.getLength(); i++) 
		{
			Node nNode = nodeList.item(i);
			if ((nNode.getNodeType() == Node.ELEMENT_NODE))
			{
				Element eElement = (Element) nNode;	

				if (Integer.parseInt(eElement.getAttribute("id"))>1 && eElement.hasAttribute("edge")) {
					List<String> labels = Arrays.asList(eElement.getAttribute("value").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(","));
					if (labels.stream().anyMatch(item->!(item.startsWith(CALabel.offer)||item.startsWith(CALabel.request)||item.startsWith(CALabel.idle))))
						throw new IOException("Ill-formed action");
					transitions.add(new MSCATransition(id2castate.get(Integer.parseInt(eElement.getAttribute("source"))),
							new CALabel(labels),//label
							id2castate.get(Integer.parseInt(eElement.getAttribute("target"))), 
							(eElement.getAttribute("style").contains("strokeColor=#FF0000"))? MSCATransition.Modality.URGENT: //red
								(eElement.getAttribute("style").contains("strokeColor=#00FF00"))? MSCATransition.Modality.LAZY: //green
									MSCATransition.Modality.PERMITTED));
				}
			}
		}

		MSCA aut= new MSCA(transitions);
		return aut;
	}

	/**
	 * save the MSCA aut as a mxGraphModel  (used by mxGraph) File with XML extension
	 * @param fileName the name of the xml file where to write the automaton
	 * @param aut the automaton to be saved
	 * @return the File containing the xml for the automaton aut
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 */
	public static File convertMSCAintoXML(String fileName, MSCA aut) throws ParserConfigurationException, TransformerException
	{
		DocumentBuilderFactory dbFactory =
				DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = 
				dbFactory.newDocumentBuilder();
		Document doc = dBuilder.newDocument();
		// root element
		Element rootElement = doc.createElement("mxGraphModel");
		doc.appendChild(rootElement);

		Element root = doc.createElement("root");
		rootElement.appendChild(root);


		Element mxcell0 = doc.createElement("mxCell");
		mxcell0.setAttribute("id", "0");
		root.appendChild(mxcell0);
		Element mxcell1 = doc.createElement("mxCell");
		mxcell1.setAttribute("id", "1");
		mxcell1.setAttribute("parent", "0");
		root.appendChild(mxcell1);

		int id=2;

		for (Entry<Integer,BasicState> e : 
			aut.getBasicStates().entrySet().stream()
			.flatMap(e-> e.getValue().stream()
					.map(bs->new AbstractMap.SimpleEntry<Integer,BasicState>(e.getKey(),bs)))
			.collect(Collectors.toSet()))
		{
			createElementBasicState(doc, root,Integer.toString(id), e.getKey(),e.getValue());
			id+=1;
		}

		Map<CAState,Element> state2element = new HashMap<CAState, Element>();

		for (CAState s : aut.getStates())
		{
			state2element.put(s, createElementState(doc, root,Integer.toString(id), s));
			id+=1;
		}

		Set<? extends MSCATransition> tr= aut.getTransition();
		for (MSCATransition t : tr)
		{
			createElementEdge(doc,root,Integer.toString(id),
					state2element.get(t.getSource()),
					state2element.get(t.getTarget()),
					Arrays.toString(t.getLabelAsList().stream().toArray(String[]::new)),t.getModality());
			id+=1;
		}

		TransformerFactory transformerFactory =
				TransformerFactory.newInstance();
		Transformer transformer =
				transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		if (fileName.contains("."))
			fileName=fileName.substring(0,fileName.indexOf("."));
		File file = new File(fileName+".mxe");
		StreamResult result =
				new StreamResult(file);
		transformer.transform(source, result);

		return file;
	}

	private static Element createElementEdge(Document doc, Element root,String id, Element source, Element target,String label,MSCATransition.Modality type)
	{
		Attr parent=doc.createAttribute("parent");
		parent.setValue("1");
		Attr style=doc.createAttribute("style");

		if (type==MSCATransition.Modality.URGENT)
			style.setValue("straight;strokeColor=#FF0000");
		else if (type==MSCATransition.Modality.LAZY)
			style.setValue("straight;strokeColor=#00FF00");
		else
			style.setValue("straight");
		Attr id1=doc.createAttribute("id");
		Attr as=doc.createAttribute("as");
		as.setValue("geometry");
		id1.setValue(id);

		Element mxcell1 = doc.createElement("mxCell");
		mxcell1.setAttribute("edge","1");
		mxcell1.setAttributeNode(id1);
		mxcell1.setAttributeNode(parent);
		mxcell1.setAttributeNode(style);
		mxcell1.setAttribute("source", source.getAttribute("id"));
		mxcell1.setAttribute("target", target.getAttribute("id"));
		mxcell1.setAttribute("value", label);

		Element mxGeometry1=doc.createElement("mxGeometry");
		mxGeometry1.setAttributeNode(as);
		mxGeometry1.setAttribute("relative","1");

		Element mxPointSource=doc.createElement("mxPoint");
		mxPointSource.setAttribute("as","sourcePoint");
		mxPointSource.setAttribute("x", ((Element)source.getChildNodes().item(0)).getAttribute("x"));
		mxPointSource.setAttribute("y", ((Element)source.getChildNodes().item(0)).getAttribute("y"));
		mxGeometry1.appendChild(mxPointSource);

		Element mxPointTarget=doc.createElement("mxPoint");
		mxPointTarget.setAttribute("as","targetPoint");
		mxPointTarget.setAttribute("x", ((Element)target.getChildNodes().item(0)).getAttribute("x"));
		mxPointTarget.setAttribute("y", ((Element)target.getChildNodes().item(0)).getAttribute("y"));
		mxGeometry1.appendChild(mxPointTarget);


		//source and target are created using createElementState: they have x and y attributes
		//		if (((Element)source.getChildNodes().item(0)).hasAttribute("x"))
		//		{
		//			mxPointSource.setAttribute("x", ((Element)source.getChildNodes().item(0)).getAttribute("x"));
		//		}
		//		else
		//			mxPointSource.setAttribute("x", "0.0");

		//		if (((Element)source.getChildNodes().item(0)).hasAttribute("y"))
		//		{
		//			mxPointSource.setAttribute("y", ((Element)source.getChildNodes().item(0)).getAttribute("y"));
		//		}
		//		else
		//			mxPointSource.setAttribute("y", "0.0");

		//		mxGeometry1.appendChild(mxPointSource);
		//		Element mxPointTarget=doc.createElement("mxPoint");
		//		mxPointTarget.setAttribute("as","targetPoint");

		//		if (((Element)target.getChildNodes().item(0)).hasAttribute("x"))
		//			mxPointTarget.setAttribute("x", ((Element)target.getChildNodes().item(0)).getAttribute("x"));
		//		else
		//		{
		//			Attr x=doc.createAttribute("x");
		//			x.setNodeValue("0.0");
		//			mxPointTarget.setAttributeNode(x);
		//		}

		//		if (((Element)target.getChildNodes().item(0)).hasAttribute("y"))
		//			mxPointTarget.setAttribute("y", ((Element)target.getChildNodes().item(0)).getAttribute("y"));
		//		else
		//		{
		//			Attr y=doc.createAttribute("y");
		//			y.setNodeValue("0.0");
		//			mxPointTarget.setAttributeNode(y);
		//		}


		Element pointArray=doc.createElement("Array");
		pointArray.setAttribute("as","points");
		Element mxPoint=doc.createElement("mxPoint");


		float xs=Float.parseFloat(((Element)source.getChildNodes().item(0)).getAttribute("x"));
		float xt=Float.parseFloat(((Element)target.getChildNodes().item(0)).getAttribute("x"));
		float ys=Float.parseFloat(((Element)source.getChildNodes().item(0)).getAttribute("y"));	
		float yt=Float.parseFloat(((Element)target.getChildNodes().item(0)).getAttribute("y"));

		//		if (((Element)source.getChildNodes().item(0)).hasAttribute("x"))
		//		{
		//			xs=Float.parseFloat(((Element)source.getChildNodes().item(0)).getAttribute("x"));
		//		}
		//		if (((Element)target.getChildNodes().item(0)).hasAttribute("x"))
		//		{
		//			xt=Float.parseFloat(((Element)target.getChildNodes().item(0)).getAttribute("x"));
		//		}
		//
		//		if (((Element)source.getChildNodes().item(0)).hasAttribute("y"))
		//		{
		//			ys=Float.parseFloat(((Element)source.getChildNodes().item(0)).getAttribute("y"));	
		//		}
		//
		//		if (((Element)target.getChildNodes().item(0)).hasAttribute("y"))
		//		{
		//			yt=Float.parseFloat(((Element)target.getChildNodes().item(0)).getAttribute("y"));
		//		}


		float coordinate=(xs+xt)/2;
		mxPoint.setAttribute("x", coordinate+"");

		coordinate=(ys+yt)/2;
		mxPoint.setAttribute("y", coordinate+"");
		pointArray.appendChild(mxPoint);

		mxGeometry1.appendChild(pointArray);
		mxcell1.appendChild(mxGeometry1);
		root.appendChild(mxcell1);
		return mxcell1;		
	}

	private static Element createElementBasicState(Document doc, Element root,String id, Integer principal, BasicState bs)
	{
		Attr parent=doc.createAttribute("parent");
		parent.setValue("1");

		Element mxcell1 = doc.createElement("mxCell");

		mxcell1.setAttributeNode(parent);

		Attr id1=doc.createAttribute("id");		
		id1.setValue(id);
		mxcell1.setAttributeNode(id1);

		mxcell1.setAttribute("value", "principal="+principal+
				","+bs.toCSV());


		root.appendChild(mxcell1);
		return mxcell1;		
	}

	/**
	 * @param doc
	 * @param root
	 * @param id
	 * @param castates
	 * @param arrintstate
	 * @return
	 */
	private static Element createElementState(Document doc, Element root,String id, CAState castate)
	{
		Attr parent=doc.createAttribute("parent");
		parent.setValue("1");
		Attr style=doc.createAttribute("style");
		if (!castate.isFinalstate())
			style.setValue("roundImage;image=/com/mxgraph/examples/swing/images/event.png");
		else
			style.setValue("roundImage;image=/com/mxgraph/examples/swing/images/terminate.png");

		Attr value=doc.createAttribute("value");
		value.setValue(castate.getState().toString());

		Element mxcell1 = doc.createElement("mxCell");
		Attr id1=doc.createAttribute("id");
		Attr vertex=doc.createAttribute("vertex");
		vertex.setNodeValue("1");		
		id1.setValue(id);
		mxcell1.setAttributeNode(id1);
		mxcell1.setAttributeNode(parent);
		mxcell1.setAttributeNode(style);
		mxcell1.setAttributeNode(value);
		mxcell1.setAttributeNode(vertex);

		Element mxGeometry1=doc.createElement("mxGeometry");
		Attr as=doc.createAttribute("as");
		as.setValue("geometry");
		mxGeometry1.setAttributeNode(as);
		mxGeometry1.setAttribute("height", "50.0");
		mxGeometry1.setAttribute("width", "50.0");


		//createElement does not set attributes x and y
		//		if (!mxGeometry1.hasAttribute("x"))
		//		{
		Attr x=doc.createAttribute("x");
		x.setNodeValue(castate.getX()+"");
		mxGeometry1.setAttributeNode(x);

		//		}
		//		else
		//			mxGeometry1.setAttribute("x", castate.getX()+"");

		//		if (!mxGeometry1.hasAttribute("y"))
		//		{
		Attr y=doc.createAttribute("y");
		y.setNodeValue(castate.getY()+"");
		mxGeometry1.setAttributeNode(y);

		//		}
		//		else
		//			mxGeometry1.setAttribute("y", castate.getY()+"");

		mxcell1.appendChild(mxGeometry1);
		root.appendChild(mxcell1);
		return mxcell1;		
	}
}

//END OF CLASS


//	private static CAState getCAStateWithValue(int[] value, Set<CAState> states)
//	{
//		if (states.parallelStream()
//				.filter(x->hasSameBasicStateLabelsOf(x, value))//Arrays.equals(x.getState(),value))
//				.count()>1)
//			throw new IllegalArgumentException("Bug: Ambiguous states: there is more than one state with value "+Arrays.toString(value));
//
//		return states.parallelStream()
//				.filter(x->hasSameBasicStateLabelsOf(x, value))//Arrays.equals(x.getState(),value))
//				.findFirst()
//				.orElseThrow(IllegalArgumentException::new);
//	}

///**
//* parse the mxGraphModel XML description (used by the mxGraph) into an MSCA object
//* 
//* 
//* @param filename  the XML file name
//* @return the MSCA parsed from the XML
//* @throws ParserConfigurationException 
//* @throws SAXException 
//* @throws IOException 
//*/
//public static MSCA parseXMLintoMSCAold(String filename) throws ParserConfigurationException, SAXException, IOException
//{
//	File inputFile = new File(filename);
//	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//	Document doc = dBuilder.parse(inputFile);
//	doc.getDocumentElement().normalize();
//	NodeList nodeList = (NodeList) doc.getElementsByTagName("mxCell");
//
//	Map<Integer, CAState> id2castate = new HashMap<>(nodeList.getLength());
//	Set<MSCATransition> transitions= new HashSet<MSCATransition>();
//
//	//first read all the states, then all the edges
//	for (int i = 0; i < nodeList.getLength(); i++) 
//	{
//		Node nNode = nodeList.item(i);
//		if ((nNode.getNodeType() == Node.ELEMENT_NODE))
//		{
//			Element eElement = (Element) nNode;
//			if (Integer.parseInt(eElement.getAttribute("id"))>1 && !eElement.hasAttribute("edge"))
//			{
//				Element geom= (Element) (NodeList)eElement.getElementsByTagName("mxGeometry").item(0);
//				int[] st=Arrays.stream(eElement.getAttribute("value").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(","))
//						.mapToInt(Integer::parseInt)
//						.toArray();
//				CAState castate=new CAState(st,
//						geom.hasAttribute("x")?Float.parseFloat(geom.getAttribute("x")):0,
//								geom.hasAttribute("y")?Float.parseFloat(geom.getAttribute("y")):0,//useful when not morphing (e.g. adding handles to edges)
//										Arrays.equals(st, new int[st.length]), //initial
//										eElement.getAttribute("style").contains("terminate.png"));//final
//				if (id2castate.put(Integer.parseInt(eElement.getAttribute("id")), castate)!=null)
//					throw new IOException("Duplicate states!");
//			}
//		}
//	}
//
//	if (id2castate.isEmpty())
//		throw new IOException("No states!");
//
//	Set<CAState> castates = id2castate.entrySet().stream()
//			.map(Entry::getValue)
//			.collect(Collectors.toSet());
//
//	//transitions
//	for (int i = 0; i < nodeList.getLength(); i++) 
//	{
//		Node nNode = nodeList.item(i);
//		if ((nNode.getNodeType() == Node.ELEMENT_NODE))
//		{
//			Element eElement = (Element) nNode;	
//
//			if (Integer.parseInt(eElement.getAttribute("id"))>1 && eElement.hasAttribute("edge")) {
//				List<String> labels = Arrays.asList(eElement.getAttribute("value").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(","));
//				if (labels.stream().anyMatch(item->!(item.startsWith(CALabel.offer)||item.startsWith(CALabel.request)||item.startsWith(CALabel.idle))))
//					throw new IOException("Ill-formed action");
//				transitions.add(new MSCATransition(id2castate.get(Integer.parseInt(eElement.getAttribute("source"))),
//						new CALabel(labels),//label
//						id2castate.get(Integer.parseInt(eElement.getAttribute("target"))), 
//						(eElement.getAttribute("style").contains("strokeColor=#FF0000"))? MSCATransition.Modality.URGENT: //red
//							(eElement.getAttribute("style").contains("strokeColor=#00FF00"))? MSCATransition.Modality.LAZY: //green
//								MSCATransition.Modality.PERMITTED));
//			}
//		}
//	}
//
//	int rank=castates.iterator().next().getState().size();
//	MSCA aut= new MSCA(rank, 
//			principalsFinalStates(castates.stream()
//					.filter(CAState::isFinalstate)
//					.map(s->s.getState().stream()
//							.mapToInt(bs->Integer.parseInt(bs.getLabel()))
//							.toArray())
//					.collect(Collectors.toList())),
//			transitions);
//	return aut;
//}
///**
//* save the MSCA aut as a mxGraphModel  (used by mxGraph) File with XML extension
//* @param fileName the name of the xml file where to write the automaton
//* @param aut the automaton to be saved
//* @return the File containing the xml for the automaton aut
//* @throws ParserConfigurationException 
//* @throws TransformerException 
//*/
//public static File convertMSCAintoXMLold(String fileName, MSCA aut) throws ParserConfigurationException, TransformerException
//{
//	DocumentBuilderFactory dbFactory =
//			DocumentBuilderFactory.newInstance();
//	DocumentBuilder dBuilder = 
//			dbFactory.newDocumentBuilder();
//	Document doc = dBuilder.newDocument();
//	// root element
//	Element rootElement = doc.createElement("mxGraphModel");
//	doc.appendChild(rootElement);
//
//	Element root = doc.createElement("root");
//	rootElement.appendChild(root);
//
//
//	Element mxcell0 = doc.createElement("mxCell");
//	mxcell0.setAttribute("id", "0");
//	root.appendChild(mxcell0);
//	Element mxcell1 = doc.createElement("mxCell");
//	mxcell1.setAttribute("id", "1");
//	mxcell1.setAttribute("parent", "0");
//	root.appendChild(mxcell1);
//
//	Map<CAState,Element> state2element = new HashMap<CAState, Element>();
//
//	int id=2;
//	for (CAState s : aut.getStates())
//	{
//		state2element.put(s, createElementState(doc, root,Integer.toString(id), s));
//		id+=1;
//	}
//
//	Set<? extends MSCATransition> tr= aut.getTransition();
//	for (MSCATransition t : tr)
//	{
//		createElementEdge(doc,root,Integer.toString(id),
//				state2element.get(t.getSource()),
//				state2element.get(t.getTarget()),
//				Arrays.toString(t.getLabelAsList().stream().toArray(String[]::new)),t.getModality());
//		id+=1;
//	}
//
//	TransformerFactory transformerFactory =
//			TransformerFactory.newInstance();
//	Transformer transformer =
//			transformerFactory.newTransformer();
//	DOMSource source = new DOMSource(doc);
//	if (fileName.contains("."))
//		fileName=fileName.substring(0,fileName.indexOf("."));
//	File file = new File(fileName+".mxe");
//	StreamResult result =
//			new StreamResult(file);
//	transformer.transform(source, result);
//
//	return file;
//}
