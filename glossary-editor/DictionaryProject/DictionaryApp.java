package dictionaryProject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*******************************************************************************
 * This class contains the main program for the Dictionary App.
 * The Dictionary App is an editor for dictionaries of terms and definitions.
 * For full detail about how to use the app, launch it and select
 * [Help]->[How to Use].
 * 
 * @author Nadav Kahlon
 */
public class DictionaryApp extends Application {
    
    @Override public void start(Stage stage) throws Exception {
        // load the fxml file containing the layout of the display
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        
        // create a scene including this display, and run it
        Scene scene = new Scene(root);
        stage.setTitle("Dictionary App");
        stage.setScene(scene);
        stage.show();
    }
    
    /***************************************************************************
     * The main program.
     * Runs the Dictionary App described above.
     * 
     * @param args arguments for the program (ignored)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
