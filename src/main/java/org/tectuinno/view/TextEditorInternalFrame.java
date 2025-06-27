package org.tectuinno.view;

import java.awt.EventQueue;

import javax.swing.JInternalFrame;

public class TextEditorInternalFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 *
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TextEditorInternalFrame frame = new TextEditorInternalFrame();
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
	public TextEditorInternalFrame() {
		setTitle("Editor Risc-V Assembler");
		setClosable(true);
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setBounds(100, 100, 450, 300);

	}

}
