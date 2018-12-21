import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
//import java.net.InetAddress;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;

public class ConversationView extends JFrame {
	
	private static final long serialVersionUID = 1L;
	/*** Attributes ***/
	protected Controller c;
	private SpringLayout currentLayout;
	private JPanel pan;
	private JTextField textField;
	protected ArrayList<Message> msgList;
	protected String myself;
	protected String other;
	private JLabel lblMoi;
	private JLabel lblLui;
	private JLabel titleLabel;
	
	/*** Constructors ***/
	public ConversationView(Controller c) {
		this.c = c;
		msgList = new ArrayList<Message>();
		myself = new String();
		other = new String();

		// Components of my frame
		pan = new JPanel();
		currentLayout = new SpringLayout();
		
		// Default things
        this.setSize(550, 550);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Methods that does everything
		setUpFrame();
	}
	
	/*** Set up ***/
	private void setUpFrame() {
		// Set the pan and the layout
		this.setContentPane(pan);
		pan.setLayout(currentLayout);
		
		/* Text field for a new message */
		textField = new JTextField();
		currentLayout.putConstraint(SpringLayout.NORTH, textField, -82, SpringLayout.SOUTH, pan);
		currentLayout.putConstraint(SpringLayout.WEST, textField, 10, SpringLayout.WEST, pan);
		currentLayout.putConstraint(SpringLayout.SOUTH, textField, -10, SpringLayout.SOUTH, pan);
		currentLayout.putConstraint(SpringLayout.EAST, textField, -91, SpringLayout.EAST, pan);
		textField.setColumns(10);
		pan.add(textField);
		
		/* The send button */
		JButton sendButton = new JButton("Send");
		currentLayout.putConstraint(SpringLayout.NORTH, sendButton, 13, SpringLayout.NORTH, textField);
		currentLayout.putConstraint(SpringLayout.WEST, sendButton, 6, SpringLayout.EAST, textField);
		currentLayout.putConstraint(SpringLayout.SOUTH, sendButton, -20, SpringLayout.SOUTH, pan);
		sendButton.setBackground(new Color(0, 206, 209));
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Things to be done when the button is clicked.
			}});
		pan.add(sendButton);
		
		lblMoi = new JLabel(myself);
		currentLayout.putConstraint(SpringLayout.NORTH, lblMoi, 24, SpringLayout.NORTH, pan);
		currentLayout.putConstraint(SpringLayout.WEST, lblMoi, 0, SpringLayout.WEST, sendButton);
		pan.add(lblMoi);
		
		lblLui = new JLabel(other);
		currentLayout.putConstraint(SpringLayout.NORTH, lblLui, 0, SpringLayout.NORTH, lblMoi);
		currentLayout.putConstraint(SpringLayout.WEST, lblLui, 36, SpringLayout.WEST, pan);
		pan.add(lblLui);
		
		titleLabel = new JLabel("Chat room");
		currentLayout.putConstraint(SpringLayout.NORTH, titleLabel, -4, SpringLayout.NORTH, lblMoi);
		currentLayout.putConstraint(SpringLayout.EAST, titleLabel, -150, SpringLayout.WEST, lblMoi);
		titleLabel.setFont(new Font("Lucida Grande", Font.BOLD, 17));
		pan.add(titleLabel);
		}
	
	/*** Methods ***/
	public void displayView (String pseudo, Conversation conv) {
		myself = pseudo;
		other = conv.getDestinationUser().getPseudo();
		msgList = conv.getMessages();
		setUpFrame();
		}
	
	public static void main(String[] args) {
		Controller c = null;
		ConversationView cv = new ConversationView(c);
		User user = new User("Thomas", null, 0);
		Conversation conv = new Conversation(user);
		cv.displayView("Laure", conv);
		cv.setVisible(true);
	}
}
