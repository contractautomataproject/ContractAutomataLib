package io.github.davidebasile.contractautomata.converters;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.CMLabel;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;

/**
 * Import/Export textual DATA format
 * 
 * @author Davide Basile
 *
 */
public class DataConverter implements MSCAConverter{

	/**
	 * Export the textual description of the automaton in a .data file
	 * 
	 * @throws FileNotFoundException in case filename is not found
	 */
	@Override
	public void exportMSCA(String filename, ModalAutomaton<CALabel> aut) throws FileNotFoundException {
		if (filename=="")
			throw new IllegalArgumentException("Empty file name");

		String suffix=(filename.endsWith(".data"))?"":".data";
		try (PrintWriter pr = new PrintWriter(filename+suffix))
		{
			pr.print(aut.toString());
		}
	}

	/**
	 * Import an ModalAutomaton<CALabel> described in a text file .data, 
	 * 
	 * @param  filename the name of the file
	 * @return the object ModalAutomaton<CALabel> described in the textfile
	 * @throws IOException problems in reading the file
	 */
	@Override
	public ModalAutomaton<CALabel> importMSCA(String filename) throws IOException {
		//TODO long method
		// Open the file
		if (!filename.endsWith(".data"))
			throw new IllegalArgumentException("Not a .data format");

		Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> tr;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename))))
		{
			int rank=0;
			String[] initial = new String[1];
			String[][] fin = new String[1][];
			tr = new HashSet<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>();
			Set<CAState> states = new HashSet<CAState>();
			Map<Integer,Set<BasicState>> mapBasicStates = new HashMap<>();

			String strLine;
			while ((strLine = br.readLine()) != null)   
			{
				if (strLine.length()>0)
				{
					String subStrLine=strLine.substring(0,1);
					switch(subStrLine)
					{
					case "R":  //Rank Line
					{
						rank = Integer.parseInt(strLine.substring(6));
						break;
					}
					case "I": //Initial state
					{
						initial=Arrays.stream(strLine.split("[\\[\\],]"))
								.filter(s->!s.contains("Initial state"))
								.map(String::trim)
								.toArray(String[]::new);
						if (initial.length!=rank)
							throw new IOException("Initial state with different rank");
						break;
					}
					case "F": //Final state
					{
						fin=Arrays.stream(strLine.split("]"))
								.map(sar->Arrays.stream(sar.split("[,|\\[]"))
										.filter(s->!s.contains("Final states"))
										.map(String::trim)
										.filter(s->!s.isEmpty())
										.toArray(String[]::new))
								.toArray(String[][]::new);
						if (fin.length!=rank)
							throw new IOException("Final states with different rank");

						break;
					}
					case "(": //a may transition
					{
						tr.add(loadTransition(strLine,rank, ModalTransition.Modality.PERMITTED, states,mapBasicStates,initial,fin));
						break;
					}
					case "!": //a must transition
					{
						String stype= strLine.substring(1,2);
						ModalTransition.Modality type=null;
						if ("U".equals(stype))
							type=ModalTransition.Modality.URGENT;
						else if ("L".equals(stype))
							type=ModalTransition.Modality.LAZY;
						
						tr.add(loadTransition(strLine,rank,type,states,mapBasicStates,initial,fin));
						break;
					}
					}
				}
			}

		}
		
		return new ModalAutomaton<CALabel>(tr);
	}

	private static ModalTransition<List<BasicState>,List<String>,CAState,CALabel> loadTransition(String str, int rank, ModalTransition.Modality type, Set<CAState> states,Map<Integer,Set<BasicState>> mapBasicStates,String[] initial, String[][] fin) throws IOException
	{
		String regex = "\\(\\["+"(.*)"+"\\],\\["+"(.*)"+"\\],\\["+"(.*)"+"\\]\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);

		matcher.find();
		String[][] tr=IntStream.range(1,4)
				.mapToObj(i->Arrays.stream(matcher.group(i).split(","))
						.map(String::trim)
						.toArray(String[]::new))
				.toArray(String[][]::new);

		if (tr[0].length!=rank || tr[1].length!=rank || tr[2].length!=rank)
			throw new IOException("Ill-formed transitions, different ranks");

		CAState source = createOrLoadState(states,mapBasicStates,tr[0],initial, fin);//source
		CAState target = createOrLoadState(states,mapBasicStates,tr[2],initial, fin);//target
		CALabel label;
		if (tr[1].length==1 && tr[1][0].contains(CMLabel.action_separator))
			label=new CMLabel(tr[1][0]);
		else
			label = new CALabel(Arrays.asList(tr[1]));

		return new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(source,label,target,type); 
	}

	private static CAState createOrLoadState(Set<CAState> states,Map<Integer,Set<BasicState>> mapBasicStates, String[] state,String[] initial, String[][] fin) throws IOException {

		return states.stream()
				.filter(cs->IntStream.range(0, cs.getState().size())
						.allMatch(i->cs.getState().get(i).getState().equals(state[i]))) 
				.findAny()
				.orElseGet(()->{
					CAState temp= new CAState(
							IntStream.range(0, state.length) //creating the list of basic states using mapBasicStates
							.mapToObj(i->{
								Set<BasicState> l = mapBasicStates.get(i);
								if (l==null || l.stream().allMatch(bs->!bs.getState().equals(state[i])))
								{
									BasicState bs=new BasicState(state[i]+"",
											state[i].equals(initial[i]),
											Arrays.stream(fin[i]).anyMatch(id->id.equals(state[i])));
									if (l==null)
										mapBasicStates.put(i, new HashSet<BasicState>(Arrays.asList(bs)));
									else
										l.add(bs);
									return (BasicState) bs;
								} else
									return (BasicState) l.stream()
											.filter(bs->bs.getState().equals(state[i]))
											.findFirst()
											.orElseThrow(RuntimeException::new);
							}).collect(Collectors.toList())
							//,0,0
							); 							
					states.add(temp); return temp;});
	}
}