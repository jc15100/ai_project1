package pvieira;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author jc15100
 */
public class Search {
    public static final int DEFAULT_COST = 0;
    private int graphSize;
            
    
    public enum NodeType{
        WALL, FREE, GOAL
    }
    
    private NodeType [][] store;
    
    /*perform search initialization given a store map with goal shown, 
     * and initial location in the store given by x0, y0 */ 
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
    
    /*list to be built*/
    private HashMap<State,State> path = new HashMap<State,State>();
            
    //TO DO
    public void buildBestPath(){
        
    }
    
    public State removeLeastCost(ArrayList<State> f){
        int min = f.get(0).getCost();
        int min_loc = 0;
        for(int i = 0; i < f.size(); i++){
            if(f.get(i).getCost() <= min){
                min = f.get(i).getCost();
                min_loc = i;
            }
        }
         return f.remove(min_loc);
    }
    
    public State A_star(int y0, int x0, int y1, int x1){
        ArrayList<State> frontier = new ArrayList<State>();
        HashSet<State> explored = new HashSet<State>();
        
        State start = new State(0, 0, y0, x0);
        
        frontier.add(start);
        
        while(!frontier.isEmpty()){
            State c = this.removeLeastCost(frontier);
            //test for goal
            if (c.getLocation().getX() == x1 && c.getLocation().getY() == y1){
                System.out.println("Goal found!");
                return c;
            }
            //add to explored set
            explored.add(c);
            
//////////////debug of explored.contains() should return TRUE if
            //I compare State(0, 0, 1, 1) and State(5, 5, 1, 1)
            //but it doesn't. Because explored.contains() is not using
            //the equals() method
            //State d = new State(5, 5, 1, 1); 
            //System.out.println("state: " + d.getCost() + ", " + d.getGCost() + ", " + d.getLocation());
           // System.out.println(explored.contains(d));
            
            Set<State> neighbors = c.getNeighbors();
            
            //check all steps around current
            for(State e: neighbors){
                if(!store[(int)e.getLocation().getY()][(int)e.getLocation().getX()].equals(NodeType.WALL)){
                    
                    //set cost of neighbor as cost to get there
                    int co = e.getGCost() + c.getGCost() + h_function(e.getLocation(), new Point(x1,y1));
                    int g = c.getGCost() + e.getGCost();
                    e.setCost(co);
                    e.setGCost(g);
               
                    //add to priority queue
                    if(!explored.contains(e) && !frontier.contains(e) ){
                    	frontier.add(e);
                        path.put(e, c);
                        
                    }
                    if(frontier.contains(e)){
                       State duplicate = frontier.get(frontier.indexOf(e));
                       if(duplicate.getCost() > e.getCost()){
                            //replace cost
                            frontier.remove(frontier.indexOf(duplicate));
                            frontier.add(e);
                            path.put(e, c);
                        }

                    }
                }
            }
        }
        return null;
    }
    
    private int h_function(Point p1, Point p2){
        return (int)Math.sqrt(Math.pow((p2.x - p1.x),2) + Math.pow((p2.y - p1.y),2));
    }
    
    public void printSolution(){
        State init = new State(0,0,18,23);
        long startTime = System.nanoTime();
        State goal = this.A_star(18,23,1,1);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        duration /= 1000000;
        System.out.println("Execution Time: " + duration + " ms");
        int [][] solution = new int[path.size()][2];
        while(!goal.equals(init)) {
        	int i = 0;
        	solution[i][0] = goal.getLocation().y;
        	solution[i][1] = goal.getLocation().x;
        	System.out.println("[" + solution[i][0] + ", " + solution[i][1] + "]");
        	goal = path.get(goal);
        }
    }
    
    
    public static void main(String[] args){
        
        if (args.length != 1) {
            System.out.println("Error: missing map filename or more arguments than expected!");
            System.exit(1);
        }

        File map = new File(args[0]); 
        Search test = new Search(map); 
        test.printSolution();
    }
}
