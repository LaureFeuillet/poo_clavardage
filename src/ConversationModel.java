import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
	protected String user = "clavardage";
	protected String pwd = "clavardage";

	/*** Constructors ***/
	public ConversationModel() {
		currentConversations = new ArrayList<Conversation>();
		currentConv = null;
		// Establish a connection to the database
		// to get all previous conversations for history
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		PreparedStatement pstmt = null;
	    ResultSet rsMsg = null;
		try {
		      Class.forName("com.mysql.cj.jdbc.Driver");
		      System.out.println("Driver OK");
		      
		      con = DriverManager.getConnection(url, user, pwd);
		      System.out.println("Connection established !");
		      
		      stmt = con.createStatement();
		      
		      // Create in DB table Conversation 
		      query = "CREATE TABLE IF NOT EXISTS Conversation ("
		      		+ " id_conv SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,"
		      		+ " pseudo VARCHAR(50) NOT NULL,"
		      		+ " starting_date VARCHAR(50) NOT NULL,"
		      		+ " PRIMARY KEY (id_conv))"
		      		+ " ENGINE=INNODB;";
		      stmt.executeUpdate(query);
		      
		      // Create in DB table Message
		      query ="CREATE TABLE IF NOT EXISTS Message("
		    		+ " id_msg SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
		      		+ " conv SMALLINT UNSIGNED NOT NULL,"
		      		+ " date VARCHAR(50) NOT NULL,"
		      		+ " content TEXT NOT NULL,"
		      		// sent is suppose to be a boolean, but it doesn't exist in mySQL
		      		// 0: false, 1: true
		      		+ " sent TINYINT(1) NOT NULL,"
		      		+ " CONSTRAINT msg_conv"
		      		+ " FOREIGN KEY (conv)"
		      		+ " REFERENCES Conversation(id_conv))"
		      		+ " ENGINE=INNODB;";
		      stmt.executeUpdate(query);

		      // Get previous conversations and corresponding messages from DB
		      history = new ArrayList<Conversation>();
		      // First, we get all previous conversations
		      query = "SELECT pseudo, starting_date"
		    		+ " FROM conversation";
		      rs = stmt.executeQuery(query);
		      
		      String pseudo = null;
		      String startingDate = null;
		      
		      ArrayList<Message> messages = null;
		      while (rs.next()){
		    	  pseudo = rs.getString("pseudo");
		    	  startingDate = rs.getString("starting_date");
		    	  query = "SELECT date, content, sent"
		    	  		+ " FROM Message"
		    	  		+ " INNER JOIN Conversation"
		    	  		+ " ON Message.conv = Conversation.id_conv"
		    	  		+ " WHERE Conversation.pseudo = ?"
		    	  		+ " AND Conversation.starting_date = ?;";

		    	  pstmt = con.prepareStatement(query);
		    	  pstmt.setString(1, pseudo);
		    	  pstmt.setString(2, startingDate);
		    	  rsMsg = pstmt.executeQuery();
		    	  
		    	  messages = new ArrayList<Message>();
		    	  while(rsMsg.next()) {
		    		  messages.add(new Message(rsMsg.getString("date"), rsMsg.getString("content"), rsMsg.getBoolean("sent")));
		    	  }
		    	  history.add(new Conversation(new User(pseudo, null, 0), startingDate, messages));
		      }
		    } catch (Exception e) {
		      e.printStackTrace();
		    } finally {
		    	if (con != null) {
		    		try {
		    			con.close();
						stmt.close();
						rs.close();
						if(pstmt != null) {
							pstmt.close();
					    	rsMsg.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
		    	}
		    }
		}

	/*** Methods ***/
	// Asks the database about a conversation between myself and "pseudo" at a given date
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

	// Deletes all conversations in DB
	public void deleteHistory(){
		history.clear();

		Statement stmt = null;
		try {
			con = DriverManager.getConnection(url, user, pwd);
			System.out.println("Connection established to delete history.");
			stmt = con.createStatement();
			String query = "DELETE FROM Conversation";
			stmt.executeUpdate(query);
			query = "DELETE FROM Message";
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
	    	if (con != null) {
	    		try {
	    			con.close();
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    	}
	    }
	}

	// Adds a specific conversation in DB
	public void addConvToHistory(Conversation conv){
		history.add(conv);
		
		PreparedStatement pstmt = null;
		try {
			con = DriverManager.getConnection(url, user, pwd);
			System.out.println("Connection established to add a conv to history.");
			String query = "INSERT INTO Conversation"
					+ " VALUES (NULL, ?, ?);";
			pstmt = con.prepareStatement(query);
	    	pstmt.setString(1, conv.getDestinationUser().getPseudo());
	    	pstmt.setString(2, conv.getStartingDate());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
	    	if (con != null) {
	    		try {
	    			con.close();
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    	}
	    }
	}
	
	// Adds a conv to the list of current conversations
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

	/*
	public static void main(String[] args)
	{
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		ConversationModel cm = new ConversationModel();
		User dest = new User("toto", null, 0);
		ArrayList<Message> messages = new ArrayList<Message>();
		messages.add(new Message(dateFormat.format(LocalDateTime.now()), "Coucou", true));
		messages.add(new Message(dateFormat.format(LocalDateTime.now()), "Salut", false));
		messages.add(new Message(dateFormat.format(LocalDateTime.now()), "Comment �a va ?", true));
		messages.add(new Message(dateFormat.format(LocalDateTime.now()), "Bien merci", false));
		Conversation conv = new Conversation(dest, dateFormat.format(LocalDateTime.now()), messages);
		cm.addConvToHistory(conv);
		ArrayList<Conversation> historique = cm.getHistory();
		
		for(Conversation c : historique)
		{
			System.out.println("*** " + c.getDestinationUser() + "***");
			for(Message m : c.getMessages())
			{
				System.out.println(m.getDate() + " : "+m.getContent());
			}
		}
		
		cm.deleteHistory();
		historique = cm.getHistory();
		
		for(Conversation c : historique)
		{
			System.out.println("*** " + c.getDestinationUser() + "***");
			for(Message m : c.getMessages())
			{
				System.out.println(m.getDate() + " : "+m.getContent());
			}
		}
	} 
	*/
}
