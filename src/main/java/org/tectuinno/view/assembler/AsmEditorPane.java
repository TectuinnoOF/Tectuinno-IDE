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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.JTextComponent;

import org.tectuinno.compiler.assembler.utils.AsmEditorStyleName;
import org.tectuinno.compiler.assembler.utils.AsmSyntaxDictionary;
import org.tectuinno.compiler.assembler.AnalysisError;

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
	private final Style immediateStyle;
	private final Style defaultStyle;
	private final Style tagStyle;
	private final Style commentStyle;
	private List<AnalysisError> inlineErrors = List.of();
	private final List<Object> inlineErrorHighlights = new ArrayList<>();
	private static final Color ERROR_LINE_FILL = new Color(255, 105, 97, 35);
	private static final Color ERROR_LINE_BORDER = new Color(255, 105, 97, 90);
	private static final Color WARNING_LINE_FILL = new Color(255, 184, 108, 28);
	private static final Color WARNING_LINE_BORDER = new Color(255, 184, 108, 90);

	// private StyledDocument styledDocument;

	public AsmEditorPane() throws Exception {

		// this.styledDocument = this.getStyledDocument();

		this.keyWordStyle = this.addStyle(AsmEditorStyleName.KEYWORD, null);
		this.registerStyle = this.addStyle(AsmEditorStyleName.REGISTER, null);
		this.immediateStyle = this.addStyle(AsmEditorStyleName.IMMEDIATE, null);
		this.defaultStyle = this.addStyle(AsmEditorStyleName.DEFAULT, null);
		this.tagStyle = this.addStyle(AsmEditorStyleName.TAG, null);
		this.commentStyle = this.addStyle(AsmEditorStyleName.COMMENT, null);

		this.setStyleConstantsInEditor();
		this.installCtrlWheelZoom();
		ToolTipManager.sharedInstance().registerComponent(this);

	}

	private void setStyleConstantsInEditor() {

		// Paleta: instrucciones cyan, registros naranja fuerte, immediatos amarillo,
		// etiquetas/labels rosa más brillante
		StyleConstants.setForeground(keyWordStyle, new Color(0x00, 0xe8, 0xc6)); // Cyan
		StyleConstants.setForeground(this.registerStyle, new Color(0xff, 0x7f, 0x50)); // Naranja más fuerte (coral)
		StyleConstants.setForeground(this.immediateStyle, new Color(0xff, 0xe6, 0x6d)); // Amarillo
		StyleConstants.setForeground(this.defaultStyle, new Color(229, 233, 240)); // Blanco suave
		StyleConstants.setForeground(this.tagStyle, new Color(0xff, 0x8a, 0xd6)); // Rosa más brillante para labels
		StyleConstants.setForeground(this.commentStyle, new Color(127, 132, 150)); // Gris comentarios

		// Colores exactos del tema Andromeda - editor más oscuro
		this.setBackground(new Color(0x0a, 0x0c, 0x12)); // Más oscuro que terminal
		this.setCaretColor(new Color(0xff, 0xff, 0xff)); // editorCursor.foreground #ffffff
		this.setForeground(new Color(0xd5, 0xce, 0xd9)); // editor.foreground #d5ced9
		this.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 13));
		StyleConstants.setItalic(this.commentStyle, true);

	}

	private void updateFontSize(int newSize) {
		int clamped = Math.max(8, Math.min(48, newSize));
		StyleConstants.setFontSize(keyWordStyle, clamped);
		StyleConstants.setFontSize(this.registerStyle, clamped);
		StyleConstants.setFontSize(this.defaultStyle, clamped);
		StyleConstants.setFontSize(this.immediateStyle, clamped);
		StyleConstants.setFontSize(this.tagStyle, clamped);
		StyleConstants.setFontSize(this.commentStyle, clamped);
		this.setFont(this.getFont().deriveFont((float) clamped));
		// Forzar re-render: aplicar defaultStyle a todo el documento y luego resaltado
		StyledDocument doc = this.getStyledDocument();
		doc.setCharacterAttributes(0, doc.getLength(), this.defaultStyle, true);
		this.highLight();
		this.revalidate();
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
	 * Permite zoom con Ctrl + rueda del ratón al estilo VS Code.
	 */
	private void installCtrlWheelZoom() {
		MouseWheelListener zoomListener = e -> {
			if (!e.isControlDown()) {
				return;
			}
			e.consume();
			zoomByWheelRotation(e.getWheelRotation());
		};
		this.addMouseWheelListener(zoomListener);
	}

	public void zoomByWheelRotation(int wheelRotation) {
		int direction = -wheelRotation; // rueda arriba -> incrementa
		int base = this.getFont().getSize();
		int target = base + (direction * 2); // paso de 2px por notch para que se note más
		this.updateFontSize(target);
		this.getParent().revalidate();
	}

	/**
	 * 
	 */
	public void highLight() {

		String text = this.getText().replaceAll("\\n.*\\r|\\r.*\\n|\\s", " ");
		StyledDocument document = this.getStyledDocument();

		SwingUtilities.invokeLater(() -> document.setCharacterAttributes(0, text.length(), this.defaultStyle, true));

		/*
		 * new Thread() { public void run() { //document.setCharacterAttributes(0, text.length(), defaultStyle, true); highlightPattern(AsmSyntaxDictionary.REGISTER_PATTERN, registerStyle); highlightPattern(AsmSyntaxDictionary.INSTRUCTION_PATTERN,
		 * keyWordStyle); highlightPattern(AsmSyntaxDictionary.TAGS_PATTERN, tagStyle); highlightPattern(AsmSyntaxDictionary.COMMENTARY_PATTERN, commentStyle); }; }.start();
		 */

		// Aplicar patrones en orden: primero genéricos (labels), luego específicos que
		// sobrescriben
		this.highlightPatternReplace(AsmSyntaxDictionary.IDENTIFIER_PATTERN, this.tagStyle); // Labels genéricos:
																								// "inicio"
		this.highlightPattern(AsmSyntaxDictionary.TAGS_PATTERN, this.tagStyle); // Declaraciones: "inicio:"
		this.highlightPattern(AsmSyntaxDictionary.INSTRUCTION_PATTERN, this.keyWordStyle); // Instrucciones
		this.highlightPattern(AsmSyntaxDictionary.REGISTER_PATTERN, this.registerStyle); // Registros
		this.highlightPattern(AsmSyntaxDictionary.IMMEDIATE_PATTERN, this.immediateStyle); // Immediatos
		this.highlightPattern(AsmSyntaxDictionary.COMMENTARY_PATTERN, this.commentStyle); // Comentarios

	}

	public void clearInlineErrors() {
		SwingUtilities.invokeLater(() -> {
			Highlighter hl = getHighlighter();
			for (Object tag : inlineErrorHighlights) {
				hl.removeHighlight(tag);
			}
			inlineErrorHighlights.clear();
			inlineErrors = List.of();
			setToolTipText(null);
			repaint();
		});
	}

	public void setInlineErrors(List<AnalysisError> errors) {
		List<AnalysisError> safe = errors == null ? List.of() : List.copyOf(errors);
		if (safe.isEmpty()) {
			clearInlineErrors();
			return;
		}
		SwingUtilities.invokeLater(() -> applyInlineErrors(safe));
	}

	private void applyInlineErrors(List<AnalysisError> errors) {
		Highlighter hl = getHighlighter();
		for (Object tag : inlineErrorHighlights) {
			hl.removeHighlight(tag);
		}
		inlineErrorHighlights.clear();
		this.inlineErrors = errors;

		StyledDocument doc = getStyledDocument();
		Element root = doc.getDefaultRootElement();
		for (AnalysisError error : errors) {
			int lineIndex = Math.max(0, Math.min(error.line() - 1, root.getElementCount() - 1));
			Element lineElem = root.getElement(lineIndex);
			if (lineElem == null) {
				continue;
			}
			int start = lineElem.getStartOffset();
			int end = Math.min(lineElem.getEndOffset(), doc.getLength());
			try {
				Object tag = hl.addHighlight(start, end, new InlineErrorPainter(error));
				inlineErrorHighlights.add(tag);
			} catch (BadLocationException ignored) {
				// No inline highlight if offsets are invalid
			}
		}

		setToolTipText(" ");
		repaint();
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		if (inlineErrors == null || inlineErrors.isEmpty()) {
			return null;
		}
		try {
			int pos = viewToModel2D(event.getPoint());
			if (pos < 0) {
				return null;
			}
			Element root = getDocument().getDefaultRootElement();
			int line = root.getElementIndex(pos) + 1;
			List<String> messages = inlineErrors.stream()
					.filter(err -> err.line() == line)
					.map(err -> {
						String prefix = err.severity() == AnalysisError.Severity.WARNING ? "⚠ " : "✖ ";
						return prefix + err.message();
					})
					.toList();
			if (messages.isEmpty()) {
				return null;
			}
			if (messages.size() == 1) {
				return "<html><body style='padding:4px;'>" + escapeHtml(messages.get(0)) + "</body></html>";
			}
			StringBuilder sb = new StringBuilder("<html><body style='padding:4px;'>");
			sb.append("<b>").append(messages.size()).append(" problemas en esta línea:</b><br>");
			for (String msg : messages) {
				sb.append("• ").append(escapeHtml(msg)).append("<br>");
			}
			sb.append("</body></html>");
			return sb.toString();
		} catch (Exception ignored) {
			return null;
		}
	}

	private static String escapeHtml(String s) {
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	private static final class InlineErrorPainter implements Highlighter.HighlightPainter {

		private final AnalysisError error;

		InlineErrorPainter(AnalysisError error) {
			this.error = error;
		}

		@Override
		public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
			if (error == null) {
				return;
			}
			try {
				Rectangle2D start = c.modelToView2D(p0);
				Rectangle2D end = c.modelToView2D(p1);
				if (start == null || end == null) {
					return;
				}

				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
						java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

				boolean isWarning = error.severity() == AnalysisError.Severity.WARNING;
				Color lineFill = isWarning ? AsmEditorPane.WARNING_LINE_FILL : AsmEditorPane.ERROR_LINE_FILL;
				Color lineBorder = isWarning ? AsmEditorPane.WARNING_LINE_BORDER : AsmEditorPane.ERROR_LINE_BORDER;
				Color waveColor = isWarning ? new Color(255, 184, 108) : new Color(255, 107, 129);

				int y = (int) start.getY();
				int h = (int) start.getHeight();

				// Resaltar toda la línea con color de fondo semi-transparente
				g2.setColor(lineFill);
				g2.fillRect(0, y, c.getWidth(), h);

				// Línea de borde inferior
				g2.setColor(lineBorder);
				g2.drawLine(0, y + h - 1, c.getWidth(), y + h - 1);

				// Barra lateral izquierda (indicador visual como en VS Code)
				g2.setColor(waveColor);
				g2.fillRect(0, y, 3, h);

				// Subrayado ondulado debajo del texto de la línea
				int underlineY = y + h - 3;
				int waveStartX = (int) start.getX();
				int waveEndX = (int) end.getMaxX();
				for (int x = waveStartX; x < waveEndX; x += 4) {
					int offset = ((x - waveStartX) / 4) % 2 == 0 ? 0 : 2;
					g2.drawLine(x, underlineY + offset, x + 2, underlineY + (offset == 0 ? 2 : 0));
				}

				g2.dispose();
			} catch (BadLocationException ignored) {
				// ignore painter errors
			}
		}
	}

	/**
	 * 
	 * @param pattern
	 * @param style
	 */
	private void highlightPattern(String pattern, Style style) {

		String text = this.getText().replaceAll("\\n.*\\r|\\r.*\\n|\\s", " ");

		Matcher matcher = Pattern.compile(pattern, Pattern.MULTILINE).matcher(text);

		// System.out.println(text);

		StyledDocument doc = getStyledDocument();

		SwingUtilities.invokeLater(() -> {
			// doc.setCharacterAttributes(0,getText().length(), this.defaultStyle, true);

			while (matcher.find()) {
				doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), style, false);
			}
		});
	}

	/**
	 * Aplica un patrón con replace=true, sobrescribiendo estilos previos. Se usa para patrones genéricos como identificadores que deben ser sobrescritos por patrones más específicos.
	 * 
	 * @param pattern
	 * @param style
	 */
	private void highlightPatternReplace(String pattern, Style style) {

		String text = this.getText().replaceAll("\\n.*\\r|\\r.*\\n|\\s", " ");

		Matcher matcher = Pattern.compile(pattern, Pattern.MULTILINE).matcher(text);

		StyledDocument doc = getStyledDocument();

		SwingUtilities.invokeLater(() -> {
			while (matcher.find()) {
				doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), style, true);
			}
		});
	}

}