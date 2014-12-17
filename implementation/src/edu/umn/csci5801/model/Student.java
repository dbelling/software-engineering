/**
 * Student
 */

package edu.umn.csci5801.model;

/**
 * Basic information for a student
 */

public class Student {

    public String id;
    public String firstName;
    public String lastName;

    public Student(String id, String first, String last) {
        this.id = id;
        this.firstName = first;
        this.lastName = last;
    }

    public String toString() {
    	return "Id: "+id+"\nFirst Name: "+firstName+"\nLast Name: "+lastName;
    }
}
