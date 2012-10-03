package juanc;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jc15100
 */
public class Step {
    private int cost;
    private Point loc;
    private Set<Step> neighbors;
    
    public Step(int c, int r, int co){
        cost = c;
        loc.setLocation(c,r);
        neighbors = new HashSet<Step>();
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    
    public int compareTo (Step other){
        int otherCost = other.getCost();
        
        return cost < otherCost ? -1: cost > otherCost ? 1 : 0;
    }
    
    public Point getLocation(){
        return loc;
    }
    
    public Set<Step> getNeighbors(){
        int top = (int) loc.getX() - 1;
        int down = (int) loc.getX() + 1;
        int right = (int) loc.getY() - 1;
        int left = (int) loc.getY() + 1;
              
        neighbors.add(new Step(1, (int)loc.getY(),top));
        neighbors.add(new Step(1, (int)loc.getY(),down));
        neighbors.add(new Step(1, right, (int)loc.getX()));
        neighbors.add(new Step(1, left, (int)loc.getX()));
            
        return neighbors;
    }
    
}
