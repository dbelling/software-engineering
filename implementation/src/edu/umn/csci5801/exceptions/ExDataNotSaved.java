/* 
 * DataNotSavedException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for uncommitted data.
 */

public class ExDataNotSaved extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExDataNotSaved() {
		super("Data could not be saved.");
	}
}
