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

public class FrWiFiWizarDialog extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	/*================================*/

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
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		{
			FlowLayout flowLayout = (FlowLayout) JPanelSuperiorBotones.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			contentPane.add(JPanelSuperiorBotones, BorderLayout.NORTH);
		}
		{
			JPanelSuperiorBotones.add(lblNewLabel);
		}
		{
			JPanelSuperiorBotones.add(txfIpHost);
		}
		{
			JPanelSuperiorBotones.add(lblNewLabel_1);
		}
		{
			jSpinSpPort.setModel(new SpinnerNumberModel(3333, 1, 65535, 1));
			JPanelSuperiorBotones.add(jSpinSpPort);
		}
		{
			btnTestConnection.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					testConnection();
				}
			});
			JPanelSuperiorBotones.add(btnTestConnection);
		}
		{
			btnSend.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendProgram();
				}
			});
			JPanelSuperiorBotones.add(btnSend);
		}
		{
			btnClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			JPanelSuperiorBotones.add(btnClose);
		}
		{
			contentPane.add(panelCenterTerminal, BorderLayout.CENTER);
		}
		panelCenterTerminal.setLayout(new BorderLayout(0, 0));
		{
			panelCenterTerminal.add(scrollPaneTerminalContainer, BorderLayout.CENTER);
		}
		{
			txaTerminalLog.setEnabled(false);
			txaTerminalLog.setEditable(false);
			scrollPaneTerminalContainer.setViewportView(txaTerminalLog);
		}
		{
			panelCenterTerminal.add(progressBar, BorderLayout.NORTH);
		}
		
		this.consoleWritte("Trama preparada: " + this.payload.toString());
	}
	
	private void testConnection() {
		
		final String host = this.txfIpHost.getText();
		final int port = (Integer) this.jSpinSpPort.getValue();
		this.consoleWritte("Provado conexión a: " + host + ":" + port + "...");
		//btnTestConnection.setEnabled(false);
		//this.btnSend.setEnabled(false);
		
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>(){
			@Override protected Boolean doInBackground() {
				try {
					return programmer.ping(host, port, 4000);
				}catch (Exception e) {
					consoleWritte("Error: " + e.getMessage());
					return false;
				}
			}
			
			@Override protected void done() {
				try {
					boolean ok = get();
					if(ok) {
						consoleWritte("Ok: Dispositivo disponible");
						btnSend.setEnabled(true);
					}else {
						consoleWritte("Intento fallido: no hubo respuesta");
						btnSend.setEnabled(true);
					}
				}catch (Exception e) {
					consoleWritte("Error: " + e.getMessage());
				}finally {
					btnTestConnection.setEnabled(true);
				}
			}
		};
		
		worker.execute();
		
		
	}
	
	private void sendProgram() {
		
		final String host = this.txfIpHost.getText();
		final int port = (Integer) this.jSpinSpPort.getValue();
		//btnTestConnection.setEnabled(false);
		//this.btnSend.setEnabled(false);
		
		this.consoleWritte("Enviando " + payload.length + " bytes a: " + host + ":" + port + "...");
		
		SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
			
			@Override
			protected Void doInBackground() throws Exception {
				progressBar.setValue(0);
				try {
					
					IntConsumer pc = p -> publish(p);
					programmer.send(host, port, payload, 7000, pc);
					consoleWritte("Envio terminado. esperando confirmación");
					
				}catch (Exception e) {
					
					consoleWritte("Error durante envio: " + e.getMessage());
					throw new RuntimeException(e);
					
				}
				
				return null;
			}
			
			@Override protected void process(List<Integer> chunks) {
				if(!chunks.isEmpty()) {
					progressBar.setValue(chunks.get(chunks.size()-1));
				}
			}
			
			@Override protected void done() {
				
				try {
					
					get();
					consoleWritte("Ok: programación completada");
					progressBar.setValue(100);
					
				}catch (Exception e) {
					
					consoleWritte("Error: " + e.getMessage());
					
				}finally {
					btnTestConnection.setEnabled(true);
					btnSend.setEnabled(true);
				}
				
			}
		};
		
		worker.execute();
	}
	
	
	private void consoleWritte(String s) {
		this.txaTerminalLog.append(">> " + s + "\n\r");
		this.txaTerminalLog.setCaretPosition(this.txaTerminalLog.getDocument().getLength());
	}
}
