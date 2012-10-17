package pvieira;

/*
 * @author Juan C Garcia, Peter Vieira
*/
public class Edge implements Comparable
{
    private String from;
    private String to; 
//    private int cost;
    private double cost;
    
//    public Edge(String f, String t, int c)
    public Edge(String f, String t, double c)
    {
        from = f;
        to = t;
        cost = c;
    }
    
    public String from()
    {
        return from;
    }
    public String to()
    {
        return to;
    }
//    public int cost()
    public double cost()
    {
        return cost;
    }
    @Override
    public String toString()
    {
        String result = "[ "+ from + " - " + to + " ]" + " Cost: " + cost; 
        return result;
    }
    public int compareTo(Object o) 
    {
        Edge other = (Edge) o;
 //       return cost() - other.cost();
        return (int)(cost() - other.cost());
    }

}//End of Edge class
