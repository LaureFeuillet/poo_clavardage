import java.util.List;
import java.io.BufferedReader;
import java.net.NetworkInterface;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.Runtime;
import org.json.*;

public class Network {
	//These will be our IP address and the broadcast address corresponding to it
	private InetAddress local;
	private int localPort = 0;
	private final String urlServlet = "http://172.20.10.13:8080/poo_servlet/ClavardageServlet?action=";
	private String pseudo = "undefined";
	private Controller controller = null;
	//These correspond to the current launched conversations, clients have been started by us, servers by remote users
	private HashMap<InetAddress,ClientThread> clients = null;
	private HashMap<InetAddress,ServerThread> servers = null;
	private boolean alreadyConnected = false;

	public Network(Controller c) {
		clients = new HashMap<InetAddress,ClientThread>();
		servers = new HashMap<InetAddress,ServerThread>();
		local = null;
		this.controller = c;
		//Launches a "waiting for discussion initiated by remote users" thread
		new ListenerThread(this);
		//Used to get our local address and the broadcast address
		init();
		//Launches a thread that will refresh the connected users list every second
		new RefreshThread(this);
		new ExitThread();
	}
	
	//WARNING : VERY COMPLICATED ONE !!!
	//Just get our local address and the broadcast one (the last is useless in this version)
	private void init(){
		Enumeration<NetworkInterface> en = null;
		DatagramSocket sock = null;
		try {
			sock = new DatagramSocket(0);
			sock.setBroadcast(true);
		} catch (SocketException e) {}
		// Finding broadcast addresses, to be taken for granted !
		try {
			en = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {}
	    while (en.hasMoreElements()) {
	      NetworkInterface ni = en.nextElement();
	
	      List<InterfaceAddress> list = ni.getInterfaceAddresses();
	      Iterator<InterfaceAddress> it = list.iterator();
	
	      while (it.hasNext()) {
	        InterfaceAddress ia = it.next();
	        if (ia.getAddress().isSiteLocalAddress()) {
	        	//broadcast = ia.getBroadcast();
	        	local = ia.getAddress();
	        }
	      }
	    }
	    System.out.print("[INIT] Local address is : " + local.toString() + "\n");
	    //System.out.print("[INIT] Broadcast address is : " + broadcast.toString() + "\n");
	}
	
	/*****************************************************/
	/*********       APPLICATION METHODS       ***********/
	/*****************************************************/
	
	//Used to get the answer from the presence server as a JSON string 
	public static String get(String url) throws IOException{ 
		String result ="";
		URL urls = new URL(url);
		URLConnection urlsc = urls.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(urlsc.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			result +=inputLine;
		in.close();
		return result;
	}
	
	// At the launch of the application, we need to know all the connected users on the network
	public ArrayList<User> findConnectedUsers(){
		ArrayList<User> connectedUsers = new ArrayList<User>();
		JSONObject usersObject;
		JSONArray usersArray;
		String users = "";
		try {
			//Server request
			users = get(urlServlet + "USERS&pseudo=" + pseudo);
			//Turns the json string into objects easier to manipulate
			usersObject = new JSONObject(users);
			usersArray = usersObject.getJSONArray("Users");
			String pseudo = null;
			String ip = null;
			String port = null;
			//Creates a user object for each connected user
			for (int i = 0 ; i < usersArray.length() ; i++) {
				pseudo = new String(usersArray.getJSONObject(i).getString("pseudo"));
				ip = new String(usersArray.getJSONObject(i).getString("ip").substring(1));
				port = new String(usersArray.getJSONObject(i).getString("port"));
				User u = new User(pseudo, InetAddress.getByName(ip), Integer.parseInt(port));
				connectedUsers.add(u);
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return connectedUsers;
	}
	
	//Launches a client thread that initiates a conversation with a remote user
	public void addConv(User dest)
	{
    	System.out.print("[CLIENT] Conversation initiated with " + dest.getAddress().toString() + "...\n");
		//We save the reference to this thread in clients
		clients.put(dest.getAddress(), new ClientThread(controller, dest, dest.getAddress().toString()));
	}
	
	//Sends a message in an already opened conversation
	public void sendMsg(User dest, String content){
		//We need to know whether the conversation was started by us or by the user we are talking to
		//First we check if the conversation was started by us :
		ClientThread ct = clients.get(dest.getAddress());
		if (ct != null)
		{
			//If it was started by us, the message is transfered to the concerned client thread
			ct.send(content);
		} else{
			//else, it was started by the remote user so the message is transfered the message to the concerned server thread
			servers.get(dest.getAddress()).send(content);
		}
	}
	
	//Notifies the remote users that the local one has changed or set his pseudo
	public void notifyPseudo(String pseudo) {
		this.pseudo = pseudo;
		try {
			if(!alreadyConnected) {
				get(urlServlet + "CONNECT&pseudo=" + pseudo + "&port=" + localPort);
				alreadyConnected = true;
			} else {
				get(urlServlet + "UPDATE&pseudo=" + pseudo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Notifies the controller of the newest list of connected users
	public void refreshUsers() {
		controller.refreshUsers(findConnectedUsers());
	}
		
	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	public HashMap<InetAddress, ServerThread> getServers() {
		return servers;
	}
	
	
	/*****************************************************/
	/***************       THREADS       *****************/
	/*****************************************************/
	
	//A thread that makes a USERS request on the presence server every second in order to have the freshest data possible
	private class RefreshThread extends Thread{
		Network n = null;
		
		public RefreshThread(Network n) {
			this.n = n;
			start();
		}
		
		public void run() {
			while(true) {
				long startTime = System.currentTimeMillis();
				//Waits one second then performs the request
				while(System.currentTimeMillis() - startTime < 1000) {}
				n.refreshUsers();
			}	
		}
	}
	
	//A thread that is listening to remote users' requests for starting a conversation with us
	private class ListenerThread extends Thread
	{
		private Network net = null;
		private ServerSocket ss = null;
		private Socket sock = null;
		
		//Just creates a listening socket and executes the run method
		public ListenerThread(Network net)
		{
			try {
				ss = new ServerSocket(0);
				if(ss != null) {
					localPort = ss.getLocalPort();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.net = net; 
			this.start();
		}
		
		//Waits for a connection and delegates it to a ServerThread instance that will the handle the message exchanges
		//When a connection is created, the thread starts to listen again
		public void run()
		{			
			while(true){
		        try {
		        	sock = ss.accept();
		        	System.out.print("[LISTENER] Conversation request from " + sock.getInetAddress().toString() + "...\n");
		        	//The created ServerThread is added to the list of the current conversations started by remote users
		        	net.getServers().put(sock.getInetAddress(), new ServerThread(net.getController(), sock));
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}
	}
	
	//A thread that handles a communication initiated by a remote user
	private class ServerThread extends Thread{
		Controller c = null;
		Socket sock = null;
		BufferedReader in = null;
		PrintWriter out = null;
		
		//Takes an already connected socket as parameter
		public ServerThread(Controller c, Socket sock){
			this.c = c;
			this.sock = sock;
			try {
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = new PrintWriter(sock.getOutputStream(),true);
				c.startConversation(sock.getInetAddress());
				this.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run(){
			String input = null;
			// Simply waits for any message coming from the remote user then transmit them to the controller
			while(true)
			{
				try {
					input = in.readLine();
					if(input != null)
					{
						//Message is transfered to the controller
						c.receiveMsg(sock.getInetAddress(), input);
						System.out.println("[SERVER_THREAD] Received message : \"" + input + "\" from " + sock.getInetAddress() + ".");
					}
				} catch (IOException e) {
					System.out.println("[SERVER_THREAD] Ended conversation with : " +  sock.getInetAddress());
					break;
				}
			}	
		}
			
		//Called by Network, sends the given message to the remote user
		public void send(String content)
		{
			out.println(content);
		}
	}

	//A thread that handles a communication initiated by us
	private class ClientThread extends Thread{
		private Controller c = null;
		private User dest = null;
		private Socket sock = null;
		private BufferedReader in = null;
		private PrintWriter out = null;

		//Starts a communication with the given user, the name of the thread will be the IP of the remote user
		//So that sendMessage can then transfer the message to the thread by knowing the IP of the remote user
		public ClientThread(Controller c, User dest, String name){
			super(name);
			this.c = c;
			this.dest = dest;
			try {
				//Connection attempt
				//System.out.println("Port = " + dest.getNumPort());
				sock = new Socket(dest.getAddress(), dest.getNumPort());
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = new PrintWriter(sock.getOutputStream(),true);
				this.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run()
		{
			String input = null;
			// Simply waits for any message coming from the remote user then transmit them to the controller
			while(true)
			{
				try {
					input = in.readLine();
					if(input != null)
					{
						//Message is transfered to the controller
						c.receiveMsg(dest.getAddress(), input);
						System.out.println("[CLIENT_THREAD] Received message : \"" + input + "\" from " + dest.getAddress() + ".");

					}
				} catch (IOException e) {
					System.out.println("[CLIENT_THREAD] Ended conversation with : " + dest.getAddress());
					break;
				}
			}			
		}
		
		//Called by Network, sends the given message to the remote user
		public void send(String content)
		{
			out.println(content);
		}
	}
	
	//Used to send a message to the connected users when exiting the application
	private class ExitThread extends Thread{
		
		public ExitThread() {
			super("EXIT THREAD");
			//This is used to specify that this thread should be executed on exiting the app
			Runtime.getRuntime().addShutdownHook(this);
		}
		
		public void run() {
			sendExitMessage();
		}
		
		//Performs a broadcast message
		private void sendExitMessage() {
			try {
				get(urlServlet + "DISCONNECT" + "&pseudo=" + pseudo);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

