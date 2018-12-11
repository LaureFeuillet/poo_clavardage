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
	public getPseudo(){
		return this.pseudo;
	}
	public getNumPort(){
		return this.numPort;
	}
	public getAddress(){
		return this.address;
	}
	public setPseudo(String newPseudo){
		this.pseudo = newPseudo;
	}
	public setNumPort(Int newNumPort){
		this.numPort = newNumPort;
	}
	public setAddress(InetAddress newAddress){
		this.address = newAddress;
	}
}
