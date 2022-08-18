import java.util.ArrayList;
import java.util.Collections;

// tic tac toe testing of two evolving AI

public class MainAuto {

	public static void main(String[] args) {
		
		int numberGenerations = 4000;
		int numberPerGeneration = 1000;
		
		ArrayList<Model> players = new ArrayList<Model>(numberPerGeneration);
		for(int i = 0; i < numberPerGeneration; i++) {
	//		players.add(new Model("nn.ylnn").deriveChild());
			players.add(new Model(new int[] {9, 16, 16, 9}));
		}
		
		for(int g = 0; g < numberGenerations; g++) {
			Collections.shuffle(players);
			
			ArrayList<Model> nextGen = new ArrayList<Model>(numberPerGeneration);
			for(int i = 0; i < numberPerGeneration-1; i+= 2) {
				Model p1 = players.get(i);
				Model p2 = players.get(i+1);
				
				Game game = new Game(p1, p2);
				game.runAuto();
				
				if(game.winner == 0 || game.winner == 1) {
					nextGen.add(p2.deriveChild());
					nextGen.add(p2.deriveChild());
				} else if(game.winner == -1) {
					nextGen.add(p1.deriveChild());
					nextGen.add(p1.deriveChild());
				}
			}
		}
		
		players.get(0).saveValuesToFile();
		
		/*
		Model p1 = new Model("nn.ylnn");
		Model p2 = p1.deriveChild();
		
		for(int gens = 0; gens < 100000; gens++) {
			Game game = new Game(p1, p2);
			game.runAuto();
			
			if(game.winner == 0 || game.winner == 1) {
				System.out.println("p2 (o) is parent");
				p1 = p2.deriveChild();
			} else if(game.winner == -1) {
				System.out.println("p1 (x) is parent");
				p2 = p1.deriveChild();
			}
			game.printBoard();
		}
		
		p1.saveValuesToFile();
		*/
	}

}