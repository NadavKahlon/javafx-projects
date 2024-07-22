package connect4Game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*******************************************************************************
 * This class is the main class for a graphical Connect-4 game.
 * 
 * @author Nadav Kahlon
 */
public class Connect4Game extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        // load the fxml file containing the layout of the display
        Parent root = FXMLLoader.load(getClass().getResource("Connect4Game.fxml"));

        // create a scene including this display, and run it
        Scene scene = new Scene(root);
        stage.setTitle("Connect4Game");
        stage.setScene(scene);
        stage.show();
    }
    
    /***************************************************************************
     * The main program.
     * Runs a game of Connect-4.
     * 
     * @param args arguments for the program (ignored)
     */
    public static void main(String[] args) {
        // launch the application
        launch(args);
    }
}
