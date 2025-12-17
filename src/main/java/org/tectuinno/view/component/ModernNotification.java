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
 */

package org.tectuinno.view.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Notificación flotante moderna tipo "toast" con esquinas redondeadas, animaciones suaves y cierre automático. Se posiciona en la esquina inferior derecha y desaparece después de 4 segundos.
 */
public class ModernNotification extends JWindow {

    private static final long serialVersionUID = 1L;
    private static final int CORNER_RADIUS = 8;
    private static final int PADDING = 12;
    private static final int WIDTH = 320;
    private static final int HEIGHT = 60;
    private static final int DISPLAY_DURATION = 6000; // 6 segundos
    private static final int ANIMATION_INTERVAL = 10; // Intervalo de actualización de animación

    private float alpha = 0f;
    private boolean isClosing = false;
    private Timer animationTimer;

    /**
     * Tipos de notificación con colores y iconos específicos.
     */
    public enum NotificationType {
        SUCCESS(new Color(0x10, 0xb9, 0x81), "[OK]"), // Verde
        ERROR(new Color(0xff, 0x69, 0x61), "[X]"), // Rojo
        WARNING(new Color(0xff, 0xe6, 0x6d), "[!]"), // Amarillo
        INFO(new Color(0x00, 0xe8, 0xc6), "[i]"); // Cyan

        public final Color color;
        public final String icon;

        NotificationType(Color color, String icon) {
            this.color = color;
            this.icon = icon;
        }
    }

    /**
     * Crea una notificación flotante moderna.
     * 
     * @param parent Ventana padre (para posicionamiento relativo)
     * @param message Mensaje a mostrar
     * @param type Tipo de notificación (determina color e ícono)
     */
    public ModernNotification(JFrame parent, String message, NotificationType type) {
        super(parent);

        // Configurar ventana
        setAlwaysOnTop(true);
        setSize(WIDTH, HEIGHT);
        setBackground(new Color(0, 0, 0, 0)); // Transparencia total para el fondo de la ventana

        // Panel principal con contenido
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo redondeado con sombra
                Color bgColor = new Color(0x0a, 0x0c, 0x12); // Andromeda dark
                g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), (int) (220 * alpha)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

                // Borde izquierdo coloreado (3px)
                g2.setColor(new Color(type.color.getRed(), type.color.getGreen(), type.color.getBlue(), (int) (255 * alpha)));
                g2.fillRoundRect(0, 0, 3, getHeight(), CORNER_RADIUS, CORNER_RADIUS);

                // Borde exterior sutil
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(new Color(255, 255, 255, (int) (30 * alpha)));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout(PADDING, 0));
        contentPanel.setBorder(new EmptyBorder(PADDING, PADDING + 8, PADDING, PADDING));

        // Ícono
        JLabel iconLabel = new JLabel(type.icon);
        iconLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        iconLabel.setForeground(type.color);
        contentPanel.add(iconLabel, BorderLayout.WEST);

        // Texto del mensaje (con word wrap)
        JLabel messageLabel = new JLabel("<html>" + escapeHtml(message) + "</html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        messageLabel.setForeground(new Color(0xe8, 0xe8, 0xe8));
        messageLabel.setVerticalAlignment(SwingConstants.CENTER);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        // Botón cerrar
        JButton closeBtn = new JButton("X");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        closeBtn.setForeground(new Color(0x74, 0x6f, 0x77));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setPreferredSize(new Dimension(20, 20));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeBtn.setForeground(new Color(0xff, 0xff, 0xff));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeBtn.setForeground(new Color(0x74, 0x6f, 0x77));
            }
        });
        closeBtn.addActionListener(e -> fadeOut());
        contentPanel.add(closeBtn, BorderLayout.EAST);

        setContentPane(contentPanel);

        // Posicionar en esquina superior derecha del editor
        if (parent != null) {
            int x = parent.getX() + parent.getWidth() - WIDTH - 25;
            int y = parent.getY() + 30;
            setLocation(x, y);
        }

        // Animar entrada
        fadeIn();
    }

    /**
     * Anima la entrada de la notificación (fade in) y programa su cierre automático.
     */
    private void fadeIn() {
        setVisible(true);
        animationTimer = new Timer(ANIMATION_INTERVAL, e -> {
            alpha += 0.05f;
            if (alpha >= 1f) {
                alpha = 1f;
                ((Timer) e.getSource()).stop();
                // Programar cierre automático
                Timer closeTimer = new Timer(DISPLAY_DURATION, evt -> fadeOut());
                closeTimer.setRepeats(false);
                closeTimer.start();
            }
            repaint();
        });
        animationTimer.start();
    }

    /**
     * Anima la salida de la notificación (fade out) y luego cierra la ventana.
     */
    private void fadeOut() {
        if (isClosing)
            return;
        isClosing = true;

        animationTimer = new Timer(ANIMATION_INTERVAL, e -> {
            alpha -= 0.05f;
            if (alpha <= 0f) {
                alpha = 0f;
                ((Timer) e.getSource()).stop();
                dispose();
            }
            repaint();
        });
        animationTimer.start();
    }

    /**
     * Escapa caracteres HTML para evitar inyecciones y malformato.
     */
    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Factory method para crear y mostrar notificación de éxito.
     */
    public static void showSuccess(JFrame parent, String message) {
        new ModernNotification(parent, message, NotificationType.SUCCESS);
    }

    /**
     * Factory method para crear y mostrar notificación de error.
     */
    public static void showError(JFrame parent, String message) {
        new ModernNotification(parent, message, NotificationType.ERROR);
    }

    /**
     * Factory method para crear y mostrar notificación de advertencia.
     */
    public static void showWarning(JFrame parent, String message) {
        new ModernNotification(parent, message, NotificationType.WARNING);
    }

    /**
     * Factory method para crear y mostrar notificación de información.
     */
    public static void showInfo(JFrame parent, String message) {
        new ModernNotification(parent, message, NotificationType.INFO);
    }
}
