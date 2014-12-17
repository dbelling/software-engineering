/* 
 * NonUserException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for id which does not correspond to a user.
 */

public class ExNonUser extends Exception {

	private static final long serialVersionUID = 1L;

	public ExNonUser() {
		super("Given ID is not a user.");
	}

	public ExNonUser(String uId) {
		super("User ID " + uId + " is not a user.");
	}
}
