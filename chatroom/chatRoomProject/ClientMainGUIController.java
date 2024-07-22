package chatRoomProject;

import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.util.List;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.net.UnknownHostException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;

/*******************************************************************************
 * This class is the controller for the GUI of the client in the Chat-Room App
 * (whose main method and full description can be found in ChatRoomClient.java).
 * It handles GUI activity and networking.
 *
 * @author Nadav Kahlon
 */
public class ClientMainGUIController {
    /***************************************************************************
     * Constants used by the controller
     */
    
    // a message displayed on a label on the GUI when no username is associated
    // with the client
    private static final String NO_USERNAME_MSG = "No username associated.";
    
    // foramt for a message displayed on a label on the GUI telling the user what 
    // username is associated with the client ("%s" is where the username is)
    private static final String USERNAME_MSG = "Your username: %s";
    
    // a message displayed on a label on the GUI when the client is not 
    // connected to any server
    private static final String NO_SERVER_MSG = "Not connected to a server.";
    
    // format for a message displayed on a label on the GUI telling the user what 
    // server it is connected to ("%s" is where the server description is)
    private static final String SERVER_MSG = "Chatroom hosted on server: %s";
    
    // the title for the "Join Chat-Room" window, opened when the user tries
    // to join a new chat-room
    private static final String JOIN_WINDOW_TITLE = 
            "Chat-Room App: Join Chat-Room";
    
    // a message displayed in an error dialog box when the GUI of the "Join
    // Chat-Room" window cannot be loaded for some reason
    private static final String JOIN_GUI_LOAD_ERR_MSG =
            "Could not load the GUI for the Join window.";
    
    // format for a message displayed in an error dialog box when the client
    // fails to connect to a server ("%s" is where the server name is)
    private static final String CONNECT_FAILED_MSG =
            "Could not connect to server %s.";
    
    // format for a message displayed in an error dialog box when the client
    // fails to get a server's IP ("%s" is where the server name is)
    private static final String UNKNOWN_SERVER_MSG =
            "Could not find server %s.";
    
    // format for a message displayed in an error dialog box when the client
    // fails to transfer messages with the server ("%s" is where the server name
    // is)
    private static final String FAILED_SERVER_COM =
            "An error occured while communicating with server %s.";
    
    // format for a message displayed in an error dialog box when the user tries
    // to use an invalid username ("%s" is where the invalid username is).
    private static final String INVALID_USERNAME_MSG =
            "Invalid username: \"%s\"";
    
    // a message displayed in an error dialog box when the client fails to tell
    // the server that it tries to leave its chat-room
    private static final String LEAVE_ERROR_MSG =
            "Could not complete the leaving operation.";
    
    // a message displayed in an error dialog box when the client fails to send
    // a text message to the server
    private static final String FAILED_SEND_TEXT_MSG =
            "Could not send the message to the server.";
    
    // a message displayed in an error dialog box when the client fails to
    // read a message from the server, even though they're connected
    private static final String FAILED_CONNECTED_READ =
            "A server-read failed. Disconnecting.";
     
    // a message displayed in an error dialog box when the server sent an object
    // of unexpected type
    private static final String UNEXPECTED_OBJECT_MSG =
            "Got an unexpectetd message from server. Disconnecting.";
    
    // a message displayed in an error dialog box when the server returns EOF
    // after the client tries to read a message from it
    private static final String SERVER_EOF_MSG =
            "Server closed the connection. Exiting the room.";
    
    // format for a message displayed in an error dialog box when the user tries
    // to connect to a chat-room with an already used username (the first "%s" 
    // is where the server name is, and the second one is where the used
    // username is)
    private static final String USED_USERNAME_MSG =
            "Cannot join chat-box hosted on server %s, since username %s "
            + "is already used by another user participating in it.";
    
    // font for bolded text objects
    private static final Font BOLD_FONT = Font.font(null, FontWeight.BOLD, 12);
    
    // font for regular text objects
    private static final Font REGULAR_FONT = Font.font(null, 12);
    
    /***************************************************************************
     * Attributes of the controller.
     */
    
    // the ListView in which the names of the users participating in the chat
    // is displayed
    @FXML private ListView<String> usersListView;

    // the label in which the title of the users ListView is written
    @FXML private Label participantsLabel;

    // the TextFlow element in which the chat messages are displayed
    @FXML private TextFlow chatTextFlow;

    // the TextArea element in which the user enters messages to the chat
    @FXML private TextArea msgTextArea;

    // the "Send Message" button for sending the entered message
    @FXML private Button sendButton;

    // the "Join Room" button for joining a chat-room
    @FXML private Button joinButton;

    // the "Leave Room" button for leaving a chat-room
    @FXML private Button leaveButton;

    // the label in which we display information about the server hosting the
    // chat-room
    @FXML private Label serverLabel;
    
    // the label in which we dispaly the client's identifying username
    @FXML private Label usernameLabel;
    
    // a StringProperty representing the client's identifying username
    private StringProperty username;
    
    // a boolean stating whether the user is connected to a chatroom
    private BooleanProperty connected;
    
    // the TCP socket used for communicating with the server
    private Socket socket;
    
    // input and output streams attached to the socket
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    
    // a list of the usernames in the current chat-room
    private ObservableList<String> usersList;
    
    /***************************************************************************
     * Initializes the controller.
     * Runs right after the GUI is loaded.
     */
    @FXML public void initialize()
    {
        // start by creating non-fxml attributes from scratch
        username = new SimpleStringProperty();
        connected = new SimpleBooleanProperty();
        socket = new Socket();
        usersList = FXCollections.observableArrayList();
        
        // bind 'usersListView' to 'usersList'
        usersListView.setItems(usersList);
        
        // bind listeners to the various buttons
        joinButton.setOnAction((e) -> { openJoinWindow(); });
        leaveButton.setOnAction((e) -> { tryLeavingRoom(); });
        sendButton.setOnAction((e) -> { trySendTextMsg(); });
        
        // bind a listener to the 'username' and 'connected' properties
        username.addListener((ob, ov, nv) -> { updateUsernameLabel(); });
        connected.addListener((ob, ov, nv) -> { prepareGUI(); });
        
        // initialize both properties
        username.set(""); // (no username associated yet)
        connected.set(false); // (not connected to a chatroom yet)
    }
    
    /***************************************************************************
     * Prepares different elements of the GUI based on whether the client is
     * connected to a chatroom or not.
     */
    private void prepareGUI()
    {
        // a boolean stating whether we are in a chat-room or not
        boolean isInRoom = connected.get();
        
        // update the message on 'serverLabel' accordingly
        if (isInRoom)
            serverLabel.setText(String.format(SERVER_MSG, socket.getInetAddress()));
        else 
            serverLabel.setText(NO_SERVER_MSG);
        
        if (!isInRoom) {
            // if client is no longer in a chat-room - clear elements associated
            // with chatting
            chatTextFlow.getChildren().clear();
            msgTextArea.clear();
            usersList.clear();
        }
        
        // enable / disable elements required to be enabled only inside a room
        usersListView.disableProperty().set(!isInRoom);
        participantsLabel.disableProperty().set(!isInRoom);
        chatTextFlow.disableProperty().set(!isInRoom);
        msgTextArea.disableProperty().set(!isInRoom);
        sendButton.disableProperty().set(!isInRoom);
        leaveButton.disableProperty().set(!isInRoom);
        
        // enable / disable elements required to be enabled only outside a room
        joinButton.disableProperty().set(isInRoom);
    }
    
    /***************************************************************************
     * Updates 'usernameLabel' based on the current username of the client
     * (stored as StringProperty in 'username').
     */
    private void updateUsernameLabel()
    {
        if (username.get().equals("")) {
            // if no name is associated yet (empty name)
            usernameLabel.setText(NO_USERNAME_MSG);
        }
        else {
            // otherwise 
            usernameLabel.setText(String.format(USERNAME_MSG, username.get()));
        }
    }
    
    /***************************************************************************
     * Tries to connect to a server via the socket, and open I/O streams on it.
     *
     * @param serverName the name idetifying the server
     * @throws UnknownHostException if the server's IP could not be found.
     * @throws IOException if an error happenned when trying to connect to the
     * server.
     */
    private void tryConnecting(String serverName)
            throws UnknownHostException, IOException
    {
        // try connecting the socket to the server
        InetAddress serverAddress = InetAddress.getByName(serverName);
        SocketAddress socketAddress =
                new InetSocketAddress(serverAddress, ChatRoomServer.PORT);
        socket.connect(socketAddress);
        
        // open input/output streams on it to transfer messages
        outStream = new ObjectOutputStream(socket.getOutputStream());
        inStream = new ObjectInputStream(socket.getInputStream());
        outStream.flush();
    }
    
    /***************************************************************************
     * Tries to join a chatroom by requesting the server (assumes the client is
     * already connected to a server, and has already opened 'inStream' and
     * 'outStream').
     * 
     * @param newUsername the username we wish to join with.
     * @return Null if the request was denied due to the username being occupied,
     * or a list of the usernames of all clients participating in the chat if
     * the request was confirmed and we entered the chat-room.
     * @throws IOException if an error occured in the connection between the
     * client and the server.
     */
    @SuppressWarnings({"unchecked"}) // (for unchecked cast of generic type -
                                     // which isn't present in older java versions)
    private List<String> tryJoiningAs(String newUsername)
            throws IOException
    {
        // send a joining request message, with the new username as data
        outStream.writeObject(new Message(Message.Type.JOIN, newUsername));
        
        try {
            // collect the server's response
            Message response = (Message) inStream.readObject();
        
            if (response.getType() == Message.Type.CONFIRM_JOIN) {
                // if the server confirmed and we've joined the room - return
                // the users list attached to the confirmation message
                return (List<String>) response.getData();
            }
            else if (response.getType() != Message.Type.DENY_JOIN) {
                // in case the message type is not a join response at all -
                // something went wrong with the server
                throw new IOException(
                    "Got a message of unexpected type from server.");
            }
            else {
                // if we reached here - the server denied the request, which can
                // only mean that the username is already in use
                return null;
            }
        }
        catch (ClassNotFoundException e) {
            // this will never happen, as long as the 'Message' class can be
            // found in the controller's package; if it does happen - report
            // the error and exit
            e.printStackTrace();
            System.exit(1);
        }
        catch (ClassCastException e) {
            // this will happen if the server sent a confirmation message but
            // didn't attach a list of usernames to it, or didn't sent a Message
            // object at all; we relate to this as a communication error
            throw new IOException("Got a cofirmation message with unexpected "
                    + "data type from server.");
        }
        return null; // this will never be reached
    }
    
    /***************************************************************************
     * Disconnects from the current server (while closing all data streams).
     */
    private void disconnect()
    {
        try {
            // close I/O strems
            inStream.close();
            outStream.close();
            // disconnect the socket
            socket.close();
            socket = new Socket();
            // set 'connected' and 'username' as wer'e no longer connected
            connected.set(false);
            username.set("");
        }
        catch (IOException e) {
            // an exception trying to disconnect is fatal - exit the client
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /***************************************************************************
     * Processes the operatiion of joining a chat-room (tries to connect to the
     * server, requests joining to its chat-room, updates the client's state,
     * and displays the relevant error messages).
     * 
     * @param serverName the name of the server hosting the chat-room.
     * @param newUsername the username identifying the joining client.
     */
    private void processJoin(String serverName, String newUsername)
    {
        try {
            // try connecting to the server
            tryConnecting(serverName);
        }
        catch (UnknownHostException e) {
            // if the host could not be found - notify the user and end
            new Alert(Alert.AlertType.ERROR,
                    String.format(UNKNOWN_SERVER_MSG, serverName))
                    .showAndWait();
            return;
        }
        catch (IOException e) {
            // if an error occured - notify the user and end
            new Alert(Alert.AlertType.ERROR,
                    String.format(CONNECT_FAILED_MSG, serverName))
                    .showAndWait();
            return;
        }
        
        // try joining the chatroom
        List<String> chatUsers;
        try {
            chatUsers = tryJoiningAs(newUsername);
        }
        catch (IOException e1) {
            // if an error occured - notify the user, disconnect, and end
            new Alert(Alert.AlertType.ERROR,
                    String.format(FAILED_SERVER_COM, serverName))
                    .showAndWait();
            disconnect();
            return;
        }
        
        if (chatUsers != null) {
            // if we reached here - we successfuly joined the room; set 'connected',
            // 'username', and usersList
            connected.set(true);
            username.set(newUsername);
            usersList.addAll(chatUsers);
            // fire a message listening thread
            fireMsgListener();
        }
        else {
            // if 'tryJoiningAs' returned null - username is already used
            new Alert(Alert.AlertType.ERROR,
                    String.format(USED_USERNAME_MSG, serverName, username.get()))
                    .showAndWait();
            disconnect();
        }
    }
    
    /***************************************************************************
     * A static method for determining whether a username is valid in a
     * chat-room.
     * 
     * @param username the username in question.
     * @return A boolean stating whether 'username' is a valid username.
     */
    private static boolean isValidUsername(String username)
    {
        // since we use an empty String for identifying when there is no
        // username associated, a valid username can't be empty
        return username != null && !username.equals("");
    }
    
    /***************************************************************************
     * Opens a "Join Chat-Room" window asking for information before entering
     * a chat-room, and processes the required joining operation.
     */
    private void openJoinWindow()
    {
        // a variable to store the "Join" window's controller
        JoinWindowController joinWindowCtrl;
        
        try {
            // load the GUI elements of the window
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("JoinWindowGUI.fxml"));
            Parent root = loader.load();
            joinWindowCtrl = loader.getController();
            
            // set a stage for it
            Stage joinWindow = new Stage();
            Scene scene = new Scene(root);
            joinWindow.setScene(scene);

            // set the window's titles, show and wait for it
            joinWindow.setTitle(JOIN_WINDOW_TITLE);
            joinWindow.showAndWait(); /*!!!*/
        }
        catch (IOException e) {
            // if the GUI's fxml couldn't be loaded - notify the user and end
            new Alert(Alert.AlertType.ERROR, JOIN_GUI_LOAD_ERR_MSG).showAndWait();
            return;
        }
        
        // continue processing only if the user didn't cancel the operation
        if (joinWindowCtrl.isSuccess()) {
            // get the username and server name
            String serverName = joinWindowCtrl.getServerName();
            String newUsername = joinWindowCtrl.getUsername();
            // verify the user name
            if (isValidUsername(newUsername)) {
                // process the joining request
                processJoin(serverName, newUsername);
            }
            else {
                // if it's not valid - notify the user.
                new Alert(Alert.AlertType.ERROR,
                        String.format(INVALID_USERNAME_MSG, newUsername))
                        .showAndWait();
            }
        }
    }
    
    /***************************************************************************
     * Tries to leave the currently associated chat-room (only if the client is
     * not connected to any server).
     * Displays appropriate error message if the operation failed.
     */
    private void tryLeavingRoom()
    {
        // leaving is relevant only when connected
        if (connected.get()) {
            try {
                // send a "leave" message to the server
                outStream.writeObject(new Message(Message.Type.LEAVE,
                        username.get()));
            }
            catch (IOException e) {
                // if an error occured - notify the user and end
                new Alert(Alert.AlertType.ERROR, LEAVE_ERROR_MSG).showAndWait();
                return;
            }
            
            // disconnect from the server
            disconnect();
        }
    }
    
    /***************************************************************************
     * Tries to send the text message written on the message TextArea (only if
     * connected to a server).
     * Displays appropriate error message if the operation failed.
     * Does not display the message itself in the chatting TextFlow. IT will
     * be displayed after the server verifies it and broadcasts it to all
     * clients.
     */
    private void trySendTextMsg()
    {
        // sending text message is relevant only when connected
        if (connected.get()) {
            try {
                // try sending the message through the socket
                outStream.writeObject(new Message(
                        Message.Type.TEXT,
                        username.get(),
                        msgTextArea.getText()));
            }
            catch (IOException e) {
                // if an error occured - notify the user and end
                new Alert(Alert.AlertType.ERROR, FAILED_SEND_TEXT_MSG).showAndWait();
                return;
            }
            
            // clear the message field
            msgTextArea.clear();
        }
    }
    
    /***************************************************************************
     * Processes a message read from the server (which may indicate that a new
     * user is joining the chatroom, a new user is leaving the chatroom, or
     * some user sent a text message).
     * If the message is of type CONFIRM_JOIN or DENY_JOIN - does nothing.
     *
     * @param message the collected message
     */
    private void processMessage(Message message)
    {
        // collect the sender
        String sendingUser = message.getUsername();
        
        // act according to the message type
        switch (message.getType()) {
            case TEXT:
                // for a text message - simply print out the message
                Text sender = new Text(message.getUsername() + ": ");
                sender.setFont(BOLD_FONT);
                Text text = new Text(message.getData().toString() + '\n');
                text.setFont(REGULAR_FONT);
                Platform.runLater(() -> 
                    { chatTextFlow.getChildren().addAll(sender, text);} );
                break;
            case JOIN:
                // for a join message - add the user and print out a message
                Text joinText = new Text(sendingUser + " joined.\n");
                joinText.setFont(BOLD_FONT);
                Platform.runLater(() -> {
                    chatTextFlow.getChildren().add(joinText);
                    usersList.add(sendingUser); });
                break;
            case LEAVE:
                // for a leave message - remove the user and print out a message
                Text leaveText = new Text(sendingUser + " left.\n");
                leaveText.setFont(BOLD_FONT);
                Platform.runLater(() -> {
                    chatTextFlow.getChildren().add(leaveText);
                    usersList.remove(sendingUser); });
                break;
            default:
                break;
        }
    }
    
    /***************************************************************************
     * continuously waits for messages from the server, and processes them.
     * This is the main method of the message processing thread.
     */
    private void listenToMsgs()
    {
        // a boolean stating whether we should continue reading
        boolean keepReading = true;
        
        // keep reading until an interruption
        while (keepReading) {
            try {
                // try to read a message, and process it
                Message message = (Message) inStream.readObject();
                processMessage(message);
            }
            catch (EOFException e) {
                // in case the server closed - tell the user, disconnect and end
                Platform.runLater(() -> { new Alert(Alert.AlertType.ERROR,
                        SERVER_EOF_MSG).showAndWait();
                        disconnect(); });
                keepReading = false;
            }
            catch (IOException e1) {
                // in case of an error occured
                if (connected.get()) {
                    // if we are connected to a server and an I\O error occured -
                    // tell the user and disconnect
                    Platform.runLater(() -> { new Alert(Alert.AlertType.ERROR,
                            FAILED_CONNECTED_READ).showAndWait();
                            disconnect(); });
                    keepReading = false;
                }
                else {
                    // otherwise - it means that 'Leave Room' was pressed, so
                    // just terminate the message reading thread
                    keepReading = false;
                }
            }
            catch (ClassNotFoundException e2) {
                // we reach here in case the Message class is not in the same
                // package as this controller class - this is fatal, terminate
                // the client
                e2.printStackTrace();
                System.exit(1);
            }
            catch (ClassCastException e2) {
                // we reach here if the server sent an object which is not
                // a Message; we relate to this as a communication error
                Platform.runLater(() -> { new Alert(Alert.AlertType.ERROR,
                        UNEXPECTED_OBJECT_MSG).showAndWait();
                        disconnect(); });
                keepReading = false;
            }
        }
    }
    
    /***************************************************************************
     * Fires a message-reading thread associated with this client (it
     * automatically stops when necessary).
     */
    private void fireMsgListener()
    {
        // create and fire a new thread that runs 'listenToMsgs'
        new Thread(() -> { listenToMsgs(); }).start();
    }
    
    /***************************************************************************
     * A cleanup method, ran after the associated GUI window is closed.
     * Leaves connected chat-room and close networking resources.
     */
    public void shutdown()
    {
        // simply call 'tryLeavingRoom', this method will handle the required ops
        tryLeavingRoom();
    }
}