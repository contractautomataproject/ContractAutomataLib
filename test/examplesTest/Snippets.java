package examplesTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import contractAutomata.MSCA;
import contractAutomata.converters.DataConverter;
import contractAutomata.converters.MSCAConverter;
import contractAutomata.converters.MxeConverter;
import contractAutomata.operators.ChoreographySynthesisOperator;
import contractAutomata.operators.CompositionFunction;
import contractAutomata.operators.OrchestrationSynthesisOperator;
import contractAutomata.operators.ProductOrchestrationSynthesisOperator;
import contractAutomata.requirements.Agreement;
import contractAutomata.requirements.StrongAgreement;
import family.FMCA;
import family.Family;
import family.PartialProductGenerator;
import family.Product;
import family.converters.DimacFamilyConverter;
import family.converters.FamilyConverter;
import family.converters.FeatureIDEfamilyConverter;
import family.converters.ProdFamilyConverter;

public class Snippets {
	final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	
	@SuppressWarnings("unused")
	@Test
	public void test1() throws Exception
	{
		MSCAConverter bdc = new DataConverter();
		List<MSCA> aut = new ArrayList<>(2);	
		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));//loading textual .data description of a CA
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));
		MSCA comp = new CompositionFunction().apply(aut, new Agreement().negate(),100);
		MSCA orc = new OrchestrationSynthesisOperator(new Agreement()).apply(comp);
	}
	

	@SuppressWarnings("unused")
	@Test
	public void test2() throws Exception
	{
		MSCAConverter bdc = new DataConverter();
		MSCA aut = bdc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe.data");
		MSCA cor = new ChoreographySynthesisOperator(new StrongAgreement()).apply(aut);
		bdc.exportMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel).data",cor);
	}
	

	@SuppressWarnings("unused")
	@Test
	public void test3() throws Exception
	{
		MSCA aut = new MxeConverter().importMSCA(dir+"(BusinessClientxHotelxEconomyClient).mxe");		
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"singleRoom"});
		MSCA orc = new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut);	
	}
	

	@SuppressWarnings("unused")
	@Test
	public void test4() throws Exception
	{
		FamilyConverter dfc = new ProdFamilyConverter();
		FamilyConverter ffc = new FeatureIDEfamilyConverter();
		FamilyConverter dimfc = new DimacFamilyConverter(false);//false means that only maximal products are imported
		
		MSCA aut = new MxeConverter().importMSCA(dir+"(BusinessClientxHotelxEconomyClient).mxe");
		Set<Product> sp = dfc.importProducts(dir+"ValidProducts.prod");// import from .prod textual description of products
		Set<Product> sp2 = ffc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.xml"); //import from FeatureIDE model
		Set<Product> sp3 = dimfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs"); //import from Dimac model
		
		
		FMCA faut1 = new FMCA(aut,new Family(sp));//ValidProducts.prod has already processed the products
		FMCA faut2 = new FMCA(aut,new PartialProductGenerator().apply(sp2));//firstly generate partial products 
																			//not presents in FeatureIDE
		FMCA faut3 = new FMCA(aut,sp3);//this only contains maximal products, it is faster
		
		MSCA orcfam1 = faut1.getOrchestrationOfFamily();	
		MSCA orcfam2 = faut2.getOrchestrationOfFamily();	
	}
}
