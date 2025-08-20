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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.util.Locale;

public class OrderedHexResultTerminalPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextArea txaOrderedHexResult = new JTextArea();

	/**
	 * Create the panel.
	 */
	public OrderedHexResultTerminalPanel() {
		setLayout(new BorderLayout(0, 0));
		{
			add(scrollPane, BorderLayout.CENTER);
		}
		{
			String initialText = this.setInitialText();
			txaOrderedHexResult.setText(initialText);
			txaOrderedHexResult.setLocale(new Locale("es", "MX"));
			txaOrderedHexResult.setForeground(new Color(0, 204, 0));
			txaOrderedHexResult.setFont(new Font("Lucida Fax", Font.PLAIN, 12));
			txaOrderedHexResult.setEditable(false);
			txaOrderedHexResult.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			txaOrderedHexResult.setCaretColor(Color.WHITE);
			txaOrderedHexResult.setBackground(new Color(51, 51, 51));
			scrollPane.setViewportView(txaOrderedHexResult);
		}

	}
	
	public void writteIn(String text) {
		//this.txaTokenListResult.setText("");
		//this.txaTokenListResult.setText(this.setInitialText());
		//this.txaTokenListResult.append("\n");
		this.txaOrderedHexResult.append("\n=================================================================\n");
		this.txaOrderedHexResult.append(text);
	}
	
	private final String setInitialText() {
		
		return new StringBuilder()
				.append("\n")
				.append("**********************************************************************************\n\r")
				.append("\t Trama Ordenada \n")
				.append("**********************************************************************************\n\r")
				.toString();
		
	}

}
