
import java.io.*;

import CA.CA;
import FMA.FMA;
import FSA.FSA;
import PFSA.PFSA;

/**
 * 
 * Main Class, contains the method main
 * @author Davide	Basile
 *
 */
public class Main {
	/**
	 * Select the kind of automaton and choose to create a new automaton or loading a stored automaton, then run 
	 * the simulator
	 * @param args
	 */
	public static void main (String [] args){
		FSA automa = null;
		try{
			InputStreamReader reader = new InputStreamReader (System.in);
	        BufferedReader myInput = new BufferedReader (reader);
	        System.out.println("Press 1 for FMA and 2 for PFSA  3 for FSA and 4 for CA ");
	        int type = Integer.parseInt(myInput.readLine());
	        //int type= 4;
	        System.out.println("Insert the name of the automaton (without file extension) to load or leave empty for creating a new one");
	        String s = myInput.readLine(); 
	        //String s ="CA1";
	        if (!s.isEmpty())
	        {
	        	if (type == 1)
	        		automa =  (FMA) FSA.load(s);
	        	else if (type == 2)
	        		automa = (PFSA) FSA.load(s);
	        	else if (type == 3)
	        		automa = FSA.load(s);
	        	else if (type == 4)
	        		automa = (CA) FSA.load(s);
	        }
	        else
	        	{
		        	if (type == 1)
		        		automa =  new FMA();
		        	else if (type == 2)
		        		automa = new PFSA();
		        	else if (type == 3)
		        		automa = new FSA();
		        	else if (type == 4)
		        		automa = new CA();
	        	}
	        automa.print();
	        if (type!=4)
	        	automa.run();
	        else
	        {
	        	CATest((CA)automa);
	        }
		}
		catch (Exception e){e.printStackTrace();}
	}
	
	private static void CATest(CA automa)
	{

		try{
			System.out.println("1 : product automata\n2 : projection \n3 : aproduct \n4: exit ");
			String s="";
			InputStreamReader reader = new InputStreamReader (System.in);
			BufferedReader myInput = new BufferedReader (reader);
			s = myInput.readLine();
			while(!s.equals("4"))
			{
				CA[] aut=load(automa);
				if(s.equals("1"))
				{
					System.out.println("Computing the product automaton ... ");
					CA prod = CA.product(aut);
					prod.print();
			        FSA.write(prod);
				}
				else if(s.equals("2"))
				{
					System.out.println("Computing the projection of the last CA loaded, insert the index of the principal:");
					s=myInput.readLine();
					int ind = Integer.parseInt(s);
					CA projected = aut[aut.length-1].proj(ind);
					projected.print();
					FSA.write(projected);
				}
				else if(s.equals("3"))
				{
					System.out.println("Computing the associative product automaton ... ");
					CA prod = CA.aproduct(aut);
					prod.print();
			        FSA.write(prod);
				}
				System.out.println("1 : product automata\n2 : projection \n3 : exit ");
				s = myInput.readLine();

			}

		}catch(Exception e){e.printStackTrace();}
	} 
	private static CA[] load(CA automa)
	{
		try
		{
			CA[] a = new CA[10];
			int i=0;
			a[i]=automa;
			i++;
			String s="";
			InputStreamReader reader = new InputStreamReader (System.in);
			BufferedReader myInput = new BufferedReader (reader);
			while (!s.equals("no")&&i<10)
			{
				System.out.println("Do you want to create/load other contract automata? (yes/no)");
				s = myInput.readLine();
				//s = "yes";
				if(!s.equals("no"))
				{
					System.out.println("Insert the name of the automaton (without file extension) to load or leave empty for create a new one");
					s = myInput.readLine();
					//s = "CA1";
			        if (!s.isEmpty())
			        {
			        	automa = (CA) FSA.load(s);
			        }
			        else
			        	{
				        automa = new CA();
			        	}
			        automa.print();
			        a[i] = automa;
			        //s="no";
			        i++;
				}
			}
			CA[] aut;
			if (i<10)
			{
				aut=new CA[i];
				for (int ind=0;ind<i;ind++)
					aut[ind]=a[ind];
			}
			else
				aut=a;
			return aut;
		}catch(Exception e){e.printStackTrace();return null;}
	}
}
