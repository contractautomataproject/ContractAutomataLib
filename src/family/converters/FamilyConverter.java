package family.converters;

import java.io.IOException;
import java.util.Set;

import family.Family;
import family.Product;

public interface FamilyConverter {
	public Set<Product> importProducts(String filename) throws Exception;
	public void exportFamily(String filename, Family fam) throws IOException;
}
