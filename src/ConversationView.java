import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//import java.net.InetAddress;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;

public class ConversationView extends JFrame {
	
	private static final long serialVersionUID = 1L;
	/*** Attributes ***/
	protected Controller c;
	protected Conversation conv;
	
	private SpringLayout currentLayout;
	private JPanel pan;
	private JTextField textField;
	protected ArrayList<Message> msgList;
	protected String myself;
	protected String other;
	private JLabel lblLui;
	private JLabel titleLabel;
	private JPanel listPan;
	private JScrollPane listScroll;
	private BoxLayout listLayout;
		
	/*** Constructors ***/
	public ConversationView(Controller c) {
		this.c = c;
		msgList = new ArrayList<Message>();
		myself = new String();
		other = new String();

		// Components of my frame
		pan = new JPanel();
		currentLayout = new SpringLayout();
		listPan = new JPanel();
		listScroll = new JScrollPane(listPan);
		listLayout = new BoxLayout(listPan, BoxLayout.Y_AXIS);
		
		// Default things
        this.setSize(550, 550);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Methods that does everything
		//setUpFrame();
	}
	
	/*** Set up ***/
	private void setUpFrame() {
		// Set the pan and the layout
		this.setContentPane(pan);
		pan.setLayout(currentLayout);
		listPan.setLayout(listLayout);
		
		/* Colors */
		pan.setBackground(new Color(255, 212, 128));
		listScroll.setBackground(new Color(0, 0, 0));
		
		/* Scroll the list of users */
		listScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		currentLayout.putConstraint(SpringLayout.WEST, listScroll, 10, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.EAST, listScroll, -10, SpringLayout.EAST, pan);
		pan.add(listScroll);
		
		/* Text field for a new message */
		textField = new JTextField("");
		currentLayout.putConstraint(SpringLayout.SOUTH, listScroll, -6, SpringLayout.NORTH, textField);
		currentLayout.putConstraint(SpringLayout.WEST, textField, 0, SpringLayout.WEST, listScroll);
		currentLayout.putConstraint(SpringLayout.NORTH, textField, -82, SpringLayout.SOUTH, pan);
		currentLayout.putConstraint(SpringLayout.SOUTH, textField, -10, SpringLayout.SOUTH, pan);
		currentLayout.putConstraint(SpringLayout.EAST, textField, -91, SpringLayout.EAST, pan);
		textField.setColumns(10);
		pan.add(textField);
		
		lblLui = new JLabel("Talking to : " + other);
		currentLayout.putConstraint(SpringLayout.WEST, lblLui, 92, SpringLayout.WEST, pan);
		pan.add(lblLui);
		
		titleLabel = new JLabel("Chat room");
		currentLayout.putConstraint(SpringLayout.NORTH, titleLabel, 20, SpringLayout.NORTH, pan);
		currentLayout.putConstraint(SpringLayout.NORTH, lblLui, 4, SpringLayout.NORTH, titleLabel);
		currentLayout.putConstraint(SpringLayout.NORTH, listScroll, 18, SpringLayout.SOUTH, titleLabel);
		currentLayout.putConstraint(SpringLayout.EAST, titleLabel, -58, SpringLayout.EAST, pan);
		titleLabel.setFont(new Font("Lucida Grande", Font.BOLD, 17));
		pan.add(titleLabel);
		
		// Prints the current conversation
		f();
		
		
		/* The send button */
		JButton sendButton = new JButton("Send");
		currentLayout.putConstraint(SpringLayout.NORTH, sendButton, 19, SpringLayout.SOUTH, listScroll);
		currentLayout.putConstraint(SpringLayout.WEST, sendButton, 6, SpringLayout.EAST, textField);
		currentLayout.putConstraint(SpringLayout.SOUTH, sendButton, -20, SpringLayout.SOUTH, pan);
		sendButton.setBackground(new Color(0, 206, 209));
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Things to be done when the send button is clicked.
				String content = new String(textField.getText());
				if(content.equals("")) {
					// Nothing to do.
				}
				else {
					textField.setText("");
					c.sendMsg(other, content);
					DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm");
					String date = dateFormat.format(LocalDateTime.now());
					JLabel myMsg = new JLabel();
					myMsg.setForeground(new Color(0, 0, 0));
					myMsg.setText(date + " - " + myself + " : " + content);
					listPan.add(myMsg);
					listPan.revalidate();
					listPan.repaint();	
				}
			}});
		pan.add(sendButton);
		
		JButton backButton = new JButton("<<<");
		currentLayout.putConstraint(SpringLayout.WEST, backButton, 10, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.SOUTH, backButton, 0, SpringLayout.SOUTH, lblLui);
		currentLayout.putConstraint(SpringLayout.EAST, backButton, -25, SpringLayout.WEST, lblLui);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Things to be done when the button is clicked.
				c.displayHomeView();
				c.cv.setVisible(false);
			}});
		pan.add(backButton);
		}
	
	private void f() {
		for(Message msg : msgList) {
			JLabel newMsg = new JLabel();
			// If the message was sent by us ...
			if(msg.getSent() == true) {
				// Color of our message is black 
				newMsg.setForeground(new Color(0, 0, 0));
				newMsg.setText(msg.getDate() + " - " + myself + " : " + msg.getContent());
			}
			// If the message was sent by the other user ...
			else
			{
				// Color of our message is blue 
				newMsg.setForeground(new Color(0, 153, 153));
				newMsg.setText(msg.getDate() + " - " + other + " : " + msg.getContent());
			}
			listPan.add(newMsg);
			}
		
	}
	public void updatePseudo(String newPseudo) {
		other = newPseudo;
		lblLui.setText(other);
		JLabel updateMsg = new JLabel();
		updateMsg.setForeground(new Color(204, 0, 102));
		//updateMsg.setFont(Font.ITALIC);
		updateMsg.setText("Your intermediary changed his pseudo to :" + other + ".");
		listPan.add(updateMsg);
		listPan.revalidate();
		listPan.repaint();	
	}
	
	public void addMsg (String content)
	{
		JLabel newMsg = new JLabel();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm");
		String date = dateFormat.format(LocalDateTime.now());
		newMsg.setForeground(new Color(0, 153, 153));
		newMsg.setText(date + " - "+ other + " : " + content);
		listPan.add(newMsg);
		listPan.revalidate();
		listPan.repaint();
	}
	
	/*** Methods ***/
	public void displayView (String pseudo, Conversation c) {
		conv = c;
		myself = pseudo;
		other = conv.getDestinationUser().getPseudo();
		msgList = conv.getMessages();
		setUpFrame();
		this.setVisible(true);
	}

	/*
	public static void main(String[] args) {
		Controller c = null;
		ConversationView cv = new ConversationView(c);
		User user = new User("Thomas", null, 0);
		Conversation conv = new Conversation(user);
		ArrayList<Message> messages = new ArrayList<Message>();
		
		messages.add(new Message("12h03", "Bonjour.", true));
		messages.add(new Message("13h18", "Salut !", false));
		messages.add(new Message("13h19", "Plus d'une heure après la réponse ...", true));
		messages.add(new Message("13h22", "Désolé j'étais aux toilettes.", false));
		messages.add(new Message("13h23", "Ouais c'est ça ouais.", true));
		messages.add(new Message("13h23", "Des bises, à demain.", true));

		conv.setMessages(messages);
		cv.displayView("Laure", conv);
		cv.setVisible(true);
	}
	*/
}
