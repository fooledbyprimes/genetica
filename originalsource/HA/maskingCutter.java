import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.math.*;
import java.util.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class maskingCutter extends JPanel implements Runnable {


    public Surface surf;


    boolean hasAdjList = false;
    boolean processingHA = false;
    boolean paused = true;
    boolean initializedHA = false;


    Thread  cutter;
    adjacencyList adjList;
    BitSet solution = null;
    int solLen;
    int numMasked = 0;


    public maskingCutter(adjacencyList adj) {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder(new EtchedBorder(), "MASKING CUTTER"));

	surf = new Surface();
        this.add(surf);

        adjList = adj;
    }


    public void setPause(boolean in) {
        paused = in;
    }

    public void startCutting() {

        if (paused && initializedHA) {
            processingHA = true;
            cutter = new Thread(this);
            cutter.setName("Cutter");
            cutter.start();
	    paused = false;
        }

    }



    public void stop() {
        paused = true;
	processingHA = false;
        //surf.stop();
    }


    public void run() {
	aLstVertex v,s,s2;
	int sink = 0;
	int edges,index;
	int least = 9999;
	int most = 0;
	int cuts = -1;
	boolean found = false;
	boolean hadFirstCut = false;


        while (processingHA) {
            if (!paused) {
		cutter.setPriority(Thread.NORM_PRIORITY);

		//DEBUG//System.out.println("START ONE PASS");
		//..DO ONE PASS OF ALGORITHM

		//..PICK VERTEX WITH LEAST EDGES (DON'T CARE ABOUT TIES)
		//..DONT PICK VERTICIES WITH ZERO EDGES.. INSTEAD MASK THEM.
		//DEBUG//System.out.println("PICK VERTEX");
		index = 1;
		found = false;

		sink = 0;
		for (Iterator i = adjList.vertexArray.iterator(); i.hasNext() && !found;index++ ) {
		    v = (aLstVertex) i.next();
		    
		    if (!v.vertex.selected && 
			 ((v.getNumCuts() > 0) || (!hadFirstCut) )  ) {

			edges = v.getNumEdges();
			cuts = v.getNumCuts();
			//DEBUG//System.out.print(index+"."+"e:"+edges+".c:"+cuts);
			if (edges == 0) {
			    v.vertex.selected = true;
			    solution.set(index);
			    numMasked++;   //SAVE FOR TERMINATION CHECKING
			}
			else if (cuts >= most) {
			    most = cuts;

			    if (edges < least) {
				least = edges;
				sink = index;
			    }
			    
			}
			//DEBUG//System.out.println(".l:"+least+".m:"+most);
	  
		    }

		}
		
		//RESET COMPARISON VARIABLES
		least = 9999;
		most = 0;

		
		//..MASK ADJACENT VERTICIES;
		
		//DEBUG//System.out.println("MASK; sink:"+sink + " masked:"+numMasked);
		if (sink > 0) {
		    v = (aLstVertex) adjList.vertexArray.get(sink - 1);    //SOURCE
		    v.vertex.selected = true;
		    solution.set(sink);
		    numMasked++;

		    
		    int nexts = 0;
		    for (int i = 1; i <= solLen; i++) {        //MASK ALL SINKS

			if (v.connections.get(i)) {            //CHECK IF SINK
			    //DEBUG//System.out.println("   sink."+i);
			    s = (aLstVertex) adjList.vertexArray.get(i-1);
			    s.vertex.selected = true;          //MASK

			    
			    int bitIndex = 1;                  //CUT EDGES @ ALL MASKED VERTICIES
			    for (int j = 1; j <= s.edges; j++) {
				while(bitIndex <= solLen) {
				    if (s.connections.get(bitIndex)) {
					s2 = (aLstVertex) adjList.vertexArray.get(bitIndex - 1);
					if (!s2.cutEdges.get(i)) {
					    s2.cutEdge(i);
					    //DEBUG//System.out.println("   s2."+bitIndex+": cut."+i);
					    s.cutEdge(bitIndex);
					    //DEBUG//System.out.println("   s ."+bitIndex+": cut."+bitIndex);
					}

					hadFirstCut = true;
				    }
				    bitIndex++;
				}
			    }
			}
		    }
		}
		else {  //..NO SINK FOUND SO DONE
                    System.out.println("cutter.finished = true");		    

		    //compute fitness for comparison to GA
		    //

		    System.out.println("FITNESS:" + getFitness());


		    paused = true;
		}

		//DEBUG//System.out.println("END ONE PASS");
		surf.setSolution(solution);
	    } else {
		cutter.setPriority(Thread.MIN_PRIORITY);
	    }
	    cutter.yield();
	}
	cutter = null;


    }
    

    public void step() { 
	paused = false;
    }

    private void initialize() {
        solLen = adjList.getNumVerticies();
        solution = new BitSet(solLen);
        initializedHA = true;
    }




    public boolean computeFeasibility(BitSet bits) {
        aLstVertex v1,v2;
        BitSet totalBad = new BitSet(solLen);
        BitSet tempBits = new BitSet(solLen);

        int badNum = 0;
        int numSetBits =0;
        boolean bad = false;


        for (int j = 1; j <= solLen; j++ ) {
            if (bits.get(j)) {
                numSetBits++;
                v1 = (aLstVertex) adjList.vertexArray.get(j-1);
                for (int l = 1; l <= solLen; l++) {
                    if (v1.connections.get(l)) {tempBits.set(l);}
                }
                tempBits.and(bits);
                
                // HOW MANY BITS ARE BAD? (LOWERING THE FITNESS VAL) 
                
                bad = false;
                for (int k = 1; (k <= solLen) && !bad; k++) {
                    if (tempBits.get(k)) {bad = true;}
                }
                if (bad) {badNum++;} 
                
                totalBad.or(tempBits);
                tempBits.andNot(tempBits);
            }
        }
	//        hnew = -numSetBits;
        
        return (badNum == 0);
    }


    public void init(adjacencyList a) {
        if (paused) {
            adjList = a;
            hasAdjList = true;
            initialize();
            surf.setSolutionSize(adjList.getNumVerticies());
	    solution = new BitSet();
	    surf.setSolution(solution);
	    surf.start();
	    surf.repaint();
        }
    }

    public boolean isProcessing() {
        return !paused;
    }
	

    public BitSet getSolution() {
	return solution;
    }


    //..THE FOLLOWING FUNCTION IS USED TO COMPARE TO A GA ALGORITHM'S FITNESS
    private double getFitness() {
        aLstVertex v1,v2;
        BitSet totalBad = new BitSet(solLen);
        BitSet tempBits = new BitSet(solLen);

	
	int badNum = 0;
	int numSetGenes =0;
	boolean bad = false;
	for (int j = 1; j <= solLen; j++ ) {
	    if (solution.get(j)) {
		numSetGenes++;
		v1 = (aLstVertex) adjList.vertexArray.get(j-1);
		for (int l = 1; l <= solLen; l++) {
		    if (v1.connections.get(l)) {tempBits.set(l);}
		}
		tempBits.and(solution);
		
		// HOW MANY GENES ARE BAD? (LOWERING THE FITNESS VAL) 
		
		bad = false;
		for (int k = 1; (k <= solLen) && !bad; k++) {
		    if (tempBits.get(k)) {bad = true;}
		}
		if (bad) {badNum++;} 
		
		// 'OR' THE BAD VERTS TO THE TOTAL-ACCUMULATED-BITSET.
		// IN THIS CASE THE 'BAD' VERTS ARE DUE TO A PARTICULAR
		// GENE HAVING BEEN SET (VERTEX SELECTED).
		// THIS WILL BE STORED LATER IN THE CHROMOSOME.
		// I.E.: THE CHROMOSOME WILL STORE A BITSET WHICH 
		// REPRESENTS THE BAD VERTICIES.  IF A BIT IS SET
		// THEN THE VERTEX WITH THE CORRESPONDING BIT INDEX
		// IS CAUSING PROBLEMS IN THE FITNESS.
		
		totalBad.or(tempBits);
		tempBits.andNot(tempBits);
	    }
	    
	}
	
	
	
	//STORE BAD VERTICIES IN A BITSTREAM REPRESENTATION
	int goodNum = numSetGenes - badNum;
	if (goodNum < 0) { goodNum = 0; }
	System.out.println("100*"+goodNum+"/"+solLen+"  *  (1-"+badNum+"/"+solLen+")");
	System.out.println("bad:"+badNum);

	return  ( 100*goodNum/solLen *(1-badNum/solLen) );
	
    }
    







    //************************************

    public class Surface extends JPanel implements Runnable {


        
        public Thread thread;
        public long sleepAmount = 1000;
        private int w, h;
        private BufferedImage bimg;
        private Graphics2D big;
        private Font font = new Font("Times New Roman", Font.PLAIN, 11);
        private int columnInc;
        private int ascent, descent;
        private ArrayList shapestream = null;
        private BitSet solBits;
        private boolean initOk = false;


        public Surface() {
	    solBits = new BitSet();
            setBackground(Color.black);
        }



        public void setSolutionSize(int sizeIn) {
            if (sizeIn > 0) {
                shapestream = new ArrayList();
                Rectangle2D rrect;
                for (int i = 1; i <= sizeIn; i++) {
                    rrect = new Rectangle2D.Float(0,0,4,4);
                    shapestream.add(rrect);
                }
                initOk = true;
            }
        }


        public void setSolution(BitSet solBitsIn) {
            solBits = solBitsIn;
            repaint();
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        public Dimension getPreferredSize() {
            return new Dimension(150,150);
        }

            
        public void paint(Graphics g) {

            if (big == null) {
                return;
            }


            big.setBackground(getBackground());
            big.clearRect(0,0,w,h);

            // .. Draw allocated and used strings ..
            big.setColor(Color.green);
            big.drawString("??",  4.0f, (float) ascent+0.5f);
            //big.drawString("Temperature: " + Double.toString(tempVal), 4, h-descent);


            big.translate(20,20);
            int xhome = 20;
            int yhome = 20;

            // .. Draw current solution ..
            if (initOk) {
                Rectangle2D rrect;
                int j = 1; 
                boolean toggleLineColor = false;
                for (Iterator i = shapestream.iterator(); i.hasNext(); ) {
                    rrect = (Rectangle2D) i.next();
                    if (solBits.get(j)) {
                        big.setColor(Color.red);
                    }
                    else {
                        big.setColor(Color.lightGray);
                    }
                    big.fill(rrect);
		    big.setColor(Color.white);
                    big.draw(rrect);
                    big.translate(4,0);
                    xhome += 4;
                    toggleLineColor = !toggleLineColor;
                    if (( (j-1) % 100 ) == 99 ) {
                        big.translate(-100*4,4); 
                        xhome += -100*4;
                        yhome += 4;
                    }
                    j++;
                }
                big.translate(-xhome,-yhome);   
            }
            g.drawImage(bimg, 0, 0, this);
        }


        public void start() {
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setName("MaskingCutter's Painter");
            thread.start();
        }


        public synchronized void stop() {
            thread = null;
            notify();
        }


        public void run() {

            while (!isShowing() || getSize().width == 0) {

                try {
                    thread.sleep(500);
                } catch (InterruptedException e) { return; }
            }
    
            while (isShowing()) {
		thread.setPriority(Thread.NORM_PRIORITY);
                Dimension d = getSize();
                if (d.width != w || d.height != h) {
                    w = d.width;
                    h = d.height;
                    bimg = (BufferedImage) createImage(w, h);
                    big = bimg.createGraphics();
                    big.setFont(font);
                    FontMetrics fm = big.getFontMetrics(font);
                    ascent = (int) fm.getAscent();
                    descent = (int) fm.getDescent();
		    repaint();
                }
		thread.yield();
            }
            thread = null;
        }
    }


}




