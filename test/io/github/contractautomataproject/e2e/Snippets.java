package io.github.contractautomataproject.e2e;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.family.FMCA;
import io.github.contractautomataproject.catlib.family.Family;
import io.github.contractautomataproject.catlib.family.PartialProductGenerator;
import io.github.contractautomataproject.catlib.family.Product;
import io.github.contractautomataproject.catlib.family.converters.DimacFamilyConverter;
import io.github.contractautomataproject.catlib.family.converters.FamilyConverter;
import io.github.contractautomataproject.catlib.family.converters.FeatureIDEfamilyConverter;
import io.github.contractautomataproject.catlib.family.converters.ProdFamilyConverter;
import io.github.contractautomataproject.catlib.operators.ChoreographySynthesisOperator;
import io.github.contractautomataproject.catlib.operators.MSCACompositionFunction;
import io.github.contractautomataproject.catlib.operators.OrchestrationSynthesisOperator;
import io.github.contractautomataproject.catlib.operators.ProductOrchestrationSynthesisOperator;
import io.github.contractautomataproject.catlib.requirements.Agreement;
import io.github.contractautomataproject.catlib.requirements.StrongAgreement;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

public class Snippets {
	final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	
	@SuppressWarnings("unused")
	public void snippet1() throws Exception
	{
		AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut = new ArrayList<>(2);	
		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));//loading textual .data description of a CA
		aut.add(bdc.importMSCA(dir+"Hotel.data"));
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp = new MSCACompositionFunction(aut,new Agreement().negate()).apply(100);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> orc = new OrchestrationSynthesisOperator(new Agreement()).apply(comp);
	}
	
	public void snippet2() throws Exception
	{
		AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).data");
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> cor = new ChoreographySynthesisOperator(new StrongAgreement()).apply(aut);
		bdc.exportMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel).data",cor);
	}
	

	@SuppressWarnings("unused")
	public void snippet3() throws Exception
	{
		AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");		
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"singleRoom"});
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> orc = new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut);	
	}
	

	@SuppressWarnings("unused")
	public void snippet4() throws Exception
	{
		AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut2 = bdc.importMSCA(dir+"BusinessClientxHotel_open.data");
	
		FamilyConverter dfc = new ProdFamilyConverter();
		
		// import from .prod textual description of products
		Set<Product> sp = dfc.importProducts(dir+"ValidProducts.prod");
		
		//ValidProducts.prod contains also partial products, no need to generate them.
		//The family is generated without being optimised against an MSCA.
		//This is useful when different plant automata (FMCA) are used for the same family.
		Family fam =  new Family(sp);
		
		//two different FMCA may be using the same family
		FMCA faut = new FMCA(aut,fam);
		FMCA faut2 = new FMCA(aut2,fam);

		//selecting a product of first FMCA and computing its orchestration
		Product p = faut.getFamily().getProducts().iterator().next(); 
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> orcfam1 = new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(faut.getAut());
	}
	
	@SuppressWarnings("unused")
	public void snippet5() throws Exception
	{
		AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");

		//import from FeatureIDE model the products generated by FeatureIDE
		FamilyConverter ffc = new FeatureIDEfamilyConverter();
		Set<Product> sp2 = ffc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.xml"); 
		
		//in case the products are imported from FeatureIDE, 
		//the partial products not generated by FeatureIDE are generated first.
		//The FMCA constructors below optimises the product line against the automaton.
		FMCA faut = new FMCA(aut,new PartialProductGenerator().apply(sp2));

	
		//selecting a product of the family and computing its orchestration
		Product p = faut.getFamily().getProducts().iterator().next(); 
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> orcfam1 = new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(faut.getAut());

	}
	
	@SuppressWarnings("unused")
	public void snippet6() throws Exception
	{
		AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");

		//false parameter means that only maximal products (models of the formula) are generated, 
		//if true all products (models of the formula) are imported
		FamilyConverter dimfc = new DimacFamilyConverter(false);
		
		//import Dimac CNF formula models. Dimac file has been created using FeatureIDE export
		Set<Product> sp3 = dimfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs"); 
		
		//in case only the orchestration the family is to be computed, it is faster
		//to only import the maximal products using dimac converter, avoiding the 
		//processing of all products and partial products
		FMCA faut = new FMCA(aut,sp3);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> orcfam2 = faut.getOrchestrationOfFamily();	
	}
}
