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
	public getdate(){
		return this.date;
	}
	public getContent(){
		return this.content;
	}
	public setdate(String newDate){
		this.date = newDate;
	}
	public setContent(Int newContent){
		this.content = newContent;
	}
}

