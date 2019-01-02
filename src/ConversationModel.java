import java.sql.Driver;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ConversationModel {

	/*** Attributes ***/
	protected ArrayList<Conversation> history;
	protected ArrayList<Conversation> currentConversations;
	// List of the conversation that are remembered
	protected Conversation currentConv;

	/*** Constructors ***/
	public ConversationModel() {
		// Need to get the list from the database ???
		try {
		      Class.forName("com.mysql.jdbc.Driver");
		      System.out.println("Driver O.K.");      
		    } catch (Exception e) {
		      e.printStackTrace();
		    }   
		
	}

	/*** Methods ***/
	// Asks the database about a conversion between myself and "pseudo" at a given date
	public Conversation getConvFromHistory(String pseudo, String date){
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
	public Conversation getConvByUser(User user) {
		Conversation goodConv =null;
		for(Conversation conv : this.currentConversations) {
			if(conv.getDestinationUser() == user) {
				goodConv = conv;
				break;
			}
		}
		return goodConv;
	}
	
	// Creates a new conversation, makes it THE current one, and adds it to the list of current convs.
	public void startConv(User userConcerned) {
		Conversation conv = new Conversation(userConcerned);
		addConvToCurrent(conv);
		this.currentConv = conv;
	}

	// Deletes a specific conversation from the history of conversations
	public void deleteHistory(){
		this.history = null;
	}

	// Adds a specific conversation to the history of conversations
	public void addConvToHistory(Conversation conv){
		this.history.add(conv);
	}
	
	// Adds a conv to the list of current
	public void addConvToCurrent(Conversation conv){
		this.currentConversations.add(conv);
	}
	
	public Conversation addMsg (Conversation convToUpdate, String content, boolean sent){
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		Conversation newConv = null;
		Message newMsg = new Message(dateFormat.format(LocalDateTime.now()),content, sent);
		for(Conversation conv : this.currentConversations) {
			if(conv == convToUpdate) {
				conv.messages.add(newMsg);
				newConv = conv;
				break;
			}
		}
		return newConv;
	}

	/*** Getters & setters ***/
	public ArrayList<Conversation> getHistory() {
		return history;
	}
	public void setHistory(ArrayList<Conversation> history) {
		this.history = history;
	}
	public ArrayList<Conversation> getCurrentConversations() {
		return currentConversations;
	}
	public void setCurrentConversations(ArrayList<Conversation> currentConversations) {
		this.currentConversations = currentConversations;
	}
	public Conversation getCurrentConv() {
		return currentConv;
	}
	public void setCurrentConv(Conversation currentConv) {
		this.currentConv = currentConv;
	}
		
}
