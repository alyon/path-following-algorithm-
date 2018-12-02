package pathfollower;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import static pathfollower.PathFollower.Direction.EAST;
import static pathfollower.PathFollower.Direction.NORTH;
import static pathfollower.PathFollower.Direction.SOUTH;
import static pathfollower.PathFollower.Direction.WEST;
/**
 *
 * @author aliuzun
 */
public class PathFollower {
   enum Direction{
   NORTH, EAST, SOUTH, WEST;
}
     /**
     * Files to create Maps
     */
    static String [] files  = {
      "files/map1.txt", 
      "files/map2.txt",
      "files/map3.txt",
    };

    
    /**
     * The character that marks the start of a map
     */
    static final char START_CHAR = '@';
    
    /**
     * The character that marks the end of the map
     */
    static final char END_CHAR = 'x';

    /**
     * Stores the last used direction
     */
    static Direction currentDirection = null;
    
    /**
     * The map to be navigated
     */
    static char[][] map;

     /**
     * Stores letters on the path
     */
    static ArrayList<Character> letters = new ArrayList();
   
    /**
     * Stores each point on path
     */
    static ArrayList<Character> pathPrint = new ArrayList();
    
    /**
     * The points that have been visited during travel
     */
    static ArrayList<Point> visited = new ArrayList<>();

    /**
     * The current path taken through the map
     */
    static Stack<Point> path = new Stack<>();

    
    public static void main(String[] args) {
        
       for (int i = 0; i<files.length; i++){
             runTaskForFile(files[i]);
       }
       
    }

     /**
     * Clears data before each task
     */
    private static void cleanUpBeforeTask(){
    currentDirection = null;
        letters.clear();
        pathPrint.clear();
        visited.clear();
        path.clear();
        map = null;
    }
    
     /**
     * Runs the path following algorithm for given file
     */
    private static void runTaskForFile(String filePath){
        cleanUpBeforeTask();
        createMapFromFile(filePath);
        boolean solved = findPath();
        if (solved) {
            printSolution();
        } else {
            System.out.println("No path found");
        }
    }
    /**
     * Finds a path from the starting point to the end of the map
     * returns true if a path has been found
     */
    private static boolean findPath() {
        Point start = findStartingPoint();
        if (start == null) {
            return false;
        }

        /*
         * Do depth-first search
         */
        path.push(start);
        visited.add(start);
        while (!path.empty()) {
            Point current = nextUnvisitedPoint(path.peek());
            if (current == null ) {
                path.pop();
            } else if (map[(int) current.x][(int) current.y] == END_CHAR) {
                registerPath(current);              
                return true;
            }else {
                path.push(current);              
                visited.add(current);
                registerPath(current);               
            }
        }

        return false;
    }
    
    /*
    * Adds each path to pathPrint array
    * Adds each letter path to letters array
    */
    private static void registerPath(Point current){
        Character ch = map[(int) current.x][(int) current.y];
        if (Character.isLetter(ch) && ch != END_CHAR ){
                letters.add(map[(int) current.x][(int) current.y]);
            }
                pathPrint.add(map[(int) current.x][(int) current.y]);
    }

    /*
    * Adds each letter path to letters array
    */
    private static Point getPointByDirection(Direction direction, Point current){
        Point nextPoint = null;
        int mapLength = map.length;
        int mapHeight = map[1].length;

        switch(direction){
 
            case NORTH:
                 if (current.y > 0 && map[(int) current.x][(int) (current.y - 1)] != ' ') {
                    nextPoint = new Point(current.x, current.y - 1);
                }
                 break;
             case EAST:
                 if (current.x < mapLength - 1 && map[(int) (current.x + 1)][(int) current.y] != ' ') {
                    nextPoint = new Point(current.x + 1, current.y);
                }
                 break;
             case SOUTH:  
                 if (current.y < mapHeight - 1 && map[(int) current.x][(int) (current.y + 1)] != ' ') {
                    nextPoint = new Point(current.x, current.y + 1);
                 }
                break;
             case WEST:
               
             if (current.x > 0 && map[(int) (current.x - 1)][(int) current.y] != ' ') {
                 nextPoint = new Point(current.x - 1, current.y);
                 }
              break;            
        }
        return nextPoint;
    }
    /**
     * Selects a neighbour point that is not yet been visited
     * Neighbours are selected from all 4 directions.
     * First checks if the path is available at current direction
     * If not makes a clockwise turn from current point to find best path to go next
     */
    private static Point nextUnvisitedPoint(Point current) {
       
        List<Point> neighbours = new ArrayList<>();

        Point north = getPointByDirection(NORTH, current);
        Point east = getPointByDirection(EAST, current);
        Point south = getPointByDirection(SOUTH, current);
        Point west = getPointByDirection(WEST, current);
     
        /*CHECK CURRENT DIRECTION FIRST*/
        if(currentDirection != null && getPointByDirection(currentDirection, current) != null && !visited.contains(getPointByDirection(currentDirection, current)) ){
             neighbours.add(getPointByDirection(currentDirection, current));
        }else{
            currentDirection = null;
            /*NORTH*/
            if(north != null && !visited.contains(north)) {
                neighbours.add(north);
                currentDirection = NORTH;
            }

             /*EAST*/
            if(east != null && !visited.contains(east)) { 
                neighbours.add(east);
                  currentDirection = EAST;
            }

            /*SOUTH*/
            if(south != null && !visited.contains(south)){
                neighbours.add(south);
                  currentDirection = SOUTH;
            }

             /*WEST*/
            if(west != null && !visited.contains(west)) {
                neighbours.add(west);
                  currentDirection = WEST;
            }            
        }
        
        while (!neighbours.isEmpty()) {
            Point candidate = neighbours.remove(0);
            if (!visited.contains(candidate)) {             
                return candidate;
            }
        }
      
        return null;
    }

    /**
     * Searches the map for a point containing the start char.
     *
     * Returns the start point if found
     */
    private static Point findStartingPoint() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == START_CHAR) {                      
                   registerPath(new Point(i, j));
                    return new Point(i, j);                   
                }
            }
        }
        return null;
    }

    /**
     * Prints the solved map to STDOUT
     */
    private static void printSolution() {
        
        for (char letter : letters) {   
             System.out.print(letter);         
        }
        System.out.println();
        
         for (char p : pathPrint) {   
             System.out.print(p);         
        }
        System.out.println("\n");
        
    }

    /**
     * Reads the map input from a file
     */
    private static void createMapFromFile(String path) {


        try (FileReader fr = new FileReader(path)) {
           BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            String[] dimensions = line.split(" ");
            int x = Integer.parseInt(dimensions[0]);
            int y = Integer.parseInt(dimensions[1]);

            map = new char[x][y];
            for (int i = 0; i < x; i++) {
                line = br.readLine();
                for (int j = 0; j < y; j++) {
                    if (line != null){
                         map[i][j] = line.charAt(j);
                    }
                }
            }
        } catch (IOException e) {
            System.out.print(e.getMessage());
            e.printStackTrace();
        }
    }
}