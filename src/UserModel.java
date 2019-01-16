import java.net.InetAddress;
import java.util.ArrayList;

public class UserModel {

	/*** Attributes ***/
	protected String myself;
	protected ArrayList<User> connectedUsers;
	
	/*** Constructors ***/
	public UserModel( ArrayList<User> Cu) {
		this.connectedUsers = new ArrayList<User>();
		//this.connectedUsers = connectedUsers;
		for(User u : Cu) {
			System.out.println("[DEBUG] Added " + u.getPseudo() + " to the list of connected users.");
			this.connectedUsers.add(u);
		}
	}

	/*** Methods ***/

	// Method that removes a specific user from the list of the connected users
	public void deleteUser(User userToDelete) {
		this.connectedUsers.remove(userToDelete);
	}

	// Returns TRUE if we can use this pseudo, false if its already used.
	public boolean availablePseudo(String pseudoToCheck){
		boolean available = true;
		for(User user : this.connectedUsers)
		{
			if(user.getPseudo().equals(pseudoToCheck))
			{
				available = false;
			}
		}
		if(available) {
			if (!pseudoToCheck.equals("undefined"))
				this.setMyself(pseudoToCheck);
			else
				available = false;
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
			if(user.getAddress().toString().compareTo(adr.toString()) == 0) {
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
        			System.out.println("[DEBUG] Added " + userToUpdate.getPseudo() + " to the list of connected users.");
        			this.connectedUsers.add(userToUpdate);
                 break;
        case DISCONNECT: 
        			userToUpdate = getUserByIP(userToUpdate.getAddress());
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
	
	public void debugUsers() {
		System.out.println("[DEBUG] current list of connected users :");
		for (User u : connectedUsers) {
			System.out.println(u.getPseudo() + " " + u.getAddress() + "@" + u.getNumPort());
		}
		System.out.println("[END OF DEBUG]");
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
