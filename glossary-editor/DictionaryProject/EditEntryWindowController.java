package dictionaryProject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.stage.Stage;

/*******************************************************************************
 * This is the controller class for the "Edit Entry" window used by the 
 * Dictionary App (whose main program, and full description, can be found in
 * DictionaryApp.java).
 * The "Edit Entry" window is a window in which the user can edit a dictionary
 * entry - whether it is a new one or an existing one. The result of this
 * window is sent to the controller of the main window (MainWindowController)
 * which applies the necessary changes to the dictionary it processes.
 * 
 * @author Nadav Kahlon
 */
public class EditEntryWindowController
{
    /***************************************************************************
     * Attributes of the controller.
     */

    // the text field in which the user edits the term of the dictionary entry
    @FXML private TextField termTextField;

    // the text are in which the user edits the definition of the term
    @FXML private TextArea definitionTextArea;

    // the label on which the title of the window is writeen
    @FXML private Label titleLabel;
    
    // a variable in which the edited entry is stored; it gets a non-null value
    // only after (and if) "Apply" was pressed
    private DictionaryEntry entry = null;
    
    /***************************************************************************
     * Returns the dictionary entry that has been edited (relevant only after
     * the stage was closed).
     * 
     * @return The edited dictionary entry, or null if "Apply" was not pressed.
     */
    public DictionaryEntry getDictionaryEntry()
    {
        // the corresponding entry - 'entry' - is set to a value which is not
        // null inly when "Apply" is pressed, otherwise it is null. So we can
        // simply return it (without copying, since DictionaryEntries are immutable)
        return entry;
    }
    
    /***************************************************************************
     * Handles events in which the "Apply" button is pressed.
     * Saves the edited dictionary entry (for later calls of 'getEntry'), and
     * closes the window.
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML private void applyButtonPressed(ActionEvent event)
    {
        // get the term and definition from the text areas
        String term = termTextField.getText();
        String definition = definitionTextArea.getText();
        
        // set a DictionaryEntry
        entry = new DictionaryEntry(term, definition);
        
        // close the window
        Node  source = (Node) event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /***************************************************************************
     * Handles events in which the "Cancel" button is pressed.
     * Closes the window without saving the written DictionaryEntry.
     * 
     * @param event 
     */
    @FXML private void cancelButtonPressed(ActionEvent event)
    {
        // there is no need to save a DictionaryEntry (and 'entry' is null by
        // default) - so just close the window
        Node  source = (Node) event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    /***************************************************************************
     * Sets the text label on the top of the window.
     * 
     * @param title the text to set.
     */
    public void setTitleLabel(String title)
    {
        // simply use Label.setText
        titleLabel.setText(title);
    }
    
    /***************************************************************************
     * Sets the term written on the "term" TextField
     * 
     * @param term the term to set.
     */
    public void setTerm(String term)
    {
        // simply use TextField.setText
        termTextField.setText(term);
    }
    
    /***************************************************************************
     * Sets the definition written on the "definition" TextArea
     * 
     * @param definition the definition to set.
     */
    public void setDefinition(String definition)
    {
        // simply use TextArea.setText
        definitionTextArea.setText(definition);
    }
    
    /***************************************************************************
     * Locks / unlocks the "term" TextField. When locked, it can't be edited.
     * 
     * @param toLock a boolean stating whether you wish to lock the TextField
     * (true) or unlock it (false).
     */
    public void setTermLock(boolean toLock)
    {
        // simply use TextField.setDisable to disable ("lock") the TextField
        termTextField.setDisable(toLock);
    }
}