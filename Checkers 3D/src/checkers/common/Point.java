package checkers.common;

/**
* Class Point
* Implements Integer Point using
* generic Pair of Integer
* 
* @author Cristian Tardivo
*/
public class Point {

    private Pair<Integer,Integer> point;
    
    /**
     * Empty Constructor
     * Point (0,0)
     */
    public Point(){
        point = new Pair<>(0,0);
    }
    
    /**
     * Specialized constructor
     * @param first first integer value
     * @param second second integer value
     */
    public Point(int first, int second){
        point = new Pair<>(first,second);
    }

    /**
     * Generate Point hashCode
     * @return integer hashCode
     */
    @Override
    public int hashCode(){
        return point.hashCode();
    }
    
    /**
     * Compare this point with other
     * @param other point to compare
     * @return equal or not equal
     */
    @Override
    public boolean equals(Object other){
        if (other != null && other instanceof Point){
            Point aux = (Point) other;
            return point.getFirst() == aux.getFirst() && point.getSecond() == aux.getSecond();
        }
        return false;
    }

    /**
     * Get Point string label
     * @return string point
     */
    @Override
    public String toString(){ 
        return point.toString();
    }
    
    /**
     * Clone current point
     * @return clone of this point
     */
    @Override
    public Point clone(){
        return new Point(point.getFirst(),point.getSecond());
    }

    /**
     * Retrieves first value
     * @return integer first value
     */
    public int getFirst(){
        return point.getFirst();
    }

    /**
     * Set first value
     * @param first integer value
     */
    public void setFirst(int first){
        point.setFirst(first);
    }

    /**
     * Retrieves second value
     * @return integer second value
     */
    public int getSecond(){
        return point.getSecond();
    }
    
    /**
     * Set second value
     * @param second integer value
     */
    public void setSecond(int second){
        point.setSecond(second);
    }           
}