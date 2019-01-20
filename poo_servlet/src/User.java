import java.io.Serializable;
import java.net.InetAddress;

@SuppressWarnings("serial")
public class User implements Serializable{

	/*** Attributes ***/
	protected String pseudo; // Unique name for a session of a user 
	protected int numPort; // Number of the port the application is listening on
	protected InetAddress address; // IP address of the user
	
	/*** Constructors ***/
	public User(String pseudo, InetAddress address, int numPort) {
		this.pseudo = pseudo;
		this.numPort = numPort;
		this.address = address;
	}
	
	// Convert a User to an understanding String
	public String toString() {
		return this.pseudo + " " + this.address + " /" + this.numPort + " ";
	}
	
	// Convert a User to a JSON format
	public String toJson() {
		return "{ \"pseudo\": \"" + this.pseudo + "\", \"ip\":\"" + this.address + "\",\"port\": \"" + this.numPort + "\" }";
	}

	/*** Getters & setters ***/
	public String getPseudo(){
		return this.pseudo;
	}
	public int getNumPort(){
		return this.numPort;
	}
	public InetAddress getAddress(){
		return this.address;
	}
	public void setPseudo(String newPseudo){
		this.pseudo = newPseudo;
	}
	public void setNumPort(int newNumPort){
		this.numPort = newNumPort;
	}
	public void setAddress(InetAddress newAddress){
		this.address = newAddress;
	}
}
