/**
 *  Graphical User Interface to setup and control the 
 *  Genetic Algorithm for Independent Set.
 *
 *
 *
 *
 **/

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.Timer.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.math.*;


public class GAprocessor extends Frame implements ActionListener, ItemListener {

    //GUI-GENERAL SETUP

    String directory; 
    String previousErr;

    //GUI COMPONENTS

    TextArea textarea;
    TextField iterate,xEdit,mEdit;
    TextField pSizeSelect;

    Label chromo;
    Label stopAt;
    Label mRate, xRate;
    Label pSizeLBL;

    Button openfile;
    Button process;
    Button start,stop,pause,reinitBtn;
    Checkbox elitism;
    Checkbox masking;
    Checkbox twoBitMutation;
    Checkbox custMutation;
    Checkbox rotateMutation;
    CheckboxGroup xovers;
    CheckboxGroup mutations;
    Checkbox uXover;
    Checkbox pnt2Xover;




    Timer execTimer;


    //GENETIC ALGORITHM REQUIREMENTS

    adjacencyList adjList;
    String graphDescrip;
    GApopulation population;

    int stopGen = 0;
    int popSize = 100;
    int chromoLen = 0;

    //GA STATE DATA
    boolean started = false;
    boolean stopped = false;
    boolean paused = false;
    boolean continued = false;



    //GUI CONSTRUCTORS... 

    public GAprocessor() { this(null, null); }
    public GAprocessor(String filename) { this(null, filename); }

    public GAprocessor(String directory, String filename) {

        //GUI STUFF FIRST

        super();  
        
        if (directory == null) {
            File f;
            if ((filename != null)&& (f = new File(filename)).isAbsolute()) {
                directory = f.getParent();
                filename = f.getName();
            }
            else directory = System.getProperty("user.dir");
        }
	
        this.directory = directory;   
        setFile(directory, filename); 


        //GA REQUIREMENTS

        adjList = new adjacencyList();
        graphDescrip = new String();
        population = new GApopulation(popSize,adjList);
        
        
        
        
        
        
        // GUI STUFF      
        addWindowListener(new WindowAdapter() {
     		public void windowClosing(WindowEvent e) { dispose(); }
	    });

        execTimer = new Timer(1,this);
        
        this.setBackground(Color.lightGray);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5,5,5,5);

        JPanel controls = new JPanel();
        controls.setLayout(new GridBagLayout());
        controls.setBorder(new EtchedBorder());
        controls.setBackground(new Color(150,150,150));

        textarea = new TextArea("", 24, 80);
        textarea.setFont(new Font("MonoSpaced", Font.PLAIN, 12));
        textarea.setRows(20);

        Font font = new Font("SansSerif", Font.BOLD, 14);
        openfile = new Button("Open File");
        process = new Button("Process");
        stop = new Button("Stop");
        start = new Button("Start");
        pause = new Button("Pause");
	reinitBtn = new Button("Re-Initialize");


        openfile.addActionListener(this);
        openfile.setActionCommand("open");
        openfile.setFont(font);
        
        start.addActionListener(this);
        start.setFont(font);
        start.setForeground(new Color(0,0,150));
        start.setEnabled(false);

        stop.addActionListener(this);
        stop.setFont(font);
        stop.setForeground(new Color(150,0,0));
        stop.setEnabled(false);

        reinitBtn.addActionListener(this);
        reinitBtn.setFont(font);
        reinitBtn.setForeground(new Color(0,0,150));
        reinitBtn.setEnabled(false);        

        pause.addActionListener(this);
        pause.setFont(font);
        pause.setForeground(new Color(0,150,150));
        pause.setEnabled(false);

        stopAt = new Label("STOP AT:");
        mRate = new Label("Mut. Rate:");
        xRate = new Label("Cross. Rate:");
        pSizeLBL = new Label("POPULATION SIZE:");

        xEdit = new TextField(6);
        xEdit.setText("0.95");
        xEdit.addActionListener(this);

        mEdit = new TextField(6);
        mEdit.setText("0.03");
        mEdit.addActionListener(this);

        iterate = new TextField(6);
        iterate.setText("1000");
        iterate.addActionListener(this);

        pSizeSelect = new TextField(6);
        pSizeSelect.setText("100");
        pSizeSelect.addActionListener(this);

        elitism = new Checkbox("Elitism");
        elitism.addItemListener(this);

        masking = new Checkbox("Masking");
        masking.addItemListener(this);

	xovers = new CheckboxGroup();
	uXover = new Checkbox("Uniform Xover",true,xovers);
	uXover.addItemListener(this);
	pnt2Xover = new Checkbox("Dual Pnt Xover",false,xovers);
	pnt2Xover.addItemListener(this);

	mutations = new CheckboxGroup();
        twoBitMutation = new Checkbox("TwoBit Mutation",true,mutations);
        twoBitMutation.addItemListener(this);
        custMutation = new Checkbox("Custom Mutation",false,mutations);
        custMutation.addItemListener(this);
        rotateMutation = new Checkbox("R-Rotate Mutation",false,mutations);
        rotateMutation.addItemListener(this);

        chromo = new Label("[CHROMOSOME 1]");
        chromo.setFont(textarea.getFont());
        

        c.gridx=0; c.gridy=0; c.gridwidth = 1;c.gridheight =1;
        c.weightx = c.weighty = 0.0;
        controls.add(openfile,c);
        c.gridy=1; controls.add(start,c);
        c.gridy=2; controls.add(stop,c);
	c.gridy=3; controls.add(reinitBtn,c);
        c.gridy=4; controls.add(pause,c);
        c.gridy=5; controls.add(elitism,c);
	c.gridy=6; controls.add(uXover,c);
	c.gridy=7; controls.add(pnt2Xover,c);
        c.gridy=8; controls.add(masking,c);
	c.gridy=9; controls.add(twoBitMutation,c);
        c.gridy=10; controls.add(custMutation,c);
	c.gridy=11; controls.add(rotateMutation,c);

        c.gridy=13; controls.add(xRate,c);
        c.gridy=14; controls.add(xEdit,c);
        c.gridy=15; controls.add(mRate,c);
        c.gridy=16; controls.add(mEdit,c);	

        c.gridy=17; controls.add(stopAt,c);
        c.gridy=18; controls.add(iterate,c);
        c.gridy=19; controls.add(pSizeLBL,c);
	c.gridy=20; controls.add(pSizeSelect,c);

        

        c.gridx = 0; c.gridy = 4; c.gridwidth = 3; c.gridheight = 4;
        c.weightx = c.weighty = 0;
        this.add(textarea,c);

        c.gridx = 0; c.gridy = 0; c.gridwidth = 3; c.gridheight = 3;
        c.weightx = c.weighty = 1;
        this.add(population);

        c.gridx = 4;c.gridy = 0;c.gridwidth=3;c.gridheight = 9 ;
        c.weightx = c.weighty = 0;
        this.add(controls,c);

        this.pack();
	
    }


    //GET DESIRED FILE.  READ IT INTO BUFFER
    public void setFile(String directory, String filename) {
        if ((filename == null) || (filename.length() == 0)) return;
        File f;
        FileReader in = null;

        try {
            f = new File(directory, filename); 
            in = new FileReader(f);            
            char[] buffer = new char[4096];    
            int len;                           
            graphDescrip = "";
            while((len = in.read(buffer)) != -1) { 
                String s = new String(buffer, 0, len); 
                graphDescrip = graphDescrip.concat(s);                    
            }
            this.setTitle(filename);  
        }
        catch (IOException e) { 
            textarea.setText(e.getClass().getName() + ": " + e.getMessage());
            this.setTitle("FileViewer: " + filename + ": I/O Exception");
        }
        finally { try { if (in!=null) in.close(); } catch (IOException e) {} }
    }


    //CONVENIENCE 
    public void echo(String s) {
        textarea.append(s);
    }



    //GUI CONTROL
    public void itemStateChanged(ItemEvent e) {
        if (e.getItemSelectable() == elitism) {
            population.setElitism(elitism.getState());
        }
        else if (e.getItemSelectable() == masking) {
            population.setMasking(masking.getState());
        }
        else if (e.getItemSelectable() == custMutation) {
            if (custMutation.getState()) {
                population.setMutationType(1);
            }
	}
        else if (e.getItemSelectable() == twoBitMutation) {
            if (twoBitMutation.getState()) {
                population.setMutationType(0);
            }
	}
        else if (e.getItemSelectable() == rotateMutation) {
            if (rotateMutation.getState()) {
                population.setMutationType(2);
            }
	}
	else if (e.getItemSelectable() == uXover) {
	    if (uXover.getState()) { population.setCrossOver(0);}
	}
	else if (e.getItemSelectable() == pnt2Xover) {
	    if (pnt2Xover.getState()) { population.setCrossOver(1);}	    
	}
    }

    
    //MAIN GUI CONTROL.   NOTE: SHOULD SEPERATE THIS FROM 
    // GUI TO GApopulation CLASS's PANEL.  
    //

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openfile) {          

            FileDialog f = new FileDialog(this, "Open File", FileDialog.LOAD);
            f.setDirectory(directory);     
            f.show();                        

            directory = f.getDirectory();  
	    if (f.getFile() != null) {
		setFile(directory, f.getFile());
		if ( setInput() ) {
		    population.setAdjacencyList(adjList);
		    chromoLen = adjList.getNumVerticies();
		    start.setEnabled(true);
		}
		else {
		    //FLAG ERROR  
		    textarea.append("Bad Graph Descrip...\n");
		    start.setEnabled(false);
		};
	    }
	    f.dispose();
        }
        else if (e.getSource() == xEdit) {
            double i = Double.parseDouble(xEdit.getText());
	    population.setXrate(i);
        }
        else if (e.getSource() == mEdit) {
            double i = Double.parseDouble(mEdit.getText());
	    population.setMrate(i);
        }
        else if (e.getSource() == iterate) {
            int i =Integer.parseInt(iterate.getText());
            if (i > population.getGenNum()) {
                stopGen = i;
		population.setStopGen(stopGen);
            } 
	    else { 
		iterate.setText(Integer.toString(population.getGenNum())); 
	    }
        }
	//..PROCESS POPULATION RESIZE REQUEST       	
	else if(e.getSource() == pSizeSelect) {
	    int i = Integer.parseInt(pSizeSelect.getText());
	    if (i < 20) {i = 20; pSizeSelect.setText(Integer.toString(i)); }
	    if (i > 800) {i = 800; pSizeSelect.setText(Integer.toString(i)); }
	    if ((i % 2) != 0) { i = i +1;}
	    population.setPopSize(i);
	}
	//..PROCESS POPULATION RESIZE REQUEST	
        else if (e.getSource() == start) {
            stopGen = Integer.parseInt(iterate.getText());
            started = true;
            start.setEnabled(false);
            stop.setEnabled(true);
            pause.setEnabled(true);
            openfile.setEnabled(false);
            stopped = false;
            paused = false;
            continued = false;
	    setControlsActive(false);

            population.setStopGen(stopGen);
            population.start();

            execTimer.start();
        } 
        else if (e.getSource() == stop) {
            stopped = true;
            started = false;
            paused = false;
            continued = false;
            execTimer.stop();

            population.stop();
	    showGAresults(0);

            start.setEnabled(false);
	    reinitBtn.setEnabled(true);
            pause.setEnabled(false);
            stop.setEnabled(false);
            openfile.setEnabled(true);
	    setControlsActive(true);

        }
	else if (e.getSource() == reinitBtn) {
	    population.init();   //..RE-INITIALIZE
	    start.setEnabled(true);
	    reinitBtn.setEnabled(false);
	    setControlsActive(true);
	    
	    
	}
        else if (e.getSource() == pause) {
            if (pause.getLabel() == "Pause") {
                if (started || continued) {
                    paused = true;
                    continued = false;
                    execTimer.stop();
                    population.setPause(true);
                    pause.setLabel("Continue");
                }
            }
            else {
                paused = false;
                continued = true;
                pause.setLabel("Pause");
                population.setPause(false);
                execTimer.start();
            }
        } 
        else if (e.getSource() == execTimer) {
            paused = true;
            continued = false;
            paused = false;
            continued = true;


            if (!population.isProcessing()) {
                stopped = true;
                started = paused = continued = false;
                execTimer.stop();

                population.stop();
		showGAresults(0);

                start.setEnabled(false);
		reinitBtn.setEnabled(true);
		openfile.setEnabled(true);
                pause.setEnabled(paused);
                stop.setEnabled(false);
            }

        }



    }    


    public void setControlsActive(boolean a) {
	elitism.setEnabled(a);
	uXover.setEnabled(a);
	pnt2Xover.setEnabled(a);
	masking.setEnabled(a);
	twoBitMutation.setEnabled(a);
	custMutation.setEnabled(a);
	rotateMutation.setEnabled(a);
	pSizeSelect.setEnabled(a);


    }





    public void showGAresults(int detail) {
	echo(getTitle()+"\n");
	echo(population.getResultsInfo()+ "\n\n");
	if (detail > 0) {
	    textarea.append(population.dump(0));
	}
    }



    public boolean setInput() {
        return parseInput();  //RESULT FROM parseInput METHOD
    }



    public boolean parseInput() {
        StringReader reader;
        StreamTokenizer parser;
        boolean expectVertex = true;
        boolean expectSink = false;
        int from, to, inVal;
        from = to = 0;

        try {

            adjList.clear();

            reader = new StringReader(graphDescrip);
            parser = new StreamTokenizer(reader);
            parser.eolIsSignificant(true);

            while (parser.nextToken() != StreamTokenizer.TT_EOF) {
                if (parser.ttype == StreamTokenizer.TT_WORD) {
                }
                else if (parser.ttype == StreamTokenizer.TT_NUMBER) {
                    inVal = (int) parser.nval;
                    if (expectVertex) {
                        from = (int) inVal;
                        adjList.addVertex(49);
                        expectVertex = false;
                        expectSink = true;
                    }
                    else if (expectSink) {
                        to = (int) inVal;
                        adjList.addEdge(from,to);
                    }


                }
                else if (parser.ttype == StreamTokenizer.TT_EOL) {
                    expectSink = false;
                    expectVertex = true;
                }
            }
            
            //ERROR MESSAGE OUTPUT
//              if () {
//                  return false;
//              }
//              else if () {
//                  return false;
//              }
//              else if () {
//                  previousErr = previousErr.concat("???????///");
//                  return false;
//              }
//              else if () {
//                  previousErr = previousErr.concat("???????????///");
//                  return false;
//              }
            return true;
          }
          catch (IOException e) {
              previousErr = previousErr.concat(e.getMessage());
              return false;
          }

    }


    


}





