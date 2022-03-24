package io.github.contractautomataproject.catlib.converters;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Import/Export textual DATA format
 *
 * @author Davide Basile
 *
 */
public class AutDataConverter<L extends Label<Action>>  implements AutConverter<Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,L>>,Automaton<?,?,?,?>> {
	private final Function<List<Action>,L> createLabel;
	private static final String SUFFIX = ".data";
	private static final String EMPTYMSG = "Empty file name";

	public AutDataConverter(Function<List<Action>, L> createLabel) {
		super();
		this.createLabel = createLabel;
	}


	public Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,L>> importMSCA(String filename) throws IOException {
		if (!filename.endsWith(SUFFIX))
			throw new IllegalArgumentException("Not a .data format");
		Path path = FileSystems.getDefault().getPath(filename);

		String safeFileName = path.toString();

		Set<ModalTransition<String,Action,State<String>,L>> tr;

		//https://github.com/find-sec-bugs/find-sec-bugs/issues/241
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(safeFileName), StandardCharsets.UTF_8)))
		{
			int rank=0;
			String[] initial = new String[1];
			String[][] fin = new String[1][];
			tr = new HashSet<>();
			Set<State<String>> states = new HashSet<>();
			Map<Integer,Set<BasicState<String>>> mapBasicStates = new HashMap<>();

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
							rank=Integer.parseInt(strLine.substring(6));
							break;
						}
						case "I": //Initial state
						{
							initial=readInitialState(strLine,rank);
							break;
						}
						case "F": //Final state
						{
							fin=readFinalState(strLine,rank);
							break;
						}
						case "(": //a permitted transition
						{
							tr.add(loadTransition(strLine,rank, ModalTransition.Modality.PERMITTED, states,mapBasicStates,initial,fin));
							break;
						}
						case ModalTransition.NECESSARY: //a necessary transition
						{
							tr.add(loadTransition(strLine,rank,readModality(strLine),states,mapBasicStates,initial,fin));
							break;
						}
						default :
					}
				}
			}

		}

		return new Automaton<>(tr);
	}

	private String[] readInitialState(String strLine, int rank){
		String[] initial=Arrays.stream(strLine.split("[\\[\\],]"))
				.filter(s->!s.contains("Initial state"))
				.map(String::trim)
				.toArray(String[]::new);
		if (initial.length!=rank)
			throw new IllegalArgumentException("Initial state with different rank");
		return initial;
	}

	private String[][] readFinalState(String strLine, int rank){
		String[][] fin=Arrays.stream(strLine.split("]"))
				.map(sar->Arrays.stream(sar.split("[,|\\[]"))
						.filter(s->!s.contains("Final states"))
						.map(String::trim)
						.filter(s->!s.isEmpty())
						.toArray(String[]::new))
				.toArray(String[][]::new);
		if (fin.length!=rank)
			throw new IllegalArgumentException("Final states with different rank");
		return fin;
	}

	private ModalTransition.Modality readModality(String strLine) {
		String stype= strLine.substring(1,2);
		if (ModalTransition.URGENT.equals(stype))
			return ModalTransition.Modality.URGENT;
		else if (ModalTransition.LAZY.equals(stype))
			return ModalTransition.Modality.LAZY;
		else
			throw new IllegalArgumentException("Invalid modality");
	}

	private ModalTransition<String,Action,State<String>,L> loadTransition(String str, int rank, ModalTransition.Modality type, Set<State<String>> states,Map<Integer,Set<BasicState<String>>> mapBasicStates,String[] initial, String[][] fin) throws IOException
	{
		String regex = "\\(\\["+"(.+)"+"\\],\\["+"(.+)"+"\\],\\["+"(.+)"+"\\]\\)";
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

		State<String> source = createOrLoadState(states,mapBasicStates,tr[0],initial, fin);//source
		State<String> target = createOrLoadState(states,mapBasicStates,tr[2],initial, fin);//target
		return new ModalTransition<>(source,createLabel(tr),target,type);
	}

	private L createLabel(String[][] tr) {
		try { return createLabel.apply(Arrays.stream(tr[1]).map(this::parseAction).collect(Collectors.toList()));}
		catch (IllegalArgumentException e) {
			//parsing failed
			return createLabel.apply(Arrays.stream(tr[1])
					.map(Action::new)
					.collect(Collectors.toList()));
		}

	}

	private State<String> createOrLoadState(Set<State<String>> states,Map<Integer,Set<BasicState<String>>> mapBasicStates, String[] state,String[] initial, String[][] fin)  {

		return states.stream()
				.filter(cs->IntStream.range(0, cs.getState().size())
						.allMatch(i->cs.getState().get(i).getState().equals(state[i])))
				.findAny()
				.orElseGet(()->{
					State<String> temp= new State<>(
							IntStream.range(0, state.length) //creating the list of basic states using mapBasicStates
									.mapToObj(i->{
										Set<BasicState<String>> l = mapBasicStates.get(i);
										if (l==null || l.stream().noneMatch(bs-> bs.getState().equals(state[i])))
										{
											BasicState<String> bs=new BasicState<>(state[i]+"",
													state[i].equals(initial[i]),
													Arrays.stream(fin[i]).anyMatch(id->id.equals(state[i])));
											if (l==null)
												mapBasicStates.put(i, new HashSet<>(List.of(bs)));
											else
												l.add(bs);
											return bs;
										} else
											return l.stream()
													.filter(bs->bs.getState().equals(state[i]))
													.findFirst()
													.orElseThrow(RuntimeException::new);
									}).collect(Collectors.toList()));
					states.add(temp); return temp;});
	}

	/**
	 * Export the textual description of the automaton in a .data file
	 *
	 * @throws FileNotFoundException in case filename is not found
	 */
	@Override
	public  void exportMSCA(String filename, Automaton<?,?,?,?> aut) throws IOException {
		if (filename.isEmpty())
			throw new IllegalArgumentException(EMPTYMSG);

		String ext=(filename.endsWith(SUFFIX))?"":SUFFIX;
		Path path = FileSystems.getDefault().getPath(filename+ext);
		String safeFileName = path.toString();

		try (PrintWriter pr = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(safeFileName)), StandardCharsets.UTF_8)))
		{
			pr.print(aut.toString());
		}
	}
}