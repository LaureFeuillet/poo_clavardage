import java.sql.Connection;
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
	protected String url = "jdbc:mysql://localhost:3306/clavardage?serverTimezone=" + TimeZone.getDefault().getID();
	protected String user = "clavardage";
	protected String pwd = "clavardage";
	// To know if the DB is operational
	protected boolean dbSet;

	
	
	/*** Constructors ***/
	public ConversationModel() {
		dbSet = true;
		currentConversations = new ArrayList<Conversation>();
		currentConv = null;
		history = new ArrayList<Conversation>();
		// Establish a connection to the database
		// to get all previous conversations for history
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
	    ResultSet rsMsg = null;
		try {
			  // Load to the MySQL Driver
		      Class.forName("com.mysql.cj.jdbc.Driver");
		      System.out.println("[DB] Driver OK.");
		      
		      // Connect to the database according to the given url, login & password
		      con = DriverManager.getConnection(url, user, pwd);
		      System.out.println("[DB] Connection established in constructor.");
		      
		      stmt = con.createStatement();
		      
		      // Create in the table Conversation in DB
		      query = "CREATE TABLE IF NOT EXISTS conversation ("
		      		+ " id_conv SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,"
		      		+ " pseudo VARCHAR(50) NOT NULL,"
		      		+ " starting_date VARCHAR(50) NOT NULL,"
		      		+ " PRIMARY KEY (id_conv))"
		      		+ " ENGINE=INNODB;";
		      stmt.executeUpdate(query);
		      
		      // Create the table Message in DB
		      query ="CREATE TABLE IF NOT EXISTS message("
		    		+ " id_msg SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
		      		+ " conv SMALLINT UNSIGNED NOT NULL,"
		      		+ " date VARCHAR(50) NOT NULL,"
		      		+ " content TEXT NOT NULL,"
		      		// sent is suppose to be a boolean, but it doesn't exist in mySQL
		      		// 0: false, 1: true
		      		+ " sent TINYINT(1) NOT NULL,"
		      		+ " CONSTRAINT msg_conv"
		      		+ " FOREIGN KEY (conv)"
		      		+ " REFERENCES conversation(id_conv))"
		      		+ " ENGINE=INNODB;";
		      stmt.executeUpdate(query);

		      // Get previous conversations and corresponding messages from DB
		      // First, we get all previous conversations is rs
		      query = "SELECT *"
		    		+ " FROM conversation";
		      rs = stmt.executeQuery(query);
		      
		      String pseudo = null;
		      String startingDate = null;
		      int id_conv = 0;
		      
		      ArrayList<Message> messages = new ArrayList<Message>();
		      // For each conversation, we get the corresponding messages
		      // By checking for each message if the conv = id_conv
		      
		      // rs stores all conversations
		      Statement stmtMsg = con.createStatement();
		      while (rs.next()) {
		    	  id_conv = rs.getInt("id_conv");
		    	  pseudo = rs.getString("pseudo");
		    	  startingDate = rs.getString("starting_date");
		    	  query = "SELECT date, content, sent"
		    	  		+ " FROM message"
		    	  		+ " INNER JOIN conversation"
		    	  		+ " ON message.conv = conversation.id_conv"
		    	  		+ " WHERE message.conv = '" + id_conv + "';";
		    	  rsMsg = stmtMsg.executeQuery(query);
		    	  // rsMsg stores all messages corresponding to the current conversation
		    	  messages = new ArrayList<Message>();
		    	  while(rsMsg.next()) {
		    		  messages.add(new Message(rsMsg.getString("date"), rsMsg.getString("content"), rsMsg.getBoolean("sent")));
		    	  }
		    	  history.add(new Conversation(new User(pseudo, null, 0), startingDate, messages));
		      }
		    } catch (ClassNotFoundException e) {
		    	dbSet = false;
		    } catch (Exception e) {
		    	e.printStackTrace();
		    } finally {
		    	if (con != null) {
		    		try {
		    			con.close();
						stmt.close();
						if (rs != null)
							rs.close();
						if(rsMsg != null) {
					    	rsMsg.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
						dbSet = false;
					}
		    	}
		    }
		}

	// To update the pseudo of a user in DB if it changed
	public void updatePseudoInDB(Conversation conv, String oldPseudo) {
		if(dbSet) {
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				con = DriverManager.getConnection(url, user, pwd);
				stmt = con.createStatement();
				
				// To find the id_conv of the old conversation
				String query = "SELECT id_conv"
						+ " FROM conversation "
						+ " WHERE pseudo = '" + oldPseudo + "'"
						+ " AND starting_date = '" + conv.getStartingDate().toString() + "';";
				// rs store the id_conv 
				rs = stmt.executeQuery(query);
				if (rs.next()) {
					// TP update the corresponding conversation according to the new pseudo
					query = "UPDATE conversation"
						  + " SET pseudo='" + conv.getDestinationUser().getPseudo() + "'"
						  + " WHERE id_conv = '" + rs.getInt("id_conv") + "';";
					stmt.executeUpdate(query);
					System.out.println("[DB] User updated.");
				} else {
					System.out.println("[DB] Error/updatePseudoInDB : Conversation not found.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
		    	if (con != null) {
		    		try {
		    			con.close();
		    			if (stmt != null) {
		    				stmt.close();
		    			}
					} catch (SQLException e) {
						e.printStackTrace();
					}
		    	}
			}
	    }
	}
	
	/*** Methods ***/
	// To ask the database about a conversation between myself and "pseudo" at a given date
	public Conversation getConvFromHistory(String pseudo){
		Conversation goodConv = null;
		for(Conversation conv : this.history) {
			if(conv.getDestinationUser().getPseudo().equals(pseudo)){
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
	
	// Creates a new conversation, add to the DB, makes it THE current one, and adds it to the list of current convs.
	public void startConv(User userConcerned, boolean setCurrent) {
		Conversation conv = new Conversation(userConcerned);
		if (dbSet) addConvToDB(conv);
		this.currentConversations.add(conv);
		if (setCurrent)
			this.currentConv = conv;
	}
	
	public void addConvToHistory(Conversation conv) {
		history.add(conv);
		currentConversations.remove(conv);
	}

	public void addMsg (Conversation convToUpdate, String content, boolean sent){
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		Message newMsg = new Message(dateFormat.format(LocalDateTime.now()),content, sent);
		if(dbSet) addMsgToDB(convToUpdate, newMsg);
		for(Conversation conv : this.currentConversations) {
			if(conv == convToUpdate) {
				conv.messages.add(newMsg);
				break;
			}
		}
	}


	// Adds a specific conversation in DB
	public void addConvToDB(Conversation conv){
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DriverManager.getConnection(url, user, pwd);
			String query = "INSERT INTO conversation"
					+ " VALUES (NULL, ?, ?);";
			pstmt = con.prepareStatement(query);
	    	pstmt.setString(1, conv.getDestinationUser().getPseudo());
	    	pstmt.setString(2, conv.getStartingDate());
			pstmt.executeUpdate();
			System.out.println("[DB] Inserted conversation in DB.");
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
	
	//Add a specific message in DB
	public void addMsgToDB(Conversation conv, Message msg) {
		String query = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
			// First we need to get the id of the conversation in DB
			con = DriverManager.getConnection(url, user, pwd);
			query="SELECT id_conv FROM conversation"
				+ " WHERE pseudo = '" + conv.getDestinationUser().getPseudo() + "'"
				+ " AND starting_date = '" + conv.getStartingDate().toString() + "';";
			stmt = con.createStatement();
			// rs store the id_conv 
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				// Then we can add the msg in DB
				query = "INSERT INTO message"
						+ " VALUES (NULL, ?, ?, ?, ?);";
				pstmt = con.prepareStatement(query);
				pstmt.setInt(1, rs.getInt("id_conv"));
		    	pstmt.setString(2, msg.getDate());
		    	pstmt.setString(3, msg.getContent());
		    	pstmt.setBoolean(4, msg.getSent());
				pstmt.executeUpdate();
				System.out.println("[DB] Inserted message in DB.");
			} else {
				System.out.println("[DB] Error : Conversation not found !");
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			if (con != null) {
	    		try {
	    			con.close();
	    			if (stmt != null)
	    				stmt.close();
	    			if (rs != null)
						rs.close();
	    			if (pstmt != null)
	    				pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    	}
		}
	}
	
	// To delete all conversations in DB
	public void deleteHistory() {
		history.clear();
		if (dbSet) {
			Connection con = null;
			Statement stmt = null;
			try {
				con = DriverManager.getConnection(url, user, pwd);
				stmt = con.createStatement();
				String query = "DELETE FROM message;";
				stmt.executeUpdate(query);
				query = "DELETE FROM conversation;";
				stmt.executeUpdate(query);
				System.out.println("[DB] Deleted history.");
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
		    	if (con != null) {
		    		try {
		    			con.close();
		    			if (stmt != null) {
		    				stmt.close();
		    			}
					} catch (SQLException e) {
						e.printStackTrace();
					}
		    	}
		    }
		}
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
	
	/*** To help the debug ***/
	
	public void printHistory () {
		ArrayList<Conversation> historique = this.getHistory();
		System.out.println("[DB] History : ");
		for(Conversation c : historique)
		{
			System.out.println("*** " + c.getDestinationUser().getPseudo() + " ***");
			for(Message m : c.getMessages())
			{
				System.out.println(m.getDate() + " : "+m.getContent());
			}
		}
	}
	
	public void debugConversations() {
		System.out.println("[DEBUG] Current list of opened conversations : ");
		for (Conversation c : this.currentConversations) {
			debugConversation(c);
		}
		System.out.println("[DEBUG] End of list");
	}
	
	public void debugConversation (Conversation c) {
		System.out.println("	[DEBUG] Conversation with " + c.getDestinationUser().getPseudo() + " :");
		for(Message m : c.getMessages())
		{
			if (m.getSent()) {
				System.out.println("		You : \"" + m.getContent() + "\"");
			}
			else{
				System.out.println("		" + c.getDestinationUser().getPseudo() + " : \"" + m.getContent() + "\"");
			}
		}
		System.out.println("	[DEBUG] End of conversation");
	}

	/*
	public static void main(String[] args)
	{
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		ConversationModel cm = new ConversationModel();
		
		cm.deleteHistory();
		cm.printHistory();
		
		/*
		User dest = new User("toto", null, 0);
		ArrayList<Message> messages = new ArrayList<Message>();
		messages.add(new Message(dateFormat.format(LocalDateTime.now()), "5", true));
		messages.add(new Message(dateFormat.format(LocalDateTime.now()), "6", false));
		messages.add(new Message(dateFormat.format(LocalDateTime.now()), "7", true));
		messages.add(new Message(dateFormat.format(LocalDateTime.now()), "8", false));
		
		
		Conversation conv = new Conversation(dest, dateFormat.format(LocalDateTime.now()), new ArrayList<Message>());
		
		cm.addConvToDB(conv);
		cm.printHistory();
		
		cm.addMsg(conv, "C'est Moi", false);
		cm.printHistory();
	} 
	*/
}
