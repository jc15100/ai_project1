package pvieira;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jc15100
 */
public class State implements Comparable<State>{
    private int cost;
    private int gcost;
    private Point loc;
    private Set<State> neighbors;
    
    public State(int c, int g, int ro, int co){
        cost = c;
        gcost = g;
        loc = new Point(co, ro);
        neighbors = new HashSet<State>();
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    
    public int getGCost() {
    	return gcost;
    }
    
    public void setGCost(int gcost) {
    	this.gcost = gcost;
    }
    
    /*@Override
    public int compareTo (State other){
        int otherCost = other.getCost();
        System.out.println("using compareto() method");
        
        return cost < otherCost ? -1: cost > otherCost ? 1 : 0;
    }*/
    
    @Override
    public int compareTo (State other){
        int otherX = other.getLocation().x;
        int otherY = other.getLocation().y;
        
        return loc.x < otherX || loc.y < otherY ? -1: loc.x > otherX || loc.y > otherY ? 1 : 0;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.loc != null ? this.loc.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final State other = (State) obj;
        if (this.loc != other.loc && (this.loc == null || !this.loc.equals(other.loc))) {
            return false;
        }
        return true;
    }

    public Point getLocation(){
        return loc;
    }
    
    public Set<State> getNeighbors(){
        int top = (int) loc.getY() - 1;
        int down = (int) loc.getY() + 1;
        int right = (int) loc.getX() + 1;
        int left = (int) loc.getX() - 1;
              
        neighbors.add(new State(1, 1, top, (int)loc.getX()));
        neighbors.add(new State(1, 1, down, (int)loc.getX()));
        neighbors.add(new State(1, 1, (int)loc.getY(), right));
        neighbors.add(new State(1, 1, (int)loc.getY(), left));
            
        return neighbors;
    }
    
}
