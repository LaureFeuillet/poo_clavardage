
public class Message {

	/*** Attributes ***/
	protected String date; // The date the message was SENT at from the user
	protected String content; // Content of the message, for now, we focus on textual messages
	protected Boolean sent;

	/*** Constructors ***/
	public Message(String date, String content, Boolean sent) {
		this.date = date;
		this.content = content;
		this.sent = sent;
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
	public Boolean getSent() {
		return this.sent;
	}
	public void setSent(Boolean sent) {
		this.sent = sent;
	}
}

