package dictionaryProject;

import javafx.collections.transformation.FilteredList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*******************************************************************************
 * This class represents a dictionary containing terms and their definitions.
 * A dictionary represents a list of DictionaryEntry objects.
 * The dictionary is suited to work with JavaFX ListViews: it is implemented
 * using an observable list, ond offers functionality to bind a filtered and
 * sorted version of it to ListView, according to a filtering predicate.
 * 
 * @author Nadav Kahlon
 */
public class Dictionary
{
    /***************************************************************************
     * Attribute of a dictionary
     */
    
    // an observable list of the dictionarie's entries
    private final ObservableList<DictionaryEntry> entriesList;
    
    // a filtered and sorted wrapper of 'entriesList' (used to bind to a
    // ListView when displaying the dictionary)
    private final FilteredList<DictionaryEntry> filteredSortedEntriesList;
    
    /***************************************************************************
     * Constructor: creates a new empty dictionary (the filtering predicate is
     * initialized to a tautology - i.e. no filter).
     */
    public Dictionary()
    {
        // initialize the entries list to an empty list
        this.entriesList = FXCollections.observableArrayList();
        this.filteredSortedEntriesList = entriesList
                .sorted() // sorted wrapper
                .filtered((x) -> true); // filttered wrapper (at the beginning
                                        // there's no filter)
    }
    
    /***************************************************************************
     * Sets the filtering predicate of the entries in the dictionary (only the
     * filtered entries are bound using 'bindToListView').
     * 
     * @param filter the new filtering predicate (does nothing if its null).
     */
    public void setFilterPredicate(Predicate<? super DictionaryEntry> filter)
    {
        // if the predicate isn't null - simply use FilteredList.setPredciate
        if (filter != null) filteredSortedEntriesList.setPredicate(filter);
    }
    
    /***************************************************************************
     * Binds the filtered-order wrapper of the dictionary to a JavaFX ListView
     * of DictionaryEntrries (the filtering predicate is the one specified by
     * 'setFilterPredicate', which is initially a tautology - i.e. no filter).
     * 0
     * @param entriesListView the listView to bind the filtered-sorted wraper to.
     */
    public void bindToListView(ListView<DictionaryEntry> entriesListView)
    {
        // simply bind the sorted-filtered wraper for the list of dictionary
        // entries to the ListView
        entriesListView.setItems(this.filteredSortedEntriesList);
    }
    
    /***************************************************************************
     * Finds a specific term in the dictionary.
     * 
     * @param term the term to look for.
     * @return A DictionaryEntry object corresponding to the term's dictionary
     * entry, or null if such a term is not present in the dictionary.
     */
    public DictionaryEntry findTerm(String term)
    {
        // we first create a "null" DictionaryEntry (with no definition) so we
        // can look for an entry identified by the same term
        DictionaryEntry nullEntry = new DictionaryEntry(term, null);
        
        // look for the index of the first occurrence of such an entry
        int idx = this.entriesList.indexOf(nullEntry);
        
        // return the corresponding entry (if it exists)
        // (note that since DictionaryEntry objects are immutable as specified
        // by the documentation of that class, we don't need to copy the entry)
        if (idx >= 0) return this.entriesList.get(idx);
        else return null; 
    }
    
    /***************************************************************************
     * Adds an entry to the dictionary if an entry for the same term is not
     * already present in it.
     * 
     * @param newEntry the entry to add.
     * @return A boolean stating whether the entry was successfuly added to
     * the dictionary (i.e. it is not null and the term is not present in the
     * dictionary yet).
     */
    public boolean addEntry(DictionaryEntry newEntry)
    {
        // simply add the new entry to the entries list, if it is not null and
        // if we cannot find an entry for the same term
        if (newEntry != null && this.findTerm(newEntry.getTerm()) == null) {
            this.entriesList.add(newEntry);
            return true;
        }
        else return false;
    }
    
    /***************************************************************************
     * Removes an entry from the dictionary (if an entry representing the same
     * term is present in the it).
     * 
     * @param toRemove the entry to remove (actually removes any entry of the
     * same term).
     * @return A boolean stating whether an entry was successfuly removed from
     * the dictionary.
     */
    public boolean removeEntry(DictionaryEntry toRemove)
    {
        // we simply use the remove method of the SortedList class
        return this.entriesList.remove(toRemove);
    }
    
    /***************************************************************************
     * Writes the dictionary to a file.
     * Use ONLY Dictionary.load to load such files.
     * 
     * @param path the path of the output file (assumed not to be null).
     * @return A boolean stating whether the operation was completed successfuly
     * (i.e. no exception was thrown).
     */
    public boolean save(String path) 
    {
        /* File format:
         * Since javafx.collections.transformation.SortedList is not serializable,
         * we can't directly serialize the object (since it has a non-serializable
         * attribute). Instead, we create an ArrayList identical to entriesList
         * and serialize it to the output object
         */
        // copy entriesList to an ArrayList
        ArrayList<DictionaryEntry> entries = new ArrayList<>(this.entriesList);
        
        // open output stream
        try(ObjectOutputStream output =
                new ObjectOutputStream(new FileOutputStream(path))) {
            
            //  serialize the entries ArrayList into it
            output.writeObject(entries);
            output.close();
            return true;
        }
        catch (Exception e) {
            // if an exception was thrown - the operation was not completed successfuly
            return false;
        }
    }
    
    /***************************************************************************
     * Loads a dictionary from a file into this dictionary.
     * Use to load ONLY dictionaries saved by Dictionary.save.
     * 
     * @param path the path to the input file (assumed it is not null).
     * @return A boolean stating whether the operation was completed successfuly
     * (i.e. no exception was thrown).
     */
    @SuppressWarnings({"unchecked"}) // (for unchecked cast of generic type -
                                     // which isn't present in older java versions)
    public boolean load(String path)
    {
        /* The file format is the same as the format described in the comments to
           Dictionary.save */

        // open input stream
        try(ObjectInputStream input =
                new ObjectInputStream(new FileInputStream(path))) {
            
            // read the serialized ArrayList
            ArrayList<DictionaryEntry> entries = 
                    (ArrayList<DictionaryEntry>)input.readObject();
            
            // swap all the entries of this dictionary by those in 'entries'
            this.entriesList.removeAll(this.entriesList);
            this.entriesList.addAll(entries);
            return true;
        }
        catch (Exception e) {
            // if an exception was thrown - the operation was not completed successfuly
            return false;
        }
    }
    
    /***************************************************************************
     * Clears all entries from the dictionary.
     */
    public void clear()
    {
        // simply use ObservableList.clear to clear the entries list
        entriesList.clear();
    }
}
