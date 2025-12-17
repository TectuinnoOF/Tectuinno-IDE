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

import javax.swing.SwingUtilities;
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

	// Campos relevantes
	private String lastFindQuery = null;
	private boolean lastFindMatchCase = true;
	private transient javax.swing.JDialog searchDialog = null;

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

		// Find in editor: Ctrl/Cmd + B (abre un diálogo para buscar sólo en este editor)
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, menuMask), "find-action");
		am.put("find-action", new javax.swing.AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				// Mostrar diálogo de búsqueda completo
				showSearchDialog();
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

	// Realiza la búsqueda dentro del editor con opción de match case.
	private void performFind(String query, boolean forward, boolean matchCase) {
		try {
			String text = asmEditorPane.getText();
			String hay = matchCase ? text : text.toLowerCase();
			String needle = matchCase ? query : query.toLowerCase();
			int startPos = asmEditorPane.getCaretPosition();
			int idx = -1;
			if (forward) {
				idx = hay.indexOf(needle, Math.max(0, startPos));
				if (idx < 0) {
					// buscar desde inicio
					idx = hay.indexOf(needle);
				}
			} else {
				// para búsqueda hacia atrás, comenzar desde la posición anterior al caret
				int from = Math.max(0, startPos - 1);
				if (from > hay.length())
					from = hay.length();
				idx = hay.lastIndexOf(needle, from);
				if (idx < 0) {
					// buscar desde el final
					idx = hay.lastIndexOf(needle);
				}
			}
			if (idx >= 0) {
				asmEditorPane.select(idx, idx + query.length());
				asmEditorPane.requestFocusInWindow();
				try {
					java.awt.geom.Rectangle2D r = asmEditorPane.modelToView2D(idx);
					if (r != null)
						asmEditorPane.scrollRectToVisible(r.getBounds());
				} catch (javax.swing.text.BadLocationException ex) {
					// ignore
				}
			} else {
				java.awt.Window w = SwingUtilities.getWindowAncestor(AsmEditorInternalFrame.this);
				if (w instanceof javax.swing.JFrame parentFrame) {
					org.tectuinno.view.component.ModernNotification.showInfo(parentFrame,
							"No se encontró '" + query + "' en este editor.");
				} else {
					javax.swing.JOptionPane.showMessageDialog(AsmEditorInternalFrame.this,
							"No se encontró '" + query + "' en este editor.", "Buscar",
							javax.swing.JOptionPane.INFORMATION_MESSAGE);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/** Buscar siguiente usando la última consulta y la opción match-case guardada. */
	public void findNext() {
		if (lastFindQuery == null || lastFindQuery.isEmpty()) {
			showSearchDialog();
			return;
		}
		performFind(lastFindQuery, true, lastFindMatchCase);
	}

	/** Buscar anterior usando la última consulta y la opción match-case guardada. */
	public void findPrevious() {
		if (lastFindQuery == null || lastFindQuery.isEmpty()) {
			showSearchDialog();
			return;
		}
		performFind(lastFindQuery, false, lastFindMatchCase);
	}

	/** Muestra el diálogo de búsqueda (se reutiliza si ya existe). */
	public void showSearchDialog() {
		if (searchDialog != null && searchDialog.isShowing()) {
			searchDialog.toFront();
			return;
		}
		java.awt.Window owner = SwingUtilities.getWindowAncestor(this);
		searchDialog = new org.tectuinno.view.component.StyledDialog(owner, "Buscar en editor",
				java.awt.Dialog.ModalityType.MODELESS);
		searchDialog.setUndecorated(true);

		// Limpiar resaltado al cerrar
		searchDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				asmEditorPane.clearSearchHighlights();
			}
		});

		// Cerrar con ESC
		javax.swing.JRootPane rootPane = searchDialog.getRootPane();
		javax.swing.InputMap im = rootPane.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
		javax.swing.ActionMap am = rootPane.getActionMap();
		im.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "closeDialog");
		am.put("closeDialog", new javax.swing.AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				searchDialog.dispose();
			}
		});

		// Header personalizado con título y botón cerrar
		javax.swing.JPanel header = new javax.swing.JPanel(new java.awt.BorderLayout());
		header.setOpaque(false);
		javax.swing.JLabel lblTitle = new javax.swing.JLabel("Buscar en editor");
		lblTitle.setForeground(new java.awt.Color(0x00, 0xe8, 0xc6));
		lblTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 4, 2, 4));
		javax.swing.JButton btnX = new javax.swing.JButton("✕");
		btnX.setBorderPainted(false);
		btnX.setContentAreaFilled(false);
		btnX.setFocusPainted(false);
		btnX.setForeground(new java.awt.Color(0xff, 0xe6, 0x6d));
		btnX.addActionListener(a -> searchDialog.dispose());
		header.add(lblTitle, java.awt.BorderLayout.WEST);
		header.add(btnX, java.awt.BorderLayout.EAST);

		final java.awt.Point dragStart = new java.awt.Point();
		header.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				dragStart.setLocation(e.getPoint());
			}
		});
		header.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseDragged(java.awt.event.MouseEvent e) {
				java.awt.Point loc = searchDialog.getLocation();
				searchDialog.setLocation(loc.x + e.getX() - dragStart.x, loc.y + e.getY() - dragStart.y);
			}
		});

		javax.swing.JPanel p = new javax.swing.JPanel(new java.awt.GridBagLayout());
		p.setOpaque(false); // To let StyledDialog background show
		java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
		c.insets = new java.awt.Insets(4, 4, 4, 4);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = java.awt.GridBagConstraints.WEST;
		javax.swing.JLabel lblSearch = new javax.swing.JLabel("Buscar:");
		lblSearch.setForeground(new java.awt.Color(0xd5, 0xce, 0xd9));
		p.add(lblSearch, c);

		c.gridx = 1;
		c.fill = java.awt.GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		javax.swing.JTextField txtQuery = new javax.swing.JTextField(lastFindQuery == null ? "" : lastFindQuery, 30);
		p.add(txtQuery, c);

		c.gridx = 0;
		c.gridy = 1;
		c.fill = java.awt.GridBagConstraints.NONE;
		c.weightx = 0;
		javax.swing.JCheckBox chkMatch = new javax.swing.JCheckBox("Coincidir mayúsculas", lastFindMatchCase);
		java.awt.Color yellowTitle = new java.awt.Color(0xff, 0xe6, 0x6d);
		chkMatch.setForeground(yellowTitle);
		chkMatch.setOpaque(false);
		p.add(chkMatch, c);

		c.gridx = 1;
		javax.swing.JPanel btns = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
		javax.swing.JButton btnPrev = new javax.swing.JButton("Anterior");
		javax.swing.JButton btnNext = new javax.swing.JButton("Siguiente");
		javax.swing.JButton btnHighlight = new javax.swing.JButton("Resaltar todo");
		javax.swing.JButton btnClear = new javax.swing.JButton("Limpiar resaltado");
		javax.swing.JButton btnClose = new javax.swing.JButton("Cerrar");

		btns.add(btnHighlight);
		btns.add(btnClear);
		btns.add(btnPrev);
		btns.add(btnNext);
		btns.add(btnClose);

		p.add(btns, c);

		btnNext.addActionListener(a -> {
			String q = txtQuery.getText();
			if (q == null || q.isEmpty())
				return;
			lastFindQuery = q;
			lastFindMatchCase = chkMatch.isSelected();
			performFind(q, true, lastFindMatchCase);
		});
		btnPrev.addActionListener(a -> {
			String q = txtQuery.getText();
			if (q == null || q.isEmpty())
				return;
			lastFindQuery = q;
			lastFindMatchCase = chkMatch.isSelected();
			performFind(q, false, lastFindMatchCase);
		});
		btnHighlight.addActionListener(a -> {
			String q = txtQuery.getText();
			if (q == null || q.isEmpty())
				return;
			lastFindQuery = q;
			lastFindMatchCase = chkMatch.isSelected();
			asmEditorPane.highlightAllOccurrences(q, lastFindMatchCase);
		});
		btnClear.addActionListener(a -> asmEditorPane.clearSearchHighlights());
		btnClose.addActionListener(a -> searchDialog.dispose());

		javax.swing.JPanel wrapper = new javax.swing.JPanel(new java.awt.BorderLayout());
		wrapper.setOpaque(false);
		wrapper.add(header, java.awt.BorderLayout.NORTH);
		wrapper.add(p, java.awt.BorderLayout.CENTER);
		searchDialog.getContentPane().add(wrapper);

		searchDialog.pack();
		searchDialog.setLocationRelativeTo(this);
		javax.swing.SwingUtilities.invokeLater(() -> {
			for (java.awt.Component comp : p.getComponents()) {
				if (comp instanceof javax.swing.JTextField tf) {
					tf.requestFocusInWindow();
					break;
				}
			}
		});
		searchDialog.setVisible(true);
	}

}
