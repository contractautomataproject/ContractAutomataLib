# n number of nodes
# m number of actions
param n;
param m;
param K;
param final; #final node
set N := {1..n};
set M := {1..m};
param t{N,N};
param a{N,N,M};
var x_t{N,N} >=0 integer;
var z_t{N,N,N} >=0;
var gamma;
var p{N} binary;
var wagreement;

#flow constraints
subject to Flow_Constraints {node in N}: 
	 sum{i in N}( x_t[i,node]*t[i,node] ) - sum{i in N}(x_t[node,i]*t[node,i]) = 
	 if (node == 1) then  -1
	 else if (node == final)  then 1
	 else 0;
;

subject to p1{node in N}: p[node] <= sum{i in N}( x_t[node,i]*t[node,i]);
subject to p2{node in N}: sum{i in N}( x_t[node,i]*t[node,i]) <= p[node]*K;


#subject to p3{snode in N}:  p[snode] = round((1/(1+exp(-10000*(sum{i in N}( x_t[snode,i]*t[snode,i])))))*2-1);

subject to Auxiliary_Flow_Constraints {snode in N diff {1},node in N}:
	sum{i in N}( z_t[snode,i,node]*t[i,node] ) - sum{i in N}(z_t[snode,node,i]*t[node,i]) =
	if (node == 1) then    - p[snode]
	else if (node == snode)  then  p[snode]
	else 0;
	
subject to Auxiliary_Flow_Constraints2{i in N, j in N,snode in N}:
	z_t[snode,i,j]*t[i,j] <= x_t[i,j]*t[i,j];
	
	
subject to threshold_constraint {act in M}: sum{i in N,j in N} x_t[i,j]*t[i,j]*a[i,j,act] >= gamma;

#subject to ga: gamma<=K;

#subject to wag: wagreement = round(1/(1+exp(-100*(gamma-0.5))));

#objective function
maximize cost: gamma;