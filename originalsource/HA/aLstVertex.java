import java.util.*;


// AN ADJACENCY LIST VERTEX.
// THIS TYPE OF VERTEX OBJECT HAS AN EDGE LIST.

public class aLstVertex {

    int edges = 0;
    int cuts = 0;
    public graphVertex vertex;
    public BitSet connections;
    public BitSet cutEdges;

    public aLstVertex(graphVertex v, int verticies) {
        vertex = new graphVertex(v);
        connections = new BitSet(verticies);
	cutEdges = new BitSet();
    }

    public void cutEdge(int sink) {
	if (!cutEdges.get(sink)) {
	    cutEdges.set(sink);  
	    cuts++;
	}
    }

    public void addOutEdge(int sink) {
        if (!connections.get(sink)) {
	    connections.set(sink);
	    edges++;
	}
    }

    public int getNumEdges() { return edges; }
    public int getNumCuts() { return cuts; }


}

