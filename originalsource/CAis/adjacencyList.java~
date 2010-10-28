import java.util.*;
import java.lang.*;

//THIS CLASS ENCAPSULATES THE ADJACENCY LIST STRUCTURE FOR
//MATHEMATICAL GRAPH SPECIFICATIONS


public class adjacencyList {
    private int numVertices;
    private int numEdges;
    public ArrayList vertexArray;
    private double TuranSetSize = 0.0;

    //CONSTRUCTORS
    public adjacencyList() {
        this.numVertices = 0;
        this.numEdges = 0;
        vertexArray = new ArrayList();
    }


    //ADD A VERTEX TO THE ADJACENCY LIST
    public void addVertex(int maxEdges) {
        int n = vertexArray.size() + 1;
        //System.out.println("ADD VERTEX:"+n);
        graphVertex vertex = new graphVertex(n);
        aLstVertex alVertex = new aLstVertex(vertex,maxEdges);
        vertexArray.add(alVertex);
        numVertices++;
    }



    //ADD AN EDGE.  TRY TO ADD THE SINK VERTEX TO THE VERTEX LIST
    //BECAUSE IT MIGHT NOT HAVE BEEN SPECIFIED IN THE INPUT JUST YET.
    //CHECK FOR EXISTANCE OF BOTH SOURCE AND SINK BEFORE WE
    //ADD THE OUT EDGE TO THE SOURCE VERTEX'S EDGE OUT LIST.

    public void addEdge(int from, int to) {
        //System.out.println(" ADD EDGE:"+to);
        aLstVertex fromVertex = null; 
        fromVertex = (aLstVertex) vertexArray.get(from-1);
        fromVertex.addOutEdge(to);
        numEdges++;
    }



    //RETURN THE NUMBER OF VERTICIES IN THE ADJACENCY LIST

    public int getNumVertices() {
        return numVertices;
    }


    //STRING REPRESENTATION OF THE ADJACENCY LIST 
    //USED FOR DEBUGGING PURPOSES

    public String textof() {
        String S = new String("adjList contents:\n");
//          aLstVertex vtxEntry = new aLstVertex();
//          aLstEdge alEdge = new aLstEdge();

//          for (Iterator i = vertexArray.iterator(); i.hasNext(); ) {
//              vtxEntry = (aLstVertex) i.next();
//              S = S.concat( "[" + vtxEntry.vertex.name + "] ");
//              for (Iterator j = vtxEntry.edgeList.iterator(); j.hasNext(); ) {
//                  alEdge = (aLstEdge) j.next(); 
//                  S = S.concat("."+alEdge.sink.vertex.name + "(" + alEdge.weight + ")");
//              }
//              S = S.concat("\n");
//          }
        
        return S; 
    }


    //MORE DEBUGGING

    public void showVertices() {
        String S = new String("ADJACENCY LIST VERTEX DUMP:\n");
        for (Iterator i = vertexArray.iterator(); i.hasNext(); ) {
            aLstVertex alv = (aLstVertex) i.next();
            S = S.concat(alv.vertex.number+",");
        } 
        System.out.println(S);
    }

    public void showVertInfo(int in) {
//          System.out.println("ShowVertInfo:"+in +" of " +vertexArray.size());
//          if (in <= vertexArray.size()) {
//              aLstVertex alv = (aLstVertex) vertexArray.get(in-1);
//              System.out.print("Vertex "+in+" dump: "+alv.vertex.number+">");

//              Integer val;
//              for (Iterator i = alv.edgeList.iterator(); i.hasNext(); ) {
//                  val = (Integer) i.next();
//                  System.out.print(val+">");
//              }
//              System.out.println("");
//          }
    }


    public void clear() {
        vertexArray.clear();
        numVertices = numEdges = 0;
    }


    public double getTuran() {
	if (numVertices > 0) {
	    double edgeAvg = 0.0;
	
	    aLstVertex v = null;
	    for (Iterator i = vertexArray.iterator(); i.hasNext(); ) {
		v = (aLstVertex) i.next();
		edgeAvg = edgeAvg + v.getNumEdges();
	    }

	    TuranSetSize = numVertices /(edgeAvg/numVertices + 1.0);
	}
	return TuranSetSize;
    }


}
