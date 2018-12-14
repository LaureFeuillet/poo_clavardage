import java.net.InetAddress;
import java.util.ArrayList;

public class UserModel {

	/*** Attributes ***/
	protected String myself;
	protected ArrayList<User> connectedUsers;
	
	/*** Constructors ***/
	public UserModel( ArrayList<User> connectedUsers) {
		// Here we need to get the list of all users active on the server.
		//this.connectedUsers = getAllConected(); // bref un truc comme ça
	}

	/*** Methods ***/

	// Method that creates a specific user and adds it to the list of the connected users
	public void addUser(String pseudo, InetAddress address, int numPort) {
		User user = new User(pseudo, address, numPort);
		this.connectedUsers.add(user);
	}

	// Method that removes a specific user from the list of the connected users
	public void deleteUser(User userToDelete) {
		this.connectedUsers.remove(userToDelete);
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
			this.setMyself(pseudoToCheck);
		}
		return available;
	}
 
	// Returns the user corresponding to a pseudo
	public User getUserByPseudo(String pseudo) {
		User goodUser = null;
		for(User user : this.connectedUsers) {
			if(user.getPseudo() == pseudo) {
				goodUser = user;
				break;
			}
		}
		return goodUser;
	}
	
	// Returns the user corresponding to a pseudo
	public User getUserByIP(InetAddress adr) {
		User goodUser = null;
		for(User user : this.connectedUsers) {
			if(user.getAddress() == adr) {
				goodUser = user;
				break;
			}
		}
		return goodUser;
	}
	
	// Depending on the action required, we either add, delete a user from the list of connected users or change its pseudo in it.
	public void refreshUser(User userToUpdate, Action action) {
		switch (action) {
        case CONNECT: 
        			this.connectedUsers.add(userToUpdate);
                 break;
        case DISCONNECT: 
        			deleteUser(userToUpdate);
                 break;
        case UPDATE: 
        			User updatedUser = null;
        			for(User user : this.connectedUsers) {
        				if((user.getAddress() == userToUpdate.getAddress()) && (user.getNumPort() == userToUpdate.getNumPort())) {
        					updatedUser = user;
        					updatedUser.setPseudo(userToUpdate.getPseudo());
        					break;
        				}
        			}
                 break;
		}
	}
	
	/*** Getters & setters ***/
	public String getMyself() {
		return myself;
	}
	public void setMyself(String myself) {
		this.myself = myself;
	}
	public ArrayList<User> getConnectedUsers() {
		return this.connectedUsers;
	}
	public void setConnectedUsers(ArrayList<User> connectedUsers) {
		this.connectedUsers = connectedUsers;
	}

}
