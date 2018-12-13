
public class Message {

	/*** Attributes ***/
	protected String date; // The date the message was SENT at from the user
	protected String content; // Content of the message, for now, we focus on textual messages
	// Links to other classes
	
	/*** Constructors ***/
	public Message(String date, String content) {
		this.date = date;
		this.content = content;
	}

	/*** Getters & setters ***/
	public String getDate(){
		return this.date;
	}
	public String getContent(){
		return this.content;
	}
	public void setDate(String newDate){
		this.date = newDate;
	}
	public void setContent(String newContent){
		this.content = newContent;
	}
}

