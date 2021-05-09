package contractAutomata;

import java.io.IOException;

public interface DataConverter {
	public MSCA importDATA(String filename) throws IOException;
	public void exportDATA(MSCA aut, String filename) throws IOException;
}
