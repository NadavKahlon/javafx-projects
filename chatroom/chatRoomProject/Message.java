package chatRoomProject;

import java.io.Serializable;

/*******************************************************************************
 * This class represents a message sent in a chat-room, as part of the Chat-Room
 * App (whose main method and full description can be found in ChatRoomClient.java).
 * 
 * There are 3 types of messages:
 * - TEXT messages: these are normal text messages the users send to each
 *   other (through the server, of course) while chatting. The data they contain
 *   is a simple character string entered by the sending user.
 * - JOIN messages: these are special messages sent by client back-ends to
 *   hosting servers, indicating that the client wishes to enter a chatroom.
 *   The data they contain is the joining user's identifying username.
 * - LEAVE messages: these are special messages sent by client back-ends to
 *   hosting servers, indicating that the client wishes to leave a chatroom.
 *   The data they contain is the joining user's identifying username.
 * - CONFIRM_JOIN messages: these are special messages sent by hosting servers 
 *   to client back-ends, indicating that a server confirms a client's request 
 *   to join a chatroom. The data they contain is an ArrayList of Strings,
 *   representing the usernames of all clients participating in the chatroom.
 * - DENY_JOIN messages: these are special messages sent by hosting servers to
 *   client back-ends, indicating that a server rejects a client's request to
 *   join a chatroom, since a client with the same identifying username already
 *   participates in the chat-room.
 * 
 * @author Nadav Kahlon
 */
public class Message implements Serializable
{
    /***************************************************************************
     * An enum representing the different messages types (described in detail
     * above).
     */
    public enum Type {TEXT, JOIN, LEAVE, CONFIRM_JOIN, DENY_JOIN};
    
    /***************************************************************************
     * Attributes of a message
     */
    
    // the message type
    private final Type type;
    
    // the name associated with the sending user
    private final String username;
    
    // the data sent in the message
    private final Serializable data;
    
    /***************************************************************************
     * Constructor: creates a new message.
     * 
     * @param type the type of the message.
     * @param username the name of the user sending the message.
     * @param data the data sent in the message. Note: the class does not copy
     * the data object, but rather stores it as it is to save time (since it is
     * not necessary if the message is only used with serialization, and it only
     * is).
     */
    public Message(Type type, String username, Serializable data)
    {
        // simply set the messages's attributes
        this.type = type;
        this.username = username;
        this.data = data;
    }
    
    /***************************************************************************
     * Constructor: creates a new message with no content (null 'data' field).
     * 
     * @param type the type of the message.
     * @param username the name of the user sending the message.
     */
    public Message(Type type, String username)
    {
        this(type, username, null);
    }
    
    /***************************************************************************
     * Gets the type of the message.
     * 
     * @return the message's type.
     */
    public Type getType()
    {
        // simply return the enumerated type atribute (no need to copy)
        return type;
    }
    
    /***************************************************************************
     * Gets the username associated with the message.
     * 
     * @return the username.
     */
    public String getUsername()
    {
        // return the username attribute (no need to copy - Strings are immutable)
        return username;
    }
    
    /***************************************************************************
     * Gets the data sent with the message.
     * 
     * @return the data. Note: the method does not copy the data object, but
     * rather returns it as it is to save time (since it is
     * not necessary if the message is only used with serialization, and it only
     * is).
     */
    public Object getData()
    {
        // simply return the data attribute
        return data;
    }
}
