/**
 * Term
 */

package edu.umn.csci5801.model;

import edu.umn.csci5801.model.Semester;

/**
 * Term information for a course offering. 
 */

public class Term {

    public Semester semester;
    public int year;

    public Term(Semester semester, int year) {
        this.semester = semester;
        this.year = year;
    }

}
