package io.github.davidebasile.contractautomata.converters;

import java.io.IOException;
import java.util.Arrays;

import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.CMLabel;

/**
 * Import/Export textual DATA format
 * 
 * @author Davide Basile
 *
 */
public class MSCADataConverter extends AutDataConverter{


	/**
	 * Import an ModalAutomaton<CALabel> described in a text file .data, 
	 * 
	 * @param  filename the name of the file
	 * @return the object ModalAutomaton<CALabel> described in the textfile
	 * @throws IOException problems in reading the file
	 */
	@Override
	public ModalAutomaton<CALabel> importMSCA(String filename) throws IOException {
		return super.importMSCA(filename).convertLabelsToCALabels();
	}

	@Override
	public CALabel createLabel(String[][] tr) {
		if (tr[1].length==1 && tr[1][0].contains(CMLabel.action_separator))
			return new CMLabel(tr[1][0]);
		else 
			return new CALabel(Arrays.asList(tr[1]));

	}
}