package juanc;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    
    public enum NodeType{ WALL, FREE, GOAL, STEP}
    
    public static final State NO_PATH = null;
    private HashMap<State,State> path = new HashMap<State,State>();
    private NodeType [][] store;
    
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
        Point goal = new Point(y1, x1);
        
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
            store[(int)current.getLocation().getY()][(int)current.getLocation().getX()] = NodeType.GOAL;
            
            //get a set of neighbors around
            Set<State> neighbors = current.getNeighbors();
            
            //check all around current
            for(State e: neighbors){
                //if the neighbor is not a wall, process it
                if(!store[(int)e.getLocation().getY()][(int)e.getLocation().getX()].equals(NodeType.WALL)){
                    //set cost of neighbor as cost to get there
                    int co = e.getCost() + current.getCost();
                    e.setCost(co);

                    //add to priority queue
                    if(!explored.contains(e) && !frontier.contains(e)){
                        frontier.add(e);
                        path.put(e, current);
                    }
                    
                    if(frontier.contains(e)){
                        int index = frontier.indexOf(e);
                        if(frontier.get(index).getCost() > this.findTotalCost(e, goal)){
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
    public void buildSolution(){
        State init = new State(1,1,1);
        
        /*track time elapsed to find solution*/
        long start = System.currentTimeMillis();
        State goal = this.A_star(1, 1, 7, 7);
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
    
    /*calculate the total cost for a state given a goal; includes heuristic*/
    private double findTotalCost(State s, Point goal){
        return (s.getCost() + h_function(s.getLocation(),goal.getLocation(), MANHATTAN));
    }
    
    /*calculate heuristic for 2 points; scales heuristic to break ties*/
    private double h_function(Point p1, Point p2, int type){
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
        if (args.length != 1) {
            System.out.println("Error: missing map filename or more arguments than expected!");
            System.exit(1);
        }

        File map = new File(args[0]); 
        Search test = new Search(map); 
        test.buildSolution();
        
    }
}
