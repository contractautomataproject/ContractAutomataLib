package io.github.contractautomata.catlib.family.converters;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class DimacConverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test"+File.separator+"test_resources"+File.separator;

	private FamilyConverter dfc;
	private FamilyConverter dfc_pi;

	@Before
	public void setUp() {
 		dfc = new DimacFamilyConverter(true);
		dfc_pi = new DimacFamilyConverter(false);
	}
	
	@Test
	public void testImport() throws Exception
	{
		String test = Files.readString(Paths.get(dir+"productsFeatureIDEmodel"), StandardCharsets.UTF_8);
		assertEquals(test, dfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs").toString());
	}
	
	@Test
	public void testPrimeImplicant() throws Exception
	{
		String test ="[R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, sharedRoom2, singleRoom, sharedRoom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedRoom2, singleRoom, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, singleRoom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, singleRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[card];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, sharedRoom2, sharedRoom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedRoom2, sharedRoom, cash];"+System.lineSeparator()+
				"]";

		assertEquals(test,dfc_pi.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs").toString());

	}


	@Test
	public void testUnsat() throws Exception
	{
		assertTrue(dfc.importProducts(dir+"unsat.dimacs").isEmpty());
	}
	
	@Test
	public void testExport() {
		assertThrows(UnsupportedOperationException.class, ()->dfc.exportFamily(null, null));
	}

}

