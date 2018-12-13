package poo_clavardage;
import java.lang.*;

public class Message {

	/*** Attributes ***/
	protected Date date; // The date the message was SENT at from the user
	protected String content; // Content of the message, for now, we focus on textual messages
	// Links to other classes
	protected Conversation conv; // ONE message always belongs to ONE conversation
	
	/*** Constructors ***/
	public void Message(Date date, String content, Conversation conv) {
		this.date = date;
		this.content = content;
		this.conv = conv;
	}

	/*** Getters & setters ***/
	public Date getDate(){
		return this.date;
	}
	public String getContent(){
		return this.content;
	}
	public Conversation getConversation(){
		return this.conv;
	}
	public void setDate(String newDate){
		this.date = newDate;
	}
	public void setContent(Int newContent){
		this.content = newContent;
	}
	public void setConversation(Conversation newConv){
		this.conv = newConv;
	}
}

