/**
 * Course
 */

package edu.umn.csci5801.model;

/**
 * Course information
 */

public class Course {
    
    public String name;
    public String id;
    public String numCredits;
    public CourseArea courseArea;

    public Course(String name, String id, String numCredits, CourseArea courseArea) {
        this.name = name;
        this.id = id;
        this.numCredits = numCredits;
        this.courseArea = courseArea;
    }

}
