package FMCA;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;



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

import java.io.File;

import CA.CA;
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
	
	/**
	 * Invoke the super constructor and take in input the added new parameters of the automaton
	 */
	public FMCA()
	{
		super();
	}
	
	public FMCA(int rank, int[] initial, int[] states, int[][] finalstates,FMCATransition[] trans)
	{
		super(rank,initial,states,finalstates,trans);
	}
	
	public FMCA(int rank, int[] initial, int[][] states, int[][] finalstates,FMCATransition[] trans)
	{
		super(rank,initial,FMCA.numberOfPrincipalsStates(FMCAUtil.setUnion(states, finalstates)),
				FMCA.principalsFinalStates(finalstates),trans);
	}
	
	
	
	/**
	 * load a MSCA described in a text file, compared to CA it also loads the must transitions
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
			String strLine;
			int rank=0;
			int[] initial = new int[1];
			int[] states = new int[1];
			int[][] fin = new int[1][];
			FMCATransition[] t = new FMCATransition[1];
		//	MSCATransition[] mustt = new MSCATransition[1];
			int pointert=0;
	//		int pointermust=0;
			
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
							  Scanner s = new Scanner(strLine);
							  s.useDelimiter("");
							  while (s.hasNext())
							  {
								  if (s.hasNextInt())
								  {
									  rank = s.nextInt();
								  }
								  else
								  {
									  s.next();
								  }
							  }
							  initial = new int[rank];
							  states = new int[rank];
							  fin = new int[rank][];
							  s.close();
							  break;
						  }
						  case "N":	//Number of states line
						  {
							  Scanner s = new Scanner(strLine);
							  s.useDelimiter("");
							  int i=0;
							  int lengthT=1;
							  while (s.hasNext())
							  {
								  if (s.hasNextInt())
								  {
									  states[i] = s.nextInt();
									  lengthT*=states[i];
									  i++;
								  }
								  else
									  s.next();
							  }
							  t = new FMCATransition[lengthT*lengthT*4];//guessed upper bound WARNING
							  s.close();
							  break;
						  }
						  case "I": //Initial state
						  {
							  Scanner s = new Scanner(strLine);
							  s.useDelimiter("");
							  int i=0;
							  while (s.hasNext())
							  {
								  if (s.hasNextInt())
								  {
									  initial[i] = s.nextInt();
									  i++;
								  }
								  else
									  s.next();
							  }
							  s.close();
							  break;
						  }
						  case "F": //Final state
						  {
							  String[] ss=strLine.split("]");
							  for (int ind=0;ind<ss.length;ind++)
							  {
								  Scanner s = new Scanner(ss[ind]);
								  s.useDelimiter("");
								  int i=0;
								  int[] tf = new int[states[i]]; //upper bound
								  while (s.hasNext())
								  {
									  if (s.hasNextInt())
									  {
										  tf[i] = s.nextInt();
										  i++;
									  }
									  else
										  s.next();
								  }
								  fin[ind]= new int[i];
								  for (int ii=0;ii<i;ii++)
									  fin[ind][ii]=tf[ii];
								  s.close();
							  }
							  break;
						  }
						  case "(": //a may transition
						  {
							  String[] ss=strLine.split("]");
							  int what=0;
							  int[][] store=new int[2][];
							  for (int i=0;i<ss.length;i++)
							  {
								  int[] arr = new int[rank];
								  Scanner s = new Scanner(ss[i]);
								  s.useDelimiter(",|\\[| ");
								  int j=0;
								  while (s.hasNext())
								  {
									  if (s.hasNextInt())
									  {
										 arr[j]=s.nextInt();
										 j++;
									  }
									  else {
										   s.next();
									  }
								  }
								  s.close();
								  if (what==2)
								  {
									  t[pointert]=new FMCATransition(store[0],store[1],arr,FMCATransition.action.PERMITTED);
									  what=0;
									  pointert++;
								  }
								  else
									  store[what]=arr;
								  what++;
							  }						 
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
							  String[] ss=strLine.split("]");
							  int what=0;
							  int[][] store=new int[2][];
							  for (int i=0;i<ss.length;i++)
							  {
								  int[] arr = new int[rank];
								  Scanner s = new Scanner(ss[i]);
								  s.useDelimiter(",|\\[| ");
								  int j=0;
								  while (s.hasNext())
								  {
									  if (s.hasNextInt())
									  {
										 arr[j]=s.nextInt();
										 j++;
									  }
									  else {
										   s.next();
									  }
								  }
								  s.close();
								  if (what==2)
								  {
									  t[pointert]=new FMCATransition(store[0],store[1],arr,type);
									  what=0;
									  pointert++;
								  }
								  else
									  store[what]=arr;
								  what++;
							  }						 
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
			 
			return new FMCA(rank,initial,states,fin,fintr);
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
	
	/**
	 * parse the XML description of graphEditor into an MSCA object
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
	         int[][] states=new int[nodeList.getLength()][];
	         int[][] finalstates=new int[nodeList.getLength()][];
	         int[] idstate=new int[nodeList.getLength()]; //each node has associated an id, used for identifying source and target of an edge
	         int[] idfinalstate=new int[nodeList.getLength()];
	         FMCATransition[] t= new FMCATransition[nodeList.getLength()];
	         int statec=0;
	         int finalstatec=0;
	         int trc=0;
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
	            	 {
	            		 if (eElement.getAttribute("style").contains("terminate.png"))
	            		 { 
	            			 idfinalstate[finalstatec]=Integer.parseInt(eElement.getAttribute("id"));
	            			 finalstates[finalstatec]=FMCAUtil.getArray(eElement.getAttribute("value"));
	            			 finalstatec++;
	            		 }
	            		 else{
	            			 idstate[statec]=Integer.parseInt(eElement.getAttribute("id"));
	            			 states[statec]=FMCAUtil.getArray(eElement.getAttribute("value"));
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
	            		 
	            		 
	            		 int[] label=FMCAUtil.getArray(eElement.getAttribute("value"));
	            		 if (eElement.getAttribute("style").contains("strokeColor=#FF0000"))
	            			 t[trc]=new FMCATransition(source,label,target,FMCATransition.action.URGENT);//red
	            		 else if (eElement.getAttribute("style").contains("strokeColor=#FFA500"))
	 	            		 t[trc]=new FMCATransition(source,label,target,FMCATransition.action.GREEDY); //orange
	            		 else if (eElement.getAttribute("style").contains("strokeColor=#00FF00"))
	 	            		 t[trc]=new FMCATransition(source,label,target,FMCATransition.action.LAZY); //green
	            		 else 
	 	            		 t[trc]=new FMCATransition(source,label,target,FMCATransition.action.PERMITTED); //otherwise
	            		 trc++;
	            	 }
	               }
	            }
	         }
	         finalstates=FMCAUtil.removeTailsNull(finalstates, finalstatec);
             states=FMCAUtil.removeTailsNull(states, statec);
             t=FMCAUtil.removeTailsNull(t, trc);
             int rank=states[0].length;
             int[] initial = new int[rank];
             for (int ind=0;ind<rank;ind++)
          	   initial[ind]=0;
             FMCA aut= new FMCA(rank, initial,states,finalstates, t);
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
	 * write the MSCA as a mxGraphModel for the GUI
	 * @param fileName
	 * @return
	 */
	public String exportToXML(String fileName)
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
			int[][][] all=this.allNonFinalAndFinalStates();
			int[][] states=all[0];
			Element[] statese=new Element[states.length];
			
			//TODO: smart graph display
			for (int i=0;i<states.length;i++)
				statese[i]=createElementState(doc, root,Integer.toString(i+2), Integer.toString(i*200),"60",states[i]);
			
			int[][] statesf=all[1];
			Element[] statesef=new Element[statesf.length];
			for (int i=0;i<statesf.length;i++)
				statesef[i]=createElementFinalState(doc, root,Integer.toString(i+2+states.length), Integer.toString(i*200),"200",statesf[i]);
			
			FMCATransition t[]= this.getTransition();
			for (int i=0;i<t.length;i++)
			{
				Element s; Element ta;
				int source;
				source=FMCAUtil.indexContains(t[i].getSourceP(), states);
				if (source==-1)
				{
					source=FMCAUtil.indexContains(t[i].getSourceP(), statesf);
					s=statesef[source];
				}
				else
					s=statese[source];
				int target;
				target=FMCAUtil.indexContains(t[i].getTargetP(), states);
				if (target==-1)
				{
					target=FMCAUtil.indexContains(t[i].getTargetP(), statesf);
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
			String s=fileName.substring(0,fileName.indexOf("."));
			StreamResult result =
					new StreamResult(new File(s+".mxe"));
			transformer.transform(source, result);
			/*// Output to console for testing
			StreamResult consoleResult =
					new StreamResult(System.out);
			transformer.transform(source, consoleResult);*/
			return s+".mxe";
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
		mxPointSource.setAttribute("x", ((Element)source.getChildNodes().item(0)).getAttribute("x"));
		mxPointSource.setAttribute("y", ((Element)source.getChildNodes().item(0)).getAttribute("y"));
		mxGeometry1.appendChild(mxPointSource);
		Element mxPointTarget=doc.createElement("mxPoint");
		mxPointTarget.setAttribute("as","targetPoint");
		mxPointTarget.setAttribute("x", ((Element)target.getChildNodes().item(0)).getAttribute("x"));
		mxPointTarget.setAttribute("y", ((Element)target.getChildNodes().item(0)).getAttribute("y"));
		mxGeometry1.appendChild(mxPointTarget);
		
		Element pointArray=doc.createElement("Array");
		pointArray.setAttribute("as","points");
		Element mxPoint=doc.createElement("mxPoint");
		float coordinate=(Float.parseFloat(((Element)source.getChildNodes().item(0)).getAttribute("x")) 
				+ Float.parseFloat(((Element)target.getChildNodes().item(0)).getAttribute("x"))
				)/2;
		mxPoint.setAttribute("x", coordinate+"");
		coordinate=(Float.parseFloat(((Element)source.getChildNodes().item(0)).getAttribute("y")) 
				+ Float.parseFloat(((Element)target.getChildNodes().item(0)).getAttribute("y"))
				)/2;
		mxPoint.setAttribute("y", coordinate+"");
		pointArray.appendChild(mxPoint);
		
		mxGeometry1.appendChild(pointArray);
		mxcell1.appendChild(mxGeometry1);
		root.appendChild(mxcell1);
		return mxcell1;		
	}
	private static Element createElementState(Document doc, Element root,String id, String x,String y,int[] state)
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
		mxGeometry1.setAttribute("x", x);
		mxGeometry1.setAttribute("y", y);
		mxcell1.appendChild(mxGeometry1);
		root.appendChild(mxcell1);
		return mxcell1;		
	}
	
	private static Element createElementFinalState(Document doc, Element root,String id, String x,String y,int[] state)
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
		mxGeometry1.setAttribute("x", x);
		mxGeometry1.setAttribute("y", y);
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
	 * @return	copy Transitions
	 */
	public  FMCATransition[] copyTransition()
	{
		FMCATransition[] at = this.getTransition();
		FMCATransition[] finalTr = new FMCATransition[at.length];
		for(int i=0;i<finalTr.length;i++)
		{
			int[] in=at[i].getSourceP();
			int[] l=at[i].getLabelP();
			int[] f= at[i].getTargetP();
			finalTr[i] = new FMCATransition(Arrays.copyOf(in,in.length),Arrays.copyOf(l,l.length),Arrays.copyOf(f,f.length),at[i].getType());
		}
		return finalTr;
	}
	
	/**
	 * compared to CA this method also clones the must transitions
	 * @return a new object CA clone
	 */
	public FMCA clone()
	{
		//TODO: call copyTransitions method and use inherited method
		FMCATransition[] at = this.getTransition();
		FMCATransition[] finalTr = new FMCATransition[at.length];
		for(int i=0;i<finalTr.length;i++)
		{
			int[] in=at[i].getSourceP();
			int[] l=at[i].getLabelP();
			int[] f= at[i].getTargetP();
			finalTr[i] = new FMCATransition(Arrays.copyOf(in,in.length),Arrays.copyOf(l,l.length),Arrays.copyOf(f,f.length),at[i].getType());
		}	
		int[][] finalstates=getFinalStatesCA();
		int[][] nf = new int[finalstates.length][];
		for (int i=0;i<finalstates.length;i++)
			nf[i]=Arrays.copyOf(finalstates[i], finalstates[i].length);
		
		return new FMCA(getRank(),Arrays.copyOf(getInitialCA(), getInitialCA().length),Arrays.copyOf(getStatesCA(), getStatesCA().length),finalstates,finalTr);
	}
	
	/**
	 * compute the projection on the i-th principal, or null if rank=1
	 * @param i		index of the CA
	 * @return		the ith principal
	 */
	public FMCA proj(int i)
	{
		/*
		if ((i<0)||(i>rank)) //check if the parameter i is in the rank of the CA
			return null;
		MSCATransition[] tra = this.getTransition();
		int[] init = new int[1];
		init[0]=initial[i];
		int[] st= new int[1];
		st[0]= states[i];
		int[][] fi = new int[1][];
		fi[0]=finalstates[i];
		MSCATransition[] t = new MSCATransition[tra.length];
		int pointer=0;
		for (int ind=0;ind<tra.length;ind++)
		{
			MSCATransition tt= ((MSCATransition)tra[ind]);
			int label = tt.getLabelP()[i];
			if(label!=0)
			{
				int source =  tt.getSource()[i];
				int dest = tt.getArrival()[i];
				int[] sou = new int[1];
				sou[0]=source;
				int[] des = new int[1];
				des[0]=dest;
				int[] lab = new int[1];
				lab[0]=label;
				MSCATransition selected = new MSCATransition(sou,lab,des);
				boolean skip=false;
				for(int j=0;j<pointer;j++)
				{
					if (t[j].equals(selected))
					{
						skip=true;
						break;
					}
				}
				if (!skip)
				{
					t[pointer]=selected;
					pointer++;
				}
			}
		}
		
		tra = new MSCATransition[pointer];
		for (int ind=0;ind<pointer;ind++)
			tra[ind]=t[ind];
		return new MSCA(1,init,st,fi,tra); */
		return null;  //TODO
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
			if (!tr[i].isUncontrollable(a)&&(tr[i].isRequest()||tr[i].isForbidden(p))) //controllable and bad
			{
				rem[removed]=tr[i]; //solo per testing
				trcopy[i] = null;
				removed++;
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
		
		int[][] unmatchedOrLazyunmatchable=new int[potentiallyUncontrollable.length][];
		int[][] R=FMCAUtil.setUnion(a.getDanglingStates(), FMCATransition.getSources(badtransitions)); //R_0
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
				int[][] danglingStates = a.getDanglingStates();
				int[][] newR=new int[trcheckpointer][];
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
				int[][] RwithDang =	FMCAUtil.setUnion(R ,danglingStates);
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
				int[][] su= FMCATransition. areUnmatchedOrLazyUnmatchable(potentiallyUncontrollable, a);
				int[][] newUnmatchedOrLazyunmatchable =	FMCAUtil.setUnion(unmatchedOrLazyunmatchable,su);
				if (newUnmatchedOrLazyunmatchable.length!=unmatchedOrLazyunmatchable.length)
				{
					unmatchedOrLazyunmatchable=newUnmatchedOrLazyunmatchable;
					R=FMCAUtil.setUnion(R, unmatchedOrLazyunmatchable);
					update=true;
				}
				
			}while(update);
		}
		//if initial state is bad or not all required actions are fired
		if (FMCAUtil.contains(a.getInitialCA(), R)||(!p.checkRequired(a.getTransition())))
			return null;
		
		a.getDanglingStates();
		a = (FMCA) FMCAUtil.removeUnreachable(a);
		return a;
	}
	
	
	
	public FMCA mpcConstraints(int[][][] products,int[][] L)
	{
		int[][][][] statesToVisit= new int[this.numberOfStates()][][][];
		statesToVisit[0]=products;
		return null;
	}
	
	
//	/**
//	 * similar to the corresponding method in CA class, with CATransition swapped with MSCATransition and CA swapped with MSCA  
//	 * 
//	 * @return all the reachable states 
//	 */
//	private int[][] reachableStates()
//	{
//		MSCA aut=this.clone();
//		aut = (MSCA) MSCAUtil.removeUnreachable(aut);
//		aut = (MSCA) MSCAUtil.removeDanglingTransitions(aut);
//		int[][] s = new int[this.prodStates()][];
//		s[0]=aut.getInitialCA();
//		MSCATransition[] t = aut.getTransition();
//		int pointer=1;
//		for (int i=0;i<t.length;i++)
//		{
//			int[] p = t[i].getArrival();
//			boolean found=false;
//			int j=0;
//			while((!found)&&(s[j]!=null))
//			{
//				found = Arrays.equals(p, s[j]);
//				j++;
//			}
//			if (!found)
//			{
//				s[pointer]=p;
//				pointer++;
//			}
//		}
//	    int[][] f = new int[pointer][];
//	    for (int i=0;i<pointer;i++)
//	    	f[i]=s[i];
//		return f;
//	}
	
	/**
	 * an array containing the number of  states of each principal
	 * @param states  all the states of the MSCA enumerated
	 * @return
	 */
	public static int[] numberOfPrincipalsStates(int[][] states)
	{
		int[] max = new int[states[0].length];
		for (int j=0;j<max.length;j++)
			max[j]=-1;
		for (int i=0;i<states.length;i++)
		{
			for (int j=0;j<max.length;j++)
			{
				if (max[j]<states[i][j])
					max[j]=states[i][j];
			}
		}
		for (int j=0;j<max.length;j++)
			max[j]+=1;		
		return max;
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
		int[][] s = new int[this.prodStates()][];
		s[0]=aut.getInitialCA();
		FMCATransition[] t = aut.getTransition();
		int pointer=1;
		for (int i=0;i<t.length;i++)
		{
			int[] start = t[i].getSourceP();
			int[] arr = t[i].getTargetP();
			
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
	
//	/**
//	 * 
//	 * similar to the corresponding method in CA class, with CATransition swapped with MSCATransition and CA swapped with MSCA  
//	 * @return all the final states of the CA
//	 */
//	public  int[][] allFinalStates()
//	{
////		if (rank==1)
////			return finalstates;
//
//		int[][] finalstates=getFinalStatesCA();
//		int[] states=new int[finalstates.length];
//		int comb=1;
//		int[] insert= new int[states.length];
//		for (int i=0;i<states.length;i++)
//		{
//			states[i]=finalstates[i].length;
//			comb*=states[i];
//			insert[i]=0;
//		}
//		int[][] modif = new int[comb][];
//		int[] indstates = new int[1];
//		indstates[0]= states.length-1;
//		int[] indmod = new int[1];
//		indmod[0]= 0; 
//		//CAUtil.recGen(finalstates, modif,  states, 0, states.length-1, insert);
//		MSCAUtil.recGen(finalstates, modif,  states, indmod, indstates, insert);
//		return modif;
//	}
	
	
	
	
	/**
	 * return redundant states who do not reach a final state or are unreachable
	 * not inherited from CA
	 * @return	redundant states of at
	 */
	protected int[][] getDanglingStates()
	{
		/*int pointerreachable=0;
		int pointerunreachable=0;
		int[][] reachable = new int[this.prodStates()][]; 
		int[][] unreachable = new int[this.prodStates()][];*/
		int[][] fs = this.allFinalStates();
		int[][] redundantStates = new int[this.prodStates()][];
		int[][] allStates = this.allStates();
		int redundantStatesPointer=0;
		for (int ind=0;ind<allStates.length;ind++)
		{
				//TODO check if it is possible to check reachability from initial state only once
				// for each state checks if it reaches one of the final states  and if it is reachable from the initial state
				int[] pointervisited = new int[1];
				pointervisited[0]=0;
				
				//I need to check the reachability from initial state only once!
				boolean remove=!FMCAUtil.amIReachable(allStates[ind],this,getInitialCA(),new int[this.prodStates()][],
						pointervisited,null,null,0,0);  	
				
				if (!remove) //if it is reachable from initial state
				{
					remove=true;  // at the end of the loop if remove=true none of final states is reachable
					for (int i=0;i<fs.length;i++)
					{
						pointervisited = new int[1];
						pointervisited[0]=0;
						if((FMCAUtil.amIReachable(fs[i],this,allStates[ind],new int[this.prodStates()][],pointervisited,
								null,null,0,0)&&remove))  
							remove=false;
					}
				}
				/*boolean remove=true;
				for (int i=0;i<fs.length;i++)
				{
					int[] pointervisited = new int[1];
					pointervisited[0]=0;
					// note that pointervisited is not reinitialised before checking reachability from initial state
					if((FMCAUtil.amIReachable(fs[i],this,allStates[ind],new int[this.prodStates()][],
							pointervisited,reachable,unreachable,pointerreachable,pointerunreachable)&&remove)  
						&&(FMCAUtil.amIReachable(allStates[ind],this,getInitialCA(),new int[this.prodStates()][],
								pointervisited,reachable,unreachable,pointerreachable,pointerunreachable)&&remove))  	
						remove=false;
				}*/
				if ((remove))//&&(!FMCAUtil.contains(allStates[ind],redundantStates)))//there should be no need for this
				{
					redundantStates[redundantStatesPointer]=allStates[ind];
					redundantStatesPointer++;
				}
													
		}
		//remove null space in array redundantStates
		redundantStates = FMCAUtil.removeTailsNull(redundantStates, redundantStatesPointer);
		
		return redundantStates;
	}
	
	/**
	 * this method is not inherited from CA
	 * @return	all the  must transitions request that are not matched 
	 */
	protected  FMCATransition[] getUnmatch()
	{
		FMCATransition[] tr = this.getTransition();
		int[][] fs=this.allFinalStates();
		int pointer=0;
		int[][] R=this.getDanglingStates();
		FMCATransition[] unmatch = new FMCATransition[tr.length];
		for (int i=0;i<tr.length;i++)
		{
			if ((tr[i].isRequest())
				&&((tr[i].isMust())
				&&(!FMCAUtil.contains(tr[i].getSourceP(), fs)))) // if source state is not final
			{
				boolean matched=false;
				for (int j=0;j<tr.length;j++)	
				{
					if ((tr[j].isMatch())
						&&(tr[j].isMust())
						&&(tr[j].getReceiver()==tr[i].getReceiver())	//the same principal
						&&(tr[j].getSourceP()[tr[j].getReceiver()]==tr[i].getSourceP()[tr[i].getReceiver()]) //the same source state					
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
	
	public FMCATransition[] createArrayTransition(int length)
	{
		return new FMCATransition[length];
	}
	public FMCATransition[][] createArrayTransition2(int length)
	{
		return new FMCATransition[length][];
	}
	public FMCA createNew(int rank, int[] initial, int[] states, int[][] finalstates,CATransition[] tra)
	{
		return new FMCA(rank,initial,states,finalstates,(FMCATransition[])tra);
	}
}