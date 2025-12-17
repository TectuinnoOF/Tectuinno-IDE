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

package org.tectuinno;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import org.tectuinno.view.StartingWindow;

public class App {

	public static void main(String[] args) {

		try {
			// Forzar renderizado por software (evita uso de GPU/overheating en algunos equipos)
			System.setProperty("sun.java2d.d3d", "false");
			System.setProperty("sun.java2d.opengl", "false");

			// Configurar tema FlatLaf Dark con colores estilo Andromeda
			System.setProperty("flatlaf.useWindowDecorations", "true");
			FlatDarkLaf.setup();

			// Redondeo y estilo visual similar a VS Code
			UIManager.put("Component.arc", 12);
			UIManager.put("Button.arc", 12);
			UIManager.put("TextComponent.arc", 8);
			UIManager.put("ScrollBar.thumbArc", 999);
			UIManager.put("ScrollBar.trackInsets", 2);

			// Paleta Andromeda exacta del tema VS Code
			Color andromedaBg = new Color(0x0c, 0x0e, 0x14); // panel.background #0c0e14
			Color andromedaBg2 = new Color(0x0a, 0x0c, 0x12); // editor.background más oscuro
			Color andromedaHover = new Color(0x37, 0x39, 0x41); // editor.hoverHighlightBackground
			Color andromedaAccent = new Color(0x00, 0xe8, 0xc6); // #00e8c6 cyan
			Color yellowTitle = new Color(0xff, 0xe6, 0x6d); // #ffe66d amarillo para barra de título

			UIManager.put("TitlePane.background", andromedaBg);
			UIManager.put("TitlePane.foreground", yellowTitle);
			UIManager.put("TitlePane.inactiveForeground", yellowTitle);
			UIManager.put("TitlePane.iconColor", yellowTitle);
			UIManager.put("MenuBar.background", andromedaBg);
			UIManager.put("Menu.background", andromedaBg2);
			UIManager.put("MenuItem.background", andromedaBg2);
			UIManager.put("MenuItem.selectionBackground", andromedaHover);
			UIManager.put("PopupMenu.background", andromedaBg2);
			UIManager.put("Button.background", andromedaBg2);
			UIManager.put("Button.hoverBackground", andromedaHover);
			UIManager.put("ToolBar.background", andromedaBg);
			UIManager.put("Panel.background", andromedaBg);
			UIManager.put("TabbedPane.background", andromedaBg);
			UIManager.put("TabbedPane.contentAreaColor", andromedaBg2);
			UIManager.put("TitlePane.unifiedBackground", true);
			UIManager.put("TabbedPane.tabsBackground", andromedaBg); // Cabecera de pestañas
			UIManager.put("ComboBox.background", andromedaBg2);
			UIManager.put("ComboBox.foreground", yellowTitle);
			UIManager.put("ComboBox.selectionBackground", andromedaHover);
			UIManager.put("ComboBox.selectionForeground", yellowTitle);
			UIManager.put("ComboBox.buttonBackground", andromedaBg2);
			UIManager.put("ComboBox.buttonHoverBackground", andromedaHover);
			UIManager.put("ComboBox.buttonArrowColor", andromedaAccent);
			UIManager.put("Spinner.background", andromedaBg2);
			UIManager.put("Spinner.foreground", yellowTitle);
			UIManager.put("Spinner.buttonBackground", andromedaBg2);
			UIManager.put("Spinner.buttonHoverBackground", andromedaHover);
			UIManager.put("Spinner.buttonArrowColor", andromedaAccent);
			// Diálogos de advertencia (JOptionPane)
			UIManager.put("OptionPane.background", andromedaBg2);
			UIManager.put("OptionPane.messageForeground", andromedaAccent);
			UIManager.put("OptionPane.buttonBackground", andromedaBg2);
			UIManager.put("Component.accentColor", andromedaAccent); // Morado Andromeda

			// JFileChooser colores y selección más azul/cyan
			UIManager.put("FileChooser.background", andromedaBg2);
			UIManager.put("FileChooser.foreground", andromedaAccent);
			UIManager.put("FileChooser.listViewBackground", andromedaBg2);
			UIManager.put("FileChooser.listViewForeground", andromedaAccent);
			UIManager.put("FileChooser.listViewSelectionBackground", andromedaBg2);
			UIManager.put("FileChooser.listViewSelectionForeground", andromedaAccent);
			UIManager.put("FileChooser.listViewBorder", new LineBorder(andromedaAccent, 1, true));
			UIManager.put("FileChooser.listBackground", andromedaBg2);
			UIManager.put("FileChooser.listSelectionBackground", andromedaBg2);
			UIManager.put("FileChooser.listSelectionForeground", andromedaAccent);
			UIManager.put("FileChooser.iconViewBackground", andromedaBg2);
			UIManager.put("FileChooser.iconViewSelectionBackground", andromedaBg2);
			UIManager.put("FileChooser.iconViewSelectionForeground", andromedaAccent);
			UIManager.put("FileChooser.iconViewBorder", new LineBorder(andromedaAccent, 1, true));
			UIManager.put("FileChooser.detailViewBackground", andromedaBg2);
			UIManager.put("FileChooser.detailViewSelectionBackground", andromedaBg2);
			UIManager.put("FileChooser.detailViewSelectionForeground", andromedaAccent);
			UIManager.put("FileChooser.detailViewBorder", new LineBorder(andromedaAccent, 1, true));
			UIManager.put("FileChooser.sidebarBackground", andromedaBg);
			UIManager.put("FileChooser.sidebarSelectionBackground", andromedaBg);
			UIManager.put("FileChooser.sidebarSelectionForeground", andromedaAccent);
			UIManager.put("FileChooser.sidebarFocusCellHighlightBorder", new LineBorder(andromedaAccent, 1, true));
			UIManager.put("FileChooser.lookInLabelForeground", yellowTitle);
			UIManager.put("FileChooser.filesOfTypeLabelForeground", yellowTitle);
			UIManager.put("FileChooser.toolbarButtonForeground", andromedaAccent);
			UIManager.put("FileChooser.toolbarButtonHoverBackground", andromedaHover);
			UIManager.put("FileChooser.toolbarButtonPressedBackground", andromedaHover);

			// Botones de toolbar (Lista/Detalles en FileChooser) - sin relleno
			Color transparent = new Color(0, 0, 0, 0);
			UIManager.put("Button.toolbar.hoverBackground", transparent);
			UIManager.put("Button.toolbar.pressedBackground", transparent);
			UIManager.put("Button.toolbar.selectedBackground", transparent);
			UIManager.put("ToggleButton.toolbar.hoverBackground", transparent);
			UIManager.put("ToggleButton.toolbar.pressedBackground", transparent);
			UIManager.put("ToggleButton.toolbar.selectedBackground", transparent);

			// Pestañas con colores exactos del tema Andromeda
			UIManager.put("TabbedPane.selectedBackground", andromedaBg2); // tab.activeBackground #0e1019
			UIManager.put("TabbedPane.selectedForeground", new Color(0xff, 0xe6, 0x6d)); // Amarillo para la pestaña activa
			UIManager.put("TabbedPane.foreground", new Color(0x74, 0x6f, 0x77)); // tab.inactiveForeground #746f77
			UIManager.put("TabbedPane.background", andromedaBg); // tab.inactiveBackground #0c0e14
			// Área de pestañas ligeramente distinta para que se diferencie de las pestañas mismas
			UIManager.put("TabbedPane.tabAreaBackground", andromedaBg); // un tono más claro que el fondo de editor
			UIManager.put("TabbedPane.hoverColor", andromedaHover);
			UIManager.put("TabbedPane.focusColor", andromedaAccent);
			// Línea indicadora aún más delgada y en cyan
			UIManager.put("TabbedPane.underlineHeight", .5);
			UIManager.put("TabbedPane.underlineColor", andromedaAccent); // Cyan para todas
			UIManager.put("TabbedPane.selectedUnderlineColor", andromedaAccent); // #00e8c6 activa
			UIManager.put("TabbedPane.inactiveUnderlineColor", andromedaAccent); // Mismo cyan sin foco
			UIManager.put("TabbedPane.showTabSeparators", true);

			// Colores cyan para UI (etiquetas, botones, menús)
			UIManager.put("Label.foreground", andromedaAccent);
			UIManager.put("Button.foreground", andromedaAccent);
			UIManager.put("MenuItem.foreground", andromedaAccent);
			UIManager.put("MenuItem.selectionForeground", yellowTitle); // Hover amarillo en items
			UIManager.put("Menu.foreground", andromedaAccent);
			UIManager.put("Menu.selectionForeground", yellowTitle); // Hover amarillo en menús
			UIManager.put("Menu.selectionBackground", andromedaHover); // Fondo hover en menús
			UIManager.put("CheckBox.foreground", andromedaAccent);
			UIManager.put("RadioButton.foreground", andromedaAccent);
			UIManager.put("List.focusCellHighlightBorder", new LineBorder(andromedaAccent, 1, true));
			UIManager.put("Table.focusCellHighlightBorder", new LineBorder(andromedaAccent, 1, true));

			// Botones de la barra de título (minimizar, maximizar, cerrar) en amarillo
			UIManager.put(
					"TitlePane.closeHoverBackground", new Color(0xc4, 0x2b, 0x1c)); // Rojo para cerrar hover
			UIManager.put("TitlePane.buttonHoverBackground", andromedaHover);
			UIManager.put("TitlePane.buttonPressedBackground", new Color(0x50, 0x52, 0x5a));
		} catch (Exception e) {

			e.printStackTrace(System.err);
		}

		EventQueue.invokeLater(
				new Runnable() {
					public void run() {
						try {
							StartingWindow frame = new StartingWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * Configura UIManager para JFileChooser con colores Andromeda. Se debe llamar antes de crear cualquier JFileChooser.
	 */
	public static void configureFileChooserTheme() {
		Color andromedaBg = new Color(0x0c, 0x0e, 0x14);
		Color andromedaBg2 = new Color(0x0a, 0x0c, 0x12);
		Color andromedaText = new Color(0xd5, 0xce, 0xd9);
		Color andromedaAccent = new Color(0x00, 0xe8, 0xc6);

		// FileChooser y componentes relacionados
		UIManager.put("FileChooser.background", andromedaBg);
		UIManager.put("FileChooser.foreground", andromedaText);
		UIManager.put("FileChooserUI.background", andromedaBg);
		UIManager.put("FilePane.background", andromedaBg2);
		UIManager.put("FilePane.foreground", andromedaText);
		UIManager.put("List.background", andromedaBg2);
		UIManager.put("List.foreground", andromedaText);
		UIManager.put("List.selectionBackground", andromedaBg2);
		UIManager.put("List.selectionForeground", andromedaAccent);
		UIManager.put("Table.background", andromedaBg2);
		UIManager.put("Table.foreground", andromedaText);
		UIManager.put("Table.selectionBackground", andromedaBg2);
		UIManager.put("Table.selectionForeground", andromedaAccent);
		UIManager.put("TableHeader.background", andromedaBg);
		UIManager.put("TableHeader.foreground", andromedaText);
		UIManager.put("TableHeader.focusCellBackground", andromedaBg);
		UIManager.put("TableHeader.focusCellForeground", andromedaText);
		UIManager.put("TextField.background", andromedaBg2);
		UIManager.put("TextField.foreground", andromedaText);
		UIManager.put("FileChooser.detailViewBackground", andromedaBg2);
		UIManager.put("FileChooser.detailViewSelectionBackground", andromedaBg2);
		UIManager.put("FileChooser.detailViewSelectionForeground", andromedaAccent);
		UIManager.put("FileChooser.listViewBackground", andromedaBg2);
		UIManager.put("FileChooser.listViewSelectionBackground", andromedaBg2);
		UIManager.put("FileChooser.listViewSelectionForeground", andromedaAccent);
		UIManager.put("List.focusCellHighlightBorder", new LineBorder(andromedaAccent, 1, true));
		UIManager.put("Table.focusCellHighlightBorder", new LineBorder(andromedaAccent, 1, true));
	}

	/**
	 * Colorea la barra de título de un JFileChooser (o cualquier JDialog/JFrame). Se debe llamar después de que el diálogo esté visible.
	 */
	public static void colorizeFileChooserTitleBar(java.awt.Window window) {
		if (window == null)
			return;
		Color andromedaBg = new Color(0x0c, 0x0e, 0x14);
		Color andromedaBg2 = new Color(0x0a, 0x0c, 0x12);
		Color yellowTitle = new Color(0xff, 0xe6, 0x6d);
		Color andromedaHover = new Color(0x37, 0x39, 0x41);

		javax.swing.JRootPane rootPane = null;
		if (window instanceof javax.swing.JFrame) {
			rootPane = ((javax.swing.JFrame) window).getRootPane();
		} else if (window instanceof javax.swing.JDialog) {
			rootPane = ((javax.swing.JDialog) window).getRootPane();
		}

		if (rootPane != null) {
			rootPane.putClientProperty("JRootPane.titleBarBackground", andromedaBg2);
			rootPane.putClientProperty("JRootPane.titleBarForeground", yellowTitle);
			rootPane.putClientProperty("JRootPane.titleBarInactiveBackground", andromedaBg);
			rootPane.putClientProperty("JRootPane.titleBarButtonsForeground", yellowTitle);
			rootPane.putClientProperty("JRootPane.titleBarButtonsHoverBackground", andromedaHover);
			rootPane.putClientProperty("JRootPane.titleBarIconColor", yellowTitle);
		}
		try {
			window.setBackground(andromedaBg2);
		} catch (Exception ignored) {
			// No-op si la implementación de ventana no lo permite
		}
	}
}
