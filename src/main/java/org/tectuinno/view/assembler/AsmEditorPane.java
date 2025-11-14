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

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.undo.UndoManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;



import org.tectuinno.compiler.assembler.utils.AsmEditorStyleName;
import org.tectuinno.compiler.assembler.utils.AsmSyntaxDictionary;




public class AsmEditorPane extends JTextPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	private final Style keyWordStyle;
	private final Style registerStyle;
	private final Style defaultStyle;
	private final Style tagStyle;
	private final Style commentStyle;	

	//private StyledDocument styledDocument;
	
	
	public AsmEditorPane() throws Exception{
		
		//this.styledDocument = this.getStyledDocument();			
		
		this.keyWordStyle = this.addStyle(AsmEditorStyleName.KEYWORD, null);
		this.registerStyle = this.addStyle(AsmEditorStyleName.REGISTER, null);
		this.defaultStyle = this.addStyle(AsmEditorStyleName.DEFAULT, null);
		this.tagStyle = this.addStyle(AsmEditorStyleName.TAG, null);
		this.commentStyle = this.addStyle(AsmEditorStyleName.COMMENT, null);
		
		this.setStyleConstantsInEditor();
				
		
	}		
	
	private void setStyleConstantsInEditor() {
		
		StyleConstants.setForeground(keyWordStyle, Color.CYAN);		
		StyleConstants.setForeground(this.registerStyle, Color.ORANGE);
		StyleConstants.setForeground(this.defaultStyle, new Color(63,227,11));
		StyleConstants.setForeground(this.tagStyle, Color.MAGENTA);
		StyleConstants.setForeground(this.commentStyle, new Color(208,244,245));
		StyleConstants.setItalic(this.commentStyle, true);
		
	}
	
	private void updateFontSize(int newSize) {
		
		StyleConstants.setFontSize(keyWordStyle, newSize);
		StyleConstants.setFontSize(this.registerStyle, newSize);
		StyleConstants.setFontSize(this.defaultStyle, newSize);
		StyleConstants.setFontSize(this.tagStyle, newSize);
		StyleConstants.setFontSize(this.commentStyle, newSize);
		
		this.setFont(this.getFont().deriveFont((float) newSize));
		this.repaint();
		
	}
	
	public void increaseFontSize() {
		
		int newSize = this.getFont().getSize() + 2;
		this.updateFontSize(newSize);
		
	}
	
	public void decreaseFontSize() {
		
		int newSize = this.getFont().getSize() - 2;
		this.updateFontSize(newSize);
		
	}

	/**
	 * 
	 */
	public void highLight() {
		
		String text = this.getText().replaceAll("\\n.*\\r|\\r.*\\n|\\s", " ");
		StyledDocument document = this.getStyledDocument();
		
		SwingUtilities.invokeLater(() -> document.setCharacterAttributes(0, text.length(), this.defaultStyle, true));
		
		/*new Thread() {
			public void run() {
				//document.setCharacterAttributes(0, text.length(), defaultStyle, true);
				highlightPattern(AsmSyntaxDictionary.REGISTER_PATTERN, registerStyle);
				highlightPattern(AsmSyntaxDictionary.INSTRUCTION_PATTERN, keyWordStyle);
				highlightPattern(AsmSyntaxDictionary.TAGS_PATTERN, tagStyle);
				highlightPattern(AsmSyntaxDictionary.COMMENTARY_PATTERN, commentStyle);
			};
		}.start();*/
		
		this.highlightPattern(AsmSyntaxDictionary.REGISTER_PATTERN, this.registerStyle);
		this.highlightPattern(AsmSyntaxDictionary.INSTRUCTION_PATTERN, this.keyWordStyle);
		this.highlightPattern(AsmSyntaxDictionary.TAGS_PATTERN, this.tagStyle);
		this.highlightPattern(AsmSyntaxDictionary.COMMENTARY_PATTERN, this.commentStyle);
		
	}
	
	/**
	 * 
	 * @param pattern
	 * @param style
	 */
	private void highlightPattern(String pattern, Style style) {
		
		String text = this.getText().replaceAll("\\n.*\\r|\\r.*\\n|\\s", " ");
		
        Matcher matcher = Pattern.compile(pattern, Pattern.MULTILINE).matcher(text);
        
        //System.out.println(text);
        
        StyledDocument doc = getStyledDocument();
        
        SwingUtilities.invokeLater(() -> {        	
        	//doc.setCharacterAttributes(0,getText().length(), this.defaultStyle, true);
        	
        	while (matcher.find()) {
                doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), style, false);
            }
        });
    }
	
	

}