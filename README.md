[![Build Status](https://travis-ci.com/davidebasile/ContractAutomataLib.svg?branch=code-cleaning)](https://travis-ci.com/davidebasile/ContractAutomataLib)
[![Coverage Status](https://coveralls.io/repos/github/davidebasile/ContractAutomataLib/badge.svg?branch=code-cleaning)](https://coveralls.io/github/davidebasile/ContractAutomataLib?branch=code-cleaning)
[![Code Quality Score](https://www.code-inspector.com/project/23757/score/svg)](https://frontend.code-inspector.com/public/project/23757/ContractAutomataLib/dashboard)
[![Code Grade](https://www.code-inspector.com/project/23757/status/svg)](https://frontend.code-inspector.com/public/project/23757/ContractAutomataLib/dashboard)


<h1>Contract Automata Tool (Lib) </h1>

The Contract Automata Tool is an ongoing basic research activity about implementing 
and experimenting with new developments in the theoretical framework of Contract Automata (CA).
Contract automata are a formalism developed in the research area of foundations for services and distributed 
computing.
They are used for specifying services' interface, called behavioral contracts, 
 as finite-state automata, with functionalities for composing contracts and generating the 
 orchestration or choreography of a composition of services, and with extensions to modalities (MSCA) and product 
 lines (FMCA).

The source code has been redesigned and refactored  in Java 8.

<h2>Usage</h2>

The  package  of the GUI Application has been moved to https://github.com/davidebasile/ContractAutomataApp.
Check that repository for an example of usage of this library for developing a tool for specifying and verifying 
CA.


The following code snippet loads two CA described in `.data` format, compute their composition and synthesise 
an orchestration in agreement (all requests are matched, that is, there are no requests transitions left). 
The composition takes two other arguments. The third is a bound to the maximum length of a path in the composition. 
The second is a pruning predicate, to avoid generating portions of the automaton only reachable by transitions 
satisfying this predicate. Indeed, the orchestration synthesis would prune these transitions anyway.
This allows to scale up to bigger compositions without losing information.
```java
String dir = System.getProperty("user.dir");
BasicMxeConverter bmc = new BasicMxeConverter();
List<MSCA> aut = new ArrayList<>(2);	
aut.add(bdc.importDATA(dir+"/CAtest/BusinessClient.mxe.data"));//loading textual .data description of a CA
aut.add(bdc.importDATA(dir+"/CAtest/Hotel.mxe.data"));
MSCA comp=new CompositionFunction().apply(aut, t->t.getLabel().isRequest(),100);
MSCA orc= new OrchestrationSynthesisOperator().apply(comp);
```

This snippet loads a composition stored in a `.data` format, synthesises the choreography and stores it 
in a .data format.
```java
MSCA aut = bdc.importMxe(dir+"/CAtest/(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe.data");
MSCA cor = ChoreographySynthesisOperator().apply(aut);
bdc.exportDATA(dir+"/CAtest/Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel).data",cor);
```

The following snippet loads an MSCA in `.mxe` format, that is the format used by the GUI app. 
It synthesises an orchestration for a specific product (i.e., a configuration), requiring featured 
`card` and `sharedBathroom`, and forbidding feature `singleRoom`.
```java
MSCA aut = bmc.importMxe(dir+"/CAtest/(BusinessClientxHotelxEconomyClient).mxe");		
Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"singleRoom"});
MSCA orc=new ProductOrchestrationSynthesisFunction().apply(aut,p);	
```

The following snippet imports a product line (i.e., a family) either in the textual format `.prod` or 
as a `.xml` FeatureIDE model. Afterwards, the orchestration of the product line is computed.
```java
FamilyConverter dfc = new DataFamilyConverter();
FamilyConverter ffc = new FeatureIDEfamilyConverter();
Family fam=dfc.importFamily(fileName);// import from .prod textual description of products
Family fam2=ffc.importFamily(dir+"//CAtest//FeatureIDEmodel//model.xml"); //import from FeatureIDE model
FMCA faut = new FMCA(bmc.importMxe(dir+"/CAtest/(BusinessClientxHotelxEconomyClient).mxe"),fam);
MSCA controller = faut.getOrchestrationOfFamily();		
```

<h2>License</h2>
The tool is available under Creative Common License 4.0,
 https://github.com/davidebasile/ContractAutomataLib/blob/code-cleaning/license.html.


<h2>Package Structure</h2>

The structure of the Contract Automata Tool is composed of two  packages:

**contractAutomata** This package (depicted below) is the core of the tool. 
The core is the class `MSCA.java` implementing the algorithm
for synthesising a safe orchestration, choreography, or most permissive controller.
This class offers functionalities for composing automata, for computing the union
of `MSCA` and other utilities. 

![The class diagram of contractAutomata package](./doc/contractAutomata_classdiagram.png)
[fig:the class diagram of contractAutomata package]

The Java classes `BasicDataConverter.java` and `BasicMxeConverter.java`, implementing 
the corresponding interfaces,   are used for the storing of file descriptors.
There are two types of formats: 
An automaton is stored in either a readable textual representation
(`*.data`) or an XML format (`*.mxe)`. The `*.data` format can be 
used by any textual editor and it consists of a textual
declaration of states and transitions of each automaton. The XML
representation of a contract (`*.mxe`) is used by the GUI for saving and
loading CA. This file descriptor also stores information related to
the graphical visualization of the CA.  
A JsonConverter is also under construction.


**family** 
This package contains the classes `Family.java` containing a set of products  `Product.java` composing a given product line.  
`Family.java` is equipped with methods for storing and loading a `*.prod`  textual description of a product line, as well as for importing 
an `.xml` model of a feature model designed in FeatureIDE, whose products have already been generated by FeatureIDE. 
When importing from FeatureIDE, the sub-families are also generated, called partial products.
`Family.java` organises the products into a partial order (variable po), and there are methods for accessing adjacent products 
of a product in the partial ordering relation.
Each object of  `Product.java` contains two sets of features (required and forbidden) described in `Feature.java`.
The class `FMCA.java` contains an MSCA object and its corresponding Family. 
`FMCA.java`  contains the methods for computing the products respecting validity of a family (see references JSCP2020), 
for computing the canonical products, for computing the orchestration of either a product or the service product line, either exploiting the partial order 
or with an enumerative approach. 
The wiki contains an excerpt from a published paper regarding the product lines functionalities of the tool.

![The class diagram of family package](./doc/family_classdiagram.png)[fig:the class diagram of family package]


<h2>Contacts</h2>

If you have any question or want to help contact me on davide.basile@isti.cnr.it.


<h2>Documentation</h2>

Several papers have been published about contract automata and their tool:

Basile, D., ter Beek, M.H., 2021, June. A Clean and Efficient Implementation of Choreography Synthesis for Behavioural Contracts. 
In Proceedings of the 23rd IFIP WG 6.1 International Conference, COORDINATION 2021, pages 225-238, 
https://doi.org/10.1007/978-3-030-78142-2_14


Basile, D., Di Giandomenico, F. and Gnesi, S., 2017, September. FMCAT: Supporting Dynamic Service-based Product Lines. In Proceedings of the 21st International Systems and Software Product Line Conference (SPLC'17), Volume B. ACM, pp. 3-8.
https://doi.org/10.1145/3109729.3109760
(pdf at http://openportal.isti.cnr.it/data/2017/386222/2017_386222.postprint.pdf)

Basile, D., ter Beek, M.H. and Gnesi, S., 2018, September. Modelling and Analysis with Featured Modal Contract Automata. In Proceedings of the 22nd International Systems and Software Product Line Conference (SPLC'18), Volume 2. ACM, pp. 11-16.
https://doi.org/10.1145/3236405.3236408
(pdf at http://openportal.isti.cnr.it/data/2018/391612/2018_391612.postprint.pdf)

Further documentation:

Basile, D., ter Beek, M.H., Degano, P., Legay, A., Ferrari, G.L., Gnesi, S. and Di Giandomenico, F., 2020. Controller synthesis of service contracts with variability. Science of Computer Programming, vol. 187, pp. 102344.
https://doi.org/10.1016/j.scico.2019.102344
(pdf at https://openportal.isti.cnr.it/data/2019/409807/2019_409807.published.pdf)

Basile, D., ter Beek, M.H. and Pugliese, R., 2020. Synthesis of Orchestrations and Choreographies: Bridging the Gap between Supervisory Control and Coordination of Services. Logical Methods in Computer Science, vol. 16(2), pp. 9:1 - 9:29.
https://doi.org/10.23638/LMCS-16(2:9)2020
(pdf at http://openportal.isti.cnr.it/data/2020/423262/2020_%20423262.published.pdf)

Basile, D., ter Beek, M.H., Di Giandomenico, F. and Gnesi, S., 2017, September. Orchestration of Dynamic Service Product Lines with Featured Modal Contract Automata. In Proceedings of the 21st International Systems and Software Product Line Conference (SPLC'17), Volume B. ACM, pp. 117-122.
https://doi.org/10.1145/3109729.3109741
(pdf at http://openportal.isti.cnr.it/data/2017/376406/2017_376406.postprint.pdf)

Basile, D., Di Giandomenico, F. and Gnesi, S., 2017, February. Enhancing Models Correctness through Formal Verification: A Case Study from the Railway Domain. In Proceedings of the 5th International Conference on Model-Driven Engineering and Software Development (MODELSWARD'17), Volume 1. SciTePress, pp. 679-686.
https://doi.org/10.5220/0006291106790686
(pdf at http://openportal.isti.cnr.it/data/2017/386223/2017_386223.preprint.pdf)

