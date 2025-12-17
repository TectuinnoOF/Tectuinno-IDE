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

package org.tectuinno.view.assembler;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.undo.UndoManager;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import org.tectuinno.compiler.assembler.AsmLexer;
import org.tectuinno.view.StartingWindow;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelListener;

import java.awt.Color;
import java.io.File;

public class AsmEditorInternalFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JPanel panelPrincipalContainer;
	private JScrollPane scrollPaneAsmEditor;
	private AsmEditorPane asmEditorPane;
	private AsmLexer asmLexer;
	private File archivoActual;
	private final UndoManager undoManager;

	/**
	 * Create the frame.
	 */
	public AsmEditorInternalFrame() throws Exception {

		setTitle("RISC-V Assembler Editor");
		setResizable(true);
		setMaximizable(false);
		setIconifiable(false);
		setClosable(false);
		BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
		ui.setNorthPane(null); // quita barra de título
		setBorder(null); // sin borde para lucir como pestaña pura
		setBounds(100, 100, 450, 300);

		panelPrincipalContainer = new JPanel();
		panelPrincipalContainer.setBackground(new Color(0x0a, 0x0c, 0x12)); // Mismo color oscuro del editor
		getContentPane().add(panelPrincipalContainer, BorderLayout.CENTER);
		panelPrincipalContainer.setLayout(new BorderLayout(0, 0));

		scrollPaneAsmEditor = new JScrollPane();
		scrollPaneAsmEditor.setBackground(new Color(0x0a, 0x0c, 0x12)); // Mismo color oscuro
		scrollPaneAsmEditor.getViewport().setBackground(new Color(0x0a, 0x0c, 0x12)); // Viewport también
		scrollPaneAsmEditor.setWheelScrollingEnabled(true);
		panelPrincipalContainer.add(scrollPaneAsmEditor, BorderLayout.CENTER);

		asmEditorPane = new AsmEditorPane();
		asmEditorPane.setContentType("asm/assembler");
		// Colores ya configurados en AsmEditorPane constructor
		scrollPaneAsmEditor.setViewportView(this.asmEditorPane);
		// Agregar números de línea en el margen izquierdo
		scrollPaneAsmEditor.setRowHeaderView(new org.tectuinno.view.component.LineNumberPanel(this.asmEditorPane));
		MouseWheelListener ctrlZoom = e -> {
			if (!e.isControlDown()) {
				// sin Ctrl: reenviar al scrollpane para asegurar desplazamiento
				scrollPaneAsmEditor.dispatchEvent(e);
				return;
			}
			e.consume();
			asmEditorPane.zoomByWheelRotation(e.getWheelRotation());
			asmEditorPane.revalidate();
			asmEditorPane.repaint();
			scrollPaneAsmEditor.revalidate();
			scrollPaneAsmEditor.repaint();
		};
		asmEditorPane.addMouseWheelListener(ctrlZoom);

		this.undoManager = new UndoManager();

		this.asmEditorPane.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				asmEditorPane.highLight();
				asmEditorPane.clearInlineErrors();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				asmEditorPane.highLight();
				asmEditorPane.clearInlineErrors();
			}

			// Desactivado: highLight() en changedUpdate hacía muy lenta la edición y penalizaba el rendimiento
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				// asmEditorPane.highLight();
			}
		});

		this.undoManager.setLimit(1000);

		this.asmEditorPane.getDocument().addUndoableEditListener(new UndoableEditListener() {

			@Override
			public void undoableEditHappened(UndoableEditEvent e) {

				var edit = e.getEdit();

				if (edit instanceof AbstractDocument.DefaultDocumentEvent dd) {

					DocumentEvent.EventType t = dd.getType();
					if (t == DocumentEvent.EventType.INSERT || t == DocumentEvent.EventType.REMOVE) {

						undoManager.addEdit(edit);

					}

				} else {
					undoManager.addEdit(edit);
				}

			}

		});

		int menuMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

		InputMap im = this.asmEditorPane.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap am = this.asmEditorPane.getActionMap();

		// Undo: Ctrl/Cmd + Z
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, menuMask), "undo-action");
		am.put("undo-action", new javax.swing.AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				undo();
			}
		});

		// Redo: Ctrl/Cmd + Y
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, menuMask), "redo-action");
		// Alternative redo: Ctrl/Cmd + Shift + Z (standar in macOS and another editors)
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, menuMask | InputEvent.SHIFT_DOWN_MASK), "redo-action");

		am.put("redo-action", new javax.swing.AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				redo();
			}
		});

		// Save: Ctrl/Cmd + S
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, menuMask), "save-action");
		am.put("save-action", new javax.swing.AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				fireSaveRequest();
			}
		});

		// Save As: Ctrl/Cmd + Shift + S
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, menuMask | InputEvent.SHIFT_DOWN_MASK), "save-as-action");
		am.put("save-as-action", new javax.swing.AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				fireSaveAsRequest();
			}
		});

	}

	public void setAsmLexer() {
		this.asmLexer = new AsmLexer(this.asmEditorPane.getText());
	}

	public AsmLexer getLexer() {
		return this.asmLexer;
	}

	public String asmGetEditorText() {
		return asmEditorPane.getText();
	}

	public void asmSetEditorText(String text) {
		asmEditorPane.setText(text);
		// Limpiar el historial de deshacer para que el contenido cargado sea el estado inicial
		// Evita que Ctrl+Z sin cambios previos borre el contenido del archivo
		undoManager.discardAllEdits();
	}

	public File getArchivoActual() {
		return archivoActual;
	}

	public void setArchivoActual(File archivoActual) {
		this.archivoActual = archivoActual;
	}

	public AsmEditorPane getAsmEditorPane() {
		return this.asmEditorPane;
	}

	public void undo() {
		if (undoManager.canUndo())
			undoManager.undo();
	}

	public void redo() {
		if (undoManager.canRedo())
			undoManager.redo();
	}

	public boolean canUndo() {
		return undoManager.canUndo();
	}

	public boolean canRedo() {
		return undoManager.canRedo();
	}

	private void fireSaveRequest() {
		// Notify parent StartingWindow to trigger save
		java.awt.Container parent = this.getParent();
		while (parent != null && !(parent instanceof StartingWindow)) {
			parent = parent.getParent();
		}
		if (parent instanceof StartingWindow sw) {
			sw.saveCurrentFile();
		}
	}

	private void fireSaveAsRequest() {
		// Notify parent StartingWindow to trigger save-as
		java.awt.Container parent = this.getParent();
		while (parent != null && !(parent instanceof StartingWindow)) {
			parent = parent.getParent();
		}
		if (parent instanceof StartingWindow sw) {
			sw.saveAsCurrentFile();
		}
	}

}
