// GRAPH VERTEX REPRESENTATION.

public class graphVertex {      //implements fibofied {
    public Integer number;     //The vertex's number

    // CONSTRUCTORS
    public graphVertex(int inNum) {
        this.number = new Integer(inNum);
    }
    public graphVertex(graphVertex v) {
        this.number = new Integer(v.number.intValue());
    }
    public graphVertex() {this(0);}
}


