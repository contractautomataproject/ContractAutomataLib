package family;

import java.io.IOException;

public interface FamilyConverter {
	public Family importFamily(String filename) throws Exception;
	public void exportFamily(String filename, Family fam) throws IOException;
}
