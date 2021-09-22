package family.converters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.Reader;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.tools.ModelIterator;

import family.Family;
import family.Feature;
import family.Product;

/**
 * Class for importing and exporting DIMAC models as families of products 
 * @author Davide Basile
 *
 */
public class DimacFamilyConverter implements FamilyConverter {
	private Function<IProblem,int[]> gen;
	
	public DimacFamilyConverter(boolean allModels) {
		if (allModels)
			gen = IProblem::model;
		else
			gen = IProblem::primeImplicant; //https://en.wikipedia.org/wiki/Implicant#Prime_implicant
	}

	@Override
	public Set<Product> importProducts(String filename) throws Exception {
		//http://www.sat4j.org/r15/doc/
		//https://sat4j.gitbooks.io/case-studies/content/using-sat4j-as-a-java-library.html
		ISolver solver = SolverFactory.newDefault();
		ModelIterator mi = new ModelIterator(solver);
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(mi);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(baos,true);

		IProblem problem =
				reader.parseInstance(filename);// CNF filename
		//IProblem sd = new Minimal4CardinalityModel(mi);

		Map<Integer,String> i2s = readFeatureStrings(filename);

		boolean unsat=true;
		while (problem.isSatisfiable()) { // do something with each model
			unsat = false;
			reader.decode(gen.apply(problem),out);
			out.println();
//			System.out.println(baos.toString());
		}
		if (unsat) // do something for unsat case
			return new HashSet<Product>();

		return Arrays.stream(baos.toString().split(System.lineSeparator()))
				.map(s->Arrays.stream(s.split(" "))
						.mapToInt(Integer::parseInt)
						.boxed()
						.filter(i->i!=0)
						.collect(Collectors.partitioningBy(i->i>=0, 
						Collectors.mapping(i->new Feature(i2s.get(Math.abs(i))), 
								Collectors.toSet()))))
				.map(e->new Product(e.get(true),e.get(false)))
				.collect(Collectors.toSet());		
	}


	private Map<Integer,String> readFeatureStrings(String filename) throws IOException
	{
		return Files.readAllLines(Paths.get(filename), Charset.forName("ISO-8859-1"))
				.stream()
				.filter(s->s.startsWith("c")) //comment
				.map(s->s.split(" "))
				.map(ar->new AbstractMap.SimpleEntry<Integer, String>(Integer.parseInt(ar[1].replace("$","")),ar[2]))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));		
	}

	@Override
	public void exportFamily(String filename, Family fam) throws IOException {
		throw new UnsupportedOperationException();
	}
}






////	System.out.println(models.toString());
//
////Set<Integer> concfeat = getConcreteFeatures(models);
//
////System.out.println(concfeat.toString());
//
////Set<Integer> absfeat=IntStream.iterate(1, i->i+1)
////		.limit(problem.nVars())
////		.boxed()
////		.collect(Collectors.toCollection(TreeSet::new));
////absfeat.removeAll(concfeat);
//
////	System.out.println(absfeat.toString());
//	
////
////Set<Integer> ignorefeature = getEquivalentToIgnore(concfeat,i2s,models);
////
//// Boolean[] toConsider = IntStream.range(0, problem.nVars())
////.mapToObj(i->concfeat.contains(i)&&!ignorefeature.contains(i))
////.toArray(Boolean[]::new);
//// 
////System.out.println(i2s.toString());
//private void removeLiterals(Set<List<Boolean>> models, Set<Integer> toRemove)
//{
//	models.stream()
//	.forEach(li->toRemove.stream()
//				.forEach(i->li.remove(i.intValue()-1))); this shift literals!
//
//}
////always positive
//private Set<Integer> getConcreteFeatures(Set<List<Boolean>> models)
//{
//	return models.parallelStream()
//	.flatMap(l->IntStream.range(0, l.size())
//			.filter(i->!l.get(i))//abstract features are enabled by default,
//			  //thus concrete features appear as false in some clause
//			.map(i->i+1) //features start from 1
//			.boxed())
//	.distinct()
//	.map(Math::abs)
//	.collect(Collectors.toSet());
//}
//
//private Map<Integer,String> readFeatureStrings(String filename,Set<Integer> concfeat) throws IOException
//{
//	return Files.readAllLines(Paths.get(filename), Charset.forName("ISO-8859-1"))
//	.stream()
//	.filter(s->s.startsWith("c")) //comment
//	.map(s->s.split(" "))
//	.map(ar->new AbstractMap.SimpleEntry<Integer, String>(Integer.parseInt(ar[1]),ar[2]))
//	.filter(e-> concfeat.contains(e.getKey()))//only concrete features
//	.collect(Collectors.toMap(Entry::getKey, Entry::getValue));		
//}
//
//private Set<Integer> getEquivalentToIgnore(Set<Integer> concfeat,Map<Integer,String> i2s,Set<List<Boolean>> models) throws IOException
//{
//	//i1 -> {i | \forall l in models. l.get(i)==l.get(i1) && i!=i1}
//
//	Map<Integer,Set<Integer>> map_eq=
//			concfeat.stream()
//			.map(i1->new AbstractMap.SimpleEntry<Integer,Set<Integer>>(i1,concfeat.stream()
//					.filter(i->i!=i1)
//					.filter(i->models.parallelStream()
//							.allMatch(l->l.get(i-1)==l.get(i1-1)))
//					.collect(Collectors.toCollection(TreeSet::new))))
//			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
//		
//	Set<Integer> equ_key = map_eq.entrySet().stream()
//	.filter(e->e.getValue().stream()
//			.allMatch(i->i2s.get(i).contains(i2s.get(e.getKey()))))
//	.map(Entry::getKey)
//	.collect(Collectors.toCollection(TreeSet::new));
//	
//	Set<Integer> ignoreEquivalent =  map_eq.entrySet().stream()
//						.filter(e->equ_key.contains(e.getKey()))
//						.map(Entry::getValue)
//						.flatMap(Set::stream)
//						.collect(Collectors.toCollection(TreeSet::new));
//	
//	if (map_eq.entrySet().stream()
//	.filter(e->!e.getValue().isEmpty())
//	.map(Entry::getKey)
//	.anyMatch(i->!equ_key.contains(i)&&!ignoreEquivalent.contains(i)))
//		throw new IOException("Found malformed equivalent feature");
//	
//	return ignoreEquivalent;
//}
