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

/**
 * A panel component that hosts the result/output consoles within the Tectuinno
 * IDE.
 * 
 * This class provides a tabbed interface for managing multiple terminal
 * instances,
 * allowing developers to visualize assembler outputs, system messages, and UART
 * communication logs.
 * It includes a button to open new terminal tabs dynamically and initializes
 * with a default terminal.
 *
 * Internally, it uses a {@code JTabbedPane} to separate outputs by session or
 * task,
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
	private JButton btnClearTerminals;
	private JTabbedPane tabbedTerminalPanel;
	private JPanel optionsPanel;
	private FlowLayout fl_optionsPanel;
	private TerminalPanel terminalPanel;
	private DisassemblyTerminalPanel disassemblyTerminalPanel;
	private TokenTerminalPanel tokenTerminalPanel;
	private OrderedHexResultTerminalPanel orderedHexResultTerminalPanel;

	/**
	 * Create the panel.
	 */
	public ResultConsolePanel() {
		setLayout(new BorderLayout(0, 0));

		tabbedTerminalPanel = new JTabbedPane(JTabbedPane.TOP);
		// Forzar subrayado cyan y delgado en pestañas de terminal
		// Usar el mismo tono que el editor para el área de pestañas
		java.awt.Color andromedaPanelBg = new java.awt.Color(0x0a, 0x0c, 0x12);
		java.awt.Color andromedaAccent = new java.awt.Color(0x00, 0xe8, 0xc6);
		tabbedTerminalPanel.putClientProperty("TabbedPane.underlineHeight", 1);
		tabbedTerminalPanel.putClientProperty("TabbedPane.underlineColor", andromedaAccent);
		tabbedTerminalPanel.putClientProperty("TabbedPane.selectedUnderlineColor", andromedaAccent);
		tabbedTerminalPanel.putClientProperty("TabbedPane.showTabSeparators", Boolean.TRUE);
		tabbedTerminalPanel.setBackground(andromedaPanelBg);
		tabbedTerminalPanel.setForeground(andromedaAccent);
		// Mantener las etiquetas de pestañas en cyan aun no seleccionadas
		tabbedTerminalPanel.putClientProperty("TabbedPane.selectedForeground", andromedaAccent);
		tabbedTerminalPanel.putClientProperty("TabbedPane.tabAreaBackground", andromedaPanelBg);
		add(tabbedTerminalPanel, BorderLayout.CENTER);

		optionsPanel = new JPanel();
		fl_optionsPanel = (FlowLayout) optionsPanel.getLayout();
		fl_optionsPanel.setAlignment(FlowLayout.RIGHT);
		add(optionsPanel, BorderLayout.NORTH);

		// Cargar ícono de bote de basura desde assets
		javax.swing.ImageIcon trashIcon = new javax.swing.ImageIcon(
				getClass().getResource("/org/tectuinno/assets/trash.png"));
		btnClearTerminals = new JButton(trashIcon);
		btnClearTerminals.setToolTipText("Limpiar todas las terminales");
		btnClearTerminals.setFocusable(false);
		btnClearTerminals.setBorderPainted(false);
		btnClearTerminals.setContentAreaFilled(false);
		btnClearTerminals.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
		// Efecto hover: cambiar opacidad o borde
		btnClearTerminals.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				btnClearTerminals.setBorderPainted(true);
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				btnClearTerminals.setBorderPainted(false);
			}
		});
		btnClearTerminals.addActionListener(e -> clearAllTerminals());
		optionsPanel.add(btnClearTerminals);

		this.openTokenTerminal();
		this.openFirstTerminal();
		this.openDissasemblyTerminal();
		this.openOrderedHexResultTerminalPanel();

	}

	/**
	 * open the first terminal when the component is called
	 */
	private void openFirstTerminal() {

		opennedTerminals++;
		this.terminalPanel = new TerminalPanel();
		this.tabbedTerminalPanel.addTab(opennedTerminals + ".- Resultado", terminalPanel);

	}

	public void openNewTerminal(String name) {

	}

	public void openTokenTerminal() {
		opennedTerminals++;
		this.tokenTerminalPanel = new TokenTerminalPanel();
		this.tabbedTerminalPanel.addTab(opennedTerminals + ".- Tokens", this.tokenTerminalPanel);
	}

	public void openDissasemblyTerminal() {
		opennedTerminals++;
		this.disassemblyTerminalPanel = new DisassemblyTerminalPanel();
		this.tabbedTerminalPanel.addTab(opennedTerminals + ".- Disassembly", disassemblyTerminalPanel);
	}

	public void openOrderedHexResultTerminalPanel() {
		opennedTerminals++;
		this.orderedHexResultTerminalPanel = new OrderedHexResultTerminalPanel();
		this.tabbedTerminalPanel.addTab(opennedTerminals + ".- Trama", orderedHexResultTerminalPanel);
	}

	public TerminalPanel getTerminalPanel() {
		return this.terminalPanel;
	}

	public DisassemblyTerminalPanel getDisassemblyTerminalPanel() {
		return this.disassemblyTerminalPanel;
	}

	public TokenTerminalPanel getTokenTerminalPanel() {
		return this.tokenTerminalPanel;
	}

	public OrderedHexResultTerminalPanel getOrderedHexResultTerminalPanel() {
		return this.orderedHexResultTerminalPanel;
	}

	// --- Limpieza de terminales para nueva sesión ---
	public void clearTerminal() {
		if (terminalPanel != null) {
			terminalPanel.reset();
		}
	}

	public void clearTokenTerminal() {
		if (tokenTerminalPanel != null) {
			tokenTerminalPanel.reset();
		}
	}

	public void clearDisassemblyTerminal() {
		if (disassemblyTerminalPanel != null) {
			disassemblyTerminalPanel.reset();
		}
	}

	public void clearOrderedHexTerminal() {
		if (orderedHexResultTerminalPanel != null) {
			orderedHexResultTerminalPanel.reset();
		}
	}

	public void clearAllTerminals() {
		clearTerminal();
		clearTokenTerminal();
		clearDisassemblyTerminal();
		clearOrderedHexTerminal();
	}

}
