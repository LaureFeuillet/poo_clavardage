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

	/*** Constructors ***/
	public ConversationModel() {
		currentConversations = new ArrayList<Conversation>();
		currentConv = null;
		// Establish a connection to the database
		// to get all previous conversations for history
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		PreparedStatement pstmt = null;
	    ResultSet rsMsg = null;
		try {
		      Class.forName("com.mysql.cj.jdbc.Driver");
		      System.out.println("Driver OK");
		      
		      con = DriverManager.getConnection(url, user, pwd);
		      System.out.println("[DB]Connection established !");
		      
		      stmt = con.createStatement();
		      
		      // Create in DB table Conversation 
		      query = "CREATE TABLE IF NOT EXISTS conversation ("
		      		+ " id_conv SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,"
		      		+ " pseudo VARCHAR(50) NOT NULL,"
		      		+ " starting_date VARCHAR(50) NOT NULL,"
		      		+ " PRIMARY KEY (id_conv))"
		      		+ " ENGINE=INNODB;";
		      stmt.executeUpdate(query);
		      
		      // Create in DB table Message
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
		      history = new ArrayList<Conversation>();
		      // First, we get all previous conversations
		      query = "SELECT pseudo, starting_date"
		    		+ " FROM conversation";
		      rs = stmt.executeQuery(query);
		      
		      String pseudo = null;
		      String startingDate = null;
		      
		      ArrayList<Message> messages = new ArrayList<Message>();
		      while (rs.next()){
		    	  pseudo = rs.getString("pseudo");
		    	  startingDate = rs.getString("starting_date");
		    	  query = "SELECT date, content, sent"
		    	  		+ " FROM message"
		    	  		+ " INNER JOIN conversation"
		    	  		+ " ON message.conv = conversation.id_conv"
		    	  		+ " WHERE conversation.pseudo = ?"
		    	  		+ " AND conversation.starting_date = ?;";

		    	  pstmt = con.prepareStatement(query);
		    	  pstmt.setString(1, pseudo);
		    	  pstmt.setString(2, startingDate);
		    	  rsMsg = pstmt.executeQuery();
		    	  
		    	  messages.clear();
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
						if (rs != null)
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
	
	// Creates a new conversation, add to the DB, makes it THE current one, and adds it to the list of current convs.
	public void startConv(User userConcerned) {
		Conversation conv = new Conversation(userConcerned);
		addConvToDB(conv);
		this.currentConversations.add(conv);
		this.currentConv = conv;
	}

	// Deletes all conversations in DB
	public void deleteHistory(){
		history.clear();

		Connection con = null;
		Statement stmt = null;
		
		try {
			con = DriverManager.getConnection(url, user, pwd);
			System.out.println("[DB]Connection established to delete history.");
			stmt = con.createStatement();
			String query = "DELETE FROM message";
			stmt.executeUpdate(query);
			query = "DELETE FROM conversation";
			stmt.executeUpdate(query);
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

	// Adds a specific conversation in DB
	public void addConvToDB(Conversation conv){
		history.add(conv);
		
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DriverManager.getConnection(url, user, pwd);
			System.out.println("[DB]Connection established to add a conv to history.");
			String query = "INSERT INTO conversation"
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
			System.out.println("[DB]Connection established to add a msg to history.");
			System.out.println("pseudo : " + conv.getDestinationUser().getPseudo());
			System.out.println("starting_date : " + conv.getStartingDate());
			query="SELECT id_conv FROM conversation"
				+ " WHERE pseudo = '" + conv.getDestinationUser().getPseudo() + "'"
				+ " AND starting_date = '" + conv.getStartingDate().toString() + "'";
			System.out.println("1");
			stmt = con.createStatement();
			// rs store the id_conv 
			System.out.println("2");
			rs = stmt.executeQuery(query);
			System.out.println("3");
			if (rs != null) {
				rs.next();
				System.out.println("[DB]On a trouvé la bonne conv.");
				// Then we can add the msg in DB
				query = "INSERT INTO message"
						+ " VALUES (NULL, ?, ?, ?, ?);";
				pstmt = con.prepareStatement(query);
				pstmt.setInt(1, rs.getInt("id_conv"));
		    	pstmt.setString(2, msg.getDate());
		    	pstmt.setString(3, msg.getContent());
		    	pstmt.setBoolean(4, msg.getSent());
				pstmt.executeUpdate();
			} else {
				System.out.println("[DB : Erreur]");
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
	

	public void addMsg (Conversation convToUpdate, String content, boolean sent){
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		Message newMsg = new Message(dateFormat.format(LocalDateTime.now()),content, sent);
		addMsgToDB(convToUpdate, newMsg);
		for(Conversation conv : this.currentConversations) {
			if(conv == convToUpdate) {
				conv.messages.add(newMsg);
				break;
			}
		}
		for(Conversation conv : this.history) {
			if(conv == convToUpdate) {
				conv.messages.add(newMsg);
				break;
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
	
	public void printHistory () {
		ArrayList<Conversation> historique = this.getHistory();
		System.out.println("[DB]Historique : ");
		for(Conversation c : historique)
		{
			System.out.println("*** " + c.getDestinationUser() + "***");
			for(Message m : c.getMessages())
			{
				System.out.println(m.getDate() + " : "+m.getContent());
			}
		}
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
