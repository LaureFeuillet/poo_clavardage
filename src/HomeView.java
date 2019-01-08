
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import java.awt.Font;

public class HomeView extends JFrame {
	
	private static final long serialVersionUID = 1L;
	/*** Attributes ***/
	protected Controller c;
	private SpringLayout currentLayout;
	private BoxLayout listLayout;
	private JPanel pan;
	private JPanel listPan;
	private JScrollPane listScroll;
	private JLabel homeLabel;
	private JLabel welcomeLabel;
	
	private ArrayList<User >listUser;
	private String myself;
	private ArrayList<Conversation> history;
	
	private JLabel testLabel;

	
	/*** Constructors ***/
	public HomeView(Controller c) {
		this.c = c;
		listUser = new ArrayList<User>();
		// Components of my frame
		pan = new JPanel();
		homeLabel = new JLabel();
		welcomeLabel = new JLabel();
		currentLayout = new SpringLayout();
		
		listPan = new JPanel();
		listScroll = new JScrollPane(listPan);
		listLayout = new BoxLayout(listPan, BoxLayout.Y_AXIS);
		
		testLabel = new JLabel();
		
		currentLayout.putConstraint(SpringLayout.SOUTH, testLabel, -118, SpringLayout.SOUTH, pan);
		currentLayout.putConstraint(SpringLayout.EAST, testLabel, -138, SpringLayout.EAST, pan);
		
        this.setSize(550, 550);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//setUpFrame();
	}

	/*** Setup method ***/
	private void setUpFrame() {
		
		this.setContentPane(pan);
		// Set the pan with its layout
		pan.setLayout(currentLayout);
		listPan.setLayout(listLayout);
		
		/* Colors */
		pan.setBackground(new Color(255, 212, 128));
		listPan.setBackground(new Color(204, 255, 255));
		listScroll.setBackground(new Color(0, 0, 0));
		
		/* Scroll the list of users */
		currentLayout.putConstraint(SpringLayout.NORTH, listScroll, 137, SpringLayout.NORTH, pan);
		currentLayout.putConstraint(SpringLayout.WEST, listScroll, 97, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.SOUTH, listScroll, 0, SpringLayout.SOUTH, testLabel);
		currentLayout.putConstraint(SpringLayout.EAST, listScroll, 228, SpringLayout.WEST, pan);
		listScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pan.add(listScroll);

		
		/* Title label : Home */
		homeLabel.setText("H O M E");
		homeLabel.setFont(new Font("Lucida Grande", Font.BOLD, 17));
		currentLayout.putConstraint(SpringLayout.NORTH, homeLabel, 33, SpringLayout.NORTH, pan);
		currentLayout.putConstraint(SpringLayout.WEST, homeLabel, 39, SpringLayout.WEST, pan);
		pan.add(homeLabel);


		/* Welcome label */
		welcomeLabel.setText("Welcome "+myself+". Who do you want to talk to ?");
		welcomeLabel.setFont(new Font("Iowan Old Style", Font.BOLD | Font.ITALIC, 13));
		currentLayout.putConstraint(SpringLayout.NORTH, welcomeLabel, 19, SpringLayout.SOUTH, homeLabel);
		currentLayout.putConstraint(SpringLayout.WEST, welcomeLabel, 72, SpringLayout.WEST, pan);
		pan.add(welcomeLabel);

			
		testLabel.setText("Talking to ...");
		pan.add(testLabel);
		
		for(User user : listUser) {
			JButton newButton = new JButton();
			newButton.setText(user.getPseudo());
			newButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Things to be done when the button is clicked.
					String i = e.getActionCommand();
					//testLabel.setText("Talking to "+ i +".");
					c.displayConversation(i);
					c.hv.hide();
				}});
			listPan.add(newButton);
		}
		
	}
	
	/*** Other methods ***/
	public void displayView(String m, ArrayList<User> coUsers, ArrayList<Conversation> hist) {
		history = hist;
		listUser = coUsers;
		myself = m;
		setUpFrame();
		this.setVisible(true);
	}

	/*
	// Main to test without the controller .
	public static void main(String[] args) {
		Controller c = null;
		HomeView hv = new HomeView(c);
		hv.setVisible(true);

		hv.displayView("Laure",null);
	}
	*/
}
