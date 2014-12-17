/* 
 * FileNotFoundException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for missing file.
 */

public class ExFileNotFound extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExFileNotFound() {
		super("File not found");
	}

	public ExFileNotFound(String fName) {
		super("File " + fName + " not found.");
	}
}
