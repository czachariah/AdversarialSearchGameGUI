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

public class Controller {

    public Board board;
    public int size;

    public boolean startSquareClicked;
    public boolean endSquareClicked;

    public int[] nextMove = new int[4];

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

    /**
     * This is the constructor of the Controller class.
     */
    public Controller() {
    } // constructor of the Controller class


    /**
     * This is the initializer of the Controller Class.
     */
    @FXML
    public void initialize() {
        initializeGridGUI(gridPane);
    } // ends the initialize() method


    public void initializeGridGUI(GridPane gridPane) {
        this.gridPane.setPadding(new Insets(5));
        this.gridPane.setHgap(5);
        this.gridPane.setVgap(5);

        this.gridPane = gridPane;
        if (Size3.isSelected()) {
            this.size = 3;
            this.board = new Board(size);
            this.board.treeDepth = 8;
            // choose heursitic (use 6 as temp for now)
            board.heuToUse = 6;
            colorBoard3();
        } else if (Size6.isSelected()) {
            //this.size = 6;
            //this.board = new Board(size);
        } else {
            //this.size = 9;
            //this.board = new Board(size);
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

    public void addClick(StackPane pane , int c , int r) {
        int realRow = r-1;
        int realCol = c-1;
        pane.setOnMouseClicked(e -> {
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
                endSquareClicked = true;
            }
        });
    }

}
