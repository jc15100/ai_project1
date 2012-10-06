package juanc;

/**
 * @author jc15100
 */
public class GraphState implements Comparable<GraphState>{
   
    private int cost;
    private int gcost;
    private String item;
    
    public GraphState(int c, int g, String s){
        cost = c;
        item = s;
        gcost= g;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.item != null ? this.item.hashCode() : 0);
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
        if ((this.item == null) ? (other.item != null) : !this.item.equals(other.item)) {
            return false;
        }
        return true;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    
    public int getGCost(){
        return gcost;
    }
    
    public void setGCost(int g){
        this.gcost = g;
    }
    
    @Override
    public String toString(){
        return item + " Cost: " + cost;
    }

     public String getItem(){
        return item;
    }
     
    @Override
    public int compareTo(GraphState t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
