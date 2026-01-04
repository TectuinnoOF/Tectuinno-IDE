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

package org.tectuinno.view.component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.tectuinno.io.WifiProgrammer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.function.IntConsumer;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;

/**
 * A Swing-based dialog (frame) that provides a user interface for programming
 * the Tectuinno device over WiFi.
 * <p>
 * This frame, called the <b>WiFi Programmer Wizard</b>, allows the user to:
 * <ul>
 * <li>Specify the host IP address and port of the Tectuinno device.</li>
 * <li>Test the network connection to ensure the device is reachable.</li>
 * <li>Send a prepared payload (compiled hexadecimal program) to the
 * device.</li>
 * <li>Monitor logs and transmission progress through a console and progress
 * bar.</li>
 * </ul>
 * </p>
 * <p>
 * Internally, the class uses {@link WifiProgrammer} to handle the network
 * operations (ping and send) while keeping the UI responsive through
 * {@link javax.swing.SwingWorker} tasks.
 * </p>
 *
 * <h3>UI Components</h3>
 * <ul>
 * <li><b>IP and Port Input:</b> Text field and spinner for connection
 * details.</li>
 * <li><b>Buttons:</b> Test connection, Send program, Exit.</li>
 * <li><b>Console Log:</b> Text area to display real-time status and
 * errors.</li>
 * <li><b>Progress Bar:</b> Shows the transmission progress of the payload.</li>
 * </ul>
 *
 * <h3>Typical Workflow</h3>
 * <ol>
 * <li>User connects manually to the "Tectuinno" WiFi network from their
 * OS.</li>
 * <li>User launches this dialog and verifies connectivity with <i>Test
 * Connection</i>.</li>
 * <li>Upon success, the <i>Send</i> button becomes available to transmit the
 * program.</li>
 * <li>Transmission progress is displayed, and logs are appended to the console
 * area.</li>
 * </ol>
 *
 * @see WifiProgrammer
 * @see javax.swing.SwingWorker
 * @author Tectuinno
 * @since 0.1.0
 */
public class FrWiFiWizarDialog extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/* ================================ */

	private final byte[] payload;
	private final WifiProgrammer programmer = new WifiProgrammer();
	private final JPanel JPanelSuperiorBotones = new JPanel();
	private final JLabel lblNewLabel = new JLabel("Host IP");
	private final JTextField txfIpHost = new JTextField();
	private final JLabel lblNewLabel_1 = new JLabel("Puerto");
	private final JSpinner jSpinSpPort = new JSpinner();
	private final JButton btnTestConnection = new JButton("Probar conexión");
	private final JButton btnSend = new JButton("Enviar");
	private final JButton btnClose = new JButton("Salir");
	private final JPanel panelCenterTerminal = new JPanel();
	private final JScrollPane scrollPaneTerminalContainer = new JScrollPane();
	private final JTextArea txaTerminalLog = new JTextArea();
	private final JProgressBar progressBar = new JProgressBar();

	/**
	 * Create the frame.
	 */
	public FrWiFiWizarDialog(byte[] payload) {
		setTitle("WiFi Programmer");
		txfIpHost.setText("192.168.4.1");
		txfIpHost.setColumns(10);

		this.payload = payload.clone();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 518, 388);

		// Paleta Andromeda
		Color andromedaBg = new Color(0x0c, 0x0e, 0x14);
		Color andromedaBg2 = new Color(0x0a, 0x0c, 0x12);
		Color andromedaText = new Color(0xd5, 0xce, 0xd9);
		Color yellowTitle = new Color(0xff, 0xe6, 0x6d);

		// Colorear barra de título
		getRootPane().putClientProperty("JRootPane.titleBarBackground", andromedaBg2);
		getRootPane().putClientProperty("JRootPane.titleBarForeground", yellowTitle);
		getRootPane().putClientProperty("JRootPane.titleBarInactiveBackground", andromedaBg);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(andromedaBg);
		contentPane.setForeground(andromedaText);
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		{
			JPanelSuperiorBotones.setBackground(andromedaBg);
			JPanelSuperiorBotones.setForeground(andromedaText);
			FlowLayout flowLayout = (FlowLayout) JPanelSuperiorBotones.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			contentPane.add(JPanelSuperiorBotones, BorderLayout.NORTH);
		}
		{
			lblNewLabel.setForeground(andromedaText);
			JPanelSuperiorBotones.add(lblNewLabel);
		}
		{
			txfIpHost.setBackground(andromedaBg2);
			txfIpHost.setForeground(andromedaText);
			JPanelSuperiorBotones.add(txfIpHost);
		}
		{
			lblNewLabel_1.setForeground(andromedaText);
			JPanelSuperiorBotones.add(lblNewLabel_1);
		}
		{
			jSpinSpPort.setModel(new SpinnerNumberModel(80, 1, 65535, 1));
			jSpinSpPort.setBackground(andromedaBg2);
			jSpinSpPort.setForeground(andromedaText);
			JPanelSuperiorBotones.add(jSpinSpPort);
		}
		{
			btnTestConnection.setBackground(andromedaBg2);
			btnTestConnection.setForeground(andromedaText);
			btnTestConnection.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					testConnection();
				}
			});
			JPanelSuperiorBotones.add(btnTestConnection);
		}
		{
			btnSend.setBackground(andromedaBg2);
			btnSend.setForeground(andromedaText);
			btnSend.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendProgram();
				}
			});
			JPanelSuperiorBotones.add(btnSend);
		}
		{
			btnClose.setBackground(andromedaBg2);
			btnClose.setForeground(andromedaText);
			btnClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			JPanelSuperiorBotones.add(btnClose);
		}
		{
			panelCenterTerminal.setBackground(andromedaBg);
			panelCenterTerminal.setForeground(andromedaText);
			contentPane.add(panelCenterTerminal, BorderLayout.CENTER);
		}
		panelCenterTerminal.setLayout(new BorderLayout(0, 0));
		{
			scrollPaneTerminalContainer.setBackground(andromedaBg2);
			scrollPaneTerminalContainer.getViewport().setBackground(andromedaBg2);
			panelCenterTerminal.add(scrollPaneTerminalContainer, BorderLayout.CENTER);
		}
		{
			txaTerminalLog.setEnabled(false);
			txaTerminalLog.setEditable(false);
			txaTerminalLog.setBackground(andromedaBg2);
			txaTerminalLog.setForeground(andromedaText);
			scrollPaneTerminalContainer.setViewportView(txaTerminalLog);
		}
		{
			progressBar.setBackground(andromedaBg2);
			progressBar.setForeground(new Color(0x00, 0xe8, 0xc6));
			panelCenterTerminal.add(progressBar, BorderLayout.NORTH);
		}

		this.consoleWritte("Trama preparada: " + this.payload.toString());
	}

	/**
	 * Tests the connectivity with the Tectuinno device over WiFi.
	 * <p>
	 * This method reads the host IP and port from the UI fields, then launches
	 * a {@link SwingWorker} in the background to avoid blocking the Event Dispatch
	 * Thread (EDT). The worker attempts to "ping" the device using
	 * {@link WifiProgrammer#ping(String, int, int)}.
	 * </p>
	 * <p>
	 * Behavior:
	 * <ul>
	 * <li>If the device responds with "OK", the console logs that the device
	 * is available and enables the <b>Send</b> button.</li>
	 * <li>If the connection fails or times out, an error is logged.</li>
	 * <li>The <b>Test Connection</b> button is re-enabled when the worker
	 * finishes.</li>
	 * </ul>
	 * </p>
	 */
	private void testConnection() {

		final String host = this.txfIpHost.getText();
		final int port = (Integer) this.jSpinSpPort.getValue();
		this.consoleWritte("Probando conexión a: " + host + ":" + port + "...");
		// btnTestConnection.setEnabled(false);
		// this.btnSend.setEnabled(false);

		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() {
				try {
					return programmer.ping(host, port, 4000);
				} catch (Exception e) {
					consoleWritte("Error: " + e.getMessage());
					return false;
				}
			}

			@Override
			protected void done() {
				try {
					boolean ok = get();
					if (ok) {
						consoleWritte("Ok: Dispositivo disponible");
						btnSend.setEnabled(true);
					} else {
						consoleWritte("Intento fallido: no hubo respuesta");
						btnSend.setEnabled(true);
					}
				} catch (Exception e) {
					consoleWritte("Error: " + e.getMessage());
				} finally {
					btnTestConnection.setEnabled(true);
				}
			}
		};

		worker.execute();

	}

	/**
	 * Sends the prepared program (payload) to the Tectuinno device over WiFi.
	 * <p>
	 * This method reads the host IP and port from the UI fields, then uses a
	 * {@link SwingWorker} to transmit the payload asynchronously.
	 * </p>
	 * <p>
	 * Behavior:
	 * <ul>
	 * <li>Resets the progress bar to 0%.</li>
	 * <li>Uses
	 * {@link WifiProgrammer#send(String, int, byte[], int, java.util.function.IntConsumer)}
	 * to transmit the payload in chunks, reporting progress back to the UI.</li>
	 * <li>Logs status messages to the console before, during, and after
	 * transmission.</li>
	 * <li>If transmission is successful, sets the progress bar to 100% and logs
	 * "programming completed".</li>
	 * <li>If an error occurs, the exception is logged and the buttons are
	 * re-enabled.</li>
	 * </ul>
	 * </p>
	 */
	private void sendProgram() {

		final String host = this.txfIpHost.getText();
		final int port = (Integer) this.jSpinSpPort.getValue();
		// btnTestConnection.setEnabled(false);
		// this.btnSend.setEnabled(false);

		this.consoleWritte("Enviando " + payload.length + " bytes a: " + host + ":" + port + "...");

		SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {

			@Override
			protected Void doInBackground() throws Exception {
				progressBar.setValue(0);
				try {

					IntConsumer pc = p -> publish(p);
					programmer.send(host, port, payload, 7000, pc);
					consoleWritte("Envio terminado. esperando confirmación");

				} catch (Exception e) {

					consoleWritte("Error durante envio: " + e.getMessage());
					throw new RuntimeException(e);

				}

				return null;
			}

			@Override
			protected void process(List<Integer> chunks) {
				if (!chunks.isEmpty()) {
					progressBar.setValue(chunks.get(chunks.size() - 1));
				}
			}

			@Override
			protected void done() {

				try {

					get();
					consoleWritte("Ok: programación completada");
					progressBar.setValue(100);

				} catch (Exception e) {

					consoleWritte("Error: " + e.getMessage());

				} finally {
					btnTestConnection.setEnabled(true);
					btnSend.setEnabled(true);
				}

			}
		};

		worker.execute();
	}

	/**
	 * Writes a message to the terminal log area in the UI.
	 * <p>
	 * Each message is prefixed with {@code ">> "} and appended to the text area,
	 * followed by a newline. The caret position is automatically moved to the end,
	 * ensuring that the latest message is always visible.
	 * </p>
	 *
	 * @param s the message to append to the console log
	 */
	private void consoleWritte(String s) {
		this.txaTerminalLog.append(">> " + s + "\n\r");
		this.txaTerminalLog.setCaretPosition(this.txaTerminalLog.getDocument().getLength());
	}
}
