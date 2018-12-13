import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
	protected MessageModel mm;
	protected Conversation currentConv;

	public Controller() {
		myself = new User();
		pv = new PseudoView();
		hv = new HomeView();
		cv = new ConversationView();
		um = new UserModel();
		cm = new ConversationModel();
		mm = new MessageModel();
		connectedUsers = um.getConnectedUsers();
		//Aucune conversation au démarrage
		currentConv = null;
		startedConversations = new ArrayList<Conversation>();
		history = cm.getHistory();

	}

	/***************************************************/
	/*********** GESTION DES CONVERSATIONS *************/
	/***************************************************/

	//Envoie un message
	public void sendMsg(Conversation conv, Message msg) {
		//Il va bien nous falloir une classe pour gérer les envois physiques...
		//XXX.sendMsg(conv, msg)
		cv.displayMessage(msg);
		addMsg(conv, msg);
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
		displayConversationView();
	}
	//Ouvre une conversation
	public void openConversation(User u) {
		if
		currentConv = conv;
		displayConversationView();
	}

	//Permet de flush la table conversation de la BD locale
	public void deleteHistory() {
		cm.deleteHistory();
	}

	/***************************************************/
	/************** GESTION DU PSEUDO ******************/
	/***************************************************/

	//Choisir un nouveau pseudo (Appelée depuis la pseudoView)
	public boolean setPseudo(String pseudo) {
		boolean available = true;
		//On parcourt la liste des utilisateurs connectés pour vérifier qu'aucun n'a
		//déjà le pseudo qu'on souhaite s'attribuer
		for(User u : connectedUsers){
			if (u.pseudo == pseudo ) {
				available = false;
				break;
			}
		}
		if (available) {
			myself.pseudo = pseudo;
		}
		return available;
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
