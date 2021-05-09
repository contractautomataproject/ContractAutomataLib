package contractAutomata;

import java.io.IOException;

public interface JsonConverter {
	public MSCA importJSON(String path) throws IOException;
	public void exportJSON(MSCA aut, String path) throws Exception;
}
