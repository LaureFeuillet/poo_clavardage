package poo_clavardage;
import java.lang.*;

public class UserModel {

	/*** Attributes ***/
	protected ArrayList<User> connectedUsers;
	
	/*** Constructors ***/
	public void UserModel( ArrayList<User> connectedUsers) {
		// Here we need to get the list of all users active on the server.
		this.connectedUsers = getAllConected(); // bref un truc comme ça
	}

	/*** Methods ***/
	// Method that returns the list of connected users, with their name, IP@ and port
	public ArrayList<User> getConnectedUsers() {
		// This might be done by something else, and thus, we would not need an attribute for it.
		return this.connectedUsers;
	}

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

	/*** Getters & setters ***/

}
