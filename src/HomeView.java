
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
	
	private ArrayList<User >listUser;
	private String myself;
	private ArrayList<Conversation> history;
	
	private JButton btnPseudo;
	
	
	/*** Constructors ***/
	public HomeView(Controller c) {
		this.cont = c;
		listUser = new ArrayList<User>();
		// Components of my frame
		pan = new JPanel();
		homeLabel = new JLabel();
		welcomeLabel = new JLabel();
		currentLayout = new SpringLayout();
		currentLayout.putConstraint(SpringLayout.WEST, welcomeLabel, 19, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.SOUTH, welcomeLabel, -434, SpringLayout.SOUTH, pan);
		
		listPan = new JPanel();
		listScroll = new JScrollPane(listPan);
		currentLayout.putConstraint(SpringLayout.NORTH, listScroll, 18, SpringLayout.SOUTH, welcomeLabel);
		currentLayout.putConstraint(SpringLayout.EAST, listScroll, 263, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.WEST, listScroll, 0, SpringLayout.WEST, welcomeLabel);
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
		pan.setBackground(new Color(255, 212, 128));
		
		listPan.setBackground(new Color(204, 255, 255));
		histPan.setBackground(new Color(204, 255, 255));
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
		homeLabel.setFont(new Font("Lucida Grande", Font.BOLD, 17));
		currentLayout.putConstraint(SpringLayout.NORTH, homeLabel, 33, SpringLayout.NORTH, pan);
		currentLayout.putConstraint(SpringLayout.WEST, homeLabel, 39, SpringLayout.WEST, pan);
		pan.add(homeLabel);


		/* Welcome label */
		welcomeLabel.setText("Hi "+myself+". Currently connected users :");
		welcomeLabel.setFont(new Font("Iowan Old Style", Font.PLAIN, 13));
		pan.add(welcomeLabel);
		
		btnPseudo = new JButton("Change pseudo");
		currentLayout.putConstraint(SpringLayout.SOUTH, listScroll, -30, SpringLayout.NORTH, btnPseudo);
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
		currentLayout.putConstraint(SpringLayout.WEST, lblHistoryPrevious, 0, SpringLayout.WEST, histScroll);
		lblHistoryPrevious.setFont(new Font("Iowan Old Style", Font.PLAIN, 13));
		pan.add(lblHistoryPrevious);
		
		JButton btnDeleteHistory = new JButton("Delete history");
		currentLayout.putConstraint(SpringLayout.SOUTH, btnDeleteHistory, -25, SpringLayout.SOUTH, pan);
		currentLayout.putConstraint(SpringLayout.NORTH, btnPseudo, 0, SpringLayout.NORTH, btnDeleteHistory);
		currentLayout.putConstraint(SpringLayout.EAST, btnDeleteHistory, -85, SpringLayout.EAST, pan);
		btnDeleteHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Things to be done when the button is clicked.
				cont.deleteHistory();
				
				histPan.revalidate();
				histPan.repaint();
			}});
		pan.add(btnDeleteHistory);
		
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
							newButton.setText(c.getDestinationUser().getPseudo());
							newButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									// Things to be done when the button is clicked.
									String i = e.getActionCommand();
									showConv(i);
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
	
	public void removeUser(String pseudo) {
		for(User u : listUser) {
			if(u.getPseudo() == pseudo) {
				listUser.remove(u);
				for(User user : listUser) {
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
	}
	
	/* A new user is connected */
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
			if(user.getPseudo() != null)
			{
				listUser.add(user);
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
		
		convPan.setLayout(convLayout);
		convFrame.setContentPane(convPan);
		msgPan.setLayout(msgLayout);
		
		msgScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		msgScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		convLayout.putConstraint(SpringLayout.NORTH, msgScroll, 18, SpringLayout.SOUTH, pseudoLabel);
		convLayout.putConstraint(SpringLayout.EAST, msgScroll, 263, SpringLayout.WEST, convPan);
		convLayout.putConstraint(SpringLayout.WEST, msgScroll, 0, SpringLayout.WEST, pseudoLabel);
		convLayout.putConstraint(SpringLayout.NORTH, backButton, 20, SpringLayout.NORTH, msgScroll);

		convLayout.putConstraint(SpringLayout.NORTH, msgScroll, 18, SpringLayout.SOUTH, pseudoLabel);
		convLayout.putConstraint(SpringLayout.EAST, msgScroll, 263, SpringLayout.WEST, convPan);
		convLayout.putConstraint(SpringLayout.WEST, msgScroll, 0, SpringLayout.WEST, pseudoLabel);
		convPan.add(msgScroll);
		
		pseudoLabel.setText("Previous conv with : "+ pseudo+".");
		pseudoLabel.setFont(new Font("Lucida Grande", Font.BOLD, 17));
		convLayout.putConstraint(SpringLayout.NORTH, pseudoLabel, 33, SpringLayout.NORTH, convPan);
		convLayout.putConstraint(SpringLayout.WEST, pseudoLabel, 19, SpringLayout.WEST, convPan);
		convLayout.putConstraint(SpringLayout.SOUTH, pseudoLabel, -434, SpringLayout.SOUTH, convPan);
		convPan.add(pseudoLabel);
		
		convLayout.putConstraint(SpringLayout.SOUTH, msgScroll, -30, SpringLayout.NORTH, backButton);
		convLayout.putConstraint(SpringLayout.WEST, backButton, 71, SpringLayout.WEST, convPan);
		convLayout.putConstraint(SpringLayout.EAST, backButton, 190, SpringLayout.WEST, convPan);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Things to be done when the button is clicked.
				//HomeView hv = new HomeView(cont);
				//cont.hv = hv;
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

		convPan.revalidate();
		convPan.repaint();	
		
		convFrame.setVisible(true);
		cont.hv.setVisible(false);
	}
	
	public void displayView(String m, ArrayList<User> coUsers, ArrayList<Conversation> hist) {
		history = hist;
		listUser = coUsers;
		myself = m;
		setUpFrame();
		pan.revalidate();
		pan.repaint();

		this.setVisible(true);
	}
}
