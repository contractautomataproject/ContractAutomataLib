![Build and Testing](https://github.com/contractautomataproject/ContractAutomataLib/actions/workflows/build.yml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/ContractAutomataProject/ContractAutomataLib/badge.svg?branch=main)](https://coveralls.io/github/ContractAutomataProject/ContractAutomataLib?branch=main)
 [![Mutation testing badge](https://img.shields.io/endpoint?style=flat&url=https%3A%2F%2Fbadge-api.stryker-mutator.io%2Fgithub.com%2Fcontractautomataproject%2FContractAutomataLib%2Fmain)](https://dashboard.stryker-mutator.io/reports/github.com/contractautomataproject/ContractAutomataLib/main)
<!--[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ContractAutomataProject_ContractAutomataLib&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ContractAutomataProject_ContractAutomataLib)-->
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/0f7dcd94be9141b1b64ef615edbb3991)](https://www.codacy.com/gh/contractautomataproject/ContractAutomataLib/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=contractautomataproject/ContractAutomataLib&amp;utm_campaign=Badge_Grade)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Sonatype Nexus](https://img.shields.io/nexus/r/io.github.contractautomataproject/catlib?server=https%3A%2F%2Fs01.oss.sonatype.org%2F)](https://s01.oss.sonatype.org/content/repositories/releases/io/github/contractautomataproject/catlib/)
[![Maven Central Repository](https://img.shields.io/maven-central/v/io.github.contractautomataproject/catlib)](https://repo1.maven.org/maven2/io/github/contractautomataproject/catlib/)
[![javadoc](https://javadoc.io/badge2/io.github.contractautomataproject/catlib/javadoc.svg)](https://javadoc.io/doc/io.github.contractautomataproject/catlib)
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.6704434.svg)](https://doi.org/10.5281/zenodo.6704434)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/davidebasile/ContractAutomataLib)
[![Open in Code Ocean](https://codeocean.com/codeocean-assets/badge/open-in-code-ocean.svg)](https://codeocean.com/capsule/3787263/tree)
<!--[![GitHub issues](https://img.shields.io/github/issues/davidebasile/ContractAutomataLib)](https://github.com/davidebasile/ContractAutomataLib/issues)-->
<!--[![Code Grade](https://api.codiga.io/project/32018/status/svg)](https://app.codiga.io/public/project/32018/ContractAutomataLib/dashboard)
[![Code Quality Score](https://api.codiga.io/project/32018/score/svg)](https://app.codiga.io/public/project/32018/ContractAutomataLib/dashboard)-->
<!--a href="https://dashboard.stryker-mutator.io/reports/github.com/contractautomataproject/ContractAutomataLib/main">
<img src="https://raw.githubusercontent.com/contractautomataproject/ContractAutomataLib/gh-pages/doc/mutation_badge.svg" />
 </a-->

<h1 align=center>Contract Automata Library </h1>


<h2>About</h2>
This repository contains the Contract Automata Library (CATLib), which is the main repository of the Contract Automata Toolkit. 
Contract automata are a formalism developed in the research area of foundations for services and distributed computing.
They are used for specifying the interfaces of services, called behavioural contracts, 
 as finite state automata, with functionalities for composing contracts and generating the 
 orchestration or choreography of a composition of services, and with extensions to modalities (MSCA) and product 
 lines (FMCA). 
 CATLib implements contract automata and all their operations.
 
 
<h3>News</h3>

The article "Contract Automata Library" has been published in  Science of Computer Programming:

Basile, D. and ter Beek, M.H., 2022. Contract Automata Library. Science of Computer Programming. https://doi.org/10.1016/j.scico.2022.102841

<h2>Getting Started</h2>

<h4>User Documentation</h4>

For  information on the usage and installation of the library, check the User Documentation at the Github Page https://contractautomataproject.github.io/ContractAutomataLib/.

<h4> API Documentation</h4>

For the API documentation, check the links below.

This documentation is up-to-date to the commit <a href="https://github.com/contractautomataproject/ContractAutomataLib/tree/4e1044e4d4f0daf192f7e040d386a01153a4349e">4e1044e</a> of 29 April 2022.


The following documentation has  been created using the Model-based Software Engineering tool Sparx Enterprise Architect:
<ul>
  <li> <a href="https://contractautomataproject.github.io/ContractAutomataLib/site/index.htm">Online documentation</a>. A navigable site visualizing the packages and class diagrams of the project.
</li>
  <li><a href="https://contractautomataproject.github.io/ContractAutomataLib/doc/CAT_Lib_diagrams.pdf">Diagram report (pdf)</a>. Contains all the diagrams of the project. </li>
  <li><a href="https://contractautomataproject.github.io/ContractAutomataLib/doc/CAT_Lib_doc.pdf">Library report (pdf)</a>. Contains information on all classes and their members (similar to javadoc).</li>
  <li><a href="https://contractautomataproject.github.io/ContractAutomataLib/doc/CATLib_Packages.pdf">Packages report (pdf)</a>. Document containing a description of the packages and the classes.</li>
</ul> 

The javadoc documentation for the release to the Maven Central Repository is available at <a href="https://javadoc.io/doc/io.github.contractautomataproject/catlib">https://javadoc.io/doc/io.github.contractautomataproject/catlib</a>.

<h2>License</h2>
The tool is available under <a href="https://www.gnu.org/licenses/gpl-3.0">GPL-3.0 license</a>.


<h2>Branches</h2>
This is the main branch of the repository. 
The branch <tt>gh-pages</tt> contains the GitHub page of this repository. 

<h2>Contacts</h2>

Davide Basile - davide.basile@isti.cnr.it.


<h2>Legacy</h2> 
<p>In previous iterations this software was called <b>CAT</b> (Contract Automata Tool). Later on, with the addition of support for product lines, the software's name was extended to <b>FMCAT</b> (Featured Modal Contract Automata Tool). It was a monolithic piece of software comprehending both a prototypical implementation of the library and a front-end. The front-end was available both as command line or as a GUI. The command-line front-end has been discontinued. The GUI front-end was later factorised out to another repository (<b>CAT_App</b>), whilst the remaining library is now called <b>CATLib</b>. The set of software supporting contract automata is now called "Contract Automata Toolkit", for continuity with its previous iteration.
</p>
<p>
The source code has been completely rewritten in 2020. It was redesigned using the new functionalities introduced with Java 8 (streams, lambda).
If you are reaching this repository from some previous paper on contract automata (e.g., <tt>JSCP2020</tt>, <tt>LMCS2020</tt>), you may be interested in checking an old version of the repository, before its refactoring. 
The latest version of this repository before its refactoring started is available at  <a href="https://github.com/contractautomataproject/ContractAutomataLib/tree/06c5c32519bb5b67a2e7d331b512cae0aa37a2e1">06c5c32519bb5b67a2e7d331b512cae0aa37a2e1</a>. 
The case studies of these papers can be found in this previous version under the folders <tt>demoJSCP</tt> and <tt>demoLMCS2020</tt>. 
The repository at <a href="https://github.com/davidebasile/experimentsReproducibilityCoordination2021">https://github.com/davidebasile/experimentsReproducibilityCoordination2021</a> contains a comparison of the two versions of the library (before and after the refactoring). 
</p>

<!--img src="https://raw.githubusercontent.com/contractautomataproject/ContractAutomataLib/gh-pages/doc/dog%20meme.jpg" width="400"/-->

