package pvieira;

/*
NAME: Juan C. Garcia 
CERTIFICATION: I certify that this work is my own and that
               none of it is the work of any other person.
*/
import java.awt.Point;
import pvieira.Edge;
import pvieira.DisjointSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class MinItemsTree 
{
    private List<Edge> edges;
    private LinkedList<String> items;
    
    /*Main "Tester" method*/    
    public static void main(String [] args)
    {
        //MinItemsTree test = new MinItemsTree("miles.dat");
        //test.printMinTree(test.minSpanTree());
    }
    
    public MinItemsTree(ArrayList<Edge> e, LinkedList<String> i){
        edges = e;
        items = i;
    }

    /*Obtains the Minimum Spanning Tree via Kruskal's algorithm*/
    public List<Edge> minSpanTree()
    {
        int v = items.size();
        List<Edge> result = new ArrayList<Edge>();
        PriorityQueue<Edge> pq = new PriorityQueue<Edge>(edges);
        DisjointSet ds = new DisjointSet(v);
        
        while(! pq.isEmpty())
        {
            /*The minimum edge is constantly removed*/
            Edge e = pq.remove();
            
            int r1 = ds.find(items.indexOf(e.from()));
            int r2 = ds.find(items.indexOf(e.to()));
            /*If the two find calls don't yield same value, then a cycle is 
             * not made and the edge can be added*/
            if(r1 != r2)
            {
                ds.union(r1, r2);
                result.add(e);
                /*Algorithm stops when there are V-1 edges*/
                if(result.size() == (v - 1))
                    break;
            }   
        }
        return result;
    }
    /*Prints the Minimum Spanning Tree with its total cost*/
    public void printMinTree(List<Edge> edges)
    {
        System.out.println("[ From     -     To ] Cost: ");
        int totalCost = 0;
        for(Edge e : edges)
        {
            totalCost += e.cost();
            System.out.println(e);
        }
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("Total Cost: " + totalCost);
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$");
    }
    
    public int getMinTreeCost(List<Edge> edges){
        int totalCost = 0;
        for(Edge e : edges)
        {
            totalCost += e.cost();
        }
        return totalCost;
    }
}
