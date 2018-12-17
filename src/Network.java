import java.util.List;
import java.io.BufferedReader;
import java.net.NetworkInterface;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;



public class Network 
{
	private final int PORT_WATCHDOG = 2000;
	
	private Controller controller = null;
	private HashMap<InetAddress,ClientThread> clients = null;
	private HashMap<InetAddress,ServerThread> servers = null;

	public Network(Controller c) 
	{
		this.controller = c;
		new ListenerThread(this);
		new WatchdogThread(this);
	}
	
	public void addConv(User dest)
	{
		clients.put(dest.getAddress(), new ClientThread(controller, dest, dest.getAddress().toString()));
	}
	
	// At the launch of the application, we need to know all the connected users on the network
	public ArrayList<User> findConnectedUsers()
	{
		int i = 0;
		DatagramPacket receivedPacket = null, sentPacket;
		DatagramSocket s = null;
		ArrayList<User> connectedUsers = new ArrayList<User>();
		Enumeration<NetworkInterface> en = null;
		long startTime;
		
		try {
			s = new DatagramSocket(0);
			s.setBroadcast(true);
		} catch (SocketException e) {
		}
		// Finding broadcast addresses, to be taken for granted !
		try {
			en = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {
		}
	    while (en.hasMoreElements()) {
	      NetworkInterface ni = en.nextElement();

	      List<InterfaceAddress> list = ni.getInterfaceAddresses();
	      Iterator<InterfaceAddress> it = list.iterator();

	      while (it.hasNext()) {
	        InterfaceAddress ia = it.next();
	        sentPacket = new DatagramPacket(null,0,ia.getBroadcast(), PORT_WATCHDOG);
	        try {
				s.send(sentPacket);
			} catch (IOException e) {
			}
	      }
	    }
	    // End of broadcast addresses
		
	    startTime = System.currentTimeMillis();
	    while(System.currentTimeMillis() - startTime < 500) {
	    	try {s.receive(receivedPacket);} catch (IOException e) {}
	    	User u = new User(Integer.toString(i),receivedPacket.getAddress(), receivedPacket.getPort());
	    	connectedUsers.add(u);
			sentPacket = new DatagramPacket(null,0,u.getAddress(), u.getNumPort());
			try {s.send(sentPacket);} catch (IOException e1) {}
			i++;
		}
		return connectedUsers;
	}
	
	public void sendMsg(User dest, String content)
	{
		ClientThread ct = clients.get(dest.getAddress());
		if (ct != null)
		{
			ct.send(content);
		} else
		{
			servers.get(dest.getAddress()).send(content);
		}
	}
		
	public Controller getController() 
	{
		return controller;
	}

	public void setController(Controller controller) 
	{
		this.controller = controller;
	}
	
	public HashMap<InetAddress, ServerThread> getServers() 
	{
		return servers;
	}

	private class ClientThread extends Thread
	{
		private Controller c = null;
		private User dest = null;
		private Socket sock = null;
		private BufferedReader in = null;
		private PrintWriter out = null;

		public ClientThread(Controller c, User dest, String name)
		{
			super(name);
			this.c = c;
			this.dest = dest;
			try {
				sock = new Socket(dest.getAddress(), dest.getNumPort());
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = new PrintWriter(sock.getOutputStream(),true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.start();
		}
		
		public void run()
		{
			String input = null;
			
			// Loop read, then tell to controller
			while(true)
			{
				try {
					input = in.readLine();
					if(input != null)
					{
						c.receiveMsg(dest.getAddress(), input);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}
		
		public void send(String content)
		{
			out.println(content);
		}
	}	
	
	private class ListenerThread extends Thread
	{
		private Network net = null;
		private ServerSocket ss = null;
		private Socket sock = null;
		
		public ListenerThread(Network net)
		{
			try {
				ss = new ServerSocket(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.net = net; 
			this.start();
		}
		
		public void run()
		{			
			while(true){
		        try {
		            sock = ss.accept();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        net.getServers().put(sock.getInetAddress(), new ServerThread(net.getController(), sock));
		    }
		}
	}
	
	private class ServerThread
	{
		Controller c = null;
		Socket sock = null;
		BufferedReader in = null;
		PrintWriter out = null;
		
		public ServerThread(Controller c, Socket sock)
		{
			this.c = c;
			this.sock = sock;
			try {
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = new PrintWriter(sock.getOutputStream(),true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run()
		{
			String input = null;
			
			// Loop read, then tell to controller
			while(true)
			{
				try {
					input = in.readLine();
					if(input != null)
					{
						c.receiveMsg(sock.getInetAddress(), input);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}
		
		public void send(String content)
		{
			out.println(content);
		}
	}
	
	private class WatchdogThread extends Thread
	{
		DatagramSocket sock;
		DatagramPacket receivedPacket, sentPacket;
		Network n = null;
		
		public WatchdogThread(Network n) {
			this.n = n;
			start();
		}
		
		public void run() {
			try {
				sock = new DatagramSocket(PORT_WATCHDOG);
			} catch (SocketException e) {}
			while(true) {
				try {
					sock.receive(receivedPacket);
					User u;
					String data = receivedPacket.getData().toString();
					switch(data) {
					case "CONNECT":
						u = new User("",receivedPacket.getAddress(), receivedPacket.getPort());
						n.getController().refreshUser(u, Action.CONNECT);
						break;
					case "DISCONNECT":
						u = new User("",receivedPacket.getAddress(), receivedPacket.getPort());
						n.getController().refreshUser(u, Action.DISCONNECT);
						break;
					default:
						u = new User(data, receivedPacket.getAddress(), receivedPacket.getPort());
						n.getController().refreshUser(u, Action.UPDATE);
						break;
					}
					sentPacket = new DatagramPacket(null,0,receivedPacket.getAddress(), receivedPacket.getPort());
					sock.send(sentPacket);
				} catch (IOException e) {}
			}
		}
	}
}

