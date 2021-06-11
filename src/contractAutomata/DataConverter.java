package contractAutomata;

import java.io.IOException;

public interface DataConverter {
	public MSCA importDATA(String filename) throws IOException;
	public void exportDATA(String filename, MSCA aut) throws IOException;
}
