package FMCA;

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
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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

import CA.CAState;
import CA.CATransition;

/**
 * Input/Output 
 * 
 * @author Davide
 *
 */
public class FMCAIO {

	/**
	 * print the description of the CA to a file
	 */
	public static void printToFile(String filename, FMCA aut)
	{
		String name=null;
		int rank= aut.getRank();
		int[][] finalstates = aut.getFinalStatesofPrincipals();
		InputStreamReader reader = new InputStreamReader(System.in);
		BufferedReader myInput = new BufferedReader(reader);
		try {
			if (filename=="")
			{
				System.out.println("Do you want to save this automaton? (write yes or no, default yes)");
				if (!myInput.readLine().equals("no"))
				{	
					System.out.println("Write the name of this automaton");
					name= myInput.readLine();
				}
				else return;
			}
			else
				name=filename;

			PrintWriter pr = new PrintWriter(name+".data"); 
			pr.println("Rank: "+rank);
			pr.println("Number of states: "+Arrays.toString(aut.getNumStatesPrinc()));
			pr.println("Initial state: " +Arrays.toString(aut.getInitialCA().getState()));
			pr.print("Final states: [");
			for (int i=0;i<finalstates.length;i++)
				pr.print(Arrays.toString(finalstates[i]));
			pr.print("]\n");
			pr.println("Transitions: \n");
			Set<FMCATransition> tr = aut.getTransition();
			if (tr!=null)
			{
				for (FMCATransition t : tr)
					pr.println(t.toString());
			}
			pr.close();
		}catch(Exception e){e.printStackTrace();}
	}


	public static File loadFMCAAndWriteIntoXML(String fileName)
	{
		return convertFMCAintoXML(fileName, load(fileName));
	}

	/**
	 * load a FMCA described in a text file,  
	 * it also loads the must transitions but it does not load the states
	 * this method is only used by loadFMCAAndWriteIntoXML
	 * 
	 * @param the name of the file
	 * @return	the CA loaded
	 */
	private static FMCA load(String fileName)
	{
		try
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
			Set<FMCATransition> tr = new HashSet<FMCATransition>();
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

							String[] arr2=arr[ind].split("[,|\\[]"); //TODO BUG with this parseInt fails with spaces
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
								fin[outerindex]=FMCAUtil.removeTailsNull(tf, innerindex); 
								outerindex++;
							}catch(NumberFormatException e){}
						}
						break;
					}
					case "(": //a may transition
					{
						tr.add(loadTransition(strLine,rank, FMCATransition.action.PERMITTED, states));
						break;
					}
					case "!": //a must transition
					{
						String stype= strLine.substring(1,2);
						FMCATransition.action type=null;
						switch (stype)
						{
						case "U": type=FMCATransition.action.URGENT;break;
						case "G": type=FMCATransition.action.GREEDY;break;
						case "L": type=FMCATransition.action.LAZY;break;
						}
						tr.add(loadTransition(strLine,rank,type,states));
						break;
					}
					}
				}
			}
			br.close();

			return new FMCA(rank,new CAState(initial, true, false),//numstates,
					fin,tr,states);
		} catch (FileNotFoundException e) {System.out.println("File not found"); return null;}
		catch (Exception e) {e.printStackTrace();}
		return null;
	}

	private static FMCATransition loadTransition(String str, int rank, FMCATransition.action type, Set<CAState> states)
	{
		int what=0;
		String[] ss=str.split("]");
		int[][] store=new int[1][];
		String[] label = new String[rank];
		for (int i=0;i<ss.length;i++)
			//TODO check
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
						if (action.contains(CATransition.idle))
							label[j]=CATransition.idle;
						else if (action.contains(CATransition.offer))
							label[j]=action.substring(action.indexOf(CATransition.offer));
						else if (action.contains(CATransition.request))
							label[j]=action.substring(action.indexOf(CATransition.request));
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
				
				return new FMCATransition(source,label,target,type);
			}
			else
			{
				if (what==0)
					store[what]=statestransition; //the source state
			}
			what++;
		}
		return null;
	}


	/**
	 * parse the XML description of graphEditor into an FMCA object
	 * 
	 * TODO the set of final states of principals is reconstructed at the end, this encoding XML lose the information of 
	 * some of the final states of principals so should be removed
	 * 
	 * @param filename
	 * @return
	 */
	public static FMCA parseXMLintoFMCA(String filename)
	{
		try {
			File inputFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nodeList = (NodeList) doc.getElementsByTagName("mxCell");
			Set<CAState> castates= new HashSet<CAState>();
			int[][] states=new int[nodeList.getLength()][];
			int[][] finalstates=new int[nodeList.getLength()][];
			int[] idstate=new int[nodeList.getLength()];
			//each node has associated an id, used for identifying source and target of an edge
			float[] xstate=new float[idstate.length];
			float[] ystate=new float[idstate.length];
			int[] idfinalstate=new int[nodeList.getLength()];
			float[] xfinalstate=new float[idstate.length];
			float[] yfinalstate=new float[idstate.length];
			Set<FMCATransition> t= new HashSet<FMCATransition>();
			int statec=0;
			int finalstatec=0;
			/**
			 * first read all the states, then all the edges
			 */
			for (int i = 0; i < nodeList.getLength(); i++) 
			{
				Node nNode = nodeList.item(i);
				//  System.out.println("\nCurrent Element :" 
				//     + nNode.getNodeName());
				if ((nNode.getNodeType() == Node.ELEMENT_NODE))//&&(nNode.getNodeName()=="mxCell")) {
				{
					Element eElement = (Element) nNode;
					String prova=eElement.getAttribute("id");
					if (Integer.parseInt(prova)>1)
					{
						if (!eElement.hasAttribute("edge"))//edge

							//TODO remove xstates ystates etc..
						{
							if (eElement.getAttribute("style").contains("terminate.png"))
							{ 
								idfinalstate[finalstatec]=Integer.parseInt(eElement.getAttribute("id"));
								finalstates[finalstatec]=getArray(eElement.getAttribute("value"));
								NodeList g= (NodeList) eElement.getElementsByTagName("mxGeometry");
								Element geo= (Element) g.item(0);
								if (geo.hasAttribute("x"))
									xfinalstate[finalstatec]=Float.parseFloat(geo.getAttribute("x"));
								else
									xfinalstate[finalstatec]=0;
								if (geo.hasAttribute("y"))
									yfinalstate[finalstatec]=Float.parseFloat(geo.getAttribute("y"));
								else
									yfinalstate[finalstatec]=0;
								boolean initial=true;
								for (int ind=0;ind<finalstates[finalstatec].length;ind++)
									initial=initial&&(finalstates[finalstatec][ind]==0);
								castates.add(new CAState(finalstates[finalstatec], xfinalstate[finalstatec], 
										yfinalstate[finalstatec], initial,true));
								finalstatec++;
							}
							else{
								idstate[statec]=Integer.parseInt(eElement.getAttribute("id"));
								states[statec]=getArray(eElement.getAttribute("value"));
								NodeList g= (NodeList) eElement.getElementsByTagName("mxGeometry");
								Element geo= (Element) g.item(0);
								if (geo.hasAttribute("x"))
									xstate[statec]=Float.parseFloat(geo.getAttribute("x"));
								else
									xstate[statec]=0;
								if (geo.hasAttribute("y"))
									ystate[statec]=Float.parseFloat(geo.getAttribute("y"));
								else
									ystate[statec]=0;
								boolean initial=true;
								for (int ind=0;ind<states[statec].length;ind++)
									initial=initial&&(states[statec][ind]==0);
								castates.add(new CAState(states[statec], xstate[statec], 
										ystate[statec], initial,false));
								statec++;
							}
						}
					}
				}
			}
			for (int i = 0; i < nodeList.getLength(); i++) 
			{
				Node nNode = nodeList.item(i);
				//  System.out.println("\nCurrent Element :" 
				//     + nNode.getNodeName());
				if ((nNode.getNodeType() == Node.ELEMENT_NODE))//&&(nNode.getNodeName()=="mxCell")) {
				{
					Element eElement = (Element) nNode;
					String prova=eElement.getAttribute("id");
					if (Integer.parseInt(prova)>1)
					{
						if (eElement.hasAttribute("edge"))//edge
						{
							int idsource=Integer.parseInt(eElement.getAttribute("source"));
							int index=FMCAUtil.getIndex(idstate, idsource);
							int[] source;
							if (index>-1)
								source=states[index];
							else
								source=finalstates[FMCAUtil.getIndex(idfinalstate, idsource)];

							int idtarget=Integer.parseInt(eElement.getAttribute("target"));
							index=FMCAUtil.getIndex(idstate, idtarget);
							int[] target;
							if (index>-1)
								target=states[index];
							else
								target=finalstates[FMCAUtil.getIndex(idfinalstate, idtarget)];


							String[] label=getArrayString(eElement.getAttribute("value"));
							if (label==null)
								return null;
							if (eElement.getAttribute("style").contains("strokeColor=#FF0000"))
								t.add(new FMCATransition(CAState.getCAStateWithValue(source, castates),label,CAState.getCAStateWithValue(target, castates),FMCATransition.action.URGENT));//red
							else if (eElement.getAttribute("style").contains("strokeColor=#FFA500"))
								t.add(new FMCATransition(CAState.getCAStateWithValue(source, castates),label,CAState.getCAStateWithValue(target, castates),FMCATransition.action.GREEDY)); //orange
							else if (eElement.getAttribute("style").contains("strokeColor=#00FF00"))
								t.add(new FMCATransition(CAState.getCAStateWithValue(source, castates),label,CAState.getCAStateWithValue(target, castates),FMCATransition.action.LAZY)); //green
							else 
								t.add(new FMCATransition(CAState.getCAStateWithValue(source, castates),label,CAState.getCAStateWithValue(target, castates),FMCATransition.action.PERMITTED)); //otherwise
							
						}
					}
				}
			}

			finalstates=FMCAUtil.removeTailsNull(finalstates, finalstatec, new int[][] {});
			xfinalstate=FMCAUtil.removeTailsNull(xstate, finalstatec);
			yfinalstate=FMCAUtil.removeTailsNull(ystate, finalstatec);
			states=FMCAUtil.removeTailsNull(states, statec, new int[][] {});
			xstate=FMCAUtil.removeTailsNull(xstate, statec);
			ystate=FMCAUtil.removeTailsNull(ystate, statec);
			int rank=states[0].length;
			int[] initial = new int[rank];
			for (int ind=0;ind<rank;ind++)
				initial[ind]=0;
			
			FMCA aut= new FMCA(rank, 
					CAState.getCAStateWithValue(initial, castates),
					FMCA.principalsFinalStates(finalstates),
					t,
					castates);
			return aut;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

		return null;
	}

	/**
	 * convert the FMCA aut as a mxGraphModel File
	 * @param fileName	write the automaton into fileName
	 * @return
	 */
	public static File convertFMCAintoXML(String fileName, FMCA aut)
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
			
			Set<FMCATransition> tr= aut.getTransition();
			for (FMCATransition t : tr)
			{
				createElementEdge(doc,root,Integer.toString(id),
						state2element.get(t.getSource()),
						state2element.get(t.getTarget()),
						Arrays.toString(t.getLabel()),t.getType());
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

	private static Element createElementEdge(Document doc, Element root,String id, Element source, Element target,String label,FMCATransition.action type)
	{
		Attr parent=doc.createAttribute("parent");
		parent.setValue("1");
		Attr style=doc.createAttribute("style");

		if (type==FMCATransition.action.URGENT)
			style.setValue("straight;strokeColor=#FF0000");
		else if (type==FMCATransition.action.GREEDY)
			style.setValue("straight;strokeColor=#FFA500");
		else if (type==FMCATransition.action.LAZY)
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
		//		for (int i=0;i<castates.length;i++)
		//		{
		//			if (Arrays.equals(arrintstate, castates[i].getState()))
		//			{
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
		//			}
		//		}
		mxcell1.appendChild(mxGeometry1);
		root.appendChild(mxcell1);
		return mxcell1;		
	}

	//	private static Element createElementFinalState(Document doc, Element root,String id, CAState[] castates, int[] arrintstate)
	//	{
	//		Attr parent=doc.createAttribute("parent");
	//		parent.setValue("1");
	//		Element mxcell1 = doc.createElement("mxCell");
	//		Attr id1=doc.createAttribute("id");
	//		Attr as=doc.createAttribute("as");
	//		Attr vertex=doc.createAttribute("vertex");
	//		vertex.setNodeValue("1");		
	//		as.setValue("geometry");
	//		id1.setValue(id);
	//		Attr stylefinal=doc.createAttribute("style");
	//		stylefinal.setValue("roundImage;image=/com/mxgraph/examples/swing/images/terminate.png");
	//		Attr valuefinal=doc.createAttribute("value");
	//		valuefinal.setValue(Arrays.toString(arrintstate));
	//		mxcell1.setAttributeNode(id1);
	//		mxcell1.setAttributeNode(parent);
	//		mxcell1.setAttributeNode(stylefinal);
	//		mxcell1.setAttributeNode(valuefinal);
	//		mxcell1.setAttributeNode(vertex);
	//		Element mxGeometry1=doc.createElement("mxGeometry");
	//		mxGeometry1.setAttributeNode(as);
	//		mxGeometry1.setAttribute("height", "50.0");
	//		mxGeometry1.setAttribute("width", "50.0");
	//		for (int i=0;i<castates.length;i++)
	//		{
	//			if (Arrays.equals(arrintstate, castates[i].getState()))
	//			{
	//				if (!mxGeometry1.hasAttribute("x"))
	//				{
	//					Attr x=doc.createAttribute("x");
	//					x.setNodeValue(castates[i].getX()+"");
	//					mxGeometry1.setAttributeNode(x);
	//				}
	//				else
	//					mxGeometry1.setAttribute("x", castates[i].getX()+"");
	//
	//				if (!mxGeometry1.hasAttribute("y"))
	//				{
	//					Attr y=doc.createAttribute("y");
	//					y.setNodeValue(castates[i].getY()+"");
	//					mxGeometry1.setAttributeNode(y);
	//				}
	//				else
	//					mxGeometry1.setAttribute("y", castates[i].getY()+"");
	//			}
	//		}
	//		mxcell1.appendChild(mxGeometry1);
	//		root.appendChild(mxcell1);
	//		return mxcell1;		
	//	}



	private static String[] getArrayString(String arr)
	{
		String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
		for (int i=0;i<items.length;i++)
		{
			if (!(items[i].startsWith("!")||items[i].startsWith("?")||items[i].startsWith("-")))
				return null;
		}
		return items;
	}

	private static int[] getArray(String arr)
	{
		String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

		int[] results = new int[items.length];

		for (int ii = 0; ii < items.length; ii++) {
			// try {
			results[ii] = Integer.parseInt(items[ii]);
			/*} catch (NumberFormatException nfe) {
		         nfe.printStackTrace();
		     };*/
		}
		return results;
	}




}
