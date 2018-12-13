import java.util.Date;

public class Conversation {

	/*** Attributes ***/
	protected Date startingDate; // The date at wich the conversation between two users started
	// Links to other classes
	protected User destinationUser; 
	// ONE conversation occurs between two users but only ONE is given here since the other is "myself"
	
	/*** Constructors ***/
	public Conversation(Date startingDate, User destinationUser) {
		this.startingDate = startingDate;
		this.destinationUser = destinationUser;
	}

	/*** Getters & setters ***/
	public Date getStartingDate(){
		return this.startingDate;
	}
	public User getDestinationUser(){
		return this.destinationUser;
	}
	public void setStartingDate(Date newStartingDate){
		this.startingDate = newStartingDate;
	}
	public void setDestinationUser(User newDestinationUser){
		this.destinationUser = newDestinationUser;
	}
}

