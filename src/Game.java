import java.util.Scanner;

public class Game {

	// p1 is -1, p2 is 1
	Model p1, p2;
	Model curr;
	int marker;
	int winner = 0;

	double[] board;
	static final int[][] winning = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 },
			{ 0, 4, 8 }, { 2, 4, 6 }, };

	// ai vs ai
	public Game(Model p1, Model p2) {
		this.p1 = p1;
		this.p2 = p2;

		board = new double[9];

		curr = p1;
		marker = -1;
	}

	// ai vs player
	public Game(Model p) {
		this.p1 = p;
		this.p2 = null;

		board = new double[9];

		curr = p1;
		marker = -1;
	}

	// run two ai against each other
	public void runAuto() {

		// play tic tac toe
		while (!gameOver()) {
			// player turn
			int pchoice = getChoice(curr.predict(board));
			while (board[pchoice] != 0) {
				double[] Y = getAvailable();
				curr.train(board, Y);
				pchoice = getChoice(curr.predict(board));
			}
			board[pchoice] = marker;

			if (marker == -1) {
				marker = 1;
				curr = p2;
			} else {
				marker = -1;
				curr = p1;
			}
		}

	}

	// run ai vs player
	public void runActive() {

		// play tic tac toe
		while (!gameOver()) {

			if (marker == -1) {
				int pchoice = getChoice(curr.predict(board));
				while (board[pchoice] != 0) {
					double[] Y = getAvailable();
					curr.train(board, Y);
					pchoice = getChoice(curr.predict(board));
				}
				board[pchoice] = marker;
				System.out.println("AI played");
			} else {
				System.out.println("Enter square number (0 through 8)");
				System.out.print("> ");
				Scanner in = new Scanner(System.in);
				int choice = in.nextInt();
				while (choice < 0 || choice > 8 || board[choice] != 0) {
					System.out.println("Invalid choice!");
					System.out.print("> ");
					choice = in.nextInt();
				}
				board[choice] = marker;
			}

			printBoard();

			if (marker == -1) {
				marker = 1;
				curr = p2;
			} else {
				marker = -1;
				curr = p1;
			}
		}

	}

	public int getChoice(Matrix output) {
		int choice = 0;
		double maxValue = output.data[0][0];
		for (int i = 1; i < output.rows; i++) {
			double currValue = output.data[i][0];
			if (currValue > maxValue) {
				choice = i;
				maxValue = currValue;
			}
		}
		return choice;
	}

	public double[] getAvailable() {
		double[] ret = new double[9];
		for (int i = 0; i < 9; i++) {
			if (board[i] == 0)
				ret[i] = 1;
		}
		return ret;
	}

	// returns true if game is over
	public boolean gameOver() {

		for (int i = 0; i < winning.length; i++) {
			int pos1 = winning[i][0];
			int pos2 = winning[i][1];
			int pos3 = winning[i][2];

			if (board[pos1] == 1 && board[pos1] == board[pos2] && board[pos2] == board[pos3]) {
				winner = 1;
				return true;
			}
			if (board[pos1] == -1 && board[pos1] == board[pos2] && board[pos2] == board[pos3]) {
				winner = -1;
				return true;
			}
		}

		// check for tie
		boolean boardClear = true;
		for (int i = 0; i < 9; i++) {
			if (board[i] == 0) {
				boardClear = false;
			}
		}
		return boardClear;
	}

	public void printBoard() {
		for (int i = 0; i < 9; i++) {
			if (board[i] == 0)
				System.out.print("_ ");
			else if (board[i] == -1)
				System.out.print("X ");
			else if (board[i] == 1)
				System.out.print("O ");
			if ((i + 1) % 3 == 0)
				System.out.println();
		}
		System.out.println();
	}

}
