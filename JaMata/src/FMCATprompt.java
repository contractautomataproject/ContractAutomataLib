
import java.io.*;

import CA.CAUtil;
import FMCA.FMCAUtil;
import FSA.FSA;


/**
 * 
 * Main Class, contains the method main
 * @author Davide	Basile
 *
 */
public class FMCATprompt {
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
	        System.out.println("  ****	JaMaTa	****");
	        System.out.println("Press 3 for FSA  4 for CA and 5 for FMCA");
	        int type = Integer.parseInt(myInput.readLine());
	        if (type == 4)
	        {
	        	CAUtil.CATest();
	        	return;
	        }
	        else if (type == 5)
	        {
	        	FMCAUtil.FMCATest();
	        	return;
	        }
	        System.out.println("Insert the name of the automaton (without file extension) to load or leave empty for creating a new one");
	        String s = myInput.readLine(); 
	        //String s ="CA1";
	        if (!s.isEmpty())
	        {
	        	 if (type == 3)
	        		automa = FSA.load(s);
	        }
	        else
	        	{
		        	 if (type == 3)
		        		automa = new FSA();
	        	}
	        automa.print();
		}
		catch (Exception e){e.printStackTrace();}
	}
	
	
}
