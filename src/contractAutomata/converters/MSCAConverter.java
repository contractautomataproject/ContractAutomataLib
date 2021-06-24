package contractAutomata.converters;

import contractAutomata.automaton.MSCA;

public interface MSCAConverter {
	public MSCA importMSCA(String filename) throws Exception;
	public void exportMSCA(String filename, MSCA aut) throws Exception;
}
