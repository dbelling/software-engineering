/* 
 * InvalidDataItemException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for invalid data item.
 */

public class ExInvalidDataItem extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExInvalidDataItem() {
		super("Invalid data item.");
	}

	public ExInvalidDataItem(String itemDesc) {
		super(itemDesc + " is invalid.");
	}
}
