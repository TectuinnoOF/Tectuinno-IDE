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
import javax.swing.JTabbedPane;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Color;

/**
 * A panel component that hosts the result/output consoles within the Tectuinno IDE.
 * 
 * This class provides a tabbed interface for managing multiple terminal instances,
 * allowing developers to visualize assembler outputs, system messages, and UART communication logs.
 * It includes a button to open new terminal tabs dynamically and initializes with a default terminal.
 *
 * Internally, it uses a {@code JTabbedPane} to separate outputs by session or task,
 * and a header panel with control buttons for user interaction.
 *
 * Typical usage:
 * - Display assembler or compiler results.
 * - Show communication logs with the Tectuinno microcontroller.
 * - Serve as an integrated developer terminal.
 */
public class ResultConsolePanel extends JPanel {
	
	private static int opennedTerminals = 0;
	private static final long serialVersionUID = 1L;
	private JButton btnNewTerminal;
	private JTabbedPane tabbedTerminalPanel;
	private JPanel optionsPanel;
	private FlowLayout fl_optionsPanel;
	private TerminalPanel terminalPanel;
	private DisassemblyTerminalPanel disassemblyTerminalPanel;

	/**
	 * Create the panel.
	 */
	public ResultConsolePanel() {
		setLayout(new BorderLayout(0, 0));
		
		tabbedTerminalPanel = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedTerminalPanel, BorderLayout.CENTER);
		
		optionsPanel = new JPanel();
		fl_optionsPanel = (FlowLayout) optionsPanel.getLayout();
		fl_optionsPanel.setAlignment(FlowLayout.RIGHT);
		add(optionsPanel, BorderLayout.NORTH);
		
		btnNewTerminal = new JButton("");
		btnNewTerminal.setBackground(Color.WHITE);
		btnNewTerminal.setToolTipText("Abrir Terminal");
		btnNewTerminal.setIcon(new ImageIcon(ResultConsolePanel.class.getResource("/org/tectuinno/assets/terminal_ico.png")));
		optionsPanel.add(btnNewTerminal);
		
		this.openFirstTerminal();
		this.openDissasemblyTerminal();
	}
	
	/**
	 * open the first terminal when the component is called
	 */
	private void openFirstTerminal() {
		
		opennedTerminals ++;
		this.terminalPanel = new TerminalPanel();		
		this.tabbedTerminalPanel.addTab( opennedTerminals + ".- Resultado", terminalPanel);
				
	}
	
	public void openNewTerminal(String name) {				
		
	}
	
	public void openDissasemblyTerminal() {
		opennedTerminals++;
		this.disassemblyTerminalPanel = new DisassemblyTerminalPanel();
		this.tabbedTerminalPanel.addTab(opennedTerminals + ".- Disassembly", disassemblyTerminalPanel);
	}		
	
	public TerminalPanel getTerminalPanel() {
		return this.terminalPanel;
	}
	
	public DisassemblyTerminalPanel getDisassemblyTerminalPanel() {
		return this.disassemblyTerminalPanel;
	}

}
