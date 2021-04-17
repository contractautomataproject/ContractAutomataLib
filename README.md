<h1>Contract Automata Tool</h1>

The Contract Automata Tool is an ongoing basic research activity about implementing 
and experimenting with new developments in the theoretical framework of contract automata.
Contract automata are a formalism developed in the research area of foundations for services and distributed 
computing.
They are used for specifying services' interface, called behavioral contracts, 
 as finite-state automata, with functionalities for composing contracts and generating the 
 orchestration or choreography of a composition of services, and with extensions to modalities (MSCA) and product 
 lines (FMCA).

The tool is undergoing a refactoring in Java 8 and redesigning.
At the time of writing, the package contractAutomata is almost totally rewritten in Java 8.

<h2>Usage</h2>
The tool has been developed using Eclipse and tested on Windows machines.
The GUI application is based on mxGraph and allows to visualize the automata, edit them, and 
use the main operations.
The main application is in the file App.java, under com.mxgraph.examples.swing.editor package. 
A precompiled App.jar file is available in the root of the project.

<h2>License</h2>
The tool is available under Creative Common License 4.0,
 https://github.com/davidebasile/ContractAutomataTool/blob/code-cleaning/license.html.


<h2>Tutorials</h2>
A first video tutorial is available at https://youtu.be/LAzCEQtYOhU and it shows the usage of the tool for composing automata and compute orchestrations of product lines, using the examples published in JSCP2020.
The directory demoJSCP contains an executable jar and the models used in this tutorial.

The second video tutorial, available at https://youtu.be/W0BHlgQEhIk, shows the computation of orchestrations and choreographies for the examples published in LMCS2020.
The directory demoLMCS2020 contains an executable jar and the models used in this tutorial.

The third video tutorial, available at https://youtu.be/QJjT7f7vlZ4, shows the recent refactoring and improvements of the tool published in Coordination2021.

<h2>Architecture</h2>

The architecture of the Contract Automata Tool is composed of three main packages:


**com.mxgraph** This package contains the Java class
`App.java`. This implements the GUI
of the tool, and it is based on an existing framework called *mxGraph* for
editing graphs in Java. 
The GUI is implemented starting from the BasicGraphEditor available 
in mxGraph.
The other classes are also modifications of the BasicGraphEditor example 
of mxGraph. 
For more info check https://jgraph.github.io/mxgraph/docs/manual_javavis.html.

**contractAutomata** This package (depicted below) is the core of the tool. 
The core is the class `MSCA.java` implementing the algorithm
for synthesising a safe orchestration, choreography, or most permissive controller.
This class offers functionalities for composing automata, for computing the union
of `MSCA` and other utilities. 
The class `FMCA.java` decorates MSCA by adding a method for computing orchestrations 
of a product.
The stand-alone Java class `MSCAIO.java` concerns with the storing of file descriptors. 
There are two types of formats: 
An automaton is stored in either a readable textual representation
(`*.data`) or an XML format (`*.mxe)`. The `*.data` format can be 
used by any textual editor and it consists of a textual
declaration of states and transitions of each automaton. The XML
representation of a contract (`*.mxe`) is used by the GUI for saving and
loading CA. This file descriptor also stores information related to
the graphical visualization of the CA.  


![The class diagram of contractAutomata package](./CATdiagram.png)[fig:the class diagram of contractAutomata package]

**family** This package contains the class `Family.java` that uses another class `Product.java` for
memorising the various products composing a given product line. It
contains the methods for computing the valid products of a family, for
computing the partial order of products and the canonical product. 
It is
also used  for computing the the union of the orchestrations of the canonical
products (i.e., orchestration of the
service product line). 
The package also contains, under the class `Product.java`, the Partial Order 
Generation from a `*.prod` file, computed when the product
files are loaded, where `*.prod`
files contain textual descriptions of products. 
At the moment of writing, the package family has not been refactored to Java 8. 
The wiki contains an excerpt from a published paper regarding the product lines functionalities of the tool.

![The class diagram of family package](./familyDiagram.png)[fig:the class diagram of family package]


<h2>Contacts</h2>

If you have any question or want to help contact me on davide.basile@isti.cnr.it.


<h2>Documentation</h2>

Several papers have been published about contract automata and their tool:

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

Pugliese, R., ter Beek, M.H. and Basile, D., 2020. Synthesis of Orchestrations and Choreographies: Bridging the Gap between Supervisory Control and Coordination of Services. Logical Methods in Computer Science, vol. 16(2), pp. 9:1 - 9:29.
https://doi.org/10.23638/LMCS-16(2:9)2020
(pdf at http://openportal.isti.cnr.it/data/2020/423262/2020_%20423262.published.pdf)

Basile, D., ter Beek, M.H., Di Giandomenico, F. and Gnesi, S., 2017, September. Orchestration of Dynamic Service Product Lines with Featured Modal Contract Automata. In Proceedings of the 21st International Systems and Software Product Line Conference (SPLC'17), Volume B. ACM, pp. 117-122.
https://doi.org/10.1145/3109729.3109741
(pdf at http://openportal.isti.cnr.it/data/2017/376406/2017_376406.postprint.pdf)

Basile, D., Di Giandomenico, F. and Gnesi, S., 2017, February. Enhancing Models Correctness through Formal Verification: A Case Study from the Railway Domain. In Proceedings of the 5th International Conference on Model-Driven Engineering and Software Development (MODELSWARD'17), Volume 1. SciTePress, pp. 679-686.
https://doi.org/10.5220/0006291106790686
(pdf at http://openportal.isti.cnr.it/data/2017/386223/2017_386223.preprint.pdf)