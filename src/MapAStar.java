
import java.io.*;
import acm.program.*;

public class MapAStar extends ConsoleProgram {

	private static int col = -1;
	private static int row = 0;
	private static int scol = 0;
	private static int srow = 0;
	private static int gcol = 0;
	private static int grow = 0;
	private static int[][] object;
	private static String heuristic = "straightLineDistance";
	
	
	public static void main(String[] args) {
		new MapAStar().start(args);
	}
	
	public void run() {
		
		readMapFile();
		int totalCost = runAStar(object);
		println("Total Cost = " + totalCost);
	}

/* Method: readMapFile() */
/** read map file in, gets coordinates of "s" and "g", get
 *  set hash characters (walls) to "0" and space characers
 *  (aisles) to "1" */
	public void readMapFile() {
		
		FileReader inputStream = null;
		
		try {
			inputStream = new FileReader("map.txt");
			println("hello");
			int c;
			int colLocal = -1;
			object = new int[8][7];
			while ((c = inputStream.read()) != -1) {
				if (c != (int)("\n".charAt(0))) {
					col++;
					colLocal++;
				}
				if (c == (int)("\n".charAt(0))) {
					row++;
					colLocal = -1;
				}
				if (c == (int)("#".charAt(0))) 
					object[row][colLocal] = 0;
				if (c == (int)("s".charAt(0))) {
					scol = colLocal;
					srow = row;
					object[row][colLocal] = 1;
				}
				if (c == (int)("g".charAt(0))) {
					gcol = colLocal;
					grow = row;
					object[row][colLocal] = 1;

				}
				if (c == (int)(" ".charAt(0))) 
					object[row][colLocal] = 1;
			}
			col /= row;
			println(row + "\t" + col);
			println("goodbye");
			inputStream.close();
		} catch (IOException ex) {
			println("can't open file");
		}
	} 

/* Method: runAStar() */
/** runs the A* algorithm */
	public static int runAStar(int[][] object) {
		double h = 0.0;
		double dist = 0.0;
		int shortCol = 0;
		int shortRow = 0;
		int cost = 0;
		h = getHeuristic(heuristic, scol, srow, gcol, grow);
//////////TODO:fix while loop to account for when two nodes
		//      result in same heuristic cost!
		while (scol != gcol || srow != grow) {
			System.out.print("dist = " + h + "\n");
			if (object[srow-1][scol] == 1) { //check up
				dist = getHeuristic(heuristic, scol, srow-1, gcol, grow);
				if (dist <= h) {
					h = dist;
					shortCol = 0; 
					shortRow = -1;
				}
			}
			if (object[srow+1][scol] == 1) { //check down
				dist = getHeuristic(heuristic, scol, srow+1, gcol, grow);
				if (dist <= h) {
					h = dist;
					shortCol = 0; 
					shortRow = 1;
				}
			}
			if (object[srow][scol-1] == 1) { //check left
				dist = getHeuristic(heuristic, scol-1, srow, gcol, grow);
				if (dist <= h) {
					h = dist;
					shortCol = -1; 
					shortRow = 0;
				}
			}
			if (object[srow][scol+1] == 1) { //check right
				dist = getHeuristic(heuristic, scol+1, srow, gcol, grow);
				if (dist <= h) {
					h = dist;
					shortCol = 1; 
					shortRow = 0;
				}
			}
			scol += shortCol;
			srow += shortRow;
			cost++;
			System.out.print("scol = " + scol + "\nsrow = " + srow + "\n");
			System.out.print("gcol = " + gcol + "\ngrow = " + grow + "\n");
			System.out.print("Cost = " + cost + "\n\n");
		}
		return cost;
	}
	
/* Method: getStraightLineDistance(); */
/** returns straight line distance between a possible location
 *  and the goal */
	public static double getHeuristic(String heuristic, int x1, int y1, int x2, int y2) {
		
		//run straight line distance heuristic
		if (heuristic == "straightLineDistance") {
			double dist = 0;
			dist = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
			return dist;
		}
		
		//run manhattan distance heuristic
		else if (heuristic == "manhattanDistance") {
			double dist = 0;
			dist = Math.abs(x1 - x2) + Math.abs(y1 - y2);
			return dist;
		}
		else return 0;
	}
}



// create map by getting coordinates of s and g
// take location of "s"
// calculate distance from "s" to an intersection
// calculate distance from intersection to "g" straight line
// do this for all options and choose smallest value
// set "s" location to new node