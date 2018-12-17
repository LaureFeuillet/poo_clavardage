import java.util.Scanner;

public class Agent {

	public static void main(String[] args) {
		String message;
		Controller c = new Controller();
		Scanner input = new Scanner(System.in);
		c.startConversation("0", true);
		while(true) {
			message = input.nextLine();
			c.sendMsg("0", message);
		}
	}
	
}
