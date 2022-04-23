package io.github.contractautomata.catlib.family.converters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
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

import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.Feature;
import io.github.contractautomata.catlib.family.Product;

/**
 * Class for importing and exporting DIMACS CNF models as families of products. <br>
 * The DIMACS CNF format is a textual representation of a formula in conjunctive normal form.<br>
 * It is the standard format for SAT solvers. <br>
 *
 * @author Davide Basile
 *
 */
public class DimacsFamilyConverter implements FamilyConverter {
	private final Function<IProblem,int[]> gen;

	/**
	 * Constructor for this converter.
	 *
	 * @param allModels if this flag is true, all the models of the formula are generated, otherwise, only prime implicants.
	 */
	public DimacsFamilyConverter(boolean allModels) {
		if (allModels)
			gen = IProblem::model;
		else
			gen = IProblem::primeImplicant; //https://en.wikipedia.org/wiki/Implicant#Prime_implicant
	}

	/**
	 * Overrides the FamilyConverter method.
	 *
	 * @param filename  the name of the file to import.
	 * @return  a set of products generated from the DIMACS filename.
	 * @throws IOException
	 * @throws ParseFormatException
	 * @throws ContradictionException
	 * @throws TimeoutException
	 */
	@Override
	public Set<Product> importProducts(String filename) throws IOException, ParseFormatException, ContradictionException, TimeoutException {
		//http://www.sat4j.org/r15/doc/
		//https://sat4j.gitbooks.io/case-studies/content/using-sat4j-as-a-java-library.html
		ISolver solver = SolverFactory.newDefault();
		ModelIterator mi = new ModelIterator(solver);
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(mi);
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintWriter out = new PrintWriter(new OutputStreamWriter(baos,StandardCharsets.UTF_8),true))
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
				return Collections.emptySet();

			return Arrays.stream(baos.toString(StandardCharsets.UTF_8).split(System.lineSeparator()))
					.map(s->Arrays.stream(s.split(" "))
							.mapToInt(Integer::parseInt)
							.boxed()
							.filter(i->i!=0)
							.collect(Collectors.partitioningBy(i-> i>-1, //i>=0 causes a mutant to survive
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
				.map(ar-> new AbstractMap.SimpleEntry<>(Integer.parseInt(ar[1].replace("$", "")), ar[2]))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));		
	}

	/**
	 * Operation not supported.
	 *
	 * @param filename the name of the file to which the family of products is stored
	 * @param fam the family to be exported
	 */
	@Override
	public void exportFamily(String filename, Family fam) {
		throw new UnsupportedOperationException();
	}
}