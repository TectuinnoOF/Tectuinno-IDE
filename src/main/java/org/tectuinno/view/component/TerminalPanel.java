/*
 * This file is part of Tectuinno IDE.
 *
 * Tectuinno IDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * As a special exception, you may use this file as part of a free software
 * library without restriction. Specifically, if other files instantiate
 * templates or use macros or inline functions from this file, or you compile
 * this file and link it with other files to produce an executable, this
 * file does not by itself cause the resulting executable to be covered by
 * the GNU General Public License. This exception does not however
 * invalidate any other reasons why the executable file might be covered by
 * the GNU General Public License.
 *
 * Copyright 2025 Tectuinno Team (https://github.com/tectuinno)
 */

package org.tectuinno.view.component;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Font;
import java.awt.Cursor;
import java.util.Locale;
import javax.swing.JScrollPane;


/**
 * Represents a single terminal panel within the Tectuinno IDE console system.
 * 
 * This component simulates a terminal-like output console where system messages,
 * assembler results, compiler feedback, and UART communication logs with the microcontroller
 * can be displayed in real-time.
 * 
 * Internally, it uses a {@link JTextArea} styled with a dark background, green monospaced font,
 * and embedded within a {@link JScrollPane} to support scrolling for long outputs.
 * 
 * Upon initialization, it displays a welcome banner similar in style to the Visual Studio Developer Command Prompt.
 * 
 * This panel is typically managed by the {@link ResultConsolePanel} and added to a {@code JTabbedPane}
 * to support multiple simultaneous output sessions.
 * 
 * Future improvements may include:
 * - Support for command input
 * - Scroll locking
 * - Redirecting output from {@code System.out} or external processes
 */
public class TerminalPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final String firstTest = "\n" +		    		
		    	    "**********************************************************************\n" +
		    	    "** Tectuinno Developer Console v1.0\n" +
		    	    "** Copyright (c) 2025 Tectuinno Project\n" +
		    	    "** Powered by RISC-V & Java Swing\n" +
		    	    "**********************************************************************\n";


	
	public TerminalPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		JTextArea txaConsoleTextResult = new JTextArea();
		txaConsoleTextResult.setText(firstTest);
		txaConsoleTextResult.setLocale(new Locale("es", "MX"));
		txaConsoleTextResult.setForeground(new Color(0, 204, 0));
		txaConsoleTextResult.setFont(new Font("Lucida Fax", Font.PLAIN, 12));
		txaConsoleTextResult.setEditable(false);
		txaConsoleTextResult.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		txaConsoleTextResult.setCaretColor(Color.WHITE);
		txaConsoleTextResult.setBackground(new Color(51, 51, 51));
		scrollPane.setViewportView(txaConsoleTextResult);
		
		//TODO es posible que se necesite algún método público de escritura cuando se requera editar desde otra clase
	}	

}
