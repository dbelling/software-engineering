/* 
 * IdNotFoundException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for missing user id.
 */

public class ExIdNotFound extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExIdNotFound() {
		super("User ID not found.");
	}

	public ExIdNotFound(String uId) {
		super("User ID " + uId + " not found.");
	}
}
