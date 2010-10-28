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

public class HCgui extends Frame implements ActionListener {

    //GUI-GENERAL SETUP

    String directory; 
    String currentFile = null;
    String previousErr;

    //GUI COMPONENTS

    TextArea textarea;
    TextField tedit,aedit,bedit,iedit;
    TextField tstopedit;

    Label temperatureLbl,tstopLbl, alpha, beta, iterations;
    Button openfile;
    Button process;
    Button start,stop,pause;

    Timer execTimer;

    //HILL CLIMBER
    hillClimber climber;
    adjacencyList adjList;
    String graphDescrip;
    BitSet solution = null;

    int solLen = 0;

    //HC STATE DATA
    boolean started = false;
    boolean stopped = false;
    boolean paused = false;
    boolean continued = false;
    int currentSol = 0;


    //GUI CONSTRUCTORS... 

    public HCgui() { this(null, null); }
    public HCgui(String filename) { this(null, filename); }
    public HCgui(String directory, String filename) {

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
        climber = new hillClimber(adjList);
        
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

        tedit = new TextField(6);
        tedit.setText("10");
        tedit.addActionListener(this);

        tstopedit = new TextField(6);
        tstopedit.setText("0.75");
        tstopedit.addActionListener(this);

        aedit = new TextField(6);
        aedit.setText("0.98");
        aedit.addActionListener(this);

        bedit = new TextField(6);
        bedit.setText("1.05");
        bedit.addActionListener(this);
        
        iedit = new TextField(6);
        iedit.setText("200");
        iedit.addActionListener(this);

        temperatureLbl = new Label("initial T:");
        tstopLbl = new Label("final T:");
        alpha = new Label("alpha:");
        beta = new Label("beta:");
        iterations = new Label("iterations:");
        
        


        
        c.gridx=0; c.gridy=0; c.gridwidth = 1;c.gridheight =1;
        c.weightx = c.weighty = 0.0;
        controls.add(openfile,c);
        c.gridy=1; controls.add(start,c);
        c.gridy=2; controls.add(stop,c);
        c.gridy=3; controls.add(pause,c);
        c.gridy=4; c.gridx=0; controls.add(temperatureLbl,c);
                   c.gridx=1; controls.add(tedit,c); 
        c.gridy=5; c.gridx=0; controls.add(tstopLbl,c);
                   c.gridx=1; controls.add(tstopedit,c); 
        c.gridy=6; c.gridx=0; controls.add(alpha,c);
                   c.gridx=1; controls.add(aedit,c); 
        c.gridy=7; c.gridx=0; controls.add(beta,c);
                   c.gridx=1; controls.add(bedit,c); 
        c.gridy=8; c.gridx=0; controls.add(iterations,c);
                    c.gridx=1; controls.add(iedit,c); 
        

        c.gridx = 0; c.gridy = 0; c.gridwidth = 3; c.gridheight = 4;
        c.weightx = c.weighty = 0;
        this.add(textarea,c);

        c.gridx = 0; c.gridy = 5; c.gridwidth = 3; c.gridheight = 4;
        c.weightx = c.weighty = 0;
        this.add(climber,c);

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
            this.setTitle("INDEPENDENT SET HILL CLIMBER");  
            currentFile = new String(filename);
        }
        catch (IOException e) { 
            textarea.setText(e.getClass().getName() + ": " + e.getMessage());
            echo("\n"+filename + ": I/O Exception\n");
        }
        finally { try { if (in!=null) in.close(); } catch (IOException e) {} }
    }


    //CONVENIENCE 
    public void echo(String s) {
        textarea.append(s);
    }


    //MAIN GUI CONTROL.
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openfile) {          
            FileDialog f = new FileDialog(this, "Open File", FileDialog.LOAD);
            f.setDirectory(directory);     
            f.show();                        

            directory = f.getDirectory();  
            if ( f.getFile() != null ) {
		setFile(directory, f.getFile());
		if ( setInput() ) {
		    climber.init(adjList);
		    setHCparams();
		    solution = climber.getSolution();
		    solLen = adjList.getNumVerticies();
		    start.setEnabled(true);
		    echo("\n\nGRAPH DATA LOAD: "+currentFile+"\n");
		}
		else {
		    //FLAG ERROR  
		    textarea.append("Bad Graph Descrip...\n");
		    start.setEnabled(false);
		};
	    }
	    f.dispose();
        }
        else if (e.getSource() == start) {
            started = true;
            start.setEnabled(false);
            stop.setEnabled(true);
            pause.setEnabled(true);
            openfile.setEnabled(false);
            stopped = false;
            paused = false;
            continued = false;
	    setControlsState(false);
            setHCparams();
            climber.startClimb();
            execTimer.start();
        } 
        else if (e.getSource() == stop) {
            stopped = true;
            started = false;
            paused = false;
            continued = false;
            execTimer.stop();
            
            climber.stop();
	    echo(climber.getResults());

            currentSol = 0;
            start.setEnabled(true);
            pause.setEnabled(false);
            stop.setEnabled(false);
            openfile.setEnabled(true);
	    setControlsState(true);
        }
        else if (e.getSource() == pause) {
            if (pause.getLabel() == "Pause") {
                if (started || continued) {
                    paused = true;
                    continued = false;
                    execTimer.stop();
                    climber.setPause(true);
                    pause.setLabel("Continue");
                }
            }
            else {
                paused = false;
                continued = true;
                pause.setLabel("Pause");
                climber.setPause(false);
                execTimer.start();
            }
        } 
        else if (e.getSource() == execTimer) {
	    if (climber.done()) {
		setControlsState(true);
		stopped = true;
		started = paused = continued = false;
		execTimer.stop();
		start.setEnabled(true);
		openfile.setEnabled(true);
		pause.setEnabled(paused);
		stop.setEnabled(false);
		echo(climber.getResults());
	    } 
	
	}



    }    



    public void setHCparams() {
        // RESET PARAMS AND THEN START
        double t = Double.parseDouble(tedit.getText());
	double tfinal = Double.parseDouble(tstopedit.getText());
        double a = Double.parseDouble(aedit.getText());
        double b = Double.parseDouble(bedit.getText());
        int i = Integer.parseInt(iedit.getText());
        climber.setParams(t,tfinal,a,b,i);
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

    public void setControlsState(boolean b) {
	//..USED TO ENABLE AND DISABLE SIMANNEAL PARAMETER
	//..EDIT BOXES 
	tedit.setEnabled(b);
	tstopedit.setEnabled(b);
	aedit.setEnabled(b);
        bedit.setEnabled(b);
	iedit.setEnabled(b);
    }
    


}





