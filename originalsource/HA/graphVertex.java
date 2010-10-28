// GRAPH VERTEX REPRESENTATION.

public class graphVertex {      
    public int number;     //The vertex's number
    boolean selected = false;

    // CONSTRUCTORS
    public graphVertex(int inNum) {
        this.number = inNum;
    }
    public graphVertex(graphVertex v) {
        this.number = v.number;
    }
    public graphVertex() {this(0);}
}


