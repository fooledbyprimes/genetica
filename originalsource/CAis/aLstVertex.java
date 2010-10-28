import java.util.*;


// AN ADJACENCY LIST VERTEX.
// THIS TYPE OF VERTEX OBJECT HAS AN EDGE LIST.

public class aLstVertex {

    int edges = 0;
    public graphVertex vertex;
    public BitSet connections;
    public boolean selected = false;
    public boolean neighborOn = false;
    public int numNeighborsOn = 0;

    public aLstVertex(graphVertex v, int verticies) {
        vertex = new graphVertex(v);
        connections = new BitSet(verticies);
    }


    public void addOutEdge(int sink) {
        if (!connections.get(sink)) {
	    connections.set(sink);
	    edges++;
	}
    }

    public int getNumEdges() { return edges; }


    public void addNeighborOn() {
	numNeighborsOn += 1;
	neighborOn = true;
    }

    public void delNeighborOn() {
	if (numNeighborsOn > 1) {
	    numNeighborsOn -= 1;
	    neighborOn = numNeighborsOn > 0;
	}

    }
}

