package family.converters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.Reader;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.tools.ModelIterator;

import family.Family;
import family.Product;

public class DimacFamilyConverter implements FamilyConverter {

	@Override
	public Family importFamily(String filename) throws Exception {
		
		ISolver solver = SolverFactory.newDefault();
        ModelIterator mi = new ModelIterator(solver);
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(mi);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(baos,true);
		
		// CNF filename is given on the command line 
		IProblem problem = reader.parseInstance(filename);
		boolean unsat=true;
		while (problem.isSatisfiable()) {
			unsat = false;
			// do something with each model
			reader.decode(problem.model(),out);
			out.println();
		}
		if (unsat)
		{
			// do something for unsat case
			System.out.println("Unsatisfiable !");
			return new Family(new HashSet<Product>());
		}
		
	//	List<String> models_lines = Arrays.asList(baos.toString().split(System.lineSeparator()));
		
		Set<List<Boolean>> models = Arrays.stream(baos.toString().split(System.lineSeparator()))
		.map(s->Arrays.stream(s.split(" "))
				.mapToInt(Integer::parseInt)
				.mapToObj(i->(i>0))//true literals are positive
				.collect(Collectors.toList()))
		.collect(Collectors.toSet());
		
		System.out.println(models.toString());
		
		Set<Integer> concfeat = getConcreteFeatures(models);
		
//		System.out.println(concfeat.toString());
		
		Set<Integer> absfeat=IntStream.iterate(1, i->i+1)
				.limit(problem.nVars())
				.boxed()
				.collect(Collectors.toSet());
//		absfeat.removeAll(concfeat);
		

		System.out.println(absfeat.toString());
		
//		removeLiterals(models,absfeat);
		
		//i1 -> {i | \forall l in models. l.get(i)==l.get(i1) && i!=i1}

//		Map<Integer,Set<Integer>> map_eq=
				concfeat.stream()
				.map(i1->new AbstractMap.SimpleEntry<Integer,Set<Integer>>(i1,concfeat.stream()
						.filter(i->i!=i1)
						.filter(i->models.stream()
								.allMatch(l->l.get(i-1)==l.get(i1-1)))
						.collect(Collectors.toSet())))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
								
					
				
		//Map<Integer,String> map = readConcreteFeatures(filename,concfeat);
		
		return null;
	}

	//always positive
	private Set<Integer> getConcreteFeatures(Set<List<Boolean>> models)
	{
		return models.stream()
		.flatMap(l->IntStream.range(0, l.size())
				.filter(i->!l.get(i))//abstract features are enabled by default,
				  //thus concrete features appear as false in some clause
				.map(i->i+1) //features start from 1
				.boxed())
		.distinct()
		.map(Math::abs)
		.collect(Collectors.toSet());
	}
	
//	private void removeLiterals(Set<List<Boolean>> models, Set<Integer> toRemove)
//	{
//		models.stream()
//		.forEach(li->toRemove.stream()
//					.forEach(i->li.remove(i.intValue()-1))); this shift literals!
//
//	}
	
//	private Map<Integer,String> readConcreteFeatures(String filename,int[] concfeat) throws IOException
//	{
//		return Files.readAllLines(Paths.get(filename), Charset.forName("ISO-8859-1"))
//		.stream()
//		.filter(s->s.startsWith("c")) //comment
//		.map(s->s.split(" "))
//		.map(ar->new AbstractMap.SimpleEntry<Integer, String>(Integer.parseInt(ar[1]),ar[2]))
//		.filter(e->Arrays.binarySearch(concfeat, e.getKey())>=0)//only concrete features
//		.collect(Collectors.toMap(Entry::getKey, Entry::getValue));		
//	}
	
	@Override
	public void exportFamily(String filename, Family fam) throws IOException {
		// TODO Auto-generated method stub

	}
	

}
