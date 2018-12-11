package poo_clavardage;
import java.lang.*;

public class UserModel {

	/*** Attributes ***/
	protected User[] connectedUsers;
	
	/*** Constructors ***/
	public void UserModel( User[] connectedUsers) {
		// Here we need to get the list of all users active on the server.
		this.connectedUsers = ???;
	}

	/*** Methods ***/
	public User[] getConnectedUsers() {
		// This might be done by something else, and thus, we would not need an attribute for it.
		return this.connectedUsers;
	}

	public void add(String pseudo, InetAddress address, int numPort) {
		User user = new User(pseudo, address, numPort);
	}

	public void delete(User userToDelete) {
		userToDelete = null;
	}

	/*** Getters & setters ***/
	
}
