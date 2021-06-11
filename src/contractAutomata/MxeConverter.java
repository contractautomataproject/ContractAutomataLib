package contractAutomata;

import java.io.File;

public interface MxeConverter {
	public MSCA importMxe(String path) throws Exception;
	public File exportMxe(String path, MSCA aut) throws Exception;
}
