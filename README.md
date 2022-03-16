<!--[![Build Status](https://app.travis-ci.com/davidebasile/ContractAutomataLib.svg?branch=code-cleaning)](https://app.travis-ci.com/davidebasile/ContractAutomataLib)-->
![CodeQL](https://github.com/ContractAutomataProject/ContractAutomataLib/actions/workflows/codeql-analysis.yml/badge.svg)
![Build and Testing](https://github.com/ContractAutomataProject/ContractAutomataLib/actions/workflows/build.yml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/ContractAutomataProject/ContractAutomataLib/badge.svg?branch=main)](https://coveralls.io/github/ContractAutomataProject/ContractAutomataLib?branch=main)
[![Code Quality Score](https://api.codiga.io/project/32018/score/svg)](https://app.codiga.io/public/project/32018/ContractAutomataLib/dashboard)
[![Code Grade](https://api.codiga.io/project/32018/status/svg)](https://app.codiga.io/public/project/32018/ContractAutomataLib/dashboard)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Sonatype Nexus](https://img.shields.io/nexus/r/io.github.davidebasile/ContractAutomataLib?server=https%3A%2F%2Fs01.oss.sonatype.org%2F)](https://s01.oss.sonatype.org/content/repositories/releases/io/github/davidebasile/ContractAutomataLib/0.0.1/)
[![Maven Central Repository](https://img.shields.io/maven-central/v/io.github.davidebasile/ContractAutomataLib)](https://repo1.maven.org/maven2/io/github/davidebasile/ContractAutomataLib/0.0.1/)
[![javadoc](https://javadoc.io/badge2/io.github.davidebasile/ContractAutomataLib/javadoc.svg)](https://javadoc.io/doc/io.github.davidebasile/ContractAutomataLib)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/davidebasile/ContractAutomataLib)

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/summary/new_code?id=ContractAutomataProject_ContractAutomataLib)
[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=ContractAutomataProject_ContractAutomataLib)](https://sonarcloud.io/summary/new_code?id=ContractAutomataProject_ContractAutomataLib)
<!--[![GitHub issues](https://img.shields.io/github/issues/davidebasile/ContractAutomataLib)](https://github.com/davidebasile/ContractAutomataLib/issues)-->

<h1 align=center>Contract Automata Lib </h1>

<h2>About</h2>
The Contract Automata Toolkit is an ongoing basic research activity about implementing 
and experimenting with new developments in the theoretical framework of Contract Automata (CA). 
This repository contains the Contract Automata Library, which is the main repository of the Contract Automata Toolkit.
Contract automata are a formalism developed in the research area of foundations for services and distributed 
computing.
They are used for specifying services' interface, called behavioral contracts, 
 as finite state automata, with functionalities for composing contracts and generating the 
 orchestration or choreography of a composition of services, and with extensions to modalities (MSCA) and product 
 lines (FMCA).

The source code has been redesigned and refactored using the new functionalities introduced with Java 8 (streams, lambda).

<h2>Getting Started</h2>


<h4>User Documentation</h4>

For  information on the usage and installation of the library, check the User Documentation at the Github Page https://contractautomataproject.github.io/ContractAutomataLib/.

<h4> API Documentation</h4>


For the API documentation, check the links below. 
<ul>
  <li> <a href="https://contractautomataproject.github.io/ContractAutomataLib/site/index.htm">Online documentation</a>
</li>
  <li><a href="https://contractautomataproject.github.io/ContractAutomataLib/doc/CAT_Lib_diagrams.pdf">Diagram report (pdf)</a></li>
  <li><a href="https://contractautomataproject.github.io/ContractAutomataLib/doc/CAT_Lib_doc.pdf">Library report (pdf)</a></li>
</ul> 

The javadoc documentation for the release to the Maven Central Repository is available at <a href="https://javadoc.io/doc/io.github.davidebasile/ContractAutomataLib/latest/overview-summary.html">https://javadoc.io/doc/io.github.davidebasile/ContractAutomataLib/latest/overview-summary.html</a>.

The documentation is up-to date to the commit <a href="https://github.com/contractautomataproject/ContractAutomataLib/commit/d92c4b6f73d157b163f0df97d0192dbd6d26b252">d92c4b6 of 8 December 2021</a>. 
The diagrams contained in this documentation have been generated using the Model-based Software Engineering tool Sparx Enterprise Architect. 


<h2>License</h2>
The tool is available under <a href="https://www.gnu.org/licenses/gpl-3.0">GPL-3.0 license</a>.


<h2>Contacts</h2>

Davide Basile - davide.basile@isti.cnr.it.


<h2>Legacy</h2> 
 
If you are reaching this repository from some previous paper on contract automata (e.g., <tt>JSCP2020</tt>, <tt>LMCS2020</tt>), you may be interested in checking an old version of the repository, before its refactoring. 
The latest version of this repository before its refactoring started is available at  https://github.com/ContractAutomataProject/ContractAutomataLib/tree/1b18cd127adc0e535595468c6faae851df3e7177. 
The case studies of these papers can be found in this previous version under the folders <tt>demoJSCP</tt> and <tt>demoLMCS2020</tt>.
The branch <tt>old-backup</tt> pointing to that old version has been removed. 

<img src="https://raw.githubusercontent.com/ContractAutomataProject/ContractAutomataLib/gh-pages/doc/dog%20meme.jpg" width="400"/>
