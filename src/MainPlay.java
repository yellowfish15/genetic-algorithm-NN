
public class MainPlay {

	public static void main(String[] args) {
		Model ai = new Model("nn.ylnn");
		
		Game game = new Game(ai);
		game.runActive();
		
		System.out.println("\nGame Over");
		if(game.winner == 0) {
			System.out.println("Tie Game!");
		} else if(game.winner == -1) {
			System.out.println("AI Wins!");
		} else {
			System.out.println("You Win!");
		}

	}

}
