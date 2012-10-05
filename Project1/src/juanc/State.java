package juanc;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jc15100
 */
public class State implements Comparable<State>{
    private int cost;
    private Point loc;
    
    
    public State(int c, int ro, int co){
        cost = c;
        loc = new Point(co, ro);
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    
    @Override
    public int compareTo (State other){
        int otherCost = other.getCost();
        
        return cost < otherCost ? -1: cost > otherCost ? 1 : 0;
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
    
    @Override
    public String toString(){
        return "X: "+ (int)loc.getX() + " Y: " + (int)loc.getY() + " Cost: " + cost;
    }

    public Point getLocation(){
        return loc;
    }
    
    public Set<State> getNeighbors(){
        int top = (int) loc.getX() - 1;
        int down = (int) loc.getX() + 1;
        int right = (int) loc.getY() - 1;
        int left = (int) loc.getY() + 1;
        Set<State> neighbors = new HashSet<State>();  
        
        neighbors.add(new State(1, (int)loc.getY(),top));
        neighbors.add(new State(1, (int)loc.getY(),down));
        neighbors.add(new State(1, right, (int)loc.getX()));
        neighbors.add(new State(1, left, (int)loc.getX()));
            
        return neighbors;
    }
}
