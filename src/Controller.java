import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

enum Action{
	CONNECT,
	DISCONNECT,
	UPDATE
};

public class Controller {
	protected User myself;
	protected ArrayList<User> connectedUsers;
	protected ArrayList<Conversation> startedConversations;
	protected ArrayList<Conversation> history;
	protected PseudoView pv;
	protected HomeView hv;
	protected ConversationView cv;
	protected UserModel um;
	protected ConversationModel cm;
	protected Network nw;

	public Controller() {
		nw = new Network(this);
		myself = new User();
		pv = new PseudoView();
		hv = new HomeView();
		cv = new ConversationView();
		um = new UserModel(nw.findConnectedUsers());
		cm = new ConversationModel();
		connectedUsers = um.getConnectedUsers();
		startedConversations = new ArrayList<Conversation>();
		history = cm.getHistory();
	}

	/***************************************************/
	/*********** GESTION DES CONVERSATIONS *************/
	/***************************************************/

	//Envoie un message
	public void sendMsg(String pseudo, String content) {
		User u = um.getUser(pseudo);
		addMsg(u, content);
		nw.sendMessage(u,content);
		currentConv = cm.getCurrentConv();
		cv.refreshView(currentConv);
	}

	//Affiche un message dans la conversation courante
	public void receiveMsg(Conversation conv, String content) {
		addMsg(conv, content, false);
	}
	
	//Stocke un message en base de données locale
	private void addMsg(Conversation conv, String content, boolean sent) {
		mm.addMsg(conv, msg, sent);
	}
	//Créé une nouvelle conversation
	public void startConversation(User u) {
		Conversation c = cm.startConv(u);
		//On affiche la vue
		displayConversationView(c);
	}
	//Ouvre une conversation
	public void openConversation(User u) {
		Conversation c = cm.getConvUser(u);
		cv.displayView(c);	
	}

	//Permet de flush la table conversation de la BD locale
	public void deleteHistory() {
		cm.deleteHistory();
		hv.refreshView();
	}

	/***************************************************/
	/************** GESTION DU PSEUDO ******************/
	/***************************************************/

	//Choisir un nouveau pseudo (Appelée depuis la pseudoView)
	public boolean setPseudo(String pseudo) {
		if (um.availablePseudo(pseudo)) {
			myself = um.getMyself();
			hv.displayView();
		}
		else {
			pv.printMsgError();
		}
	}

	/***************************************************/
	/************** AFFICHAGE DES VUES *****************/
	/***************************************************/

	//Appelée depuis la HomeView
	public void displayPseudoView() {
		pv.displayView();
	}
	//Appelée depuis la pseudoView
	public void displayHomeView() {
		hv.displayView();
	}
	//Appelée depuis la homeView
	public void displayConversationView() {
		cv.displayView(currentConv);
	}
}
