package juanc;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author jc15100
 */
public class Shopping {
    
    //TO DO:takes an ASCII map file and a list of items with their location in the map
    public Shopping(){
        
        
        
    }
    
    public static void main(String [] args){
        
        LinkedList<String> items = new LinkedList<String>();
        items.add("entrance");
        items.add("milk");
        items.add("cereal");
        items.add("ham");
        items.add("checkout");
        
        ArrayList<Edge> edges = new ArrayList<Edge>();
        edges.add(new Edge("entrance", "milk", 5));
        edges.add(new Edge("milk", "entrance", 5));
        edges.add(new Edge("entrance", "cereal", 6));
        edges.add(new Edge("cereal", "entrance", 6));
        edges.add(new Edge("entrance", "ham",10));
        edges.add(new Edge("ham", "entrance", 10));
        edges.add(new Edge("milk", "cereal", 13));
        edges.add(new Edge("cereal", "milk", 13));
        edges.add(new Edge("milk", "ham", 14));
        edges.add(new Edge("ham", "milk", 14));
        edges.add(new Edge("milk", "checkout", 10));
        edges.add(new Edge("checkout", "milk", 10));
        edges.add(new Edge("cereal", "ham", 4));
        edges.add(new Edge("ham", "cereal", 4));
        edges.add(new Edge("cereal", "checkout", 7));
        edges.add(new Edge("checkout", "cereal", 7));
        edges.add(new Edge("ham", "checkout", 4));
        edges.add(new Edge("checkout", "ham", 4));
        Search test = new Search();
        String output = test.A_star(edges, items).getItem();
        test.buildSolution(output);
    }
}
