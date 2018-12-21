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
	public UserModel um;
	public ConversationModel cm;
	protected Network nw;

	public Controller() {
		nw = new Network(this);
		pv = new PseudoView(this);
		hv = new HomeView(this);
		cv = new ConversationView(this);
		//The user model must be told of the already active users on the local network
		um = new UserModel(nw.findConnectedUsers());
		cm = new ConversationModel();
		displayPseudoView();
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
		//cv.displayView(um.getMyself(), c);
	}

	//Handles a message reception and displays it if it is linked to the current conversation
	public void receiveMsg(InetAddress ip, String content) {
		User u = um.getUserByIP(ip);
		Conversation c = cm.getConvByUser(u);
		c = addMsg(c, content, false);
		//If the message is linked to the conversation that's currently displayed, then the view is refreshed to display it
		if (c == cm.getCurrentConv()) {
			cv.addMsg(content);
		}
	}
	
	//Inserts a message in the local DB, sent is used to tell if the message comes from us 
	private Conversation addMsg(Conversation conv, String content, boolean sent) {
		return cm.addMsg(conv, content, sent);
	}
	
	//Creates a new conversation (always initiated by a remote user) 
	//for conversations started by our own, see displayConversation
	public void startConversation(InetAddress adr) {
		User u = um.getUserByIP(adr);
		cm.startConv(u);
	}
	
	//Displays an already started conversation, or start it and display it if it was not
	public void displayConversation(String pseudo) {
		User u = um.getUserByPseudo(pseudo);
		Conversation c = cm.getConvByUser(u);
		if (c == null) {
			cm.startConv(u);
			cm.setCurrentConv(c);
			hv.hide();
			cv.displayView(um.getMyself(),c);
		}	
	}

	//Flushes the history of past conversations in local DB
	public void deleteHistory() {
		cm.deleteHistory();
	}
	
	/***************************************************/
	/************** NETWORK MANAGEMENT *****************/
	/***************************************************/
	
	//Adds, udpates or removes a user from the connectedUsers list
	public void refreshUser(User u, Action a) {
		if (a == Action.UPDATE) {
			User us = um.getUserByIP(u.getAddress());
			us.setPseudo(u.getPseudo());
			/*
			Conversation c = cm.getConvByUser(us);
			if (c == cm.getCurrentConv()) {
				//cv.updatePseudo(u.getPseudo());
			}
			*/
		}
		//u = um.getUserByIP(u.getAddress());
		um.refreshUser(u, a);
	}

	/***************************************************/
	/************** PSEUDO MANAGEMENT ******************/
	/***************************************************/

	//Used to choose a new pseudo from the pseudo view
	public void setPseudo(String pseudo) {
		//Is the chosen pseudo available ?
		if (um.availablePseudo(pseudo)) {
			nw.notifyPseudo(pseudo);
			//If it is, then we proceed to the home view
			ArrayList<User> users = um.getConnectedUsers();
			pv.hide();
			hv.displayView(um.getMyself(),um.getConnectedUsers(),cm.getHistory());
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
		hv.displayView(um.getMyself(), um.getConnectedUsers(),cm.getHistory());
	}
	//Called from the home view
	public void displayConversationView() {
		cv.displayView(um.getMyself(), cm.getCurrentConv());
	}
}
