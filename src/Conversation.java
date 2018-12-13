import java.util.ArrayList;

public class Conversation {

	/*** Attributes ***/
	protected String startingDate; // The date at which the conversation between two users started
	// Links to other classes
	protected User destinationUser; // ONE conversation occurs between two users but only ONE is given here since the other is "myself"
	protected ArrayList<Message> messages; // A conversation has a list of messages.

	/*** Constructors ***/
	public Conversation(String startingDate, User destinationUser) {
		this.startingDate = startingDate;
		this.destinationUser = destinationUser;
	}

	/*** Getters & setters ***/
	public String getStartingDate(){
		return this.startingDate;
	}
	public User getDestinationUser(){
		return this.destinationUser;
	}
	public void setStartingDate(String newStartingDate){
		this.startingDate = newStartingDate;
	}
	public void setDestinationUser(User newDestinationUser){
		this.destinationUser = newDestinationUser;
	}
	public ArrayList<Message> getMessages() {
		return messages;
	}
	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}
}

