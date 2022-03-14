package io.github.contractautomataproject.catlib.family.converters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

import io.github.contractautomataproject.catlib.family.Family;
import io.github.contractautomataproject.catlib.family.Feature;
import io.github.contractautomataproject.catlib.family.Product;

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
	public Set<Product> importProducts(String filename) throws IOException, ParseFormatException, ContradictionException, TimeoutException {
		//http://www.sat4j.org/r15/doc/
		//https://sat4j.gitbooks.io/case-studies/content/using-sat4j-as-a-java-library.html
		ISolver solver = SolverFactory.newDefault();
		ModelIterator mi = new ModelIterator(solver);
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(mi);
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintWriter out = new PrintWriter(new OutputStreamWriter(baos,StandardCharsets.UTF_8),true);)
		{

			IProblem problem =reader.parseInstance(filename);// CNF filename

			Map<Integer,String> i2s = readFeatureStrings(filename);

			boolean unsat=true;
			while (problem.isSatisfiable()) { // do something with each model
				unsat = false;
				reader.decode(gen.apply(problem),out);
				out.println();
			}
			if (unsat) // do something for unsat case
				return new HashSet<>();

			return Arrays.stream(baos.toString("UTF-8").split(System.lineSeparator()))
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
	}


	private Map<Integer,String> readFeatureStrings(String filename) throws IOException
	{
		return Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8)
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