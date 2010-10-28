/**
 *
 *
 *
 *
 **/

import java.awt.*;
import java.awt.event.*;

public class SA  {
    public static void main(String[] args) {
        SAgui f = new SAgui(null);
        f.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) { System.exit(0); }
            });
        f.show();
    }


}
