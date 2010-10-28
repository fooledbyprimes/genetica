import java.util.*;


// AN ADJACENCY LIST VERTEX.
// THIS TYPE OF VERTEX OBJECT HAS AN EDGE LIST.

public class aLstVertex {

    int edges;
    public graphVertex vertex;
    public BitSet connections;
    //CONSTRUCTORS

    public aLstVertex(graphVertex v, int verticies) {
        vertex = new graphVertex(v);
        connections = new BitSet(verticies);
        edges = 0;
    }


    //ADD AN OUT EDGE TO THIS VERTEX
    //CALLED DURING CONSTRUCTION OF THE 
    //ADJACENCY LIST

    public void addOutEdge(int sink) {
        connections.set(sink);
        edges++;
    }

    public int getNumEdges() { return edges; }

}

