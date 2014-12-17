/* 
 * FatalErrorException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for a fatal error.
 */

public class ExFatalError extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ExFatalError() {
		super("Fatal error. This exception should never be thrown.");
	}
}
