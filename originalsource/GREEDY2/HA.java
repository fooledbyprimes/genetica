/**
 *
 *
 *
 *
 **/

import java.awt.*;
import java.awt.event.*;

public class HA  {
    public static void main(String[] args) {
        HAgui f = new HAgui(null);
        f.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) { System.exit(0); }
            });
        f.show();
    }


}
