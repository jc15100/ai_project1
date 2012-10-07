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
    public static final int DEFAULT_COST = 1;
    public static final int EUCLIDEAN = 0;
    public static final int MANHATTAN = 1;
    public static final double D = 1.0;
    public static final double P = 1/50;
    
    public enum NodeType{ WALL, FREE, GOAL, STEP, ITEM}
    
    public static final State NO_PATH = null;
    public static final GraphState NO_ORDER = null;
    
    private HashMap<State,State> path = new HashMap<State,State>();
    private HashMap<String, String> order = new HashMap<String, String>();
    private NodeType [][] store;
    
    private ArrayList<Edge> edges;
    private LinkedList<String> items;
    
    
    public Search(ArrayList<Edge> e, LinkedList<String> i){
        edges = e;
        items = i;
    }
    
    /*perform search initialization given an ASCII store map*/ 
    public Search(File map){
        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(map));
        } catch (Throwable e) {
            System.out.println("Error opening/reading the file: " + e.getMessage());
            System.exit(1);
        }
        
        try{
            int x_dim = Integer.parseInt(br.readLine());
            int y_dim = Integer.parseInt(br.readLine());
            
            store = new NodeType[y_dim][x_dim];
            
            for(int y = 0; y < y_dim; y++){
                String line = br.readLine();
                for(int x = 0; x < line.length(); x++){
                    switch(line.charAt(x)){
                        case ' ':
                            store[y][x] = NodeType.FREE;
                            break;
                        case '#':
                            store[y][x] = NodeType.WALL;
                            break;
                        case 'C':
                            store[y][x] = NodeType.GOAL;
                            break;
                    }
                }
            }
        }
        catch (Throwable e) {
            System.out.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /*perform A* search given a start(x0, y0) and a goal(x1,y1)*/
    public State A_star(int x0, int y0, int x1, int y1){
        ArrayList<State> frontier = new ArrayList<State>();
        Set<State> explored = new HashSet<State>();
        
        State start = new State(DEFAULT_COST, y0, x0);
        Point goal = new Point(x1, y1);
        System.out.println("Start: " + start + " > Goal: " + goal);
        frontier.add(start);
        
        while(!frontier.isEmpty()){
            //pick least cost from frontier; heuristic used here
            State current = this.removeLeastCost(frontier, goal);
            
            //test for goal
            if (current.getLocation().getX() == x1 && current.getLocation().getY() == y1){
                System.out.println("Goal found!");
                 System.out.println("Explored:" + explored.size());
                return current;
            }
            //add to explored set
            explored.add(current);
            //store[(int)current.getLocation().getY()][(int)current.getLocation().getX()] = NodeType.GOAL;
            
            //get a set of neighbors around
            Set<State> neighbors = current.getNeighbors();
            
            //check all around current
            for(State e: neighbors){
                //if the neighbor is not a wall, process it
                if(!store[(int)e.getLocation().getY()][(int)e.getLocation().getX()].equals(NodeType.WALL)){
                    //set cost of neighbor as cost to get there
                    int g = e.getCost() + current.getCost();
                    e.setCost(g);

                    //add to priority queue
                    if(!explored.contains(e) && !frontier.contains(e)){
                        frontier.add(e);
                        path.put(e, current);
                    }
                    
                    if(frontier.contains(e)){
                        int index = frontier.indexOf(e);
                        if(this.findTotalCost(frontier.get(index),goal) > this.findTotalCost(e, goal)){
                            //replace state with lower alternative
                            frontier.remove(index);
                            frontier.add(e);
                            path.put(e,current);
                        }
                    }
                }
            }
        }
        return NO_PATH;
    }

    
    public GraphState A_star(){
        
        ArrayList<GraphState> frontier = new ArrayList<GraphState>();
        Set <GraphState> Explored = new HashSet<GraphState>();
        
        GraphState current = new GraphState(0, 0, items.getFirst());
        frontier.add(current);
        //GraphState old = null;
        
        while(!frontier.isEmpty()){
            GraphState old = this.removeLeastCost(frontier, current);
            if(!old.getItem().equals("entrance")){
                order.put(old.getItem(), current.getItem());
            }
            current = old;
             
            /*if(current.getItem().equals("bread")){
                System.out.println("");
            }*/
            //test for goal
            if (current.getItem().equals("checkout")){
                System.out.println("Goal found!");
                return current;
            }
            Explored.add(current);
           // System.out.println("Current: " + current.getItem());
            ArrayList<GraphState> successors = this.getSuccessors(current.getItem());
            for(GraphState s: successors){
                s.setGCost(s.getGCost() + current.getGCost());
                s.setCost(this.findTotalCost(s,current, -2));
                
                //System.out.println(s);
                //add to priority queue
                    if(!Explored.contains(s) && !frontier.contains(s)){
                        frontier.add(s);
                        //order.put(s.getItem(), current.getItem());
                    }
                    
                    if(frontier.contains(s)){
                        int index = frontier.indexOf(s);
                        //this.findTotalCost(frontier.get(index), current, -1)
                        if(frontier.get(index).getCost() > s.getCost()){//this.findTotalCost(s, current, 0)){
                            //replace state with lower alternative
                            frontier.remove(index);
                            frontier.add(s);
                            //order.put(s.getItem(), current.getItem());
                        }
                    }
            }
            this.removeNode(current.getItem(), edges);
        }
        
        return NO_ORDER;
       
    }
    
    private ArrayList<GraphState> getSuccessors(String current){
        ArrayList<GraphState> successors = new ArrayList<GraphState>();
        for(Edge e: edges){
            if(e.from().equals(current)){
                successors.add(new GraphState(0,e.cost(), e.to()));
            }
        }
        return successors;
    }
    
    private GraphState removeLeastCost(ArrayList<GraphState> f, GraphState current){
        GraphState min = null;
        for(GraphState s: f){
            if((min == null || s.getCost() < min.getCost())){//this.findTotalCost(min, current, 2))){
                if(!s.getItem().equals("checkout")){
                    min = s;
                }
                if(f.size() == 1){
                    min = s;
                }
            }
        }
        f.remove(min);
        return min;
    }
    
    
    private int findTotalCost(GraphState s, GraphState c, int kind){
        /*System.out.println("\t\t Total for "+ kind + " " + s.getItem() + " is " + s.getGCost() + "+" + this.findNearestItem(c) + "+" + this.getMSTCost(c,s) + 
                "+" +this.findNearestToCheckOut(s) + "=" + ( s.getGCost() + this.findNearestItem(c) + this.getMSTCost(c,s) + this.findNearestToCheckOut(s)));*/
        return  s.getGCost() + this.findNearestItem(c) + this.getMSTCost(c,s) + this.findNearestToCheckOut(s);
    }
    
    //gives MST Cost for remaining nodes not including current one and including checkout
    private int getMSTCost(GraphState s, GraphState n){
        //remove current one from consideration
        ArrayList<Edge> reduced = new ArrayList<Edge>(edges);
        LinkedList<String> less = new LinkedList<String>(items);
        
        this.removeNode(s.getItem(), reduced);
        less.remove(s.getItem());
        
        /*this.removeNode(n.getItem(), reduced);
        less.remove(n.getItem());*/
        
        MinItemsTree mst = new MinItemsTree(reduced, less);
        //System.out.println("\t" + s.getItem() +", " + n.getItem() + " MST cost: " + mst.getMinTreeCost(mst.minSpanTree()));
        
        //System.out.println("");
        //mst.printMinTree(mst.minSpanTree());
        //System.out.println("");
        
        return mst.getMinTreeCost(mst.minSpanTree());
    }
    
    private void removeNode(String s, ArrayList<Edge> list){
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).from().equals(s) || list.get(i).to().equals(s)){
                list.remove(i);
                i--;
            }
        }
    }
    
    //gives d_n(n): distance to nearest item from current one
    private int findNearestItem(GraphState s){
        int min = 0;
        for(Edge e: edges){
            if(e.from().equals(s.getItem())){
               if(min == 0 || e.cost() < min){
                   min = e.cost();
               }
            }
        }
        return min;
    }
    
    //gives dc_n(n): distance to checkout item nearest to it not including current one
    private int findNearestToCheckOut(GraphState s){
        int min = 0;
        for(Edge e: edges){
            if(e.to().equals("checkout")){
                if(!e.from().equals(s.getItem())){
                    if(min == 0 || e.cost() < min){
                        min = e.cost();
                    }
                }
            }
        }
        return min;
    }
   
    /*given a list of states, remove state with least total cost to goal*/
    private State removeLeastCost(ArrayList<State> f, Point goal){
       State min = null;
       for(State s: f){
           //heuristic guides search of min cost
           if(min == null || (this.findTotalCost(s, goal) < this.findTotalCost(min, goal))){
               min = s;
           }
       }
       f.remove(min);
       return min;
    }
    
    /*after A* finds a goal, backtracks along explored states to build path*/
    public void buildSolution(Point from, Point to){
        State init = new State(1,(int)from.getY(), (int)from.getX());
        
        /*track time elapsed to find solution*/
        long start = System.currentTimeMillis();
        State goal = this.A_star((int)from.getX(), (int)from.getY(),(int)to.getX(), (int)to.getY());
        System.out.println("Elapsed A* Search Time: " + (System.currentTimeMillis() - start));
        
        int path_length = 0;
        
        //show init on map
        store[(int)init.getLocation().getY()][(int)init.getLocation().getX()] = NodeType.STEP;
        while(!goal.equals(init)){
            //show steps on map
            store[(int)goal.getLocation().getY()][(int)goal.getLocation().getX()] = NodeType.STEP;
            
            goal = path.get(goal);
            path_length++;
            this.showPathOnMap();
        }
        
        System.out.println("Shortest Path Length: " + path_length);
    }
    
    public ArrayList<String> buildSolution(String goal){
        String init = "entrance";
        ArrayList<String> ordered = new ArrayList<String>();
        
        System.out.println(goal);
        while(!goal.equals(init)){
            ordered.add(goal);
            goal = order.get(goal);
            
            System.out.println(goal);
        }
        ordered.add(init);
        return ordered;
    }
    
    /*calculate the total cost for a state given a goal; includes heuristic*/
    public double findTotalCost(State s, Point goal){
        return (s.getCost() + h_function(s.getLocation(),goal.getLocation(), MANHATTAN));
    }
    
    /*calculate heuristic for 2 points; scales heuristic to break ties*/
    public double h_function(Point p1, Point p2, int type){
        if(type == EUCLIDEAN){
            return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(),2));
        }
        else {//return MANHATTAN distance
            double h =  Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
            return (h *= (1.0 + P));
            
        }
       
    }
    
    /*draw the path found on the ASCII map*/
    private void showPathOnMap(){
        for(int y = 0; y < store.length; y++){
            for(int x = 0; x < store.length; x++){
                String map = null;
                switch(store[y][x]){
                    case WALL:
                        map = "#";
                        break;
                    case FREE:
                        map = " ";
                        break;
                    case STEP:
                        map = "x";
                        break;
                    case GOAL:
                        map = ".";
                        break;
                }
                System.out.print(map);
            }
            System.out.println("");
        }
        System.out.println("");
    }
    
    public static void main(String[] args){
        long start = System.currentTimeMillis();

        /*if (args.length != 1) {
            System.out.println("Error: missing map filename or more arguments than expected!");
            System.exit(1);
        }*/

        File map = new File(args[0]);
        Search test = new Search(map);

        HashMap<String, Point> toShop = new HashMap<String, Point>();
        
        toShop.put("fg", new Point(28, 3));
        toShop.put("milk", new Point(4, 15));
        toShop.put("hotdogs", new Point(13, 20));
        toShop.put("bread", new Point(16, 22));
        toShop.put("juice", new Point(25, 25));
        toShop.put("eggs", new Point(25, 2));
        //toShop.put("cereal", new Point(14, 14));
        
        toShop.put("checkout", new Point(28, 2));
        toShop.put("entrance", new Point(1, 1));
        

        Set<String> keys = toShop.keySet();
        ArrayList<Edge> edges = new ArrayList<Edge>();
        LinkedList<String> items = new LinkedList<String>();

        for (String from : keys) {
            //populate items LinkedList for MST
            items.addFirst(from);
            for (String to : keys) {
                //create edges from every item to every other item in the list
                if (!from.equals(to) && !(from.equals("entrance") && (to.equals("checkout"))) 
                        && !(from.equals("checkout") && (to.equals("entrance")))) {
                    edges.add(new Edge(from, to, (int) test.h_function(toShop.get(from), toShop.get(to), MANHATTAN)));
                }
            }
        }
        
        /*for(Edge e: edges){
            System.out.println(e);
        }*/
        
        System.out.println(edges.size());
        System.out.println(items.size());
        
        Search test2 = new Search(edges, items);
        String output = test2.A_star().getItem();
        ArrayList<String> o = test2.buildSolution(output);
        
        
        for(int i = o.size()-1; i > 0; i--){
            test.buildSolution(toShop.get(o.get(i)),toShop.get(o.get(i-1)));
        }

        System.out.println("TOTAL ELAPSED TIME: " + (System.currentTimeMillis() - start));
    }
}
