package poo_clavardage;
import java.lang.*;

public class Conversation {

	/*** Attributes ***/
	protected Date startingDate;
	// Links to other classes
	protected User destinationUser;
	
	/*** Constructors ***/
	public void Conversation(Date startingDate) {
		this.startingDate = startingDate;
	}

	/*** Getters & setters ***/
	public Date getStartingDate(){
		return this.startingDate;
	}

	public void setStartingDate(String newStartingDate){
		this.startingDate = newStartingDate;
	}
}

