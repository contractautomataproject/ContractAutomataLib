package it.io.github.contractautomata.catlib.family;

import it.io.github.contractautomata.catlib.automaton.ITAutomatonTest;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.family.FMCA;
import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.Product;
import io.github.contractautomata.catlib.family.converters.FamilyConverter;
import io.github.contractautomata.catlib.family.converters.ProdFamilyConverter;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ITFMCATest {
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
    private final FamilyConverter dfc = new ProdFamilyConverter();

	@Test
	public void getAut_test() throws Exception 
	{
		Automaton<String,Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>> a
				= bdc.importMSCA(ITAutomatonTest.dir+ "(BusinessClientxHotelxEconomyClient).data");
		FMCA aut = new FMCA(a,new Family(new HashSet<>()));
		Assert.assertEquals(aut.getAut(),a);
	}
	
	
	@Test
	public void testValidProductsOrc() throws Exception
	{
		String fileName = ITAutomatonTest.dir+ "ValidProducts.prod";
		Family fam= new Family(dfc.importProducts(fileName));
		FMCA aut = new FMCA(bdc.importMSCA(ITAutomatonTest.dir+ "Orc_(BusinessClientxHotelxEconomyClient).data"),fam);
		Set<Product> vp = aut.productsRespectingValidity();

		Family test = new Family(dfc.importProducts(ITAutomatonTest.dir+ "validProductsOrcTest.prod"));
		
		assertEquals(vp,test.getProducts());
	}

	@Test
	public void testCanonicalProducts() throws Exception
	{
		String fileName = ITAutomatonTest.dir+ "ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		FMCA aut = new FMCA(bdc.importMSCA(ITAutomatonTest.dir+ "(BusinessClientxHotelxEconomyClient).data"),fam);

		Set<Product> cps = aut.getCanonicalProducts().keySet();
		
		Family test = new Family(dfc.importProducts(ITAutomatonTest.dir+ "canonicalProductsTest.prod"));

		assertEquals(cps,test.getProducts());
	}

	@Test
	public void testProductsWithNonEmptyOrchestration() throws Exception
	{
		
		String fileName = ITAutomatonTest.dir+ "ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		FMCA aut = new FMCA(bdc.importMSCA(ITAutomatonTest.dir+ "Orc_(BusinessClientxHotelxEconomyClient).data"),fam);
		Set<Product> vp = aut.productsWithNonEmptyOrchestration();

		Family test = new Family(dfc.importProducts(ITAutomatonTest.dir+ "productsWithNonEmptyOrchestration.prod"));
		
		assertEquals(vp,test.getProducts());
	}

	@Test
	public void testFamilyOrc() throws Exception
	{
		String fileName = ITAutomatonTest.dir+ "ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		FMCA faut = new FMCA(bdc.importMSCA(ITAutomatonTest.dir+ "(BusinessClientxHotelxEconomyClient).data"),fam);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(ITAutomatonTest.dir+ "Orc_family_(BusinessClientxHotelxEconomyClient)_test.data");

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> controller = faut.getOrchestrationOfFamily();

		assertTrue(ITAutomatonTest.autEquals(controller, test));
	}
	@Test
	public void testOrchestrationOfFamilyEnumerative() throws Exception
	{
		String fileName = ITAutomatonTest.dir+ "ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		FMCA aut = new FMCA(bdc.importMSCA(ITAutomatonTest.dir+ "(BusinessClientxHotelxEconomyClient).data"),fam);
		
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> ofe =  aut.getOrchestrationOfFamilyEnumerative();
		Automaton<String, Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(ITAutomatonTest.dir+ "test_ofe.data");//Orc_fam_wopo_test.mxe");

		assertTrue(ITAutomatonTest.autEquals(ofe, test));
	}

	//exceptions
	
	@Test
	public void testCanonicalProductsException() throws Exception
	{
		String fileName = ITAutomatonTest.dir+ "ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut
				= bdc.importMSCA(ITAutomatonTest.dir+ "Orc_family_(BusinessClientxHotelxEconomyClient)_test.data");
		FMCA fmca = new FMCA(aut,fam);
		assertThrows(UnsupportedOperationException.class,fmca::getCanonicalProducts);
	}

	@Test
	public void testSelectProductSatisfyingPredicateException() throws Exception
	{
		String fileName = ITAutomatonTest.dir+ "ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		FMCA aut = new FMCA(bdc.importMSCA(ITAutomatonTest.dir+ "Orc_family_(BusinessClientxHotelxEconomyClient)_test.data"),fam);
		
		assertThrows(UnsupportedOperationException.class,aut::productsWithNonEmptyOrchestration);
	}
}

//END OF THE CLASS



//@Test
//public void validProductsOrcFam() throws Exception
//{
//	
//	String fileName =dir+"ValidProducts.prod";
//	Family fam=new Family(dfc.importFamily(fileName));
//	FMCA aut = new FMCA(Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>IO.parseXMLintoAutomaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>(dir+"Orc_family_(BusinessClientxHotelxEconomyClient)_test.mxe"),fam);
//
//	Set<Product> rv = aut.respectingValidityFamily();
//
//	Set<Product> test = Family.readFileNew(dir+"respectingValidityTest.prod");
////	Set<Product> vp = Arrays.stream(fam.validProducts(aut.getAut()))
////			.mapToObj(i->fam.getElements()[i])
////			.collect(Collectors.toSet());
////	
//	assertTrue(rv.equals(test));
//}