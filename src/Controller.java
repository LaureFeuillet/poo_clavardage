import java.net.InetAddress;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
import java.util.ArrayList;

/* Used to tell the user model how to refresh the connected user list
 * CONNECT to add a new user
 * DISCONNECT to remove a user
 * UPDATE to update the user's pseudo 
 */

//Used to determine what to do with a user that changed is status
enum Action{
	CONNECT,
	DISCONNECT,
	UPDATE
};

//Used to determine whether the view we are currently displaying has to be refreshed or not
enum CurrentView{
	PSEUDO,
	HOME,
	CONVERSATION
};

public class Controller {
	protected PseudoView pv;
	protected HomeView hv;
	protected ConversationView cv;
	public UserModel um;
	public ConversationModel cm;
	protected Network nw;
	protected CurrentView currentView;

	public Controller() {
		currentView = CurrentView.PSEUDO;
		nw = new Network(this);
		pv = new PseudoView(this);
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
		addMsg(c, content, true);
		nw.sendMsg(u,content);
		//The conversation view is refreshed to display the newly sent message
		//cv.displayView(um.getMyself(), c);
	}

	//Handles a message reception and displays it if it is linked to the current conversation
	public void receiveMsg(InetAddress ip, String content) {
		User u = um.getUserByIP(ip);
		Conversation c = cm.getConvByUser(u);
		addMsg(c, content, false);
		//If the message is linked to the conversation that's currently displayed, then the view is refreshed to display it
		if (c == cm.getCurrentConv()) {
			cv.addMsg(content);
		}
	}
	
	//Inserts a message in the local DB, sent is used to tell if the message comes from us 
	private void addMsg(Conversation conv, String content, boolean sent) {
		cm.addMsg(conv, content, sent);
	}
	
	//Creates a new conversation (always initiated by a remote user) 
	//for conversations started by our own, see displayConversation
	public void startConversation(InetAddress adr) {
		User u = um.getUserByIP(adr);
		cm.startConv(u, false);
	}
	
	//Displays an already started conversation, or starts it and displays it if it was not
	public void displayConversation(String pseudo) {
		User u = um.getUserByPseudo(pseudo);
		Conversation c = cm.getConvByUser(u);
		if (c == null) {
			nw.addConv(u);
			cm.startConv(u, true);
			c = cm.getConvByUser(u);
		}	
		cm.setCurrentConv(c);
		displayConversationView();
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
		Conversation conv = cm.getConvByUser(um.getUserByIP(u.getAddress()));
		um.refreshUser(u, a);
		switch(a) {
		case CONNECT:
			if (currentView == CurrentView.HOME)
				hv.refreshView();
			break;
		case UPDATE:
			if (conv != null)
				cm.updatePseudoInDB(conv, u.getPseudo());
			if (currentView == CurrentView.HOME) {
				hv.refreshView();
			}
			else {
				if (currentView == CurrentView.CONVERSATION) {
					if (conv == cm.getCurrentConv()) {
						cv.updatePseudo(u.getPseudo());
					}
				}
			}
			break;
		case DISCONNECT:
			if (currentView == CurrentView.HOME) {
				hv.refreshView();
			}	
			else {
				if (currentView == CurrentView.CONVERSATION) {
					if (conv.getStartingDate().equals(cm.getCurrentConv().getStartingDate())) {
						cv.userLeft();
					}
				}
			}
			break;
		}
	}

	/***************************************************/
	/************** PSEUDO MANAGEMENT ******************/
	/***************************************************/

	//Used to choose a new pseudo from the pseudo view
	public void setPseudo(String pseudo) {
		//Is the chosen pseudo available ?
		if (um.availablePseudo(pseudo)) {
			//Notifies all connected users of the newly chosen pseudo
			nw.notifyPseudo(pseudo);
			//If it is, then we proceed to the home view
			pv.setVisible(false);
			displayHomeView();
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
		currentView = CurrentView.PSEUDO;
		pv.displayView();
	}
	//Called from the pseudo view
	public void displayHomeView() {
		um.debugUsers();
		this.hv = new HomeView(this, um.getMyself(), um.getConnectedUsers(), cm.getHistory());
		currentView = CurrentView.HOME;
		hv.displayView();
	}
	//Called from the home view
	public void displayConversationView() {
		um.debugUsers();
		this.cv = new ConversationView(this);
		currentView = CurrentView.CONVERSATION;
		cv.displayView(um.getMyself(), cm.getCurrentConv());
		cm.printHistory();
	}
}
