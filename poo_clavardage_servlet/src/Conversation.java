import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Conversation {

	/*** Attributes ***/
	protected String startingDate; // The date at which the conversation between two users started
	// Links to other classes
	protected User destinationUser; // ONE conversation occurs between two users but only ONE is given here since the other is "myself"
	protected ArrayList<Message> messages; // A conversation has a list of messages.

	/*** Constructors ***/
	public Conversation(User destinationUser) {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		this.startingDate = dateFormat.format(LocalDateTime.now());
		this.destinationUser = destinationUser;
		this.messages = new ArrayList<Message>();
	}
	
	public Conversation(User destinationUser, String startingDate, ArrayList<Message> messages) {
		this.startingDate = startingDate;
		this.destinationUser = destinationUser;
		this.messages = messages;
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

