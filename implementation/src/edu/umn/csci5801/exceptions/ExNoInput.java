/* 
 * NoInputException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for missing input.
 */

public class ExNoInput extends Exception {

	private static final long serialVersionUID = 1L;

	public ExNoInput() {
		super("No input provided.");
	}

	public ExNoInput(String desc) {
		super(desc + " not provided.");
	}
}
