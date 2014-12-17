/**
 * RequirementDetails
 */

package edu.umn.csci5801.model;

import java.util.List;

import edu.umn.csci5801.model.Milestone;
import edu.umn.csci5801.model.CourseTaken;

/**
 * Details of a specific requirement
 */

public class RequirementDetails {
    
    public Double gpa;
    public List<CourseTaken> courses;
    public List<Milestone> milestones;
    public List<String> notes;
    
    public RequirementDetails(){
    	
    }

    public RequirementDetails(double gpa, List<CourseTaken> courses, List<Milestone> milestones) {
        this.gpa = gpa;
        this.courses = courses;
        this.milestones = milestones;
    }

}
