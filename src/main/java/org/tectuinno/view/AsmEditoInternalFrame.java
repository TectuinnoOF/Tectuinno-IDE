package org.tectuinno.view;

import java.awt.EventQueue;

import javax.swing.JInternalFrame;

public class AsmEditoInternalFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 *
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AsmEditoInternalFrame frame = new AsmEditoInternalFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 */
	public AsmEditoInternalFrame() {
		setTitle("Editor de Texto:");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setBounds(100, 100, 450, 300);

	}

}
