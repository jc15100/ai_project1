package juanc;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author jc15100
 */
public class Search {
    public static final int DEFAULT_COST = 1;
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
    private ArrayList<State> shortestPath;
    private HashMap<State,State> path = new HashMap<State,State>();
            
    //TO DO
    public void buildBestPath(){
        
    }
    
    public State removeLeastCost(ArrayList<State> f){
        int min = f.get(0).getCost();
        int min_loc = 0;
        for(int i = 0; i < f.size(); i++){
            if(f.get(i).getCost() < min){
                min = f.get(i).getCost();
                min_loc = i;
            }
        }
        return f.remove(min_loc);
    }
    
    public State A_star(int x0, int y0, int x1, int y1){
        ArrayList<State> frontier = new ArrayList<State>();
        TreeSet<State> explored = new TreeSet<State>();
        
        State start = new State(DEFAULT_COST, y0, x0);
        
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
            
            Set<State> neighbors = c.getNeighbors();
            
            //check all steps around current
            for(State e: neighbors){
                if(!store[(int)e.getLocation().getY()][(int)e.getLocation().getX()].equals(NodeType.WALL)){
                    
                    //System.out.println("Type:" + store[(int)e.getLocation().getY()][(int)e.getLocation().getX()]);
                    
                    //set cost of neighbor as cost to get there
                    int co = e.getCost() + c.getCost() + h_function(e.getLocation(), new Point(x1,y1));
                    c.setCost(co);

                    //add to priority queue
                    if(!explored.contains(e) || !frontier.contains(e) ){
                        frontier.add(e);
                        path.put(e, c);
                    }
                    else{
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
        }
        return null;
    }
    
    private int h_function(Point p1, Point p2){
        return (int)Math.sqrt((p2.x - p1.x)^2 + (p2.y - p1.y)^2);
    }
    
    public void printSolution(){
        State init = new State(1,1,1);
        State goal = this.A_star(1, 1, 5, 1);
        System.out.println("Path size: "+ path.size());
        
        while(!goal.equals(init)){
            goal = path.get(goal);
            System.out.println("Steps:" + goal.getLocation() + " Cost" + goal.getCost());
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
