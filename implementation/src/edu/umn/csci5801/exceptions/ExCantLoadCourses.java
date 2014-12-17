/* 
 * CantLoadCoursesException
 */

package edu.umn.csci5801.exceptions;

/* 
 * Exception class for when GRADS cannot load Courses.
 */

public class ExCantLoadCourses extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ExCantLoadCourses() {
		super("Failed to load courses.");
	}
}
