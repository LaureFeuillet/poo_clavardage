import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/* Used to tell the user model how to refresh the connected user list
 * CONNECT to add a new user
 * DISCONNECT to remove a user
 * UPDATE to update the user's pseudo 
 */
enum Action{
	CONNECT,
	DISCONNECT,
	UPDATE
};

public class Controller {
	protected PseudoView pv;
	protected HomeView hv;
	protected ConversationView cv;
	protected UserModel um;
	protected ConversationModel cm;
	protected Network nw;

	public Controller() {
		nw = new Network(this);
		pv = new PseudoView();
		hv = new HomeView();
		cv = new ConversationView();
		//The user model must be told of the already active users on the local network
		um = new UserModel(nw.findConnectedUsers());
		cm = new ConversationModel();
	}

	/***************************************************/
	/*********** CONVERSATIONS MANAGEMENT **************/
	/***************************************************/

	//Sends a message
	public void sendMsg(String pseudo, String content) {
		User u = um.getUserByPseudo(pseudo);
		Conversation c = cm.getCurrentConv();
		c = addMsg(c, content, true);
		nw.sendMsg(u,content);
		//The conversation view is refreshed to display the newly sent message
		cv.refreshView(c);
	}

	//Handles a message reception and displays it if it is linked to the current conversation
	public void receiveMsg(InetAddress ip, String content) {
		User u = um.getUserByIP(ip);
		Conversation c = cm.getConvByUser(u);
		c = addMsg(c, content, false);
		//If the message is linked to the conversation that's currently displayed, then the view is refreshed to display it
		if (c == cm.getCurrentConv())
			cv.refreshView();
	}
	
	//Inserts a message in the local DB, sent is used to tell if the message comes from us 
	private Conversation addMsg(Conversation conv, String content, boolean sent) {
		return cm.addMsg(conv, content, sent);
	}
	
	//Creates a new conversation and displays it if it was initiated by the user
	public void startConversation(String pseudo, boolean startedByMe) {
		User u = um.getUserByPseudo(pseudo);
		cm.startConv(u);
		//If the user chose to start this conversation, then it must be displayed
		if (startedByMe) {
			displayConversationView();
		}
	}
	
	//Opens an already started conversation
	public void displayConversation(String pseudo) {
		User u = um.getUserByPseudo(pseudo);
		Conversation c = cm.getConvByUser(u);
		cm.setCurrentConv(c);
		displayConversationView();	
	}

	//Flushes the history of past conversations in local DB
	public void deleteHistory() {
		cm.deleteHistory();
		hv.refreshView();
	}

	/***************************************************/
	/************** PSEUDO MANAGEMENT ******************/
	/***************************************************/

	//Used to choose a new pseudo from the pseudo view
	public boolean setPseudo(String pseudo) {
		//Is the chosen pseudo available ?
		if (um.availablePseudo(pseudo)) {
			//If it is, then we proceed to the home view
			hv.displayView();
		}
		else {
			//Otherwise we just notice the user that he must choose another pseudo
			pv.printMsgError();
		}
	}

	/***************************************************/
	/*************** VIEWS DISPLAYING ******************/
	/***************************************************/

	//Called from the home view
	public void displayPseudoView() {
		pv.displayView();
	}
	//Called from the pseudo view
	public void displayHomeView() {
		ArrayList<User> connectedUsers = um.getConnectedUsers();
		String myPseudo = um.getMyself();
		hv.displayView(myPseudo, connectedUsers);
	}
	//Called from the home view
	public void displayConversationView() {
		String myPseudo = um.getMyself();
		cv.displayView(myPseudo, cm.getCurrentConv());
	}
}
