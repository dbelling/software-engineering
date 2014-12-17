/**
 * Professor
 */

package edu.umn.csci5801.model;

/**
 * Basic information for a professor
 */

public class Professor {

    public Department department;
    public String firstName;
    public String lastName;

    public Professor(Department department, String first, String last) {
        this.department = department;
        this.firstName = first;
        this.lastName = last;
    }
    
}
