package contractAutomata;

import java.io.File;

public interface MSCAConverterR {
	public MSCA importMSCA(String path) throws Exception;
	public File exportMSCA(String path, MSCA aut) throws Exception;
}
