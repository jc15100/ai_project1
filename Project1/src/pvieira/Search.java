package pvieira;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author jc15100
 */
public class Search {
    public enum NodeType {
        WALL, FREE, EXPLORED, STEP, ITEM,
        ZERO, ONE, TWO, THREE, FOUR, FIVE, 
        SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN,
        TWELVE, THIRTEEN, FOURTEEN, FIFTEEN
    }
    
    public static final int DEFAULT_COST = 1;
    public static final int EUCLIDEAN = 0;
    public static final int MANHATTAN = 1;
    public static final double D = 1.0;
    public static final double P = 1 / 50;
    public static final State NO_PATH = null;
    public static final GraphState NO_ORDER = null;
    public static final String START = "ENTRANCE";
    public static final String END = "CHECKOUT";
    
    private HashMap<State, State> path = new HashMap<State, State>();
    private HashMap<String, String> order = new HashMap<String, String>();
    private NodeType[][] store;
    private ArrayList<Edge> edges;
    private LinkedList<String> items;
    private ArrayList<Edge> cpy_edges;
    
    /*perform search initialization given an ASCII store map*/
    public Search(File map, ArrayList<Edge> eds, LinkedList<String> its) {
        edges = eds;
        items = its;
        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(map));
        } catch (Throwable e) {
            System.out.println("Error opening/reading the file: " + e.getMessage());
            System.exit(1);
        }

        try {
            int x_dim = Integer.parseInt(br.readLine());
            int y_dim = Integer.parseInt(br.readLine());

            store = new NodeType[y_dim][x_dim];

            for (int y = 0; y < y_dim; y++) {
                String line = br.readLine();
                for (int x = 0; x < line.length(); x++) {
                    switch (line.charAt(x)) {
                        case ' ':
                            store[y][x] = NodeType.FREE;
                            break;
                        case '#':
                            store[y][x] = NodeType.WALL;
                            break;
                    }
                }
            }
        } catch (Throwable e) {
            System.out.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
    }

    /*perform A* search given a start(x0, y0) and a goal(x1,y1)*/
    public State A_star(int x0, int y0, int x1, int y1) {
        ArrayList<State> frontier = new ArrayList<State>();
        Set<State> explored = new HashSet<State>();

        State start = new State(DEFAULT_COST, y0, x0);
        Point goal = new Point(y1, x1);

        frontier.add(start);

        while (!frontier.isEmpty()) {
            //pick least cost from frontier; heuristic used here
            State current = this.removeLeastCost(frontier, goal);

            //test for goal
            if (current.getLocation().getX() == x1 && current.getLocation().getY() == y1) {
                //System.out.println("Explored:" + explored.size());
                return current;
            }
            //add to explored set
            explored.add(current);
            //store[(int)current.getLocation().getY()][(int)current.getLocation().getX()] = NodeType.EXPLORED;

            //get a set of neighbors around
            Set<State> neighbors = current.getNeighbors();

            //check all around current
            for (State e : neighbors) {
                //if the neighbor is not a wall, process it
                if (!store[(int) e.getLocation().getY()][(int) e.getLocation().getX()].equals(NodeType.WALL)) {
                    //set cost of neighbor as cost to get there
                    int g = e.getCost() + current.getCost();
                    e.setCost(g);

                    //add to priority queue
                    if (!explored.contains(e) && !frontier.contains(e)) {
                        frontier.add(e);
                        path.put(e, current);
                    }

                    if (frontier.contains(e)) {
                        int index = frontier.indexOf(e);
                        if (this.findTotalCost(frontier.get(index), goal) > this.findTotalCost(e, goal)) {
                            //replace state with lower alternative
                            frontier.remove(index);
                            frontier.add(e);
                            path.put(e, current);
                        }
                    }
                }
            }
        }
        return NO_PATH;
    }

    /*calculate the total cost for a state given a goal; includes heuristic*/
    public double findTotalCost(State s, Point goal) {
        return (s.getCost() + h_function(s.getLocation(), goal.getLocation(), MANHATTAN));
    }

    /*given a list of states, remove state with least total cost to goal*/
    private State removeLeastCost(ArrayList<State> f, Point goal) {
        State min = null;
        for (State s : f) {
            //heuristic guides search of min cost
            if (min == null || (this.findTotalCost(s, goal) < this.findTotalCost(min, goal))) {
                min = s;
            }
        }
        f.remove(min);
        return min;
    }

    /*after A* finds a goal, connects explored states to build path*/
    public int buildSolution(Point from, Point to) {
        State init = new State(1, (int) from.getY(), (int) from.getX());
        /*track time elapsed to find solution*/
        long start = System.currentTimeMillis();
        State goal = this.A_star((int) from.getX(), (int) from.getY(), (int) to.getX(), (int) to.getY());
        //System.out.println("Elapsed A* Search Time: " + (System.currentTimeMillis() - start));

        int path_length = 0;

        //show init on map
        store[(int) init.getLocation().getY()][(int) init.getLocation().getX()] = NodeType.STEP;
        while (!goal.equals(init)) {
            //show steps on map
            store[(int) goal.getLocation().getY()][(int) goal.getLocation().getX()] = NodeType.STEP;

            goal = path.get(goal);
            path_length++;
            
        }
        
        //System.out.println("Shortest Path Length: " + path_length);
        return path_length;
    }

    /*draw the path found on the ASCII map*/
    private void showPathOnMap(ArrayList<String> o, HashMap<String, Point> il) {
    	int i = 0;
		store[il.get(o.get(i)).y][il.get(o.get(i)).x] = NodeType.TEN; i++;
		store[il.get(o.get(i)).y][il.get(o.get(i)).x] = NodeType.NINE; i++;
		store[il.get(o.get(i)).y][il.get(o.get(i)).x] = NodeType.EIGHT; i++;
		store[il.get(o.get(i)).y][il.get(o.get(i)).x] = NodeType.SEVEN; i++;
		store[il.get(o.get(i)).y][il.get(o.get(i)).x] = NodeType.SIX; i++;
		store[il.get(o.get(i)).y][il.get(o.get(i)).x] = NodeType.FIVE; i++;
		store[il.get(o.get(i)).y][il.get(o.get(i)).x] = NodeType.FOUR; i++;
		store[il.get(o.get(i)).y][il.get(o.get(i)).x] = NodeType.THREE; i++;
		store[il.get(o.get(i)).y][il.get(o.get(i)).x] = NodeType.TWO; i++;
		store[il.get(o.get(i)).y][il.get(o.get(i)).x] = NodeType.ONE; i++;
		store[il.get(o.get(i)).y][il.get(o.get(i)).x] = NodeType.ZERO; i++;
        for (int y = 0; y < store.length; y++) {
            for (int x = 0; x < store.length; x++) {
                String map = null;
                switch (store[y][x]) {
                    case WALL:
                        map = "▓";
                        break;
                    case FREE:
                        map = " ";
                        break;
                    case STEP:
                        map = "*";
                        break;
                    case EXPLORED:
                        map = ".";
                        break;
                    case ZERO:
                    	map = "0";
                    	break;
                    case ONE:
                    	map = "1";
                    	break;
                    case TWO:
                    	map = "2";
                    	break;
                    case THREE:
                    	map = "3";
                    	break;
                    case FOUR:
                    	map = "4";
                    	break;
                    case FIVE:
                    	map = "5";
                    	break;
                    case SIX:
                    	map = "6";
                    	break;
                    case SEVEN:
                    	map = "7";
                    	break;
                    case EIGHT:
                    	map = "8";
                    	break;
                    case NINE:
                    	map = "9";
                    	break;
                    case TEN:
                    	map = "A";
                    	break;
                }
                System.out.print(map);
            }
            System.out.println("");
        }
    }

    /*perform A* to solve TSP given a list of items and their locations in a store*/
    public GraphState A_star() {

        ArrayList<GraphState> frontier = new ArrayList<GraphState>();
        Set<GraphState> explored = new HashSet<GraphState>();

        GraphState current = new GraphState(0, 0, START);
        frontier.add(current);
        
        //build copy of edge list for future use in cost determination
        cpy_edges = new ArrayList<Edge>(edges);
        
        while (!frontier.isEmpty()) {
            //remove least cost 
            GraphState old = this.removeLeastCost(frontier, current);

            //only add to order those which are selected as current
            if (!old.getItem().equals(START)) {
                order.put(old.getItem(), current.getItem());
            }
            current = old;

            //test for goal
            if (current.getItem().equals(END)) {
                return current;
            }

            //add to explored
            explored.add(current);

            ArrayList<GraphState> successors = this.getSuccessors(current.getItem());
            for (GraphState s : successors) {

                //save internal costs of succesors for later comparison
                s.setGCost(s.getGCost() + current.getGCost());
                s.setCost(this.findTotalCost(s, current));

                //add to frontier
                if (!explored.contains(s) && !frontier.contains(s)) {
                    frontier.add(s);
                }

                //check to update frontier contents with lower cost alternative
                if (frontier.contains(s)) {
                    int index = frontier.indexOf(s);
                    if (frontier.get(index).getCost() > s.getCost()) {
                        frontier.remove(index);
                        frontier.add(s);
                    }
                }
            }
            this.removeNode(current.getItem(), edges);
        }
        return NO_ORDER;
    }

    /*get a list of unvisited items from the current one*/
    private ArrayList<GraphState> getSuccessors(String current) {
        ArrayList<GraphState> successors = new ArrayList<GraphState>();
        for (Edge e : edges) {
            if (e.from().equals(current)) {
                successors.add(new GraphState(0, e.cost(), e.to()));
            }
        }
        return successors;
    }

    /*remove least cost item; special considerations to ensure goal is visited last*/
    private GraphState removeLeastCost(ArrayList<GraphState> f, GraphState current) {
        GraphState min = null;
        for (GraphState s : f) {
            if ((min == null || s.getCost() < min.getCost())) {
                if (!s.getItem().equals(END) || f.size() == 1) {
                    min = s;
                }
            }
        }
        f.remove(min);
        return min;
    }

    /*find total cost for a current item given itself and its parent*/
    private int findTotalCost(GraphState s, GraphState c) {
        /*         g(n)      +         d_n(n)          +         MST(n)     +     dc_n(n) */
        return s.getGCost() + this.findNearestItem(c) + this.getMSTCost(c) + this.findNearestToCheckOut(s);
    }

    /*gives MST Cost for remaining nodes not including current one*/
    private int getMSTCost(GraphState s) {
        //make copy of global list of edges/items and remove current one from consideration
        ArrayList<Edge> reduced = new ArrayList<Edge>(edges);
        LinkedList<String> less = new LinkedList<String>(items);

        this.removeNode(s.getItem(), reduced);
        less.remove(s.getItem());

        //calculate MST cost
        MinItemsTree mst = new MinItemsTree(reduced, less);
        return mst.getMinTreeCost(mst.minSpanTree());
    }

    /*remove all edges that current node s is a part of*/
    private void removeNode(String s, ArrayList<Edge> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).from().equals(s) || list.get(i).to().equals(s)) {
                list.remove(i);
                i--;
            }
        }
    }

    /*gives d_n(n): distance to nearest item from current one*/
    private int findNearestItem(GraphState s) {
        int min = 0;
        for (Edge e : edges) {
            if (e.from().equals(s.getItem())) {
                if (min == 0 || e.cost() < min) {
                    min = e.cost();
                }
            }
        }
        return min;
    }

    /*gives dc_n(n): distance to checkout item nearest to it not including current one*/
    private int findNearestToCheckOut(GraphState s) {
        int min = 0;
        for (Edge e : edges) {
            if (e.to().equals(END)) {
                if (!e.from().equals(s.getItem())) {
                    if (min == 0 || e.cost() < min) {
                        min = e.cost();
                    }
                }
            }
        }
        return min;
    }

    /*after A* finds a goal, connects explored items to make TSP order*/
    public ArrayList<String> buildSolution(String goal) {
        ArrayList<String> ordered = new ArrayList<String>();
        
        int leastOrderCost = 0;
        
        //System.out.println(goal);
        while (!goal.equals(START)) {
            ordered.add(goal);
            leastOrderCost += this.findEdgeCost(goal, order.get(goal));
 
            goal = order.get(goal);
            //System.out.println(goal);
        }
        System.out.println("LEAST ORDER COST: " + leastOrderCost);
        ordered.add(START);
        return ordered;
    }

    /*calculate heuristic for 2 points; scales heuristic to break ties*/
    public double h_function(Point p1, Point p2, int type) {
        if (type == EUCLIDEAN) {
            return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
        } else {//return MANHATTAN distance
            double h = Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
            return (h *= (1.0 + P));
        }
    }

    /*find cost of edges for least order cost calculation*/
    private int findEdgeCost(String from, String to){
        for(Edge e: cpy_edges){
            if(e.from().equals(from) && e.to().equals(to)){
                return e.cost();
            }
        }
        return 0;
    }
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        
         if (args.length != 1) {
            System.out.println("Error: missing map filename or more arguments than expected!");
            System.exit(1);
        }

        File map = new File(args[0]);
        HashMap<String, Point> toShop = new HashMap<String, Point>();

        toShop.put("fg", new Point(28, 3));
        toShop.put("milk", new Point(4, 15));
        toShop.put("hotdogs", new Point(13, 20));
        toShop.put("bread", new Point(16, 22));
        toShop.put("juice", new Point(25, 25));
        toShop.put("eggs", new Point(25, 2));
        toShop.put("cereal", new Point(4, 25));
        toShop.put("CHECKOUT", new Point(28, 2));
        toShop.put("ENTRANCE", new Point(1, 1));
        toShop.put("beans", new Point(13, 8));
        toShop.put("icecream", new Point(22, 13));
        //toShop.put("rice", new Point(1, 28));
        //toShop.put("water", new Point(2, 28));
        //toShop.put("chips", new Point(4, 28));*/
        Set<String> keys = toShop.keySet();
        ArrayList<Edge> edges = new ArrayList<Edge>();
        LinkedList<String> items = new LinkedList<String>();
        
        Search test = new Search(map, edges, items);
        
        for (String from : keys) {
            //populate items LinkedList for MST
            items.add(from);
            for (String to : keys) {
                //create edges from every item to every other item in the list
                if (!from.equals(to) && !(from.equals("ENTRANCE") && (to.equals("CHECKOUT")))
                        && !(from.equals("CHECKOUT") && (to.equals("ENTRANCE")))) {
                    edges.add(new Edge(from, to, (int) test.h_function(toShop.get(from), toShop.get(to), MANHATTAN)));
                }
            }
        }
        
        /* for(Edge e: edges){
         System.out.println(e);
         }
         */ 
        
        String output = test.A_star().getItem();
        ArrayList<String> order = test.buildSolution(output);
       
        System.out.println("SHOPPING ORDER: ");
        int totalShoppingDistance = 0;
        int step = 0;
        for (int i = order.size() - 1; i > 0; i--) {
            System.out.println("\t"+ step++ +"-"+ order.get(i));
            totalShoppingDistance += test.buildSolution(toShop.get(order.get(i)), toShop.get(order.get(i - 1)));
        }
        System.out.println("\t"+ step + "-" + order.get(0));
        System.out.println("\nSHOPPING MAP: ");
        test.showPathOnMap(order, toShop);
        System.out.println("\nTOTAL SHOPPING DISTANCE: " + totalShoppingDistance + " steps.");
        System.out.println("TOTAL ELAPSED TIME: " + (System.currentTimeMillis() - start) + " ms.\n");
    }
}
