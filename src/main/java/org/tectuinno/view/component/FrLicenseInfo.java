package org.tectuinno.view.component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FrLicenseInfo extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea TxaInfoLicenseText;
	private JPanel PanelCentralText;
	private JScrollPane ScrollPaneLicenseText;
	private JPanel PanelDownButtons;
	private FlowLayout flowLayout;
	private JButton BtnExit;
	
	private static final String LICENSE_TEXT = """
			TECTUINNO IDE — Información de Licencia

			Este programa se distribuye como software libre bajo los términos de la
			GNU General Public License (GPL), versión 3, o (a tu elección) cualquier
			versión posterior.

			No hay garantía para este programa, en la medida permitida por la ley.
			El programa se distribuye con la esperanza de que sea útil, pero SIN
			NINGUNA GARANTÍA; sin siquiera la garantía implícita de COMERCIABILIDAD
			o APTITUD PARA UN PROPÓSITO PARTICULARsdfasdfasdf

			Texto completo de la licencia:
			- GNU GPL v3: https://www.gnu.org/licenses/gpl-3.0.html

			Excepción especial (enlace/uso como librería):
			Como excepción especial, puedes usar este archivo como parte de una librería
			libre sin restricción. Específicamente, si otros archivos instancian plantillas
			o usan macros o funciones inline de este archivo, o si compilas este archivo y
			lo enlazas con otros archivos para producir un ejecutable, este archivo por sí
			solo no provoca que el ejecutable resultante esté cubierto por la GNU GPL.
			Esta excepción no invalida otras razones por las cuales el ejecutable podría
			estar cubierto por la GNU GPL.

			Aviso de Copyright:
			Copyright 2025 Tectuinno Team (https://github.com/TectuinnoOF)

			Notas:
			- Si no recibiste una copia de la GNU GPL junto con este programa, consulta:
			  https://www.gnu.org/licenses/
			- Consulta el archivo 'license.txt' incluido en el repositorio para la copia
			  local de la licencia.

			Repositorio oficial del IDE:
			https://github.com/TectuinnoOF/Tectuinno-IDE
			""";

	/**
	 * Create the frame.
	 */
	public FrLicenseInfo() {
		setTitle("Licencia");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 468, 398);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		PanelCentralText = new JPanel();
		contentPane.add(PanelCentralText, BorderLayout.CENTER);
		PanelCentralText.setLayout(new BorderLayout(0, 0));
		
		ScrollPaneLicenseText = new JScrollPane();
		PanelCentralText.add(ScrollPaneLicenseText, BorderLayout.CENTER);
		
		TxaInfoLicenseText = new JTextArea();
		TxaInfoLicenseText.setEditable(false);
		this.TxaInfoLicenseText.setBackground(new Color(51, 51, 51));
		this.TxaInfoLicenseText.setForeground(new Color(0, 153, 0));
		this.TxaInfoLicenseText.setFont(new Font("Liberation Mono", Font.PLAIN, 13));
		ScrollPaneLicenseText.setViewportView(TxaInfoLicenseText);
		this.TxaInfoLicenseText.setText(LICENSE_TEXT);
		
		PanelDownButtons = new JPanel();
		flowLayout = (FlowLayout) PanelDownButtons.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		contentPane.add(PanelDownButtons, BorderLayout.SOUTH);
		
		BtnExit = new JButton("Salir");
		BtnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disposeWindow();
			}
		});
		PanelDownButtons.add(BtnExit);

		
	}
	
	/**
	 * Dispose the current window
	 *
	 */
	private void disposeWindow() {
		this.dispose();
	}

}
