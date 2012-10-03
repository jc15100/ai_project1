package juanc;
/**
 * @author jc15100
 */
public class StoreMap {
    public enum NodeType{
        WALL, FREE, GOAL
    }
    
    private NodeType [][] store;
    private int rows;
    private int cols;
    
    
    public StoreMap(NodeType[][] s){
        store = s;
    }
    
    public int getRows(){
        return rows;
    }
    
    public int getCols(){
        return cols;
    }
    
}
