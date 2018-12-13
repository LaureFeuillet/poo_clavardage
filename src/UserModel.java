import java.net.InetAddress;
import java.util.ArrayList;

public class UserModel {

	/*** Attributes ***/
	protected User myself;
	protected ArrayList<User> connectedUsers;
	
	/*** Constructors ***/
	public UserModel( ArrayList<User> connectedUsers) {
		// Here we need to get the list of all users active on the server.
		//this.connectedUsers = getAllConected(); // bref un truc comme ça
	}

	/*** Methods ***/

	// Method that creates a specific user and adds it to the list of the connected users
	public ArrayList<User> addUser(String pseudo, InetAddress address, int numPort) {
		User user = new User(pseudo, address, numPort);
		connectedUsers.add(user);
		return this.connectedUsers;
	}

	// Method that removes a specific user from the list of the connected users
	public ArrayList<User> deleteUser(User userToDelete) {
		connectedUsers.remove(userToDelete);
		return this.connectedUsers;
	}

	// Returns TRUE if we can use this pseudo, false if its already used.
	public boolean availablePseudo(String pseudoToCheck){
		boolean available = true;
		for(User user : this.connectedUsers)
		{
			if(user.getPseudo() == pseudoToCheck)
			{
				available = false;
			}
		}
		if(available) {
			
		}
		return available;
	}
 
	/*** Getters & setters ***/
	public User getMyself() {
		return myself;
	}

	public void setMyself(User myself) {
		this.myself = myself;
	}
	public ArrayList<User> getConnectedUsers() {
		return this.connectedUsers;
	}
	public void setConnectedUsers(ArrayList<User> connectedUsers) {
		this.connectedUsers = connectedUsers;
	}

}
