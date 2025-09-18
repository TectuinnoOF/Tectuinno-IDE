package org.tectuinno.view.component;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import org.tectuinno.io.WifiProgrammer;

public class FrWiFiWizarDialog extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	/*================================*/
	private final JTextField txtHost = new JTextField("192.168.4.1");
    private final JSpinner spPort = new JSpinner(new SpinnerNumberModel(3333, 1, 65535, 1));
    private final JButton btnTest = new JButton("Probar conexión");
    private final JButton btnSend = new JButton("Enviar programa");
    private final JButton btnClose = new JButton("Cerrar");
    private final JProgressBar progress = new JProgressBar(0, 100);
    private final JTextArea log = new JTextArea(10, 60);

    private final byte[] payload;
    private final WifiProgrammer programmer = new WifiProgrammer();
    
    

	/**
	 * Create the frame.
	 */
	public FrWiFiWizarDialog(byte[] payload) {
		
		this.payload = payload.clone();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		

	}

}
