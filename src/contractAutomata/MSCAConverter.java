package contractAutomata;

public interface MSCAConverter {
	public MSCA importMSCA(String filename) throws Exception;
	public void exportMSCA(String filename, MSCA aut) throws Exception;
}
