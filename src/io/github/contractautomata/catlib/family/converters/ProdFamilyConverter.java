package io.github.contractautomata.catlib.family.converters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;

import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.Feature;
import io.github.contractautomata.catlib.family.Product;

/**
 * Class implementing import/export from the <tt>.prod</tt> textual format.
 * 
 * @author Davide Basile
 *
 */
public class ProdFamilyConverter implements FamilyConverter {

	private static final String EMPTYMSG = "Empty file name";

	/**
	 * Overrides the method of FamilyConverter
	 *
	 * @param filename  the name of the file to import
	 * @return  a set of products loaded from filename, representing a family of products
	 * @throws IOException
	 */
	@Override
	public Set<Product> importProducts(String filename) throws IOException {
		Path path = FileSystems.getDefault().getPath(filename);
		File f = new File(path.toString());
		
		List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);

		Pattern pattern = Pattern.compile("p[0-9]+: R=\\{(.*)\\} F=\\{(.*)\\}");

		return lines.parallelStream()
				.map(pattern::matcher)
				.filter(Matcher::find)
				.map(matcher ->new Product(Arrays.stream(matcher.group(1).split(","))
						.map(String::trim)
						.filter(s->!s.isEmpty())
						.map(Feature::new)
						.collect(Collectors.toSet()),
						Arrays.stream(matcher.group(2).split(","))
						.map(String::trim)
						.filter(s->!s.isEmpty())
						.map(Feature::new)
						.collect(Collectors.toSet())))
				.collect(Collectors.toSet());
	}

	/**
	 * Overrides the method of FamilyConverter
	 *
	 * @param filename the name of the file to which the family of products is stored
	 * @param fam the family to be exported
	 * @throws IOException
	 */
	@Override
	public void exportFamily(String filename, Family fam) throws IOException{
		if (filename==null || filename.isEmpty())
			throw new IllegalArgumentException(EMPTYMSG);

		String suffix = (filename.endsWith(".prod"))?"":".prod";
		List<Product> ar = new ArrayList<>(fam.getProducts());
		Path path = FileSystems.getDefault().getPath(filename+suffix);

		try (PrintWriter pr = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path.toString()), StandardCharsets.UTF_8)))
		{
			pr.print(IntStream.range(0, ar.size())
					.mapToObj(i->toStringFile(ar.get(i), i))
					.collect(Collectors.joining(System.lineSeparator())));
			
		}
	}


	/**
	 * Returns a String representation of the product (to be stored in a file .prod).
	 *
	 * @param p the product
	 * @param id the id of the product
	 * @return a String representation of the product (to be stored in a file .prod).
	 */
	private String toStringFile(Product p, int id)
	{
		String req=p.getRequired().stream()
				.map(Feature::getName)
				.collect(Collectors.joining(","));
		String forb=p.getForbidden().stream()
				.map(Feature::getName)
				.collect(Collectors.joining(","));
		return "p"+id+": R={"+req+",} F={"+forb+",}";
	}

}
