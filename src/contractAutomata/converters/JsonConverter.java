package contractAutomata.converters;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONObject;

import contractAutomata.automaton.MSCA;
import contractAutomata.automaton.label.CALabel;
import contractAutomata.automaton.state.BasicState;
import contractAutomata.automaton.state.CAState;
import contractAutomata.automaton.transition.MSCATransition;

public class JsonConverter implements MSCAConverter {

	@Override
	public MSCA importMSCA(String filename) throws IOException {

		String content = Files.readAllLines(Paths.get(filename)).stream()
				.collect(Collectors.joining(" "));

		JSONObject obj = new JSONObject(content);

		JSONArray nodes = obj.getJSONArray("nodes");	

		Map<Integer, CAState> id2ct = IntStream.range(0, nodes.length())
				.mapToObj(nodes::getJSONObject)
				.collect(Collectors.toMap(n->n.getInt("id"), n-> {
					JSONArray atoms = n.getJSONArray("atoms");
					String label=n.getInt("id")+"_"+IntStream.range(0,atoms.length())
					.mapToObj(atoms::getString)
					.collect(Collectors.joining("_"));
					return new CAState(
							new ArrayList<BasicState>(Arrays.asList(new BasicState(label,n.getInt("id")==0,true))), 
							0, 0);
				}));

		JSONArray arcs = obj.getJSONArray("arcs");

		return new MSCA(IntStream.range(0, arcs.length())
				.mapToObj(arcs::getJSONObject)
				.map(n-> new MSCATransition(
						id2ct.get(n.getInt("source")),
						new CALabel(1,0,"!dummy"),
						id2ct.get(n.getInt("target")),
						MSCATransition.Modality.PERMITTED))
				.collect(Collectors.toSet()));
	}


	@Override
	public void exportMSCA(String filename, MSCA aut) throws IOException {
		// TODO Auto-generated method stub
		
	}



}
;