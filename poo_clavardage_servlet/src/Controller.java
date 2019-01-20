import java.net.InetAddress;
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
		displayPseudoView("");
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
		//um.debugUsers();
		//System.out.println(ip.toString());
		User u = um.getUserByIP(ip);
		//System.out.println(u.getAddress().toString());
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
	
	
	//Refreshes the list of connected users
	public void refreshUsers(ArrayList<User> users) {
		ArrayList<User> usersToDelete = new ArrayList<User>();
		ArrayList<User> connectedUsers = um.getConnectedUsers();
		Conversation convWithU;
		//First we check whether new users have connected, or if pseudos have been updated
		for (User newU : users) {
			boolean found = false;
			for (User u : connectedUsers) {
				if (newU.getAddress().toString().equals(u.getAddress().toString())) {
					found = true;
					//If there is a different pseudo
					if (!newU.getPseudo().equals(u.getPseudo())) {
						//If it is the user we are currently talking to
						convWithU = cm.getConvByUser(u);
						if (convWithU == cm.getCurrentConv()) {
							cv.updatePseudo(newU.getPseudo());
						}
						String oldPseudo = "";
						Conversation conv = cm.getConvByUser(um.getUserByIP(newU.getAddress()));
						if (conv != null)
							oldPseudo = conv.getDestinationUser().getPseudo();
						//Refreshes the list
						um.refreshUser(newU, Action.UPDATE);
						//Updates the conversation in DB in order to change the pseudo that was previously in there
						if (convWithU != null)
							cm.updatePseudoInDB(convWithU, oldPseudo);		
					}
				}
			}
			if (!found) {
				//If the user was not in the already known list
				um.refreshUser(newU, Action.CONNECT);
			}
		}
		//Now checks if some users have left
		for (User u : connectedUsers) {
			boolean found = false;
			for (User newU : users) {
				if (newU.getAddress().toString().equals(u.getAddress().toString())) {
					found = true;
				}
			}
			if (!found) {
				//If a user has left, it is added to the list of the users to delete
				usersToDelete.add(u);
			}	
		}
		//Actually removes the users that have left
		for (User u : usersToDelete) {
			convWithU = cm.getConvByUser(u);
			//Notifies the user that his intermediary has left if he was associated to the current conversation
			if (convWithU == cm.getCurrentConv()) {
				cv.userLeft();
			}
			//User removal
			um.refreshUser(u, Action.DISCONNECT);
		}
		//Refreshes the home view if we are currently displaying it
		if(currentView == CurrentView.HOME) {
			hv.refreshView();
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
	public void displayPseudoView(String pseudo) {
		currentView = CurrentView.PSEUDO;
		pv.displayView(pseudo);
	}
	//Called from the pseudo view
	public void displayHomeView() {
		//um.debugUsers();
		this.hv = new HomeView(this, um.getMyself(), um.getConnectedUsers(), cm.getHistory());
		currentView = CurrentView.HOME;
		hv.displayView();
		//cm.printHistory();
	}
	//Called from the home view
	public void displayConversationView() {
		//um.debugUsers();
		this.cv = new ConversationView(this);
		currentView = CurrentView.CONVERSATION;
		cv.displayView(um.getMyself(), cm.getCurrentConv());
		//cm.printHistory();
	}
}
