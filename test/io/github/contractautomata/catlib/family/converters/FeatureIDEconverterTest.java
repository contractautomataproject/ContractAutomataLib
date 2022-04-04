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
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

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

//		FileWriter fileWriter = new FileWriter(dir+"productsFeatureIDEmodel2");
//		PrintWriter printWriter = new PrintWriter(fileWriter);
//		printWriter.print(FMCATest.sorting.apply(sp).toString());
//		printWriter.close();
		String test = Files.readString(Paths.get(dir+"productsFeatureIDEmodel2"), StandardCharsets.UTF_8);
		assertEquals(test,FMCATest.sorting.apply(sp).toString() );
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

		ffc.importProducts(dir+"FeatureIDEmodel2"+File.separator+"model.xml");
//		this test provokes an IOException for covering the catch block, however nor Travis neither GithubAction do raise the throwable

		try (RandomAccessFile raFile = new RandomAccessFile(dir+"FeatureIDEmodel2"+File.separator+
				"products"+File.separator+"00003.config", "rw")) {
			raFile.getChannel().lock();
			assertThrows("java.lang.RuntimeException: java.io.IOException: The process cannot access the file because another process has locked a portion of the file",
					RuntimeException.class, () -> ffc.importProducts(dir + "FeatureIDEmodel2" + File.separator + "model.xml"));
		}
	}

	
	@Test
	public void testExportException()
	{
		assertThrows(UnsupportedOperationException.class, () -> ffc.exportFamily("", fam));
	}

}
