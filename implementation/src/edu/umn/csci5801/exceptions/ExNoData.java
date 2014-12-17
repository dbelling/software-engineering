/* 
 * NoDataException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for no data at all.
 */

public class ExNoData extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExNoData() {
		super("No data exists. Make sure to invoke loadUsers(), loadCourses() and loadRecords() before doing anything else.");
	}
}
