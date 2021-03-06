package Board;

import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This is the Board class. This will be the interface used in order to make
 * moves and play the game.
 */
public class Board {

    // Global variables
    public int size;                                    // this is the dimension of the board (size x size) ; will be dimension of 3
    public Piece[][] board;                             // this is the main board that the game will take place on
    public Random rand = new Random();                  // randomizer
    public List<int[]> allMoves = new LinkedList<>();   // will be used in order to store all the moves that have taken place
    public int treeDepth = 0;                           // will be the max depth of the minimax tree to look for the next move
    public int heuToUse = 0;                            // the heuristic for minimax to use
    public int aiTurns = 0;                             // number of moves AI has made so far


    /**
     * This is the constructor of the Board class.
     * @param size is the dimension size of the board (must be a multiple of 3)
     */
    public Board(int size) {
        this.size = size;
        this.board = new Piece[size][size];
        for (int i = 0 ; i < size ; ++i) {
            for (int j = 0; j < size ; ++j) {
                this.board[i][j] = new Piece(i, j, 4, 2,0,0,0,0,false,false,false,false); // initialize everything as an empty space and as color green
            }
        }
        initBoard();
    } // ends the Board() constructor
    
    /**
     * This method will be used in order to initilize the Board with all the pieces and elements for the game.
     */
    public void initBoard() {
        int curType = 1;                    // use this to determine which piece is to be placed on the board
        for(int i = 0 ; i < size ; ++i) {   // initialize the top row (for the AI)

            if (curType == 1) {
                this.board[0][i].probWumpus = 1.0;
                this.board[0][i].probHero = 0.0;
                this.board[0][i].probMage = 0.0;
                this.board[0][i].probPit = 0.0;
            } else if (curType == 2) {
                this.board[0][i].probWumpus = 0.0;
                this.board[0][i].probHero = 1.0;
                this.board[0][i].probMage = 0.0;
                this.board[0][i].probPit = 0.0;
            } else {
                this.board[0][i].probWumpus = 0.0;
                this.board[0][i].probHero = 0.0;
                this.board[0][i].probMage = 1.0;
                this.board[0][i].probPit = 0.0;
            }

            this.board[0][i].type = curType;
            this.board[0][i].color = 0;          // red for AI
            ++curType;
            if (curType >= 4) {
                curType = 1;
            }
        }

        curType = 1;
        for(int i = 0 ; i < size ; ++i) {   // initialize the bottom row (for the Human-Player)
            this.board[size-1][i].type = curType;
            this.board[size-1][i].color = 1;     // white for Human
            ++curType;
            if (curType >= 4) {
                curType = 1;
            }
        }

        int numPitsPerRow = (size/3) -1;
        int numPlaced = 0;
        if (numPitsPerRow > 0) {
            for(int i = 1 ; i < size-1 ; ++i) { // set the pits in each row except the top and bottom
                // place the pit(s) randomly in the row
                while(numPlaced < numPitsPerRow) {
                    int col = rand.nextInt(size);       // random place between [0,size-1]
                    if (this.board[i][col].type != 0) {
                        this.board[i][col].type = 0;
                        this.board[i][col].color = 3;
                        ++numPlaced;
                    }
                }
                numPlaced = 0;
            } 
        }
    } // ends the initBoard() method

    /**
     * This method will be used in order to print the current state of the board.
     */
    public void printBoard() {
        for(int i = 0 ; i < size ; ++i) {
            for (int j = 0 ; j < size ; ++j) {
                if (this.board[i][j].type == 0) {        // pit
                    System.out.print("PT\t");
                } else if (this.board[i][j].type == 1) { // wumpus
                    if (this.board[i][j].color == 0) {   // red
                        System.out.print("rW\t");
                    } else {
                        System.out.print("wW\t");
                    }
                } else if (this.board[i][j].type == 2) { // hero
                    if (this.board[i][j].color == 0) {   // red
                        System.out.print("rH\t");
                    } else {
                        System.out.print("wH\t");
                    }
                } else if (this.board[i][j].type == 3) { // mage
                    if (this.board[i][j].color == 0) {   // red
                        System.out.print("rM\t");
                    } else {
                        System.out.print("wM\t");
                    }
                } else {                            // empty
                    System.out.print("XX\t");
                }
                // print row num at the end of the columns
                if (j == size-1) {
                    System.out.print(i);
                }
            }
            // print columns at the last row
            System.out.println();
            if (i == size-1) {
                for (int k = 0 ; k < size ; ++k) {
                    System.out.print(k + "\t");
                }
                System.out.println();
            }
        }
    } // ends the printBoard() method

    /**
     * This method will check if the game is done.
     * @return true if the game is conculded, false otherwise 
     */
    public boolean isDone() {
        if (aiHasWon() || manHasWon() || draw()) { return true; }
        return false;
    } // ends the isDone() method

    /**
     * This method will check if the AI has won.
     * @return true if the AI has won the game, false otherwise
     */
    public boolean aiHasWon() {
        return ((getNumAIPieces() > 0) && (getNumManPieces() <= 0)) ? true : false;
    } // ends the aiHasWon() method

    /**
     * This method will check if the human has won.
     * @return true if the human has won the game, false otherwise
     */
    public boolean manHasWon() {
        return ((getNumManPieces() > 0) && (getNumAIPieces() <= 0)) ? true : false;
    } // ends the manHasWon() method

    /**
     * This method will check if the game is a draw.
     * @return true if the game is a draw, false otherwise
     */
    public boolean draw() {
        return ((getNumManPieces() == 0) && (getNumAIPieces() == 0)) ? true : false;
    } // ends the draw() method

    /**
     * This method will check if the move to make is possible and make it if true.
     * @param move the move to make
     * @param color the current player's color
     * @return true if the move was valid and made on the board and false otherwise
     */
    public boolean nextMove(int[] move , int color) {
        if ((move[0] == move[2]) && (move[1] == move[3])) { // must move the piece selected to a neighbor
            //System.out.println("MUST MOVE THE PIECE SELECTED TO A NEIGHBORING SQUARE.");
            return false;
        }
        if ((isSquareOnBoard(move[0], move[1]) == false) 
        || (isSquareOnBoard(move[2], move[3]) == false) 
        || (isNeighbor(move[0], move[1], move[2], move[3]) == false)) {
            //System.out.println("PLEASE MAKE SURE THE SQUARES ARE WITHIN THE BOUNDS OF THE BOARD AND/OR THEY ARE NEIGHBORS.");
            return false;
        }
        if ((board[move[0]][move[1]].type == 0) || (board[move[0]][move[1]].type == 4)) { // cannot be a pit or empty square
            //System.out.println("STARTING SQUARE MUST BE ONE OF YOUR PIECES.");
            return false;
        }
        if (board[move[0]][move[1]].color != color) { // cannot move opponent's piece
            //System.out.println("CANNOT MOVE YOUR OPPONENT'S PIECE.");
            return false;
        }
        if (board[move[2]][move[3]].color == color) { // cannot move into square with one's own piece in it
            //System.out.println("CANNOT ATTACK YOUR OWN PIECE.");
            return false;
        }

        int[] newMove = new int[8];                     // used to store new move into allMoves
        newMove[0] = move[0];                           // old x
        newMove[1] = move[1];                           // old y
        newMove[2] = board[move[0]][move[1]].type;      // type
        newMove[3] = board[move[0]][move[1]].color;     // color
        newMove[4] = move[2];                           // new x
        newMove[5] = move[3];                           // new y
        newMove[6] = board[move[2]][move[3]].type;      // type
        newMove[7] = board[move[2]][move[3]].color;     // color
        allMoves.add(newMove);

        // find the outcome of the move and update the board
        if (moveResult(move) == -1) {           // old piece gets destroyed, new piece stays the same
            
            board[move[0]][move[1]].type = 4;   // empty square   
            board[move[0]][move[1]].color = 2;  // green

        } else if (moveResult(move) == 0) {     // old and new piece are the same type, both get destoryed
            
            board[move[0]][move[1]].type = 4;   // empty square   
            board[move[0]][move[1]].color = 2;  // green
            board[move[2]][move[3]].type = 4;   // empty square   
            board[move[2]][move[3]].color = 2;  // green

        } else {                                // old piece wins battle, new piece gets destoryed

            board[move[2]][move[3]].type = board[move[0]][move[1]].type; 
            board[move[2]][move[3]].color = board[move[0]][move[1]].color;
            board[move[0]][move[1]].type = 4;   
            board[move[0]][move[1]].color = 2;

        }

        return true;

    } //  ends the nextMove() method


    /**
     * This method will figure out the outcome of the move to make
     * @param move the old and new placement of the piece
     * @return -1 if old piece loses , 1 if old piece wins , 0 if draw and both lose
     */
    public int moveResult(int[] move) {
        if (board[move[0]][move[1]].type == 1) {                // Wumpus
            /*
            Wumpus vs Wumpus : Draw   : 0
            Wumpus vs Hero   : Hero   : -1
            Wumpus vs Mage   : Wumpus : 1
            Wumpus vs Pit    : Pit    : -1
            Wumpus vs Empty  : Wumpus : 1
            */
            if (board[move[2]][move[3]].type == 1) {
                return 0;
            } else if (board[move[2]][move[3]].type == 2) {
                return -1;
            } else if (board[move[2]][move[3]].type == 3) {
                return 1;
            } else if (board[move[2]][move[3]].type == 0) {
                return -1;
            } else {
                return 1;
            }
        } else if (board[move[0]][move[1]].type == 2) {         // Hero
            /*
            Hero vs Hero   : Draw   : 0
            Hero vs Mage   : Mage   : -1
            Hero vs Wumpus : Hero   : 1
            Hero vs Pit    : Pit    : -1
            Hero vs Empty  : Hero   : 1
            */
            if (board[move[2]][move[3]].type == 2) {
                return 0;
            } else if (board[move[2]][move[3]].type == 3) {
                return -1;
            } else if (board[move[2]][move[3]].type == 1) {
                return 1;
            } else if (board[move[2]][move[3]].type == 0) {
                return -1;
            } else {
                return 1;
            }
        } else {                                                // Mage
            /*
            Mage vs Mage   : Draw   : 0
            Mage vs Wumpus : Wumpus : -1
            Mage vs Hero   : Mage   : 1
            Mage vs Pit    : Pit    : -1
            Mage vs Empty  : Mage   : 1
            */
            if (board[move[2]][move[3]].type == 3) {
                return 0;
            } else if (board[move[2]][move[3]].type == 1) {
                return -1;
            } else if (board[move[2]][move[3]].type == 2) {
                return 1;
            } else if (board[move[2]][move[3]].type == 0) {
                return -1;
            } else {
                return 1;
            }
        }
    } // ends the moveResult() method

    /**
     * This method will check if a given coordinate pair is on the board or not.
     * @param x the row
     * @param y the column
     * @return true if the square is valid and false otherwise
     */
    public boolean isSquareOnBoard(int x , int y) {
        if ((x >= 0 && x < size) && (y >= 0 && y < size)) {
            return true;
        } else {
            return false;
        }
    } // ends the isSquareOnBoard() method


    /**
     * This method will check is the new (x,y) coordinate is a neighbor (one move away) of the old (x,y) coordinate
     * @param oldX old x
     * @param oldY old y 
     * @param newX new x
     * @param newY new y
     * @return true if the new (x,y) is a neighbor of old (x,y)
     */
    public boolean isNeighbor(int oldX, int oldY , int newX , int newY) {
        for (int i = -1 ; i <= 1 ; ++i) {
            for (int j = -1 ; j <= 1 ; ++j) {
                if(i == 0 && j == 0) {continue;}
                int curX = oldX + i;
                int curY = oldY + j;
                if ((curX == newX) && (curY == newY)) {
                    return true;
                }
            }
        }
        return false;
    } // ends the isNeighbor() method

    /**
     * This method is used in order to get the total number of AI's pieces on the board currently.
     * @return number of AI's pieces on the board
     */
    public int getNumAIPieces() {
        int num = 0;
        for (int i = 0 ; i < size ; ++i) {
            for (int j = 0 ; j < size ; ++j) {
                if (board[i][j].color == 0) {
                    ++num;
                }
            }
        }
        return num;
    } // ends the getNumAIPieces() method

    /**
     * This method is used in order to get thr total number of Human's pieces on the board currently.
     * @return number of Human's pieces on the board
     */
    public int getNumManPieces() {
        int num = 0;
        for (int i = 0 ; i < size ; ++i) {
            for (int j = 0 ; j < size ; ++j) {
                if (board[i][j].color == 1) {
                    ++num;
                }
            }
        }
        return num;
    } // ends the getNumManPieces() method

    public int getNumSpecific(int type, int color){
        int num = 0;
        for (int i = 0 ; i < size ; ++i) {
            for (int j = 0 ; j < size ; ++j) {
                if (board[i][j].color == color && board[i][j].type == type) {
                    ++num;
                }
            }
        }
        return num;
    }

    /**
     * This method will be used in order to reverse/undo the last move made on the board.
     */
    public void reverse() {
        int[] move = allMoves.remove(allMoves.size()-1);
        board[move[0]][move[1]].type = move[2];
        board[move[0]][move[1]].color = move[3];
        board[move[4]][move[5]].type = move[6];
        board[move[4]][move[5]].color = move[7];
    } // ends the reverse(I) method

    /**
     * This method will be used in order to find the best move for the AI
     * @return an int array of size 4 that holds the next move to make
     */
    public int[] findNextBestMove() {
        int[] bestMove = new int[4];            // best move to make at the end
        int[] curMove = new int[4];             // current move to look at
        int curMoveVal = Integer.MIN_VALUE;
        int bestMoveVal = Integer.MIN_VALUE;

        PriorityQueue<int[]> nextAvailableMoves = new PriorityQueue<>((one, two) -> Integer.compare(two[4], one[4])); // max heap

        // get all the AI's pieces and run minimax on each one with specified depth
        List<int[]> pieces = getCurrentAIPieces();
        for (int[] piece : pieces) {
            //System.out.println("Outside: Piece: (" + piece[0] + " , " + piece[1] + ")");
            List<int[]> neighbors = getNeighbors(piece);
            for (int[] neighbor : neighbors) {
                //System.out.println("Outside: Neighbor: (" + neighbor[0] + " , " + neighbor[1] + ")");
                curMove[0] = piece[0];
                curMove[1] = piece[1];
                curMove[2] = neighbor[0];
                curMove[3] = neighbor[1];
                if (nextMove(curMove, 0)) { // make AI move, then put into minimax
                    //treeDepth = getNumAIPieces();
                    curMoveVal = minimax(Integer.MIN_VALUE, Integer.MAX_VALUE, true, heuToUse , treeDepth);
                    //System.out.println(bestMoveVal + " | " + curMoveVal + " = (" + curMove[0] + " , " + curMove[1] + ") -> (" + curMove[2] + " , " + curMove[3] + ")");
                    reverse();
                    if (curMoveVal != -1000 && curMoveVal > bestMoveVal) {
                        bestMoveVal = curMoveVal;
                        bestMove[0] = curMove[0];
                        bestMove[1] = curMove[1];
                        bestMove[2] = curMove[2];
                        bestMove[3] = curMove[3];
                    }
                    if (curMoveVal == 1000) {
                        bestMoveVal = curMoveVal;
                        bestMove[0] = curMove[0];
                        bestMove[1] = curMove[1];
                        bestMove[2] = curMove[2];
                        bestMove[3] = curMove[3];
                        return bestMove;
                    }

                }
            }
        }
        System.out.println("\n");
        return bestMove;
    } // ends the findNextBestMove() method








    /**
     * This method will be used in order to find the best move for the AI
     * @return an int array of size 4 that holds the next move to make
     */
    public int[] findNextBestMoveFogOfWar() throws InterruptedException {
        int[] bestMove = new int[4];            // best move to make at the end
        int[] curMove = new int[4];             // current move to look at
        int curMoveVal = Integer.MIN_VALUE;
        int bestMoveVal = Integer.MIN_VALUE;

        PriorityQueue<int[]> nextAvailableMoves = new PriorityQueue<>((one, two) -> Integer.compare(two[4], one[4])); // max heap

        // get all the AI's pieces and run minimax on each one with specified depth
        List<int[]> pieces = getCurrentAIPieces();
        for (int[] piece : pieces) {
            //System.out.println("Outside: Piece: (" + piece[0] + " , " + piece[1] + ")");
            List<int[]> neighbors = getNeighbors(piece);
            for (int[] neighbor : neighbors) {
                //System.out.println("Outside: Neighbor: (" + neighbor[0] + " , " + neighbor[1] + ")");
                curMove[0] = piece[0];
                curMove[1] = piece[1];
                curMove[2] = neighbor[0];
                curMove[3] = neighbor[1];
                if (nextMove(curMove, 0)) { // make AI move, then put into minimax
                    //treeDepth = getNumAIPieces();
                    curMoveVal = minimaxFogOfWar(Integer.MIN_VALUE, Integer.MAX_VALUE, true, heuToUse , treeDepth);
                    //System.out.println(bestMoveVal + " | " + curMoveVal + " = (" + curMove[0] + " , " + curMove[1] + ") -> (" + curMove[2] + " , " + curMove[3] + ")");
                    reverse();
                    if (curMoveVal != -1000 && curMoveVal > bestMoveVal) {
                        bestMoveVal = curMoveVal;
                        bestMove[0] = curMove[0];
                        bestMove[1] = curMove[1];
                        bestMove[2] = curMove[2];
                        bestMove[3] = curMove[3];
                    }
                    if (curMoveVal == 1000) {
                        bestMoveVal = curMoveVal;
                        bestMove[0] = curMove[0];
                        bestMove[1] = curMove[1];
                        bestMove[2] = curMove[2];
                        bestMove[3] = curMove[3];
                        return bestMove;
                    }

                }
            }
        }
        System.out.println("\n");
        return bestMove;
    } // ends the findNextBestMoveFogOfWar() method










    /**
     * This is the maximizer method used to obtain the best move for player using minimax.
     * @param alpha the alpha value
     * @param beta the beta value
     * @param isMaximizer boolean tells us if current player is maximizer or not
     * @param heu the heuristic used to give a value to certain moves at the end
     * @param curDepth the currentDepth of the Search
     * @return
     */
    public int minimaxFogOfWar(int alpha , int beta , boolean isMaximizer , int heu , int curDepth) throws InterruptedException {
        if (isDone() || curDepth == 0) {
            return FogOfWarPolicy();
        }
        if (isMaximizer) { // it is the AI

            PriorityQueue<int[]> nextAvailableMoves = new PriorityQueue<>((one, two) -> Integer.compare(two[4], one[4])); // max heap


            int value = Integer.MIN_VALUE;
            // get all the AI's pieces and run minimax on each one with specified depth
            List<int[]> pieces = getCurrentAIPieces();
            for (int[] piece : pieces) {
                //System.out.println("Inside: Piece: (" + piece[0] + " , " + piece[1] + ")");
                List<int[]> neighbors = getNeighbors(piece);
                for (int[] neighbor : neighbors) {
                    //System.out.println("Inside: Neighbor: (" + neighbor[0] + " , " + neighbor[1] + ")");
                    int[] move = new int[4];
                    move[0] = piece[0];
                    move[1] = piece[1];
                    move[2] = neighbor[0];
                    move[3] = neighbor[1];
                    if (nextMove(move, 0)) {
                        int hVal = FogOfWarPolicy();

                        int[] possibleMove = new int[5];
                        possibleMove[0] = move[0];
                        possibleMove[1] = move[1];
                        possibleMove[2] = move[2];
                        possibleMove[3] = move[3];
                        possibleMove[4] = hVal;
                        //System.out.println("Pushing: (" + move[0] + " , " + move[1] + ") ==> ("+ move[2] + " , " + move[3] + ")");
                        nextAvailableMoves.add(possibleMove);
                        reverse();
                    }
                } // inner for loop (get neighbors)
            } // outer for loop (get pieces)
            //System.out.println("BEFORE HEAP");
            int[] curBestNeighbor = nextAvailableMoves.poll();
            while (curBestNeighbor != null) {
                int[] move = new int[4];
                move[0] = curBestNeighbor[0];
                move[1] = curBestNeighbor[1];
                move[2] = curBestNeighbor[2];
                move[3] = curBestNeighbor[3];
                nextMove(move, 0);
                //System.out.println("Pulling: (" + move[0] + " , " + move[1] + ") ==> ("+ move[2] + " , " + move[3] + ")");
                value += Integer.max(value, minimax(alpha, beta, !isMaximizer, heu, curDepth-1));
                alpha = Integer.max(alpha, value);
                if (alpha >= beta) {
                    reverse();
                    return value;
                }
                reverse();
                curBestNeighbor = nextAvailableMoves.poll();
            }
            return value;
        } else { // human

            PriorityQueue<int[]> nextAvailableMoves = new PriorityQueue<>((one, two) -> Integer.compare(two[4], one[4])); // max heap

            int value = Integer.MAX_VALUE;
            // get all the Humans's pieces and run minimax on each one with specified depth
            List<int[]> pieces = getCurrentHumanPieces();
            for (int[] piece : pieces) {
                List<int[]> neighbors = getNeighbors(piece);
                for (int[] neighbor : neighbors) {
                    int[] move = new int[4];
                    move[0] = piece[0];
                    move[1] = piece[1];
                    move[2] = neighbor[0];
                    move[3] = neighbor[1];
                    if (nextMove(move, 1)) {
                        int hVal = FogOfWarPolicy();

                        int[] possibleMove = new int[5];
                        possibleMove[0] = move[0];
                        possibleMove[1] = move[1];
                        possibleMove[2] = move[2];
                        possibleMove[3] = move[3];
                        possibleMove[4] = -hVal;
                        //System.out.println("Pushing: (" + move[0] + " , " + move[1] + ") ==> ("+ move[2] + " , " + move[3] + ")");
                        nextAvailableMoves.add(possibleMove);
                        reverse();
                    }
                }
            }
            int[] curBestNeighbor = nextAvailableMoves.poll();
            while (curBestNeighbor != null) {
                int[] move = new int[4];
                move[0] = curBestNeighbor[0];
                move[1] = curBestNeighbor[1];
                move[2] = curBestNeighbor[2];
                move[3] = curBestNeighbor[3];
                nextMove(move, 1);
                //System.out.println("Pulling: (" + move[0] + " , " + move[1] + ") ==> ("+ move[2] + " , " + move[3] + ")");
                value = Integer.min(value, minimax(alpha, beta, !isMaximizer, heu, curDepth-1));
                beta = Integer.min(beta, value);
                if (beta <= alpha) {
                    reverse();
                    return value;
                }
                reverse();
                curBestNeighbor = nextAvailableMoves.poll();
            }
            return value;
        }
    } // ends the minimaxFogOfWar() method





    public int highestProb(int[] place) {
        int type = 1;
        double highest = board[place[0]][place[1]].probWumpus;

        if (board[place[0]][place[1]].probHero > highest) {
            highest = board[place[0]][place[1]].probHero;
            type = 2;
        }

        if (board[place[0]][place[1]].probMage > highest) {
            highest = board[place[0]][place[1]].probMage;
            type = 3;
        }

        return type;
    }






    public int FogOfWarPolicy() throws InterruptedException {

        if (aiHasWon()) {
            return 1000;
        }
        if (manHasWon()) {
            return -1000;
        }
        if (draw()) {
            return 0;
        }

        // first update the probabilities in the board in reference to the AI
        updateProbsAI();

        // overall value for the current board
        int val = 0;


        /**
         * Here we want to figure out if the position on the board is optimal or not and add points up for each of the good moves that the AI can make
         *
         * Get the list of all the current AI Pieces
         *
         * Get all the neighbors of each piece and guess which piece is good or bad and add up the points accordingly
         *
         */
        List<int[]> AIpieces = getCurrentAIPieces();
        for (int[] piece : AIpieces) {
            List<int[]> neighbors = getAllNeighbors(piece);
            for (int[] neighbor : neighbors) {
                if (board[piece[0]][piece[1]].type == 1) { // wumpus
                    int highestType = highestProb(neighbor);
                    if (highestType == 3) {
                        val += 500;
                    } else if (highestType == 1) {
                        val += 250;
                    } else {
                        val += 0;
                    }
                } else if (board[piece[0]][piece[1]].type == 2) { // hero
                    int highestType = highestProb(neighbor);
                    if (highestType == 1) {
                        val += 500;
                    } else if (highestType == 2) {
                        val += 250;
                    } else {
                        val += 0;
                    }
                } else { // mage
                    int highestType = highestProb(neighbor);
                    if (highestType == 2) {
                        val += 500;
                    } else if (highestType == 3) {
                        val += 250;
                    } else {
                        val += 0;
                    }
                }
            }
        }

        return val;

    } // ends the FogOfWarPolicy() method

















    /*
    if (curMoveVal > bestMoveVal) {
                        bestMoveVal = curMoveVal;
                        bestMove[0] = curMove[0];
                        bestMove[1] = curMove[1];
                        bestMove[2] = curMove[2];
                        bestMove[3] = curMove[3];
                    }
     */


    /**
     * This is the maximizer method used to obtain the best move for player using minimax.
     * @param alpha the alpha value
     * @param beta the beta value
     * @param isMaximizer boolean tells us if current player is maximizer or not
     * @param heu the heuristic used to give a value to certain moves at the end
     * @param curDepth the currentDepth of the Search
     * @return
     */
    public int minimax(int alpha , int beta , boolean isMaximizer , int heu , int curDepth) {
        if (isDone() || curDepth == 0) {
            if (heu == 1) {
                return A();
            } else if (heu == 2) {
                return B();
            } else if (heu == 3) {
                return C();
            } else if (heu == 4) {
                return D();
            } else if (heu == 5) {
                return E();
            } else {
                return F();
            }
        }
        if (isMaximizer) { // it is the AI

            PriorityQueue<int[]> nextAvailableMoves = new PriorityQueue<>((one, two) -> Integer.compare(two[4], one[4])); // max heap


            int value = Integer.MIN_VALUE;
            // get all the AI's pieces and run minimax on each one with specified depth
            List<int[]> pieces = getCurrentAIPieces();
            for (int[] piece : pieces) {
                //System.out.println("Inside: Piece: (" + piece[0] + " , " + piece[1] + ")");
                List<int[]> neighbors = getNeighbors(piece);
                for (int[] neighbor : neighbors) {
                    //System.out.println("Inside: Neighbor: (" + neighbor[0] + " , " + neighbor[1] + ")");
                    int[] move = new int[4];
                    move[0] = piece[0];
                    move[1] = piece[1];
                    move[2] = neighbor[0];
                    move[3] = neighbor[1];
                    if (nextMove(move, 0)) {
                        int hVal = 0;
                        if (heu == 1) {
                            hVal = A();
                        } else if (heu == 2) {
                            hVal = B();
                        } else if (heu == 3) {
                            hVal = C();
                        } else if (heu == 4) {
                            hVal = D();
                        } else if (heu == 5) {
                            hVal = E();
                        } else {
                            hVal = F();
                        }
                        int[] possibleMove = new int[5];
                        possibleMove[0] = move[0];
                        possibleMove[1] = move[1];
                        possibleMove[2] = move[2];
                        possibleMove[3] = move[3];
                        possibleMove[4] = hVal;
                        //System.out.println("Pushing: (" + move[0] + " , " + move[1] + ") ==> ("+ move[2] + " , " + move[3] + ")");
                        nextAvailableMoves.add(possibleMove);
                        reverse();
                    }
                } // inner for loop (get neighbors)
            } // outer for loop (get pieces)
            //System.out.println("BEFORE HEAP");
            int[] curBestNeighbor = nextAvailableMoves.poll();
            while (curBestNeighbor != null) {
                int[] move = new int[4];
                move[0] = curBestNeighbor[0];
                move[1] = curBestNeighbor[1];
                move[2] = curBestNeighbor[2];
                move[3] = curBestNeighbor[3];
                nextMove(move, 0);
                //System.out.println("Pulling: (" + move[0] + " , " + move[1] + ") ==> ("+ move[2] + " , " + move[3] + ")");
                value += Integer.max(value, minimax(alpha, beta, !isMaximizer, heu, curDepth-1));
                alpha = Integer.max(alpha, value);
                if (alpha >= beta) {
                    reverse();
                    return value;
                }
                reverse();
                curBestNeighbor = nextAvailableMoves.poll();
            }
            return value;
        } else { // human

            PriorityQueue<int[]> nextAvailableMoves = new PriorityQueue<>((one, two) -> Integer.compare(two[4], one[4])); // max heap

            int value = Integer.MAX_VALUE;
            // get all the Humans's pieces and run minimax on each one with specified depth
            List<int[]> pieces = getCurrentHumanPieces();
            for (int[] piece : pieces) {
                List<int[]> neighbors = getNeighbors(piece);
                for (int[] neighbor : neighbors) {
                    int[] move = new int[4];
                    move[0] = piece[0];
                    move[1] = piece[1];
                    move[2] = neighbor[0];
                    move[3] = neighbor[1];
                    if (nextMove(move, 1)) {
                        int hVal = 0;
                        if (heu == 1) {
                            hVal = A();
                        } else if (heu == 2) {
                            hVal = B();
                        } else if (heu == 3) {
                            hVal = C();
                        } else if (heu == 4) {
                            hVal = D();
                        } else if (heu == 5) {
                            hVal = E();
                        } else {
                            hVal = F();
                        }
                        int[] possibleMove = new int[5];
                        possibleMove[0] = move[0];
                        possibleMove[1] = move[1];
                        possibleMove[2] = move[2];
                        possibleMove[3] = move[3];
                        possibleMove[4] = -hVal;
                        //System.out.println("Pushing: (" + move[0] + " , " + move[1] + ") ==> ("+ move[2] + " , " + move[3] + ")");
                        nextAvailableMoves.add(possibleMove);
                        reverse();
                    }
                }
            }
            int[] curBestNeighbor = nextAvailableMoves.poll();
            while (curBestNeighbor != null) {
                int[] move = new int[4];
                move[0] = curBestNeighbor[0];
                move[1] = curBestNeighbor[1];
                move[2] = curBestNeighbor[2];
                move[3] = curBestNeighbor[3];
                nextMove(move, 1);
                //System.out.println("Pulling: (" + move[0] + " , " + move[1] + ") ==> ("+ move[2] + " , " + move[3] + ")");
                value = Integer.min(value, minimax(alpha, beta, !isMaximizer, heu, curDepth-1));
                beta = Integer.min(beta, value);
                if (beta <= alpha) {
                    reverse();
                    return value;
                }
                reverse();
                curBestNeighbor = nextAvailableMoves.poll();
            }
            return value;
        }
    } // ends the minimax() method


    public int A() {

        //return getNumAIPieces() - getNumManPieces();  // OLD A

        if (aiHasWon()) {
            return 1000;
        }
        if (manHasWon()) {
            return -1000;
        }
        if (draw()) {
            return 0;
        }

        // see if there are any pieces that they can kill immediately
        int totalPoints = 0;
        List<int[]> pieces = getCurrentAIPieces();
        for (int[] piece : pieces) {
            List<int[]> neighbors = getNeighbors(piece);
            for (int[] neighbor : neighbors) {
                if (moveResult(new int[]{piece[0],piece[1],neighbor[0],neighbor[1]}) == 1) {
                    totalPoints += 150;
                } else if (moveResult(new int[]{piece[0],piece[1],neighbor[0],neighbor[1]}) == 0) {
                    totalPoints += 100;
                } else {
                    totalPoints -= 50;
                }
            }
        }


        pieces = getCurrentAIPieces();
        List<int[]> humanPieces = getCurrentHumanPieces();
        for (int[] piece : pieces) {
            for (int[] manPiece : humanPieces) {
                if (moveResult(new int[]{piece[0],piece[1],manPiece[0],manPiece[1]}) == 1) {
                    totalPoints += 150 / distanceBetween(new int[]{piece[0],piece[1],manPiece[0],manPiece[1]});
                }  else if (moveResult(new int[]{piece[0],piece[1],manPiece[0],manPiece[1]}) == 0) {
                    totalPoints += 100 / distanceBetween(new int[]{piece[0],piece[1],manPiece[0],manPiece[1]});
                } else {
                    totalPoints -= 50 / distanceBetween(new int[]{piece[0],piece[1],manPiece[0],manPiece[1]});
                }
            }
        }


        return totalPoints;

    } // ends the A() method

    // get euclidean distance
    public int distanceBetween(int[] points) {
        return (int)(Math.sqrt(((points[0]-points[2])*(points[0]-points[2]) + ((points[1]-points[3])*(points[1]-points[3])))));
    }



    // try to have more pieces than human
    public int B() {
        if (aiHasWon()) {
            return 100;
        }
        if (manHasWon()) {
            return -100;
        }
        if (draw()) {
            return 0;
        }
        if (getNumAIPieces() < getNumManPieces()) {
            return -50;
        } else {
            return 50;
        }
    }
    

    public int C(){
        return this.getNumSpecific(1, 0) - this.getNumSpecific(1, 1);

    }


    //heruistic 4 prioritizes hero
    public int D(){
        return this.getNumSpecific(2, 0) - this.getNumSpecific(2, 1);
    }



    //heuristic 5 prioritizes mage
    public int E(){
        return this.getNumSpecific(3, 0) - this.getNumSpecific(3, 1);
    }



    //heuristic 6 returns the max of all previous heuristics
    public int F(){
        PriorityQueue<Integer> heap = new PriorityQueue<>((a, b) -> b - a);
        heap.offer(this.A());
        heap.offer(this.B());
        heap.offer(this.C());
        heap.offer(this.D());
        heap.offer(this.E());
        return heap.peek();
    }



    /**
     * This method will be used in order to obtain all the AI's Pieces' coordinates.
     * @return a list of coordinates for all the AI's pieces on the board
     */
    public List<int[]> getCurrentAIPieces() {
        List<int[]> aiPieces = new LinkedList<>();
        for (int i = 0 ; i < size ; ++i) {
            for (int j = 0 ; j < size ; ++j) {
                if (board[i][j].color == 0) {
                    int[] coords = new int[2];
                    coords[0] = i;
                    coords[1] = j;
                    aiPieces.add(coords);
                }
            }
        }
        return aiPieces;
    }// ends the getCurrentAIPieces() method

     /**
     * This method will be used in order to obtain all the Human's Pieces' coordinates.
     * @return a list of coordinates for all the Human's pieces on the board
     */
    public List<int[]> getCurrentHumanPieces() {
        List<int[]> manPieces = new LinkedList<>();
        for (int i = 0 ; i < size ; ++i) {
            for (int j = 0 ; j < size ; ++j) {
                if (board[i][j].color == 1) {
                    int[] coords = new int[2];
                    coords[0] = i;
                    coords[1] = j;
                    manPieces.add(coords);
                }
            }
        }
        return manPieces;
    }// ends the getCurrentAIPieces() method
    

    /**
     * This method will get all the possible neighbors (squares) that the current piece can move to (EXCEPT FOR PIT)
     * @param curPiece
     * @return
     */
    public List<int[]> getNeighbors(int[] curPiece) {
        List<int[]> neighbors = new LinkedList<>();
        for (int i = -1 ; i <= 1 ; ++i) {
             for (int j = -1 ; j <= 1 ; ++j) {
                if (i == 0 && j == 0) {continue;}
                int curX = curPiece[0] + i;
                int curY = curPiece[1] + j;
                if ((isSquareOnBoard(curX, curY))                                         // valid piece on the board
                && (isNeighbor(curPiece[0], curPiece[1], curX, curY))                     // the current neighbor is in fact a neighbor
                && (board[curPiece[0]][curPiece[1]].color != board[curX][curY].color)     // the current neighbor isn't one of your own other pieces
                && (board[curX][curY].type != 0)                                          // current neighbor is not a pit
                ) {
                    int[] coords = new int[2];
                    coords[0] = curX;
                    coords[1] = curY;
                    neighbors.add(coords);
                }
             }
        }
        return neighbors;
    } // ends the getNeighbors






    /*****************************************************************************************************************************************************************************************************/
    // Here starts all the code written for Fog of War


    /**
     * This method will get all the possible neighbors (squares) that the current piece can move to
     * @param curPiece
     * @return
     */
    public List<int[]> getAllNeighbors(int[] curPiece) {
        List<int[]> neighbors = new LinkedList<>();
        for (int i = -1 ; i <= 1 ; ++i) {
            for (int j = -1 ; j <= 1 ; ++j) {
                if (i == 0 && j == 0) {continue;}
                int curX = curPiece[0] + i;
                int curY = curPiece[1] + j;
                if ((isSquareOnBoard(curX, curY))                                         // valid piece on the board
                        && (isNeighbor(curPiece[0], curPiece[1], curX, curY))                     // the current neighbor is in fact a neighbor
                        && (board[curPiece[0]][curPiece[1]].color != board[curX][curY].color))      //the current neighbor isn't one of your own other pieces
            {
                    int[] coords = new int[2];
                    coords[0] = curX;
                    coords[1] = curY;
                    neighbors.add(coords);
                }
            }
        }
        return neighbors;
    } // ends the getNeighbors



    // Method to get different observations (S,N,HE,B), then set them on the human pieces
    public void getObservations() {
        // get all human pieces
        List<int[]> humanPieces = getCurrentHumanPieces();
        for (int[] piece : humanPieces) {
            board[piece[0]][piece[1]].hasStench = false;
            board[piece[0]][piece[1]].hasNoise = false;
            board[piece[0]][piece[1]].hasHeat = false;
            board[piece[0]][piece[1]].hasBreeze = false;


            // get all neightbors
            List<int[]> neighbors = getAllNeighbors(piece);
            for (int[] neighbor : neighbors) {

                if (hasAIWumpus(neighbor)) {
                    board[piece[0]][piece[1]].hasStench = true;
                }

                if (hasAIHero(neighbor)) {
                    board[piece[0]][piece[1]].hasNoise = true;
                }

                if (hasAIMage(neighbor)) {
                    board[piece[0]][piece[1]].hasHeat = true;
                }

                if (hasPit(neighbor)) {
                    board[piece[0]][piece[1]].hasBreeze = true;
                }

            }

        }


    } // ends the getObservations() method





    // true if that tile is a AI Wumpus
    public boolean hasAIWumpus(int[] place) {
        if ((board[place[0]][place[1]].color == 0) && (board[place[0]][place[1]].type == 1)) { // AI Wumpus
            return true;
        } else {
            return false;
        }
    }





    // true if that tile is a AI Hero
    public boolean hasAIHero(int[] place) {
        if ((board[place[0]][place[1]].color == 0) && (board[place[0]][place[1]].type == 2)) { // AI Hero
            return true;
        } else {
            return false;
        }
    }





    // true if that tile is a AI Mage
    public boolean hasAIMage(int[] place) {
        if ((board[place[0]][place[1]].color == 0) && (board[place[0]][place[1]].type == 3)) { // AI Mage
            return true;
        } else {
            return false;
        }
    }





    // true if that tile is a pit
    public boolean hasPit(int[] place) {
        if (board[place[0]][place[1]].type == 0) { // Pit
            return true;
        } else {
            return false;
        }
    }


    // will zero out all the probs of all non-human piece tiles
    public void zeroOutProbs() {
        for (int i = 0 ; i < size ; ++i) {
            for (int j = 0 ; j < size ; ++j) {
                if (board[i][j].color != 1) {
                    board[i][j].probPit = 0.0;
                    board[i][j].probMage = 0.0;
                    board[i][j].probHero = 0.0;
                    board[i][j].probWumpus = 0.0;
                }
            }
        }
    }


    public boolean alreadyHas(List<int[]> arr, int[] p) {
        if (arr.size() > 0) {
            for(int[] i : arr) {
                if (i[0] == p[0] && i[1] == p[1]) {
                    return true;
                }
            }
        }
        return false;
    }


    // get surrounding human neighbors
    public List<int[]> getHumanNeighbors(int[] curPiece) {
        List<int[]> neighbors = new LinkedList<>();
        for (int i = -1 ; i <= 1 ; ++i) {
            for (int j = -1 ; j <= 1 ; ++j) {
                if (i == 0 && j == 0) {continue;}
                int curX = curPiece[0] + i;
                int curY = curPiece[1] + j;
                if ((isSquareOnBoard(curX, curY))                                         // valid piece on the board
                        && (isNeighbor(curPiece[0], curPiece[1], curX, curY))                     // the current neighbor is in fact a neighbor
                        && (board[curX][curY].color == 1) )                              // neighbors is a human piece
                {
                    int[] coords = new int[2];
                    coords[0] = curX;
                    coords[1] = curY;
                    neighbors.add(coords);
                }
            }
        }
        return neighbors;
    }



    // this is the method called to update probabilities for the human every turn
    public void updateProbs() throws InterruptedException {
        // zero out the probs first
        zeroOutProbs();

        if (size == 3) {
            // go through list of observations for 3x3 (S,N,HE)
            for (int i = 1; i <= 3 ; ++i) {
                List<int[]> pieceList = getPieceList(i);

//                System.out.println(i + " , " + pieceList.size());
//                if (pieceList.size() > 0) {
//                    for (int[] piece : pieceList) {
//                        System.out.println("(" + piece[0] + " , " + piece[1] + ")");
//                    }
//                }

                if (i == 1) { // wumpus / S
                    if (pieceList.size() > 0) {
                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }
                        List<int[]> possibleTiles = new ArrayList<>();
                        if (sharedNeighbors.size() > 0) {
                            for(int[] neighbor : sharedNeighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                            }
                            // find neighbor whose neighbors are all the pieces in the piece list
                            for (int[] neighbor : sharedNeighbors) {
                                List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                if (humanNeighbors.size() == pieceList.size()) {
                                    boolean good = false;
                                    for (int[] piece : pieceList) {
                                        good = alreadyHas(humanNeighbors, piece);
                                        if (!good) {
                                            break;
                                        }
                                    }
                                    if (good) {
                                        possibleTiles.add(neighbor);
                                    }
                                }
                            }
                        }
                        //System.out.println();
                        if (possibleTiles.size() > 0) {
                            for(int[] tile : possibleTiles) {
                                //System.out.println("(" + tile[0] + " , " + tile[1] + ")");
                            }
                            // since 3x3 with 1 wumpus
                            double probW = 1.0 / (double)possibleTiles.size();
                            BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                            probW = bd.doubleValue();
                            for (int[] tile : possibleTiles) {
                                board[tile[0]][tile[1]].probWumpus = probW;
                            }
                        }

                    } else {

                        // check if AI has wumpus
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 1) {
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 3x3 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = 0.0;
                                }
                            }
                        }

                    }

                } else if (i == 2) { // hero / N
                    if (pieceList.size() > 0) {
                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }
                        List<int[]> possibleTiles = new ArrayList<>();
                        if (sharedNeighbors.size() > 0) {
                            for(int[] neighbor : sharedNeighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                            }
                            // find neighbor whose neighbors are all the pieces in the piece list
                            for (int[] neighbor : sharedNeighbors) {
                                List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                if (humanNeighbors.size() == pieceList.size()) {
                                    boolean good = false;
                                    for (int[] piece : pieceList) {
                                        good = alreadyHas(humanNeighbors, piece);
                                        if (!good) {
                                            break;
                                        }
                                    }
                                    if (good) {
                                        possibleTiles.add(neighbor);
                                    }
                                }
                            }
                        }
                        //System.out.println();
                        if (possibleTiles.size() > 0) {
                            for(int[] tile : possibleTiles) {
                                //System.out.println("(" + tile[0] + " , " + tile[1] + ")");
                            }
                            // since 3x3 with 1 hero
                            double probH = 1.0 / (double)possibleTiles.size();
                            BigDecimal bd = new BigDecimal(probH).setScale(3, RoundingMode.HALF_UP);
                            probH = bd.doubleValue();
                            for (int[] tile : possibleTiles) {
                                board[tile[0]][tile[1]].probHero = probH;
                            }
                        }

                    } else {
                        // check if AI has hero
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 2) {
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 3x3 with 1 hero
                                double probH = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probH).setScale(3, RoundingMode.HALF_UP);
                                probH = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = probH;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = 0.0;
                                }
                            }
                        }

                    }

                } else { // mage / HE
                    if (pieceList.size() > 0) {
                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false ) {
                                        sharedNeighbors.add(neighbor);
                                }
                            }
                        }
                        List<int[]> possibleTiles = new ArrayList<>();
                        if (sharedNeighbors.size() > 0) {
                            for(int[] neighbor : sharedNeighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                            }
                            // find neighbor whose neighbors are all the pieces in the piece list
                            for (int[] neighbor : sharedNeighbors) {
                                List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                if (humanNeighbors.size() == pieceList.size()) {
                                    boolean good = false;
                                    for (int[] piece : pieceList) {
                                        good = alreadyHas(humanNeighbors, piece);
                                        if (!good) {
                                            break;
                                        }
                                    }
                                    if (good && neighbor[0] <= aiTurns) {
                                        possibleTiles.add(neighbor);
                                    }
                                }
                            }
                        }
                        //System.out.println();
                        if (possibleTiles.size() > 0) {
                            for(int[] tile : possibleTiles) {
                                //System.out.println("(" + tile[0] + " , " + tile[1] + ")");
                            }
                            // since 3x3 with 1 mage
                            double probM = 1.0 / (double)possibleTiles.size();
                            BigDecimal bd = new BigDecimal(probM).setScale(3, RoundingMode.HALF_UP);
                            probM = bd.doubleValue();
                            for (int[] tile : possibleTiles) {
                                board[tile[0]][tile[1]].probMage = probM;
                            }
                        }

                    } else {
                        // check if AI has mage
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 3) {
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 3x3 with 1 hero
                                double probM = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probM).setScale(3, RoundingMode.HALF_UP);
                                probM = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = probM;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = 0.0;
                                }
                            }
                        }

                    }
                }
            } // ends the for-loop

        }
        else if (size == 6) {
            // go through the list of observations for 6x6 (S,N,HE,B)
            for (int i = 0 ; i  <= 3 ; ++i) {
                List<int[]> pieceList = getPieceList(i);

                if (i == 1) { // Wumpus / S
                    if (pieceList.size() > 0) {

                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }

                        // get number of AI wumpuses on the board
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 1) {
                                numWump++;
                            }
                        }

                        //need to make the list of possible tiles
                        if (numWump == 1) {
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            }
                        } else { // more than 1 wumpus
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    boolean good = false;
                                    for (int[] piece : pieceList) {
                                        good = alreadyHas(humanNeighbors, piece);
                                        if (!good) {
                                            break;
                                        }
                                    }
                                    if (good) {
                                        possibleTiles.add(neighbor);
                                    }
                                }
                            }
                            // was possibleTiles ...
                            if (sharedNeighbors.size() > 0) {
                                // need to incorporate all the other tiles as well
                                List<int[]> allOtherTiles = new ArrayList<>();
                                for (int k = 0 ; k < size ; ++k)  {
                                    for (int l = 0 ; l < size ; ++l ) {
                                        int[] curPlace = {k,l};
                                        if (board[curPlace[0]][curPlace[1]].color != 1) {
                                            List<int[]> humans = getHumanNeighbors(curPlace);
                                            if (humans.size() == 0) {
                                                allOtherTiles.add(curPlace);
                                            }
                                        }
                                    }
                                }

                                // go through human pieces, find ones with no Stench and take away from shared neighbors if it contains it
                                List<int[]> currentHPieces = getCurrentHumanPieces();
                                for(int[] p : currentHPieces) {
                                    if (board[p[0]][p[1]].hasStench == false) {
                                        List<int[]> pNei = getAllNeighbors(p);
                                        for(int[] n : pNei) {
                                            if (alreadyHas(sharedNeighbors,n)) {
                                                int indexToRem = -1;
                                                for(int q = 0 ; q  < sharedNeighbors.size() ; ++q) {
                                                    int[] curNE = sharedNeighbors.get(q);
                                                    if (curNE[0] == n[0] && curNE[1] == n[1]) {
                                                        indexToRem = q;
                                                    }
                                                }
                                                sharedNeighbors.remove(indexToRem);
                                            }
                                        }
                                    }
                                }

                                // since 6x6 with > 1 wumpuses (was possibleTiles)
                                double probW = (double)numWump / ((double)sharedNeighbors.size() + (double)allOtherTiles.size());
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : sharedNeighbors) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                                for (int[] tile : allOtherTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            }
                        }
                    } else {
                        // check if AI has wumpus
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 1) {
                                numWump++;
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with possibly more than 1 wumpus
                                double probW = (double)numWump / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = 0.0;
                                }
                            }
                        }
                    }
                } else if (i == 2) { // Hero / N
                    if (pieceList.size() > 0) {

                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }

                        // get number of AI heroes on the board
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 2) {
                                numWump++;
                            }
                        }

                        //need to make the list of possible tiles
                        if (numWump == 1) {
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                            }
                        } else { // more than 1 wumpus
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (sharedNeighbors.size() > 0) {
                                // need to incorporate all the other tiles as well
                                List<int[]> allOtherTiles = new ArrayList<>();
                                for (int k = 0 ; k < size ; ++k)  {
                                    for (int l = 0 ; l < size ; ++l ) {
                                        int[] curPlace = {k,l};
                                        if (board[curPlace[0]][curPlace[1]].color != 1) {
                                            List<int[]> humans = getHumanNeighbors(curPlace);
                                            if (humans.size() == 0) {
                                                allOtherTiles.add(curPlace);
                                            }
                                        }
                                    }
                                }

                                // go through human pieces, find ones with no Stench and take away from shared neighbors if it contains it
                                List<int[]> currentHPieces = getCurrentHumanPieces();
                                for(int[] p : currentHPieces) {
                                    if (board[p[0]][p[1]].hasNoise == false) {
                                        List<int[]> pNei = getAllNeighbors(p);
                                        for(int[] n : pNei) {
                                            if (alreadyHas(sharedNeighbors,n)) {
                                                int indexToRem = -1;
                                                for(int q = 0 ; q  < sharedNeighbors.size() ; ++q) {
                                                    int[] curNE = sharedNeighbors.get(q);
                                                    if (curNE[0] == n[0] && curNE[1] == n[1]) {
                                                        indexToRem = q;
                                                    }
                                                }
                                                sharedNeighbors.remove(indexToRem);
                                            }
                                        }
                                    }
                                }

                                // since 6x6 with > 1 heroes
                                double probW = (double)numWump / ((double)sharedNeighbors.size() + (double)allOtherTiles.size());
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : sharedNeighbors) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                                for (int[] tile : allOtherTiles) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                            }
                        }
                    } else {
                        // check if AI has wumpus
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 2) {
                                numWump++;
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with possibly more than 1 wumpus
                                double probW = (double)numWump / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = 0.0;
                                }
                            }
                        }
                    }

                } else if (i == 3) { // Mage / HE

                    if (pieceList.size() > 0) {

                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }

                        // get number of AI heroes on the board
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 3) {
                                numWump++;
                            }
                        }

                        //need to make the list of possible tiles
                        if (numWump == 1) {
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                            }
                        } else { // more than 1 wumpus
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (sharedNeighbors.size() > 0) {
                                // need to incorporate all the other tiles as well
                                List<int[]> allOtherTiles = new ArrayList<>();
                                for (int k = 0 ; k < size ; ++k)  {
                                    for (int l = 0 ; l < size ; ++l ) {
                                        int[] curPlace = {k,l};
                                        if (board[curPlace[0]][curPlace[1]].color != 1) {
                                            List<int[]> humans = getHumanNeighbors(curPlace);
                                            if (humans.size() == 0) {
                                                allOtherTiles.add(curPlace);
                                            }
                                        }
                                    }
                                }

                                // go through human pieces, find ones with no Stench and take away from shared neighbors if it contains it
                                List<int[]> currentHPieces = getCurrentHumanPieces();
                                for(int[] p : currentHPieces) {
                                    if (board[p[0]][p[1]].hasHeat == false) {
                                        List<int[]> pNei = getAllNeighbors(p);
                                        for(int[] n : pNei) {
                                            if (alreadyHas(sharedNeighbors,n)) {
                                                int indexToRem = -1;
                                                for(int q = 0 ; q  < sharedNeighbors.size() ; ++q) {
                                                    int[] curNE = sharedNeighbors.get(q);
                                                    if (curNE[0] == n[0] && curNE[1] == n[1]) {
                                                        indexToRem = q;
                                                    }
                                                }
                                                sharedNeighbors.remove(indexToRem);
                                            }
                                        }
                                    }
                                }

                                // since 6x6 with > 1 heroes
                                double probW = (double)numWump / ((double)sharedNeighbors.size() + (double)allOtherTiles.size());
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : sharedNeighbors) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                                for (int[] tile : allOtherTiles) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                            }
                        }
                    } else {
                        // check if AI has wumpus
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 3) {
                                numWump++;
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with possibly more than 1 wumpus
                                double probW = (double)numWump / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = 0.0;
                                }
                            }
                        }
                    }


                } else { // Pit / B

                }
            } // ends the for-loop

        }
        else {
            // go through the list of observations for 6x6 (S,N,HE,B)
            for (int i = 0 ; i  <= 3 ; ++i) {
                List<int[]> pieceList = getPieceList(i);

                if (i == 1) { // Wumpus / S
                    if (pieceList.size() > 0) {

                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }

                        // get number of AI wumpuses on the board
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 1) {
                                numWump++;
                            }
                        }

                        //need to make the list of possible tiles
                        if (numWump == 1) {
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            }
                        } else { // more than 1 wumpus
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    boolean good = false;
                                    for (int[] piece : pieceList) {
                                        good = alreadyHas(humanNeighbors, piece);
                                        if (!good) {
                                            break;
                                        }
                                    }
                                    if (good) {
                                        possibleTiles.add(neighbor);
                                    }
                                }
                            }
                            // was possibleTiles ...
                            if (sharedNeighbors.size() > 0) {
                                // need to incorporate all the other tiles as well
                                List<int[]> allOtherTiles = new ArrayList<>();
                                for (int k = 0 ; k < size ; ++k)  {
                                    for (int l = 0 ; l < size ; ++l ) {
                                        int[] curPlace = {k,l};
                                        if (board[curPlace[0]][curPlace[1]].color != 1) {
                                            List<int[]> humans = getHumanNeighbors(curPlace);
                                            if (humans.size() == 0) {
                                                allOtherTiles.add(curPlace);
                                            }
                                        }
                                    }
                                }

                                // go through human pieces, find ones with no Stench and take away from shared neighbors if it contains it
                                List<int[]> currentHPieces = getCurrentHumanPieces();
                                for(int[] p : currentHPieces) {
                                    if (board[p[0]][p[1]].hasStench == false) {
                                        List<int[]> pNei = getAllNeighbors(p);
                                        for(int[] n : pNei) {
                                            if (alreadyHas(sharedNeighbors,n)) {
                                                int indexToRem = -1;
                                                for(int q = 0 ; q  < sharedNeighbors.size() ; ++q) {
                                                    int[] curNE = sharedNeighbors.get(q);
                                                    if (curNE[0] == n[0] && curNE[1] == n[1]) {
                                                        indexToRem = q;
                                                    }
                                                }
                                                sharedNeighbors.remove(indexToRem);
                                            }
                                        }
                                    }
                                }

                                // since 6x6 with > 1 wumpuses (was possibleTiles)
                                double probW = (double)numWump / ((double)sharedNeighbors.size() + (double)allOtherTiles.size());
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : sharedNeighbors) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                                for (int[] tile : allOtherTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            }
                        }
                    } else {
                        // check if AI has wumpus
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 1) {
                                numWump++;
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with possibly more than 1 wumpus
                                double probW = (double)numWump / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = 0.0;
                                }
                            }
                        }
                    }
                } else if (i == 2) { // Hero / N
                    if (pieceList.size() > 0) {

                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }

                        // get number of AI heroes on the board
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 2) {
                                numWump++;
                            }
                        }

                        //need to make the list of possible tiles
                        if (numWump == 1) {
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                            }
                        } else { // more than 1 wumpus
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (sharedNeighbors.size() > 0) {
                                // need to incorporate all the other tiles as well
                                List<int[]> allOtherTiles = new ArrayList<>();
                                for (int k = 0 ; k < size ; ++k)  {
                                    for (int l = 0 ; l < size ; ++l ) {
                                        int[] curPlace = {k,l};
                                        if (board[curPlace[0]][curPlace[1]].color != 1) {
                                            List<int[]> humans = getHumanNeighbors(curPlace);
                                            if (humans.size() == 0) {
                                                allOtherTiles.add(curPlace);
                                            }
                                        }
                                    }
                                }

                                // go through human pieces, find ones with no Stench and take away from shared neighbors if it contains it
                                List<int[]> currentHPieces = getCurrentHumanPieces();
                                for(int[] p : currentHPieces) {
                                    if (board[p[0]][p[1]].hasNoise == false) {
                                        List<int[]> pNei = getAllNeighbors(p);
                                        for(int[] n : pNei) {
                                            if (alreadyHas(sharedNeighbors,n)) {
                                                int indexToRem = -1;
                                                for(int q = 0 ; q  < sharedNeighbors.size() ; ++q) {
                                                    int[] curNE = sharedNeighbors.get(q);
                                                    if (curNE[0] == n[0] && curNE[1] == n[1]) {
                                                        indexToRem = q;
                                                    }
                                                }
                                                sharedNeighbors.remove(indexToRem);
                                            }
                                        }
                                    }
                                }

                                // since 6x6 with > 1 heroes
                                double probW = (double)numWump / ((double)sharedNeighbors.size() + (double)allOtherTiles.size());
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : sharedNeighbors) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                                for (int[] tile : allOtherTiles) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                            }
                        }
                    } else {
                        // check if AI has wumpus
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 2) {
                                numWump++;
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with possibly more than 1 wumpus
                                double probW = (double)numWump / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = 0.0;
                                }
                            }
                        }
                    }

                } else if (i == 3) { // Mage / HE

                    if (pieceList.size() > 0) {

                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }

                        // get number of AI heroes on the board
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 3) {
                                numWump++;
                            }
                        }

                        //need to make the list of possible tiles
                        if (numWump == 1) {
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                            }
                        } else { // more than 1 wumpus
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (sharedNeighbors.size() > 0) {
                                // need to incorporate all the other tiles as well
                                List<int[]> allOtherTiles = new ArrayList<>();
                                for (int k = 0 ; k < size ; ++k)  {
                                    for (int l = 0 ; l < size ; ++l ) {
                                        int[] curPlace = {k,l};
                                        if (board[curPlace[0]][curPlace[1]].color != 1) {
                                            List<int[]> humans = getHumanNeighbors(curPlace);
                                            if (humans.size() == 0) {
                                                allOtherTiles.add(curPlace);
                                            }
                                        }
                                    }
                                }

                                // go through human pieces, find ones with no Stench and take away from shared neighbors if it contains it
                                List<int[]> currentHPieces = getCurrentHumanPieces();
                                for(int[] p : currentHPieces) {
                                    if (board[p[0]][p[1]].hasHeat == false) {
                                        List<int[]> pNei = getAllNeighbors(p);
                                        for(int[] n : pNei) {
                                            if (alreadyHas(sharedNeighbors,n)) {
                                                int indexToRem = -1;
                                                for(int q = 0 ; q  < sharedNeighbors.size() ; ++q) {
                                                    int[] curNE = sharedNeighbors.get(q);
                                                    if (curNE[0] == n[0] && curNE[1] == n[1]) {
                                                        indexToRem = q;
                                                    }
                                                }
                                                sharedNeighbors.remove(indexToRem);
                                            }
                                        }
                                    }
                                }

                                // since 6x6 with > 1 heroes
                                double probW = (double)numWump / ((double)sharedNeighbors.size() + (double)allOtherTiles.size());
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : sharedNeighbors) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                                for (int[] tile : allOtherTiles) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                            }
                        }
                    } else {
                        // check if AI has wumpus
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 3) {
                                numWump++;
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with possibly more than 1 wumpus
                                double probW = (double)numWump / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = 0.0;
                                }
                            }
                        }
                    }


                } else { // Pit / B

                }
            }
        }

    } // ends the updateProbs() method





    // will zero out all the probs of all non-human piece tiles
    public void zeroOutProbsAI() {
        for (int i = 0 ; i < size ; ++i) {
            for (int j = 0 ; j < size ; ++j) {
                if (board[i][j].color != 0) {
                    board[i][j].probPit = 0.0;
                    board[i][j].probMage = 0.0;
                    board[i][j].probHero = 0.0;
                    board[i][j].probWumpus = 0.0;
                }
            }
        }
    }




    // this is the method called to update probabilities for the human every turn
    public void updateProbsAI() throws InterruptedException {
        // zero out the probs first
        zeroOutProbsAI();

        if (size == 3) {
            // go through list of observations for 3x3 (S,N,HE)
            for (int i = 1; i <= 3 ; ++i) {
                List<int[]> pieceList = getPieceListAI(i);

//                System.out.println(i + " , " + pieceList.size());
//                if (pieceList.size() > 0) {
//                    for (int[] piece : pieceList) {
//                        System.out.println("(" + piece[0] + " , " + piece[1] + ")");
//                    }
//                }

                if (i == 1) { // wumpus / S
                    if (pieceList.size() > 0) {
                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }
                        List<int[]> possibleTiles = new ArrayList<>();
                        if (sharedNeighbors.size() > 0) {
                            for(int[] neighbor : sharedNeighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                            }
                            // find neighbor whose neighbors are all the pieces in the piece list
                            for (int[] neighbor : sharedNeighbors) {
                                List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                if (humanNeighbors.size() == pieceList.size()) {
                                    boolean good = false;
                                    for (int[] piece : pieceList) {
                                        good = alreadyHas(humanNeighbors, piece);
                                        if (!good) {
                                            break;
                                        }
                                    }
                                    if (good) {
                                        possibleTiles.add(neighbor);
                                    }
                                }
                            }
                        }
                        //System.out.println();
                        if (possibleTiles.size() > 0) {
                            for(int[] tile : possibleTiles) {
                                //System.out.println("(" + tile[0] + " , " + tile[1] + ")");
                            }
                            // since 3x3 with 1 wumpus
                            double probW = 1.0 / (double)possibleTiles.size();
                            BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                            probW = bd.doubleValue();
                            for (int[] tile : possibleTiles) {
                                board[tile[0]][tile[1]].probWumpus = probW;
                            }
                        }

                    } else {

                        // check if AI has wumpus
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 1) {
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 3x3 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = 0.0;
                                }
                            }
                        }

                    }

                } else if (i == 2) { // hero / N
                    if (pieceList.size() > 0) {
                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }
                        List<int[]> possibleTiles = new ArrayList<>();
                        if (sharedNeighbors.size() > 0) {
                            for(int[] neighbor : sharedNeighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                            }
                            // find neighbor whose neighbors are all the pieces in the piece list
                            for (int[] neighbor : sharedNeighbors) {
                                List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                if (humanNeighbors.size() == pieceList.size()) {
                                    boolean good = false;
                                    for (int[] piece : pieceList) {
                                        good = alreadyHas(humanNeighbors, piece);
                                        if (!good) {
                                            break;
                                        }
                                    }
                                    if (good) {
                                        possibleTiles.add(neighbor);
                                    }
                                }
                            }
                        }
                        //System.out.println();
                        if (possibleTiles.size() > 0) {
                            for(int[] tile : possibleTiles) {
                                //System.out.println("(" + tile[0] + " , " + tile[1] + ")");
                            }
                            // since 3x3 with 1 hero
                            double probH = 1.0 / (double)possibleTiles.size();
                            BigDecimal bd = new BigDecimal(probH).setScale(3, RoundingMode.HALF_UP);
                            probH = bd.doubleValue();
                            for (int[] tile : possibleTiles) {
                                board[tile[0]][tile[1]].probHero = probH;
                            }
                        }

                    } else {
                        // check if AI has hero
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 2) {
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 3x3 with 1 hero
                                double probH = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probH).setScale(3, RoundingMode.HALF_UP);
                                probH = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = probH;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = 0.0;
                                }
                            }
                        }

                    }

                } else { // mage / HE
                    if (pieceList.size() > 0) {
                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false ) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }
                        List<int[]> possibleTiles = new ArrayList<>();
                        if (sharedNeighbors.size() > 0) {
                            for(int[] neighbor : sharedNeighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                            }
                            // find neighbor whose neighbors are all the pieces in the piece list
                            for (int[] neighbor : sharedNeighbors) {
                                List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                if (humanNeighbors.size() == pieceList.size()) {
                                    boolean good = false;
                                    for (int[] piece : pieceList) {
                                        good = alreadyHas(humanNeighbors, piece);
                                        if (!good) {
                                            break;
                                        }
                                    }
                                    if (good && neighbor[0] <= aiTurns) {
                                        possibleTiles.add(neighbor);
                                    }
                                }
                            }
                        }
                        //System.out.println();
                        if (possibleTiles.size() > 0) {
                            for(int[] tile : possibleTiles) {
                                //System.out.println("(" + tile[0] + " , " + tile[1] + ")");
                            }
                            // since 3x3 with 1 mage
                            double probM = 1.0 / (double)possibleTiles.size();
                            BigDecimal bd = new BigDecimal(probM).setScale(3, RoundingMode.HALF_UP);
                            probM = bd.doubleValue();
                            for (int[] tile : possibleTiles) {
                                board[tile[0]][tile[1]].probMage = probM;
                            }
                        }

                    } else {
                        // check if AI has mage
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 3) {
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 3x3 with 1 hero
                                double probM = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probM).setScale(3, RoundingMode.HALF_UP);
                                probM = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = probM;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = 0.0;
                                }
                            }
                        }

                    }
                }
            } // ends the for-loop

        }
        else if (size == 6) {
            // go through the list of observations for 6x6 (S,N,HE,B)
            for (int i = 0 ; i  <= 3 ; ++i) {
                List<int[]> pieceList = getPieceListAI(i);

                if (i == 1) { // Wumpus / S
                    if (pieceList.size() > 0) {

                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }

                        // get number of AI wumpuses on the board
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 1) {
                                numWump++;
                            }
                        }

                        //need to make the list of possible tiles
                        if (numWump == 1) {
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            }
                        } else { // more than 1 wumpus
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    boolean good = false;
                                    for (int[] piece : pieceList) {
                                        good = alreadyHas(humanNeighbors, piece);
                                        if (!good) {
                                            break;
                                        }
                                    }
                                    if (good) {
                                        possibleTiles.add(neighbor);
                                    }
                                }
                            }
                            // was possibleTiles ...
                            if (sharedNeighbors.size() > 0) {
                                // need to incorporate all the other tiles as well
                                List<int[]> allOtherTiles = new ArrayList<>();
                                for (int k = 0 ; k < size ; ++k)  {
                                    for (int l = 0 ; l < size ; ++l ) {
                                        int[] curPlace = {k,l};
                                        if (board[curPlace[0]][curPlace[1]].color != 1) {
                                            List<int[]> humans = getHumanNeighbors(curPlace);
                                            if (humans.size() == 0) {
                                                allOtherTiles.add(curPlace);
                                            }
                                        }
                                    }
                                }

                                // go through human pieces, find ones with no Stench and take away from shared neighbors if it contains it
                                List<int[]> currentHPieces = getCurrentHumanPieces();
                                for(int[] p : currentHPieces) {
                                    if (board[p[0]][p[1]].hasStench == false) {
                                        List<int[]> pNei = getAllNeighbors(p);
                                        for(int[] n : pNei) {
                                            if (alreadyHas(sharedNeighbors,n)) {
                                                int indexToRem = -1;
                                                for(int q = 0 ; q  < sharedNeighbors.size() ; ++q) {
                                                    int[] curNE = sharedNeighbors.get(q);
                                                    if (curNE[0] == n[0] && curNE[1] == n[1]) {
                                                        indexToRem = q;
                                                    }
                                                }
                                                sharedNeighbors.remove(indexToRem);
                                            }
                                        }
                                    }
                                }

                                // since 6x6 with > 1 wumpuses (was possibleTiles)
                                double probW = (double)numWump / ((double)sharedNeighbors.size() + (double)allOtherTiles.size());
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : sharedNeighbors) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                                for (int[] tile : allOtherTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            }
                        }
                    } else {
                        // check if AI has wumpus
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 1) {
                                numWump++;
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with possibly more than 1 wumpus
                                double probW = (double)numWump / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = 0.0;
                                }
                            }
                        }
                    }
                } else if (i == 2) { // Hero / N
                    if (pieceList.size() > 0) {

                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }

                        // get number of AI heroes on the board
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 2) {
                                numWump++;
                            }
                        }

                        //need to make the list of possible tiles
                        if (numWump == 1) {
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                            }
                        } else { // more than 1 wumpus
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (sharedNeighbors.size() > 0) {
                                // need to incorporate all the other tiles as well
                                List<int[]> allOtherTiles = new ArrayList<>();
                                for (int k = 0 ; k < size ; ++k)  {
                                    for (int l = 0 ; l < size ; ++l ) {
                                        int[] curPlace = {k,l};
                                        if (board[curPlace[0]][curPlace[1]].color != 1) {
                                            List<int[]> humans = getHumanNeighbors(curPlace);
                                            if (humans.size() == 0) {
                                                allOtherTiles.add(curPlace);
                                            }
                                        }
                                    }
                                }

                                // go through human pieces, find ones with no Stench and take away from shared neighbors if it contains it
                                List<int[]> currentHPieces = getCurrentHumanPieces();
                                for(int[] p : currentHPieces) {
                                    if (board[p[0]][p[1]].hasNoise == false) {
                                        List<int[]> pNei = getAllNeighbors(p);
                                        for(int[] n : pNei) {
                                            if (alreadyHas(sharedNeighbors,n)) {
                                                int indexToRem = -1;
                                                for(int q = 0 ; q  < sharedNeighbors.size() ; ++q) {
                                                    int[] curNE = sharedNeighbors.get(q);
                                                    if (curNE[0] == n[0] && curNE[1] == n[1]) {
                                                        indexToRem = q;
                                                    }
                                                }
                                                sharedNeighbors.remove(indexToRem);
                                            }
                                        }
                                    }
                                }

                                // since 6x6 with > 1 heroes
                                double probW = (double)numWump / ((double)sharedNeighbors.size() + (double)allOtherTiles.size());
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : sharedNeighbors) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                                for (int[] tile : allOtherTiles) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                            }
                        }
                    } else {
                        // check if AI has wumpus
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 2) {
                                numWump++;
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with possibly more than 1 wumpus
                                double probW = (double)numWump / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = 0.0;
                                }
                            }
                        }
                    }

                } else if (i == 3) { // Mage / HE

                    if (pieceList.size() > 0) {

                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }

                        // get number of AI heroes on the board
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 3) {
                                numWump++;
                            }
                        }

                        //need to make the list of possible tiles
                        if (numWump == 1) {
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                            }
                        } else { // more than 1 wumpus
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (sharedNeighbors.size() > 0) {
                                // need to incorporate all the other tiles as well
                                List<int[]> allOtherTiles = new ArrayList<>();
                                for (int k = 0 ; k < size ; ++k)  {
                                    for (int l = 0 ; l < size ; ++l ) {
                                        int[] curPlace = {k,l};
                                        if (board[curPlace[0]][curPlace[1]].color != 1) {
                                            List<int[]> humans = getHumanNeighbors(curPlace);
                                            if (humans.size() == 0) {
                                                allOtherTiles.add(curPlace);
                                            }
                                        }
                                    }
                                }

                                // go through human pieces, find ones with no Stench and take away from shared neighbors if it contains it
                                List<int[]> currentHPieces = getCurrentHumanPieces();
                                for(int[] p : currentHPieces) {
                                    if (board[p[0]][p[1]].hasHeat == false) {
                                        List<int[]> pNei = getAllNeighbors(p);
                                        for(int[] n : pNei) {
                                            if (alreadyHas(sharedNeighbors,n)) {
                                                int indexToRem = -1;
                                                for(int q = 0 ; q  < sharedNeighbors.size() ; ++q) {
                                                    int[] curNE = sharedNeighbors.get(q);
                                                    if (curNE[0] == n[0] && curNE[1] == n[1]) {
                                                        indexToRem = q;
                                                    }
                                                }
                                                sharedNeighbors.remove(indexToRem);
                                            }
                                        }
                                    }
                                }

                                // since 6x6 with > 1 heroes
                                double probW = (double)numWump / ((double)sharedNeighbors.size() + (double)allOtherTiles.size());
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : sharedNeighbors) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                                for (int[] tile : allOtherTiles) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                            }
                        }
                    } else {
                        // check if AI has wumpus
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 3) {
                                numWump++;
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with possibly more than 1 wumpus
                                double probW = (double)numWump / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = 0.0;
                                }
                            }
                        }
                    }


                } else { // Pit / B

                }
            } // ends the for-loop

        }
        else {
            // go through the list of observations for 6x6 (S,N,HE,B)
            for (int i = 0 ; i  <= 3 ; ++i) {
                List<int[]> pieceList = getPieceListAI(i);

                if (i == 1) { // Wumpus / S
                    if (pieceList.size() > 0) {

                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }

                        // get number of AI wumpuses on the board
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 1) {
                                numWump++;
                            }
                        }

                        //need to make the list of possible tiles
                        if (numWump == 1) {
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            }
                        } else { // more than 1 wumpus
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    boolean good = false;
                                    for (int[] piece : pieceList) {
                                        good = alreadyHas(humanNeighbors, piece);
                                        if (!good) {
                                            break;
                                        }
                                    }
                                    if (good) {
                                        possibleTiles.add(neighbor);
                                    }
                                }
                            }
                            // was possibleTiles ...
                            if (sharedNeighbors.size() > 0) {
                                // need to incorporate all the other tiles as well
                                List<int[]> allOtherTiles = new ArrayList<>();
                                for (int k = 0 ; k < size ; ++k)  {
                                    for (int l = 0 ; l < size ; ++l ) {
                                        int[] curPlace = {k,l};
                                        if (board[curPlace[0]][curPlace[1]].color != 1) {
                                            List<int[]> humans = getHumanNeighbors(curPlace);
                                            if (humans.size() == 0) {
                                                allOtherTiles.add(curPlace);
                                            }
                                        }
                                    }
                                }

                                // go through human pieces, find ones with no Stench and take away from shared neighbors if it contains it
                                List<int[]> currentHPieces = getCurrentHumanPieces();
                                for(int[] p : currentHPieces) {
                                    if (board[p[0]][p[1]].hasStench == false) {
                                        List<int[]> pNei = getAllNeighbors(p);
                                        for(int[] n : pNei) {
                                            if (alreadyHas(sharedNeighbors,n)) {
                                                int indexToRem = -1;
                                                for(int q = 0 ; q  < sharedNeighbors.size() ; ++q) {
                                                    int[] curNE = sharedNeighbors.get(q);
                                                    if (curNE[0] == n[0] && curNE[1] == n[1]) {
                                                        indexToRem = q;
                                                    }
                                                }
                                                sharedNeighbors.remove(indexToRem);
                                            }
                                        }
                                    }
                                }

                                // since 6x6 with > 1 wumpuses (was possibleTiles)
                                double probW = (double)numWump / ((double)sharedNeighbors.size() + (double)allOtherTiles.size());
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : sharedNeighbors) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                                for (int[] tile : allOtherTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            }
                        }
                    } else {
                        // check if AI has wumpus
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 1) {
                                numWump++;
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with possibly more than 1 wumpus
                                double probW = (double)numWump / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probWumpus = 0.0;
                                }
                            }
                        }
                    }
                } else if (i == 2) { // Hero / N
                    if (pieceList.size() > 0) {

                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }

                        // get number of AI heroes on the board
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 2) {
                                numWump++;
                            }
                        }

                        //need to make the list of possible tiles
                        if (numWump == 1) {
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                            }
                        } else { // more than 1 wumpus
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (sharedNeighbors.size() > 0) {
                                // need to incorporate all the other tiles as well
                                List<int[]> allOtherTiles = new ArrayList<>();
                                for (int k = 0 ; k < size ; ++k)  {
                                    for (int l = 0 ; l < size ; ++l ) {
                                        int[] curPlace = {k,l};
                                        if (board[curPlace[0]][curPlace[1]].color != 1) {
                                            List<int[]> humans = getHumanNeighbors(curPlace);
                                            if (humans.size() == 0) {
                                                allOtherTiles.add(curPlace);
                                            }
                                        }
                                    }
                                }

                                // go through human pieces, find ones with no Stench and take away from shared neighbors if it contains it
                                List<int[]> currentHPieces = getCurrentHumanPieces();
                                for(int[] p : currentHPieces) {
                                    if (board[p[0]][p[1]].hasNoise == false) {
                                        List<int[]> pNei = getAllNeighbors(p);
                                        for(int[] n : pNei) {
                                            if (alreadyHas(sharedNeighbors,n)) {
                                                int indexToRem = -1;
                                                for(int q = 0 ; q  < sharedNeighbors.size() ; ++q) {
                                                    int[] curNE = sharedNeighbors.get(q);
                                                    if (curNE[0] == n[0] && curNE[1] == n[1]) {
                                                        indexToRem = q;
                                                    }
                                                }
                                                sharedNeighbors.remove(indexToRem);
                                            }
                                        }
                                    }
                                }

                                // since 6x6 with > 1 heroes
                                double probW = (double)numWump / ((double)sharedNeighbors.size() + (double)allOtherTiles.size());
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : sharedNeighbors) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                                for (int[] tile : allOtherTiles) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                            }
                        }
                    } else {
                        // check if AI has wumpus
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 2) {
                                numWump++;
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with possibly more than 1 wumpus
                                double probW = (double)numWump / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probHero = 0.0;
                                }
                            }
                        }
                    }

                } else if (i == 3) { // Mage / HE

                    if (pieceList.size() > 0) {

                        // get shared neighbors of the pieces in the pieceList
                        List<int[]> sharedNeighbors = new ArrayList<>();
                        for (int[] piece : pieceList) {
                            List<int[]> neighbors = getAllNeighbors(piece);
                            for (int[] neighbor : neighbors) {
                                //System.out.println("(" + neighbor[0] + " , " + neighbor[1] + ")");
                                if (sharedNeighbors.size() == 0) {
                                    sharedNeighbors.add(neighbor);
                                } else if (alreadyHas(sharedNeighbors,neighbor) == false) {
                                    sharedNeighbors.add(neighbor);
                                }
                            }
                        }

                        // get number of AI heroes on the board
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 3) {
                                numWump++;
                            }
                        }

                        //need to make the list of possible tiles
                        if (numWump == 1) {
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with 1 wumpus
                                double probW = 1.0 / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                            }
                        } else { // more than 1 wumpus
                            List<int[]> possibleTiles = new ArrayList<>();
                            if (sharedNeighbors.size() > 0) {
                                // find neighbor whose neighbors are all the pieces in the piece list
                                for (int[] neighbor : sharedNeighbors) {
                                    List<int[]> humanNeighbors = getHumanNeighbors(neighbor);
                                    if (humanNeighbors.size() == pieceList.size()) {
                                        boolean good = false;
                                        for (int[] piece : pieceList) {
                                            good = alreadyHas(humanNeighbors, piece);
                                            if (!good) {
                                                break;
                                            }
                                        }
                                        if (good) {
                                            possibleTiles.add(neighbor);
                                        }
                                    }
                                }
                            }
                            if (sharedNeighbors.size() > 0) {
                                // need to incorporate all the other tiles as well
                                List<int[]> allOtherTiles = new ArrayList<>();
                                for (int k = 0 ; k < size ; ++k)  {
                                    for (int l = 0 ; l < size ; ++l ) {
                                        int[] curPlace = {k,l};
                                        if (board[curPlace[0]][curPlace[1]].color != 1) {
                                            List<int[]> humans = getHumanNeighbors(curPlace);
                                            if (humans.size() == 0) {
                                                allOtherTiles.add(curPlace);
                                            }
                                        }
                                    }
                                }

                                // go through human pieces, find ones with no Stench and take away from shared neighbors if it contains it
                                List<int[]> currentHPieces = getCurrentHumanPieces();
                                for(int[] p : currentHPieces) {
                                    if (board[p[0]][p[1]].hasHeat == false) {
                                        List<int[]> pNei = getAllNeighbors(p);
                                        for(int[] n : pNei) {
                                            if (alreadyHas(sharedNeighbors,n)) {
                                                int indexToRem = -1;
                                                for(int q = 0 ; q  < sharedNeighbors.size() ; ++q) {
                                                    int[] curNE = sharedNeighbors.get(q);
                                                    if (curNE[0] == n[0] && curNE[1] == n[1]) {
                                                        indexToRem = q;
                                                    }
                                                }
                                                sharedNeighbors.remove(indexToRem);
                                            }
                                        }
                                    }
                                }

                                // since 6x6 with > 1 heroes
                                double probW = (double)numWump / ((double)sharedNeighbors.size() + (double)allOtherTiles.size());
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : sharedNeighbors) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                                for (int[] tile : allOtherTiles) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                            }
                        }
                    } else {
                        // check if AI has wumpus
                        int numWump = 0;
                        List<int[]> aiPieces = getCurrentAIPieces();
                        boolean hasPiece = false;
                        for (int[] piece : aiPieces) {
                            if (board[piece[0]][piece[1]].color == 0 && board[piece[0]][piece[1]].type == 3) {
                                numWump++;
                                hasPiece = true;
                            }
                        }

                        if (hasPiece) {
                            // evenly distribute among tiles that are not neighbors of any human piece
                            List<int[]> possibleTiles = new ArrayList<>();
                            for (int k = 0 ; k < size ; ++k)  {
                                for (int l = 0 ; l < size ; ++l ) {
                                    int[] curPlace = {k,l};
                                    if (board[curPlace[0]][curPlace[1]].color != 1) {
                                        List<int[]> humans = getHumanNeighbors(curPlace);
                                        if (humans.size() == 0) {
                                            possibleTiles.add(curPlace);
                                        }
                                    }
                                }
                            }
                            if (possibleTiles.size() > 0) {
                                // since 6x6 with possibly more than 1 wumpus
                                double probW = (double)numWump / (double)possibleTiles.size();
                                BigDecimal bd = new BigDecimal(probW).setScale(3, RoundingMode.HALF_UP);
                                probW = bd.doubleValue();
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = probW;
                                }
                            } else {
                                for (int[] tile : possibleTiles) {
                                    board[tile[0]][tile[1]].probMage = 0.0;
                                }
                            }
                        }
                    }


                } else { // Pit / B

                }
            }
        }

    } // ends the updateProbsAI() method












    // will return piece list (list of human pieces that share the same observation)
    // type refers to what kind of observation
    // 1 = Wumpus = S
    // 2 = Hero = N
    // 3 = Mage = HE
    // 0 = Pit = B
    public List<int[]> getPieceList(int type) {
        List<int[]> pieceList = new ArrayList<>();
        List<int[]> humanPieces = getCurrentHumanPieces();
        if (type == 1) {
            for(int[] piece : humanPieces) {
                if (board[piece[0]][piece[1]].hasStench) {
                    pieceList.add(piece);
                }
            }
        } else if (type == 2) {
            for(int[] piece : humanPieces) {
                if (board[piece[0]][piece[1]].hasNoise) {
                    pieceList.add(piece);
                }
            }

        } else if (type == 3) {
            for(int[] piece : humanPieces) {
                if (board[piece[0]][piece[1]].hasHeat) {
                    pieceList.add(piece);
                }
            }

        } else {
            for(int[] piece : humanPieces) {
                if (board[piece[0]][piece[1]].hasBreeze) {
                    pieceList.add(piece);
                }
            }
        }
        return pieceList;
    } // ends the getPieceList()



    // will return piece list (list of human pieces that share the same observation)
    // type refers to what kind of observation
    // 1 = Wumpus = S
    // 2 = Hero = N
    // 3 = Mage = HE
    // 0 = Pit = B
    public List<int[]> getPieceListAI(int type) {
        List<int[]> pieceList = new ArrayList<>();
        List<int[]> AIPieces = getCurrentAIPieces();
        if (type == 1) {
            for(int[] piece : AIPieces) {
                if (board[piece[0]][piece[1]].hasStench) {
                    pieceList.add(piece);
                }
            }
        } else if (type == 2) {
            for(int[] piece : AIPieces) {
                if (board[piece[0]][piece[1]].hasNoise) {
                    pieceList.add(piece);
                }
            }

        } else if (type == 3) {
            for(int[] piece : AIPieces) {
                if (board[piece[0]][piece[1]].hasHeat) {
                    pieceList.add(piece);
                }
            }

        } else {
            for(int[] piece : AIPieces) {
                if (board[piece[0]][piece[1]].hasBreeze) {
                    pieceList.add(piece);
                }
            }
        }
        return pieceList;
    } // ends the getPieceList()







    
} // this ends the Board class