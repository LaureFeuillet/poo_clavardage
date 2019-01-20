import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ClavardageServlet
 */
@WebServlet("/ClavardageServlet")
public class ClavardageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private ArrayList<User> users;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClavardageServlet() {
        super();
        users = new ArrayList<User>();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter pw = response.getWriter();
		
		try {
			String action = request.getParameter("action");
			String pseudo = null;
			InetAddress ip = null;
			int port = 0;
			
			switch(action) {
			// The client wants to connect with the given pseudo
			// ?action=CONNECT&pseudo=...&port=...
			case "CONNECT":
				System.out.println("CONNECT *** ");
				pseudo = new String(request.getParameter("pseudo"));
				port = Integer.parseInt(request.getParameter("port"));
				
				//ip = InetAddress.getByName("10.188.208.182");
				ip = InetAddress.getByName(request.getRemoteAddr());
				
				User newUser = new User(pseudo, ip, port);
				if(users.contains(newUser)) {
					System.out.println("[SERVLET] User already registered");
				} else {
					System.out.println(newUser.toString());
					users.add(newUser);
					System.out.println(stringUsers());
				}
				break;
			// The client want to disconnect
			// ?action=DISCONNECT&pseudo=...
			case "DISCONNECT":
				System.out.println("DISCONNECT *** ");
				pseudo = new String(request.getParameter("pseudo"));
				ip = InetAddress.getByName(request.getRemoteAddr());
				port = request.getRemotePort();
				System.out.println(pseudo + " " + ip + " /" + port);
				User userToDelete = null;
				//System.out.println(userToDelete.toString());
				// We remove the corresponding user from the list of connected users.
				for(User u : this.users) {
					// The condition on pseudo is sufficient, each pseudo beeing unique.
					if(u.getPseudo().equals(pseudo)) {
						System.out.println(u.toString());
						userToDelete=u;
						System.out.println("[SERVLET] We found the user to remove.");
						break;
					}
				}
				users.remove(userToDelete);
				System.out.println(stringUsers());
				//pw.append(jsonUsers());
				break;
			// The client wants to change his pseudo to the given one
			// // ?action=UPDATE&pseudo=...
			case "UPDATE": 
				System.out.println("UPDATE *** ");
				pseudo = new String(request.getParameter("pseudo"));
				ip = InetAddress.getByName(request.getRemoteAddr());
				port = request.getRemotePort();
				System.out.println(pseudo + " " + ip + " /" + port);
				for (User u : users) {
					// The condition on the ip address is sufficient, because there is only a client by machine
					if(u.getAddress().equals(ip)) {
						System.out.println("[SERVLET] We found the user to update.");
						u.setPseudo(pseudo);
						System.out.println(u.toString());
						break;
					}
				}
				System.out.println(stringUsers());
				//pw.append(jsonUsers());
				break;
			// The client wants to know all the connected users
			// ?action=USERS
			case "USERS":
				System.out.println("USERS *** ");
				pw.append(jsonUsers());
				break;
			default: 
				System.out.println("[SERVLET]Don't understand request.");
			}
		}
		catch(NullPointerException e) {
			System.out.println(" *** Fail try/catch *** ");
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	// Return a String with all the connected users
	protected String stringUsers() {
		String usersToString = "*** Users : ";
		for(User u : this.users) {
			usersToString = usersToString + u.toString() + " --- ";
		}
		usersToString = usersToString + " *** ";
		return usersToString;
	}
	
	// Return the JSON form of the list of users
	protected String jsonUsers() {
		String usersToJson = "{\"Users\": [ ";
		Iterator<User> iter = users.iterator();
		User u = null;
		if(iter.hasNext()) {
			u=iter.next();
			usersToJson += u.toJson();
		}
		while(iter.hasNext()) {
			u=iter.next();
			usersToJson += ", " + u.toJson();
		}
		usersToJson = usersToJson + "]}";
		return usersToJson;
		
	}

}
