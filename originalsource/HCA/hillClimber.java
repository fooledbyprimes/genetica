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


public class hillClimber extends JPanel implements Runnable {


    public Surface surf;
    JPanel controls;
    boolean doControls;


    boolean hasAdjList = false;
    boolean initializedHC = false;
    boolean mustReInit = false;
    boolean processingHC = false;
    boolean paramsSet = false;
    boolean paused = false;
    boolean prematureStop = false;

    Thread  climber;
    adjacencyList adjList;
    Random sRand;
    //    Random selectionRand;
    BitSet solution = null;
    BitSet newSolution = null;
    BitSet totalBad = null;

    int numNewSolutions = 0;
    int solLen;

    int seed1;

    long algoTime = 0;

    //PARAMS

    double userTemp  = 10.0;
    double userStopT  = 1.0;
    int    userIters = 200;
    double userAlpha = 0.91;
    double userBeta  = 1.1;

    double currentTemp  = 100.0;
    int    currentIters = 200;

    double hofs = 0;
    double hnew = 0;




    public hillClimber(adjacencyList adj) {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder(new EtchedBorder(), "Hill Climber"));
        add(surf = new Surface());
        controls = new JPanel();
        controls.setPreferredSize(new Dimension(135,80));
        Font font = new Font("serif", Font.PLAIN, 10);
        
        //HC STUFF
        adjList = adj;
        seed1 = 12032;
        sRand = new Random(seed1);
    }






    public void setPause(boolean in) {
        paused = in;
    }





    public void startClimb() {

        if (!processingHC && initializedHC && paramsSet) {
            processingHC = true;
	    algoTime = 0;

            climber = new Thread(this);
            climber.setPriority(Thread.MIN_PRIORITY);
            climber.setName("hill climber");
            climber.start();
        }

    }



    public void stop() {
        processingHC = false;
        mustReInit = true;
        surf.stop();
    }


    public void run() {
        
        Thread me = Thread.currentThread();
        long timeNow;

        while (climber == me && processingHC) {
            if (mustReInit) {
                initialize();
                mustReInit = false;
            }
            
            if (!paused) {
		timeNow = System.currentTimeMillis();
                boolean useBadSol;
                for (int i = 1; i <= currentIters; i++) {
                    perturb();
                    double tmp = sRand.nextFloat();
                    double prob = Math.exp((hofs - hnew) / currentTemp);
                    useBadSol = tmp < prob;
                    if ( ( hnew < hofs ) ) {
                        solution = newSolution;
                        hofs = hnew;
                    }
                }
                surf.setSolution(solution);
                surf.setTemperature(currentTemp);
                surf.setH(hofs);
                
            
                currentTemp = userAlpha*currentTemp;
                currentIters = (int) userBeta*currentIters;
		
		algoTime += System.currentTimeMillis() - timeNow;

                numNewSolutions++;
            
                if (currentTemp <= userStopT) {
                    processingHC = false;
                    mustReInit = true;
                }


            }
            climber.yield();
        }
        
        climber = null;
        
    }











    private void initialize() {
        // RESET
        hofs = 0;
        hnew = 0;
        currentTemp  = userTemp;
        currentIters = userIters;
        sRand.setSeed(seed1);
        numNewSolutions = 0;
        solLen = adjList.getNumVerticies();
        solution = new BitSet(solLen);
        
        // GENERATE INITIAL SOLUTION
        boolean notFeasible = false;
        //            while (!notFeasible) {
        for (int i = 1; i <= solLen; i++) {
            if (sRand.nextBoolean()) {
                solution.set(i);
            }
            if (!computeFeasibility(solution)) { 
                solution.clear(i); 
            }
            
        }
        if (computeFeasibility(solution))  {
            notFeasible = false;
        }
        //            }
        initializedHC = true;

        surf.setSolutionSize(adjList.getNumVerticies());
        surf.setSolution(solution);
        surf.setTemperature(currentTemp);
        surf.setH(hofs);
        surf.start();



    }



    public boolean computeFeasibility(BitSet bits) {
        aLstVertex v1,v2;
        totalBad = new BitSet(solLen);
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
        hnew = -numSetBits;
        
        return (badNum == 0);
    }


    public void perturb() {
        boolean found = false;

        while (!found) {
            int index1 = (int) (sRand.nextFloat() * solLen);
            int index2 = (int) (sRand.nextFloat() * solLen);
        
            newSolution = new BitSet(solLen);
            newSolution.or(solution);
        
            while ( index2 == index1 ) {
                index2 = (int) (sRand.nextFloat() * solLen);
            }
            
            
            if (newSolution.get(index1)) {
                newSolution.clear(index1);
            }
            else {
                newSolution.set(index1);
            }
            
            if (newSolution.get(index2)) {
                newSolution.clear(index2);
            }
            else {
                newSolution.set(index2);
            }
            
            found = computeFeasibility(newSolution);
        }

    }



    public void setParams(double tin, double tfinal, double a, double b, int iters) {
        if (!processingHC) {
            userTemp  = tin;
	    userStopT = tfinal;
            userIters = iters;
            userAlpha = a;
            userBeta = b;
            paramsSet = true;
            mustReInit = true;
        }
    }



    public void init(adjacencyList a) {
        if (!processingHC) {
            adjList = a;
            hasAdjList = true;
            initialize();
        }
    }

    public boolean processing() {
        return processingHC;
    }
	

	public BitSet getSolution() {
		return solution;
	}

    public double getCurrentTemp() { return currentTemp; }

    public int getCurrentSolNum() { return numNewSolutions; }

    
    public boolean done() {
        return !processingHC;
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
        private BitSet solBits = null;
        private boolean initOk = false;
        private double tempVal = 0.0;
        private double hofs = 0.0;

        public Surface() {
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
        
        public void setTemperature(double in) {
            tempVal = in;
        }

        public void setH(double in) {
            hofs = in;
        }


        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        public Dimension getPreferredSize() {
            return new Dimension(135,80);
        }

            
        public void paint(Graphics g) {

            if (big == null) {
                return;
            }


            big.setBackground(getBackground());
            big.clearRect(0,0,w,h);

            // .. Draw allocated and used strings ..
            big.setColor(Color.green);
            big.drawString("h: " + Double.toString(hofs),  4.0f, (float) ascent+0.5f);
            big.drawString("Temperature: " + Double.toString(tempVal), 4, h-descent);


            big.translate(4,20);
            int xhome = 4;
            int yhome = 20;

            // .. Draw current solution ..
            if (initOk) {
                Rectangle2D rrect;
                int j = 1; 
                boolean toggleLineColor = false;
                for (Iterator i = shapestream.iterator(); i.hasNext(); ) {
                    rrect = (Rectangle2D) i.next();
                    if (solBits.get(j)) {
                        big.setColor(Color.blue);
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
                    if (( (j-1) % 90 ) == 89 ) {
                        big.translate(-90*4,4); 
                        xhome += -90*4;
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
            thread.setName("Painter");
            thread.start();
            
        }


        public synchronized void stop() {
            thread = null;
            notify();
        }


        public void run() {

            Thread me = Thread.currentThread();

            while (thread == me && !isShowing() || getSize().width == 0) {
                try {
                    thread.sleep(500);
                } catch (InterruptedException e) { return; }
            }
    
            while (thread == me && isShowing()) {
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
                }
                repaint();

                try {
                    thread.sleep(sleepAmount);
                } catch (InterruptedException e) { break; }
            }
            thread = null;
        }
    }


    public String getResults() {
	String s = new String("");
	s = s.concat("STARTING TEMP: " + userTemp + "\n");
	if (prematureStop) {
	    s = s.concat("   FINAL TEMP: " + userStopT + "\n");
	}
	else {
	    double tmp = currentTemp / userAlpha;
	    s = s.concat("   FINAL TEMP: " + tmp + "\n");
	}
	s = s.concat("   ITERATIONS: " + userIters + "\n");
	s = s.concat("        ALPHA: " + userAlpha + "\n");
	s = s.concat("         BETA: " + userBeta + "\n");
	s = s.concat("     TIME(ms): " + algoTime + "\n");
	s = s.concat("|IndependentSet| = " + (int) (-hnew) + "\n");

	return s;
    }








}
