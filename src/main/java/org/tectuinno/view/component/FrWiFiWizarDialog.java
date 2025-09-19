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
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;

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
			JPanelSuperiorBotones.add(btnTestConnection);
		}
		{
			JPanelSuperiorBotones.add(btnSend);
		}
		{
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
		
		
	}

}
