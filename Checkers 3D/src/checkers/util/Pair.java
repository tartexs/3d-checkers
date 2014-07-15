package checkers.util;

/**
 * Class Pair
 * Implements a Pair of generic objects
 * @param <A> class of first object
 * @param <B> class of second object
 * 
 * @author Cristian Tardivo
 */
public class Pair<A,B> {
    
    private A first;
    private B second;

    /**
     * Specialized constructor
     * @param first first generic value
     * @param second second generic value
     */
    public Pair(A first, B second){
        this.first = first;
        this.second = second;
    }

    /**
     * Generate current Pair hashCode
     * @return integer hashCode
     */
    @Override
    public int hashCode(){
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;
        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }
    
    /**
     * Compare this pair with other pair
     * @param other pair to compare
     * @return equal or not equal
     */
    @Override
    public boolean equals(Object other){
        if (other instanceof Pair){
            Pair aux = (Pair) other;
            // Invalid null Pair values
            if(aux.first == null && this.first != null) return false;
            if(aux.first != null && this.first == null) return false;
            if(aux.second == null && this.second != null) return false;
            if(aux.second != null && this.second == null) return false;
            // Compare elements
            boolean res = true;
            if(this.first != null){
                if(aux.first.getClass() != this.first.getClass()) return false;
                res &= aux.first.equals(this.first);
            }
            if(this.second != null){
                if(aux.second.getClass() != this.second.getClass()) return false;
                res &= aux.second.equals(this.second);
            }
            return res;
        }
        return false;
    }

    /**
     * Get Pair string label
     * @return string pair
     */
    @Override
    public String toString(){ 
           return "(" + first + ", " + second + ")"; 
    }
    
    /**
     * Retrieves first value
     * @return A first value
     */
    public A getFirst(){
        return first;
    }

    /**
     * Set first value
     * @param first A value
     */
    public void setFirst(A first){
        this.first = first;
    }

    /**
     * Retrieves second value
     * @return B second value
     */
    public B getSecond(){
        return second;
    }
    
    /**
     * Set second value
     * @param second B value
     */
    public void setSecond(B second){
        this.second = second;
    }
}