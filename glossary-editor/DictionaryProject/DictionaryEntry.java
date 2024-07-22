package dictionaryProject;

import java.io.Serializable;

/*******************************************************************************
 * This class represents an entry in a dictionary.
 * A dictionary entry contains a term (String) and its definition (String).
 * Entries are identified and ordered (lexicographically) by the terms they
 * represent (ignoring letter cases).
 * The string representation of an entry contains the term only.
 * DictionaryEntry objects are IMMUTABLE.
 * 
 * @author Nadav Kahlon
 */
public class DictionaryEntry implements Comparable<DictionaryEntry>, Serializable
{
    /***************************************************************************
     * Attributes of a dictionary entry (final, since such an entry is immutable)
     */
    private final String term; // the term
    private final String definition; // its definition
    
    /***************************************************************************
     * Constructor: creates a new dictionary entry for a given term with its
     * given definition.
     * 
     * @param term the new term.
     * @param definition its definition
     */
    public DictionaryEntry(String term, String definition)
    {
        // since Strings are immutable, we don't need to copy the parameters
        this.term = term;
        this.definition = definition;
    }
    
    /***************************************************************************
     * Gets the term of this entry.
     * 
     * @return the term.
     */
    public String getTerm()
    {
        // since Strings are immutable, we don't need to copy the term
        return this.term;
    }
    
    /***************************************************************************
     * Gets the definition of the term of this entry.
     * 
     * @return the definition.
     */
    public String getDefinition()
    {
        // since Strings are immutable, we don't need to copy the definition
        return this.definition;
    }
    
    /***************************************************************************
     * Gets a String representation of the entry, containing the term only.
     * (same as 'getTerm').
     * 
     * @return the term.
     */
    @Override public String toString()
    {
        // since Strings are immutable, we don't need to copy the term
        return this.term;
    }
    
    /***************************************************************************
     * Checks if this entry and another given entry represent the same term.
     * 
     * @param other the other object to compare to this entry. Should be a
     * DictionaryEntry.
     * @return A boolean stating whether both entries represent the same term
     * (ignoring letter cases).
     */
    @Override public boolean equals(Object other)
    {
        // we simply make sure that 'other' is also a DictionaryEntry, and that
        // both terms are the same
        return (other instanceof DictionaryEntry) &&
                this.term.equalsIgnoreCase(((DictionaryEntry)other).term);
    }
    
    /***************************************************************************
     * Compares this entry to another given entry based on the terms they
     * represent.
     * 
     * @param other the other entry to compare to this entry.
     * @return A negative integer, zero, or a positive integer as this entrie's
     * term is lexicogrphically less than, equal to, or greater than the other
     * entrie's term (ignoring letter cases).
     */
    @Override public int compareTo(DictionaryEntry other)
    {
        // simply use String.compareToIgnoreCase to compare the terms
        return this.term.compareToIgnoreCase(other.term);
    }
}
