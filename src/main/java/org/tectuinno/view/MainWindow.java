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

/**
 * 
 * Hola lennin este es un comentario de prueba para que veas como 
 * se integran los cambios de forma remota
 * 
 * 
 */

package org.tectuinno.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.tectuinno.compiler.assembler.AsmFirstPass;
import org.tectuinno.compiler.assembler.AsmLexer;
import org.tectuinno.compiler.assembler.AsmParser;
import org.tectuinno.compiler.assembler.AsmSecondPass;
import org.tectuinno.compiler.assembler.AsmSemanticAnalyzer;
import org.tectuinno.compiler.assembler.EncoderIrLine;
import org.tectuinno.compiler.assembler.encode.FrameUtil;
import org.tectuinno.compiler.assembler.utils.AsmListingFormatter;
import org.tectuinno.compiler.assembler.utils.Token;
import org.tectuinno.io.PortInfo;
import org.tectuinno.io.SerialPortService;
import org.tectuinno.utils.DialogResult;
import org.tectuinno.utils.FileType;
import org.tectuinno.view.assembler.AsmEditorInternalFrame;
import org.tectuinno.view.assembler.AsmEditorPane;
import org.tectuinno.view.component.ResultConsolePanel;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog.ModalityType;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.sql.SQLOutput;
import java.util.List;

import java.awt.Dimension;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JMenuBar menuBar;
	private JMenu JMenuFile;
	private JMenu JMenuArchivoNuevo;
	private JMenuItem MenuItemNvoAsm;
	/*private JMenuItem MenuItemFicheroTexto;*/
	private JPanel panelToolBar;
	private JToolBar compilerToolBar;
	private JButton btnAnalice;
	private JMenuItem JMenuArchivoGuardar;
	private JSplitPane splitPaneEditorAndConsole;
	private JDesktopPane desktopPane;
	private ResultConsolePanel consolePanel;
	private boolean isSemanticCorrect;
	private boolean isSintaxCorrect;
	private JButton btnConvert;
	private List<Token> tokens;
	private byte[] preparedFrame = new byte[0];
	private List<EncoderIrLine> encodedIrLineResult;
	private final JComboBox<String> cmbEnableComDevices = new JComboBox<String>();
	/*private final JMenu JMenuEdit = new JMenu("Editar");
	private final JMenu JMenuProgram = new JMenu("Programa");
	private final JMenu JMenuTools = new JMenu("Herramientas");*/
	private final JButton btnSearchComDevices = new JButton("Escanear");
	private final JSeparator separator = new JSeparator();
	private List<PortInfo> lastPorts = List.of();

	/**
	 * Create the frame.
	 */
	public MainWindow() {

		this.isSemanticCorrect = false;
		this.isSintaxCorrect = false;

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setConsoleDividerLocationEvent();
			}
		});
		setTitle("Tectuinno IDE");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 864, 692);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenuFile = new JMenu("Archivo");
		menuBar.add(JMenuFile);

		JMenuArchivoNuevo = new JMenu("Nuevo");
		JMenuFile.add(JMenuArchivoNuevo);

		MenuItemNvoAsm = new JMenuItem("Fichero ASM Risc-V");
		MenuItemNvoAsm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openNewAsmEditor();
			}
		});
		JMenuArchivoNuevo.add(MenuItemNvoAsm);

		/*MenuItemFicheroTexto = new JMenuItem("Texto");
		JMenuArchivoNuevo.add(MenuItemFicheroTexto);*/

		JMenuArchivoGuardar = new JMenuItem("Guardar");
		JMenuArchivoGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guardarArchivo();
			}
		});
		JMenuFile.add(JMenuArchivoGuardar);



		/*{
			menuBar.add(JMenuEdit);
		}
		{
			menuBar.add(JMenuProgram);
		}
		{
			menuBar.add(JMenuTools);
		}*/
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		panelToolBar = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelToolBar.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(panelToolBar, BorderLayout.NORTH);

		compilerToolBar = new JToolBar();
		panelToolBar.add(compilerToolBar);

		{ /* Analice code button settings, events and configurations */

			btnAnalice = new JButton("Verificar");
			btnAnalice.setBackground(new Color(255, 255, 255));
			btnAnalice.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					tokens = analizeCurrentLexer();
                    if(tokens.isEmpty()){
                        JOptionPane.showMessageDialog(null,"No hay contenido que verificar.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

					new Thread() {

						@Override
						public void run() {
							consolePanel.getTokenTerminalPanel().writteIn("===========================================================\n");

							for (Token token : tokens) {
								consolePanel.getTokenTerminalPanel().writteIn(token.toString() + " \n");
							}

							consolePanel.getTokenTerminalPanel().writteIn("===========================================================\n");
						}

					}.start();

					new Thread() {
						@Override
						public void run() {

							asmSyntaxParse(tokens);

							if (!isSintaxCorrect) {
								btnConvert.setEnabled(false);
								JOptionPane.showMessageDialog(null, "Existen errores de sintaxis en el código", "Error",
										JOptionPane.ERROR_MESSAGE);
								return;
							}
							;

							asmSemanticParse(tokens);

							if (!isSemanticCorrect) {
								btnConvert.setEnabled(false);
								JOptionPane.showMessageDialog(null, "Existen errores de Semanticos en el código",
										"Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							;

							btnConvert.setEnabled(true);

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
			btnConvert.setBackground(new Color(255, 255, 255));
			compilerToolBar.add(btnConvert);

		}

		JButton btnEnviarLocal = new JButton("Enviar");
		btnEnviarLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendDataToMicroContoller();
			}
		});
		btnEnviarLocal.setBackground(new Color(255, 255, 255));
		compilerToolBar.add(btnEnviarLocal);
		{
			separator.setOrientation(SwingConstants.VERTICAL);
			compilerToolBar.add(separator);
		}
		{
			cmbEnableComDevices.setPreferredSize(new Dimension(190, 22));
			compilerToolBar.add(cmbEnableComDevices);
			//this.cmbEnableComDevices
		}
		{
			btnSearchComDevices.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					searchForComDevices();
				}
			});
			compilerToolBar.add(btnSearchComDevices);
		}

		splitPaneEditorAndConsole = new JSplitPane();
		splitPaneEditorAndConsole.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneEditorAndConsole.setDividerSize(5);
		splitPaneEditorAndConsole.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
		splitPaneEditorAndConsole.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		splitPaneEditorAndConsole.setContinuousLayout(false);
		contentPane.add(splitPaneEditorAndConsole, BorderLayout.CENTER);

		desktopPane = new JDesktopPane();
		splitPaneEditorAndConsole.setLeftComponent(desktopPane);

		consolePanel = new ResultConsolePanel();
		splitPaneEditorAndConsole.setRightComponent(consolePanel);
		splitPaneEditorAndConsole.setDividerLocation(410);
		
		this.searchForComDevices();

	}

	/*
	 * private boolean buildAll() {
	 * 
	 * List<Token> tokens = analizeCurrentLexer();
	 * 
	 * asmSyntaxParse(tokens);
	 * 
	 * if(!isSintaxCorrect) { JOptionPane.showMessageDialog(null,
	 * "Existen errores de sintaxis en el código", "Error",
	 * JOptionPane.ERROR_MESSAGE); return false; };
	 * 
	 * asmSemanticParse(tokens);
	 * 
	 * if(!isSemanticCorrect) { JOptionPane.showMessageDialog(null,
	 * "Existen errores de Semanticos en el código", "Error",
	 * JOptionPane.ERROR_MESSAGE); }; }
	 */
	
	
	private void searchForComDevices() {
		lastPorts = SerialPortService.listAvailablePorts();
		this.cmbEnableComDevices.removeAllItems();
		for(var pi : lastPorts) {
			this.cmbEnableComDevices.addItem(pi.toString());	
		}
	}
	
	private void guardarArchivo() {
        AsmEditorInternalFrame frame = (AsmEditorInternalFrame) this.desktopPane.getSelectedFrame();
        if (frame != null) {
            String contenido = frame.asmGetEditorText();
            String titulo = frame.getTitle();
            File archivo = new File(titulo);
            archivo = frame.getArchivoActual();
            if (archivo == null) {
                JFileChooser guardado = new JFileChooser();
                guardado.setDialogTitle("Guardar archivo");
                guardado.setSelectedFile(new File(titulo));
                int opcion = guardado.showSaveDialog(this);
                if (opcion == JFileChooser.APPROVE_OPTION) {
                    archivo = guardado.getSelectedFile();
                    if (!archivo.getName().contains(".")) {
                        archivo = new File(archivo.getAbsolutePath() + ".asm");
                    }
                    frame.setTitle(archivo.getName());
                }
                frame.setArchivoActual(archivo);
            }
            try (FileWriter escribir = new FileWriter(archivo)) {
                escribir.write(contenido);
                escribir.flush();
                JOptionPane.showMessageDialog(this, "Archivo guardado correctamente en: "
                        + archivo.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar " + e.getMessage() + " .");
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No hay ningún editor abierto.");
        }
    }

    private void sendDataToMicroContoller() {
		
		byte[] data = getPreparedFrame();
		if(data == null || data.length == 0) {
			this.consolePanel.getOrderedHexResultTerminalPanel().writteIn(">> Ha ocurrido un error: No existen datos a enviar");
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
	            consolePanel.getTerminalPanel().writteIn(">> Error enviando a " + selected.systemName() + ": " + ex.getMessage() + "\n");
	            ex.printStackTrace(System.err);
	        }
	    }, "uart-send-thread").start();
		
	}

	public void asmSemanticParse(List<Token> tokens) {

		try {

			AsmSemanticAnalyzer analizer = new AsmSemanticAnalyzer(tokens, this.consolePanel);
			this.isSemanticCorrect = analizer.analize();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			this.consolePanel.getTerminalPanel().writteIn(">>> " + e.getMessage());
		}

	}

	private void showDisassemblyResult() {

		String currentEditorTittle = this.desktopPane.getSelectedFrame().getTitle();
		this.consolePanel.getDisassemblyTerminalPanel().writteIn(">>Current code result: " + currentEditorTittle);

		if (this.tokens == null || this.tokens.isEmpty()) {
			this.consolePanel.getDisassemblyTerminalPanel().writteIn(">>Error: No Tokens");
			return;
		}

		// First Pass: Symbol table + IRLines
		AsmFirstPass firstPass = new AsmFirstPass(this.tokens, 0);
		AsmFirstPass.Result result = firstPass.run();

		/*
		 * String listing = AsmListingFormatter.buildListing(result.lines);
		 * this.consolePanel.getDisassemblyTerminalPanel().writteIn(listing);
		 */

		// Wroking on the second pass
		AsmSecondPass second = new AsmSecondPass(result.lines, result.symbols.asMap());
		AsmSecondPass.Result encRes = second.run();
		
		this.encodedIrLineResult = encRes.encoded();
		
		String listing = AsmListingFormatter.buildListing(encodedIrLineResult);
		this.consolePanel.getDisassemblyTerminalPanel().writteIn(listing);

	}
	
	public void preparedOrderedHex(List<EncoderIrLine> data) {
		
		if(data == null || data.isEmpty()) {
			this.consolePanel.getOrderedHexResultTerminalPanel().writteIn(">>Error: No existen datos en la tabla de resultados o existene errores de decodificación");
			return;
		}
		
		this.preparedFrame = FrameUtil.buildLittleEndianFrame(data);
		String orderedHex = FrameUtil.toHex(preparedFrame, false);
		this.consolePanel.getOrderedHexResultTerminalPanel().writteIn(orderedHex);
	}

	public void asmSyntaxParse(List<Token> tokens) {

		try {

			AsmParser parser = new AsmParser(tokens);
			parser.setResultConsolePanel(consolePanel);
			consolePanel.getTerminalPanel()
					.writteIn("\n================================================================\n");
			consolePanel.getTerminalPanel().writteIn("\n>>Iniciando Analisis\n");
			this.isSintaxCorrect = parser.parseProgram();
			consolePanel.getTerminalPanel().writteIn(">>Analisis Terminado\n");

		} catch (Exception er) {
			er.printStackTrace(System.err);
			this.consolePanel.getTerminalPanel().writteIn(">>> " + er.getMessage());
		}

	}

	public List<Token> analizeCurrentLexer() {

		AsmLexer currentLexer = getCurrentLexer();
		List<Token> tokens = currentLexer.tokenize();
		return tokens;
	}

	private AsmLexer getCurrentLexer() {
		AsmEditorInternalFrame frame = (AsmEditorInternalFrame) this.desktopPane.getSelectedFrame();
		frame.setAsmLexer();
		return frame.getLexer();
	}

	public void openNewAsmEditor() {

		try {

			NewEditorWizardDialog dialog = this.openEditorWizard(FileType.ASSEMBLY_FILE);

			if (dialog.getDialogResult() != DialogResult.OK) {
				JOptionPane.showMessageDialog(this, "Result: " + dialog.getDialogResult());
				return;
			}
			JOptionPane.showMessageDialog(this,
					"Result: " + dialog.getDialogResult() + "file: " + dialog.getFileModel().getName());

			JInternalFrame asmInternalFrame = new AsmEditorInternalFrame();
			asmInternalFrame.setTitle(dialog.getFileModel().getName());
			asmInternalFrame.setVisible(true);
			asmInternalFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			this.desktopPane.add(asmInternalFrame);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			return;
		}

	}


	private void openNewEditor() {

	}

	/**
	 * fix the position of the console and the file explorer before rezising the
	 * window
	 */
	private void setConsoleDividerLocationEvent() {
		this.splitPaneEditorAndConsole.setDividerLocation(this.getHeight() - 410);
		// this.SplitPanePrincipal.setDividerLocation(/*700 - this.getWidth()*/ 0);
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
}
