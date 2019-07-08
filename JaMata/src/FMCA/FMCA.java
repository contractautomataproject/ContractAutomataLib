package FMCA;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

import java.io.File;

import CA.CA;
import CA.CAState;
import CA.CATransition;
import FMCA.FMCA;
import FMCA.FMCATransition;
import FMCA.FMCAUtil;
import FSA.Transition;


/** 
 * Class implementing a Modal Service Contract Automaton and its functionalities
 * The class is under construction, some functionalities are not yet updated
 * 
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class FMCA  extends CA implements java.io.Serializable
{
	/*
	private int rank;
	private int[] initial;
	private int[] states;
	private int[][] finalstates; */
//	private static String message = "*** CA ***\n The alphabet is represented by integers: " +
//			" negative numbers are request actions, positive are offer actions, 0 stands for idle\n";
	
	//TODO: add feature constraint
	
	/*float[] xstate=null;		
	float[] ystate;
	float[] xfinalstate;
	float[] yfinalstate;
	*/
	private CAState[] fstates=null;
	private Family family;
	/**
	 * Invoke the super constructor and take in input the added new parameters of the automaton
	 */
	public FMCA()
	{
		super();
	}
	
	public FMCA(int rank, CAState initial, int[] states, int[][] finalstates,FMCATransition[] trans)
	{
		super(rank,initial,states,finalstates,trans);
	}
	
		
	public FMCA(int rank, CAState initial, int[] states, int[][] finalstates,FMCATransition[] trans, Family f)
	{
		super(rank,initial,states,finalstates,trans);
		this.family=f;
	}
	
/*	public FMCA(int rank, int[] initial, int[] states, float[] xstate, float[] ystate, int[][] finalstates, float[] xfinalstate, float[] yfinalstate, FMCATransition[] trans)
	{

		super(rank,initial,states,finalstates,trans);
		this.xstate=xstate;
		this.ystate=ystate;
		this.xfinalstate=xfinalstate;
		this.yfinalstate=yfinalstate;
	}
*/
	public FMCA(int rank, CAState initial, int[] states, int[][] finalstates, FMCATransition[] trans, CAState[] fstates)
	{

		super(rank,initial,states,finalstates,trans);
		this.fstates=fstates;
	}

	/*public FMCA(int rank, int[] initial, int[][] states, int[][] finalstates,FMCATransition[] trans)
	{
		super(rank,initial,FMCA.numberOfPrincipalsStates(FMCAUtil.setUnion(states, finalstates)),
				FMCA.principalsFinalStates(finalstates),trans);
	}*/
	
	public FMCA(int rank, CAState initial, int[][] states, int[][] finalstates, FMCATransition[] trans, CAState[] fstate)
	{

		super(rank,initial,FMCA.numberOfPrincipalsStates(FMCAUtil.setUnion(states, finalstates)),
				FMCA.principalsFinalStates(finalstates),trans);
		System.out.println("");
		this.fstates=fstate;
	}
	
	public void setFamily(Family f)
	{
		this.family=f;
	}
	
	public void setState(CAState[] s)
	{
		this.fstates=s;
	}
	
	public CAState[] getState()
	{
		return this.fstates;
	}
	
	
	public Family getFamily()
	{
		return family;
	}
	
	
	public boolean containAction(String act)
	{
		String[] actions = this.getActions();
		return FMCAUtil.contains(act, actions);
	}
	
	/**
	 * load a FMCA described in a text file, compared to CA it also loads the must transitions
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

								  String[] arr2=arr[ind].split("[,|\\[]"); 
								  try{
									  //Integer.parseInt(arr2[0]); //check if we are reading states!
									  int innerindex=0;
									  int[] tf = new int[states[innerindex]]; //upper bound
									  for(int ind2=0;ind2<arr2.length;ind2++)
									  {
										  try{
											  int num=Integer.parseInt(arr2[ind2]);
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
							  CATransition temp=loadTransition(strLine,rank);
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
							  CATransition temp=loadTransition(strLine,rank);
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
	            			 finalstates[finalstatec]=FMCAUtil.getArray(eElement.getAttribute("value"));
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
	            			 states[statec]=FMCAUtil.getArray(eElement.getAttribute("value"));
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
	            		 
	            		 
	            		 String[] label=FMCAUtil.getArrayString(eElement.getAttribute("value"));
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
	         finalstates=FMCAUtil.removeTailsNull(finalstates, finalstatec);
	         xfinalstate=FMCAUtil.removeTailsNull(xstate, finalstatec);
             yfinalstate=FMCAUtil.removeTailsNull(ystate, finalstatec);
	         states=FMCAUtil.removeTailsNull(states, statec);
             xstate=FMCAUtil.removeTailsNull(xstate, statec);
             ystate=FMCAUtil.removeTailsNull(ystate, statec);
             fstates=FMCAUtil.removeTailsNull(fstates, fstatescount);
             t=FMCAUtil.removeTailsNull(t, trc);
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
	 * write the FMCA as a mxGraphModel for the GUI
	 * @param fileName	write the automaton into fileName
	 * @return
	 */
	public File exportToXML(String fileName)
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
			int[][][] all=this.allNonFinalAndFinalStates();
			int[][] states=all[0];
			Element[] statese=new Element[states.length];
			
			//TODO: smart graph display
			for (int i=0;i<states.length;i++)
			{
				if (this.fstates!=null)
					statese[i]=createElementState(doc, root,Integer.toString(i+2), this.fstates,states[i]);
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
				if (this.fstates!=null)
					statesef[i]=createElementFinalState(doc, root,Integer.toString(i+2+states.length), this.fstates,statesf[i]);
				else
				{	
					CAState[] dum= new CAState[1];
					dum[0]= new CAState(statesf[i],i*200,200); 
					//statesef[i]=createElementFinalState(doc, root,Integer.toString(i+2+states.length), Integer.toString(i*200),"200",statesf[i]);
					statesef[i]=createElementFinalState(doc, root,Integer.toString(i+2+states.length), dum,statesf[i]);
				}
					
			}
			FMCATransition t[]= this.getTransition();
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
	 * @return	the array of transitions
	 */
	public  FMCATransition[] getTransition()
	{
		Transition[] temp = super.getTransition();
		FMCATransition[] t = new FMCATransition[temp.length];
		for (int i=0;i<temp.length;i++)
				t[i]=(FMCATransition)temp[i];
		return t;
	}
	
	
	/**
	 * 
	 * @return  the x coordinate of the furthest state (to the right)
	 */
	public float furthestNodeX()
	{
		float max=0;
		for (int i=0;i<fstates.length;i++)
		{
			if (max<fstates[i].getX())
				max=fstates[i].getX();
		}
		return max;
	}
	
	/**
	 * @return	copy Transitions
	 */
	public  FMCATransition[] copyTransition()
	{
		FMCATransition[] at = this.getTransition();
		FMCATransition[] finalTr = new FMCATransition[at.length];
		for(int i=0;i<finalTr.length;i++)
		{
			CAState in=at[i].getSourceP();
			String[] l=at[i].getLabelP();
			CAState out= at[i].getTargetP();
			//TODO this is not good, the CAState of transitions should point to fstates field of the FMCA,
			//		I removed the clone operation, previously an Arrays.copy operation was also called
			//finalTr[i] = new FMCATransition(in.clone(),Arrays.copyOf(l,l.length),f.clone(),at[i].getType());
			finalTr[i] = new FMCATransition(in,Arrays.copyOf(l,l.length),out,at[i].getType());
		}
		return finalTr;
	}
	
	/**
	 * compared to CA this method also clones the must transitions
	 * @return a new object CA clone
	 */
	public FMCA clone()
	{
		CAState[] clonefstates= this.getState();
		if (fstates!=null)
		{
			for (int i=0;i<clonefstates.length;i++)
			{
				clonefstates[i]=clonefstates[i].clone();
			}
			//TODO: call copyTransitions method and use inherited method
			FMCATransition[] at = this.getTransition();
			FMCATransition[] finalTr = new FMCATransition[at.length];
			for(int i=0;i<finalTr.length;i++)
			{
				CAState in=at[i].getSourceP();
				String[] l=at[i].getLabelP();
				CAState out= at[i].getTargetP();
				in = CAState.getCAStateWithValue(in.getState(), clonefstates);  //retrieve cloned states
				out = CAState.getCAStateWithValue(out.getState(), clonefstates);
				finalTr[i] = new FMCATransition(in,Arrays.copyOf(l,l.length),out,at[i].getType());
			}	
			int[][] finalstates=getFinalStatesCA();
			int[][] nf = new int[finalstates.length][];
			for (int i=0;i<finalstates.length;i++)
				nf[i]=Arrays.copyOf(finalstates[i], finalstates[i].length);
			return new FMCA(getRank(),
					 CAState.getCAStateWithValue(getInitialCA().getState(),clonefstates), 
					 Arrays.copyOf(getStatesCA(), getStatesCA().length), 
					 finalstates,
					 finalTr,
					 clonefstates); 
		}
		else
		{
			//this is used when a composition is computed and fstates are not yet generated
			//TODO probably this should be fixed
			FMCATransition[] at = this.getTransition();
			FMCATransition[] finalTr = new FMCATransition[at.length];
			for(int i=0;i<finalTr.length;i++)
			{
				CAState in=at[i].getSourceP();
				String[] l=at[i].getLabelP();
				CAState f= at[i].getTargetP();
				finalTr[i] = new FMCATransition(in.clone(),Arrays.copyOf(l,l.length),f.clone(),at[i].getType());
			}	
			int[][] finalstates=getFinalStatesCA();
			int[][] nf = new int[finalstates.length][];
			for (int i=0;i<finalstates.length;i++)
				nf[i]=Arrays.copyOf(finalstates[i], finalstates[i].length);
			return new FMCA(getRank(),getInitialCA().clone(), 
					 Arrays.copyOf(getStatesCA(), getStatesCA().length), 
					 finalstates,
					 finalTr);
		}		
	}
	
	/**
	 * compute the projection on the i-th principal
	 * @param indexprincipal		index of the FMCA
	 * @return		the ith principal
	 */
	public FMCA proj(int indexprincipal)
	{
		if ((indexprincipal<0)||(indexprincipal>this.getRank())) //check if the parameter i is in the rank of the FMCA
			return null;
		if (this.getRank()==1)
			return this;
		FMCATransition[] tra = this.getTransition();
		int[] numberofstatesprincipal= new int[1];
		numberofstatesprincipal[0]= this.getStatesCA()[indexprincipal];
		FMCATransition[] transitionsprincipal = new FMCATransition[tra.length];
		int pointer=0;
		for (int ind=0;ind<tra.length;ind++)
		{
			FMCATransition tt= ((FMCATransition)tra[ind]);
			String label = tt.getLabelP()[indexprincipal];
			if(label!=CATransition.idle)
			{
				int source =  tt.getSourceP().getState()[indexprincipal];
				int dest = tt.getTargetP().getState()[indexprincipal];
				int[] sou = new int[1];
				sou[0]=source;
				int[] des = new int[1];
				des[0]=dest;
				String[] lab = new String[1];
				lab[0]=label;
				FMCATransition selected = null;
				if (label.substring(0,1).equals(CATransition.offer))
				{
					selected = new FMCATransition(new CAState(sou),lab, new CAState(des),FMCATransition.action.PERMITTED);
				}
				else {
					selected = new FMCATransition(new CAState(sou),lab, new CAState(des),tt.getType());
				}
				
				if (!FMCAUtil.contains(selected, transitionsprincipal, pointer))
				{
					transitionsprincipal[pointer]=selected;
					pointer++;
				}
			}
		}
		
		transitionsprincipal = FMCAUtil.removeTailsNull(transitionsprincipal, pointer);
		CAState[] fstates = CAState.extractCAStatesFromTransitions(transitionsprincipal);
		int[] init=new int[1]; init[0]=0;
		CAState initialstateprincipal = CAState.getCAStateWithValue(init, fstates);
		initialstateprincipal.setInitial(true);  //if is dangling will throw exception
		int[][] finalstatesprincipal = new int[1][];
		finalstatesprincipal[0]=this.getFinalStatesCA()[indexprincipal];
		for (int ind=0;ind<finalstatesprincipal[0].length;ind++)
		{
			int[] value=new int[1]; value[0]=finalstatesprincipal[0][ind];
			CAState.getCAStateWithValue(value, fstates).setFinalstate(true); //if is dangling will throw exception
		}
		// FMCA(int rank, CAState initial, int[] states, int[][] finalstates, FMCATransition[] trans, CAState[] fstates)
		
		return new FMCA(1,initialstateprincipal,numberofstatesprincipal,finalstatesprincipal,transitionsprincipal,fstates); 
	}
	
	
	/**
	 * compute the most permissive controller of product p
	 * the algorithm is different from the corresponding of MSCA
	 * 
	 * @return the most permissive controller for modal agreement
	 */
	public FMCA mpc(Product p)
	{
		FMCA a = this.clone();
		FMCATransition[] tr = a.getTransition();
		FMCATransition[] rem = new FMCATransition[tr.length];  //solo per testing
		//int[][] fs=a.allFinalStates();
		int removed = 0;
		
		//I need to store the transitions, to check later on 
		//if some controllable transition becomes uncontrollable
		FMCATransition[] potentiallyUncontrollable = new FMCATransition[tr.length]; 
		int potentiallyUncontrollableCounter = 0;
		
		FMCATransition[] badtransitions=new FMCATransition[tr.length]; 
		int badtransitioncounter=0;
		
		//I need a copy of the actual transitions of K_i because in the loop I remove transitions 
		//and this operation affects the set of uncontrollable transitions in K_i
		FMCATransition[] trcopy=a.copyTransition();
		
		
		for (int i=0;i<tr.length;i++)
		{
		//	System.out.println("transition "+i);
			if (!tr[i].isUncontrollable(a)&&(tr[i].isRequest()||tr[i].isForbidden(p))) //controllable and bad
			{
				rem[removed]=tr[i]; //solo per testing
				trcopy[i] = null;
				removed++;
			}
			if(	(tr[i].isGreedy()&&tr[i].isRequest())	||	(tr[i].isLazy()))
			{
				potentiallyUncontrollable[potentiallyUncontrollableCounter]= new FMCATransition(tr[i].getSourceP(),tr[i].getLabelP(),tr[i].getTargetP(),tr[i].getType());
				potentiallyUncontrollableCounter++;		
			}
		}
		
		tr=trcopy;
		tr=  FMCAUtil.removeHoles(tr, removed);		
		a.setTransition(tr); //K_0 
		
		//computing R_0
		for (int i=0;i<tr.length;i++)
		{
			if (tr[i].isUncontrollable(a)) 	//uncontrollable and bad
			{	
				if ((tr[i].isRequest()||tr[i].isForbidden(p)))
				{
					badtransitions[badtransitioncounter]= new FMCATransition(tr[i].getSourceP(),tr[i].getLabelP(),tr[i].getTargetP(),tr[i].getType());
					badtransitioncounter++;		
				}
			}
			if(	(tr[i].isGreedy()&&tr[i].isRequest())	||	(tr[i].isLazy()))
			{
				potentiallyUncontrollable[potentiallyUncontrollableCounter]= new FMCATransition(tr[i].getSourceP(),tr[i].getLabelP(),tr[i].getTargetP(),tr[i].getType());
				potentiallyUncontrollableCounter++;		
			}
		}

		badtransitions=FMCAUtil.removeTailsNull(badtransitions, badtransitioncounter);
		potentiallyUncontrollable = FMCAUtil.removeTailsNull(potentiallyUncontrollable, potentiallyUncontrollableCounter);
		
		// TODO now CAState are fully compared, check if computing dangling states makes two equal states different
		//
		CAState[] unmatchedOrLazyunmatchable=new CAState[potentiallyUncontrollable.length];
		CAState[] R=FMCAUtil.setUnion(a.getDanglingStates(), FMCATransition.getSources(badtransitions)); //R_0
		boolean update=false;
		if (R.length>0)
		{
			do{
				FMCATransition[] trcheck= new FMCATransition[tr.length*R.length];//used for storing all uncontrollable transitions without bad source state
				int trcheckpointer=0;
				removed=0;
				rem= new FMCATransition[tr.length]; 
				
				//I need a copy of the actual transitions of K_i because in the loop I remove transitions 
				//and this operation affects the set of uncontrollable transitions in K_i
				trcopy=a.copyTransition();
				for (int i=0;i<tr.length;i++)  //for all transitions
				{
					if (!(tr[i]==null))
					{
						if (tr[i].isUncontrollable(a)) 
						{   
							if (FMCAUtil.contains(tr[i].getSourceP(), R)) //remove uncontrollable with bad source
							{
								rem[removed]=tr[i];//solo per testing
								trcopy[i]=null;
								removed++;
							}
							else
							{
								trcheck[trcheckpointer]=tr[i]; //store all uncontrollable transitions without bad source state
								trcheckpointer++;
							}
						}
						else if (!tr[i].isUncontrollable(a)&&(FMCAUtil.contains(tr[i].getTargetP(), R))) //remove controllable with bad target
						{
							rem[removed]=tr[i]; //solo per testing
							trcopy[i]=null;
							removed++;
						}
					}
				} 
				tr=trcopy;
				tr=  FMCAUtil.removeHoles(tr, removed);
				a.setTransition(tr);  //K_i
				//
				//
				// building R_i
				//
				//
				CAState[] danglingStates = a.getDanglingStates();
				CAState[] newR=new CAState[trcheckpointer];
				int newRpointer=0;
				
				for (int i=0;i<trcheckpointer;i++)//for all uncontrollable transitions without bad source state
				{
					//if target state is bad,  add source state to R if it has not been already added, we know that source state is not in R
					// setUnion removes duplicates we could skip the check
					if ((FMCAUtil.contains(trcheck[i].getTargetP(), R)&&(!FMCAUtil.contains(trcheck[i].getSourceP(),R))))
					{
						newR[newRpointer]=trcheck[i].getSourceP();
						newRpointer++;
					}
				}
				//add dangling states to R
				CAState[] RwithDang =	FMCAUtil.setUnion(R ,danglingStates);
				update = (RwithDang.length!=R.length);
				if (update)
					R = RwithDang;
				
				//add source states of uncontrollable transitions with redundant target to R
				if (newRpointer>0)
				{
					R=FMCAUtil.setUnion(R, FMCAUtil.removeTailsNull(newR, newRpointer));
					update=true;
				}
				
				//add source states of uncontrollable transitions that were previously controllable
				CAState[] su= FMCATransition.areUnmatchedOrLazyUnmatchable(potentiallyUncontrollable, a);
				CAState[] newUnmatchedOrLazyunmatchable =	FMCAUtil.setUnion(unmatchedOrLazyunmatchable,su);
				if (newUnmatchedOrLazyunmatchable.length!=unmatchedOrLazyunmatchable.length)
				{
					unmatchedOrLazyunmatchable=newUnmatchedOrLazyunmatchable;
					R=FMCAUtil.setUnion(R, unmatchedOrLazyunmatchable);
					update=true;
				}
				
			}while(update);
		}
		
		//a.getDanglingStates();
		a = (FMCA) FMCAUtil.removeUnreachableTransitions(a);
		
		//if initial state is bad or not all required actions are fired
		if (FMCAUtil.contains(a.getInitialCA(), R)||(!p.checkRequired(a.getTransition())))
			return null;
		
		return a;
	}
		
	
	/**
	 * an array containing the number of  states of each principal
	 * @param states  all the states of the MSCA enumerated
	 * @return
	 */
	public static int[] numberOfPrincipalsStates(int[][] states)
	{
		int[] rank = new int[states[0].length];
		for (int i=0;i<rank.length;i++)
		{
			int[] principalstates=new int[states.length];//upperbound
			int count=0;
			for (int j=0;j<principalstates.length;j++)
			{
				if (!FMCAUtil.contains(states[j][i],principalstates,count))
				{
					principalstates[count]=states[j][i];
					count++;
				}
			}
			rank[i]=count;
		}
		
//		the old code was selecting the state with higher value, not working for states renaming (e.g. FMCA union)
//		for (int j=0;j<max.length;j++)
//			max[j]=-1;
//		for (int i=0;i<states.length;i++)
//		{
//			for (int j=0;j<max.length;j++)
//			{
//				if (max[j]<states[i][j])
//					max[j]=states[i][j];
//			}
//		}
//		for (int j=0;j<max.length;j++)
//			max[j]+=1;		
		return rank;
	}
	
	/**
	 * 
	 * @param all final states of the composed automaton
	 * @return the final states of each principal
	 */
	public static int[][] principalsFinalStates(int[][] states)
	{
		if (states.length<=0)
			return null;
		int rank=states[0].length;
		int[] count=new int[rank];
		int[][] pfs=new int[rank][states.length];
		for (int j=0;j<rank;j++)
		{
			pfs[j][0]=states[0][j];
			count[j]=1;
		}
		for (int i=1;i<states.length;i++)
		{
			for (int j=0;j<rank;j++)
			{
				if (FMCAUtil.getIndex(pfs[j], states[i][j])==-1 )
				{
					pfs[j][count[j]]=states[i][j];
					count[j]++;
				}
			}
		}
		for (int j=0;j<rank;j++)
			pfs[j]=FMCAUtil.removeTailsNull(pfs[j], count[j]);
		return pfs;
	}
	
	/**
	 * 
	 * 
	 * @return all  states that appear in at least one transition
	 */
	public int[][] allStates()
	{
		FMCA aut=this.clone();
		int[][] s = new int[this.prodStates()+1][]; //there could be a dummy initial state
		s[0]=aut.getInitialCA().getState();
		FMCATransition[] t = aut.getTransition();
		int pointer=1;
		for (int i=0;i<t.length;i++)
		{
			int[] start = t[i].getSourceP().getState();
			int[] arr = t[i].getTargetP().getState();
			
			if (!FMCAUtil.contains(arr, s))
			{
				s[pointer]=arr;
				pointer++;
			}
			if (!FMCAUtil.contains(start, s))
			{
				s[pointer]=start;
				pointer++;
			}
		}
		s=FMCAUtil.removeTailsNull(s, pointer);
//	    int[][] f = new int[pointer][];
//	    for (int i=0;i<pointer;i++)
//	    	f[i]=s[i];
		return s;
	}
	
	@Override
	public int getStates()
	{
		return this.allStates().length;
	}
	
	public int[][][] allNonFinalAndFinalStates()
	{
		int[][][] r = new int[2][][];
		int[][] states=this.allStates();
		int[][] finalstates=FMCAUtil.setIntersection(states, this.allFinalStates());//only reachable final states
		int[][] nonfinalstates=FMCAUtil.setDifference(states, finalstates);
		
		r[0]=nonfinalstates;
		r[1]=finalstates;
		return r;
	}
	
	/**
	 * 
	 * @return all actions present in the automaton
	 */
	public String[] getActions()
	{
		FMCATransition[] tr=this.getTransition();
		String[] act = new String[tr.length];
		for (int i=0;i<tr.length;i++)
			act[i]=	CATransition.getUnsignedAction(tr[i].getAction());
		act=FMCAUtil.removeDuplicates(act);
		return act;
	}
	
	/**
	 * return redundant states who do not reach a final state or are unreachable
	 * not inherited from CA
	 * @return	redundant states of at
	 */
	protected CAState[] getDanglingStates()
	{
		
//		int[][] fs = this.allFinalStates();
//		int[][] redundantStates = new int[this.prodStates()][];
//		//int[][] allStates = this.allStates();		
//		int redundantStatesPointer=0;
//		this.setReachableStates();
//		for (int ind=0;ind<this.fstates.length;ind++) //for all states
//		{
//				//TODO check if it is possible to check reachability from initial state only once
//				// for each state checks if it reaches one of the final states  and if it is reachable from the initial state
//				int[] pointervisited = new int[1];
//				pointervisited[0]=0;
//				
//				//I need to check the reachability from initial state only once!
//				boolean remove=!FMCAUtil.amIReachable(allStates[ind],this,getInitialCA().getState(),new int[this.prodStates()][],
//						pointervisited,null,null,0,0);  	
//				
//				if (fstates[ind].isSetReachable())//!remove) //if it is reachable from initial state
//				{
//					remove=true;  // at the end of the loop if remove=true none of final states is reachable
//					for (int i=0;i<fs.length;i++)
//					{
//						pointervisited = new int[1];
//						pointervisited[0]=0;
//						if((FMCAUtil.amIReachable(fs[i],this,allStates[ind],new int[this.prodStates()][],pointervisited,
//								null,null,0,0)&&remove))  
//							remove=false;
//					}
//				}
//				if ((remove))
//				{
//					redundantStates[redundantStatesPointer]=fstates[ind].getState();
//					redundantStatesPointer++;
//				}													
//		}
//		//remove null space in array redundantStates
//		redundantStates = FMCAUtil.removeTailsNull(redundantStates, redundantStatesPointer);
//		
//		return redundantStates;
		this.resetReachableAndSuccessfulStates();
		this.setReachableAndSuccessfulStates();
		CAState[] dang=new CAState[fstates.length];
		
		int dangcounter=0;
		for (int i=0;i<dang.length;i++)
		{
			if (!(fstates[i].isReachable()&&fstates[i].isSuccessfull()))
			{
				dang[dangcounter]=fstates[i];
				dangcounter++;
			}	
		}
		return FMCAUtil.removeTailsNull(dang, dangcounter);
	}
	
	/**
	 * this method is not inherited from MSCA
	 * @return	all the  must transitions request that are not matched 
	 */
	protected  FMCATransition[] getUnmatch()
	{
		FMCATransition[] tr = this.getTransition();
		int[][] fs=this.allFinalStates();
		int pointer=0;
		CAState[] R=this.getDanglingStates();
		FMCATransition[] unmatch = new FMCATransition[tr.length];
		for (int i=0;i<tr.length;i++)
		{
			if ((tr[i].isRequest())
				&&((tr[i].isMust())
				&&(!FMCAUtil.contains(tr[i].getSourceP().getState(), fs)))) // if source state is not final
			{
				boolean matched=false;
				for (int j=0;j<tr.length;j++)	
				{
					if ((tr[j].isMatch())
						&&(tr[j].isMust())
						&&(tr[j].getReceiver()==tr[i].getReceiver())	//the same principal
						&&(tr[j].getSourceP().getState()[tr[j].getReceiver()]==tr[i].getSourceP().getState()[tr[i].getReceiver()]) //the same source state					
						&&(tr[j].getLabelP()[tr[j].getReceiver()]==tr[i].getLabelP()[tr[i].getReceiver()]) //the same request
						&&(!FMCAUtil.contains(tr[i].getSourceP(), R))) //source state is not redundant
						{
							matched=true; // the request is matched
						}
				}
				if (!matched)
				{
					unmatch[pointer]=tr[i];
					pointer++;
				}
			}
		}
		if (pointer>0)
		{
			unmatch = FMCAUtil.removeTailsNull(unmatch, pointer);
			return unmatch;
		}
		else
			return null;
	}

	private int[] getIndexOfLazyTransitions()
	{
		
		FMCATransition[] tr = this.getTransition();
		int[] arr = new int[tr.length];
		int count=0;
		for (int i=0;i< tr.length;i++)
		{
			if (tr[i].isLazy())
			{
				arr[count]=i;
				count++;
			}
		}
		arr = FMCAUtil.removeTailsNull(arr, count);
		return arr;
	}
	
	public String removeLazy()
	{
		int[] arr = this.getIndexOfLazyTransitions();
		int l = arr.length;
		long ll = (long) Math.pow(2.0, (double)arr.length);
		long ns = this.getStates()+1;
		//FMCA[] aut = new FMCA[(int)Math.pow(2.0, (double)arr.length)];
		return "The automaton contains the following number of lazy transitions : "+l+" \n"
				//+"There are 2^"+l+" possible combinations of removing such transitions.\n"
				+"The resulting automaton with only urgent transitions will have the following number of states ("+ns+") * (2^"+l+"-1)";
	}
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
	
	 public void setReachableAndSuccessfulStates()
	 {
		 visit(this.getInitialCA()); //firstly reachability must be set !
		 //TODO record the set of final states this is a fix
		 int[][] fs = this.allFinalStates();
		 for (int i=0; i<fs.length; i++)
		 {
			 CAState f = CAState.getCAStateWithValue(fs[i], this.getState());
			 if (f!=null)//not all combinations of final states could be available (in case a controller is checked)
				 reverseVisit(f);
		 }
	 }
	 
	 public void resetReachableAndSuccessfulStates()
	 {
		 for (int i=0;i<fstates.length;i++)
		 {
			 fstates[i].setReachable(false);
			 fstates[i].setSuccessfull(false);
		 } 
	 }
	 
	/**
	 * each reachable states will be set
	 */
	public void setReachableStates()
	{
		visit(this.getInitialCA());
	}
	
	/**
	 * s = current state
	 * forall t in FS(s)
	 * 		if target(t) not visited
	 * 			visited += target(t); iterate( target(t))
	 * 		else
	 * 			do nothing
	 * 
	 */
	private void visit(CAState currentstate)
	{ 
		currentstate.setReachable(true);
		FMCATransition[] tr=FMCATransition.getTransitionFrom(currentstate, this.getTransition());
//		if (tr==null)
//		{
//			tr=FMCATransition.getTransitionFrom(currentstate, this.getTransition());
//		}
		for (int i=0;i<tr.length;i++)
		{
			CAState target=tr[i].getTargetP();
			if (!target.isReachable())
				visit(target);
		}
	}
	
	private void reverseVisit(CAState currentstate)
	{ 
		currentstate.setSuccessfull(true);
		FMCATransition[] tr=FMCATransition.getTransitionTo(currentstate, this.getTransition());
		for (int i=0;i<tr.length;i++)
		{
			CAState source=tr[i].getSourceP();
			if (source.isReachable()&&!source.isSuccessfull()) //warning: it requires to compute reachability
				reverseVisit(source);
		}
	}
	 
	public FMCATransition[] createArrayTransition(int length)
	{
		return new FMCATransition[length];
	}
	public FMCATransition[][] createArrayTransition2(int length)
	{
		return new FMCATransition[length][];
	}
	public FMCA createNew(int rank, CAState initial, int[] states, int[][] finalstates,CATransition[] tra)
	{
		return new FMCA(rank,initial,states,finalstates,(FMCATransition[])tra);
	}
}




///**
// * 
// * similar to the corresponding method in CA class, with CATransition swapped with MSCATransition and CA swapped with MSCA  
// * @return all the final states of the CA
// */
//public  int[][] allFinalStates()
//{
////	if (rank==1)
////		return finalstates;
//
//	int[][] finalstates=getFinalStatesCA();
//	int[] states=new int[finalstates.length];
//	int comb=1;
//	int[] insert= new int[states.length];
//	for (int i=0;i<states.length;i++)
//	{
//		states[i]=finalstates[i].length;
//		comb*=states[i];
//		insert[i]=0;
//	}
//	int[][] modif = new int[comb][];
//	int[] indstates = new int[1];
//	indstates[0]= states.length-1;
//	int[] indmod = new int[1];
//	indmod[0]= 0; 
//	//CAUtil.recGen(finalstates, modif,  states, 0, states.length-1, insert);
//	MSCAUtil.recGen(finalstates, modif,  states, indmod, indstates, insert);
//	return modif;
//}
/*	public FMCA mpcConstraints(int[][][] products,int[][] L)
{
	int[][][][] statesToVisit= new int[this.numberOfStates()][][][];
	statesToVisit[0]=products;
	return null;
}*/

