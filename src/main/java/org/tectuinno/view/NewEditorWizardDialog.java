package org.tectuinno.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.tectuinno.utils.DialogResult;

import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.DropMode;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NewEditorWizardDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private DialogResult dialogResult;
	private JTextField txfFileName;

	/**
	 * Launch the application.
	 *
	public static void main(String[] args) {
		try {
			NewEditorWizardDialog dialog = new NewEditorWizardDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * Create the dialog.
	 */
	public NewEditorWizardDialog() {
		setResizable(false);
		
		setBounds(100, 100, 534, 244);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			Box verticalBox = Box.createVerticalBox();
			contentPanel.add(verticalBox, BorderLayout.NORTH);
			{
				JPanel panelTittleHeader = new JPanel();
				verticalBox.add(panelTittleHeader);
				{
					JLabel lblNewLabel_1 = new JLabel("Risc-V Assembly");
					lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 16));
					panelTittleHeader.add(lblNewLabel_1);
				}
			}
			{
				Component verticalStrut = Box.createVerticalStrut(20);
				verticalBox.add(verticalStrut);
			}
			{
				Box horizontalBox = Box.createHorizontalBox();
				verticalBox.add(horizontalBox);
				{
					JPanel panel = new JPanel();
					FlowLayout flowLayout = (FlowLayout) panel.getLayout();
					flowLayout.setAlignment(FlowLayout.LEFT);
					horizontalBox.add(panel);
					{
						JLabel lblNewLabel = new JLabel("Nombre");
						panel.add(lblNewLabel);
					}
					{
						Component horizontalStrut = Box.createHorizontalStrut(20);
						panel.add(horizontalStrut);
					}
					{
						txfFileName = new JTextField();
						panel.add(txfFileName);
						txfFileName.setColumns(45);
					}
				}
			}
		}
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						closingDialog();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		this.pack();
	}
	
	private void closingDialog() {
		this.dispose();
		this.dialogResult = DialogResult.ABORT;
	}
	
	public void setDialogResult(DialogResult result) {
		this.dialogResult = result;
	}
	
	public DialogResult getDialogResult() {
		return this.dialogResult;
	}
	

}
