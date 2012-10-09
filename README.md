ai_project1
===========

#################
BASIC INFORMATION
#################
Project Members: Peter Vieira, Juan-Carlos Garcia

Application Name: Grocery Shopping

Release Date:  10/09/2012

Contact Information: pete.vieira@gmail.com, juanky.garcia15@gmail.com

########################
APPLICATION DESCRIPTION:
########################
	Takes a list of grocery items, including the "entrance" and "checkout", each with x,y coordinates corresponding to locations in a store, with consideration of where walls and obstacles are. The edge costs between all the items are calculated using Manhattan distances. The edges are then input into an A* algorithm that computes the least cost order of the items, starting with the "entrance" and ending with the "checkout." This optimal order is given as the parameter to a second A* algorithm which determines the shortest path between each item pair in order and returns these paths on a map, along with path cost and execution time. All the outputs are done through the console of whatever environment is used.

###################
CLASS DESCRIPTIONS:
###################

-----------
Search.java
-----------
	This class contains the main method which runs the program with one argument to specify the map file. It creates a hashmap of the grocery items and their x,y coordinates, an array list of their edges and a linked list of the string names of the items. It then runs the first A* algorithm to obtain the least cost order of the items. It inputs this into a second A* algorithm by calling buildSolution(), which computes the least cost path to each item in order. The map of the paths is then printed.

---------------
GraphState.java
---------------
	This class contains the state information for nodes for the first A* algorithm in Search.java called "public GraphState A_star()". A GraphState consists of the total cost of the node, the path cost g(n) of the node and its name.

----------
State.java
----------
	This class contains the state information for nodes for the second A* algorithm in Search.java called "public State A_star(int x0, int y0, int x1, int y1)". A State consists of the total cost of the node, its location and its name.

---------
Edge.java
---------
	This class contains the information for each edge in the edges array list. An edge consists of its starting location "from," its ending location "to" and its cost.

-----------------
MinItemsTree.java
-----------------
	This class calculates the minimum spanning tree given a set of edges and a list of items. It returns the cost of the minimum spanning tree.

----------------
DisjointSet.java
----------------
	This class is a helper class for MinItemsTree.java. It ensures that edges in the minimum spanning tree don't create cycles
