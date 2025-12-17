package org.tectuinno.view.component;

import java.awt.Color;
import java.awt.Window;
import javax.swing.JDialog;

public class StyledDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public StyledDialog(Window owner, String title, ModalityType modalityType) {
        super(owner, title, modalityType);

        // Apply Andromeda theme colors to title bar
        Color andromedaBg2 = new Color(0x0a, 0x0c, 0x12);
        Color andromedaBg = new Color(0x0c, 0x0e, 0x14);
        Color yellowTitle = new Color(0xff, 0xe6, 0x6d);

        getRootPane().putClientProperty("JRootPane.titleBarBackground", andromedaBg2);
        getRootPane().putClientProperty("JRootPane.titleBarForeground", yellowTitle);
        getRootPane().putClientProperty("JRootPane.titleBarInactiveBackground", andromedaBg);

        // Set background color for the content pane
        getContentPane().setBackground(andromedaBg);
    }
}
