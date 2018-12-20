public class Agent {

	public static void main(String[] args) {
		Controller c = new Controller();
		for (User u : c.test()) {
			System.out.println(u.getAddress().toString());
		}
	}
	
}
