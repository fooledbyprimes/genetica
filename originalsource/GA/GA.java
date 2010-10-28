/**
 *
 *  Program Entry point.  Create main gui window.
 *
 *
 **/

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class GA  {
    public static void main(String[] args) {
        Frame f = new GAprocessor(null);
        f.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) { System.exit(0); }
            });
	f.setResizable(false);
        f.show();
    }


}
