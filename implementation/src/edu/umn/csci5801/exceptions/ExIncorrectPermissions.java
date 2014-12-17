/* 
 * IncorrectPermissionsException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for missing file.
 */

public class ExIncorrectPermissions extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExIncorrectPermissions() {
		super("User does not have permission.");
	}
	
	public ExIncorrectPermissions(String userId) {
		super("User " + userId + " does not have permission.");
	}
	
	public ExIncorrectPermissions(String curId, String reqId) {
		super("User " + curId + " cannot access records for " + reqId);
	}
}
