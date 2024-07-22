package chatRoomProject;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

/*******************************************************************************
 * This class is the controller for the "Join Chat-Room" window's GUI, as part
 * of the GUI of the client in the Chat-Room App (whose main method and full
 * description can be found in ChatRoomClient.java).
 * The "Join Chat-Room" window is a dialog window in which the user enters his
 * username and the hosting server's name, before joining a chat-room hosted on
 * a remote server. This information is then accessed by the main GUI controller,
 * which uses it to enter a chat-room.
 * 
 * @author Nadav Kahlon
 */
public class JoinWindowController {
    /***************************************************************************
     * Attributes of the controller
     */
    
    // the test-field in which the user enters his username
    @FXML private TextField usernameTextField;

    // the text-field in which the user enters the hosting server's name
    @FXML private TextField serverTextField;
    
    // a boolean stating whether the operation completed successfully (i.e. the
    // user pressed "Join") - initialized to false
    private boolean success = false;

    /***************************************************************************
     * Handles events in which the "Cancel" button is pressed.
     * Cancels the operation and closes the window.
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML private void cancelButtonPressed(ActionEvent event)
    {
        // simply close the "Join" window, and don't change the 'success'
        // atribute (as it is false by default); the "Join" window is identified
        // as the window in which the event occured
        Node  source = (Node) event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /***************************************************************************
     * Handles events in which the "Join" button is pressed.
     * Changes the operation state to a success, and closes the window.
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML private void joinButtonPressed(ActionEvent event)
    {
        // set 'success' to true to inform the main GUI controller
        success = true;
        
        // close the "Join" window; the "Join" window is identified as the
        // window in which the event occured
        // as the window in which the event occured
        Node  source = (Node) event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    /***************************************************************************
     * Returns a boolean stating whether the operation was completed successfuly
     * (i.e. whether the user pressed "Join").
     * 
     * @return a boolean stating whether the operation was completed successfuly.
     */
    public boolean isSuccess()
    {
        // simply returen the 'success' attribute
        return success;
    }
    
    /***************************************************************************
     * Gets the username entered by the user in the window.
     * 
     * @return the username entered.
     */
    public String getUsername()
    {
        // to do this, we return the String entered in the username TextField
        return usernameTextField.getText();
    }
    
    /***************************************************************************
     * Gets the server name entered by the user in the window.
     * 
     * @return the server name entered.
     */
    public String getServerName()
    {
        // to do this, we return the String entered in the server TextField
        return serverTextField.getText();
    }
}
