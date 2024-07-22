package paintApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*******************************************************************************
 * This class is the main class of the Painter application.
 * The Painter application is a simple GUI-based program to draw basic shapes.
 * It has the following features:
 * > The user can draw lines, rectangles, or ellipses by dragging the mouse over
 *   the drawing pane.
 * > The user can choose between 4 colors - black, red, green and blue.
 * > The user can undo its actions or clear all the shapes by just pressing a
 *   button.
 * > The user can specify whether he wants to draw empty or filled-in shapes.
 * 
 * @author Nadav Kahlon
 */
public class PaintApp extends Application {
    /***************************************************************************
     * Initiates the display of the app on a given stage.
     * 
     * @param stage stage to display in.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        // load the fxml file containing the layout of the display
        Parent root = FXMLLoader.load(getClass().getResource("PaintApp.fxml"));

        // create a scene including this display, and run it
        Scene scene = new Scene(root);
        stage.setTitle("PaintApp");
        stage.setScene(scene);
        stage.show();
    }
    
    /***************************************************************************
     * The main program.
     * Runs the Paint app describes above.
     * 
     * @param args arguments for the program (ignored)
     */
    public static void main(String[] args) {
        // launch the application
        launch(args);
    }
    
}
