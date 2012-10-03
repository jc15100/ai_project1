/*
NAME: Juan C. Garcia
CERTIFICATION: I certify that this work is my own and that
               none of it is the work of any other person.
*/
public class Edge implements Comparable
{
    private String from;
    private String to; 
    private int cost;
    
    public Edge(String f, String t, int c)
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
    public int cost()
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
        return cost() - other.cost();
    }

}//End of Edge class
