import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class Network 
{
	private Controller controller = null;
	private HashMap<InetAddress,ClientThread> clients = null;
	private HashMap<InetAddress,ServerThread> servers = null;

	public Network(Controller c) 
	{
		this.controller = c;
		new ListenerThread(this);
	}
	
	public void addConv(User dest)
	{
		clients.put(dest.getAddress(), new ClientThread(controller, dest, dest.getAddress().toString()));
	}
	
	// At the launch of the application, we need to know all the connected users on the network
	public ArrayList<User> findConnectedUsers()
	{
		return null;
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
		private int nport = 1234;
		
		
	}

}

