/**
 *
 *
 *
 *
 **/

import java.awt.*;
import java.awt.event.*;

public class HC  {
    public static void main(String[] args) {
        HCgui f = new HCgui(null);
        f.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) { System.exit(0); }
            });
        f.show();
    }


}
