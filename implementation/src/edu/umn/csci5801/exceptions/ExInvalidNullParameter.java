/* 
 * InvalidNullParameterException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for invalid GPA type.
 */

public class ExInvalidNullParameter extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExInvalidNullParameter() {
		super("Invalid null parameter.");
	}
}
