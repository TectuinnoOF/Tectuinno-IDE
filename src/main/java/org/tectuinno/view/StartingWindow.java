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

/** Ventana principal del IDE con editor, consola y toolbars. */
// Desactivado: highLight() en changedUpdate provocaba ralentizaciones fuertes
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.DebugGraphics;
/* removed unused imports to satisfy linter */
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComboBox;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.tectuinno.compiler.assembler.AsmFirstPass;
import org.tectuinno.compiler.assembler.AnalysisError;
import org.tectuinno.compiler.assembler.AsmLexer;
import org.tectuinno.compiler.assembler.AsmParser;
import org.tectuinno.compiler.assembler.AsmSecondPass;
import org.tectuinno.compiler.assembler.AsmSemanticAnalyzer;
import org.tectuinno.compiler.assembler.EncoderIrLine;
import org.tectuinno.compiler.assembler.encode.FrameUtil;
import org.tectuinno.compiler.assembler.utils.AsmListingFormatter;
import org.tectuinno.compiler.assembler.utils.Token;
import org.tectuinno.App;
import org.tectuinno.io.PortInfo;
import org.tectuinno.io.SerialPortService;
import org.tectuinno.utils.DialogResult;
import org.tectuinno.utils.ExampleResources;
import org.tectuinno.utils.FileType;
import org.tectuinno.view.assembler.AsmEditorInternalFrame;
import org.tectuinno.view.assembler.AsmEditorPane;
import org.tectuinno.view.component.FrAbout;
import org.tectuinno.view.component.FrLicenseInfo;
import org.tectuinno.view.component.FrWiFiWizarDialog;
import org.tectuinno.view.component.ResultConsolePanel;
import org.tectuinno.view.component.ModernNotification;

public class StartingWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JMenuBar menuBar;
	private JMenu JMenuFile;
	private JMenu JMenuArchivoNuevo;
	private JMenuItem MenuItemNvoAsm;
	/* private JMenuItem MenuItemFicheroTexto; */
	private JPanel panelToolBar;
	private JToolBar compilerToolBar;
	private JButton btnAnalice;
	private JMenuItem JMenuArchivoGuardar;
	private JSplitPane splitPaneEditorAndConsole;
	private JTabbedPane editorTabs;
	private ResultConsolePanel consolePanel;
	private boolean isSemanticCorrect;
	private boolean isSintaxCorrect;
	private JButton btnConvert;
	private List<Token> tokens;
	private byte[] preparedFrame = new byte[0];
	private List<EncoderIrLine> encodedIrLineResult;
	private final JComboBox<String> cmbEnableComDevices = new JComboBox<String>();
	/*
	 * private final JMenu JMenuEdit = new JMenu("Editar"); private final JMenu JMenuProgram = new JMenu("Programa"); private final JMenu JMenuTools = new JMenu("Herramientas");
	 */
	private final JButton btnSearchComDevices = new JButton("Escanear");
	private final JSeparator separator = new JSeparator();
	private List<PortInfo> lastPorts = List.of();
	private javax.swing.Timer comScanTimer;
	private JMenuItem JMenuArchivoAbrir;
	private JSeparator separator_1;
	private JMenu jMenuItemEjemplos;
	private JButton btnWifiSend;

	// Barra de estado inferior con contador de errores
	private JPanel statusBarPanel;
	private JLabel statusBarLabel;

	/**
	 * Muestra notificación moderna flotante de error (se cierra automáticamente).
	 */
	private void showErrorDialog(String message) {
		ModernNotification.showError(this, message);
	}

	/**
	 * Muestra notificación moderna flotante de información (se cierra automáticamente).
	 */
	private void showInfoDialog(String message) {
		ModernNotification.showInfo(this, message);
	}

	/**
	 * Muestra notificación moderna flotante de advertencia (se cierra automáticamente).
	 */
	private void showWarnDialog(String message) {
		ModernNotification.showWarning(this, message);
	}

	// Removed unused cache field to satisfy strict compilation settings
	private JMenu JMenuEdit;
	private JMenuItem JMenuOptionUndoo;
	private JMenuItem JMenuOptionRedo;
	private JMenuItem JMenuOptionCopy;
	private JMenuItem JMenuOptionCut;
	private JMenuItem JMenuOptionPaste;
	private JSeparator separator_2;
	private JMenuItem JMenuOptionGoToLine;
	private JMenuItem JMenuOptionSelectAll;
	private JMenu JMenuHelp;
	private JMenuItem JMenuOptionAbout;
	private JMenuItem JMenuOptionLicense;
	private JMenu JMenuSourceRepository;
	private JMenuItem JMenuOptionSourceCode;
	private JMenuItem JMenuOptionWiki;
	// private List<String> opennedEditors;

	/**
	 * Create the frame.
	 */
	public StartingWindow() {

		// Paleta Andromeda usada en App.java
		Color andromedaBg = new Color(0x0c, 0x0e, 0x14);
		Color andromedaBg2 = new Color(0x0a, 0x0c, 0x12);
		Color yellowTitle = new Color(0xff, 0xe6, 0x6d);
		// Colorear explícitamente la barra de título cuando FlatLaf controla las
		// decoraciones
		getRootPane().putClientProperty("JRootPane.titleBarBackground", andromedaBg2);
		getRootPane().putClientProperty("JRootPane.titleBarForeground", yellowTitle);
		getRootPane().putClientProperty("JRootPane.titleBarInactiveBackground", andromedaBg);
		// Colorear iconos de minimizar/maximizar/cerrar en amarillo
		getRootPane().putClientProperty("JRootPane.titleBarButtonsForeground", yellowTitle);
		getRootPane().putClientProperty("JRootPane.titleBarButtonsHoverBackground", new Color(0x37, 0x39, 0x41));

		this.isSemanticCorrect = false;
		this.isSintaxCorrect = false;

		// Escaneo automático de puertos COM y auto-selección del único conectado
		this.comScanTimer = new javax.swing.Timer(1500, e -> refreshComPortsAutoSelect());
		this.comScanTimer.setRepeats(true);
		this.comScanTimer.start();

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setConsoleDividerLocationEvent();
			}
		});
		setTitle("Tectuinno IDE");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 960, 692);

		menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		menuBar.setBackground(andromedaBg);
		menuBar.setForeground(yellowTitle);
		setJMenuBar(menuBar);

		JMenuFile = new JMenu("Archivo");
		JMenuFile.setForeground(yellowTitle);
		menuBar.add(JMenuFile);

		JMenuArchivoNuevo = new JMenu("Nuevo");
		JMenuFile.add(JMenuArchivoNuevo);

		MenuItemNvoAsm = new JMenuItem("Fichero ASM Risc-V");
		MenuItemNvoAsm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openNewAsmEditor();
			}
		});
		// Atajo: Ctrl/Cmd + N para nuevo archivo ASM
		MenuItemNvoAsm.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		JMenuArchivoNuevo.add(MenuItemNvoAsm);

		// Atajo global: Ctrl/Cmd + N en toda la ventana para nuevo archivo
		{
			javax.swing.InputMap im = getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
			javax.swing.ActionMap am = getRootPane().getActionMap();
			javax.swing.KeyStroke ksNew = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N,
					java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
			im.put(ksNew, "global-new-file");
			am.put("global-new-file", new javax.swing.AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					openNewAsmEditor();
				}
			});
		}

		/*
		 * MenuItemFicheroTexto = new JMenuItem("Texto"); JMenuArchivoNuevo.add(MenuItemFicheroTexto);
		 */

		JMenuArchivoGuardar = new JMenuItem("Guardar");
		JMenuArchivoGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveCurrentFile();
			}
		});
		// Atajo: Ctrl/Cmd + S
		JMenuArchivoGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		JMenuFile.add(JMenuArchivoGuardar);

		// Guardar Como...
		javax.swing.JMenuItem jMenuArchivoGuardarComo = new javax.swing.JMenuItem("Guardar Como...");
		jMenuArchivoGuardarComo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAsCurrentFile();
			}
		});
		// Atajo: Ctrl/Cmd + Shift + S
		jMenuArchivoGuardarComo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
		JMenuFile.add(jMenuArchivoGuardarComo);

		JMenuArchivoAbrir = new JMenuItem("Abrir");
		JMenuArchivoAbrir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openAsmFile();
			}
		});
		// Atajo: Ctrl/Cmd + O
		JMenuArchivoAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		JMenuFile.add(JMenuArchivoAbrir);

		separator_1 = new JSeparator();
		JMenuFile.add(separator_1);

		jMenuItemEjemplos = new JMenu("Ejemplos");
		JMenuFile.add(jMenuItemEjemplos);

		JMenuEdit = new JMenu("Editar");
		JMenuEdit.setForeground(yellowTitle);
		menuBar.add(JMenuEdit);

		JMenuOptionUndoo = new JMenuItem("Deshacer");
		JMenuOptionUndoo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				undoActionInActiveEditor();
			}
		});
		// Atajo: Ctrl/Cmd + Z
		JMenuOptionUndoo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		JMenuEdit.add(JMenuOptionUndoo);

		JMenuOptionRedo = new JMenuItem("Rehacer");
		JMenuOptionRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				redoActionInActiveEditor();
			}
		});
		// Atajo: Ctrl/Cmd + Y y Ctrl/Cmd + Shift + Z
		JMenuOptionRedo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		JMenuEdit.add(JMenuOptionRedo);

		JMenuOptionCopy = new JMenuItem("Copiar");
		JMenuOptionCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Copiar el texto seleccionado
				copySelectedCodeToSystemClipboard();
			}
		});
		// Atajo: Ctrl/Cmd + C
		JMenuOptionCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		JMenuEdit.add(JMenuOptionCopy);

		JMenuOptionCut = new JMenuItem("Cortar");
		JMenuOptionCut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cutSelectedCodeToSystemClipboard();
			}
		});
		// Atajo: Ctrl/Cmd + X
		JMenuOptionCut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		JMenuEdit.add(JMenuOptionCut);

		JMenuOptionPaste = new JMenuItem("Pegar");
		JMenuOptionPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pasteCurrentContextOfTheClipboard();
			}
		});
		// Atajo: Ctrl/Cmd + V
		JMenuOptionPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		JMenuEdit.add(JMenuOptionPaste);

		separator_2 = new JSeparator();
		JMenuEdit.add(separator_2);

		JMenuOptionGoToLine = new JMenuItem("Ir a línea...");
		// Atajo: Ctrl/Cmd + G
		JMenuOptionGoToLine.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		JMenuOptionGoToLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goToLineInActiveEditor();
			}
		});
		JMenuEdit.add(JMenuOptionGoToLine);

		JMenuOptionSelectAll = new JMenuItem("Seleccionar todo");
		// Atajo: Ctrl/Cmd + A
		JMenuOptionSelectAll.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		JMenuEdit.add(JMenuOptionSelectAll);

		JMenuEdit.add(new JSeparator());

		JMenuItem JMenuOptionFind = new JMenuItem("Buscar...");
		// Atajo: Ctrl/Cmd + B
		JMenuOptionFind.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B,
				java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		JMenuOptionFind.addActionListener(e -> {
			AsmEditorInternalFrame frame = getActiveEditorFrame();
			if (frame != null) {
				frame.showSearchDialog();
			}
		});
		JMenuEdit.add(JMenuOptionFind);
		
		JMenuHelp = new JMenu("Ayuda");
		menuBar.add(JMenuHelp);
		
		JMenuOptionAbout = new JMenuItem("Acerca de...");
		JMenuOptionAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openAboutForm();
			}
		});
		JMenuHelp.add(JMenuOptionAbout);
		
		JMenuOptionLicense = new JMenuItem("Licencia");
		JMenuOptionLicense.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openLicenseInfoForm();
			}
		});
		JMenuHelp.add(JMenuOptionLicense);
		
		JMenuSourceRepository = new JMenu("Repositorio");
		JMenuHelp.add(JMenuSourceRepository);
		
		JMenuOptionSourceCode = new JMenuItem("Código");
		JMenuSourceRepository.add(JMenuOptionSourceCode);
		
		JMenuOptionWiki = new JMenuItem("Wiki Tectuinno IDE");
		JMenuSourceRepository.add(JMenuOptionWiki);

		// Opciones de tamaño de fuente desactivadas: se usa el zoom con Ctrl + rueda
		/*
		 * { menuBar.add(JMenuEdit); } { menuBar.add(JMenuProgram); } { menuBar.add(JMenuTools); { }
		 */
		contentPane = new JPanel();
		contentPane.setBackground(andromedaBg);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		panelToolBar = new JPanel();
		panelToolBar.setOpaque(true);
		panelToolBar.setBackground(andromedaBg);
		FlowLayout flowLayout = (FlowLayout) panelToolBar.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(panelToolBar, BorderLayout.NORTH);

		compilerToolBar = new JToolBar();
		compilerToolBar.setOpaque(true);
		compilerToolBar.setBackground(andromedaBg2);
		panelToolBar.add(compilerToolBar);

		{ /* Analice code button settings, events and configurations */

			btnAnalice = new JButton("Verificar");

			btnAnalice.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					final AsmEditorInternalFrame activeFrame = getActiveEditorFrame();
					if (activeFrame != null) {
						activeFrame.getAsmEditorPane().clearInlineErrors();
					}

					tokens = analizeCurrentLexer();
					if (tokens.isEmpty()) {
						showErrorDialog("No hay contenido que verificar.");
						return;
					}

					new Thread() {

						@Override
						public void run() {
							consolePanel.getTokenTerminalPanel()
									.writteIn("===========================================================\n");

							for (Token token : tokens) {
								consolePanel.getTokenTerminalPanel().writteIn(token.toString() + " \n");
							}

							consolePanel.getTokenTerminalPanel()
									.writteIn("===========================================================\n");
						}

					}.start();

					new Thread() {
						@Override
						public void run() {
							if (activeFrame == null) {
								return;
							}

							List<AnalysisError> collectedErrors = new java.util.ArrayList<>();
							List<AnalysisError> syntaxErrors = asmSyntaxParse(tokens);
							collectedErrors.addAll(syntaxErrors);

							if (!isSintaxCorrect) {
								final int errorCount = collectedErrors.size();
								SwingUtilities.invokeLater(() -> {
									btnConvert.setEnabled(false);
									activeFrame.getAsmEditorPane().setInlineErrors(collectedErrors);
									updateStatusBar(errorCount, 0);
									showErrorDialog("Existen errores de sintaxis en el código");
								});
								return;
							}

							List<AnalysisError> semanticErrors = asmSemanticParse(tokens);
							collectedErrors.addAll(semanticErrors);

							if (!isSemanticCorrect) {
								final int errorCount = collectedErrors.size();
								SwingUtilities.invokeLater(() -> {
									btnConvert.setEnabled(false);
									activeFrame.getAsmEditorPane().setInlineErrors(collectedErrors);
									updateStatusBar(errorCount, 0);
									showErrorDialog("Existen errores de Semanticos en el código");
								});
								return;
							}

							SwingUtilities.invokeLater(() -> {
								activeFrame.getAsmEditorPane().setInlineErrors(collectedErrors);
								btnConvert.setEnabled(true);
								updateStatusBar(0, 0);
							});

						};
					}.start();

					/*
					 * new Thread() {
					 * 
					 * @Override public void run() { asmSyntaxParse(tokens); } }.start();
					 */

				}
			});

			compilerToolBar.add(btnAnalice);

		}

		{ /* Convert to machine code button settings and configurations */

			btnConvert = new JButton("Convertir");
			btnConvert.setEnabled(false);
			btnConvert.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					showDisassemblyResult();
					preparedOrderedHex(encodedIrLineResult);

				}
			});
			compilerToolBar.add(btnConvert);

		}

		JButton btnEnviarLocal = new JButton("Enviar");
		btnEnviarLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendDataToMicroContoller();
			}
		});
		compilerToolBar.add(btnEnviarLocal);
		{
			separator.setOrientation(SwingConstants.VERTICAL);
			compilerToolBar.add(separator);
		}
		{
			cmbEnableComDevices.setPreferredSize(new Dimension(190, 22));
			compilerToolBar.add(cmbEnableComDevices);
			// this.cmbEnableComDevices
		}
		{
			btnSearchComDevices.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					searchForComDevices();
				}
			});
			compilerToolBar.add(btnSearchComDevices);
		}

		btnWifiSend = new JButton("WiFi Programmer");
		btnWifiSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openWifiWizard();
			}
		});

		compilerToolBar.add(btnWifiSend);

		splitPaneEditorAndConsole = new JSplitPane();
		splitPaneEditorAndConsole.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneEditorAndConsole.setDividerSize(5);
		splitPaneEditorAndConsole.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
		splitPaneEditorAndConsole.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		splitPaneEditorAndConsole.setContinuousLayout(false);
		splitPaneEditorAndConsole.setBackground(andromedaBg);
		contentPane.add(splitPaneEditorAndConsole, BorderLayout.CENTER);

		editorTabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		editorTabs.setBackground(andromedaBg2); // Color oscuro del editor
		editorTabs.setForeground(new Color(0x74, 0x6f, 0x77)); // #746f77
		editorTabs.setBorder(new EmptyBorder(4, 4, 0, 4));
		editorTabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateTabLabelColors();
			}
		});
		splitPaneEditorAndConsole.setLeftComponent(editorTabs);

		consolePanel = new ResultConsolePanel();
		splitPaneEditorAndConsole.setRightComponent(consolePanel);
		splitPaneEditorAndConsole.setDividerLocation(410);

		// Barra de estado inferior con contador de errores/advertencias
		statusBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
		statusBarPanel.setBackground(andromedaBg2);
		statusBarPanel.setBorder(new EmptyBorder(2, 8, 2, 8));
		statusBarLabel = new JLabel("✓ Sin problemas");
		statusBarLabel.setForeground(new Color(0x00, 0xe8, 0xc6)); // Cyan
		statusBarPanel.add(statusBarLabel);
		contentPane.add(statusBarPanel, BorderLayout.SOUTH);

		this.searchForComDevices();

		this.builExamplesMenu();

		// Abrir automáticamente un editor temporal al iniciar (como Word)
		try {
			openNewAsmEditor("Sin título.asm", "");
		} catch (Exception ex) {
			// Si falla, no interrumpir el inicio
			ex.printStackTrace(System.err);
		}
	}

	/*
	 * private boolean buildAll() {
	 * 
	 * List<Token> tokens = analizeCurrentLexer();
	 * 
	 * asmSyntaxParse(tokens);
	 * 
	 * if(!isSintaxCorrect) { JOptionPane.showMessageDialog(null, "Existen errores de sintaxis en el código", "Error", JOptionPane.ERROR_MESSAGE); return false; };
	 * 
	 * asmSemanticParse(tokens);
	 * 
	 * if(!isSemanticCorrect) { JOptionPane.showMessageDialog(null, "Existen errores de Semanticos en el código", "Error", JOptionPane.ERROR_MESSAGE); }; }
	 */

	private void searchForComDevices() {
		// Escaneo inicial manual: delega en el refresco con auto-selección
		refreshComPortsAutoSelect();
	}

	private void refreshComPortsAutoSelect() {
		List<PortInfo> current = SerialPortService.listAvailablePorts();
		boolean changed = portsChanged(lastPorts, current);
		if (!changed) {
			return;
		}
		lastPorts = current;
		this.cmbEnableComDevices.removeAllItems();
		if (current.isEmpty()) {
			this.cmbEnableComDevices.addItem("Conecta un dispositivo...");
			this.cmbEnableComDevices.setSelectedIndex(0);
			return;
		}
		for (var pi : current) {
			this.cmbEnableComDevices.addItem(pi.systemName() + " - " + pi.descriptiveName());
		}
		// Auto-selección: si solo hay uno, seleccionarlo; si el seleccionado ya no existe, seleccionar el primero
		if (current.size() == 1) {
			this.cmbEnableComDevices.setSelectedIndex(0);
		} else {
			int idx = this.cmbEnableComDevices.getSelectedIndex();
			if (idx < 0 || idx >= current.size()) {
				this.cmbEnableComDevices.setSelectedIndex(0);
			}
		}
	}

	private boolean portsChanged(List<PortInfo> oldList, List<PortInfo> newList) {
		if (oldList.size() != newList.size())
			return true;
		for (int i = 0; i < oldList.size(); i++) {
			PortInfo a = oldList.get(i);
			PortInfo b = newList.get(i);
			if (!a.systemName().equals(b.systemName()))
				return true;
		}
		return false;
	}

	public void saveCurrentFile() {

		AsmEditorInternalFrame frame = getActiveEditorFrame();

		if (frame != null) {
			String contenido = frame.asmGetEditorText();
			String titulo = frame.getTitle();
			File archivo = new File(titulo);
			archivo = frame.getArchivoActual();
			if (archivo == null) {
				// Aplicar colores Andromeda a JFileChooser
				App.configureFileChooserTheme();
				JFileChooser guardado = new JFileChooser();
				guardado.setDialogTitle("Guardar archivo");
				guardado.setSelectedFile(new File(titulo));
				colorizeFileChooserWindowAsync(guardado);
				int opcion = guardado.showSaveDialog(this);
				if (opcion == JFileChooser.APPROVE_OPTION) {
					archivo = guardado.getSelectedFile();
					if (!archivo.getName().contains(".")) {
						archivo = new File(archivo.getAbsolutePath() + ".asm");
					}
					frame.setTitle(archivo.getName());
					int idx = indexOfFrame(frame);
					if (idx >= 0) {
						this.editorTabs.setTitleAt(idx, archivo.getName());
					}
				}
				frame.setArchivoActual(archivo);
			}
			try {
				assert archivo != null;
				try (FileWriter escribir = new FileWriter(archivo)) {
					escribir.write(contenido);
					escribir.flush();
					showInfoDialog("Archivo guardado correctamente en: " + archivo.getAbsolutePath());
				}
			} catch (IOException e) {
				showErrorDialog("Error al guardar " + e.getMessage() + " .");
				e.printStackTrace();
			}
		} else {
			showWarnDialog("No hay ningún editor abierto.");
		}
	}

	public void saveAsCurrentFile() {
		AsmEditorInternalFrame frame = getActiveEditorFrame();

		if (frame != null) {
			String contenido = frame.asmGetEditorText();
			String titulo = frame.getTitle();

			// Siempre mostrar diálogo para elegir ubicación en "Guardar Como"
			App.configureFileChooserTheme();
			JFileChooser guardado = new JFileChooser();
			guardado.setDialogTitle("Guardar Como...");
			guardado.setSelectedFile(new File(titulo));
			colorizeFileChooserWindowAsync(guardado);
			int opcion = guardado.showSaveDialog(this);

			if (opcion == JFileChooser.APPROVE_OPTION) {
				File archivo = guardado.getSelectedFile();
				if (!archivo.getName().contains(".")) {
					archivo = new File(archivo.getAbsolutePath() + ".asm");
				}
				frame.setTitle(archivo.getName());
				int idx = indexOfFrame(frame);
				if (idx >= 0) {
					this.editorTabs.setTitleAt(idx, archivo.getName());
				}
				frame.setArchivoActual(archivo);

				try {
					try (FileWriter escribir = new FileWriter(archivo)) {
						escribir.write(contenido);
						escribir.flush();
						showInfoDialog("Archivo guardado correctamente en: " + archivo.getAbsolutePath());
					}
				} catch (IOException e) {
					showErrorDialog("Error al guardar " + e.getMessage() + " .");
					e.printStackTrace();
				}
			} else {
				showWarnDialog("Guardado cancelado.");
			}
		} else {
			showWarnDialog("No hay ningún editor abierto.");
		}
	}

	/**
	 * Muestra un diálogo para ir a una línea y posiciona el cursor en el editor activo. Valida el rango y realiza scroll hasta la línea indicada.
	 */
	private void goToLineInActiveEditor() {
		AsmEditorInternalFrame frame = getActiveEditorFrame();
		if (frame == null) {
			showWarnDialog("No hay ningún editor abierto.");
			return;
		}

		String input = javax.swing.JOptionPane.showInputDialog(this, "Número de línea:", "Ir a línea",
				javax.swing.JOptionPane.QUESTION_MESSAGE);
		if (input == null) {
			return; // cancelado
		}
		input = input.trim();
		if (input.isEmpty()) {
			showWarnDialog("Ingrese un número de línea válido.");
			return;
		}
		int lineNumber;
		try {
			lineNumber = Integer.parseInt(input);
		} catch (NumberFormatException ex) {
			showErrorDialog("El valor debe ser un número entero.");
			return;
		}

		javax.swing.text.StyledDocument doc = frame.getAsmEditorPane().getStyledDocument();
		javax.swing.text.Element root = doc.getDefaultRootElement();
		int maxLines = root.getElementCount();
		if (lineNumber < 1 || lineNumber > maxLines) {
			showWarnDialog("Línea fuera de rango. (1 - " + maxLines + ")");
			return;
		}

		javax.swing.text.Element lineElem = root.getElement(lineNumber - 1);
		int startOffset = lineElem.getStartOffset();
		frame.getAsmEditorPane().setCaretPosition(startOffset);
		try {
			java.awt.geom.Rectangle2D r2d = frame.getAsmEditorPane().modelToView2D(startOffset);
			if (r2d != null) {
				frame.getAsmEditorPane().scrollRectToVisible(r2d.getBounds());
			}
		} catch (javax.swing.text.BadLocationException ignored) {
			// ignorar si no se puede convertir
		}
		frame.getAsmEditorPane().requestFocusInWindow();
	}

	private void sendDataToMicroContoller() {

		byte[] data = getPreparedFrame();
		if (data == null || data.length == 0) {
			this.consolePanel.getOrderedHexResultTerminalPanel()
					.writteIn(">> Ha ocurrido un error: No existen datos a enviar");
			return;
		}

		int idx = this.cmbEnableComDevices.getSelectedIndex();
		if (idx < 0 || idx >= lastPorts.size()) {

			consolePanel.getOrderedHexResultTerminalPanel().writteIn(">> Selecciona un puerto primero\n");
			return;
		}

		var selected = lastPorts.get(idx);
		final int baud = 115200;

		new Thread(() -> {
			try {
				SerialPortService.sendBytes(selected.systemName(), baud, preparedFrame);
				consolePanel.getTerminalPanel().writteIn(">> Trama enviada a " + selected.systemName() + "\n");
			} catch (Exception ex) {
				consolePanel.getTerminalPanel()
						.writteIn(">> Error enviando a " + selected.systemName() + ": " + ex.getMessage() + "\n");
				ex.printStackTrace(System.err);
			}
		}, "uart-send-thread").start();

	}

	public List<AnalysisError> asmSemanticParse(List<Token> tokens) {

		List<AnalysisError> semanticErrors = List.of();
		try {

			AsmSemanticAnalyzer analizer = new AsmSemanticAnalyzer(tokens, this.consolePanel);
			this.isSemanticCorrect = analizer.analize();
			semanticErrors = analizer.getErrors();

			int idx = this.editorTabs.getSelectedIndex();
			if (idx >= 0) {
				if (this.isSemanticCorrect) {
					this.editorTabs.setForegroundAt(idx, new Color(229, 233, 240));
				} else {
					this.editorTabs.setForegroundAt(idx, new Color(255, 184, 108));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			this.consolePanel.getTerminalPanel().writteIn(">>> " + e.getMessage());
		}

		return semanticErrors;

	}

	private void showDisassemblyResult() {
		AsmEditorInternalFrame activeFrame = getActiveEditorFrame();
		if (activeFrame == null) {
			this.consolePanel.getDisassemblyTerminalPanel().writteIn(">>Error: No hay editor activo");
			return;
		}
		String currentEditorTittle = activeFrame.getTitle();
		this.consolePanel.getDisassemblyTerminalPanel().writteIn(">>Current code result: " + currentEditorTittle);

		if (this.tokens == null || this.tokens.isEmpty()) {
			this.consolePanel.getDisassemblyTerminalPanel().writteIn(">>Error: No Tokens");
			return;
		}

		// First Pass: Symbol table + IRLines
		AsmFirstPass firstPass = new AsmFirstPass(this.tokens, 0);
		AsmFirstPass.Result result = firstPass.run();

		/*
		 * String listing = AsmListingFormatter.buildListing(result.lines); this.consolePanel.getDisassemblyTerminalPanel().writteIn(listing);
		 */

		// Wroking on the second pass
		AsmSecondPass second = new AsmSecondPass(result.lines, result.symbols.asMap());
		AsmSecondPass.Result encRes = second.run();

		this.encodedIrLineResult = encRes.encoded();

		String listing = AsmListingFormatter.buildListing(encodedIrLineResult);
		this.consolePanel.getDisassemblyTerminalPanel().writteIn(listing);

	}

	public void preparedOrderedHex(List<EncoderIrLine> data) {
		if (data == null || data.isEmpty()) {
			this.consolePanel.getOrderedHexResultTerminalPanel().writteIn(
					">>Error: No existen datos en la tabla de resultados o existen errores de decodificación");
			return;
		}

		this.preparedFrame = FrameUtil.buildLittleEndianFrame(data);
		String orderedHex = FrameUtil.toHex(preparedFrame, false);
		// this.orderedHexCache = orderedHex;
		this.consolePanel.getOrderedHexResultTerminalPanel().writteIn(orderedHex);

	}

	public List<AnalysisError> asmSyntaxParse(List<Token> tokens) {

		List<AnalysisError> syntaxErrors = List.of();
		try {

			AsmParser parser = new AsmParser(tokens);
			parser.setResultConsolePanel(consolePanel);
			consolePanel.getTerminalPanel()
					.writteIn("\n================================================================\n");
			consolePanel.getTerminalPanel().writteIn("\n>>Iniciando Analisis\n");
			this.isSintaxCorrect = parser.parseProgram();
			syntaxErrors = parser.getErrors();
			consolePanel.getTerminalPanel().writteIn(">>Analisis Terminado\n");

			int idx = this.editorTabs.getSelectedIndex();
			if (idx >= 0) {
				if (this.isSintaxCorrect) {
					this.editorTabs.setForegroundAt(idx, new Color(229, 233, 240));
				} else {
					this.editorTabs.setForegroundAt(idx, new Color(255, 105, 97)); // rojo suave para sintaxis
				}
			}

		} catch (Exception er) {
			er.printStackTrace(System.err);
			this.consolePanel.getTerminalPanel().writteIn(">>> " + er.getMessage());
		}

		return syntaxErrors;

	}

	public List<Token> analizeCurrentLexer() {

		AsmLexer currentLexer = getCurrentLexer();
		List<Token> tokens = currentLexer.tokenize();
		return tokens;
	}

	private AsmLexer getCurrentLexer() {
		AsmEditorInternalFrame frame = getActiveEditorFrame();
		frame.setAsmLexer();
		return frame.getLexer();
	}

	private void undoActionInActiveEditor() {

		AsmEditorInternalFrame frame = getActiveEditorFrame();
		// AsmEditorPane activeAsmEditorPane = frame.getAsmEditorPane();

		consolePanel.getTerminalPanel()
				.writteIn("\n================================================================\n");
		frame.undo();
		this.consolePanel.getTerminalPanel().writteIn("Acción deshecha");

	}

	private void redoActionInActiveEditor() {

		AsmEditorInternalFrame frame = getActiveEditorFrame();
		consolePanel.getTerminalPanel()
				.writteIn("\n================================================================\n");
		frame.redo();
		consolePanel.getTerminalPanel().writteIn("Acción recuperada");

	}

	public void openNewAsmEditor() {

		try {

			// Open the editor wizard and the user fill the data
			NewEditorWizardDialog dialog = this.openEditorWizard(FileType.ASSEMBLY_FILE);

			// Check if the file that the user is attemping to create already exist in the
			// list of oppened editors, if exist, we set the
			// DilalogResult to ERROR...
			if (this.isEditorAlreadyOpenned(dialog.getFileModel().getName()))
				dialog.setDialogResult(DialogResult.ERROR);

			if (dialog.getDialogResult() == DialogResult.ABORT)
				showWarnDialog("Operacion abortada");

			if (dialog.getDialogResult() != DialogResult.OK)
				return;

			showInfoDialog("Result: " + dialog.getDialogResult() + "file: " + dialog.getFileModel().getName());

			AsmEditorInternalFrame asmInternalFrame = new AsmEditorInternalFrame();
			asmInternalFrame.setTitle(dialog.getFileModel().getName());
			asmInternalFrame.setVisible(true);
			asmInternalFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			this.editorTabs.addTab(dialog.getFileModel().getName(), asmInternalFrame);
			int idx = this.editorTabs.indexOfComponent(asmInternalFrame);
			this.editorTabs.setTabComponentAt(idx,
					createClosableTabComponent(dialog.getFileModel().getName(), asmInternalFrame));
			this.editorTabs.setSelectedComponent(asmInternalFrame);
			updateTabLabelColors();

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	private void openNewAsmEditor(String tittle, String content) throws Exception {
		AsmEditorInternalFrame asminternalFrame = new AsmEditorInternalFrame();
		asminternalFrame.setTitle(tittle);
		asminternalFrame.asmSetEditorText(content);
		asminternalFrame.setVisible(true);
		asminternalFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.editorTabs.addTab(tittle, asminternalFrame);
		int idx = this.editorTabs.indexOfComponent(asminternalFrame);
		this.editorTabs.setTabComponentAt(idx, createClosableTabComponent(tittle, asminternalFrame));
		this.editorTabs.setSelectedComponent(asminternalFrame);
		updateTabLabelColors();
	}

	private java.awt.Component createClosableTabComponent(String title, AsmEditorInternalFrame frame) {
		JPanel tab = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
		tab.setOpaque(false);
		JLabel lbl = new JLabel(title);
		// Color por defecto para pestañas no seleccionadas (cyan menos brillante)
		lbl.setForeground(new Color(0x00, 0xc8, 0xa8));
		tab.putClientProperty("tabLabel", lbl);
		javax.swing.JButton close = new javax.swing.JButton("✕");
		close.setFocusable(false);
		close.setBorderPainted(false);
		close.setContentAreaFilled(false);
		close.setForeground(new Color(200, 200, 205));
		close.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				close.setForeground(new Color(255, 184, 108));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				close.setForeground(new Color(200, 200, 205));
			}
		});
		close.addActionListener(ev -> {
			int i = editorTabs.indexOfComponent(frame);
			if (i >= 0) {
				editorTabs.removeTabAt(i);
				try {
					frame.dispose();
				} catch (Exception ignored) {
				}
			}
		});
		tab.add(lbl);
		tab.add(Box.createHorizontalStrut(6));
		tab.add(close);
		return tab;
	}

	private void updateTabLabelColors() {
		Color selected = new Color(0xff, 0xe6, 0x6d); // Amarillo activo
		Color cyanDim = new Color(0x00, 0xc8, 0xa8); // Cyan menos brillante para no seleccionadas
		int sel = editorTabs.getSelectedIndex();
		for (int i = 0; i < editorTabs.getTabCount(); i++) {
			java.awt.Component tabComp = editorTabs.getTabComponentAt(i);
			if (tabComp instanceof JPanel) {
				Object lblObj = ((JPanel) tabComp).getClientProperty("tabLabel");
				if (lblObj instanceof JLabel) {
					((JLabel) lblObj).setForeground(i == sel ? selected : cyanDim);
				}
			}
		}
	}

	private boolean isEditorAlreadyOpenned(String editorTittle) {

		for (int i = 0; i < this.editorTabs.getTabCount(); i++) {
			if (this.editorTabs.getTitleAt(i).equals(editorTittle)) {
				showErrorDialog("El archivo ya existe");
				return true;
			}
		}

		return false;

	}

	/** Ajusta la posición del divisor entre consola y explorador al redimensionar la ventana. */
	private void setConsoleDividerLocationEvent() {
		this.splitPaneEditorAndConsole.setDividerLocation(this.getHeight() - 410);
		// this.SplitPanePrincipal.setDividerLocation(/*700 - this.getWidth()*/ 0);
	}

	private AsmEditorInternalFrame getActiveEditorFrame() {
		var c = this.editorTabs.getSelectedComponent();
		if (c instanceof AsmEditorInternalFrame f) {
			return f;
		}
		return null;
	}

	private int indexOfFrame(AsmEditorInternalFrame frame) {
		for (int i = 0; i < editorTabs.getTabCount(); i++) {
			if (editorTabs.getComponentAt(i) == frame) {
				return i;
			}
		}
		return -1;
	}

	private NewEditorWizardDialog openEditorWizard(FileType fileType) throws Exception {

		NewEditorWizardDialog newEditorWizard = new NewEditorWizardDialog(fileType);
		newEditorWizard.setModal(true);
		newEditorWizard.setModalityType(ModalityType.APPLICATION_MODAL);
		newEditorWizard.setLocationRelativeTo(this);
		newEditorWizard.setVisible(true);
		return newEditorWizard;

	}

	public byte[] getPreparedFrame() {
		return this.preparedFrame;
	}

	private void openAsmFile() {

		StringBuilder sb = new StringBuilder();

		// Aplicar colores Andromeda a JFileChooser
		App.configureFileChooserTheme();
		JFileChooser fileChooser = new JFileChooser();

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Assembly", "asm");
		fileChooser.setFileFilter(filter);
		colorizeFileChooserWindowAsync(fileChooser);
		int returnVal = fileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			showInfoDialog("Fichero seleccionado: " + fileChooser.getSelectedFile().getName());

			File asmFile = fileChooser.getSelectedFile();

			try {

				FileReader fReader = new FileReader(asmFile);
				BufferedReader lector = new BufferedReader(fReader);
				String linea;

				while ((linea = lector.readLine()) != null) {
					sb.append(linea).append("\n");
				}

				AsmEditorInternalFrame asmInternalFrame = new AsmEditorInternalFrame();
				asmInternalFrame.setTitle(fileChooser.getSelectedFile().getName());
				// Establecer el archivo actual para que Ctrl+S guarde sin mostrar diálogo
				asmInternalFrame.setArchivoActual(asmFile);
				asmInternalFrame.setVisible(true);
				asmInternalFrame.asmSetEditorText(sb.toString());
				asmInternalFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				this.editorTabs.addTab(fileChooser.getSelectedFile().getName(), asmInternalFrame);
				int idx = this.editorTabs.indexOfComponent(asmInternalFrame);
				this.editorTabs.setTabComponentAt(idx,
						createClosableTabComponent(fileChooser.getSelectedFile().getName(), asmInternalFrame));
				this.editorTabs.setSelectedComponent(asmInternalFrame);
				updateTabLabelColors();

				lector.close();
				return;

			} catch (Exception e) {

				e.printStackTrace();

				showErrorDialog("Ha ocurrido un error: " + e.getMessage());
			}

		} else {
			// !JFileChooser.APPROVE_OPTION entonces...
			showWarnDialog("Acción cancelada");
		}

	}

	/**
	 * Aplica color a la barra de título del JFileChooser cuando su diálogo se crea (se ejecuta de forma diferida para que el dialog ya exista).
	 */
	private void colorizeFileChooserWindowAsync(JFileChooser chooser) {
		chooser.addHierarchyListener(new java.awt.event.HierarchyListener() {
			@Override
			public void hierarchyChanged(java.awt.event.HierarchyEvent e) {
				if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0
						&& chooser.isShowing()) {
					java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(chooser);
					if (w != null) {
						App.colorizeFileChooserTitleBar(w);
					}
					applyFileChooserToolbarAccent(chooser);
					chooser.removeHierarchyListener(this);
				}
			}
		});
	}

	/**
	 * Resalta los botones de la barra de vistas del JFileChooser con contorno cyan para que no queden grises.
	 */
	private void applyFileChooserToolbarAccent(java.awt.Container chooser) {
		Color accent = new Color(0x00, 0xe8, 0xc6);
		Color bg = new Color(0x0a, 0x0c, 0x12);

		applyToolbarAccentRecursive(chooser, accent, bg);
	}

	private void applyToolbarAccentRecursive(java.awt.Container container, Color accent, Color bg) {
		for (java.awt.Component child : container.getComponents()) {
			if (child instanceof javax.swing.JToolBar bar) {
				bar.setOpaque(true);
				bar.setBackground(bg);
				for (java.awt.Component btnComp : bar.getComponents()) {
					if (btnComp instanceof AbstractButton btn) {
						styleToolbarButton(btn, accent, bg);
					}
				}
			}
			if (child instanceof java.awt.Container nested) {
				applyToolbarAccentRecursive(nested, accent, bg);
			}
		}
	}

	private void styleToolbarButton(AbstractButton btn, Color accent, Color bg) {
		btn.setForeground(accent);
		btn.setBackground(new Color(0, 0, 0, 0)); // fully transparent
		btn.setOpaque(false);
		btn.setContentAreaFilled(false);
		btn.putClientProperty("JButton.buttonType", "toolBarButton");
		// FlatLaf specific: disable selected/hover backgrounds
		btn.putClientProperty("JButton.selectedBackground", new Color(0, 0, 0, 0));
		btn.putClientProperty("JButton.pressedBackground", new Color(0, 0, 0, 0));
		btn.putClientProperty("JButton.hoverBackground", new Color(0, 0, 0, 0));
		btn.putClientProperty("JToggleButton.selectedBackground", new Color(0, 0, 0, 0));
		btn.setBorderPainted(true);
		btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
		btn.setRolloverEnabled(true);
		btn.getModel().addChangeListener(ev -> updateToolbarButtonBorder(btn, accent, bg));
		// Initial state
		updateToolbarButtonBorder(btn, accent, bg);
	}

	private void updateToolbarButtonBorder(AbstractButton btn, Color accent, Color bg) {
		ButtonModel model = btn.getModel();
		boolean active = model.isRollover() || model.isPressed() || model.isSelected();
		// Always keep transparent
		btn.setBackground(new Color(0, 0, 0, 0));
		btn.setOpaque(false);
		btn.setContentAreaFilled(false);
		if (active) {
			btn.setBorder(new LineBorder(accent, 2, true));
		} else {
			btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
		}
		btn.repaint();
	}

	private void builExamplesMenu() {

		List<String> files = ExampleResources.listAsm();

		if (files.isEmpty()) {
			JMenuItem emptyMenu = new JMenuItem("(no hay ejemplos para mostar)");
			emptyMenu.setEnabled(false);
			this.jMenuItemEjemplos.add(emptyMenu);
			return;
		}

		for (String fileName : files) {

			JMenuItem menuItem = new JMenuItem(fileName);
			menuItem.addActionListener(ev -> {

				try {

					String content = ExampleResources.readAsm(fileName);
					openNewAsmEditor(fileName, content);

				} catch (Exception er) {
					er.printStackTrace();
				}

			});

			this.jMenuItemEjemplos.add(menuItem);
		}

	}

	private void openWifiWizard() {

		if (this.preparedFrame.length <= 0) {
			showErrorDialog("No hay datos para el envio");
			return;
		}

		FrWiFiWizarDialog wifiWizard = new FrWiFiWizarDialog(this.preparedFrame);
		wifiWizard.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		wifiWizard.setLocationRelativeTo(this);
		wifiWizard.setVisible(true);

	}

	private void copySelectedCodeToSystemClipboard() {

		AsmEditorInternalFrame currentFrame = getActiveEditorFrame();
		if (currentFrame == null)
			return;
		AsmEditorPane currentEditorPane = currentFrame.getAsmEditorPane();
		if (currentEditorPane == null)
			return;
		currentEditorPane.copy();

	}

	private void cutSelectedCodeToSystemClipboard() {

		AsmEditorInternalFrame currentFrame = getActiveEditorFrame();
		if (currentFrame == null)
			return;
		AsmEditorPane currentEditorPane = currentFrame.getAsmEditorPane();
		if (currentEditorPane == null)
			return;
		currentEditorPane.cut();

	}

	private void pasteCurrentContextOfTheClipboard() {

		AsmEditorInternalFrame currentFrame = getActiveEditorFrame();
		if (currentFrame == null)
			return;
		AsmEditorPane currentEditorPane = currentFrame.getAsmEditorPane();
		if (currentEditorPane == null)
			return;
		currentEditorPane.paste();

	}

	/**
	 * Actualiza la barra de estado inferior con el contador de errores y advertencias.
	 * 
	 * @param errorCount Número de errores encontrados
	 * @param warningCount Número de advertencias encontradas
	 */
	private void updateStatusBar(int errorCount, int warningCount) {
		if (statusBarLabel == null)
			return;

		if (errorCount == 0 && warningCount == 0) {
			statusBarLabel.setText("✓ Sin problemas");
			statusBarLabel.setForeground(new Color(0x00, 0xe8, 0xc6)); // Cyan
		} else {
			StringBuilder sb = new StringBuilder();
			if (errorCount > 0) {
				sb.append("✕ ").append(errorCount).append(" error").append(errorCount > 1 ? "es" : "");
			}
			if (warningCount > 0) {
				if (sb.length() > 0)
					sb.append("  ");
				sb.append("⚠ ").append(warningCount).append(" advertencia").append(warningCount > 1 ? "s" : "");
			}
			statusBarLabel.setText(sb.toString());
			statusBarLabel.setForeground(errorCount > 0 ? new Color(0xff, 0x69, 0x61) : new Color(0xff, 0xe6, 0x6d)); // Rojo o amarillo
		}
	}

	// Ajuste de tamaño de fuente se realiza solo via Ctrl + rueda en el editor
	
	private void openAboutForm() {
		
		JFrame parent = this;
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					FrAbout frame = new FrAbout();
					frame.setLocationRelativeTo(parent);
					frame.setVisible(true);
					
				}catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace(System.err);
				}
			}
		});
	}
	
	private void openLicenseInfoForm() {
		JFrame parent = this;
		
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					
					FrLicenseInfo frame = new FrLicenseInfo();
					frame.setLocationRelativeTo(parent);
					frame.setVisible(true);
					
				}catch (Exception e) {
					e.printStackTrace(System.err);
					System.err.println(e.getMessage());
				}
				
			}
		});
	}
}
