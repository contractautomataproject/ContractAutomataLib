## Welcome

The Contract Automata Library (CATLib)  is the main repository of the Contract Automata Toolkit.
The Contract Automata Toolkit groups together repositories about contract automata.
Contract automata are a formalism developed in the research area of foundations for services and distributed computing.
They are used for specifying services' interface, called behavioral contracts, as finite state automata, with functionalities for composing contracts
and generating the orchestration or choreography of a composition of services, and with extensions to modalities (MSCA) and product lines (FMCA).
The source code has been redesigned and refactored using the new functionalities introduced with Java 8 (streams, lambda).

<h2>License</h2>
The tool is available under <a href="https://www.gnu.org/licenses/gpl-3.0">GPL-3.0 license</a>.

<h2>Install</h2>

The Contract Automata Library is released in the Maven Central Repository, simply add this dependency to the `pom.xml`
of your Maven project.

```xml
<dependency>
 <groupId>io.github.contractautomataproject</groupId>
 <artifactId>catlib</artifactId>
 <version>1.0.1</version>
</dependency>
```

<h2>Usage</h2>

This section shows the usage of some functionalities of the library. 
It is assumed that the reader is familiar with contract automata and their operations. 
If not, you may refer to the references below, as well as the technical documentation of the library
(whose links are in the README of the repository (link above)). 

The repository <a href="https://github.com/contractautomataproject/tictactoe">https://github.com/contractautomataproject/tictactoe</a> contains 
an example of usage of the Contract Automata Library to model the tic-tac-toe game using contract automata and their composition 
and synthesis operations to compute the strategy for a player to never lose a game.
The example also shows how to realize the final application (the game) using the synthesised automata to orchestrate the control 
flow of the application. We refer to the above repository for more information.

In the following examples, we apply CATLib to the Hotel Reservation case study, which is thoroughly described in [SCICO20] (see references below).
The specific example showing the choreography synthesis is taken from  Example 2.2 of [LMCS20]. 
Both these journal articles are Open Access and can be freely downloaded. 
The hotel reservation system is one of the classical service booking examples that can be found in publications about formal methods for service-oriented comput computing. 
 It features clients booking reservations from hotels with intermediary brokers. 
The following code is available in the executable class `/src/test/java/examples/Examples.java` of the GitHub repository.
These examples are available in a reproducible capsule at <a href="https://doi.org/10.24433/CO.1575879.v1">https://doi.org/10.24433/CO.1575879.v1</a>.
When launching the class, it will print to the console output the `.data` descriptions of the computed automata. 
It is possible to crosscheck this output with the figures depicting these automata in [SCICO20][LMCS20] (more below). 
This check is also performed automatically by the tests of the library.
The files used are stored under the folder `/src/test/resources` of the repository.

The first  example, whose code is reported below,  shows how to load automata, compute their composition and compute the synthesis of the orchestration.
It starts by loading the `BusinessClient` and `Hotel` automata that are depicted in Figure 2 and Figure 3 in [SCICO20]. 
These automata have been stored in `.data` textual format, and are imported using `AutDataConverter` (for more information 
on the textual format check the Editing section below).
The `MSCACompositionFunction` is used to compute a composition of these two automata. 
The constructor takes a pruning predicate: when generating the composition, transitions whose labels are requests 
will not be further explored.
This is used to avoid generating portions of the automaton only reachable by transitions
violating the predicate that we want to enforce with the orchestration.
Indeed, the orchestration synthesis would prune these transitions anyway.
This allows to scale up to bigger compositions without losing information.
The predicate is the property of agreement: each request of either the client 
or the hotel must be matched by an offer of the other partner. 
Transitions whose labels have a request but no offer are pruned.
When applying the composition, a bound to the maximum length of a path in the composition is passed as argument. 
This allows to implement a bounded composition.
After the composition is computed, it is refined to an orchestration.
This is done with the `OrchestrationSynthesisOperator`, which takes as argument the aforementioned agreement property.
This composition is depicted in Figure 6 in [SCICO20].

```java
AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);
aut.add(bdc.importMSCA(dir+"BusinessClient.data"));//loading textual .data description of a CA
aut.add(bdc.importMSCA(dir+"Hotel.data"));
Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp = new MSCACompositionFunction<>(aut,t->t.getLabel().isRequest()).apply(100);
Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = new OrchestrationSynthesisOperator<String>(new Agreement()).apply(comp);
```


The second example, reported below, shows how to synthesise a choreography and store the result. 
It loads a precomputed composition stored in a `.data` format. 
The automata in the composition are depicted in Figure 1, Figure 2 and Figure 3 in [LMCS20].
Subsequently, the class `ChoreographySynthesisOperator` is used to synthesise the choreography. 
 The constructor takes as argument a property invariant on the 
synthesised labels. In this case the property is strong agreement: all labels must be matches 
between an offer and a request. 
The choreography is stored again in a `.data` format. 
The computed choreography is depicted in Figure 8 in [LMCS20].  
```java
AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).data");
Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> cor = new ChoreographySynthesisOperator<String>(new StrongAgreement()).apply(aut);
bdc.exportMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_example.data",cor);
```

The third example, reported below, shows how to instantiate a single product and synthesise an orchestration for that 
specific product. 
The loaded automaton is the composition of the example in [SCICO20]. 
The `EconomyClient` automaton is depicted in Figure 4 in [SCICO20].
The product is instantiated using the class `Product`, taking as argument the required and forbidden features. 
The product requires features `card` and `sharedBathroom` to be reachable as labels in the automaton, and forbidding feature `cash`.
The class `ProductOrchestrationSynthesisOperator` takes as argument the property to enforce (agreement) and the 
product, and it is applied on the loaded automaton.  
The orchestration for this product is depicted in Figure 5 in [SCICO20].

```java
AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"cash"});
Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = new ProductOrchestrationSynthesisOperator<String>(new Agreement(),p).apply(aut);
```

The following example shows how to import a product-line expressed in `.prod` format and how 
to instantiate a Featured Modal Contract Automaton (FMCA).
Firstly, two automata are loaded, again these are automata taken from the example in [SCICO20].
The set of products of the family is imported using the class `ProdFamilyConverter`.
In this example the Family is generated independently of the automaton. 
The family is instantiated with the class `Family`. 
Afterward, two `FMCA` are instantiated. 
They have the same automaton, but different families. 
Finally, one of the products of the family is picked up and the corresponding orchestration is computed.

```java
AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut2 = bdc.importMSCA(dir+"BusinessClientxHotel_open.data");
	
FamilyConverter dfc = new ProdFamilyConverter();
		
// import from .prod textual description of products
Set<Product> sp = dfc.importProducts(dir+"ValidProducts.prod");
		
//ValidProducts.prod contains also partial products, no need to generate them.
//The family is generated without being optimised against an MSCA.
//This is useful when different plant automata (FMCA) are used for the same family.
Family fam =  new Family(sp);
		
//two different FMCA may be using the same family
FMCA faut = new FMCA(aut,fam);
new FMCA(aut2,fam);

//selecting a product of the first FMCA and computing its orchestration
Product p = faut.getFamily().getProducts().iterator().next(); 
Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orcfam1 = new ProductOrchestrationSynthesisOperator<String>(new Agreement(),p).apply(faut.getAut());
```

The following example shows how to import a product line as a `.xml` FeatureIDE model (whose products are generated by FeatureIDE). 
This is done with the class `FeatureIDEfamilyConverter`. 
When instantiating the FMCA, we apply the `PartialProductGenerator` to the products we have just imported.
Indeed, FeatureIDE does not generate partial products, which are generated using `PartialProductGenerator`.
The set of products is passed to the constructor of the `FMCA`: this will exploit the information on the actions
of the automaton to refine the set of products, discarding redundant ones.
The orchestration of a product of the family is synthesised.

```java
AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");

//import from FeatureIDE model the products generated by FeatureIDE
FamilyConverter ffc = new FeatureIDEfamilyConverter();
Set<Product> sp2 = ffc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.xml"); 
		
//in case the products are imported from FeatureIDE, 
//the partial products not generated by FeatureIDE are generated first.
//The FMCA constructors below optimises the product line against the automaton.
FMCA faut = new FMCA(aut,new PartialProductGenerator().apply(sp2));

	
//selecting a product of the family and computing its orchestration
Product p = faut.getFamily().getProducts().iterator().next(); 
Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orcfam1 = new ProductOrchestrationSynthesisOperator<String>(new Agreement(),p).apply(faut.getAut());
```

Finally, the following example shows how to  import a product line as a `.dimac` file.
The `DimacsFamilyConverter` constructor has a parameter, if true all products are generated,
otherwise only maximal products are generated.
Avoiding the generation of all products and partial products greatly improve
performances with respect to generating them as in the above example. 
This is useful if only the orchestration of the product line is to
be synthesised, and there is no need to operate on a single product.
The orchestration of the family is computed by invoking the method 
`getOrchestrationOfFamily` on the `FMCA` object. 

```java
AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");

//false parameter means that only maximal products (models of the formula) are generated, 
//if true all products (models of the formula) are imported
FamilyConverter dimfc = new DimacsFamilyConverter(false);
		
//import Dimac CNF formula models. Dimac file has been created using FeatureIDE export
Set<Product> sp3 = dimfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs"); 
		
//in case only the orchestration the family is to be computed, it is faster
//to only import the maximal products using dimac converter, avoiding the 
//processing of all products and partial products
FMCA faut = new FMCA(aut,sp3);
Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orcfam2 = faut.getOrchestrationOfFamily();
```

<h2>Videos</h2>
Check the <a href="https://www.youtube.com/playlist?list=PLory_2tIDsJvZB2eVlpji-baIz0320TwM">playlist of video tutorials and presentations</a> about CAT.


<h2>Editing</h2>

The Contract Automata Library is a back-end and new front-ends can be developed for
editing or automatically generating contracts (e.g., from static analysis tools), using this library.

The <a href="https://github.com/davidebasile/ContractAutomataApp">Contract Automata App</a> is
a GUI front-end allowing to graphically edit and export contract automata in a `.mxe` format.

A textual `.data` format is also supported, as well as a `.json` format (still under development).
This is an example of the `PriviledgedClient.data` principal contract automaton taken from [LMCS20].
This contract has rank 1, initial state is `[0]`, final states are `[4]`,`[0]` and `[3]`.
This contract has been created using the GUI app and has been exported in the `.data` format.
It has four transitions, described by indicating the source state, label and target state.
A necessary lazy transition is prefixed by `!L` whilst a necessary urgent transition is prefixed by `!U`.

```c
Rank: 1
Initial state: [0]
Final states: [[4, 0, 3]]
Transitions: 
([1],[?bestoffer],[2])
!L([0],[!contact],[1])
([2],[!reject],[4])
([2],[!accept],[3])
```

This is an example of the choreography automatically computed by the library, the example is taken from [LMCS20].

```c
Rank: 5
Initial state: [0, 0, 0, 0, 0]
Final states: [[0][0, 4, 3][8, 0, 14][4, 0, 3][4, 0]]
Transitions: 
([0, 1, 3, 1, 0],[-, -, ?ans, !ans, -],[0, 1, 5, 2, 0])
([0, 2, 6, 2, 2],[-, !accept, ?accept, -, -],[0, 3, 7, 2, 2])
!L([0, 0, 0, 0, 0],[-, !contact, ?contact, -, -],[0, 1, 1, 0, 0])
([0, 3, 9, 3, 2],[-, -, !nobook, -, ?nobook],[0, 3, 8, 3, 4])
([0, 1, 11, 2, 2],[-, ?bestoffer, !bestoffer, -, -],[0, 2, 6, 2, 2])
([0, 1, 5, 2, 0],[-, -, !check, -, ?check],[0, 1, 10, 2, 1])
([0, 3, 7, 2, 2],[-, -, !book, ?book, -],[0, 3, 9, 3, 2])
([0, 1, 10, 2, 1],[-, -, ?ans, -, !ans],[0, 1, 11, 2, 2])
([0, 4, 13, 2, 4],[-, -, !nobook, ?nobook, -],[0, 4, 14, 4, 4])
([0, 1, 1, 0, 0],[-, -, !check, ?check, -],[0, 1, 3, 1, 0])
([0, 4, 12, 2, 2],[-, -, !nobook, -, ?nobook],[0, 4, 13, 2, 4])
([0, 2, 6, 2, 2],[-, !reject, ?reject, -, -],[0, 4, 12, 2, 2])
```

In this case, the contract automaton is of rank 5.
Note that the final states of each principal are separately reported.
In a final state of the composed automaton it is required that all principals are in a final state (e.g., `[0, 4, 14, 4, 4]` is a final state).

See the references below for more information on the contract automata formalism.


<h2> Developers Documentation </h2>

The info for developers is available at the README of the GitHub repository (link above).

<h2>Contacts</h2>

Contact me on davide.basile@isti.cnr.it if you have questions.


<h2>References</h2>

[COORD21] Basile, D., ter Beek, M.H., 2021, June. A Clean and Efficient Implementation of Choreography Synthesis for Behavioural Contracts.
In Proceedings of the 23rd IFIP WG 6.1 International Conference, COORDINATION 2021, pages 225-238,
https://doi.org/10.1007/978-3-030-78142-2_14
(pdf at https://openportal.isti.cnr.it/data/2021/454603/2021_454603.postprint.pdf)

[SCICO20] Basile, D., ter Beek, M.H., Degano, P., Legay, A., Ferrari, G.L., Gnesi, S. and Di Giandomenico, F., 2020. Controller synthesis of service contracts with variability. Science of Computer Programming, vol. 187, pp. 102344.
https://doi.org/10.1016/j.scico.2019.102344 (Open Access)

[LMCS20] Basile, D., ter Beek, M.H. and Pugliese, R., 2020. Synthesis of Orchestrations and Choreographies: Bridging the Gap between Supervisory Control and Coordination of Services. Logical Methods in Computer Science, vol. 16(2), pp. 9:1 - 9:29.
https://doi.org/10.23638/LMCS-16(2:9)2020 (Open Access)

[SPLC18] Basile, D., ter Beek, M.H. and Gnesi, S., 2018, September. Modelling and Analysis with Featured Modal Contract Automata. In Proceedings of the 22nd International Systems and Software Product Line Conference (SPLC'18), Volume 2. ACM, pp. 11-16.
https://doi.org/10.1145/3236405.3236408
(pdf at http://openportal.isti.cnr.it/data/2018/391612/2018_391612.postprint.pdf)

[SPLC17] Basile, D., Di Giandomenico, F. and Gnesi, S., 2017, September. FMCAT: Supporting Dynamic Service-based Product Lines. In Proceedings of the 21st International Systems and Software Product Line Conference (SPLC'17), Volume B. ACM, pp. 3-8.
https://doi.org/10.1145/3109729.3109760
(pdf at http://openportal.isti.cnr.it/data/2017/386222/2017_386222.postprint.pdf)

[DSPL17] Basile, D., ter Beek, M.H., Di Giandomenico, F. and Gnesi, S., 2017, September. Orchestration of Dynamic Service Product Lines with Featured Modal Contract Automata. In Proceedings of the 21st International Systems and Software Product Line Conference (SPLC'17), Volume B. ACM, pp. 117-122.
https://doi.org/10.1145/3109729.3109741
(pdf at http://openportal.isti.cnr.it/data/2017/376406/2017_376406.postprint.pdf)

[MOD17] Basile, D., Di Giandomenico, F. and Gnesi, S., 2017, February. Enhancing Models Correctness through Formal Verification: A Case Study from the Railway Domain. In Proceedings of the 5th International Conference on Model-Driven Engineering and Software Development (MODELSWARD'17), Volume 1. SciTePress, pp. 679-686.
https://doi.org/10.5220/0006291106790686
(pdf at http://openportal.isti.cnr.it/data/2017/386223/2017_386223.preprint.pdf)

[FORTE16] Basile, D., Degano, P., Ferrari, G.L. and Tuosto, E., 2016, June. Playing with our CAT and communication-centric applications. In International Conference on Formal Techniques for Distributed Objects, Components, and Systems (pp. 62-73).
https://doi.org/10.1007/978-3-319-39570-8_5

[JLAMP16] Basile, D., Degano, P., Ferrari, G.L. and Tuosto, E., 2016. Relating two automata-based models of orchestration and choreography. Journal of logical and algebraic methods in programming, 85(3), pp.425-446.
https://doi.org/10.1016/j.jlamp.2015.09.011

[LMCS16] Ferrari, G.L., Degano, P. and Basile, D., 2016. Automata for specifying and orchestrating service contracts. Logical methods in computer science, 12.
https://doi.org/10.2168/LMCS-12(4:6)2016  (Open Access)



<!--
<h2>Package Structure</h2>

The structure of the Contract Automata Tool Library is composed of two  packages:

**automaton** This package and the others contained are the core of the tool. 
A CA is defined by the class `MSCA`. 
Several operations are available: composition, projection, union, 
synthesis of  orchestration, choreography, or most permissive controller.
These operations are extending corresponding functional interfaces and are using the class MSCA.
The Java classes `DataConverter` and `MxeConverter`, implementing 
the interface `MSCAConverter`,   are used for the storing of file descriptors.
There are two types of formats: 
An automaton is stored in either a readable textual representation
(`*.data`) or an XML format (`*.mxe)`. The `*.data` format can be 
used by any textual editor and it consists of a textual
declaration of states and transitions of each automaton. The XML
representation of a contract (`*.mxe`) is used by the GUI (mxGraph) for saving and
loading CA. This file descriptor also stores information related to
the graphical visualization of the CA.  
A JsonConverter is also under construction.
Below are the class diagram of the packages automatically generated by Sparx Enterprise Architect.

![The class diagram of contractAutomata package](./doc/contractAutomata_classdiagram.png)

![The class diagram of contractAutomata.converters package](./doc/contractAutomata.converters_classdiagram.png)

![The class diagram of contractAutomata.operators package](./doc/contractAutomata.operators_classdiagram.png)

![The class diagram of contractAutomata.requirements package](./doc/contractAutomata.requirements_classdiagram.png)


**family** 
This package contains the classes `Family` containing a set of products  `Product` composing a given product line.  
`FamilyConverter` is an interface for storing and loading a family, either as `*.prod`  textual description of a product line, as well as for importing 
an `.xml` description of a feature model designed in FeatureIDE, whose products have already been generated by FeatureIDE. 
When importing from FeatureIDE, the sub-families are also generated, called partial products.
`Family` organises the products into a partial order (variable `po`), and there are methods for accessing adjacent products 
of a product in the partial ordering relation.
Each object of  `Product` contains two sets of features (required and forbidden) described in `Feature`.
The class `FMCA` contains an MSCA object and its corresponding Family. 
`FMCA`  contains the methods for computing the products respecting validity of a family (see references JSCP2020), 
for computing the canonical products and their union  (the service product line).
The synthesis of the orchestration of a product is an operation implementing a functional interface and using both `Product` and `MSCA`.
The wiki contains an excerpt from a published paper regarding the product lines functionalities of the tool.

![The class diagram of family package](./doc/family_classdiagram.png)

![The class diagram of family.converters package](./doc/family.converters_classdiagram.png)
-->
