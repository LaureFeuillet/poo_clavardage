import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TimeZone;

public class ConversationModel {

	/*** Attributes ***/
	protected ArrayList<Conversation> history;
	protected ArrayList<Conversation> currentConversations;
	// List of the conversation that are remembered
	protected Conversation currentConv;
	
	// To connect to the database
	protected Connection con = null;
	protected String url = "jdbc:mysql://localhost:3306/clavardage?serverTimezone=" + TimeZone.getDefault().getID();
	protected String user = "root";
	protected String pwd = "lkilipoutte";

	/*** Constructors ***/
	public ConversationModel() {
		// Establish a connection to the database
		// to get all previous conversations for history
		Statement stmt = null;
		ResultSet rs = null;
		try {
		      Class.forName("com.mysql.cj.jdbc.Driver");
		      System.out.println("Driver OK");
		      
		      con = DriverManager.getConnection(url, user, pwd);
		      System.out.println("Connection established !");
		      
		      stmt = con.createStatement();
		      rs = stmt.executeQuery("SELECT * FROM ");
		      
		      while (rs.next()){
		    	  System.out.print(rs.getString(1)+ ":");
		    	  System.out.println(rs.getDouble("salary"));
		    }
		      
		    } catch (Exception e) {
		      e.printStackTrace();
		    } finally {
		    	if (con != null) {
		    		try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
		    	}
		    }
		
		currentConversations = new ArrayList<Conversation>();
		history = new ArrayList<Conversation>();
	}

	/*** Methods ***/
	// Asks the database about a conversion between myself and "pseudo" at a given date
	public Conversation getConvFromHistory(String pseudo, String date){
		Conversation goodConv = null;
		for(Conversation conv : this.history){
			if((conv.getStartingDate() == date && (conv.getDestinationUser().getPseudo() == pseudo))){
				goodConv = conv;
				break;
			}
		}
		return goodConv;
	}
	
	// Returns the conversation of a connected user.
	public Conversation getConvByUser(User user) {
		Conversation goodConv =null;
		for(Conversation conv : this.currentConversations) {
			if(conv.getDestinationUser() == user) {
				goodConv = conv;
				break;
			}
		}
		return goodConv;
	}
	
	// Creates a new conversation, makes it THE current one, and adds it to the list of current convs.
	public void startConv(User userConcerned) {
		Conversation conv = new Conversation(userConcerned);
		addConvToCurrent(conv);
		this.currentConv = conv;
	}

	// Deletes a specific conversation from the history of conversations
	public void deleteHistory(){
		this.history = null;
	}

	// Adds a specific conversation to the history of conversations
	public void addConvToHistory(Conversation conv){
		this.history.add(conv);
	}
	
	// Adds a conv to the list of current
	public void addConvToCurrent(Conversation conv){
		this.currentConversations.add(conv);
	}
	
	public Conversation addMsg (Conversation convToUpdate, String content, boolean sent){
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		Conversation newConv = null;
		Message newMsg = new Message(dateFormat.format(LocalDateTime.now()),content, sent);
		for(Conversation conv : this.currentConversations) {
			if(conv == convToUpdate) {
				conv.messages.add(newMsg);
				newConv = conv;
				break;
			}
		}
		return newConv;
	}

	/*** Getters & setters ***/
	public ArrayList<Conversation> getHistory() {
		return history;
	}
	public void setHistory(ArrayList<Conversation> history) {
		this.history = history;
	}
	public ArrayList<Conversation> getCurrentConversations() {
		return currentConversations;
	}
	public void setCurrentConversations(ArrayList<Conversation> currentConversations) {
		this.currentConversations = currentConversations;
	}
	public Conversation getCurrentConv() {
		return currentConv;
	}
	public void setCurrentConv(Conversation currentConv) {
		this.currentConv = currentConv;
	}
	
	public static void main(String[] args)
	{
		ConversationModel cm = new ConversationModel();
	}
}
