package org.tectuinno.view.component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FrAbout extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	String aboutText = """
			===========================================
			            TECTUINNO IDE
			        Entorno de Desarrollo Oficial
			===========================================

			Versión: 1.2.1.1

			Desarrollado por:
			Tectuinno Team
			Sitio web oficial:
			https://tectuinno.org/

			-------------------------------------------
			   Instrucciones soportadas
			-------------------------------------------

			LW
			ADDI
			SLTI
			ORI
			ANDI
			SW
			ADD
			SUB
			SLT
			OR
			AND
			BEQ
			JAL
			JALR
			LUI
			------------------------------------------
			CALL
			RET

			-------------------------------------------
			   Repositorios Oficiales del Proyecto
			-------------------------------------------

			Repositorio del IDE:
			https://github.com/TectuinnoOF/Tectuinno-IDE

			Repositorio General de Tectuinno:
			https://github.com/TectuinnoOF
			""";

	/**
	 * Create the frame.
	 */
	public FrAbout() {
		setTitle("Acerca de");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 468, 398);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel PanelCentralText = new JPanel();
		contentPane.add(PanelCentralText, BorderLayout.CENTER);
		PanelCentralText.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPaneAboutText = new JScrollPane();
		PanelCentralText.add(scrollPaneAboutText, BorderLayout.CENTER);

		JTextArea TxaAboutInfoText = new JTextArea();
		TxaAboutInfoText.setBackground(new Color(51, 51, 51));
		TxaAboutInfoText.setForeground(new Color(0, 153, 0));
		TxaAboutInfoText.setFont(new Font("Liberation Mono", Font.PLAIN, 13));
		TxaAboutInfoText.setEnabled(true);
		TxaAboutInfoText.setEditable(false);
		scrollPaneAboutText.setViewportView(TxaAboutInfoText);
		TxaAboutInfoText.setText(aboutText);

		JPanel PanelDowButtons = new JPanel();
		FlowLayout fl_PanelDowButtons = (FlowLayout) PanelDowButtons.getLayout();
		fl_PanelDowButtons.setAlignment(FlowLayout.RIGHT);
		contentPane.add(PanelDowButtons, BorderLayout.SOUTH);

		JButton btn_exit = new JButton("Salir");
		btn_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				disposeWindow();
			}
		});
		PanelDowButtons.add(btn_exit);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private void disposeWindow() {
		this.dispose();
	}

}
