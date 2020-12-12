package MSCA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import CA.CALabel;
import CA.CAState;

/**
 * Input/Output 
 * 
 * @author Davide Basile
 *
 */
public class MSCAIO {

	/**
	 * print the description of the CA to a file
	 * @throws FileNotFoundException 
	 */
	public static void printToFile(String filename, MSCA aut) throws FileNotFoundException
	{
		if (filename=="")
		{
			throw new IllegalArgumentException("Empty file name");
		}
//		int rank= aut.getRank();
//		int[][] finalstates = aut.getFinalStatesofPrincipals();

		PrintWriter pr = new PrintWriter(filename+".data"); 
//		pr.println("Rank: "+rank);
//		pr.println("Number of states: "+Arrays.toString(aut.getNumStatesPrinc()));
//		pr.println("Initial state: " +Arrays.toString(aut.getInitial().getState()));
//		pr.print("Final states: [");
//		for (int i=0;i<finalstates.length;i++)
//			pr.print(Arrays.toString(finalstates[i]));
//		pr.print("]\n");
//		pr.println("Transitions: \n");
//		Set<? extends MSCATransition> tr = aut.getTransition();
//		if (tr!=null)
//		{
//			for (MSCATransition t : tr)
//				pr.println(t.toString());
//		}
		pr.print(aut.toString());
		pr.close();

	}


	/*
	 * public static File loadMSCAAndWriteIntoXML(String fileName) { return
	 * convertMSCAintoXML(fileName, load(fileName)); }
	 */

	/**
	 * load a MSCA described in a text file,  
	 * it also loads the must transitions but it does not load the states
	 * this method is only used by loadMSCAAndWriteIntoXML
	 * 
	 * @param the name of the file
	 * @return	the CA loaded
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
			fstream = new FileInputStream(fileName+".data");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		int rank=0;
		int[] initial = new int[1];
		int[] numstates = new int[1]; //TODO this can be removed
		int[][] fin = new int[1][]; //rank initialization later
		Set<MSCATransition> tr = new HashSet<MSCATransition>();
		Set<CAState> states = new HashSet<CAState>();
		String strLine;
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   
		{
			if (strLine.length()>0)
			{
				switch(strLine.substring(0,1))
				{
				case "R":  //Rank Line
				{
					String srank = strLine.substring(6);
					rank = Integer.parseInt(srank);
					initial = new int[rank];
					numstates = new int[rank];
					fin = new int[rank][];
					break;
				}
				case "N":	//Number of states line
				{
					String[] arr=strLine.split("[\\[\\],]");
					int i=0;

					for (int ind=0;ind<arr.length;ind++)
					{
						try{
							int num=Integer.parseInt(arr[ind].trim());
							numstates[i] = num;
							i++; //incremented when there are no exceptions
						}catch(NumberFormatException e){}
					}
				}
				case "I": //Initial state
				{
					String[] arr=strLine.split("[\\[\\],]");
					int i=0;
					for (int ind=0;ind<arr.length;ind++)
					{
						try{
							int num=Integer.parseInt(arr[ind].trim());
							initial[i] = num;
							i++;
						}catch(NumberFormatException e){}
					}
					break;
				}
				case "F": //Final state
				{
					String[] arr=strLine.split("]");  
					int outerindex=0;

					for (int ind=0;ind<arr.length;ind++)
					{

						String[] arr2=arr[ind].split("[,|\\[]"); //FIXME BUG with this parseInt fails with spaces
						try{
							int innerindex=0;
							int[] tf = new int[numstates[outerindex]]; //upper bound
							for(int ind2=0;ind2<arr2.length;ind2++)
							{
								try{
									int num=Integer.parseInt(arr2[ind2].trim());
									tf[innerindex] = num;
									innerindex++;
								}catch(NumberFormatException e){} //skip values that are not numbers
							}
							fin[outerindex]=MSCAUtils.removeTailsNull(tf, innerindex); 
							outerindex++;
						}catch(NumberFormatException e){}
					}
					break;
				}
				case "(": //a may transition
				{
					tr.add(loadTransition(strLine,rank, MSCATransition.Modality.PERMITTED, states));
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
					tr.add(loadTransition(strLine,rank,type,states));
					break;
				}
				}
			}
		}
		br.close();

		return new MSCA(rank,new CAState(initial, true, false),//numstates,
				fin,tr,states);
	}

	private static MSCATransition loadTransition(String str, int rank, MSCATransition.Modality type, Set<CAState> states)
	{
		int what=0;
		String[] ss=str.split("]");
		int[][] store=new int[1][];
		String[] arrlabel = new String[rank];
		String offer=null, request = null;
		Integer offerer=null,requester=null;
		for (int i=0;i<ss.length;i++)
		{
			int[] statestransition = new int[rank];
			Scanner s = new Scanner(ss[i]);
			s.useDelimiter(",|\\[| ");
			int j=0;
			while (s.hasNext())
			{
				if (what==0||what==2)//source or target
				{
					if (s.hasNextInt())
					{
						statestransition[j]=s.nextInt();
						j++;
					}
					else {
						s.next();
					}
				}
				else
				{
					if (s.hasNext())
					{
						String action=s.next();
						if (action.contains(CALabel.idle))
							arrlabel[j]=CALabel.idle;
						else if (action.contains(CALabel.offer))
						{
							arrlabel[j]=action.substring(action.indexOf(CALabel.offer));
							offerer=j;
							offer=action.substring(action.indexOf(CALabel.offer));//FIXME?
						}
						else if (action.contains(CALabel.request))
						{
							arrlabel[j]=action.substring(action.indexOf(CALabel.request));
							requester=j;
							request=action.substring(action.indexOf(CALabel.request));//FIXME?
						}
						else
							j--; //trick for not increasing the counter j

						j++;
					}
					else {
						s.next();
					}
				}
			}
			s.close();
			if (what==2)
			{
				CAState source = states.stream()
						.filter(x->Arrays.equals(x.getState(), store[0])) //source
						.findAny()
						.orElseGet(()->{CAState temp= new CAState(store[0]); states.add(temp); return temp;});

				CAState target = states.stream()
						.filter(x->Arrays.equals(x.getState(), statestransition)) //target
						.findAny()
						.orElseGet(()->{CAState temp= new CAState(statestransition); states.add(temp); return temp;});

				CALabel label;
				if (offerer!=null&&requester!=null&&offer!=null&&offer.startsWith(CALabel.offer))
					label = new CALabel(source.getRank(),offerer,requester,offer);
				else if (offerer!=null&&requester==null&&offer!=null&&offer.startsWith(CALabel.offer))
					label = new CALabel(source.getRank(),offerer, offer);
				else if (offerer==null&&requester!=null&&request!=null&&request.startsWith(CALabel.request))
					label = new CALabel(source.getRank(),requester,request);
				else
					throw new RuntimeException("Bug in loading label");

				return new MSCATransition(source,label,target,type); //TODO check & remove arrlabel
				//				return new MSCATransition(source,List.of(arrlabel),target,type);
			}
			else
			{
				if (what==0)
					store[what]=statestransition; //the source state
			}
			what++;
		}
		throw new RuntimeException(); //check
		//return null;
	}


	/**
	 * parse the XML description of graphEditor into an MSCA object
	 * 
	 * TODO the set of final states of principals is reconstructed at the end, this encoding XML lose the information of 
	 * some of the final states of principals so should be removed
	 * 
	 * @param filename
	 * @return
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

		Map<Integer, CAState> id2castate = new HashMap<>(nodeList.getLength());
		Set<MSCATransition> transitions= new HashSet<MSCATransition>();

		//first read all the states, then all the edges
		for (int i = 0; i < nodeList.getLength(); i++) 
		{
			Node nNode = nodeList.item(i);
			if ((nNode.getNodeType() == Node.ELEMENT_NODE))
			{
				Element eElement = (Element) nNode;
				if (Integer.parseInt(eElement.getAttribute("id"))>1 && !eElement.hasAttribute("edge"))
				{
					Element geom= (Element) (NodeList)eElement.getElementsByTagName("mxGeometry").item(0);
					int[] st=getArray(eElement.getAttribute("value"));
					CAState castate=new CAState(st,
							geom.hasAttribute("x")?Float.parseFloat(geom.getAttribute("x")):0,
									geom.hasAttribute("y")?Float.parseFloat(geom.getAttribute("y")):0,//useful when not morphing (e.g. adding handles to edges)
											Arrays.equals(st, new int[st.length]), //initial
											eElement.getAttribute("style").contains("terminate.png"));//final
					if (id2castate.put(Integer.parseInt(eElement.getAttribute("id")), castate)!=null)
						throw new IllegalArgumentException("Duplicate states!");
				}
			}
		}

		// A set of checks for detecting bugs

		assert !(id2castate.isEmpty()):"No states!";

		Set<CAState> castates = id2castate.entrySet().stream()
				.map(Entry::getValue)
				.collect(Collectors.toSet());

		//transitions
		for (int i = 0; i < nodeList.getLength(); i++) 
		{
			Node nNode = nodeList.item(i);
			if ((nNode.getNodeType() == Node.ELEMENT_NODE))
			{
				Element eElement = (Element) nNode;
				if (Integer.parseInt(eElement.getAttribute("id"))>1 && eElement.hasAttribute("edge"))
					transitions.add(new MSCATransition(id2castate.get(Integer.parseInt(eElement.getAttribute("source"))),
							new CALabel(Arrays.asList(getArrayString(eElement.getAttribute("value")))),//label
							id2castate.get(Integer.parseInt(eElement.getAttribute("target"))), 
							(eElement.getAttribute("style").contains("strokeColor=#FF0000"))? MSCATransition.Modality.URGENT: //red
								(eElement.getAttribute("style").contains("strokeColor=#00FF00"))? MSCATransition.Modality.LAZY: //green
									MSCATransition.Modality.PERMITTED));
			}
		}


		int rank=castates.iterator().next().getState().length;
		MSCA aut= new MSCA(rank, 
				CAState.getCAStateWithValue(new int[rank], castates),
				principalsFinalStates(castates.stream()
						.filter(CAState::isFinalstate)
						.map(CAState::getState)
						.collect(Collectors.toList())),
				transitions,
				castates);
		return aut;
	}


	/**
	 * convert the MSCA aut as a mxGraphModel File
	 * @param fileName	write the automaton into fileName
	 * @return
	 */
	public static File convertMSCAintoXML(String fileName, MSCA aut)
	{
		try{
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

			Map<CAState,Element> state2element = new HashMap<CAState, Element>();

			int id=2;
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
			/*// Output to console for testing
			StreamResult consoleResult =
					new StreamResult(System.out);
			transformer.transform(source, consoleResult);*/
			return file;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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


		if (((Element)source.getChildNodes().item(0)).hasAttribute("x"))
		{
			mxPointSource.setAttribute("x", ((Element)source.getChildNodes().item(0)).getAttribute("x"));
		}
		else
			mxPointSource.setAttribute("x", "0.0");

		if (((Element)source.getChildNodes().item(0)).hasAttribute("y"))
		{
			mxPointSource.setAttribute("y", ((Element)source.getChildNodes().item(0)).getAttribute("y"));
		}
		else
			mxPointSource.setAttribute("y", "0.0");

		mxGeometry1.appendChild(mxPointSource);
		Element mxPointTarget=doc.createElement("mxPoint");
		mxPointTarget.setAttribute("as","targetPoint");

		if (((Element)target.getChildNodes().item(0)).hasAttribute("x"))
			mxPointTarget.setAttribute("x", ((Element)target.getChildNodes().item(0)).getAttribute("x"));
		else
		{
			Attr x=doc.createAttribute("x");
			x.setNodeValue("0.0");
			mxPointTarget.setAttributeNode(x);
		}

		if (((Element)target.getChildNodes().item(0)).hasAttribute("y"))
			mxPointTarget.setAttribute("y", ((Element)target.getChildNodes().item(0)).getAttribute("y"));
		else
		{
			Attr y=doc.createAttribute("y");
			y.setNodeValue("0.0");
			mxPointTarget.setAttributeNode(y);
		}
		mxGeometry1.appendChild(mxPointTarget);

		Element pointArray=doc.createElement("Array");
		pointArray.setAttribute("as","points");
		Element mxPoint=doc.createElement("mxPoint");


		float xs=0; float  xt=0;
		float ys=0; float  yt=0;

		if (((Element)source.getChildNodes().item(0)).hasAttribute("x"))
		{
			xs=Float.parseFloat(((Element)source.getChildNodes().item(0)).getAttribute("x"));
		}
		if (((Element)target.getChildNodes().item(0)).hasAttribute("x"))
		{
			xt=Float.parseFloat(((Element)target.getChildNodes().item(0)).getAttribute("x"));
		}

		if (((Element)source.getChildNodes().item(0)).hasAttribute("y"))
		{
			ys=Float.parseFloat(((Element)source.getChildNodes().item(0)).getAttribute("y"));	
		}

		if (((Element)target.getChildNodes().item(0)).hasAttribute("y"))
		{
			yt=Float.parseFloat(((Element)target.getChildNodes().item(0)).getAttribute("y"));
		}


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

	/**
	 * this method retrieves the coordinates from CAState castate
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
		value.setValue(Arrays.toString(castate.getState()));
		Element mxcell1 = doc.createElement("mxCell");
		Attr id1=doc.createAttribute("id");
		Attr as=doc.createAttribute("as");
		Attr vertex=doc.createAttribute("vertex");
		vertex.setNodeValue("1");		
		as.setValue("geometry");
		id1.setValue(id);
		mxcell1.setAttributeNode(id1);
		mxcell1.setAttributeNode(parent);
		mxcell1.setAttributeNode(style);
		mxcell1.setAttributeNode(value);
		mxcell1.setAttributeNode(vertex);
		Element mxGeometry1=doc.createElement("mxGeometry");
		mxGeometry1.setAttributeNode(as);
		mxGeometry1.setAttribute("height", "50.0");
		mxGeometry1.setAttribute("width", "50.0");

		if (!mxGeometry1.hasAttribute("x"))
		{
			Attr x=doc.createAttribute("x");
			x.setNodeValue(castate.getX()+"");
			mxGeometry1.setAttributeNode(x);
		}
		else
			mxGeometry1.setAttribute("x", castate.getX()+"");

		if (!mxGeometry1.hasAttribute("y"))
		{
			Attr y=doc.createAttribute("y");
			y.setNodeValue(castate.getY()+"");
			mxGeometry1.setAttributeNode(y);
		}
		else
			mxGeometry1.setAttribute("y", castate.getY()+"");

		mxcell1.appendChild(mxGeometry1);
		root.appendChild(mxcell1);
		return mxcell1;		
	}

	/**
	 * this method is used when importing from XML, where no description of  principal final states is given 
	 * and it is reconstructed
	 * 
	 * //TODO remove this in the future, when importing from XML there is loss of information anyway
	 * 
	 * @param all final states of the composed automaton
	 * @return the final states of each principal
	 */
	private static int[][] principalsFinalStates(List<int[]> states)
	{
		if (states.size()<=0)
			return null;
		int rank=states.get(0).length;
		int[] count=new int[rank];
		int[][] pfs=new int[rank][states.size()];

		for (int ind=0; ind<pfs.length;ind++)
			for (int ind2=0; ind2<pfs[ind].length;ind2++)
				pfs[ind][ind2] = -1;    //the check MSCAUtil.getIndex(pfs[j], states[i][j])==-1  will not work otherwise because 0 is the initialization value but can also be a state

		for (int j=0;j<rank;j++)
		{
			pfs[j][0]=states.get(0)[j];
			count[j]=1;		//initialising count[j] and doing first iteration (I guess..)
		}
		for (int i=1;i<states.size();i++)
		{
			for (int j=0;j<rank;j++)
			{
				if (MSCAUtils.getIndex(pfs[j], states.get(i)[j])==-1 )  // if states[i][j] is not in pfs[j]
				{
					pfs[j][count[j]]=states.get(i)[j];
					count[j]++;
				}
			}
		}
		for (int j=0;j<rank;j++)
			pfs[j]=MSCAUtils.removeTailsNull(pfs[j], count[j]);
		return pfs;
	}


	private static String[] getArrayString(String arr)
	{
		String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
		if (Arrays.stream(items).anyMatch(item->!(item.startsWith(CALabel.offer)||item.startsWith(CALabel.request)||item.startsWith(CALabel.idle))))
			return null;
		return items;
	}

	private static int[] getArray(String arr)
	{
		String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
		return Arrays.stream(items)
				.mapToInt(Integer::parseInt)
				.toArray();
	}
}
