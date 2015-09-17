This is a prototype implementation of the verification techniques for the properties of weak agreement and weak safety of a Contract Automata.
The linear problems are specified using AMPL. (http://www.ampl.com/DOWNLOADS/)

The main files are:
	weakagreement.mod
	weaksafety.mod

To run an example print from command line "ampl flow.run", the script flow.run will be executed, that will check
if the CA described in the file "flow.dat" admits weak agreement. To check weak safety it suffices to change
in the file "flow.run" the model to weaksafety.mod.

The specification of the CA is given in the file flow.dat. 
An automa is defined through:
	the number of nodes (it is assumed that the node 1 is the initial node)
	the id of the final node (assuming only one final state)
	the cardinality of the alphabet of actions
	the matrix of transitions (0 if there is no transition for the arc (n,m)
							   1 if there is a transition for the arc (n,m) 
							   )
							   
	for each action a 	matrix of labels
								( 1 if the action is an offer in the transition (n,m)
								  -1 if the action is a request in the transition (n,m)
								  0 if the action is a match
								)
	

