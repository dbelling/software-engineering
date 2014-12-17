/* 
 * InvalidGpaTypeException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for invalid GPA type.
 */

public class ExInvalidGpaType extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExInvalidGpaType() {
		super("Invalid GPA type.");
	}
}
