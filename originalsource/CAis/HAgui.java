import javax.swing.*;
import javax.swing.border.*;
import javax.swing.Timer.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.math.*;

public class HAgui extends Frame implements ActionListener, ItemListener {

    //GUI-GENERAL SETUP

    String directory; 
    String currentFile = null;
    String previousErr;

    //GUI COMPONENTS

    TextArea textarea;

    Button openfile;
    Button process;
    Button start,stop,pause,step;
    Checkbox graphicsOn;

    Timer execTimer;

    //INDEPENDENT SET HEURISTIC:  MASKING CUT ALGORITHM
    maskingCutter cutProcessor;
    adjacencyList adjList;
    String graphDescrip;
    BitSet solution = null;

    int solLen = 0;


    //HA STATE DATA
    boolean started = false;
    boolean stopped = false;
    boolean paused = false;
    boolean continued = false;



    //GUI CONSTRUCTORS... 
    public HAgui() { this(null, null); }
    public HAgui(String filename) { this(null, filename); }
    public HAgui(String directory, String filename) {

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


        //NON-GUI
        adjList = new adjacencyList();
        graphDescrip = new String();
        cutProcessor = new maskingCutter(adjList);
        
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
        textarea.setColumns(60);
        textarea.setRows(10);
        //        textarea.setSize(new Dimension(600,200));

        Font font = new Font("SansSerif", Font.BOLD, 14);
        openfile = new Button("Open File");
        process = new Button("Process");
        stop = new Button("Stop");
        start = new Button("Start");
        pause = new Button("Pause");
	step = new Button("STEP");
	

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
        
        pause.addActionListener(this);
        pause.setFont(font);
        pause.setForeground(new Color(0,150,150));
        pause.setEnabled(false);

        step.addActionListener(this);
        step.setFont(font);
        step.setForeground(new Color(0,150,150));
        step.setEnabled(false);

        graphicsOn = new Checkbox("Graphics");
        graphicsOn.addItemListener(this);
        graphicsOn.setEnabled(true);




        
        


        
        c.gridx=0; c.gridy=0; c.gridwidth = 1;c.gridheight =1;
        c.weightx = c.weighty = 0.0;
        controls.add(openfile,c);
        c.gridy=1; controls.add(start,c);
        c.gridy=2; controls.add(stop,c);
        c.gridy=3; controls.add(pause,c);
	c.gridy=4; controls.add(step,c);
        c.gridy=5; controls.add(graphicsOn,c);

        c.gridx = 0; c.gridy = 0; c.gridwidth = 3; c.gridheight = 4;
        c.weightx = c.weighty = 0;
        this.add(textarea,c);

        c.gridx = 0; c.gridy = 5; c.gridwidth = 3; c.gridheight = 4;
        c.weightx = c.weighty = 0;
        this.add(cutProcessor,c);

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
            this.setTitle("FileViewer: " + filename);  
            currentFile = new String(filename);
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
        if (e.getItemSelectable() == graphicsOn   ) {
        }
    }

    
    //MAIN GUI CONTROL.
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openfile) {          

            FileDialog f = new FileDialog(this, "Open File", FileDialog.LOAD);
            f.setDirectory(directory);     
            f.show();                        

            directory = f.getDirectory();  
            setFile(directory, f.getFile());

            f.dispose();

            if ( setInput() ) {

                cutProcessor.init(adjList);
                //solution = cutProcessor.getSolution();
                solLen = adjList.getNumVertices();
                start.setEnabled(true);
		

                echo("GRAPH DATA LOAD: "+currentFile+"\n");
		echo("TURAN SET SIZE: " + adjList.getTuran()+"\n");

            }
            else {
                //FLAG ERROR  
                textarea.append("Bad Graph Descrip...\n");
                start.setEnabled(false);
            };
        }
        else if (e.getSource() == start) {
            started = true;
            start.setEnabled(false);
            stop.setEnabled(true);
            pause.setEnabled(true);
	    step.setEnabled(true);
            openfile.setEnabled(false);
            stopped = false;
            paused = false;
            continued = false;
            cutProcessor.startCutting();
            execTimer.start();
        } 
        else if (e.getSource() == stop) {
            stopped = true;
            started = false;
            paused = false;
            continued = false;
            execTimer.stop();
            
            cutProcessor.stop();

            start.setEnabled(true);
            pause.setEnabled(false);
            stop.setEnabled(false);
	    step.setEnabled(false);
            openfile.setEnabled(true);
        }
        else if (e.getSource() == pause) {
            if (pause.getLabel() == "Pause") {
                if (started || continued) {
                    paused = true;
                    continued = false;
                    execTimer.stop();
                    cutProcessor.setPause(true);
                    pause.setLabel("Continue");
                }
            }
            else {
                paused = false;
                continued = true;
                pause.setLabel("Pause");
                cutProcessor.setPause(false);
                execTimer.start();
            }
        } 
	else if (e.getSource() == step) {
	    cutProcessor.step();
	}
        else if (e.getSource() == execTimer) {

	    if (!cutProcessor.isProcessing()) {
		stopped = true;
		started = paused = continued = false;
		execTimer.stop();
		start.setEnabled(true);
		openfile.setEnabled(true);
		pause.setEnabled(paused);
		stop.setEnabled(false);
		echo("|IS|: " + cutProcessor.getNumOn() +"\n\n");
	    } 
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









