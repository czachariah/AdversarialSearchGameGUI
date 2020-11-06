package Visualizer;

import Board.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Controller {

    public Board board;
    public int size;

    public boolean startSquareClicked;
    public boolean endSquareClicked;
    public int[] nextMove = new int[4];

    public boolean gameStarted = false;

    @FXML
    public GridPane gridPane;

    @FXML
    public ToggleGroup ToggleSizes;

    @FXML
    public Toggle Size3;

    @FXML
    public Toggle Size6;

    @FXML
    public Toggle Size9;

    @FXML
    public ToggleGroup ToggleHeuristics;

    @FXML
    public Toggle HeuA;

    @FXML
    public Toggle HeuB;

    @FXML
    public Toggle HeuC;

    @FXML
    public Toggle HeuD;

    @FXML
    public Toggle HeuE;

    @FXML
    public Toggle HeuF;

    @FXML
    public Button playButton;

    @FXML
    public Button stopButton;

    @FXML
    public TextArea textArea;

    // not going to need a constructor for this class since, the sizes and stuff will change based on the choices


    /**
     * This is the initializer of the Controller Class.
     */
    @FXML
    public void initialize() {
        initializeGridGUI(gridPane);
    } // ends the initialize() method


    public void initializeGridGUI(GridPane gridPane) { // default will be a board of size 3
        this.gridPane = gridPane;
        if (Size3.isSelected()) {
            this.gridPane.setPadding(new Insets(5));
            this.gridPane.setHgap(5);
            this.gridPane.setVgap(5);
            this.size = 3;
            this.board = new Board(size);
            this.board.treeDepth = 5;
            if (HeuA.isSelected()) {
                board.heuToUse = 1;
            } else if (HeuB.isSelected()) {
                board.heuToUse = 2;
            } else if (HeuC.isSelected()) {
                board.heuToUse = 3;
            } else if (HeuD.isSelected()) {
                board.heuToUse = 4;
            } else if (HeuE.isSelected()) {
                board.heuToUse = 5;
            } else {
                board.heuToUse = 6;
            }
            colorBoard3();
        } else if (Size6.isSelected()) {
            this.gridPane.setPadding(new Insets(5));
            this.gridPane.setHgap(5);
            this.gridPane.setVgap(5);
            this.size = 6;
            this.board = new Board(size);
            this.board.treeDepth = 5;
            if (HeuA.isSelected()) {
                board.heuToUse = 1;
            } else if (HeuB.isSelected()) {
                board.heuToUse = 2;
            } else if (HeuC.isSelected()) {
                board.heuToUse = 3;
            } else if (HeuD.isSelected()) {
                board.heuToUse = 4;
            } else if (HeuE.isSelected()) {
                board.heuToUse = 5;
            } else {
                board.heuToUse = 6;
            }
            colorBoard6();
        } else {
            this.gridPane.setPadding(new Insets(5));
            this.gridPane.setHgap(5);
            this.gridPane.setVgap(5);
            this.size = 9;
            this.board = new Board(size);
            this.board.treeDepth = 3;
            if (HeuA.isSelected()) {
                board.heuToUse = 1;
            } else if (HeuB.isSelected()) {
                board.heuToUse = 2;
            } else if (HeuC.isSelected()) {
                board.heuToUse = 3;
            } else if (HeuD.isSelected()) {
                board.heuToUse = 4;
            } else if (HeuE.isSelected()) {
                board.heuToUse = 5;
            } else {
                board.heuToUse = 6;
            }
            colorBoard9();
        }
    }

    public void colorBoard3() {
        this.gridPane.getChildren().clear();

        Rectangle rect;
        Piece square;
        Color color = Color.BLACK;
        Piece[][] arr = board.board;

        for (int r = 1; r <= size; r++) {
            for (int c = 1; c <= size; c++) {
                square = arr[r-1][c-1];
                if (square.color == 0) {
                    color = Color.RED;
                } else if (square.color == 1) {
                    color = Color.WHITE;
                } else if (square.color == 2) {
                    color = Color.GREEN;
                } else {
                    color = Color.BLACK;
                }

                StackPane pane = new StackPane();
                Label text = new Label("PIT");

                if (square.type == 0) {
                    pane = new StackPane();
                    text = new Label("");
                    text.setFont(new Font(40));
                    rect = new Rectangle(222, 222, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                } else if (square.type == 1) {
                    pane = new StackPane();
                    text = new Label("WUMPUS");
                    text.setFont(new Font(40));
                    rect = new Rectangle(222, 222, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                } else if (square.type == 2) {
                    pane = new StackPane();
                    text = new Label("HERO");
                    text.setFont(new Font(40));
                    rect = new Rectangle(222, 222, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                } else if (square.type == 3) {
                    pane = new StackPane();
                    text = new Label("MAGE");
                    text.setFont(new Font(40));
                    rect = new Rectangle(222, 222, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                } else {
                    pane = new StackPane();
                    text = new Label("");
                    text.setFont(new Font(40));
                    rect = new Rectangle(222, 222, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                }
                addClick(pane,c,r);
                gridPane.add(pane, c, r);
            } // ends inner for-loop
        } // ends outer-for loop
    }

    public void colorBoard6() {
        this.gridPane.getChildren().clear();

        Rectangle rect;
        Piece square;
        Color color = Color.BLACK;
        Piece[][] arr = board.board;

        for (int r = 1; r <= size; r++) {
            for (int c = 1; c <= size; c++) {
                square = arr[r-1][c-1];
                if (square.color == 0) {
                    color = Color.RED;
                } else if (square.color == 1) {
                    color = Color.WHITE;
                } else if (square.color == 2) {
                    color = Color.GREEN;
                } else {
                    color = Color.BLACK;
                }

                StackPane pane = new StackPane();
                Label text = new Label("PIT");

                if (square.type == 0) {
                    pane = new StackPane();
                    text = new Label("");
                    text.setFont(new Font(20));
                    rect = new Rectangle(110, 110, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                } else if (square.type == 1) {
                    pane = new StackPane();
                    text = new Label("WUMPUS");
                    text.setFont(new Font(20));
                    rect = new Rectangle(110, 110, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                } else if (square.type == 2) {
                    pane = new StackPane();
                    text = new Label("HERO");
                    text.setFont(new Font(20));
                    rect = new Rectangle(110, 110, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                } else if (square.type == 3) {
                    pane = new StackPane();
                    text = new Label("MAGE");
                    text.setFont(new Font(20));
                    rect = new Rectangle(110, 110, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                } else {
                    pane = new StackPane();
                    text = new Label("");
                    text.setFont(new Font(20));
                    rect = new Rectangle(110, 110, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                }
                addClick(pane,c,r);
                gridPane.add(pane, c, r);
            } // ends inner for-loop
        } // ends outer-for loop
    }



    public void colorBoard9() {
        this.gridPane.getChildren().clear();

        Rectangle rect;
        Piece square;
        Color color = Color.BLACK;
        Piece[][] arr = board.board;

        for (int r = 1; r <= size; r++) {
            for (int c = 1; c <= size; c++) {
                square = arr[r-1][c-1];
                if (square.color == 0) {
                    color = Color.RED;
                } else if (square.color == 1) {
                    color = Color.WHITE;
                } else if (square.color == 2) {
                    color = Color.GREEN;
                } else {
                    color = Color.BLACK;
                }

                StackPane pane = new StackPane();
                Label text = new Label("PIT");

                if (square.type == 0) {
                    pane = new StackPane();
                    text = new Label("");
                    text.setFont(new Font(15));
                    rect = new Rectangle(70, 70, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                } else if (square.type == 1) {
                    pane = new StackPane();
                    text = new Label("WUMPUS");
                    text.setFont(new Font(15));
                    rect = new Rectangle(70, 70, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                } else if (square.type == 2) {
                    pane = new StackPane();
                    text = new Label("HERO");
                    text.setFont(new Font(15));
                    rect = new Rectangle(70, 70, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                } else if (square.type == 3) {
                    pane = new StackPane();
                    text = new Label("MAGE");
                    text.setFont(new Font(15));
                    rect = new Rectangle(70, 70, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                } else {
                    pane = new StackPane();
                    text = new Label("");
                    text.setFont(new Font(15));
                    rect = new Rectangle(70, 70, color); // entire grid made up of rectangles
                    pane.getChildren().addAll(rect,text);
                }
                addClick(pane,c,r);
                gridPane.add(pane, c, r);
            } // ends inner for-loop
        } // ends outer-for loop
    }




    public void addClick(StackPane pane , int c , int r) {
        int realRow = r-1;
        int realCol = c-1;
        pane.setOnMouseClicked(e -> {
            if (gameStarted == true) {
                if (startSquareClicked == false) {
                    nextMove[0] = realRow;
                    nextMove[1] = realCol;
                    //System.out.println(" " + realRow + " , " + realCol);
                    startSquareClicked = true;
                } else {
                    nextMove[2] = realRow;
                    nextMove[3] = realCol;
                    endSquareClicked = true;
                    //System.out.println(" " + realRow + " , " + realCol);
                    try {
                        makeHumanMove();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void makeHumanMove() throws InterruptedException {
        if (board.aiHasWon()) {
            textArea.appendText("\n\nAI WINS!");
            playButton.setDisable(false);
            gameStarted = false;
            return;
        } else if (board.manHasWon()) {
            textArea.appendText("\n\nHUMAN WINS!");
            playButton.setDisable(false);
            gameStarted = false;
            return;
        } else if (board.draw()) {
            textArea.appendText("\n\nDRAW!");
            playButton.setDisable(false);
            gameStarted = false;
            return;
        }

        textArea.appendText("\n\nHUMAN's MOVE:");
        if (nextMove[0] == -1) {
            textArea.appendText("\nHUMAN HAS STOPPED THE GAME");
            playButton.setDisable(false);
            gameStarted = false;
            return;
        } else {
            if (board.nextMove(nextMove,1)) {
                if (size == 3) {
                    printHumanMove(nextMove);
                    colorBoard3();
                    startSquareClicked = false;
                    endSquareClicked = false;
                    makeAIMove();
                    return;
                } else if (size == 6) {
                    printHumanMove(nextMove);
                    colorBoard6();
                    startSquareClicked = false;
                    endSquareClicked = false;
                    makeAIMove();
                    return;
                } else {
                    printHumanMove(nextMove);
                    colorBoard9();
                    startSquareClicked = false;
                    endSquareClicked = false;
                    makeAIMove();
                    return;
                }
            } else {
                textArea.appendText("\n\nBAD MOVE HUMAN, TRY AGAIN");
                startSquareClicked = false;
                endSquareClicked = false;
                return;
            }

        }
    }

    public void makeAIMove() {
        if (board.aiHasWon()) {
            textArea.appendText("\n\nAI WINS!");
            playButton.setDisable(false);
            gameStarted = false;
            return;
        } else if (board.manHasWon()) {
            textArea.appendText("\n\nHUMAN WINS!");
            playButton.setDisable(false);
            gameStarted = false;
            return;
        } else if (board.draw()) {
            textArea.appendText("\n\nDRAW!");
            playButton.setDisable(false);
            gameStarted = false;
            return;
        }
        int[] aiMove = board.findNextBestMove();
        while (board.nextMove(aiMove,0) == false) {
            aiMove = board.findNextBestMove();
        }
        if (size == 3) {
            printAIMove(aiMove);
            colorBoard3();
        } else if (size == 6) {
            printAIMove(aiMove);
            colorBoard6();
        } else {
            printAIMove(aiMove);
            colorBoard9();
        }

        if (board.aiHasWon()) {
            textArea.appendText("\n\nAI WINS!");
            playButton.setDisable(false);
            gameStarted = false;
            return;
        } else if (board.manHasWon()) {
            textArea.appendText("\n\nHUMAN WINS!");
            playButton.setDisable(false);
            gameStarted = false;
            return;
        } else if (board.draw()) {
            textArea.appendText("\n\nDRAW!");
            playButton.setDisable(false);
            gameStarted = false;
            return;
        }

        return;
    }

    public void stopGameClicked() throws InterruptedException {
        nextMove[0] = -1;
        startSquareClicked = true;
        endSquareClicked = true;
        gameStarted = false;
        makeHumanMove();
    }

    public void playButtonClicked() throws InterruptedException {
        // initialize the grid based on the chosen size and heuristic to use
        initializeGridGUI(gridPane);

        // disable play button until the end of the game
        playButton.setDisable(true);

        textArea.appendText("\n\nStarting Game:\nBoard size = " + size + " , Heuristic Chosen = " + ToggleHeuristics.getSelectedToggle().toString().substring(ToggleHeuristics.getSelectedToggle().toString().length()-3,ToggleHeuristics.getSelectedToggle().toString().length()));
        nextMove[0] = 0;
        nextMove[1] = 0;
        nextMove[2] = 0;
        nextMove[3] = 0;
        startSquareClicked = false;
        endSquareClicked = false;
        gameStarted = true;

    }


    public void printHumanMove(int[] move) {

        textArea.appendText("\n(" + move[0] + " , " + move[1] + ") to (" + move[2] + " , " + move[3] + ")");

    }

    public void printAIMove(int[] move) {

        textArea.appendText("\n\nAI's MOVE: \n(" + move[0] + " , " + move[1] + ") to (" + move[2] + " , " + move[3] + ")");


    }

}
