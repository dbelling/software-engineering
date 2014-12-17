package edu.umn.csci5801.model;
/**
 * RequirementCheck
 */


import java.util.List;
import edu.umn.csci5801.model.RequirementDetails;

/**
 * Boolean check of requirement completion
 */

public class RequirementCheck {
    
    public String name;
    public boolean passed;
    public RequirementDetails details;

    public RequirementCheck(){
    	details = new RequirementDetails();
    }
    
    public RequirementCheck(String name, boolean passed, RequirementDetails details, List<String> notes) {
        this.name = name;
        this.passed = passed;
        this.details = details;
    }

}
