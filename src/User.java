package poo_clavardage;
import java.lang.*;

public class User {

	/*** Attributes ***/
	protected String pseudo;
	protected int numPort;
	protected InetAddress address;
	
	/*** Constructors ***/
	public void User(String pseudo, int numPort, InetAddress address) {
		this.pseudo = pseudo;
		this.numPort = numPort;
		this.address = address;
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
	public void setNumPort(Int newNumPort){
		this.numPort = newNumPort;
	}
	public void setAddress(InetAddress newAddress){
		this.address = newAddress;
	}
}
