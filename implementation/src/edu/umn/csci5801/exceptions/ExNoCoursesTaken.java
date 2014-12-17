/* 
 * NoCoursesTakenException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for missing file.
 */

public class ExNoCoursesTaken extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExNoCoursesTaken() {
		super("No courses taken.");
	}

	public ExNoCoursesTaken(String id) {
		super("User " + id + " has not taken any courses.");
	}
}
