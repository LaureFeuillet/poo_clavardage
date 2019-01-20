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
	
	// List of the connected users
	private ArrayList<User> users;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClavardageServlet() {
        super();
        // At the launch of the servlet, there is nobody connected
        users = new ArrayList<User>();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter pw = response.getWriter();
		
		try {
			// There are 4 possible actions with the servlet : CONNECT, DISCONNECT, UPDATE and USERS.
			String action = request.getParameter("action");
			String pseudo = null;
			InetAddress ip = null;
			int port = 0;
			
			switch(action) {
				// The client wants to connect with the given pseudo and at the given n°port.
				// The ip address is automatically found. 
				// ?action=CONNECT&pseudo=...&port=...
				case "CONNECT":
					System.out.println();
					pseudo = new String(request.getParameter("pseudo"));
					port = Integer.parseInt(request.getParameter("port"));
					
					ip = InetAddress.getByName(request.getRemoteAddr());
					
					// We build the new User
					User newUser = new User(pseudo, ip, port);
					if(users.contains(newUser)) {
						System.out.println("[SERVLET] User already registered");
					} else {
						System.out.println("[SERVLET] CONNECT : " + newUser.toString());
						users.add(newUser);
						System.out.println(jsonUsers(pseudo));
					}
					break;
				// The client want to disconnect and gives his unique pseudo
				// ?action=DISCONNECT&pseudo=...
				case "DISCONNECT":
					System.out.println("[SERVLET] DISCONNECT");
					pseudo = new String(request.getParameter("pseudo"));
					ip = InetAddress.getByName(request.getRemoteAddr());
					port = request.getRemotePort();
					User userToDelete = null;
					//System.out.println(userToDelete.toString());
					// We remove the corresponding user from the list of connected users.
					for(User u : this.users) {
						// The condition on pseudo is sufficient, each pseudo beeing unique.
						if(u.getPseudo().equals(pseudo)) {
							userToDelete=u;
							System.out.println("[SERVLET] DISCONNECT : " + u.toString());
							System.out.println(jsonUsers(pseudo));
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
					pseudo = new String(request.getParameter("pseudo"));
					ip = InetAddress.getByName(request.getRemoteAddr());
					port = request.getRemotePort();
					for (User u : users) {
						// The condition on the ip address is sufficient, because there is suppose to be one client by machine
						if(u.getAddress().equals(ip)) {
							System.out.println("[SERVLET] UPDATE : " + pseudo);
							u.setPseudo(pseudo);
							System.out.println(jsonUsers(pseudo));
							break;
						}
					}
					System.out.println(stringUsers());
					//pw.append(jsonUsers());
					break;
				// The client wants to know all the connected users except himself, so he gives his pseudo
				// ?action=USERS&pseudo=...
				case "USERS":
					System.out.println("[SERVLET] USERS");
					System.out.println(jsonUsers(pseudo));
					pseudo = new String(request.getParameter("pseudo"));
					pw.append(jsonUsers(pseudo));
					break;
				default: 
					System.out.println("[SERVLET] Don't understand request.");
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
		boolean first = true;
		for(User u : this.users) {
			if(first) {
				usersToString += u.toString();
				first = false;
			} else {
				usersToString += " --- " + u.toString();
			}
		}
		usersToString = usersToString + " *** ";
		return usersToString;
	}
	
	// Return the JSON form of the list of users
	protected String jsonUsers(String pseudo) {
		String usersToJson = "{\"Users\": [ ";
		Iterator<User> iter = users.iterator();
		User u = null;
		boolean first = true;
		while(iter.hasNext() && first) {
			u=iter.next();
			if(!u.getPseudo().equals(pseudo)) {
				usersToJson += u.toJson();
				first = false;
			}
		}
		while(iter.hasNext()) {
			u=iter.next();
			if(!u.getPseudo().equals(pseudo)) {
				usersToJson += ", " + u.toJson();
			}
		}
		usersToJson = usersToJson + "]}";
		return usersToJson;
		
	}

}
