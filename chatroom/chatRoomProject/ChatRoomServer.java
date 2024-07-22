package chatRoomProject;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*******************************************************************************
 * This class is the represents the server's end in the Chat-Room App.
 * The Chat-Room App is a client-server application that enables users to join
 * a public chat-room hosted on a remote server, and interact with it using an
 * easy-to-understand graphical user interface.
 * The classe's static main method creates and fires such a server.
 * The server notifies the server manager about its acitivity to the standard
 * output.
 * 
 * @author Nadav Kahlon
 */
public class ChatRoomServer
{
    /***************************************************************************
     * Constants used by the program
     */
    
    // the port on which servers listen for clients (this constant is public so
    // clients can read it and know to which port they should connect)
    public static final int PORT = 8888;
    
    // format for a message displayed on the standard output right after the
    // server accepted the first signal from a new client ("%s" is where the
    // client's InetAddress is)
    private static final String NEW_CLIENT_CON_MSG =
            "Server got a first connection request from client at %s.";
    
    // format for a message displayed on the standard output when the first
    // messgae a client sends is not a JOIN type message ("%s" is where the
    // client InetAddress is)
    private static final String NOT_JOIN_FIRST_MSG =
            "The first message read from client %s is not a join message - it "
            + "is ignored.";
    
    // format for a message displayed on the standard output when a client tries
    // to join the chatroom with an already used username (the first "%s" is 
    // where the client's InetAddress is, and the second "%s" is the used name)
    private static final String USERNAME_DENIED_MSG =
            "Client at %s tried to join with the already used username \"%s\" "
            + "- its request is denied.";
    
    // format for a message displayed on the standard output when the server fails
    // to open I/O streams on a client's socket ("%s" is where the client's
    // InetAddress is)
    private static final String OPEN_IO_FAILED_MSG =
            "Server failed to open I/O streams on client at %s - client connection "
            + "request is ignored.";
    
    // format for a message displayed on the standard output when the server failed
    // to read the first message sent from a client (whose InetAddress is printed
    // in "%s")
    private static final String FIRST_READ_FAILED_MSG =
            "Failed to read the first message from client at %s - client is "
            + "ignored ignored";
    
    // format for a message displayed on the standard output when the server fails
    // to let a client enter the chatroom (after connection has been established)
    // due to I/O issues ("%s" is there the client's InetAddress is)
    private static final String JOIN_COM_ERROR_MSG =
            "Server failed to let client %s into the chat-room, after connection "
            + "has already been established. The client is ignored, but some "
            + "resources may not have been closed.";
    
    // format for a message displayed on the standard output when the server failed
    // to disconnect from a client (%s is where the client username is)
    private static final String COULDNT_DISCONNECT_MSG =
            "Disconnection from client associated with username %s was failed.";
    
    // format for a message displayed on the standard output after a new user has
    // joined the char  (the first "%s" is where the client's InetAddress is, and
    // the second "%s" is the client's username)
    private static final String NEW_USER_MSG =
            "Client at %s successfuly joined the chat under username \"%s\"";
    
    // format for a message displayed on the standard output after a user has
    // left the chat and disconnected from the server  (the first "%s" is where
    // the client's InetAddress is, and the second "%s" is the client's username)
    private static final String USER_LEFT_MSG =
            "Client at %s under username \"%s\" leaving the chat.";
    
    // format for a message displayed on the standard output after an exception
    // was thrown trying to read a message from client (the first "%s" is where
    // the client's InetAddress is, and the second "%s" is the client's username)
    private static final String USER_READ_MSG_ERROR =
            "An issue occured while trying to read a message from client at %s "
            + "under username \"%s\", disconnecting.";
    
    // format for a message displayed on the standard output after sending a 
    // message to a client threw an IOException (the first "%s" is where the
    // client's InetAddress is, and the second "%s" is the client's username)
    private static final String CLIENT_WRITE_FAILED_MSG =
            "Sending a message to client at %s under username \"%s\" failed, "
            + "disconnecting.";
    
    // format for a message displayed on the standard output after a client joining
    // the chat-room failed, but the server fails in closing its resources ("%s"
    // is where the client's InetAddress is)
    private static final String FAILED_JOIN_CLOSE_MSG =
            "Failed to close resources associated with client at %s, even though "
            + "client failed to join chat-room. Some resources may remain open "
            + "(and the server can't do enything about it).";
    
    /***************************************************************************
     * Attributes of the server.
     */
    
    // the server-socket associated with the server
    private final ServerSocket serverSocket;
    
    // a thread listening for new clients wishing to connect to the server
    private Thread acceptListener;
    
    // a mapping between usernames and the socket associated with them
    private final Map<String, Socket> socketMap;
    
    // a mapping between usernames and the input/output streams associated with
    // them
    private final Map<String, ObjectInputStream> inStreamMap;
    private final Map<String, ObjectOutputStream> outStreamMap;
    
    // a mapping between usernames and the threads associated with them
    // (listening for messages)
    private final Map<String, Thread> threadMap;
    
    /***************************************************************************
     * Constructor: creates a new server.
     * 
     * @throws IOException in case the server-socket could not be opened.
     */
    public ChatRoomServer() throws IOException
    {
        // create a server-socket and a the different maps associated with it
        serverSocket = new ServerSocket(PORT);
        socketMap = new HashMap<>();
        inStreamMap = new HashMap<>();
        outStreamMap = new HashMap<>();
        threadMap = new HashMap<>();
    }
    
    /***************************************************************************
     * Processes a join message collected from a client.
     * 
     * @param  clientSocket the socket associated with the requesting client.
     * @param clientInStream the object-input-stream associated with the requesting
     * cliemt.
     * @param clientOutStream the object-output-stream associated with the requesting
     * client.
     * @param joinMsg the joining message sent from the client (its FIRST message).
     * @return the client's new username if the operation was successful, or null
     * otherwise
     * @throws IOException if communicating the client failed. Note that In such
     * case the server may not have properly closed all resources.
     */
    private String processJoinMsg(Socket clientSocket, ObjectInputStream clientInStream,
            ObjectOutputStream clientOutStream, Message joinMsg)
            throws IOException
    {
        if (joinMsg.getType() != Message.Type.JOIN) {
            // in case first message is not of type JOIN - inform the manager and
            // ignore client
            clientOutStream.close(); clientInStream.close(); clientSocket.close();
            System.out.println(String.format(NOT_JOIN_FIRST_MSG,
                    clientSocket.getInetAddress()));
            return null;
        }
        
        // get sending user's name
        String username = joinMsg.getUsername();
        if (socketMap.keySet().contains(username)) {
            // if the username already exists - deny client and inform the manager
            clientOutStream.writeObject(
                    new Message(Message.Type.DENY_JOIN, null));
            clientOutStream.close(); clientInStream.close(); clientSocket.close();
            System.out.println(
                    String.format(USERNAME_DENIED_MSG,
                            clientSocket.getInetAddress(), username));
            return null;
        }
        
        // otherwise - confirm request, send user list, and record new user client
        clientOutStream.writeObject(new Message(Message.Type.CONFIRM_JOIN, null,
                        new ArrayList<>(socketMap.keySet())));
        socketMap.put(username, clientSocket);
        inStreamMap.put(username, clientInStream);
        outStreamMap.put(username, clientOutStream);
            
        // create, store, and fire a thread dealing with the
        // client's messages
        Thread clientThread = new Thread(
                () -> { listenForClientMsgs(username); });
        threadMap.put(username, clientThread);
        clientThread.start();
            
        // broadcast the client's join message to all users
        broadcastMsg(joinMsg);
        return username;
    }
    
    /***************************************************************************
     * Continuously tries to accept new clients that wish to connect to the
     * this server.
     * 
     * @throws IOException in case an I/O error occured while trying to connect
     * to a client.
     */
    private void listenForAccepts()
    {
        while (true) {
            // try to accept new client
            Socket clientSocket = null;
            InetAddress clientAdds = null;
            try {
                clientSocket = serverSocket.accept();
                clientAdds = clientSocket.getInetAddress();
            }
            catch (IOException e) {
                // an error here is fatal - exit
                e.printStackTrace();
                System.exit(1);
            }
            System.out.println(String.format(NEW_CLIENT_CON_MSG, clientAdds));
                
            // try opening I/O streams on it
            ObjectInputStream clientInStream = null;
            ObjectOutputStream clientOutStream = null;
            try { clientInStream = new ObjectInputStream(
                    clientSocket.getInputStream()); }
            catch (IOException e) {
                // if connecting failed - inform the manager and ignore client
                System.out.println(String.format(OPEN_IO_FAILED_MSG, clientAdds));
                try { clientSocket.close(); }
                catch (IOException e1) {
                    // an error here is problematic - inform the manager but don't crush
                    System.out.println(String.format(FAILED_JOIN_CLOSE_MSG, clientAdds));
                }
                continue;
            }
            try {
                clientOutStream = new ObjectOutputStream(
                    clientSocket.getOutputStream());
                clientOutStream.flush();
            }
            catch (IOException e) {
                // connecting failed - inform the manager and ignore client
                System.out.println(String.format(OPEN_IO_FAILED_MSG, clientAdds));
                try { clientInStream.close(); clientSocket.close(); }
                catch (IOException e1) {
                    // an error here is problematic - inform the manager but don't crush
                    System.out.println(String.format(FAILED_JOIN_CLOSE_MSG, clientAdds));
                }
                continue;
            }
            
            // try to read a join message from it, and continue processing its
            // request ONLY if the connection with it is correct (otherwise -
            // ignore it)
            Message joinMsg = null;
            try {
                joinMsg = (Message) clientInStream.readObject();
            }
            catch (Exception e2) {
                // reading failed - inform the manager and ignore cloent
                System.out.println(String.format(FIRST_READ_FAILED_MSG, clientAdds));
                try { clientOutStream.close(); clientInStream.close(); clientSocket.close(); }
                catch (IOException e1) {
                    // an error here is problematic - inform the manager but don't crush
                    System.out.println(String.format(FAILED_JOIN_CLOSE_MSG, clientAdds));
                }
                continue;
            }
            
            String username;
            try {
                // process the joining message
                username = processJoinMsg(clientSocket, clientInStream,
                        clientOutStream, joinMsg);
            } catch (IOException e) {
                // infrom the manager if an error occured
                System.out.println(String.format(JOIN_COM_ERROR_MSG, clientAdds));
                continue;
            }
            
            // if we reached here and the returned username is not null - a new user
            // has successfuly joined
            if (username != null)
                System.out.println(
                        String.format(NEW_USER_MSG, clientAdds, username));
        }
    }
    
    /***************************************************************************
     * Disconnects the server from a certain client (closes and discards the
     * associated networking and I/O resources, and broadcasts a leaving message
     * associated with the user).
     * 
     * @param username the name of the client to disconnect.
     */
    private void disconnectClient(String username)
    {
        try {
            // start by closing resources associted with it
            inStreamMap.get(username).close();
            outStreamMap.get(username).close();
            socketMap.get(username).close();
        }
        catch (IOException e) {
            // if for some reason we couldn't close a resource, this is a real
            // issue - notify the manager but do not crush the server
            System.out.println(String.format(COULDNT_DISCONNECT_MSG, username));
        }
        
        // remove the relevant entries from the server's tables
        inStreamMap.remove(username);
        outStreamMap.remove(username);
        socketMap.remove(username);
        
        // broadcast a leave message associated with the user
        broadcastMsg(new Message(Message.Type.LEAVE, username));
    }
    
    /***************************************************************************
     * Continuously listening for a certain client's messages, assuming that
     * it is already associated with a user in the server's tables.
     * 
     * @param username the username associated with the client.
     */
    private void listenForClientMsgs(String username)
    {
        // get client's input stream
        ObjectInputStream inStream = inStreamMap.get(username);
        InetAddress clientAdds = socketMap.get(username).getInetAddress();
        
        // Continuously collect messages
        try {
            while (true) {
                Message msg = (Message) inStream.readObject();
                Message.Type msgType = msg.getType();

                // make sure it's of type TEXT or LEAVE
                if (msgType != Message.Type.TEXT && msgType != Message.Type.LEAVE)
                    throw new IOException("Got message of invalid type from client");
                // make sure it's from the correct user
                if (! username.equals(msg.getUsername()))
                    throw new IOException("Got message from incorrect user name");
                
                
                // broadcast it for the rest of the users if it's text
                if (msgType == Message.Type.TEXT)
                    broadcastMsg(msg);
                
                // if it's a leave message - disconnect the client
                if (msgType == Message.Type.LEAVE) {
                    System.out.println(String.format(USER_LEFT_MSG, 
                            socketMap.get(username).getInetAddress(), username));
                    disconnectClient(username);
                    break;
                }
            }
        }
        catch (Exception e) {
            // if an issue occured with the sent message - inform the manager
            // and try to disconnect the failing client
            System.out.println(String.format(USER_READ_MSG_ERROR, 
                    clientAdds, username));
            disconnectClient(username);
        }
    }
    
    /***************************************************************************
     * Sends a certain message to all clients participating in the chat-room.
     * 
     * @param msg the message to broadcast.
     */
    private void broadcastMsg(Message msg)
    {
        System.out.printf("Broadcasting message of type %s from user \"%s\".%n",
                msg.getType(), msg.getUsername());
        
        // check all participating users
        for (String username : socketMap.keySet()) {
            try {
                // try to send the message to it
                outStreamMap.get(username).writeObject(msg);
            }
            catch (IOException e) {
                // if an error occured - tell the manager and disconnect the client
                System.out.println(String.format(CLIENT_WRITE_FAILED_MSG, 
                        socketMap.get(username).getInetAddress(), username));
                disconnectClient(username);
            }
        }
    }
    
    /***************************************************************************
     * Starts the server.
     */
    public void start()
    {
        // the only thing necessary is to initiate a thread accepting clients
        acceptListener = new Thread(() -> { listenForAccepts(); });
        acceptListener.start();
    }
    
    /***************************************************************************
     * The main server program.
     * Runs an instance of a server in the Chat-Room App described above.
     * 
     * @param args arguments for the program (ignored).
     */
    public static void main(String[] args)
    {
        ChatRoomServer server;
        try {
            // create server and start it
            server = new ChatRoomServer();
            System.out.println("Server created successfuly.");
            server.start();
        }
        catch (IOException e) {
            // if creating the server's socket failed - notify the manager
            System.out.println("Failed to open a server-socket for the new server.");
        }
    }
}
