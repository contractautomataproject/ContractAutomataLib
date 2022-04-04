package io.github.contractautomata.catlib.family.converters;

import io.github.contractautomata.catlib.family.Family;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Collections;

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
	public void testImportFamily() throws Exception {

		assertEquals(products2(),ffc.importProducts(dir+"FeatureIDEmodel2"+File.separator+"model.xml").toString() );
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
	
	
	private String products2(){
		return "[R:[sharedBathroom, card, receipt, singleRoom, freeCancellation];"+System.lineSeparator()+
				"F:[noFreeCancellation, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[card, noFreeCancellation, receipt, privateBathroom];"+System.lineSeparator()+
				"F:[sharedBathroom, singleRoom, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[card, noFreeCancellation, receipt, singleRoom, privateBathroom];"+System.lineSeparator()+
				"F:[sharedBathroom, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[sharedBathroom, invoice, cash, freeCancellation];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, receipt, singleRoom, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[sharedBathroom, receipt, singleRoom, invoice, cash, freeCancellation];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[sharedBathroom, receipt, invoice, sharedRoom, cash, privateBathroom];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, singleRoom, freeCancellation];"+System.lineSeparator()+
				", R:[sharedBathroom, noFreeCancellation, singleRoom, invoice, sharedRoom, cash, privateBathroom];"+System.lineSeparator()+
				"F:[card, receipt, freeCancellation];"+System.lineSeparator()+
				", R:[card, sharedBathroom, noFreeCancellation, receipt, sharedRoom, freeCancellation];"+System.lineSeparator()+
				"F:[singleRoom, invoice, cash, privateBathroom];"+System.lineSeparator()+
				", R:[card, sharedBathroom, invoice, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, singleRoom, cash];"+System.lineSeparator()+
				", R:[card, noFreeCancellation, receipt, singleRoom, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				"]";
	}
}
