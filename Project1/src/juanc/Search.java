package juanc;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author jc15100
 */
public class Search {
    public static final int DEFAULT_COST = 1;
    private int graphSize;
    
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
            
            StoreMap.NodeType[][] store = new StoreMap.NodeType[y_dim][x_dim];
            
            for(int y = 0; y < y_dim; y++){
                String line = br.readLine();
                for(int x = 0; x < line.length(); x++){
                    switch(line.charAt(x)){
                        case ' ':
                            store[y][x] = StoreMap.NodeType.FREE;
                            break;
                        case '#':
                            store[y][x] = StoreMap.NodeType.WALL;
                            break;
                        case 'C':
                            store[y][x] = StoreMap.NodeType.GOAL;
                            break;
                    }
                }
            }
            //instantiate StoreMap with built array
            StoreMap storeMap = new StoreMap(store);
            
        }
        catch (Throwable e) {
            System.out.println("Error reading file");
            System.exit(1);
        }
        
    }
    
    /*list to be built*/
    private ArrayList<Step> shortestPath;
    
    //TO DO
    public void buildBestPath(){
        
    }
    
    
    public void A_star(int x0, int y0, int x1, int y1){
        PriorityQueue<Step> steps = new PriorityQueue<Step>();
        TreeSet<Step> explored = new TreeSet<Step>();
        
        Step start = new Step(DEFAULT_COST, x0, y0);
        
        steps.add(start);
        while(!steps.isEmpty() && explored.size() < graphSize){
            Step c = steps.remove();
            
            //test for goal
            if (c.getLocation().getX() == x1 && c.getLocation().getY() == y1){
                System.out.println("Goal found!");
                return;
            }
            //add to explored set
            explored.add(c);
            
            Set<Step> neighbors = c.getNeighbors();
            
            //check all steps around current
            for(Step e: neighbors){
                //set cost of neighbor as cost to get there
                int co = e.getCost() + c.getCost();
                c.setCost(co);
                
                //add to priority queue
                if(!explored.contains(e) && !steps.contains(e) ){
                    steps.add(e);
                }
                else if(steps.contains(e)){
                    
                    int c_function = co + h_function(c.getLocation(),e.getLocation());
                    if(e.getCost() > c_function){
                        //replace cost
                        e.setCost(c_function);
                        //add to steps 
                        steps.add(e);
                    }
                }
            }
        }
    }
    
    private int h_function(Point p1, Point p2){
        return (int)Math.sqrt((p2.x - p1.x)^2 + (p2.y - p1.y)^2);
    }
    
    public static void main(String [] args){
        
         if (args.length != 1) {
            System.out.println("Error: missing map filename or more arguments than expected!");
            System.exit(1);
        }
        
        File map = new File(args[0]); 
        Search test = new Search(map); 
    }
}
