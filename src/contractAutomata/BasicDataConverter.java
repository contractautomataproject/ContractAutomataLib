package contractAutomata;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BasicDataConverter implements DataConverter{

	/**
	 * print the description of the CA to a file
	 * 
	 * @throws FileNotFoundException 
	 */
	@Override
	public void exportDATA(String filename, MSCA aut) throws FileNotFoundException {
		if (filename=="")
			throw new IllegalArgumentException("Empty file name");

		String suffix=(filename.endsWith(".data"))?"":".data";
		try (PrintWriter pr = new PrintWriter(filename+suffix))
		{
			pr.print(aut.toString());
		}
	}

	/**
	 * load a MSCA described in a text file,  
	 * it also loads the must transitions but it does not load the states
	 * 
	 * @param  filename the name of the file
	 * @return	the MSCA described in the textfile
	 * @throws IOException 
	 */
	@Override
	public MSCA importDATA(String filename) throws IOException {
		//TODO long method
		// Open the file
		if (!filename.endsWith(".data"))
			throw new IllegalArgumentException("Not a .data format");

		Set<MSCATransition> tr;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename))))
		{
			int rank=0;
			String[] initial = new String[1];
			String[][] fin = new String[1][];
			tr = new HashSet<MSCATransition>();
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
						tr.add(loadTransition(strLine,rank, MSCATransition.Modality.PERMITTED, states,mapBasicStates,initial,fin));
						break;
					}
					case "!": //a must transition
					{
						String stype= strLine.substring(1,2);
						MSCATransition.Modality type=null;
						if ("U".equals(stype))
							type=MSCATransition.Modality.URGENT;
						else if ("L".equals(stype))
							type=MSCATransition.Modality.LAZY;
						
						tr.add(loadTransition(strLine,rank,type,states,mapBasicStates,initial,fin));
						break;
					}
					}
				}
			}

		}
		
		return new MSCA(tr);
	}



	private static MSCATransition loadTransition(String str, int rank, MSCATransition.Modality type, Set<CAState> states,Map<Integer,Set<BasicState>> mapBasicStates,String[] initial, String[][] fin) throws IOException
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
		CALabel label = new CALabel(Arrays.asList(tr[1]));

		return new MSCATransition(source,label,target,type); 
	}

	private static CAState createOrLoadState(Set<CAState> states,Map<Integer,Set<BasicState>> mapBasicStates, String[] state,String[] initial, String[][] fin) throws IOException {

		return states.stream()
				.filter(cs->IntStream.range(0, cs.getState().size())
						.allMatch(i->cs.getState().get(i).getLabel().equals(state[i]))) 
				.findAny()
				.orElseGet(()->{
					CAState temp= new CAState(
							IntStream.range(0, state.length) //creating the list of basic states using mapBasicStates
							.mapToObj(i->{
								Set<BasicState> l = mapBasicStates.get(i);
								if (l==null || l.stream().allMatch(bs->!bs.getLabel().equals(state[i])))
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
											.filter(bs->bs.getLabel().equals(state[i]))
											.findFirst()
											.orElseThrow(RuntimeException::new);
							}).collect(Collectors.toList())
							,0,0); 							
					states.add(temp); return temp;});
	}
}