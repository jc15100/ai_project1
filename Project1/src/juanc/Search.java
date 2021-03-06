package juanc;

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
        WALL, FREE, EXPLORED, STEP
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
        store[(int) from.getY()][(int) from.getX()] = NodeType.STEP;
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
    private char[][] showPathOnMap() {
        char[][] map = new char[store.length][store.length];
        for (int y = 0; y < store.length; y++) {
            for (int x = 0; x < store.length; x++) {
                
                switch (store[y][x]) {
                    case WALL:
                        map[y][x] = '█';
                        break;
                    case FREE:
                        map[y][x] = ' ';
                        break;
                    case STEP:
                        map[y][x] = '▒';
                        break;
                    case EXPLORED:
                        map[y][x] = '.';
                        break;
                }
            }
        }
        return map;
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
    private int findTotalCost(GraphState current, GraphState parent) {
        /*         g(n)      +         d_n(n)          +         MST(n)     +     dc_n(n) */
        return current.getGCost() + this.findNearestItem(parent) + this.getMSTCost(current) + this.findNearestToCheckOut(current);
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
        
        /*Case for Complicated cased in Kroger-based map
        toShop.put("ground beef", new Point(21, 27));
        toShop.put("milk", new Point(27, 25));
        toShop.put("hotdogs", new Point(13, 27));
        toShop.put("bread", new Point(13, 9));
        toShop.put("juice", new Point(11, 22));
        toShop.put("eggs", new Point(28, 22));
        toShop.put("cereal", new Point(14, 20));
        toShop.put("CHECKOUT", new Point(23, 1));
        toShop.put("ENTRANCE", new Point(1, 1));
        toShop.put("beans", new Point(11, 9));
        toShop.put("icecream", new Point(23, 20));
        toShop.put("rice", new Point(11, 11));
        toShop.put("chips", new Point(8, 24));
        toShop.put("ham", new Point(16, 27));
        toShop.put("salt", new Point(17, 18));
        toShop.put("tomato", new Point(4, 12));
        toShop.put("lettuce", new Point(1, 12));
        toShop.put("onions", new Point(4, 6));
            /*
        toShop.put("hotdogs", new Point(1,1));
        toShop.put("milk", new Point(28, 1));
        toShop.put("ham", new Point(1, 28));
        toShop.put("juice", new Point(15, 28));
        toShop.put("orange", new Point(20, 15));
        toShop.put("CHECKOUT", new Point(14, 27));
        toShop.put("ENTRANCE", new Point(28, 28));

        /*40 Random Assignments
        toShop.put("bread", new Point(22, 17));
        toShop.put("CHECKOUT", new Point(43, 11));
        toShop.put("ENTRANCE", new Point(48, 2));
        toShop.put("beans", new Point(11, 9));
        toShop.put("icecream", new Point(5, 8));
        toShop.put("water", new Point(2, 5));
        toShop.put("pototoes", new Point(1, 35));
        toShop.put("apples", new Point(35, 35));
        toShop.put("CHECKOUT", new Point(110, 54));
        toShop.put("ENTRANCE", new Point(236, 110));
        toShop.put("c", new Point(153,151));
        toShop.put("d", new Point(227, 49));
        toShop.put("e", new Point(307, 176));
        toShop.put("f", new Point(220, 211));
        toShop.put("g", new Point(341, 90));
        toShop.put("h", new Point(149, 91));
        toShop.put("i", new Point(335, 40));
        toShop.put("j", new Point(371, 150));
        toShop.put("k", new Point(218, 161));
        toShop.put("l", new Point(334, 239));
        toShop.put("m", new Point(148, 227));
        toShop.put("n", new Point(49, 128));
        toShop.put("o", new Point(25, 24));
        toShop.put("p", new Point(25, 46));
        toShop.put("q", new Point(14, 25));
        toShop.put("r", new Point(28, 40));
        toShop.put("s", new Point(20, 25));
        toShop.put("t", new Point(20, 20));
        toShop.put("u", new Point(25, 35));
        toShop.put("v", new Point(48, 35));
        toShop.put("w", new Point(40, 47));
        toShop.put("x", new Point(14, 4));
        toShop.put("y", new Point(28, 24));
        toShop.put("z", new Point(23, 28));
        toShop.put("1", new Point(8, 25));
        toShop.put("2", new Point(45, 45));
        toShop.put("3", new Point(42, 35));
        toShop.put("4", new Point(15, 28));
        toShop.put("5", new Point(18, 47));
        toShop.put("6", new Point(5, 20));
        toShop.put("7", new Point(8, 20));
        toShop.put("8", new Point(15, 15));
        toShop.put("9", new Point(8, 15));
        toShop.put("@", new Point(48, 13));*/
        
        /*15 cities case for TIME COMPARISON*/
        toShop.put("CHECKOUT", new Point(110, 54));
        toShop.put("ENTRANCE", new Point(236, 110));
        toShop.put("c", new Point(153,151));
        toShop.put("d", new Point(227, 49));
        toShop.put("e", new Point(307, 176));
        toShop.put("f", new Point(220, 211));
        toShop.put("g", new Point(341, 90));
        toShop.put("h", new Point(149, 91));
        toShop.put("i", new Point(335, 40));
        toShop.put("j", new Point(371, 150));
        toShop.put("k", new Point(218, 161));
        toShop.put("l", new Point(334, 239));
        toShop.put("m", new Point(148, 227));
        toShop.put("n", new Point(49, 128));
        
        
        
        Set<String> keys = toShop.keySet();
        ArrayList<Edge> edges = new ArrayList<Edge>();
        LinkedList<String> items = new LinkedList<String>();
        
        /*Comparing paper case*/
        /*edges.add(new Edge("ENTRANCE", "C", 5));
        edges.add(new Edge("A", "ENTRANCE", 8));
        edges.add(new Edge("A", "B", 2));
        edges.add(new Edge("A", "C", 7));
        edges.add(new Edge("A", "CHECKOUT", 10));
        edges.add(new Edge("B", "ENTRANCE", 8));
        edges.add(new Edge("B", "A", 2));
        edges.add(new Edge("B", "C", 5));
        edges.add(new Edge("B", "CHECKOUT", 8));
        edges.add(new Edge("C", "ENTRANCE", 5));
        edges.add(new Edge("C", "A", 7));
        edges.add(new Edge("C", "B", 5));
        edges.add(new Edge("C", "CHECKOUT", 5));
        edges.add(new Edge("CHECKOUT", "A", 10));
        edges.add(new Edge("CHECKOUT", "B", 8));
        edges.add(new Edge("CHECKOUT", "C", 5));

        items.add("ENTRANCE");
        items.add("A");
        items.add("B");
        items.add("C");
        items.add("CHECKOUT");*/

        
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
        
        //find TSP order for list of items
        String output = test.A_star().getItem();
        ArrayList<String> order = test.buildSolution(output);
       
        System.out.println("SHOPPING ORDER: ");
        int totalShoppingDistance = 0;

        char step = 'A';
        //find shortest path between items in order given
        for (int i = order.size() - 1; i > 0; i--) {
            System.out.println("\t"+ step++ +"-"+ order.get(i));
           // totalShoppingDistance += test.buildSolution(toShop.get(order.get(i)), toShop.get(order.get(i - 1)));
        }
        System.out.println("\t"+ step + "-" + order.get(0));
        /*
        //time keeping ends after finding path between each pair
        long end = System.currentTimeMillis();
        
        /*PRINT OUTPUTS
        //show items initials in the map
        char[][]store = test.showPathOnMap();
        step = 'A';
        for(int i = order.size() -1; i > 0; i--){
            store[(int)toShop.get(order.get(i)).getY()][(int)toShop.get(order.get(i)).getX()] = step++;
        }
        store[(int)toShop.get(order.get(0)).getY()][(int)toShop.get(order.get(0)).getX()] = step++;
        
        //print out shopping map with paths and items shown
        System.out.println("\nSHOPPING MAP: ");
        for(int i =0; i< store.length; i++){
            for(int j = 0; j < store.length; j++){
                System.out.print(store[i][j]);
            }
            System.out.println("");
        }
        //some stats
        System.out.println("\nTOTAL SHOPPING DISTANCE: " + totalShoppingDistance + " steps.");
        System.out.println("TOTAL ELAPSED TIME: " + (end - start) + " ms.\n");*/
    }
}
