/* 
 * CantLoadRecordsException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for when GRADS cannot load Records.
 */

public class ExCantLoadRecords extends Exception {
	private static final long serialVersionUID = 1L;

	
	public ExCantLoadRecords() {
		super("Failed to load records.");
	}
}
