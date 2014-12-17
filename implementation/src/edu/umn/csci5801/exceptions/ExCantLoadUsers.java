/* 
 * CantLoadUsersException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for when GRADS cannot load users.
 */

public class ExCantLoadUsers extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExCantLoadUsers() {
		super("Failed to load users.");
	}
}
