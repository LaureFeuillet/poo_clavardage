import java.util.List;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.NetworkInterface;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
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
	//This port is common to every user using the application, it corresponds to the destination port of every broadcast
	//private final int PORT_WATCHDOG = 17171;
	//Size of the packets sent by the application
	//private final int PACKET_SIZE = 1024;
	private final String urlServlet = "http://localhost:8080/poo_servlet/ClavardageServlet?action=";
	private String pseudo = "undefined";
	private Controller controller = null;
	//These correspond to the current launched conversations, clients have been started by us, servers by remote users
	private HashMap<InetAddress,ClientThread> clients = null;
	private HashMap<InetAddress,ServerThread> servers = null;
	private ExitThread et = null;
	private boolean alreadyConnected = false;

	public Network(Controller c) {
		clients = new HashMap<InetAddress,ClientThread>();
		servers = new HashMap<InetAddress,ServerThread>();
		local = null;
		this.controller = c;
		//Launches a "waiting for discussion initiated by remote users" thread
		new ListenerThread(this);
		//Launches a thread that will handle every broadcast messages sent by remote users
		//Used to get our local address and the broadcast address
		init();
		//new WatchdogThread(this);
		et = new ExitThread();
	}
	
	//WARNING : VERY COMPLICATED ONE !!!
	//Just get our local address and the broadcast one
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
			users = get(urlServlet + "USERS");
			usersObject = new JSONObject(users);
			usersArray = usersObject.getJSONArray("Users");
			String pseudo = null;
			String ip = null;
			String port = null;
			for (int i = 0 ; i < usersArray.length() ; i++) {
				pseudo = new String(usersArray.getJSONObject(i).getString("pseudo"));
				ip = new String(usersArray.getJSONObject(i).getString("ip").substring(1));
				port = new String(usersArray.getJSONObject(i).getString("port"));
				System.out.println("User : " + pseudo + " " + ip + " " + port);
				//User u = new User(usersArray.getJSONObject(i).getString("pseudo"), InetAddress.getByName(usersArray.getJSONObject(i).getString("ip")), Integer.parseInt(usersArray.getJSONObject(i).getString("port")));
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
			// If 
			if(!alreadyConnected) {
				get(urlServlet + "CONNECT&pseudo=" + pseudo + "&port=" + localPort);
				alreadyConnected = true;
			} else {
				get(urlServlet + "UPDATE&pseudo=" + pseudo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		DatagramPacket sentPacket = null;
		DatagramSocket s = null;
		try {
			//Creates a broadcast UDP socket
			s = new DatagramSocket(0);
			s.setBroadcast(true);
		} catch (SocketException e) {}
		byte[] dataToSend = createMessageUser(pseudo);
		//Sends the packet containing our pseudo the remote users
        sentPacket = new DatagramPacket(dataToSend, dataToSend.length, broadcast, PORT_WATCHDOG);
        try {
			s.send(sentPacket);
		} catch (IOException e) {}
		*/
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
	/*
	//A thread that catches all broadcast messages, runs on PORT_WATCHDOG port
	private class WatchdogThread extends Thread{
		DatagramSocket sock;
		DatagramPacket receivedPacket = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
		DatagramPacket sentPacket = null;
		Network n = null;
		
		public WatchdogThread(Network n) {
			this.n = n;
			sock = null;
			//System.out.print("[WATCHDOG] Starting watchdog...\n");
			start();
		}
		
		public void run() {
			User u;
			while(sock == null) {
				try {
					//Starts the watchdog on port PORT_WATCHDOG
					sock = new DatagramSocket(PORT_WATCHDOG);
					//System.out.print("[WATCHDOG] Socket created...\n");
					
				} catch (SocketException e) {}
			}
			while(true) {
				try {
					//Waits for any broadcast packet
					sock.receive(receivedPacket);
					if (receivedPacket.getAddress().toString().compareTo(local.toString()) != 0) {
						//Gets the content of the packet
						String data = new String(receivedPacket.getData(),0,receivedPacket.getLength());
						switch(data) {
						//This happens when a remote user connects and sends a request to find the already connected users
						case "CONNECT":
							System.out.print("[WATCHDOG] Received request from" + receivedPacket.getAddress() + "...\n");
							//We start by answering his request and telling him that we are here
							byte[] dataToSend = createMessageUser(pseudo);
							sentPacket = new DatagramPacket(dataToSend,dataToSend.length,receivedPacket.getAddress(), receivedPacket.getPort());
							sock.send(sentPacket);
							//A this moment, the remote user does not yet have a pseudo so its set to null, the port is set to
							//0 and will be updated when the remote user chooses his pseudo
							u = new User("undefined",receivedPacket.getAddress(), 0);
							//The controller is noticed that a new user has actually connected
							n.getController().refreshUser(u, Action.CONNECT);
							break;
						//This happens when a remote user closes the application, before actually exiting, he notifies 
						//all the other users that he is leaving
						case "DISCONNECT":
							System.out.print("[WATCHDOG] Received disconnect from" + receivedPacket.getAddress() + "...\n");
							//The pseudo is set to null because we have no way to know it at this point,
							//anyway, it is not required to remove the user from our contacts
							u = new User(null,receivedPacket.getAddress(), receivedPacket.getPort());
							//The controller is notified that a user is leaving
							n.getController().refreshUser(u, Action.DISCONNECT);
							break;
						//This happens when a remote user updates his pseudo, note that this usually happens right after
						//receiving a CONNECT packet from the same user so that we can actually identify him by something more
						//user friendly than his IP
						default:
							//The user is contained in the data of the packet
						    u = ReceiveMessageUser(receivedPacket.getData());
						    System.out.print("[WATCHDOG] Received pseudo \"" + u.getPseudo() + "\" from " + receivedPacket.getAddress() + "...\n");
							//The controller is notified that the user behind an IP address that we already know has
							//changed his pseudo
							n.getController().refreshUser(u, Action.UPDATE);
							break;
						}
					}
				} catch (IOException e) {}
			}
		}
	}
	*/
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
			/*
			DatagramPacket sentPacket = null;
			DatagramSocket s = null;
			try {
				//Creates a broadcast UDP socket
				s = new DatagramSocket(0);
				s.setBroadcast(true);
			} catch (SocketException e) {}
			//Sends the packet containing our pseudo the remote users
	        sentPacket = new DatagramPacket("DISCONNECT".getBytes(), "DISCONNECT".length(), broadcast, PORT_WATCHDOG);
	        try {
	        	System.out.println("[EXIT THREAD] Sending DISCONNECT message");
				s.send(sentPacket);
			} catch (IOException e) {}
			*/
		}
	}
	
	/*****************************************************/
	/***************        TOOLS        *****************/
	/*****************************************************/
	/*
	//Used to send a User object in a packet
	public byte[] createMessageUser(String pseudo) {
		//Prepare Data
        User u = new User(pseudo,local,localPort);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        try {
			oos.writeObject(u);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        return baos.toByteArray();
	}

	//Used to get a User object from a packet
	public static User ReceiveMessageUser(byte[] buf) {
		//Getting the object MessageSync
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        ObjectInputStream ois = null;
        User u = null;
		try {
			ois = new ObjectInputStream(bais);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        try {
            Object readObject = ois.readObject();
            if (readObject instanceof User) {
            	u = (User) readObject;
            } else {
                System.out.println("The object is not a MessageSync");
            }
        } catch (Exception e) {
            System.out.println("There are no object in this packet");
        }
        return u;
	}
	*/
}

