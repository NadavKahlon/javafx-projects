package connect4Game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/*******************************************************************************
 * This class is the controller for the GUI of the Connect-4 Game application
 * (whose main routine is defined in the Connect4Game class).
 * 
 * @author Nadav Kahlon
 */
public class Connect4GameController {
    /***************************************************************************
     * Constants used by the controller.
     */
    
    // number of columns and rows in the game grid (respectively)
    private static final int GRID_WIDTH = 7;
    private static final int GRID_HEIGHT = 6;
    
    // radius of the circles that represent discs on the game GridPane
    private static final double DISC_RADIUS = 28.0;
    
    // color and width of the circles that represent discs on the game GridPane
    private static final Color DISC_STROKE_COLOR = Color.BLACK;
    private static final double DISC_STROKE_WIDTH = 3.0;
    
    // the number of discs in a vertical, horizontal or diagonal sequence
    // that compose a winning sequence of disks
    private static final int SEQUENCE_SIZE = 4;
    
    /***************************************************************************
     * An enumeration type representing discs in a Connect-4 game, each belongs
     * to a specific player and having a specific color.
     */
    private enum Disc {
        /* Possible Discs */
        RED("The red player", Color.RED),
        BLUE("The blue player", Color.BLUE);
        
        /* Attributes of a Disc */
        private final String playerName; // name of the player that the disc belongs to
        private final Color color; // color of the disc
        
        /* Methods of a Disc */
        // Constructor - creates a new Disc belonging to the player named 'pName'
        // with the color 'color'.
        private Disc(String pname, Color color) {
            this.playerName = pname;
            this.color = color;
        }
    }
    
    /***************************************************************************
     * FXML ttributes of the controller
     */
    
    // the grid pane representing the game board into which discs are inserted
    @FXML private GridPane gameGridPane;
    
    // the clear button (a button to restart the game)
    @FXML private Button clearButton;

    // buttons for adding discs into the different columns of the game grid
    @FXML private Button column0Button;
    @FXML private Button column1Button;
    @FXML private Button column2Button;
    @FXML private Button column3Button;
    @FXML private Button column4Button;
    @FXML private Button column5Button;
    @FXML private Button column6Button;
    
    /***************************************************************************
     * Additional attributes the controller stores
     */
    
    // an array to keep track of the next free cell in every column of the game
    // grid (indexed by column number);
    private final int[] nextFreeCell = new int[GRID_WIDTH];
    
    // a multidemnsional array of Disc objects containing the discs in the cells
    // of the game grid, or null if a cell is empty
    private final Disc[][] discGrid = new Disc[GRID_WIDTH][GRID_HEIGHT];
    
    // the current disc to insert into the game grid
    private Disc currDisc;
    
    /***************************************************************************
     * Initializes the controller.
     * Runs right after the window is loaded.
     */
    @FXML
    public void initialize()
    {
        // attach column indices to the column buttons
        column0Button.setUserData(0);
        column1Button.setUserData(1);
        column2Button.setUserData(2);
        column3Button.setUserData(3);
        column4Button.setUserData(4);
        column5Button.setUserData(5);
        column6Button.setUserData(6);
        
        // start a new game
        restartGame();
    }
    
    /***************************************************************************
     * Starts a new game by re-initializing all the state objects of the
     * controller.
     */
    private void restartGame()
    {
        // clear game grid (remove all circles in it)
        gameGridPane.getChildren().remove(1, gameGridPane.getChildren().size());
        
        // empty all the cells in the disc grid
        for (int x = 0; x < GRID_WIDTH; x++)
            for (int y = 0; y < GRID_HEIGHT; y++)
                discGrid[x][y] = null;
        
        // set the next free cell in the grid to be lowest cells
        for (int x = 0; x < GRID_WIDTH; x++)
            nextFreeCell[x] = GRID_HEIGHT-1;
        
        // enable all column buttons (since no column is now full)
        column0Button.setDisable(false);
        column1Button.setDisable(false);
        column2Button.setDisable(false);
        column3Button.setDisable(false);
        column4Button.setDisable(false);
        column5Button.setDisable(false);
        column6Button.setDisable(false);
        
        // set the first disc as the one that should be inserted next
        currDisc = Disc.values()[0];
    }
    
    /***************************************************************************
     * Updates the next disc to be played to be the following Disc constant in
     * the list of values of the Disc enum type.
     */
    private void updateDiscTurn()
    {
        // calculate the index of the following Disc constant
        int i = (currDisc.ordinal() + 1) % Disc.values().length;
        // set currDisc to it
        currDisc = Disc.values()[i];
    }
    
    /***************************************************************************
     * Check if the game grid is full.
     * 
     * @return A boolean stating whether the game grid is full
     */
    private boolean isGridFull()
    {
        // we simply check if the next free cell in every column is out of the grid
        for (int y : nextFreeCell)
            if (y >= 0) return false; // there is a free slot in a clumn
        // if we reached here - all columns are full
        return true;
    }
    
    /***************************************************************************
     * Checks if a given disc appears in an horizontal, vertical or diagonal
     * full sequence on the game grid, containing a cell at a given coordinates.
     * 
     * @param disc the disc to check for a sequence of.
     * @param x the x coordiante of the cell to look for.
     * @param y the y coordinate of the cell to look for.
     * @return A boolean value, stating whether such a  sequence exists.
     */
    private boolean checkForSequence(Disc disc, int x, int y)
    {
        /*
         * Algorithm idea: if there exist a sequence of SEQUENCE_SIZE sequential
         * appearances of 'disc' containing the (x,y) cell, the x coordinate of
         * the discs in it will be between x-(SEQUENCE_SIZE-1) and x+(SEQUENCE_SIZE-1)
         * and their y coordinate will be between y-(SEQUENCE_SIZE-1) and
         * y+(SEQUENCE_SIZE-), so we look for sequences only between these
         * boundries.
         * To do this, we scan 4 possible axes of length [2*SEQUENCE_SIZE-1] around
         * the required cell (x,y) - a vertical axis ('verAxis'), an horizontal axis
         * ('horAxis'), a diagonal axis going from top-left to bottom-right
         * ('diag1Axis'), and a diagonal axis going from bottom-left to top-right.
         * ('diag2Axis').
         * For each axis we keep a counter of the number of sequential appearances
         * of 'disc' we have encountered so far, zeroing the counter when encountering
         * a different disc. If the counter reaches SEQUENCE_SIZE - there is such
         * a sequence, otherwise - there is not.
         */
        
        // scan 4 axes around the (x,y) cell
        final String[] axes = {"verAxis", "horAxis", "diag1Axis", "diag2Axis"};
        for (String axis : axes) {
            // initialize a counter for the 'disc' sequence length on the current axis
            int counter = 0;

            // scan the axis based on an offset
            for (int offset = 1 - SEQUENCE_SIZE; offset <= SEQUENCE_SIZE - 1; offset++) {
                // calculate coordinates of current cell in the scan based on the axis
                int currX, currY;
                switch (axis)
                {
                    case "verAxis": // vertical 
                        currX = x; currY = y + offset;
                        break;
                    case "horAxis": // horizontal 
                        currX = x + offset; currY = y;
                        break;
                    case "diag1Axis": // diagonal from top-left to bottom-right
                        currX = x + offset; currY = y + offset;
                        break;
                    case "diag2Axis": // diagonal from bottom-left to top-right
                        currX = x + offset; currY = y - offset;
                        break;
                    default: // default will never be reached
                        currX = 0; currY = 0;
                        break;
                }

                // check if the currnt cell is inside the grid and has 'disc' in it
                if (0 <= currX && currX < GRID_WIDTH && 0 <= currY && currY < GRID_HEIGHT
                        && discGrid[currX][currY] == disc) {
                    // if so - update the sequence counter and check if a full sequence
                    // was reached
                    counter++;
                    if (counter == SEQUENCE_SIZE)
                        return true; 
                }
                else
                    counter = 0; // zero the counter if we encountered a different disc
            }
        }
        
        // if we reached here - the required sequence does not exist
        return false;
    }
    
    /***************************************************************************
     * Displays a dialog box announcing that a round is won by some player.
     * 
     * @param playerName the name of the winning player.
     */
    private void announceWinner(String playerName)
    {
        // create and display an alert box containing the message
        Alert alert = new Alert(null,
                String.format("%s won the round!%n"+
                              "Starting a new round.",
                currDisc.playerName),
                ButtonType.OK);
        alert.setTitle("Connect4Game");
        alert.showAndWait();
    }
    
    /***************************************************************************
     * Displays a dialog box declaring that a round ends with a tie.
     */
    private void declareTie()
    {
        Alert alert = new Alert(null,
                String.format("The round ended with a tie!%n"+
                              "Starting a new round."),
                ButtonType.OK);
        alert.setTitle("Connect4Game");
        alert.showAndWait();
    }
    
    /***************************************************************************
     * Handles events in which one of the column button was pressed (to insert a
     * disc into that column): adds the right disc (if
     * possible).
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML private void columnButtonPressed(ActionEvent event) {
        // get the button that generated the event
        Button button = (Button)event.getSource();
        // get the index of the corresponding column
        int x = (int)button.getUserData();
        // get the y coordinate of the next free slot in this column
        int y = nextFreeCell[x];
        
        // add the disc to the disc grid and draw a disc-circle in the GridPane
        discGrid[x][y] = currDisc;
        Circle discCircle = new Circle(DISC_RADIUS, currDisc.color);
        discCircle.setStroke(DISC_STROKE_COLOR);
        discCircle.setStrokeWidth(DISC_STROKE_WIDTH);
        gameGridPane.add(discCircle, x, y);
        
        // update the next free cell in this column and disable its button if it's full
        nextFreeCell[x]--;
        if (nextFreeCell[x] < 0)
            button.setDisable(true);
        
        // check if placing the new disc created a winning sequence
        if (checkForSequence(currDisc, x, y)) {
            // if so - announce the winner and restart the game
            announceWinner(currDisc.playerName);
            restartGame();
        }
        // check if placing the new disc filled the entire grid
        else if (isGridFull()) {
            // if so - declare a tie and restart the game
            declareTie();
            restartGame();
        }
        // otherwise - proceed to the next turn of a disc
        else
            updateDiscTurn();
    }
    
    /***************************************************************************
     * Handles events in which the clear (restart) button is used: clears the
     * game grid and initializes a new game.
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML private void clearButtonPressed(ActionEvent event) {
        restartGame(); // simply use the restartGame function to restart
    }
}
