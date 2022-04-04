package io.github.contractautomata.catlib.family.converters;

import io.github.contractautomata.catlib.family.FMCATest;
import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


@RunWith(MockitoJUnitRunner.Strict.class)
public class FeatureIDEconverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;	
	private FamilyConverter ffc;

	@Mock private Family fam;

	@Before
	public void setUp() {
		ffc = new FeatureIDEfamilyConverter();
	}

	@Test
	public void testImportFamily() throws Exception
	{
		Set<Product> sp = ffc.importProducts(dir+"FeatureIDEmodel2"+File.separator+"model.xml");

//		FileWriter fileWriter = new FileWriter(dir+"productsFeatureIDEmodel2", StandardCharsets.UTF_8);
//		PrintWriter printWriter = new PrintWriter(fileWriter);
//		printWriter.print(FMCATest.sorting.apply(sp).stream().collect(Collectors.joining(System.lineSeparator())));
//		printWriter.close();
		Set<String> test = new HashSet<>(Files.readAllLines(Paths.get(dir+"productsFeatureIDEmodel2"), StandardCharsets.UTF_8));
		assertEquals(test,FMCATest.sorting.apply(sp));
	}

	@Test
	public void testImportFamilyEmptyFolder() throws Exception {
		assertEquals(Collections.emptySet(),ffc.importProducts(dir+"FeatureIDEmodel3"+File.separator+"model.xml"));
	}



	@Test
	public void testImportFamilyHiddenDirectory() throws Exception {
		String test = "[R:[card, noFreeCancellation, receipt, singleRoom, privateBathroom];"+System.lineSeparator()+
				"F:[sharedBathroom, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				"]";
		assertEquals(test,ffc.importProducts(dir+"FeatureIDEmodel4"+File.separator+"model.xml").toString());
	}

	@Test
	public void testImportFamilyWithSubfolderAndException() throws Exception
	{
		if (!System.getProperty("os.name").contains("Windows"))
			return;
//		this test provokes an IOException for covering the catch block, however only works on Windows, in other OS
//      locking files is advisory

		try (RandomAccessFile raFile = new RandomAccessFile(dir+"FeatureIDEmodel2"+File.separator+
				"products"+File.separator+"00003.config", "rw")) {

			raFile.getChannel().lock();
			assertThrows("java.lang.RuntimeException: java.io.IOException: The process cannot access the file because another process has locked a portion of the file",
					RuntimeException.class, () -> ffc.importProducts(dir + "FeatureIDEmodel2" + File.separator + "model.xml"));
		}
	}


	@Test
	public void testImportFamilyExceptionUnix() throws Exception
	{
		if (System.getProperty("os.name").contains("Windows"))
			return;

		Path path = Paths.get(dir + "FeatureIDEmodel5" + File.separator + "products"+File.separator+"00002.config");
		Set<PosixFilePermission> perms = Files.readAttributes(path, PosixFileAttributes.class).permissions();

		System.out.format("Permissions before: %s%n",  PosixFilePermissions.toString(perms));

		perms.remove(PosixFilePermission.OWNER_WRITE);
		perms.remove(PosixFilePermission.OWNER_READ);
		perms.remove(PosixFilePermission.OWNER_EXECUTE);
		perms.remove(PosixFilePermission.GROUP_WRITE);
		perms.remove(PosixFilePermission.GROUP_READ);
		perms.remove(PosixFilePermission.GROUP_EXECUTE);
		perms.remove(PosixFilePermission.OTHERS_WRITE);
		perms.remove(PosixFilePermission.OTHERS_READ);
		perms.remove(PosixFilePermission.OTHERS_EXECUTE);
		Files.setPosixFilePermissions(path, perms);

		System.out.format("Permissions after:  %s%n",  PosixFilePermissions.toString(perms));

		//one of the configurations has no permissions to read or write
		assertThrows(RuntimeException.class, () -> ffc.importProducts(dir + "FeatureIDEmodel5" + File.separator + "model.xml"));
	}

	
	@Test
	public void testExportException()
	{
		assertThrows(UnsupportedOperationException.class, () -> ffc.exportFamily("", fam));
	}

}
