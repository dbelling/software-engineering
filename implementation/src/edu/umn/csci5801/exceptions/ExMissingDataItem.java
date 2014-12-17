/* 
 * MisingDataItemException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for missing data item.
 */

public class ExMissingDataItem extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExMissingDataItem() {
		super("Data item not found");
	}

	public ExMissingDataItem(String itemDesc) {
		super(itemDesc + " not found.");
	}
}
