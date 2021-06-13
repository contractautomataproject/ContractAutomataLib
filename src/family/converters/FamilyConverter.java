package family.converters;

import java.io.IOException;

import family.Family;

public interface FamilyConverter {
	public Family importFamily(String filename) throws Exception;
	public void exportFamily(String filename, Family fam) throws IOException;
}
