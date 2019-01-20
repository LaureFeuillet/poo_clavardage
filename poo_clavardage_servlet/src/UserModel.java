import java.net.InetAddress;
import java.util.ArrayList;

public class UserModel {

	/*** Attributes ***/
	protected String myself;
	protected ArrayList<User> connectedUsers;
	
	/*** Constructors ***/
	public UserModel( ArrayList<User> cU) {
		connectedUsers = cU;
		debugUsers();
	}

	/*** Methods ***/

	// Method that removes a specific user from the list of the connected users
	public void deleteUser(User userToDelete) {
		System.out.println("[DEBUG] REMOVING " + userToDelete.getPseudo() + " from the list of connected users.");
		this.connectedUsers.remove(userToDelete);
	}
	
	public void addUser(User userToAdd) {
		System.out.println("[DEBUG] ADDING " + userToAdd.getPseudo() + " to the list of connected users.");
		connectedUsers.add(userToAdd);
	}
	
	public void refreshUsers(ArrayList<User> users) {
		for (User newU : users) {
			boolean found = false;
			for (User u : connectedUsers) {
				if (newU.getAddress().toString().equals(u.getAddress().toString())) {
					found = true;
					if (!newU.getPseudo().equals(u.getPseudo())) {
						u.setPseudo(newU.getPseudo());
					}
				}
			}
			if (!found)
				addUser(newU);
		}
		for (User u : connectedUsers) {
			boolean found = false;
			for (User newU : users) {
				if (newU.getAddress().toString().equals(u.getAddress().toString())) {
					found = true;
				}
			}
			if (!found)
				deleteUser(u);
		}
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
			if(user.getPseudo().equals(pseudo)) {
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
			if(user.getAddress().toString().equals(adr.toString())) {
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
        			addUser(userToUpdate);
                 break;
        case DISCONNECT: 
        			userToUpdate = getUserByIP(userToUpdate.getAddress());
        			deleteUser(userToUpdate);
                 break;
        case UPDATE: 
        			User updatedUser = null;
        			boolean found = false;
        			for(User user : this.connectedUsers) {
        				if(user.getAddress().toString().equals(userToUpdate.getAddress().toString())) {
        					updatedUser = user;
        					updatedUser.setPseudo(userToUpdate.getPseudo());
        					updatedUser.setNumPort(userToUpdate.getNumPort());
        					found = true;
        					break;
        				}
        			}
        			//If somehow the user was not already known (typically when he is not answering the discovery broadcast)
        			//We simply add him to the list
        			if (found == false) {
        				System.out.println("[DEBUG] The user " + userToUpdate.getPseudo() + " was not found in the list.");
        				addUser(userToUpdate);
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
