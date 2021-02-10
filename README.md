


![The Architecture of the tool](./CATdiagram.png) [fig:the class diagram of contractAutomata package]


<h1>Contract Automata Tool</h1>

The Contract Automata Tool is an ongoing basic research activity about implementing 
and experimenting with new developments in the theoretical framework of contract automata.
Contract automata are a formalism developed in the  research area of foundations for services and distributed 
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




<h2>Tutorials</h2>
A first video tutorial is available at https://youtu.be/LAzCEQtYOhU  and it shows the usage of the tool for composing automata and compute orchestrations of product lines, using the examples published in JSCP2020.
The directory demoJSCP contains an executable jar and the models used in this tutorial.

The second video tutorial, https://youtu.be/W0BHlgQEhIk, shows the computation of orchestrations and choreographies for the examples published in LMCS2020.
The directory demoLMCS2020 contains an executable jar and the models used in this tutorial.

<h2>Contacts</h2>

If you have any question or want to help contact me on davide.basile@isti.cnr.it.


<h2>Documentation</h2>

The documentation for this tool is available at:

Basile, D., Di Giandomenico, F. and Gnesi, S., 2017, September. FMCAT: supporting dynamic service-based product lines. In Proceedings of the 21st International Systems and Software Product Line Conference-Volume B (pp. 3-8).
https://doi.org/10.1145/3109729.3109760

Basile, D., Beek, M.H.T. and Gnesi, S., 2018, September. Modelling and analysis with featured modal contract automata. In Proceedings of the 22nd International Systems and Software Product Line Conference-Volume 2 (pp. 11-16).
https://doi.org/10.1145/3236405.3236408

Further documentation:

Basile, D., ter Beek, M.H., Degano, P., Legay, A., Ferrari, G.L., Gnesi, S. and Di Giandomenico, F., 2020. Controller synthesis of service contracts with variability. Science of Computer Programming, 187, p.102344.
https://www.sciencedirect.com/science/article/pii/S0167642318302260

Pugliese, R., ter Beek, M.H. and Basile, D., 2020. Synthesis of Orchestrations and Choreographies: Bridging the Gap between Supervisory Control and Coordination of Services. Logical Methods in Computer Science, 16.
https://lmcs.episciences.org/6527

Basile, D., ter Beek, M.H., Di Giandomenico, F. and Gnesi, S., 2017, September. Orchestration of dynamic service product lines with featured modal contract automata. In Proceedings of the 21st International Systems and Software Product Line Conference-Volume B (pp. 117-122).
https://doi.org/10.1145/3109729.3109741

Basile, D., Di Giandomenico, F. and Gnesi, S., 2017, February. Enhancing Models Correctness through Formal Verification: A Case Study from the Railway Domain. In MODELSWARD (pp. 679-686).
https://www.scitepress.org/Link.aspx?doi=10.5220/0006291106790686





<h1>Service-oriented Computing and Contracts</h1>

**What follows is an excerpt of SPLC 2017 publication, recently the tool has been equipped with 
 a choreography synthesis algorithm**
 
Service-oriented computing (SOC)  is a paradigm for distributed
applications based on the publication, discovery and orchestration of
*services*. Services are composed to provide Web applications and can be
reused in different configurations over time.

Through SOC it is possible to build dynamic service-based applications
capable of adapting to changes in the environment or to the resources of
the devices on which they run. Services are usually programmed with
little or no knowledge about clients and other services before being
loosely coupled into networks of collaborating end-user applications.
Therefore the idea to organise them into *dynamic service product lines*
has been explored at different SPLC conferences,
leading to applications for Web stores, smart grids and services as used
in scientific workflows and grid computing  
and interest has been recently revived . Concerning SOC,
*service contracts*  have been
introduced to formally describe the behaviour of services in terms of
their obligations (i.e. *offers* of the service) and their requirements
(i.e. *requests* by the service). Contracts characterise an *agreement*
among services as an orchestration (i.e. a composition) of them based on
the satisfaction of all requirements through obligations. Orchestrations
can dynamically adapt to the discovery of new services and to services
that are no longer available.

Featured modal contract automata (FMCA) have been introduced
  for modelling contract-based dynamic service product
lines, and are an extension of modal service contract
automata  and contract
automata. An FMCA can model either single
services (called *principals*) or compositions of services based on an
orchestrated coordination . The goal of each principal is to
reach an accepting (final) state by matching its requests with
corresponding offers of other principals. Through service contracts it
is possible to characterise the behaviour of an ensemble of services. An
execution is considered safe if all requests are matched by
corresponding offers. Variability mechanisms are available to
distinguish *necessary*  from *permitted* requests. Offers are only
permitted as dictated by agreement. Necessary service requests can be
urgent, greedy or lazy and have, in decreasing order of relevance,
further restrictions on their satisfiability.

Features are identified as service actions, and each  represents a
behavioural product line of services equipped with feature constraints.
Feature models are described as usual, where each product of the product
line is identified as a truth assignment satisfying the corresponding
feature constraints. Contract agreement guarantees the fulfilment of all
feature constraints, all variants of necessary requests and the maximum
number of permitted requests that could be fulfilled without spoiling
the service composition. Contracts adapt to the overall agreement by
renouncing to unsatisfiable, yet permitted requirements.

FMCAT is a prototypical tool implementing the
theory of FMCA. FMCAT organises the products into a partial
order, to efficiently compute all valid products and the orchestration
of the service product line from only a subset of products. FMCAT also
allows to compute the orchestration of a single product and features
both a GUI and a command-line prompt. 


<h2>Tool functionalities</h2>

We consider a simple franchise of Hotel reservation systems and model it
as a service product line. Such a system consists of clients (either
business or economy) interested in booking a room in a Hotel, which
offers either credit card or cash payments and possibly emits an invoice
according to the Hotel feature model described by the formula phi below
(cf. references for more details on this example).
The *orchestration* for the composition
`EconomyClient` `X` `Hotel` `X` `BusinessClient` computed with
 is depicted in the [fig:architecture], specifically for the product
requiring features *invoice* and *card* while forbidding feature *cash*
(cf. product `p2` below), i.e. payments can only be made through credit
card and invoices are required. This orchestration dictates how the
computation evolves: at each transition the states of principals and
their actions are identified, e.g. `BusinessClient` (third service) is
served before `EconomyClient` (first service), and according to the
product no cash payment is performed.

Urgent, greedy, lazy and permitted transitions are specified by the
colour of the corresponding transitions, which are red, orange, green
and blue, respectively. Offer actions are prefixed by `!` while requests
are prefixed by `?`. In our example, the room request (`?room`) of
`BusinessClient` is urgent while the same request for `EconomyClient` is
lazy, accordingly in their orchestration the urgent request is served
before the lazy one.

FMCAT exploits FMCA , and in particular it uses results from Supervisory Control Theory  to
build a safe orchestration of services as the *most permissive
controller* (mpc) of the composition of FMCA. Permitted and necessary
actions are interpreted as controllable and uncontrollable actions,
respectively. Controllable actions can be blocked by the mpc, while this
is not possible for the uncontrollable ones. Indeed, necessary
requirements of all service contracts must be fulfilled for obtaining a
safe orchestration of services. The main
functionalities provided by FMCAT are listed below:


**Import/Export FMCA** FMCAT features both a command-line interface and
a GUI. Accordingly, an FMCA can be specified either through a text file
(extension `*.data`) or through the GUI (extension `*.mxe`). It is
possible to import textual descriptions of FMCA directly into the XML
format for the GUI and vice-versa. In the actual version of the tool,
the graphical arrangement of an FMCA is
not exported to the textual description, where only the information
about states and transitions is kept.


**Contract Composition** the operation of contract composition is an
adaptation of the one available for contract automata. FMCA are composable, and an automaton can
specify either a single service or a service composition. Indeed,
through FMCA it is possible to specify dynamic service product line,
where new services can be added to the whole composition at binding
time. The operator of composition basically interleaves all the actions
of principals, with the only restriction being the case in which two
principals are ready on their corresponding request/offer action: in
this case only their synchronization (called match) will be available.
The FMCA in Figure [fig:architecture] is a composition of three
principals.

**Generation of Partial Order of Products** An FMCA consists of a
behavioural description of a service (i.e. the automaton) together with
a feature model describing the product line. In particular, in FMCA a
text file (extension .prod) specifies each product through its set of
*required* and *forbidden* actions that are, respectively, true and
false atoms of the formula (feature constraints) representing the
feature model. An example of family
description is below, corresponding to the feature model identified by the formula
`phi=((card && !cash) || (cash && !card)) && (!cash ||invoice)`. 
Three products satisfying phi are listed below:

    p2: R={card,invoice} F={cash}; p3: R={card} 
    F={cash,invoice}; p4: R={cash,invoice} F={card}

Note that in FMCA the leaves of the corresponding feature model are
features, and are a subset of service actions. Moreover, FMCAT considers
products where not all variability has been resolved (aka sub-families),
but sufficient to decide whether the formula is satisfied or not. In
this case, the interpretation function (i.e. product) `card`=true,
`cash`=false satisfies phi and has to solve the variability
related to the `invoice` feature: this is the product
`p1: R={card} F={cash}`, identified as a *super-*product of both
`p2` and `p3`. Indeed, required and forbidden actions
of `p1` are included in its sub-products; and the two “top” products are
`p1` and `p4`. FMCAT exploits this ordering
relation (i.e. set inclusion) among products to efficiently verify the
service product line. The partial order of the above
products is automatically generated by FMCAT.


**Verification of Valid Products** Once an FMCA A has been
loaded or imported, together with its partial order of products, all
products that are valid in the FMCA A can identified, i.e.
those where all required features (i.e. actions) are available in
A, and none of the forbidden features is. By relying on the theory of FMCA, it is
possible to identify all such products, potentially exponential in
number, without performing the check for each one of them. Indeed, FMCAT
internally represents the products through a tree data-structure. The
algorithm for this operation basically performs a top-down breadth-first
visit of the tree, where sub-trees rooted in products non-valid in
  A are pruned. It is known that validity of a product of an
FMCA implies validity of all its super-products. Given the
FMCA depicted in Figure [fig:architecture]  and the products in
phi, the products valid in the FMCA are `p1` and `p2` only.


**Computing Canonical Products** Canonical products are those
characterising the whole service product line. All other services of the
given family can be obtained by refinement of some canonical product.
Canonical products are all valid “top” products of the given partial
order, quotiented by their set of forbidden features. FMCAT allows to
identify all canoniFigure [fig:architecture]cal products from an FMCA and its products. In the
above example, `p1` is the only canonical product of the service product
line. Indeed, the orchestration of `p2` is contained into the one of
`p1`.


**Orchestration of a Product** An orchestration of services is the
maximal sub-portion of the FMCA that is *safe*, i.e. all requests of
services are matched by corresponding offers (e.g. the FMCA in
Figure [fig:architecture] is safe). The orchestration, being the mpc from
Supervisory Control Theory, tries to keep the maximum number of
permitted actions, while necessary requests must be matched for reaching
a non-empty orchestration. Urgent, greedy and lazy necessary requests
characterise “when” the request can be matched, and give rise to
different Figure [fig:architecture]. Urgent requests do not allow delays (due to
interleavings generated by the composition), i.e. they are
uncontrollable. For example, the red transition in
Figure [fig:architecture] is matched in the initial state. Greedy
(orange) requests can be delayed as soon as the first match is
available, that is, greedy matches are uncontrollable. Lazy
matches/requests can be controlled by the orchestration, provided that
at least one match is available. For example, in
Figure [fig:architecture], the green request of the first principal
(`EconomyClient`) is served after the red request of the third principal
(`BusinessClient`).

FMCAT computes an orchestration for a single product, where all its
required actions must be available in the orchestration, and none of the
forbidden actions is available.


**Orchestration of a Service Product Line** Through FMCAT it is also
possible to compute the orchestration of a whole service product line.
By exploiting theoretical results from FMCA (see references(, the orchestration
can be computed without iterating through each product. In particular,
the orchestration of the service product line is the union of the
orchestrations of all canonical products. The FMCA in
Figure [fig:architecture] is the orchestration of the canonical product
`p1` and hence it is also the orchestration of the whole service product
line, identified by the feature model in phi.
Indeed, the orchestration of `p2`, in this case, is exactly the one of
`p1` (in general it could be a sub-automaton). If, for example, we would
add a fifth product `p5: R={receipt,invoice}, F={taxi}` (obtained by
modifying the feature model), then this would be another canonical
product and the union of the orchestrations of `p1` and `p5` would be
the mpc of the service product line.

<h2>Architecture</h2>

The architecture of FMCAT is is composed of three main modules:


**User Interface** This module contains the Java class
`App.java`. This implements the GUI
of FMCAT, and it is based on an existing framework called *mxGraph* for
editing graphs in Java. 

**I/O Module** This module contains the stand-alone Java class 
`MSCAIO.java` and concerns with the storing of file descriptors
used by FMCA. There are three types of files used by the tool: `*.prod`
files contain textual descriptions of products as described previously.
An FMCAT is stored in either a readable textual representation
(`*.data`) or an XML format (`*.mxe)`. The `*.data` format is mainly
used by the command-line interface and it consists of a textual
declaration of states and transitions of each automaton. The XML
representation of FMCA (`*.mxe`) is used by the GUI for saving and
loading FMCA. This file descriptor also stores information related to
the graphical visualization of the FMCA. The module also contains, under 
the class `Product.java`, the Partial Order Generation from a `*.prod` file, computed when the product
files are loaded, and it contains the utilities for converting a
description of FMCA into one of the available formats. 



**Core** The core module of FMCAT is composed of, among the others, the
class `MSCA.java` implementing the algorithm
for synthesising a safe orchestration of services for a given product.
This class offers functionalities for composing automata, for computing the union
of `MSCA` (used for synthesising the orchestration of a family) and other
utilities. The class `Family.java` uses another class `Product.java` for
memorising the various products composing a given product line. It
contains the methods for computing the valid products of a family, for
computing the partial order of products and the canonical product. It is
also used by the `FMCA` class for computing the orchestration of the
service product line (i.e. the union of the mpc of the canonical
products).
