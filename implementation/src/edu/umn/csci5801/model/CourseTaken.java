/**
 * CourseTaken
 */

package edu.umn.csci5801.model;

import edu.umn.csci5801.model.Term;
import edu.umn.csci5801.model.Grade;

/**
 * Past information of performance within a course
 */

public class CourseTaken {

    public Course course;
    public Term term;
    public Grade grade;

    public CourseTaken(Course course, Term term, Grade grade) {
        this.course = course;
        this.term = term;
        this.grade = grade;
    }

}
