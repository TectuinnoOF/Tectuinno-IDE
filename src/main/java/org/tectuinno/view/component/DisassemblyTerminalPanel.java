package org.tectuinno.view.component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.util.Locale;

public class DisassemblyTerminalPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextArea txaDisassemblyResult = new JTextArea();

	/**
	 * Create the panel.
	 */
	public DisassemblyTerminalPanel() {
		
		setLayout(new BorderLayout(0, 0));
		{
			add(scrollPane);
		}
		
		{/*Configuring the disassembly text terminal*/
			
			String initialText = this.setInitialText();
			txaDisassemblyResult.setText(initialText);
			txaDisassemblyResult.setLocale(new Locale("es", "MX"));
			txaDisassemblyResult.setForeground(new Color(0, 204, 0));
			txaDisassemblyResult.setFont(new Font("Lucida Fax", Font.PLAIN, 12));
			txaDisassemblyResult.setEditable(false);
			txaDisassemblyResult.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			txaDisassemblyResult.setCaretColor(Color.WHITE);
			txaDisassemblyResult.setBackground(new Color(51, 51, 51));
			scrollPane.setViewportView(txaDisassemblyResult);
		}		
		
	}
	
	private final String setInitialText() {
		
		return new StringBuilder()
				.append("\n")
				.append("************************************************************\n\r")
				.append("\t Objdumb Code Dissasembly \n")
				.append("************************************************************\n\r")
				.toString();
		
	}

}
