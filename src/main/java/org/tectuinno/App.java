package org.tectuinno;

import java.awt.EventQueue;

import javax.swing.JFrame;

import org.tectuinno.view.MainWindow;


/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        
    	EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    	
    }
}
