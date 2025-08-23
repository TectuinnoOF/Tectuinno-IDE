package org.tectuinno.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.tectuinno.model.FileModel;
import org.tectuinno.utils.DialogResult;
import org.tectuinno.utils.FileType;

import javax.swing.Box;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NewEditorWizardDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private DialogResult dialogResult;
	private JTextField txfFileName;
	private FileModel fileModel;
	private Box verticalBox;
	private JPanel panelTittleHeader;
	private Component verticalStrut;
	private Box horizontalBox;
	private JPanel panel;
	private FlowLayout flowLayout;
	private JLabel lblNewLabel;
	private Component horizontalStrut;
	private JPanel buttonPane;
	private JButton okButton;
	private JButton cancelButton;
	private JLabel headerLabel;
	private FileType fileType;

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
	public NewEditorWizardDialog(FileType fileType) {
		
		this.fileType = fileType;
		
		setResizable(false);
		
		setBounds(100, 100, 534, 244);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			verticalBox = Box.createVerticalBox();
			contentPanel.add(verticalBox, BorderLayout.NORTH);
			{
				panelTittleHeader = new JPanel();
				verticalBox.add(panelTittleHeader);
				{
					headerLabel = new JLabel("Risc-V Assembly");
					headerLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
					panelTittleHeader.add(headerLabel);
				}
			}
			{
				verticalStrut = Box.createVerticalStrut(20);
				verticalBox.add(verticalStrut);
			}
			{
				horizontalBox = Box.createHorizontalBox();
				verticalBox.add(horizontalBox);
				{
					panel = new JPanel();
					flowLayout = (FlowLayout) panel.getLayout();
					flowLayout.setAlignment(FlowLayout.LEFT);
					horizontalBox.add(panel);
					{
						lblNewLabel = new JLabel("Nombre");
						panel.add(lblNewLabel);
					}
					{
						horizontalStrut = Box.createHorizontalStrut(20);
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
			buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						dialogResult = DialogResult.OK;
						setFileModel();
						dispose();
						
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
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
	
	
	public void setTittleHeader(FileType fileType) {
		
		switch (fileType){
		
		case ASSEMBLY_FILE: {
			this.headerLabel.setText("Risc-V Assembly");
			break;
		}
		case TEXT_FILE: {
			this.headerLabel.setText("Documento de texto");
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + fileType);
		}
		
	}
	
	private void closingDialog() {
		this.dialogResult = DialogResult.ABORT;
		this.dispose();
	}
	
	public void setDialogResult(DialogResult result) {
		this.dialogResult = result;
	}
	
	public DialogResult getDialogResult() {
		return this.dialogResult;
	}
	
	public void setFileModel() {
        // Need to idenitify the filetype here
        if(txfFileName.getText().contains(".asm")){
            this.fileModel = new FileModel(this.txfFileName.getText(), this.fileType);
        }
        else
            this.fileModel = new FileModel(this.txfFileName.getText() + ".asm", this.fileType);
	}
	
	public FileModel getFileModel() {
		return this.fileModel;
	}
	
}
