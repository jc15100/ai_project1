/*
NAME: Juan C. Garcia
CERTIFICATION: I certify that this work is my own and that
               none of it is the work of any other person.
*/
public class DisjointSet 
{
    private int [] s;
    
    public DisjointSet(int numElements)
    {
        s = new int[numElements];
        for(int i = 0; i< s.length; i++)
            s[i] = -1;
    }
    /*Unions two roots*/
    public void union(int root1, int root2)
    {
        assertIsRoot(root1);
        assertIsRoot(root2);
        
        if(root1 == root2)
            throw new IllegalArgumentException();
        
        if(s[root2] < s[root1])
            s[root1] = root2;
        else
        {
            if(s[root1] == s[root2])
                s[root1]--;
            s[root2] = root1;
        }
    }
    /*Finds the root of an item*/
    public int find(int x)
    {
        assertIsItem(x);
        if(s[x] < 0)
            return x;
        else
            return s[x] = find(s[x]);
    }
    /*Helper to check whether an input is in fact a root*/
    private void assertIsRoot(int root)
    {
        assertIsItem(root);
        if(s[root] >= 0)
            throw new IllegalArgumentException();
    }
    /*Helper to check whether an input is a valid item*/
    private void assertIsItem(int x)
    {
        if(x < 0 || x >= s.length)
            throw new IllegalArgumentException();
    }
}//End of DisjointSet Class
