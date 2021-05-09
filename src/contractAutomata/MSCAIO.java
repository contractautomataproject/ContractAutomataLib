package contractAutomata;

import java.io.File;
import java.io.IOException;

/**
 * Input/Output Proxy
 * 
 * @author Davide Basile
 *
 */
public class MSCAIO {
	
	private static DataConverter  dc = new BasicDataConverter();
	private static MxeConverter  mc = new BasicMxeConverter();

	public static void printToFile(String filename, MSCA aut) throws IOException
	{
		dc.exportDATA(aut, filename);	
	}

	public static MSCA load(String filename) throws IOException
	{
		return dc.importDATA(filename);
	}

	public static MSCA parseXMLintoMSCA(String filename) throws Exception	
	{
		return mc.importMxe(filename);
	}

	public static File convertMSCAintoXML(String filename, MSCA aut) throws Exception
	{
		return mc.exportMxe(aut, filename);
	}
}
