package pvieira;

import java.util.Set;
/**
 * @author jc15100
 */
public class GraphState implements Comparable<GraphState>{
   
    private int cost;
    private String item;
    private Set<String> visited;

   
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.visited != null ? this.visited.hashCode() : 0);
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
        final GraphState other = (GraphState) obj;
        if (this.visited != other.visited && (this.visited == null || !this.visited.equals(other.visited))) {
            return false;
        }
        return true;
    }
    
    public GraphState( int c, String i){
        cost = c;
        item = i;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    
    public String getItem() {
    	return item;
    }
    
    public void setItem(String item) {
    	this.item = item;
    }
    @Override
    public String toString(){
        String v = "";
        for(String s: visited){
            v += " " + s;
        }
        return v + " Cost: " + cost;
    }


    @Override
    public int compareTo(GraphState t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
