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


package org.tectuinno.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import java.awt.Cursor;
import javax.swing.DebugGraphics;
import java.awt.Color;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JMenuBar menuBar;
	private JMenu JMenuArchivo;
	private JMenu JMenuArchivoNuevo;
	private JMenuItem MenuItemNvoAsm;
	private JMenu JMenuProyecto;
	private JMenuItem MenuItemFicheroTexto;
	private JSplitPane SplitPanePrincipal;
	private JSplitPane splitPaneEditorAndConsole;
	private JPanel panelToolBar;

	/**
	 * Launch the application.
	 *
	public static void main(String[] args) {
		
	}*/

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setTitle("Tectuinno IDE");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 686, 559);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenuArchivo = new JMenu("Archivo");
		menuBar.add(JMenuArchivo);
		
		JMenuArchivoNuevo = new JMenu("Nuevo");
		JMenuArchivo.add(JMenuArchivoNuevo);
		
		MenuItemNvoAsm = new JMenuItem("Fichero ASM Risc-V");
		JMenuArchivoNuevo.add(MenuItemNvoAsm);
		
		MenuItemFicheroTexto = new JMenuItem("Texto");
		JMenuArchivoNuevo.add(MenuItemFicheroTexto);
		
		JMenuProyecto = new JMenu("Proyecto");
		menuBar.add(JMenuProyecto);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		SplitPanePrincipal = new JSplitPane();
		SplitPanePrincipal.setBackground(new Color(105, 105, 105));
		contentPane.add(SplitPanePrincipal, BorderLayout.CENTER);
		
		splitPaneEditorAndConsole = new JSplitPane();
		splitPaneEditorAndConsole.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
		splitPaneEditorAndConsole.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		splitPaneEditorAndConsole.setDividerSize(5);
		splitPaneEditorAndConsole.setContinuousLayout(false);
		splitPaneEditorAndConsole.setOrientation(JSplitPane.VERTICAL_SPLIT);
		SplitPanePrincipal.setRightComponent(splitPaneEditorAndConsole);
		splitPaneEditorAndConsole.setDividerLocation(this.getHeight() - 130);
		
		panelToolBar = new JPanel();
		contentPane.add(panelToolBar, BorderLayout.NORTH);

	}

}
