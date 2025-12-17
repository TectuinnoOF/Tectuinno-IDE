package org.tectuinno.view.component;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * Simple line number panel for any JTextComponent. Attach via
 * scrollPane.setRowHeaderView(new LineNumberPanel(textComponent)).
 */
public class LineNumberPanel extends JComponent implements DocumentListener, CaretListener {

    private static final int PADDING_LEFT = 6;
    private static final int PADDING_RIGHT = 6;
    private final JTextComponent text;
    private final Color bg = new Color(0x0a, 0x0c, 0x12); // Mismo tono oscuro del editor
    private final Color fg = new Color(0x74, 0x6f, 0x77); // Gris números de línea

    public LineNumberPanel(JTextComponent text) {
        this.text = text;
        setFont(text.getFont());
        setForeground(fg);
        setBackground(bg);

        text.getDocument().addDocumentListener(this);
        text.addCaretListener(this);
        // Mantener el tamaño de fuente de los números de línea sincronizado con el editor
        text.addPropertyChangeListener("font", evt -> {
            setFont(text.getFont());
            revalidate();
            repaint();
        });
        text.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) { repaint(); }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        int lineCount = getLineCount();
        FontMetrics fm = getFontMetrics(getFont());
        int digits = Math.max(2, String.valueOf(lineCount).length());
        int width = PADDING_LEFT + digits * fm.charWidth('0') + PADDING_RIGHT;
        int height = text.getHeight();
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(getForeground());
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int ascent = fm.getAscent();

            // Determinar líneas visibles
            Rectangle vr = getViewportViewRect();
            int startOffset = text.viewToModel2D(new Point(vr.x, vr.y));
            int endOffset = text.viewToModel2D(new Point(vr.x, vr.y + vr.height));

            Element root = text.getDocument().getDefaultRootElement();
            int startLine = root.getElementIndex(startOffset);
            int endLine = Math.max(startLine, root.getElementIndex(endOffset));

            for (int line = startLine; line <= endLine; line++) {
                int lineStart = root.getElement(line).getStartOffset();
                Rectangle r = text.modelToView2D(lineStart).getBounds();
                if (r == null) continue;
                String num = String.valueOf(line + 1);
                int x = getWidth() - PADDING_RIGHT - fm.stringWidth(num);
                int y = r.y + ascent;
                g2.drawString(num, x, y);
            }
        } catch (BadLocationException ignored) {
        } finally {
            g2.dispose();
        }
    }

    private Rectangle getViewportViewRect() {
        Container p = text.getParent();
        if (p instanceof JViewport) {
            return ((JViewport) p).getViewRect();
        }
        return new Rectangle(0, 0, text.getWidth(), text.getHeight());
    }

    private int getLineCount() {
        return text.getDocument().getDefaultRootElement().getElementCount();
    }

    // DocumentListener
    @Override public void insertUpdate(DocumentEvent e) { updateAndRepaint(); }
    @Override public void removeUpdate(DocumentEvent e) { updateAndRepaint(); }
    @Override public void changedUpdate(DocumentEvent e) { updateAndRepaint(); }

    // CaretListener
    @Override public void caretUpdate(CaretEvent e) { repaint(); }

    private void updateAndRepaint() {
        revalidate();
        repaint();
    }
}
