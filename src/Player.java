import Board.*;
import java.util.Scanner;

/**
 * This is the player class used in order to play the Wumpus Game.
 */
public class Player {
    // Global variables
    public static int boardSize = 0;                    // size of the board
    public static Board board;                          // main board used in order to play the game
    public static int treeDepth = 0;                    // max depth of the tree used for minimax in the board for the AI move
    public static Scanner sc = new Scanner(System.in);  // scanner for input

    public static void main(String[] args) {
        System.out.print("Input Board Size (multiple of 3): ");
        boardSize = sc.nextInt();
        System.out.println();
        board = new Board(boardSize);
        System.out.print("Input Max Tree Depth: ");
        board.treeDepth = sc.nextInt();
        System.out.println();
        System.out.print("Input Which Heurisitc: ");
        board.heuToUse = sc.nextInt();
        System.out.println();
        board.printBoard();

        int[] move = new int[4];
        int color = 1;              // 0 = Player 1 (will be AI in future), 1 = Player 2
        while (!board.isDone()) {
            if (color == 0) {
                System.out.println("AI's move: Red(r)\nFormat:[oldRow] [oldCol] [newRow] [newCol])");
                //for (int i = 0 ; i  < move.length ; ++i) {
                    //move[i] = sc.nextInt();
                //}
                System.out.println();
                move = board.findNextBestMove();
                if (board.nextMove(move, color)) {
                    board.printBoard();
                    color = 1;
                } else {
                    System.out.println("AI : BAD MOVE , TRY AGAIN.");
                    board.printBoard();
                    color = 0;
                }
            } else {
                System.out.println("Human's move: White(w)\nFormat:[oldRow] [oldCol] [newRow] [newCol])");
                for (int i = 0 ; i  < move.length ; ++i) {
                    move[i] = sc.nextInt();
                }
                System.out.println();
                if (board.nextMove(move, color)) {
                    board.printBoard();
                    color = 0;
                } else {
                    System.out.println("Human : BAD MOVE , TRY AGAIN.");
                    board.printBoard();
                    color = 1;
                }
            }    
        } // ends the while loop
        System.out.println();
        // find the winner
        if (board.aiHasWon()) {
            System.out.println("AI WINS!");
        } else if (board.manHasWon()) {
            System.out.println("HUMAN WINS!");
        } else { // draw
            System.out.println("DRAW!");
        }
        System.out.println();
        sc.close();
    } // ends main() method

} // ends the Player class
