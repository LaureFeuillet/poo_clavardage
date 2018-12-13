package poo_clavardage;
import java.lang.*;

public class ConversationModel {

	/*** Attributes ***/
	protected ArrayList<Conversation> history;
	// List of the conversation that are remembered

	/*** Constructors ***/
	public void ConversationModel(ArrayList<Conversation> history) {
		this.history = history; // Need to get the list from the database ???
	}

	/*** Methods ***/
	// Asks the database about a conversion between myself and "pseudo" at a given date
	public Conversation getConvHistory(String pseudo, Date date){
		// 
		for(Conversation conv : this.history){
			if((conv.getStartingDate() == date && (conv.getDestinationUser().getPseudo() == pseudo)){
				return conv;
			}
		}
	}

	public Conversation getConvUser() {
		// ...
		return conv;
	}

	// Deletes a specific conversation from the history of conversations
	public void deleteConversation(Conversation conv){

	}

	// Adds a specific conversation to the history of conversations
	public void addConversation(Conversation conv){
		

	}

	/*** Getters & setters ***/

}
