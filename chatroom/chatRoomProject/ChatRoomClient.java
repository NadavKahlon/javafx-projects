package chatRoomProject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*******************************************************************************
 * This class is the represents the client's application in the Chat-Room App.
 * The Chat-Room App is a client-server application that enables users to join
 * a public chat-room hosted on a remote server, and interact with it using an
 * easy-to-understand graphical user interface.
 * The classe's main method fires such a client program.
 * 
 * @author Nadav Kahlon
 */
public class ChatRoomClient extends Application
{
    @Override public void start(Stage stage) throws Exception
    {
        // load the fxml file containing the layout of the GUI
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ClientMainGUI.fxml"));
        Parent root = loader.load();
        ClientMainGUIController controller = loader.getController();
        
        // create a scene including this GUI, and run it
        Scene scene = new Scene(root);
        stage.setTitle("Chat-Room App");
        stage.setScene(scene);
        stage.setOnHidden((e) -> { controller.shutdown(); });
        stage.show();
    }
    
    /***************************************************************************
     * The main client program.
     * Runs an instance of a client in the Chat-Room App described above.
     * 
     * @param args arguments for the program (ignored).
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}
