/**
 * Milestone
 */

package edu.umn.csci5801.model;

import edu.umn.csci5801.model.Term;

/**
 * Milestone information for a student
 */

public class Milestone {

    public Milestones_All_Plans milestone;
    public Term term;

    public Milestone(Milestones_All_Plans milestone, Term term) {
        this.milestone = milestone;
        this.term = term;
    }
    
}
