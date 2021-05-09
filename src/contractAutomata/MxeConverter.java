package contractAutomata;

import java.io.File;

public interface MxeConverter {
	public MSCA importMxe(String path) throws Exception;
	public File exportMxe(MSCA aut, String path) throws Exception;
}
