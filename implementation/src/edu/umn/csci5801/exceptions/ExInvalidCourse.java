/* 
 * InvalidCourseException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for invalid or disallowed course.
 */

public class ExInvalidCourse extends Exception {
	
	private static final long serialVersionUID = 1L;

	
	public ExInvalidCourse() {
		super("Course is invalid or is disallowed.");
	}
}
