package org.tectuinno.config;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import com.formdev.flatlaf.FlatDarkLaf;

/**
 * Configuración general y Setup para la librería Flatlaf
 */
public class FlatlafManager {

	private class ColorsPalet {
		public static final Color andromedaBg = new Color(0x0c, 0x0e, 0x14);
		public static final Color andromedaBg2 = new Color(0x0a, 0x0c, 0x12); // editor.background más oscuro
		public static final Color andromedaHover = new Color(0x37, 0x39, 0x41); // editor.hoverHighlightBackground
		public static final Color andromedaAccent = new Color(0x00, 0xe8, 0xc6); // #00e8c6 cyan
		public static final Color yellowTitle = new Color(0xff, 0xe6, 0x6d); // #ffe66d amarillo para barra de título
		public static final Color andromedaText = new Color(0xd5, 0xce, 0xd9);
		public static final Color transparent = new Color(0, 0, 0, 0);
	}

	/**
	 * Se habilita el renderizado solo por software, deshabilitando el uso de
	 * GPU/Overheating
	 */
	public static void enableOnlySoftwareRendering() {

		try {

			System.setProperty("sun.java2d.d3d", "false");
			System.setProperty("sun.java2d.opengl", "false");

		} catch (Exception e) {

			e.printStackTrace(System.err);

		}

	}

	public static void setUpFlatlafLookAndFell() {

		try {

			System.setProperty("flatlaf.useWindowDecorations", "true");
			FlatDarkLaf.setup();

		} catch (Exception e) {

			e.printStackTrace(System.err);

		}

	}

	public static void setupBorderRadiusArcs() {

		try {

			// Redondeo y estilo visual similar a VS Code
			UIManager.put("Component.arc", 12);
			UIManager.put("Button.arc", 12);
			UIManager.put("TextComponent.arc", 8);
			UIManager.put("ScrollBar.thumbArc", 999);
			UIManager.put("ScrollBar.trackInsets", 2);

		} catch (Exception e) {

			e.printStackTrace(System.err);

		}

	}

	/**
	 * Configura UIManager para JFileChooser con colores Andromeda. Se debe llamar
	 * antes de crear cualquier JFileChooser.
	 */
	public static void configureFileChooserTheme() {

		try {
			UIManager.put("FileChooser.background", ColorsPalet.andromedaBg);
			UIManager.put("FileChooser.foreground", ColorsPalet.andromedaText);
			UIManager.put("FileChooserUI.background", ColorsPalet.andromedaBg);
			UIManager.put("FilePane.background", ColorsPalet.andromedaBg2);
			UIManager.put("FilePane.foreground", ColorsPalet.andromedaText);
			UIManager.put("List.background", ColorsPalet.andromedaBg2);
			UIManager.put("List.foreground", ColorsPalet.andromedaText);
			UIManager.put("List.selectionBackground", ColorsPalet.andromedaBg2);
			UIManager.put("List.selectionForeground", ColorsPalet.andromedaAccent);
			UIManager.put("Table.background", ColorsPalet.andromedaBg2);
			UIManager.put("Table.foreground", ColorsPalet.andromedaText);
			UIManager.put("Table.selectionBackground", ColorsPalet.andromedaBg2);
			UIManager.put("Table.selectionForeground", ColorsPalet.andromedaAccent);
			UIManager.put("TableHeader.background", ColorsPalet.andromedaBg);
			UIManager.put("TableHeader.foreground", ColorsPalet.andromedaText);
			UIManager.put("TableHeader.focusCellBackground", ColorsPalet.andromedaBg);
			UIManager.put("TableHeader.focusCellForeground", ColorsPalet.andromedaText);
			UIManager.put("TextField.background", ColorsPalet.andromedaBg2);
			UIManager.put("TextField.foreground", ColorsPalet.andromedaText);
			UIManager.put("FileChooser.detailViewBackground", ColorsPalet.andromedaBg2);
			UIManager.put("FileChooser.detailViewSelectionBackground", ColorsPalet.andromedaBg2);
			UIManager.put("FileChooser.detailViewSelectionForeground", ColorsPalet.andromedaAccent);
			UIManager.put("FileChooser.listViewBackground", ColorsPalet.andromedaBg2);
			UIManager.put("FileChooser.listViewSelectionBackground", ColorsPalet.andromedaBg2);
			UIManager.put("FileChooser.listViewSelectionForeground", ColorsPalet.andromedaAccent);
			UIManager.put("List.focusCellHighlightBorder", new LineBorder(ColorsPalet.andromedaAccent, 1, true));
			UIManager.put("Table.focusCellHighlightBorder", new LineBorder(ColorsPalet.andromedaAccent, 1, true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Colorea la barra de título de un JFileChooser (o cualquier JDialog/JFrame). Se debe llamar después de que el diálogo esté visible.
	 */
	public static void colorizeFileChooserTitleBar(java.awt.Window window) {
		if (window == null)
			return;		

		javax.swing.JRootPane rootPane = null;
		if (window instanceof javax.swing.JFrame) {
			rootPane = ((javax.swing.JFrame) window).getRootPane();
		} else if (window instanceof javax.swing.JDialog) {
			rootPane = ((javax.swing.JDialog) window).getRootPane();
		}

		if (rootPane != null) {
			rootPane.putClientProperty("JRootPane.titleBarBackground", ColorsPalet.andromedaBg2);
			rootPane.putClientProperty("JRootPane.titleBarForeground", ColorsPalet.yellowTitle);
			rootPane.putClientProperty("JRootPane.titleBarInactiveBackground", ColorsPalet.andromedaBg);
			rootPane.putClientProperty("JRootPane.titleBarButtonsForeground", ColorsPalet.yellowTitle);
			rootPane.putClientProperty("JRootPane.titleBarButtonsHoverBackground", ColorsPalet.andromedaHover);
			rootPane.putClientProperty("JRootPane.titleBarIconColor", ColorsPalet.yellowTitle);
		}
		try {
			
			window.setBackground(ColorsPalet.andromedaBg2);
			
		} catch (Exception ignored) {
			
			
			
		}
	}

	public static void paintComponents() {

		try {

			UIManager.put("TitlePane.background", ColorsPalet.andromedaBg);
			UIManager.put("TitlePane.foreground", ColorsPalet.yellowTitle);
			UIManager.put("TitlePane.inactiveForeground", ColorsPalet.yellowTitle);
			UIManager.put("TitlePane.iconColor", ColorsPalet.yellowTitle);
			UIManager.put("MenuBar.background", ColorsPalet.andromedaBg);
			UIManager.put("Menu.background", ColorsPalet.andromedaBg2);
			UIManager.put("MenuItem.background", ColorsPalet.andromedaBg2);
			UIManager.put("MenuItem.selectionBackground", ColorsPalet.andromedaHover);
			UIManager.put("PopupMenu.background", ColorsPalet.andromedaBg2);
			UIManager.put("Button.background", ColorsPalet.andromedaBg2);
			UIManager.put("Button.hoverBackground", ColorsPalet.andromedaHover);
			UIManager.put("ToolBar.background", ColorsPalet.andromedaBg);
			UIManager.put("Panel.background", ColorsPalet.andromedaBg);
			UIManager.put("TabbedPane.background", ColorsPalet.andromedaBg);
			UIManager.put("TabbedPane.contentAreaColor", ColorsPalet.andromedaBg2);
			UIManager.put("TitlePane.unifiedBackground", true);
			UIManager.put("TabbedPane.tabsBackground", ColorsPalet.andromedaBg); // Cabecera de pestañas
			UIManager.put("ComboBox.background", ColorsPalet.andromedaBg2);
			UIManager.put("ComboBox.foreground", ColorsPalet.yellowTitle);
			UIManager.put("ComboBox.selectionBackground", ColorsPalet.andromedaHover);
			UIManager.put("ComboBox.selectionForeground", ColorsPalet.yellowTitle);
			UIManager.put("ComboBox.buttonBackground", ColorsPalet.andromedaBg2);
			UIManager.put("ComboBox.buttonHoverBackground", ColorsPalet.andromedaHover);
			UIManager.put("ComboBox.buttonArrowColor", ColorsPalet.andromedaAccent);
			UIManager.put("Spinner.background", ColorsPalet.andromedaBg2);
			UIManager.put("Spinner.foreground", ColorsPalet.yellowTitle);
			UIManager.put("Spinner.buttonBackground", ColorsPalet.andromedaBg2);
			UIManager.put("Spinner.buttonHoverBackground", ColorsPalet.andromedaHover);
			UIManager.put("Spinner.buttonArrowColor", ColorsPalet.andromedaAccent);
			
			UIManager.put("Button.toolbar.hoverBackground", ColorsPalet.transparent);
			UIManager.put("Button.toolbar.pressedBackground", ColorsPalet.transparent);
			UIManager.put("Button.toolbar.selectedBackground", ColorsPalet.transparent);
			UIManager.put("ToggleButton.toolbar.hoverBackground", ColorsPalet.transparent);
			UIManager.put("ToggleButton.toolbar.pressedBackground", ColorsPalet.transparent);
			UIManager.put("ToggleButton.toolbar.selectedBackground", ColorsPalet.transparent);

			// Pestañas con colores exactos del tema Andromeda
			UIManager.put("TabbedPane.selectedBackground", ColorsPalet.andromedaBg2); // tab.activeBackground #0e1019
			UIManager.put("TabbedPane.selectedForeground", new Color(0xff, 0xe6, 0x6d)); // Amarillo para la pestaña activa
			UIManager.put("TabbedPane.foreground", new Color(0x74, 0x6f, 0x77)); // tab.inactiveForeground #746f77
			UIManager.put("TabbedPane.background", ColorsPalet.andromedaBg); // tab.inactiveBackground #0c0e14
			// Área de pestañas ligeramente distinta para que se diferencie de las pestañas mismas
			UIManager.put("TabbedPane.tabAreaBackground", ColorsPalet.andromedaBg); // un tono más claro que el fondo de editor
			UIManager.put("TabbedPane.hoverColor", ColorsPalet.andromedaHover);
			UIManager.put("TabbedPane.focusColor", ColorsPalet.andromedaAccent);
			// Línea indicadora aún más delgada y en cyan
			UIManager.put("TabbedPane.underlineHeight", .5);
			UIManager.put("TabbedPane.underlineColor", ColorsPalet.andromedaAccent); // Cyan para todas
			UIManager.put("TabbedPane.selectedUnderlineColor", ColorsPalet.andromedaAccent); // #00e8c6 activa
			UIManager.put("TabbedPane.inactiveUnderlineColor", ColorsPalet.andromedaAccent); // Mismo cyan sin foco
			UIManager.put("TabbedPane.showTabSeparators", true);

			// Colores cyan para UI (etiquetas, botones, menús)
			UIManager.put("Label.foreground", ColorsPalet.andromedaAccent);
			UIManager.put("Button.foreground", ColorsPalet.andromedaAccent);
			UIManager.put("MenuItem.foreground", ColorsPalet.andromedaAccent);
			UIManager.put("MenuItem.selectionForeground", ColorsPalet.yellowTitle); // Hover amarillo en items
			UIManager.put("Menu.foreground",ColorsPalet.andromedaAccent);
			UIManager.put("Menu.selectionForeground", ColorsPalet.yellowTitle); // Hover amarillo en menús
			UIManager.put("Menu.selectionBackground", ColorsPalet.andromedaHover); // Fondo hover en menús
			UIManager.put("CheckBox.foreground", ColorsPalet.andromedaAccent);
			UIManager.put("RadioButton.foreground", ColorsPalet.andromedaAccent);
			UIManager.put("List.focusCellHighlightBorder", new LineBorder(ColorsPalet.andromedaAccent, 1, true));
			UIManager.put("Table.focusCellHighlightBorder", new LineBorder(ColorsPalet.andromedaAccent, 1, true));

			// Botones de la barra de título (minimizar, maximizar, cerrar) en amarillo
			UIManager.put(
					"TitlePane.closeHoverBackground", new Color(0xc4, 0x2b, 0x1c)); // Rojo para cerrar hover
			UIManager.put("TitlePane.buttonHoverBackground", ColorsPalet.andromedaHover);
			UIManager.put("TitlePane.buttonPressedBackground", new Color(0x50, 0x52, 0x5a));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void colorizeWarningDialogs() {
		
		try {
			
			// Diálogos de advertencia (JOptionPane)
			UIManager.put("OptionPane.background", ColorsPalet.andromedaBg2);
			UIManager.put("OptionPane.messageForeground", ColorsPalet.andromedaAccent);
			UIManager.put("OptionPane.buttonBackground", ColorsPalet.andromedaBg2);
			UIManager.put("Component.accentColor", ColorsPalet.andromedaAccent); // Morado Andromeda
			
		}catch (Exception e) {
			
			e.printStackTrace(System.err);
			
		}
		
	}
	
	public static void colorizeJFileChooser() {
		
		try {
			
			UIManager.put("FileChooser.background", ColorsPalet.andromedaBg2);
			UIManager.put("FileChooser.foreground", ColorsPalet.andromedaAccent);
			UIManager.put("FileChooser.listViewBackground", ColorsPalet.andromedaBg2);
			UIManager.put("FileChooser.listViewForeground", ColorsPalet.andromedaAccent);
			UIManager.put("FileChooser.listViewSelectionBackground", ColorsPalet.andromedaBg2);
			UIManager.put("FileChooser.listViewSelectionForeground", ColorsPalet.andromedaAccent);
			UIManager.put("FileChooser.listViewBorder", new LineBorder(ColorsPalet.andromedaAccent, 1, true));
			UIManager.put("FileChooser.listBackground", ColorsPalet.andromedaBg2);
			UIManager.put("FileChooser.listSelectionBackground", ColorsPalet.andromedaBg2);
			UIManager.put("FileChooser.listSelectionForeground", ColorsPalet.andromedaAccent);
			UIManager.put("FileChooser.iconViewBackground", ColorsPalet.andromedaBg2);
			UIManager.put("FileChooser.iconViewSelectionBackground", ColorsPalet.andromedaBg2);
			UIManager.put("FileChooser.iconViewSelectionForeground", ColorsPalet.andromedaAccent);
			UIManager.put("FileChooser.iconViewBorder", new LineBorder(ColorsPalet.andromedaAccent, 1, true));
			UIManager.put("FileChooser.detailViewBackground", ColorsPalet.andromedaBg2);
			UIManager.put("FileChooser.detailViewSelectionBackground", ColorsPalet.andromedaBg2);
			UIManager.put("FileChooser.detailViewSelectionForeground", ColorsPalet.andromedaAccent);
			UIManager.put("FileChooser.detailViewBorder", new LineBorder(ColorsPalet.andromedaAccent, 1, true));
			UIManager.put("FileChooser.sidebarBackground", ColorsPalet.andromedaBg);
			UIManager.put("FileChooser.sidebarSelectionBackground", ColorsPalet.andromedaBg);
			UIManager.put("FileChooser.sidebarSelectionForeground", ColorsPalet.andromedaAccent);
			UIManager.put("FileChooser.sidebarFocusCellHighlightBorder", new LineBorder(ColorsPalet.andromedaAccent, 1, true));
			UIManager.put("FileChooser.lookInLabelForeground", ColorsPalet.yellowTitle);
			UIManager.put("FileChooser.filesOfTypeLabelForeground", ColorsPalet.yellowTitle);
			UIManager.put("FileChooser.toolbarButtonForeground", ColorsPalet.andromedaAccent);
			UIManager.put("FileChooser.toolbarButtonHoverBackground", ColorsPalet.andromedaHover);
			UIManager.put("FileChooser.toolbarButtonPressedBackground", ColorsPalet.andromedaHover);
			
		}catch (Exception e) {
			
			e.printStackTrace(System.err);
		}
		
	}
}
