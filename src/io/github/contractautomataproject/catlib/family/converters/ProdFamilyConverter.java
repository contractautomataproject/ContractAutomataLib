package io.github.contractautomataproject.catlib.family.converters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.contractautomataproject.catlib.family.Family;
import io.github.contractautomataproject.catlib.family.Feature;
import io.github.contractautomataproject.catlib.family.Product;

/**
 * Class implementing import/export from the .prod textual format
 * 
 * @author Davide Basile
 *
 */
public class ProdFamilyConverter implements FamilyConverter {

	@Override
	public Set<Product> importProducts(String filename) throws IOException {
		Path path = FileSystems.getDefault().getPath(filename);	
		if (path==null)
			throw new IllegalArgumentException("Empty file name");
		File f = new File(path.toString());
		
		Charset charset = Charset.forName("ISO-8859-1");
		List<String> lines = Files.readAllLines(f.toPath(), charset);

		Pattern pattern = Pattern.compile("p[0-9]++: R=\\{(.*)\\} F=\\{(.*)\\}");

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

	@Override
	public void exportFamily(String filename, Family fam) throws IOException{
		if (filename==null || filename.isEmpty())
			throw new IllegalArgumentException("Empty file name");

		String suffix = (filename.endsWith(".prod"))?"":".prod";
		List<Product> ar = new ArrayList<Product>(fam.getProducts());
		Path path = FileSystems.getDefault().getPath(filename+suffix);	
		if (path==null)
			throw new IllegalArgumentException("Empty file name");
		
		try (PrintWriter pr = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(path.toString())), "UTF-8")))
		{
			pr.print(IntStream.range(0, ar.size())
					.mapToObj(i->ar.get(i).toStringFile(i))
					.collect(Collectors.joining(System.lineSeparator())));
			
		}
	}

}
