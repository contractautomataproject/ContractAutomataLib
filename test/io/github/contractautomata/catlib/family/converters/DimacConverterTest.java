package io.github.contractautomata.catlib.family.converters;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class DimacConverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;

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
		assertEquals(products(), dfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs").toString());
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
	
	private String products() {
		return "[R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, singleRoom, invoice, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[noFreeCancellation, sharedRoom2, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[noFreeCancellation, sharedRoom2, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, receipt, sharedRoom2, sharedRoom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[card, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, receipt, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, card, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, Hotel, invoice, sharedRoom, singleRoom2, singleRoom3, EconomyRoom, BusinessCancellation, privateBathroom, BusinessClient, Notification, freeCancellation, EconomyClient];"+System.lineSeparator()+
				"F:[noFreeCancellation, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, receipt, sharedRoom2, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, receipt, singleRoom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, receipt, sharedRoom2, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, sharedRoom2, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, Hotel, invoice, sharedRoom, singleRoom2, singleRoom3, EconomyRoom, BusinessCancellation, BusinessClient, Notification, freeCancellation, EconomyClient];"+System.lineSeparator()+
				"F:[privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, receipt, sharedRoom2, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[noFreeCancellation, sharedRoom2, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, singleRoom, invoice, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedRoom2, singleRoom, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[invoice, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, Hotel, invoice, sharedRoom, singleRoom2, singleRoom3, EconomyRoom, BusinessCancellation, privateBathroom, BusinessClient, Notification, freeCancellation, EconomyClient, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, invoice, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, singleRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, sharedRoom2, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[receipt, sharedRoom2, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, singleRoom, invoice, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, singleRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, singleRoom, invoice, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, sharedRoom2, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[receipt, sharedRoom2, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, receipt, sharedRoom2, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, invoice, cash];"+System.lineSeparator()+
				", R:[Composition, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, Hotel, invoice, sharedRoom, singleRoom2, singleRoom3, EconomyRoom, BusinessCancellation, BusinessClient, Notification, freeCancellation, EconomyClient, cash];"+System.lineSeparator()+
				"F:[card, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, singleRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, singleRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, receipt, sharedRoom2, singleRoom, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, sharedRoom2, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, singleRoom, invoice, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, sharedRoom2, singleRoom, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, singleRoom];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, receipt, sharedRoom2, singleRoom, sharedRoom];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, receipt, singleRoom, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, receipt, sharedRoom2, singleRoom, sharedRoom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, sharedRoom2, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, noFreeCancellation, receipt, sharedRoom2, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, receipt, sharedRoom2, sharedRoom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, singleRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[invoice, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, sharedRoom2, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, receipt, sharedRoom2, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, singleRoom, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, sharedRoom2, singleRoom, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, singleRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, receipt, sharedRoom2, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, noFreeCancellation, sharedRoom2, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, receipt, singleRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, noFreeCancellation, receipt, sharedRoom2, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, singleRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[invoice, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedRoom2, singleRoom, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, receipt, sharedRoom2, singleRoom, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, sharedRoom2, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, receipt, sharedRoom2, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, sharedRoom2, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, singleRoom, invoice, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, receipt, sharedRoom2, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, sharedRoom2, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, singleRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, receipt, sharedRoom2, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, singleRoom, invoice, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, receipt, singleRoom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, singleRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, noFreeCancellation, receipt, sharedRoom2, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedRoom2, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, sharedRoom2, singleRoom, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, noFreeCancellation, sharedRoom2, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, singleRoom, invoice, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, receipt, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, singleRoom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, singleRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, Hotel, invoice, sharedRoom, singleRoom2, singleRoom3, EconomyRoom, BusinessCancellation, privateBathroom, BusinessClient, Notification, EconomyClient, cash];"+System.lineSeparator()+
				"F:[card, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, receipt, sharedRoom2, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, sharedRoom2, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, noFreeCancellation, sharedRoom2, sharedRoom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, receipt, sharedRoom2, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[receipt, invoice, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedRoom2, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, singleRoom, invoice, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, receipt, singleRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, sharedRoom2, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, receipt, sharedRoom2, singleRoom, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, sharedRoom2, singleRoom, sharedRoom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, singleRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, invoice, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, singleRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, sharedRoom2, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, Hotel, invoice, sharedRoom, singleRoom2, singleRoom3, EconomyRoom, BusinessCancellation, privateBathroom, BusinessClient, Notification, EconomyClient];"+System.lineSeparator()+
				"F:[cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, sharedRoom2, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, receipt, sharedRoom2, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[noFreeCancellation, sharedRoom2, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, sharedRoom2, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, invoice, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, singleRoom, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, receipt, singleRoom, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, sharedRoom2, singleRoom, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, sharedRoom2, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedRoom2, privateBathroom, freeCancellation, sharedBathroom, noFreeCancellation, receipt, singleRoom, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, receipt, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, receipt, sharedRoom2, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, receipt, sharedRoom2, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, sharedRoom2, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, singleRoom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, sharedRoom2, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, sharedRoom2, singleRoom, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, receipt, singleRoom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, singleRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, singleRoom, invoice, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, sharedRoom2, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, receipt, sharedRoom2, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, sharedRoom2, sharedRoom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, singleRoom, invoice, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, sharedRoom2, sharedRoom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, sharedRoom2, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[receipt, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, noFreeCancellation, receipt, sharedRoom2, sharedRoom];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, invoice, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedRoom2, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, receipt, sharedRoom2, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, sharedRoom2, singleRoom, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, singleRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[receipt, invoice, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, sharedRoom2, singleRoom, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, receipt, sharedRoom2, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, receipt, sharedRoom2, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, singleRoom, invoice, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, sharedRoom2, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, singleRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedRoom2, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, receipt];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, sharedRoom2, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, sharedRoom2, singleRoom, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[receipt, sharedRoom2, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, singleRoom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, singleRoom, invoice, cash];"+System.lineSeparator()+
				", R:[Composition, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, Hotel, invoice, sharedRoom, singleRoom2, singleRoom3, EconomyRoom, BusinessCancellation, privateBathroom, BusinessClient, Notification, freeCancellation, EconomyClient, cash];"+System.lineSeparator()+
				"F:[card, receipt];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, privateBathroom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, receipt, sharedRoom2, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[card, receipt, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, receipt, sharedRoom2, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, invoice, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, receipt, sharedRoom2, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, sharedRoom2, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, singleRoom, invoice, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, invoice, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, receipt, sharedRoom2, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, singleRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[noFreeCancellation, sharedRoom2, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, sharedRoom2, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[noFreeCancellation, sharedRoom2, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, sharedRoom2, singleRoom, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, sharedRoom2, singleRoom, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[receipt, sharedRoom2, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[card, receipt, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, sharedRoom2, singleRoom, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, sharedRoom2, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[receipt, sharedRoom2, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[receipt, invoice, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, sharedRoom2, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, sharedRoom2, singleRoom, sharedRoom];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedRoom2, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, receipt, sharedRoom2, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, singleRoom, invoice, cash];"+System.lineSeparator()+
				", R:[Composition, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, Hotel, invoice, sharedRoom, singleRoom2, singleRoom3, EconomyRoom, BusinessCancellation, privateBathroom, BusinessClient, Notification, freeCancellation, EconomyClient, cash];"+System.lineSeparator()+
				"F:[card];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[receipt, invoice, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, sharedRoom2, singleRoom, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[noFreeCancellation, sharedRoom2, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, singleRoom, invoice, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedRoom2, singleRoom, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedRoom2, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[receipt, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, receipt, sharedRoom2, singleRoom, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[receipt, sharedRoom2, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, sharedRoom2, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, sharedRoom];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, invoice, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, sharedRoom2, singleRoom, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedRoom2, singleRoom, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, receipt, sharedRoom2, invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, singleRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedRoom2, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, Hotel, sharedRoom, singleRoom2, singleRoom3, EconomyRoom, BusinessCancellation, privateBathroom, BusinessClient, Notification, freeCancellation, EconomyClient];"+System.lineSeparator()+
				"F:[invoice, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[receipt, sharedRoom2, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, sharedRoom2, singleRoom, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, sharedRoom2, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, Hotel, invoice, sharedRoom, singleRoom2, singleRoom3, EconomyRoom, BusinessCancellation, privateBathroom, BusinessClient, Notification, freeCancellation, EconomyClient];"+System.lineSeparator()+
				"F:[cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, Hotel, invoice, sharedRoom, singleRoom2, singleRoom3, EconomyRoom, BusinessCancellation, privateBathroom, BusinessClient, Notification, freeCancellation, EconomyClient];"+System.lineSeparator()+
				"F:[receipt, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, sharedRoom2, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, sharedRoom2, sharedRoom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, sharedRoom2, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[receipt, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedRoom2, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, noFreeCancellation, sharedRoom2, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, sharedRoom2, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, sharedRoom2, singleRoom, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[sharedBathroom, card, receipt, sharedRoom2, sharedRoom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[card, noFreeCancellation, receipt, sharedRoom2, sharedRoom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[noFreeCancellation, sharedRoom2, sharedRoom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, sharedRoom2, invoice, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, noFreeCancellation, sharedRoom2, singleRoom, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[receipt, sharedRoom2, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, singleRoom, cash];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, receipt, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, sharedBathroom, card, sharedRoom2, singleRoom, sharedRoom, privateBathroom, freeCancellation];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, Payment, EconomyBathroom, BusinessBathroom, receipt, singleRoom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[sharedBathroom, noFreeCancellation, sharedRoom2, sharedRoom, privateBathroom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, sharedRoom2, singleRoom, invoice, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom, cash];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[card, receipt, privateBathroom];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, noFreeCancellation, receipt, sharedRoom2, singleRoom, sharedRoom, privateBathroom];"+System.lineSeparator()+
				", R:[singleRoom2, singleRoom3, Composition, card, EconomyRoom, BusinessCancellation, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, sharedBathroom, Payment, EconomyBathroom, BusinessBathroom, singleRoom, EconomyClient, Hotel];"+System.lineSeparator()+
				"F:[noFreeCancellation, receipt, sharedRoom2, invoice, sharedRoom, cash, freeCancellation];"+System.lineSeparator()+
				", R:[Composition, EconomyRoom, BusinessCancellation, sharedRoom2, EconomyCancellation, HotelRoom, BusinessRoom, privateBathroom, BusinessClient, Notification, freeCancellation, sharedBathroom, Payment, noFreeCancellation, EconomyBathroom, BusinessBathroom, EconomyClient, Hotel, invoice, sharedRoom, cash];"+System.lineSeparator()+
				"F:[singleRoom2, singleRoom3, card, receipt, singleRoom];"+System.lineSeparator()+
				"]";
	}
}

