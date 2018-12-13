import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ConversationModel {

	/*** Attributes ***/
	protected ArrayList<Conversation> history;
	protected ArrayList<Conversation> currentConversations;
	// List of the conversation that are remembered

	/*** Constructors ***/
	public ConversationModel(ArrayList<Conversation> history) {
		this.history = history; // Need to get the list from the database ???
	}

	/*** Methods ***/
	// Asks the database about a conversion between myself and "pseudo" at a given date
	public Conversation getConvHistory(String pseudo, String date){
		Conversation goodConv = null;
		for(Conversation conv : this.history){
			if((conv.getStartingDate() == date && (conv.getDestinationUser().getPseudo() == pseudo))){
				goodConv = conv;
				break;
			}
		}
		return goodConv;
	}
	
	// Returns the conversation of a connected user.
	public Conversation getConvUser(User user) {
		Conversation goodConv =null;
		for(Conversation conv : this.currentConversations) {
			if(conv.getDestinationUser() == user) {
				goodConv = conv;
				break;
			}
		}
		return goodConv;
	}
	
	public Conversation startConv(User userConcerned) {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		Conversation conv = new Conversation(dateFormat.format(LocalDateTime.now()), userConcerned);
		return conv;
	}

	// Deletes a specific conversation from the history of conversations
	public void deleteConv(Conversation conv){
		this.history.remove(conv);
	}

	// Adds a specific conversation to the history of conversations
	public void addConv(Conversation conv){
		this.history.add(conv);
	}
	
	public void addMsg (Conversation convToUpdate, String content, boolean sent){
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		Message newMsg = Message(dateFormat.format(LocalDateTime.now()),content);
		for(Conversation conv : this.currentConversations) {
			if(conv == convToUpdate) {
				conv.messages.add(newMsg);
				break;
			}
		}
	}

	/*** Getters & setters ***/
	public ArrayList<Conversation> getHistory() {
		return history;
	}
	public void setHistory(ArrayList<Conversation> history) {
		this.history = history;
	}
		
}
