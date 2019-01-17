import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
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
	
	protected String myself;
    
    /* Constructors */
    public PseudoView(Controller c) {
        this.c = c;
		currentLayout = new SpringLayout();
		pan = new JPanel();
		errorLabel = new JLabel("");
		currentLayout.putConstraint(SpringLayout.SOUTH, errorLabel, -73, SpringLayout.SOUTH, pan);
		
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
		pan.setBackground(new Color(220, 220, 220));
		
		/* The title label : CHOOSE ... */
		JLabel pseudoLabel = new JLabel("CHOOSE YOUR PSEUDO :");
		currentLayout.putConstraint(SpringLayout.WEST, pseudoLabel, 25, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.EAST, pseudoLabel, -24, SpringLayout.EAST, pan);
		pseudoLabel.setForeground(new Color(0, 0, 0));
		pseudoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		pseudoLabel.setVerticalAlignment(SwingConstants.CENTER);
		pseudoLabel.setFont(new Font("Lucida Grande", Font.BOLD, 25));
		pan.add(pseudoLabel);
		
		/* The text field to enter new pseudo */
		pseudoField = new JTextField();
		currentLayout.putConstraint(SpringLayout.SOUTH, pseudoLabel, -28, SpringLayout.NORTH, pseudoField);
		currentLayout.putConstraint(SpringLayout.NORTH, errorLabel, 27, SpringLayout.SOUTH, pseudoField);
		currentLayout.putConstraint(SpringLayout.NORTH, pseudoField, 100, SpringLayout.NORTH, pan);
		currentLayout.putConstraint(SpringLayout.WEST, pseudoField, 10, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.SOUTH, pseudoField, -100, SpringLayout.SOUTH, pan);
		currentLayout.putConstraint(SpringLayout.EAST, pseudoField, -10, SpringLayout.EAST, pan);
		currentLayout.putConstraint(SpringLayout.WEST, errorLabel, 50, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.EAST, errorLabel, 200, SpringLayout.EAST, pan);
		currentLayout.putConstraint(SpringLayout.WEST, pseudoLabel, 12, SpringLayout.WEST, pseudoField);
		pseudoField.setFont(new Font("Lucida Grande", Font.ITALIC, 35));
		pseudoField.setText(myself);
		pan.add(pseudoField);
		pseudoField.setColumns(10);
		
		/* The check button */
		JButton checkButton = new JButton("Start chatting !");
		currentLayout.putConstraint(SpringLayout.NORTH, checkButton, 0, SpringLayout.NORTH, errorLabel);
		currentLayout.putConstraint(SpringLayout.WEST, checkButton, 119, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.EAST, checkButton, -121, SpringLayout.EAST, pan);
		checkButton.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		checkButton.setForeground(new Color(210, 105, 30));
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
    
    public void displayView(String pseudo) {
    		myself = pseudo;
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
