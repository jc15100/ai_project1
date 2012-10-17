package pvieira;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Juan C Garcia, Peter Vieira
 */
public class State implements Comparable<State>{
//    private int cost;
//    private Point loc;
	private double cost;
    private Point2D loc;
    private String item;
    
//    public State(int c, int ro, int co){
    public State(double c, double ro, double co){
        cost = c;
//        loc = new Point(co, ro);
        loc = new Point2D.Double(co, ro);
    }
    
    public State( int c, String i){
        cost = c;
        item = i;
    }

//    public int getCost() {
    public double getCost() {
        return cost;
    }
    
    public String getItem(){
        return item;
    }
    
    public void setCost(int cost) {
        this.cost = cost;
    }
    
    @Override
    public int compareTo (State other){
//        int otherCost = other.getCost();
    	double otherCost = other.getCost();
        
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
    
    public String toString(){
        return "X: "+ (int)loc.getX() + " Y: " + (int)loc.getY() + " Cost: " + cost;
    }

//    public Point getLocation(){
    public Point2D getLocation(){
        return loc;
    }
    //Method to obtain the neighbors (successors)
    //of current node
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
