package juanc;
/**
 * @author jc15100
 */
public class StoreMap {
    public enum NodeType{
        WALL, FREE, GOAL
    }
    
    private NodeType [][] store;
    
    public StoreMap(NodeType[][] s){
        store = s;
    }
    
}
