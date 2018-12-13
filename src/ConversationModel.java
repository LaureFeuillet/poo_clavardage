import java.util.ArrayList;
import java.util.Date;

public class ConversationModel {

	/*** Attributes ***/
	protected ArrayList<Conversation> history;
	// List of the conversation that are remembered

	/*** Constructors ***/
	public ConversationModel(ArrayList<Conversation> history) {
		this.history = history; // Need to get the list from the database ???
	}

	/*** Methods ***/
	// Asks the database about a conversion between myself and "pseudo" at a given date
	public Conversation getConvHistory(String pseudo, Date date){
		Conversation goodConv = null;
		for(Conversation conv : this.history){
			if((conv.getStartingDate() == date && (conv.getDestinationUser().getPseudo() == pseudo))){
				goodConv = conv;
				break;
			}
		}
		return goodConv;
	}
	
	
	public Conversation startConvUser(User userConcerned) {
		
		return conv;
	}

	// Deletes a specific conversation from the history of conversations
	public void deleteConversation(Conversation conv){

	}

	// Adds a specific conversation to the history of conversations
	public void addConversation(Conversation conv){
		

	}

	/*** Getters & setters ***/
	public ArrayList<Conversation> getHistory() {
		return history;
	}

	public void setHistory(ArrayList<Conversation> history) {
		this.history = history;
	}
		
}
