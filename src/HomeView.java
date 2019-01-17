
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
	protected Controller cont;
	
	private SpringLayout currentLayout;
	private BoxLayout listLayout;
	private BoxLayout histLayout;
	
	private JPanel pan;
	private JPanel listPan;
	private JPanel histPan;
	private JScrollPane listScroll;
	private JScrollPane histScroll;
	
	private JLabel homeLabel;
	private JLabel welcomeLabel;
	
	private ArrayList<User> listUser;
	private String myself;
	private ArrayList<Conversation> history;
	
	private JButton btnPseudo;
	private JLabel lblWelcomeMachin;
	
	
	/*** Constructors ***/
	public HomeView(Controller c, String myself, ArrayList<User> listUser, ArrayList<Conversation> history) {
		this.cont = c;
		this.listUser = listUser;
		this.myself = myself;
		this.history = history;
		// Components of my frame
		pan = new JPanel();
		homeLabel = new JLabel();
		welcomeLabel = new JLabel();
		currentLayout = new SpringLayout();
		currentLayout.putConstraint(SpringLayout.NORTH, homeLabel, 25, SpringLayout.NORTH, pan);
		currentLayout.putConstraint(SpringLayout.WEST, homeLabel, 34, SpringLayout.WEST, pan);
		
		listPan = new JPanel();
		listScroll = new JScrollPane(listPan);
		currentLayout.putConstraint(SpringLayout.NORTH, listScroll, 112, SpringLayout.NORTH, pan);
		currentLayout.putConstraint(SpringLayout.WEST, welcomeLabel, 0, SpringLayout.WEST, listScroll);
		currentLayout.putConstraint(SpringLayout.SOUTH, welcomeLabel, -6, SpringLayout.NORTH, listScroll);
		currentLayout.putConstraint(SpringLayout.WEST, listScroll, 19, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.EAST, listScroll, 263, SpringLayout.WEST, pan);
		listLayout = new BoxLayout(listPan, BoxLayout.Y_AXIS);
		
		histPan = new JPanel();
		histScroll = new JScrollPane(histPan);
		currentLayout.putConstraint(SpringLayout.NORTH, histScroll, 112, SpringLayout.NORTH, pan);
		currentLayout.putConstraint(SpringLayout.WEST, histScroll, 22, SpringLayout.EAST, listScroll);
		currentLayout.putConstraint(SpringLayout.SOUTH, histScroll, -84, SpringLayout.SOUTH, pan);
		currentLayout.putConstraint(SpringLayout.EAST, histScroll, -21, SpringLayout.EAST, pan);
		histLayout = new BoxLayout(histPan, BoxLayout.Y_AXIS);
		
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
		histPan.setLayout(histLayout);
		
		/* Colors */
		pan.setBackground(new Color(220, 220, 220));
		
		listPan.setBackground(new Color(224, 255, 255));
		histPan.setBackground(new Color(230, 230, 250));
		listScroll.setBackground(new Color(0, 0, 0));
		histScroll.setBackground(new Color(0, 0, 0));

		// Scrolling policies
		listScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		histScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		histScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		pan.add(listScroll);
		pan.add(histScroll);

		
		/* Title label : Home */
		homeLabel.setText("H O M E");
		homeLabel.setFont(new Font("Lucida Grande", Font.BOLD, 20));
		pan.add(homeLabel);


		/* Welcome label */
		welcomeLabel.setText("Currently connected users :");
		welcomeLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		pan.add(welcomeLabel);
		
		btnPseudo = new JButton("Change pseudo");
		btnPseudo.setBackground(new Color(255, 255, 255));
		currentLayout.putConstraint(SpringLayout.SOUTH, btnPseudo, -25, SpringLayout.SOUTH, pan);
		btnPseudo.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		currentLayout.putConstraint(SpringLayout.SOUTH, listScroll, -30, SpringLayout.NORTH, btnPseudo);
		btnPseudo.setForeground(new Color(255, 140, 0));
		currentLayout.putConstraint(SpringLayout.WEST, btnPseudo, 71, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.EAST, btnPseudo, 212, SpringLayout.WEST, pan);
		btnPseudo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Things to be done when the button is clicked.
				PseudoView pv = new PseudoView(cont);
				cont.pv = pv;
				cont.hv.setVisible(false);
				cont.displayPseudoView();
			}});
		pan.add(btnPseudo);
		
		JLabel lblHistoryPrevious = new JLabel("History of previous conversations :");
		currentLayout.putConstraint(SpringLayout.NORTH, lblHistoryPrevious, 0, SpringLayout.NORTH, welcomeLabel);
		currentLayout.putConstraint(SpringLayout.EAST, lblHistoryPrevious, 0, SpringLayout.EAST, histScroll);
		lblHistoryPrevious.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		pan.add(lblHistoryPrevious);
		
		JButton btnDeleteHistory = new JButton("Delete history");
		currentLayout.putConstraint(SpringLayout.NORTH, btnDeleteHistory, 0, SpringLayout.NORTH, btnPseudo);
		currentLayout.putConstraint(SpringLayout.EAST, btnDeleteHistory, -70, SpringLayout.EAST, pan);
		btnDeleteHistory.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		btnDeleteHistory.setForeground(new Color(128, 0, 0));
		btnDeleteHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Things to be done when the button is clicked.
				cont.deleteHistory();
				histPan.removeAll();
				
				histPan.revalidate();
				histPan.repaint();
			}});
		pan.add(btnDeleteHistory);
		
		lblWelcomeMachin = new JLabel("Welcome "+ myself +" !");
		currentLayout.putConstraint(SpringLayout.WEST, lblWelcomeMachin, 78, SpringLayout.EAST, homeLabel);
		currentLayout.putConstraint(SpringLayout.SOUTH, lblWelcomeMachin, -18, SpringLayout.NORTH, welcomeLabel);
		lblWelcomeMachin.setFont(new Font("Lucida Grande", Font.BOLD, 17));
		pan.add(lblWelcomeMachin);
		
		// Creation of the list of connected users
		if(listUser != null) {
			for(User user : listUser) {
				if (!user.getPseudo().equals("undefined")) {
					JButton newButton = new JButton();
					newButton.setText(user.getPseudo());
					newButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// Things to be done when the button is clicked.
							String i = e.getActionCommand();
							//testLabel.setText("Talking to "+ i +".");
							cont.displayConversation(i);
							cont.hv.setVisible(false);
						}});
					listPan.add(newButton);
					listPan.revalidate();
					listPan.repaint();	
				}
			}
		}
		// Creation of the history
		if(history != null) {
			for(Conversation c : history) {
				if (!c.getDestinationUser().getPseudo().equals("undefined")) {
					JButton newButton = new JButton();
					newButton.setText(c.getDestinationUser().getPseudo() + " : " + c.getStartingDate());
					newButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// Things to be done when the button is clicked.
							//String i = e.getActionCommand();
							showConv(c.getDestinationUser().getPseudo());
						}});
					histPan.add(newButton);
					histPan.revalidate();
					histPan.repaint();	
				}
			}
		}
		pan.revalidate();
		pan.repaint();
		histPan.revalidate();
		histPan.repaint();
	}
	
	public void refreshView() {
		System.out.println("[DEBUG] Refreshed homeView.");
		//listUser.remove(u);
		listPan.removeAll();

		listPan.revalidate();
		listPan.repaint();
		for(User user : listUser) {
			if (!user.getPseudo().equals("undefined")) {
				JButton newButton = new JButton();
				newButton.setText(user.getPseudo());
				newButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Things to be done when the button is clicked.
						String i = e.getActionCommand();
						//testLabel.setText("Talking to "+ i +".");
						cont.displayConversation(i);
						cont.hv.setVisible(false);
					}});
				listPan.add(newButton);
				listPan.revalidate();
				listPan.repaint();	
			}	
		}
	}
	
	/* A new user is connected */
	/*
	public void addUser(User user)
	{
		boolean exists = false;
		for(User u : listUser) {
			if((u.getAddress() == user.getAddress()) && (u.getNumPort() == user.getNumPort())) {
				exists = true;
				u.setPseudo(user.getPseudo());
				listPan.revalidate();
				listPan.repaint();	
				break;
			}
		}
		if(!exists) {
			if(!user.getPseudo().equals("undefined"))
			{
				//listUser.add(user);
				JButton newButton = new JButton();
				newButton.setText(user.getPseudo());
				newButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Things to be done when the button is clicked.
						String i = e.getActionCommand();
						cont.displayConversation(i);
						cont.hv.setVisible(false);
					}});
				listPan.add(newButton);
				listPan.revalidate();
				listPan.repaint();	
			}
		}

	}
	*/
	
	/*** Other methods ***/
	public void showConv(String pseudo) {
		// All thats needed to show a conversation from history
		JFrame convFrame = new JFrame();
		JPanel convPan = new JPanel();
		JPanel msgPan = new JPanel();
		JScrollPane msgScroll = new JScrollPane(msgPan);
		SpringLayout convLayout = new SpringLayout();
		BoxLayout msgLayout = new BoxLayout(msgPan, BoxLayout.Y_AXIS);
		JLabel pseudoLabel = new JLabel();
		JButton backButton = new JButton("Back to home");

        convFrame.setSize(550, 550);
        convFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		convFrame.setContentPane(convPan);
		convPan.setLayout(convLayout);
		msgPan.setLayout(msgLayout);
		
		convPan.setBackground(new Color(255, 212, 128));

		msgScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		msgScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


		convLayout.putConstraint(SpringLayout.NORTH, pseudoLabel, 20, SpringLayout.NORTH, convPan);
		convLayout.putConstraint(SpringLayout.WEST, pseudoLabel, 33, SpringLayout.WEST, convPan);
		convLayout.putConstraint(SpringLayout.SOUTH, pseudoLabel, -434, SpringLayout.SOUTH, convPan);

		convLayout.putConstraint(SpringLayout.NORTH, msgScroll, 18, SpringLayout.SOUTH, pseudoLabel);
		convLayout.putConstraint(SpringLayout.EAST, msgScroll, -30, SpringLayout.EAST, convPan);
		convLayout.putConstraint(SpringLayout.WEST, msgScroll, 0, SpringLayout.WEST, pseudoLabel);
		convLayout.putConstraint(SpringLayout.SOUTH, msgScroll, -60, SpringLayout.SOUTH, convPan);

		convPan.add(pseudoLabel);

		convLayout.putConstraint(SpringLayout.WEST, backButton, 33, SpringLayout.WEST, convPan);
		convLayout.putConstraint(SpringLayout.NORTH, backButton, 15, SpringLayout.SOUTH, msgScroll);
		
		pseudoLabel.setText("Previous conv with : "+ pseudo+".");
		pseudoLabel.setFont(new Font("Lucida Grande", Font.BOLD, 17));
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Things to be done when the button is clicked.
				convFrame.setVisible(false);
				cont.hv.setVisible(true);
			}});
		convPan.add(backButton);
		
		//User user = cont.um.getUserByPseudo(pseudo);
		ArrayList<Message> listMsg = cont.cm.getConvFromHistory(pseudo).getMessages();
		if(listMsg != null) {
			for(Message msg : listMsg) {
				JLabel newMsg = new JLabel();
				if(msg.getSent() == true) {
					newMsg.setForeground(new Color(0, 0, 0));
					newMsg.setText(msg.getDate() + " - " + myself + " : " + msg.getContent());
				}
				else{
					newMsg.setForeground(new Color(0, 153, 153));
					newMsg.setText(msg.getDate() + " - " + pseudo + " : " + msg.getContent());
				}
				msgPan.add(newMsg);
			}
			msgPan.revalidate();
			msgPan.repaint();
		}
		convPan.add(msgScroll);
		convPan.revalidate();
		convPan.repaint();	
		
		convFrame.setVisible(true);
		cont.hv.setVisible(false);
	}
	
	public void displayView() {
		setUpFrame();
		pan.revalidate();
		pan.repaint();

		this.setVisible(true);
	}
}
