import java.util.Scanner;

public class Server {

	
	public static void main(String[] args) {
		String message;
		Controller c = new Controller();
		Scanner input = new Scanner(System.in);
		while(true) {
			message = input.nextLine();
			c.sendMsg("0", message);
			User u = c.um.getUserByPseudo("0");
			Conversation conv = c.cm.getConvByUser(u);
			System.out.println(conv.messages.get(conv.messages.size()-1).content);
		}
	}
}
