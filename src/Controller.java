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
		nw = new Network();
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
		cm.addMessage(u, content);
		nw.sendMessage(u,content);
		currentConv = cm.getCurrentConv();
		cv.refreshView(currentConv);
	}

	//Affiche un message dans la conversation courante
	public void receiveMsg(Conversation conv, Message msg) {
		cv.displayMsg(msg);
		addMsg(conv, msg);
	}
	//Stocke un message en base de données locale
	private void addMsg(Conversation conv, Message msg) {
		mm.addMsg(conv, msg);
	}
	//Créé une nouvelle conversation
	public void startConversation(User u) {
		//On créé un format de date
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		//On créé une nouvelle conversation avec la date actuelle
		Conversation conv = new Conversation(u, dateFormat.format(LocalDateTime.now()));
		startedConversations = cm.addConversation();
		//La conversation ouverte devient celle que l'on vient de commencer
		currentConv = conv;
		//On affiche la vue
		displayConversationView(conv);
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
