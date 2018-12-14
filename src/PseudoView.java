import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PseudoView implements ActionListener {
    JFrame pseudoViewFrame; // convertFrame
    JPanel pseudoViewPanel; // convertPanel
    JTextField insertPseudo; //tempCelsius
    JLabel pseudoLabel; //celsiusLabel
    JLabel checkLabel; // fahrenheitLabel
    JButton chooseButton; // convertTemp

    public PseudoView() {
        //Create and set up the window.
    		pseudoViewFrame = new JFrame("Choose your pseudo.");
    		pseudoViewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		pseudoViewFrame.setSize(new Dimension(400, 300));

        //Create and set up the panel.
    		pseudoViewPanel = new JPanel(new GridLayout(3, 3));

        //Add the widgets.
        addWidgets();

        //Set the checking button.
        pseudoViewFrame.getRootPane().setDefaultButton(chooseButton);

        //Add the panel to the window.
        pseudoViewFrame.getContentPane().add(pseudoViewPanel, BorderLayout.CENTER);

        //Display the window.
        pseudoViewFrame.pack();
        pseudoViewFrame.setVisible(true);
    }

    /**
     * Create and add the widgets.
     */
    private void addWidgets() {
        //Create widgets.
        insertPseudo = new JTextField(2);
        pseudoLabel = new JLabel("New pseudo : ", SwingConstants.LEFT);
        chooseButton = new JButton("Choose this one.");
        checkLabel = new JLabel("Result : ", SwingConstants.LEFT);

        //Listen to events from the choosing button.
        chooseButton.addActionListener(this);

        //Add the widgets to the container.
        pseudoViewPanel.add(insertPseudo);
        pseudoViewPanel.add(pseudoLabel);
        pseudoViewPanel.add(chooseButton);
        
        pseudoLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        checkLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

    }

    public void actionPerformed(ActionEvent event) {
        String newPseudo = insertPseudo.getText();
        // HERE ADD CONDITIONS ON THE PSEUDO .
        
        pseudoViewPanel.add(checkLabel);
        checkLabel.setText(newPseudo + " is valid.");
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        PseudoView view = new PseudoView();
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}