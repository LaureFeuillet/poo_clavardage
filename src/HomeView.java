import java.awt.Color;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.JList;

public class HomeView extends JFrame {
	
	private static final long serialVersionUID = 1L;
	/*** Attributes ***/
	protected Controller c;
	private SpringLayout currentLayout;
	private JPanel pan;
	private JLabel homeLabel;
	private JLabel welcomeSentence;

	
	/*** Constructors ***/
	public HomeView(Controller c) {
		this.c = c;
		
		// Components of my frame
		pan = new JPanel();
		homeLabel = new JLabel();
		welcomeSentence = new JLabel();
		currentLayout = new SpringLayout();
		
		// Default things
        this.setSize(300, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Methods that does everything
		setUpFrame();
	}

	/*** Setup method ***/
	private void setUpFrame() {
		// Set the pan and the layout
		this.setContentPane(pan);
		pan.setLayout(currentLayout);
		
		// Title label : Home
		homeLabel.setText("H O M E");
		currentLayout.putConstraint(SpringLayout.NORTH, homeLabel, 33, SpringLayout.NORTH, pan);
		currentLayout.putConstraint(SpringLayout.WEST, homeLabel, 39, SpringLayout.WEST, pan);
		
		// List of users
		DefaultListModel<String> dlf = new DefaultListModel<String>();
		dlf.addElement("Laure");
		dlf.addElement("Thomas");
		dlf.addElement("Maël");
		JList<String> list = new JList<String>(dlf);
		// list.addListSelectionListener(listener); ??????? Possible ?
		currentLayout.putConstraint(SpringLayout.NORTH, list, 219, SpringLayout.SOUTH, pan);
		currentLayout.putConstraint(SpringLayout.WEST, list, 164, SpringLayout.WEST, getContentPane());
		currentLayout.putConstraint(SpringLayout.SOUTH, list, -148, SpringLayout.SOUTH, getContentPane());
		currentLayout.putConstraint(SpringLayout.EAST, list, 686, SpringLayout.WEST, pan);
		getContentPane().add(list);

		pan.add(homeLabel);
		pan.add(list);
		//pan.setBackground(new Color(200, 200, 200));
		
	}
	
	/*** Other methods ***/
	public void displayView(String myself, ArrayList<User> coUsers) {
		// Sets the welcome phrase.
		welcomeSentence.setText("Welcome "+myself+". Who do you want to talk to ?");
		
	}
	public static void main(String[] args) {
		Controller c = null;
		HomeView hv = new HomeView(c);
		hv.setVisible(true);
	}
}
