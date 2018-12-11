package poo_clavardage;
import java.lang.*;

public class ConversationModel {

	/*** Attributes ***/
	protected ArrayList<Conversation> history;
	// List of the conversation that are remembered

	/*** Constructors ***/
	public void ConversationModel(ArrayList<Conversation> history) {
		this.history = history;
	}

	/*** Methods ***/
	// Asks the database about a conversion between myself and "pseudo" at a given date
	public Conversation getConversation(String pseudo, Date date){

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
