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

import org.tectuinno.compiler.assembler.AsmLexer;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

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
	public AsmEditorInternalFrame() throws Exception{
		
		setTitle("RISC-V Assembler Editor");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setBounds(100, 100, 450, 300);
		
		panelPrincipalContainer = new JPanel();
		getContentPane().add(panelPrincipalContainer, BorderLayout.CENTER);
		panelPrincipalContainer.setLayout(new BorderLayout(0, 0));
		
		scrollPaneAsmEditor = new JScrollPane();
		panelPrincipalContainer.add(scrollPaneAsmEditor, BorderLayout.CENTER);
		
		asmEditorPane = new AsmEditorPane();
		asmEditorPane.setContentType("asm/assembler");
		asmEditorPane.setCaretColor(new Color(255, 255, 255));
		asmEditorPane.setForeground(new Color(0, 153, 0));
		asmEditorPane.setBackground(new Color(51, 51, 51));
		scrollPaneAsmEditor.setViewportView(this.asmEditorPane);
		
		this.undoManager = new UndoManager();	
		
		this.asmEditorPane.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {				
				asmEditorPane.highLight();				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				asmEditorPane.highLight();
			}
			
			/*
			 * Cuando usaba este método para este evento el rograma se ponía bien pinche 
			 * lentísimo hasta su puta madre
			 * 
			 * tuve que reiniciar como 9 veces mi pc XD
			 * */
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//asmEditorPane.highLight();
			}
		});
		
		this.undoManager.setLimit(1000);
		
		this.asmEditorPane.getDocument().addUndoableEditListener(new UndoableEditListener() {
			
			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				
				var edit = e.getEdit();
				
				if(edit instanceof AbstractDocument.DefaultDocumentEvent dd) {
					
					DocumentEvent.EventType t = dd.getType();
					if (t == DocumentEvent.EventType.INSERT || t == DocumentEvent.EventType.REMOVE) {
						
		                undoManager.addEdit(edit);
		                
		            }
					
				}else {
					undoManager.addEdit(edit);
				}
				
			}			
			
		});
		
		int menuMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

	    InputMap im = this.asmEditorPane.getInputMap(JComponent.WHEN_FOCUSED);
	    ActionMap am = this.asmEditorPane.getActionMap();

	    // Undo: Ctrl/Cmd + Z
	    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,menuMask), "undo-action");
	    am.put("undo-action", new javax.swing.AbstractAction() {
	        @Override public void actionPerformed(java.awt.event.ActionEvent e) { undo(); }
	    });

	    // Redo: Ctrl/Cmd + Y
	    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, menuMask), "redo-action");
	    // Alternative redo: Ctrl/Cmd + Shift + Z (standar in macOS and another editors)
	    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, menuMask | InputEvent.SHIFT_DOWN_MASK), "redo-action");

	    am.put("redo-action", new javax.swing.AbstractAction() {
	        @Override public void actionPerformed(java.awt.event.ActionEvent e) { redo(); }
	    });
		
	}
	
	public void setAsmLexer() {
		this.asmLexer = new AsmLexer(this.asmEditorPane.getText());
	}
	
	public AsmLexer getLexer() {
		return this.asmLexer;
	}

    public String asmGetEditorText(){
        return asmEditorPane.getText();
    }
    public void asmSetEditorText(String text){
        asmEditorPane.setText(text);
    }
    public File getArchivoActual(){
        return archivoActual;
    }
    public void setArchivoActual(File archivoActual){
        this.archivoActual = archivoActual;
    }
    
    public AsmEditorPane getAsmEditorPane() {
    	return this.asmEditorPane;
    }
    
    public void undo() {
	    if (undoManager.canUndo()) undoManager.undo();
	}

	public void redo() {
	    if (undoManager.canRedo()) undoManager.redo();
	}

	public boolean canUndo() { return undoManager.canUndo(); }
	public boolean canRedo() { return undoManager.canRedo(); }

}
