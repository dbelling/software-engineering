/**
 * ProgressSummary
 */

package edu.umn.csci5801.model;

import java.util.List;
import edu.umn.csci5801.model.DegreeSought;
import edu.umn.csci5801.model.Department;
import edu.umn.csci5801.model.Professor;
import edu.umn.csci5801.model.Student;
import edu.umn.csci5801.model.Term;

/**
 * Progress summary for a student
 */

public class ProgressSummary {

    private Student student;
    private Department department;
    private DegreeSought degreeSought;
    private Term termBegan;
    private List<Professor> advisors;
    private List<Professor> committee;
    private List<RequirementCheck> requirementCheckResults;
    private List<String> notes;
    
    public void setStudent(Student student) {
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDegreeSought(DegreeSought degreeSought) {
        this.degreeSought = degreeSought;
    }

    public DegreeSought getDegreeSought() {
        return degreeSought;
    }

    public void setTerm(Term term) {
        this.termBegan = term;
    }

    public Term getTerm() {
        return termBegan;
    }

    public void setAdvisors(List<Professor> advisors) {
        this.advisors = advisors;
    }

    public List<Professor> getAdvisors() {
        return advisors;
    }

    public void setCommittee(List<Professor> committee) {
        this.committee = committee;
    }

    public List<Professor> getCommittee() {
        return committee;
    }

    public void setRequirementCheckResults(List<RequirementCheck> reqCheckResults) {
        this.requirementCheckResults = reqCheckResults;
    }

    public List<RequirementCheck> getRequirementCheckResults() {
        return requirementCheckResults;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public List<String> getNotes() {
        return notes;
    }

}
