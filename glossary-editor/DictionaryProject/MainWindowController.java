package dictionaryProject;

import java.io.IOException;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

/*******************************************************************************
 * This is the controller class for the main window of the Dictionary App.
 * This app, whose main program can be found in DictionaryApp.java, offers
 * a nice interface to manipulate dictionaries of terms.
 * For more detail about the app, see the documentation of its main program
 * in DictionaryApp.java.
 * 
 * @author Nadav Kahlon
 */
public class MainWindowController
{
    /***************************************************************************
     * Constants used by the program.
     */
    
    // a message displayed on a label on the screen when no file is associated
    // with the current working dictionary
    private static final String NO_ASSOCIATED_FILE_MSG =
            "No file is associated yet.";
    
    // a message displayed on the main pane when no entry is chosen
    private static final String CHOOSE_ENTRY_MSG =
            "Select a dictionary entry to display, or add a new entry by "
            + "selecting [Edit] -> [Add Entry].";
    
    // the title for the file-chooser window for loading a dictionary
    private static final String LOAD_DICT_TITLE =
            "Dictionary App: Load Dictionary";
    
    // the title for the file-chooser window for loading a dictionary
    private static final String SAVE_DICT_TITLE =
            "Dictionary App: Save Dictionary";
    
    // filter for Dictionary files
    private static final String DICT_FILES_FILTER = "*.dict";
    
    // name of the type of Dictionary files
    private static final String DICT_FILES_TYPE = "Dictionary Files";
    
    // a message to display in a dialog box after loading a dictionary was failed
    // ("%s" is the spot in which the path to the dictionary is placed)
    private static final String COULDNT_LOAD_MSG =
            "Couldn't load Dictionary at:\n%s";
    
    // a message to display in a dialog box after saving a dictionary was failed
    // ("%s" is the spot in which the path to the dictionary is placed)
    private static final String COULDNT_SAVE_MSG =
            "Couldn't save Dictionary at:\n%s";
    
    // a message displayed near the associated path when the dictionary is saved
    private static final String DICT_SAVED_MSG =
            "Saved";
    
    // a message displayed near the associated path when the dictionary isn't saved
    private static final String DICT_NOT_SAVED_MSG =
            "Not Saved";
    
    // the actual title for the window in which the user edits a new dictionary entry
    private static final String ADD_ENTRY_STAGE_TITLE =
            "Dictionary App: Add Entry";
    
    // the label title for the window in which the user edits a new dictionary entry
    private static final String ADD_ENTRY_LABEL_TITLE =
            "Create Dictionary Entry";
    
    // the actual title for the window in which the user edits an existing dictionary entry
    private static final String EDIT_ENTRY_STAGE_TITLE =
            "Dictionary App: Edit Entry";
    
    // the label title for the window in which the user edits an existing dictionary entry
    private static final String EDIT_ENTRY_LABEL_TITLE =
            "Edit Dictionary Entry";
    
    // a message displayed in a dialog box when a user tries to add a dictionary
    // entry with an empty String as the term
    private static final String EMPTY_TERM_MSG =
            "Can't add a dictionary entry whose term is empty.";
    
    // a message displayed in a dialog box when a user tries to add a dictionary
    // entry whose term is already present in the dictionary ("%s" is where
    // the term is inserted)
    private static final String TERM_EXISTS_MSG =
            "An entry for the term \"%s\" is already present in the "
            + "dictionary.";
    
    // a message displayed in a dialog box when the user tries to edit an entry,
    // while no entry is selected
    private static final String EDIT_NO_ENTRY_MSG =
            "Select a dictionary entry to edit first!";
    
    /**************************************************************************
     * Attributes of the controller.
     */

    // a label on the main pane on which the title is displayed (i.e. a term)
    @FXML private Label titleLabel;

    // a label on the main pane on which the descriptionn is displayed (i.e. a
    // definition)
    @FXML private Label descriptionLabel;

    // a label on the bottom of the window telling the path of the current
    // file associated with the working dictionary
    @FXML private Label currPathLabel;

    // a TextField used to search for a specific term in the dictionary
    @FXML private TextField searchTermTextField;
    
    // the ListView container for the entries in the working dictionary
    @FXML private ListView<DictionaryEntry> entriesListView;
    
    // the current working dictionary
    private Dictionary dictionary;
    
    // the path for the file associated with the current dictionary (an empty
    // String if no file is associated yet)
    private StringProperty currPath;
    
    // a boolean property stating whether the current working dictionary is saved
    private BooleanProperty isDictSaved;

    /***************************************************************************
     * Initializes the controller.
     * Runs right after the window is loaded.
     */
    @FXML public void initialize()
    {
        // initialize a working dictionary and bind it to the entries ListView
        dictionary = new Dictionary();
        dictionary.bindToListView(entriesListView);
        entriesListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends DictionaryEntry> observable,
                    DictionaryEntry oldValue, DictionaryEntry newValue) -> {
                        // the listener only needs to update the displayed entry
                        displayEntry();
                    });
        displayEntry();
        
        // bind a change listener to the text property of 'searchTermTextField',
        // i.e. bind a method that will be called when the user searches for
        // a specific term
        searchTermTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // the listener sets the dictionarie's filtering predicate,
                    // so that only entries that match the searched term are
                    // displayed in the bound ListView
                    dictionary.setFilterPredicate(
                            entry -> entry.getTerm().toLowerCase()
                                    .startsWith(newValue.toLowerCase()));
                });
        
        // initialize currPath, and set it to "" (as no file is associated yet)
        currPath = new SimpleStringProperty();
        currPath.addListener(
                (ObservableValue<? extends String> observable,
                    String oldValue, String newValue) -> {
                        // the listener only needs to update the displayed path
                        displayPath();
                    });
        currPath.set("");
        
        // initialize dictSaved, and set it to False (as the dictionary is not saved)
        isDictSaved = new SimpleBooleanProperty();
        isDictSaved.addListener(
                (ObservableValue<? extends Boolean> observable,
                    Boolean oldValue, Boolean newValue) -> {
                        // the listener only needs to update the displayed path
                        displayPath();
                    });
        isDictSaved.set(false);
    }
    
    /***************************************************************************
     * Gets the window on which the GUI is diaplyed
     * 
     * @return the window.
     */
    private Window getWindow()
    {
        // simply return the window on which any element is displyed
        return this.titleLabel.getScene().getWindow();
    }
    
    /***************************************************************************
     * Displays the most recently selected DictionaryEntry on the main pane
     * If no entry is selected, displays a message telling the user to choose or
     * add an entry.
     */
    private void displayEntry()
    {
        // try to fetch selected entry
        DictionaryEntry selectedEntry =
                entriesListView.getSelectionModel().getSelectedItem();
        
        // set title and description to display
        String title = selectedEntry != null?
                selectedEntry.getTerm() : "";
        String description = selectedEntry != null?
                selectedEntry.getDefinition() : CHOOSE_ENTRY_MSG;
        
        // and display them
        titleLabel.setText(title);
        descriptionLabel.setText(description);
    }
    
    /***************************************************************************
     * Displays the path of the currently associated file on a label on the
     * bottom of the window, or a message saying that no file is associated yet
     * if no file is associated
     */
    private void displayPath()
    {
        // set the text of the label as a path is associated
        if (currPath.get().equals(""))
            currPathLabel.setText(NO_ASSOCIATED_FILE_MSG);
        else
            currPathLabel.setText(String.format("(%s) %s",
                    isDictSaved.get()? DICT_SAVED_MSG : DICT_NOT_SAVED_MSG,
                    currPath.getValue()));
    }

    /***************************************************************************
     * Handles events in which the "Add Entry" MenuItem in the "Edit" menu is
     * choosed.
     * Displays and waits for a new "EditEntry" window (see EditEntryWindow.fxml
     * and EditEntryWindowController.java), which enables the user to add a new
     * entry to the dictionary.
     * 
     * @param event the event that caused the handler to be called.
     * @throws IOException if it can't load "EditEntryWindow.fxml".
     */
    @FXML private void addEntryPressed(ActionEvent event) throws IOException
    {
        // create a fresh "EditEntry" window:
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("EditEntryWindow.fxml"));
        Parent root = loader.load();
        EditEntryWindowController editWindowCtrl = loader.getController();
        
        Stage editWindow = new Stage();
        Scene scene = new Scene(root);
        editWindow.setScene(scene);
        
        // set its titles, show and wait for it
        editWindow.setTitle(ADD_ENTRY_STAGE_TITLE);
        editWindowCtrl.setTitleLabel(ADD_ENTRY_LABEL_TITLE);
        editWindow.showAndWait();
        
        // get the edited entry, and make sure "Apply" was properly pressed
        DictionaryEntry newEntry = editWindowCtrl.getDictionaryEntry();
        if (newEntry != null) {
            
            if (newEntry.getTerm().equals("")) {
                // if the term is empty - notify the user
                new Alert(AlertType.ERROR, EMPTY_TERM_MSG).showAndWait();
            } 
            // try to add the entry
            else if (!dictionary.addEntry(newEntry)) {
                // if the term was already in the dictionary - notify the user
                new Alert(AlertType.ERROR,
                        String.format(TERM_EXISTS_MSG, newEntry.getTerm()))
                        .showAndWait();
            }
            else {
                // otherwise - simply make sure that the new entry is selected
                entriesListView.getSelectionModel().select(newEntry);
                // and that the new dictionary is not saved (it was just changed)
                isDictSaved.set(false);
            }
        }
    }

    /***************************************************************************
     * Handles events in which the "Delete Entry" MenuItem in the "Edit" menu is
     * choosed.
     * Removes the currently selected dictionary entry (if any is selected).
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML private void deleteEntryPressed(ActionEvent event)
    {
        // simply remove the selected entry from the dictionary (if any is selected)
        if (dictionary.removeEntry(
                entriesListView.getSelectionModel().getSelectedItem()))
            isDictSaved.set(false); // after it is changed, it isn't saved
    }

    /***************************************************************************
     * Handles events in which the "Edit Entry" MenuItem in the "Edit" menu is
     * choosed.
     * Displays and waits for a new "EditEntry" window (see EditEntryWindow.fxml
     * and EditEntryWindowController.java) containing information about the
     * currently selected dictionary entry (if any is selected), which enables
     * the user to update this information. 
     * 
     * @param event the event that caused the handler to be called.
     * @throws IOException if it can't load "EditEntryWindow.fxml".
     */
    @FXML private void editEntryPressed(ActionEvent event) throws IOException
    {
        // get the selected entry to edit
        DictionaryEntry selectedEntry =
                entriesListView.getSelectionModel().getSelectedItem();
        
        if (selectedEntry == null) {
            // if no entry was selected - notify the user
            new Alert(AlertType.ERROR, EDIT_NO_ENTRY_MSG).showAndWait();
        }
        else {
            // otherwise create a fresh "EditEntry" window to edit the entry
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("EditEntryWindow.fxml"));
            Parent root = loader.load();
            EditEntryWindowController editWindowCtrl = loader.getController();

            Stage editWindow = new Stage();
            Scene scene = new Scene(root);
            editWindow.setScene(scene);

            // set in it the selected entry to edit, and lock the term
            editWindowCtrl.setTerm(selectedEntry.getTerm());
            editWindowCtrl.setDefinition(selectedEntry.getDefinition());
            editWindowCtrl.setTermLock(true);

            // set the window's titles, show and wait for it
            editWindow.setTitle(EDIT_ENTRY_STAGE_TITLE);
            editWindowCtrl.setTitleLabel(EDIT_ENTRY_LABEL_TITLE);
            editWindow.showAndWait();

            // get the edited entry, and make sure "Apply" was properly pressed
            DictionaryEntry editedEntry = editWindowCtrl.getDictionaryEntry();
            if (editedEntry != null) {
                
                // replace the original entry with the new edited entry
                dictionary.removeEntry(selectedEntry);
                dictionary.addEntry(editedEntry);
                entriesListView.getSelectionModel().select(editedEntry);
                
                // the changed dictionary is not saved now
                isDictSaved.set(false);
            }
        }
    }

    /***************************************************************************
     * Handles events in which the "Load" MenuItem in the "File" menu is
     * choosed.
     * Opens a file-chooser window in which the user can choose a dictionary
     * file to load to the Dictionary App.
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML private void loadPressed(ActionEvent event) 
    {
        // open a file chooser for Dictionary files (.dict)
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(LOAD_DICT_TITLE);
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter(DICT_FILES_TYPE, DICT_FILES_FILTER));
        File inputFile = fileChooser.showOpenDialog(getWindow());
        
        if (inputFile != null) {
            // try to load the Dictionary and set a new associated path
            if (dictionary.load(inputFile.getAbsolutePath())) {
                currPath.set(inputFile.getAbsolutePath());
                isDictSaved.set(true);
            }
            else {
                // otherwise tell the user that we couldn't load
                new Alert(AlertType.ERROR,
                        String.format(COULDNT_LOAD_MSG,
                                inputFile.getAbsolutePath())).showAndWait();
            }
        }
    }

    /***************************************************************************
     * Handles events in which the "New" MenuItem in the "File" menu is
     * choosed.
     * Loads a freshly new dictionary to the Dictionary App (which is not
     * associated with any file yet).
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML private void newPressed(ActionEvent event) 
    {
        // simply clear the working dictionary
        dictionary.clear();
        
        // and update info about the associated file
        currPath.set("");
        isDictSaved.set(false);
    }
    
    /***************************************************************************
     * Handles events in which the "Save As..." MenuItem in the "File" menu is
     * choosed.
     * Saves the current dictionary to a new file, which is chosen by the user
     * using a file-chooser window.
     * 
     * @param event the event that caused the handler to be called (ignored).
     */
    @FXML private void saveAsPressed(ActionEvent event) 
    {
        // simply use 'runSaveAss'
        runSaveAs();
    }
    
    /***************************************************************************
     * Displays a file-chooser box, letting the user to save the current working
     * dictionary at any file he chooses.
     */
    private void runSaveAs() 
    {
        // open a file chooser for Dictionary files (.dict)
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(SAVE_DICT_TITLE);
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter(DICT_FILES_TYPE, DICT_FILES_FILTER));
        File outputFile = fileChooser.showSaveDialog(getWindow());
        
        if (outputFile != null) {
            // try to save the Dictionary and set a new associated path
            if (dictionary.save(outputFile.getAbsolutePath())) {
                currPath.set(outputFile.getAbsolutePath());
                isDictSaved.set(true);
            }
            else {
                // otherwise tell the user that we couldn't load
                new Alert(AlertType.ERROR,
                        String.format(COULDNT_SAVE_MSG,
                                outputFile.getAbsolutePath())).showAndWait();
            }
        }
    }
    
    /***************************************************************************
     * Handles events in which the "Save" MenuItem in the "File" menu is
     * choosed.
     * Saves the current dictionary to a file.
     * If no file is associated with the current dictionary, this is the same
     * as 'saveAsPressed'. Otherwise - no file-chooser window is opened and
     * the dictionary is simply saved to this associated file.
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML private void savePressed(ActionEvent event) 
    {
        // we save only if the dictionary is not already saved
        if (! isDictSaved.get()) {
            if (! currPath.get().equals("")) {
                // if there is an associated path - we save to it
                dictionary.save(currPath.get());
                isDictSaved.set(true);
            }
            else {
                // otherwise - this is the same as "Save As..."
                runSaveAs();
            }
        }
    }
    
    /***************************************************************************
     * Handles events in which the "How to Use" MenuItem in the "Help" menu is
     * choosed.
     * Displays a dialog box with instructions on how to use the Dictionary App.
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML private void HowToUsePressed(ActionEvent event)
    {
        // the instructions
        String instructions = "";
        instructions += "Welcome to the Dictionary App!\n";
        instructions += "This app will help you manipulate dictionaries of terms "
                + "and their definitions.\n";
        instructions += "\n";
        instructions += "To add a new entry to the dictionary - press [Edit]->"
                + "[Add Entry], fill in the term and its definition, and press "
                + "[Apply].\n";
        instructions += "To edit the definition of a term - select it, press "
                + "[Edit]->[Edit Entry], edit the definition, and press "
                + "[Apply].\n";
        instructions += "To delete an entry from the dictionary - select it "
                + "and press [Edit]->[Delete Entry].\n";
        instructions += "\n";
        instructions += "To start a new dictionary - press [File]->[New].\n";
        instructions += "To load a dictionary from disk (.dict file) - press "
                + "[File]->[Load] and select your dictionary file.\n";
        instructions += "To save the dictionary to disk - press [File]->[Save].\n";
        instructions += "To save the dictionary to a new file on disk - press "
                + "[File]->[Save As...] and select your file.\n";
        instructions += "The path for the file associated with the loaded "
                + "dictionary is displayed at the bottom of the window.\n";
        instructions += "\n";
        instructions += "After the dictionary is loaded with terms, you can "
                + "select a dictionary entry to view from the list on the left.\n";
        instructions += "To search for a specific term, use the text-field above "
                + "the list.\n";
        instructions += "\n";
        instructions += "Have fun, and good luck ^_^\n";
        instructions += "Nadav.";
        
        // display a dialog box with the instructions
        Alert dialogBox = new Alert(AlertType.INFORMATION, instructions);
        dialogBox.setTitle("Dictionary App: How to Use");
        dialogBox.setHeaderText("Dictionary App - Instructions");
        dialogBox.showAndWait();
    }
}
