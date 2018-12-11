package poo_clavardage;
import java.lang.*;

public class Message {

	/*** Attributes ***/
	protected Date date;
	protected String content;
	// Links to other classes
	protected Conversation conv;
	
	/*** Constructors ***/
	public void Message(Date date, String content) {
		this.date = date;
		this.content = content;
	}

	/*** Getters & setters ***/
	public Date getdate(){
		return this.date;
	}
	public String getContent(){
		return this.content;
	}
	public void setdate(String newDate){
		this.date = newDate;
	}
	public void setContent(Int newContent){
		this.content = newContent;
	}
}

