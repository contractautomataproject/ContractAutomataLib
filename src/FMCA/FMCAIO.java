package FMCA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

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

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

import CA.CA;
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
	public static void printToFile(String filename, CA aut)
	{
		String name=null;
		int rank= aut.getRank();
		int[][] finalstates = aut.getFinalStatesCA();
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
			 pr.println("Number of states: "+Arrays.toString(aut.getStatesCA()));
			 pr.println("Initial state: " +Arrays.toString(aut.getInitialCA().getState()));
			 pr.print("Final states: [");
			 for (int i=0;i<finalstates.length;i++)
				 pr.print(Arrays.toString(finalstates[i]));
			 pr.print("]\n");
			 pr.println("Transitions: \n");
			 CATransition[] t = aut.getTransition();
			 if (t!=null)
			 {
				 for (int i=0;i<t.length;i++)
					 pr.println(t[i].toString());
			 }
			 pr.close();
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * load a FMCA described in a text file, compared to CA it also loads the must transitions
	 * 
	 * this method is now only used  in the import phase
	 * 
	 * @param the name of the file
	 * @return	the CA loaded
	 */
	public static FMCA load(String fileName)
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
			int[] states = new int[1];
			int[][] fin = new int[1][]; //rank initialization later
			FMCATransition[] t = new FMCATransition[1];
			//	MSCATransition[] mustt = new MSCATransition[1];
			int pointert=0;
			//		int pointermust=0;
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   
			{
				// Print the content on the console
				if (strLine.length()>0)
				{
					switch(strLine.substring(0,1))
					{
					case "R":  //Rank Line
					{
						//							  Scanner s = new Scanner(strLine);
						//							  s.useDelimiter("");
						//							  while (s.hasNext())
						//							  {
						//								  if (s.hasNextInt())
						//								  {
						//									  rank = s.nextInt();
						//								  }
						//								  else
						//								  {
						//									  s.next();
						//								  }
						//							  }
						String srank = strLine.substring(6);
						rank = Integer.parseInt(srank);
						initial = new int[rank];
						states = new int[rank];
						fin = new int[rank][];
						//						  s.close();
						break;
					}
					case "N":	//Number of states line
					{
						String[] arr=strLine.split("[\\[\\],]");
						int i=0;
						int lengthT=1;

						for (int ind=0;ind<arr.length;ind++)
						{
							try{
								int num=Integer.parseInt(arr[ind].trim());
								states[i] = num;
								lengthT*=states[i];
								i++;
							}catch(NumberFormatException e){}
						}

						/*
							  Scanner s = new Scanner(strLine);
							  s.useDelimiter("");
							  while (s.hasNext())
							  {
								  if (s.hasNextInt())
								  {
									  int test=s.nextInt();
									  states[i] = test;
									  lengthT*=states[i];
									  i++;
								  }
								  else
									  s.next();
							  }
							  s.close();
						 */
						try{
							int length = lengthT*lengthT*4;
							t = new FMCATransition[length];//TODO guessed upper bound WARNING
						} catch (Exception e) { t = new FMCATransition[1000];}
						break;
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

						//String[] ss=strLine.split("]");
						//for (int ind=0;ind<ss.length;ind++)
						for (int ind=0;ind<arr.length;ind++)
						{
							//								  Scanner s = new Scanner(ss[ind]);
							//								  s.useDelimiter("");

							String[] arr2=arr[ind].split("[,|\\[]"); //TODO BUG with this parseInt failes with spaces
							try{
								//Integer.parseInt(arr2[0]); //check if we are reading states!
								int innerindex=0;
								int[] tf = new int[states[outerindex]]; //upper bound
								for(int ind2=0;ind2<arr2.length;ind2++)
								{
									try{
										int num=Integer.parseInt(arr2[ind2].trim());
										tf[innerindex] = num;
										innerindex++;
									}catch(NumberFormatException e){}  //exceptions should never be thrown this way
								}
								fin[outerindex]=FMCAUtil.removeTailsNull(tf, innerindex); 
								outerindex++;
							}catch(NumberFormatException e){}
							//								  while (s.hasNext())
							//								  {
							//									  if (s.hasNextInt())
							//									  {
							//										  int test=s.nextInt();
							//										  tf[i] = test;
							//										  i++;
							//									  }
							//									  else
							//										  s.next();
							//								  }
							//								  fin[ind]= new int[i];
							//								  for (int ii=0;ii<i;ii++)
							//									  fin[ind][ii]=tf[ii];
							//								  s.close();
						}
						break;
					}
					case "(": //a may transition
					{
						CATransition temp=CA.loadTransition(strLine,rank);
						t[pointert]=new FMCATransition(temp.getSourceP(),temp.getLabelP(),temp.getTargetP(),FMCATransition.action.PERMITTED);
						pointert++;
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
						CATransition temp=CA.loadTransition(strLine,rank);
						t[pointert]=new FMCATransition(temp.getSourceP(),temp.getLabelP(),temp.getTargetP(),type);
						pointert++;				 
						break;
					}
					}
				}
			}
			br.close();	
			FMCATransition[] fintr = new FMCATransition[pointert]; //the length of the array is exactly the number of transitions
			for (int i=0;i<pointert;i++)
			{
				fintr[i]=t[i];
			}

			return new FMCA(rank,new CAState(initial,CAState.type.INITIAL),states,fin,fintr);
		} catch (FileNotFoundException e) {System.out.println("File not found"); return null;}
		catch (Exception e) {e.printStackTrace();}
		return null;
	}

	/**
	 * parse the XML description of graphEditor into an FMCA object
	 * @param filename
	 * @return
	 */
	public static FMCA importFromXML(String filename)
	{
		try {
			File inputFile = new File(filename);
			DocumentBuilderFactory dbFactory 
			= DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;

			dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			//XPath xPath =  XPathFactory.newInstance().newXPath();

			//NodeList nodeList = (NodeList) xPath.compile("").evaluate(doc, XPathConstants.NODESET);
			NodeList nodeList = (NodeList) doc.getElementsByTagName("mxCell");
			CAState[] fstates= new CAState[nodeList.getLength()];
			int[][] states=new int[nodeList.getLength()][];
			int[][] finalstates=new int[nodeList.getLength()][];
			int[] idstate=new int[nodeList.getLength()]; //each node has associated an id, used for identifying source and target of an edge
			float[] xstate=new float[idstate.length];
			float[] ystate=new float[idstate.length];
			int[] idfinalstate=new int[nodeList.getLength()];
			float[] xfinalstate=new float[idstate.length];
			float[] yfinalstate=new float[idstate.length];
			FMCATransition[] t= new FMCATransition[nodeList.getLength()];
			int statec=0;
			int finalstatec=0;
			int trc=0;
			int fstatescount=0;
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
								fstates[fstatescount]=new CAState(finalstates[finalstatec], xfinalstate[finalstatec], 
										yfinalstate[finalstatec], initial,true);
								finalstatec++;
								fstatescount++;
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
								fstates[fstatescount]=new CAState(states[statec], xstate[statec], 
										ystate[statec], initial,false);
								statec++;
								fstatescount++;
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
							if (eElement.getAttribute("style").contains("strokeColor=#FF0000"))
								t[trc]=new FMCATransition(CAState.getCAStateWithValue(source, fstates),label,CAState.getCAStateWithValue(target, fstates),FMCATransition.action.URGENT);//red
							else if (eElement.getAttribute("style").contains("strokeColor=#FFA500"))
								t[trc]=new FMCATransition(CAState.getCAStateWithValue(source, fstates),label,CAState.getCAStateWithValue(target, fstates),FMCATransition.action.GREEDY); //orange
							else if (eElement.getAttribute("style").contains("strokeColor=#00FF00"))
								t[trc]=new FMCATransition(CAState.getCAStateWithValue(source, fstates),label,CAState.getCAStateWithValue(target, fstates),FMCATransition.action.LAZY); //green
							else 
								t[trc]=new FMCATransition(CAState.getCAStateWithValue(source, fstates),label,CAState.getCAStateWithValue(target, fstates),FMCATransition.action.PERMITTED); //otherwise
							trc++;
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
			fstates=FMCAUtil.removeTailsNull(fstates, fstatescount, new CAState[] {});
			t=FMCAUtil.removeTailsNull(t, trc, new FMCATransition[] {});
			int rank=states[0].length;
			int[] initial = new int[rank];
			for (int ind=0;ind<rank;ind++)
				initial[ind]=0;
			FMCA aut= new FMCA(rank, CAState.getCAStateWithValue(initial, fstates),states,finalstates,t,fstates);
			return aut;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return null;
	}

	/**
	 * write the FMCA aut as a mxGraphModel for the GUI
	 * @param fileName	write the automaton into fileName
	 * @return
	 */
	public static File exportToXML(String fileName, FMCA aut)
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
			//TODO this could be improved: it generates all combinations of states, 
			// but in case CAState[] fstates !=null we already have all fstates instantiated
			// there is no need to generate them again
			int[][][] all=aut.allNonFinalAndFinalStates();
			int[][] states=all[0];
			CAState[] fstates=aut.getState();
			Element[] statese=new Element[states.length];

			//TODO: smart graph display -- I think I did it
			for (int i=0;i<states.length;i++)
			{
				if (fstates!=null)
					statese[i]=createElementState(doc, root,Integer.toString(i+2), fstates,states[i]);
				else
				{	
					CAState[] dum= new CAState[1];
					dum[0]= new CAState(states[i],i*200,60); 
					//statese[i]=createElementState(doc, root,Integer.toString(i+2), Integer.toString(i*200),"60",states[i]);
					statese[i]=createElementState(doc, root,Integer.toString(i+2), dum,states[i]);
				}
			}
			int[][] statesf=all[1];
			Element[] statesef=new Element[statesf.length];
			for (int i=0;i<statesf.length;i++)
			{
				if (fstates!=null)
					statesef[i]=createElementFinalState(doc, root,Integer.toString(i+2+states.length), fstates,statesf[i]);
				else
				{	
					CAState[] dum= new CAState[1];
					dum[0]= new CAState(statesf[i],i*200,200); 
					//statesef[i]=createElementFinalState(doc, root,Integer.toString(i+2+states.length), Integer.toString(i*200),"200",statesf[i]);
					statesef[i]=createElementFinalState(doc, root,Integer.toString(i+2+states.length), dum,statesf[i]);
				}

			}
			FMCATransition t[]= aut.getTransition();
			for (int i=0;i<t.length;i++)
			{
				Element s; Element ta;
				int source;
				source=FMCAUtil.indexContains(t[i].getSourceP().getState(), states);
				if (source==-1)
				{
					source=FMCAUtil.indexContains(t[i].getSourceP().getState(), statesf);
					s=statesef[source];
				}
				else
					s=statese[source];
				int target;
				target=FMCAUtil.indexContains(t[i].getTargetP().getState(), states);
				if (target==-1)
				{
					target=FMCAUtil.indexContains(t[i].getTargetP().getState(), statesf);
					ta=statesef[target];
				}
				else
					ta=statese[target];
				createElementEdge(doc,root,Integer.toString(i+2+states.length+statesf.length),s,ta,Arrays.toString(t[i].getLabelP()),t[i].getType());
			}
			//edges
			/* createElementEdge(doc,root,"4",state1,state2,"a!");
			 createElementEdge(doc,root,"5",state2,state2,"a?");
			 */
			// write the content into xml file

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
			return file;//fileName+".mxe";
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
	 * this method retrieves the coordinates from CAState[] states
	 * @param doc
	 * @param root
	 * @param id
	 * @param states
	 * @param state
	 * @return
	 */
	private static Element createElementState(Document doc, Element root,String id, CAState[] states,int[] state)
	{
		Attr parent=doc.createAttribute("parent");
		parent.setValue("1");
		Attr style=doc.createAttribute("style");
		style.setValue("roundImage;image=/com/mxgraph/examples/swing/images/event.png");
		Attr value=doc.createAttribute("value");
		value.setValue(Arrays.toString(state));
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
		for (int i=0;i<states.length;i++)
		{
			if (Arrays.equals(state, states[i].getState()))
			{
				if (!mxGeometry1.hasAttribute("x"))
				{
					Attr x=doc.createAttribute("x");
					x.setNodeValue(states[i].getX()+"");
					mxGeometry1.setAttributeNode(x);
				}
				else
					mxGeometry1.setAttribute("x", states[i].getX()+"");

				if (!mxGeometry1.hasAttribute("y"))
				{
					Attr y=doc.createAttribute("y");
					y.setNodeValue(states[i].getY()+"");
					mxGeometry1.setAttributeNode(y);
				}
				else
					mxGeometry1.setAttribute("y", states[i].getY()+"");
			}
		}
		mxcell1.appendChild(mxGeometry1);
		root.appendChild(mxcell1);
		return mxcell1;		
	}

	private static Element createElementFinalState(Document doc, Element root,String id, CAState[] states,int[] state)
	{
		Attr parent=doc.createAttribute("parent");
		parent.setValue("1");
		Element mxcell1 = doc.createElement("mxCell");
		Attr id1=doc.createAttribute("id");
		Attr as=doc.createAttribute("as");
		Attr vertex=doc.createAttribute("vertex");
		vertex.setNodeValue("1");		
		as.setValue("geometry");
		id1.setValue(id);
		Attr stylefinal=doc.createAttribute("style");
		stylefinal.setValue("roundImage;image=/com/mxgraph/examples/swing/images/terminate.png");
		Attr valuefinal=doc.createAttribute("value");
		valuefinal.setValue(Arrays.toString(state));
		mxcell1.setAttributeNode(id1);
		mxcell1.setAttributeNode(parent);
		mxcell1.setAttributeNode(stylefinal);
		mxcell1.setAttributeNode(valuefinal);
		mxcell1.setAttributeNode(vertex);
		Element mxGeometry1=doc.createElement("mxGeometry");
		mxGeometry1.setAttributeNode(as);
		mxGeometry1.setAttribute("height", "50.0");
		mxGeometry1.setAttribute("width", "50.0");
		for (int i=0;i<states.length;i++)
		{
			if (Arrays.equals(state, states[i].getState()))
			{
				if (!mxGeometry1.hasAttribute("x"))
				{
					Attr x=doc.createAttribute("x");
					x.setNodeValue(states[i].getX()+"");
					mxGeometry1.setAttributeNode(x);
				}
				else
					mxGeometry1.setAttribute("x", states[i].getX()+"");

				if (!mxGeometry1.hasAttribute("y"))
				{
					Attr y=doc.createAttribute("y");
					y.setNodeValue(states[i].getY()+"");
					mxGeometry1.setAttributeNode(y);
				}
				else
					mxGeometry1.setAttribute("y", states[i].getY()+"");
			}
		}
		mxcell1.appendChild(mxGeometry1);
		root.appendChild(mxcell1);
		return mxcell1;		
	}

	/**
	 * utility for arranging the graphical display of the automaton
	 * @param graph
	 * @param graphComponent
	 */
	public static void morphGraph(final mxGraph graph,
			mxGraphComponent graphComponent) 
	{
		// define layout
		mxIGraphLayout layout = new mxFastOrganicLayout(graph);

		((mxFastOrganicLayout) layout).setForceConstant(100);
		((mxFastOrganicLayout) layout).setDisableEdgeStyle( false); 
		//mxGraphModel mg=(mxGraphModel) graph.getModel();
		//mxCell cell = (mxCell) ((mxGraphModel)mg).getCell("3");

		// layout using morphing
		graph.getModel().beginUpdate();
		try {
			layout.execute(graph.getDefaultParent());
		} finally {
			mxMorphing morph = new mxMorphing(graphComponent, 20, 1.5, 20);

			morph.addListener(mxEvent.DONE, new mxIEventListener() {

				@Override
				public void invoke(Object arg0, mxEventObject arg1) {
					graph.getModel().endUpdate();
					// fitViewport();
				}



			});

			morph.startAnimation();
		}

	}
	

	private static String[] getArrayString(String arr) throws Exception
	{
		 String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
		 for (int i=0;i<items.length;i++)
		 {
			 if (!(items[i].startsWith("!")||items[i].startsWith("?")||items[i].startsWith("-")))
				 throw new Exception();
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
