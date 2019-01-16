import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PseudoView extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;
	/* Attributes */
    protected Controller c;
	protected SpringLayout currentLayout;
	protected JPanel pan;
	private JTextField pseudoField;
	protected JLabel errorLabel;

    
    /* Constructors */
    public PseudoView(Controller c) {
        this.c = c;
        
		currentLayout = new SpringLayout();
		pan = new JPanel();
		errorLabel = new JLabel("");
		
		/* Size and of the frame */
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        /* Method that does everything */
		//setUpFrame();
    }
    
    private void setUpFrame () {
		this.setContentPane(pan);
		// Set the pan with its layout
		pan.setLayout(currentLayout);
		
		/* Background colors */
		pan.setBackground(new Color(222, 137, 211));
		
		/* The title label : CHOOSE ... */
		JLabel pseudoLabel = new JLabel("CHOOSE YOUR PSEUDO :");
		currentLayout.putConstraint(SpringLayout.WEST, pseudoLabel, 49, SpringLayout.WEST, pan);
		pseudoLabel.setFont(new Font("Lucida Grande", Font.BOLD, 25));
		pan.add(pseudoLabel);
		
		/* The text field to enter new pseudo */
		pseudoField = new JTextField();
		currentLayout.putConstraint(SpringLayout.NORTH, pseudoField, 100, SpringLayout.NORTH, pan);
		currentLayout.putConstraint(SpringLayout.WEST, pseudoField, 10, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.SOUTH, pseudoField, -100, SpringLayout.SOUTH, pan);
		currentLayout.putConstraint(SpringLayout.EAST, pseudoField, -10, SpringLayout.EAST, pan);
		currentLayout.putConstraint(SpringLayout.NORTH, errorLabel, 10, SpringLayout.SOUTH, pseudoField);
		currentLayout.putConstraint(SpringLayout.WEST, errorLabel, 50, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.EAST, errorLabel, 200, SpringLayout.EAST, pan);
		currentLayout.putConstraint(SpringLayout.SOUTH, pseudoLabel, -55, SpringLayout.NORTH, pseudoField);
		currentLayout.putConstraint(SpringLayout.WEST, pseudoLabel, 12, SpringLayout.WEST, pseudoField);
		currentLayout.putConstraint(SpringLayout.EAST, pseudoLabel, -12, SpringLayout.EAST, pseudoField);
		pseudoField.setFont(new Font("Lucida Grande", Font.ITALIC, 35));
		pseudoField.setText("");
		pan.add(pseudoField);
		pseudoField.setColumns(10);
		
		/* The check button */
		JButton checkButton = new JButton("CONNECT");
		currentLayout.putConstraint(SpringLayout.NORTH, checkButton, 45, SpringLayout.SOUTH, pseudoField);
		currentLayout.putConstraint(SpringLayout.EAST, checkButton, -160, SpringLayout.EAST, pan);
		checkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String pseudo = pseudoField.getText();
				if(!pseudo.equals(""))
				{
					c.setPseudo(pseudo);
				}
			}
		});
		pan.add(checkButton);
		
		/* Error display label */
		errorLabel.setForeground(new Color(139, 0, 0));
		errorLabel.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		currentLayout.putConstraint(SpringLayout.WEST, errorLabel, 169, SpringLayout.WEST, pan);
		pan.add(errorLabel);
    }
    
    public void displayView() {
    		setUpFrame();
    		pan.revalidate();
    		pan.repaint();
		this.setVisible(true);
    }
    
    public void printMsgError(){
        errorLabel.setText("This pseudo is not available.");
        //pseudoField.setText("Please try another one ...");
    }
    
/*
	// Main to test without the controller .
	public static void main(String[] args) {
		Controller c = null;
		PseudoView pv = new PseudoView(c);
		pv.displayView();
		pv.setVisible(true);
	}
*/ 
}
