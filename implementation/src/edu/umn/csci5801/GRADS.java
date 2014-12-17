/**
 * GRADS
 */

package edu.umn.csci5801;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.umn.csci5801.model.CourseArea;
import edu.umn.csci5801.model.CourseMapping;
import edu.umn.csci5801.model.CourseTaken;
import edu.umn.csci5801.model.DegreeSought;
import edu.umn.csci5801.model.Grade;
import edu.umn.csci5801.model.Milestone;
import edu.umn.csci5801.model.Milestones_All_Plans;
import edu.umn.csci5801.model.PermissionMapping;
import edu.umn.csci5801.model.ProgressSummary;
import edu.umn.csci5801.model.RequirementCheck;
import edu.umn.csci5801.model.Role;
import edu.umn.csci5801.model.StudentRecord;
import edu.umn.csci5801.exceptions.ExCantLoadCourses;
import edu.umn.csci5801.exceptions.ExCantLoadRecords;
import edu.umn.csci5801.exceptions.ExCantLoadUsers;
import edu.umn.csci5801.exceptions.ExDataNotSaved;
import edu.umn.csci5801.exceptions.ExFatalError;
import edu.umn.csci5801.exceptions.ExIdNotFound;
import edu.umn.csci5801.exceptions.ExIncorrectPermissions;
import edu.umn.csci5801.exceptions.ExInvalidGpaType;
import edu.umn.csci5801.exceptions.ExInvalidNullParameter;
import edu.umn.csci5801.exceptions.ExNoCoursesTaken;
import edu.umn.csci5801.exceptions.ExNoData;

/**
 * This class acts implements the GRADSIntf interface
 * and contains the primary processing code for checking
 * and generating the graduation progress summary.
 */

public class GRADS implements GRADSIntf {

    private static PermissionMapping currentUser;
    private static List<PermissionMapping> users;
    private static List<CourseMapping> courses;
    private static List<StudentRecord> records;
    private boolean usersRead = false;
    private boolean coursesRead = false;
    private boolean recordsRead = false;
    
    private String recordsPath;
    
    /**
     * Loads the list of system user names and permissions.
     * @param usersFile the filename of the users file.
     * @throws Exception for I/O errors.
     */
    public void loadUsers(String usersFile) throws Exception {
    	try {
        	users = new Gson().fromJson( new FileReader( new File(usersFile)), new TypeToken<List<PermissionMapping>>(){}.getType());
        	usersRead = true;
    	} catch (Exception e){
    		throw new ExCantLoadUsers();
    	}
    }
    
    /**
     * Loads the list of courses.
     * @param coursesFile the filename of the users file.
     * @throws Exception for I/O errors.  
     */
    public void loadCourses(String coursesFile) throws Exception {
    	try {
        	courses = new Gson().fromJson( new FileReader( new File(coursesFile)), new TypeToken<List<CourseMapping>>(){}.getType());
        	coursesRead = true;
    	} catch (Exception e){
    		throw new ExCantLoadCourses();
    	}
    }
    
    /**
     * Loads the list of system transcripts.
     * @param recordsFile the filename of the transcripts file.
     * @throws Exception for I/O errors.
     */
    public void loadRecords(String recordsFile) throws Exception {
    	try {
        	records = new Gson().fromJson( new FileReader( new File(recordsFile)), new TypeToken<List<StudentRecord>>(){}.getType());
        	recordsPath = recordsFile;
        	recordsRead = true;
    	} catch (Exception e){
    		throw new ExCantLoadRecords();
    	}
    }
    
    private void checkDataExist() throws Exception {
    	if (usersRead == false || coursesRead == false || recordsRead == false) {
    		throw new ExNoData();
    	}
    }
    
    /**
     * Sets the user id (X500) of the user currently using the system.
     * @param userId  the X500 id of the user generating progress summaries.
     * @throws Exception  if the user id is invalid.
     */
    public void setUser(String userId) throws Exception {
    	checkDataExist();
    	
        //check if user is valid
    	for (PermissionMapping i : users) {
    		if (i.id.equals(userId)) {
    			currentUser = i;
    			return;
    		}
    	}
    	throw new ExIdNotFound(userId); // Id is not valid.
    }
    
    /**
     * Gets the user id of the user currently using the system.
     * @return the X500 user id of the user currently using the system.
     */
    public String getUser() {
        return currentUser.id;
    }
    
    /**
     * Gets a list of the userIds of the students that a GPC can view.
     * @return a list containing the userId of for each student in the
     *         system belonging to the current user
     * @throws Exception is the current user is not a GPC.
     */
    public List<String> getStudentIDs() throws Exception {
    	checkDataExist();
    	
    	if (currentUser.role == Role.GRADUATE_PROGRAM_COORDINATOR) {
    		List<String> studentIDs = new ArrayList<String>();
    		for (StudentRecord i : records) {
    			studentIDs.add(i.getStudent().id);
    		}
    		return studentIDs;
    	} else {
    		throw new ExIncorrectPermissions(); // Wrong role
    	}
    }
    
    /**
     * Gets the raw student record data for a given userId.
     * @param userId  the identifier of the student.
     * @return the student record data.
     * @throws Exception  if the form data could not be retrieved.
     */
    public StudentRecord getTranscript(String userId) throws Exception {
    	checkDataExist();
    	
    	for (StudentRecord i : records) {
    		if(i.getStudent().id.equals(userId)) {
    			/* Throw exception unless current user is GPC or else a student requesting own transcript. */
    			if((currentUser.role == Role.GRADUATE_PROGRAM_COORDINATOR) || currentUser.id.equals(userId)) { 
    				return i;
    			} else {
    				throw new ExIncorrectPermissions();
    			}
    		}
    	}
    	
    	throw new ExIdNotFound(userId); // ID doesn't exist
    }
    
    /**
     * Saves a new set of student data to the records data.
     * @param userId the student ID to overwrite.
     * @param transcript  the new student record
     * @throws Exception  if the transcript data could not be saved, failed
     * a validity check, or a non-GPC tries to call.
     */
    public void updateTranscript(String userId, StudentRecord transcript)
            throws Exception {
    	checkDataExist();
    	
    	if (currentUser.role == Role.GRADUATE_PROGRAM_COORDINATOR) {
    		StudentRecord currentRecord = getTranscript(userId);
        	currentRecord.setStudent(transcript.getStudent());
        	currentRecord.setDepartment(transcript.getDepartment());
        	currentRecord.setDegreeSought(transcript.getDegreeSought());
        	currentRecord.setTerm(transcript.getTerm());
        	currentRecord.setAdvisors(transcript.getAdvisors());
        	currentRecord.setCommittee(transcript.getCommittee());
        	currentRecord.setCoursesTaken(transcript.getCoursesTaken());
        	currentRecord.setMilestonesSet(transcript.getMilestonesSet());
        	//currentRecord.setNotes(transcript.getNotes());
        	
        	try {
        		String representation = new GsonBuilder().setPrettyPrinting().create().toJson(records);
        		PrintWriter writer = new PrintWriter(recordsPath);
        		writer.print(representation);
        		writer.close();
        	} catch (Exception e) {
        		throw new ExDataNotSaved();
        	}
        	
    	} else {
    		throw new ExIncorrectPermissions();
    	}
    	
    }
    
    /**
     * Appends a note to a student record.
     * @param userId the student ID to add a note to.
     * @param note  the note to append
     * @throws Exception  if the note could not be saved or a non-GPC tries to call.
     */
    public void addNote(String userId, String note)
            throws Exception {
    	checkDataExist();
    	
    	if (currentUser.role == Role.GRADUATE_PROGRAM_COORDINATOR) {
    		StudentRecord currentRecord = getTranscript(userId);
            List<String> currentNote = currentRecord.getNotes();
            currentNote.add(note);
            currentRecord.setNotes(currentNote);
            
            try {
        		String representation = new GsonBuilder().setPrettyPrinting().create().toJson(records);
        		PrintWriter writer = new PrintWriter(recordsPath);
        		writer.print(representation);
        		writer.close();
        	} catch (Exception e) {
        		throw new ExDataNotSaved();
        	}
            
    	} else {
    		throw new ExIncorrectPermissions();
    	}
    }
    
    /**
     * Generates progress summary
     * @param userId the student to generate the record for.
     * @return the student's progress summary in a data class matching the JSON format.
     * @throws Exception  if the progress summary could not be generated.
     */
    public ProgressSummary generateProgressSummary(String userId) throws Exception {
    	if(currentUser.role != Role.GRADUATE_PROGRAM_COORDINATOR && !currentUser.id.equals(userId)) {
    		throw new ExIncorrectPermissions(currentUser.id, userId);
    	}
    	
    	checkDataExist();
    	
	/* Exception will be thrown if GRADS is unable to get the transcript. */
    	StudentRecord currentRecord = getTranscript(userId);
    	
	/* If we were able to load the transcript, go ahead and copy the values into result.
	   Some of these values may be null. */
    	ProgressSummary result = new ProgressSummary();
    	result.setStudent(currentRecord.getStudent());
    	result.setDepartment(currentRecord.getDepartment());
    	result.setDegreeSought(currentRecord.getDegreeSought());
    	result.setTerm(currentRecord.getTerm());
    	result.setAdvisors(currentRecord.getAdvisors());
    	result.setCommittee(currentRecord.getCommittee());
    	result.setRequirementCheckResults(requirementCheck(userId));
    	
    	return result;
    }
    
    /**
     * Generates a new progress summary, assuming that the student passes the
     * provided set of prospective courses.
     * @param userId the student to generate the record for.
     * @param courses a list of the prospective courses.
     * @return a map containing the student's hypothetical progress summary
     * @throws Exception  if the progress summary could not be generated or the courses
     * are invalid.
     */
    public ProgressSummary simulateCourses(String userId, List<CourseTaken> courses) 
    	throws Exception {
    	if(currentUser.role != Role.GRADUATE_PROGRAM_COORDINATOR && !currentUser.id.equals(userId)) {
    		throw new ExIncorrectPermissions();
    	}
    	
    	checkDataExist();
    	
	/* If there is a problem getting the transcript, getTranscript will throw an exception. */    
    	StudentRecord currentRecord = getTranscript(userId);
    	/* Attempt to load courses taken. If no courses have yet been taken, throw an exception. */
    	List<CourseTaken> oldCourseTaken = currentRecord.getCoursesTaken();
    	
    	/* Cannot call this method with a null parameter for courses */
    	if(courses == null) {
    		throw new ExInvalidNullParameter();
    	}
    	
    	/* Make sure that the student has taken at least one course already. */
    	if(oldCourseTaken == null) {
    		throw new ExNoCoursesTaken();
    	}
    	
    	/* Create alternate course list containing the extra course(s). */
    	List<CourseTaken> newCoursesTaken = new ArrayList<CourseTaken>();
    	for(int i = 0; i < oldCourseTaken.size(); i++) {
    		newCoursesTaken.add(oldCourseTaken.get(i));
    	}
    	for(int i = 0; i < courses.size(); i++) {
    		newCoursesTaken.add(courses.get(i));
    	}
    	
    	currentRecord.setCoursesTaken(newCoursesTaken); // Replace by using new courseTaken
    	
    	ProgressSummary result = new ProgressSummary();
    	result.setStudent(currentRecord.getStudent());
    	result.setDepartment(currentRecord.getDepartment());
    	result.setDegreeSought(currentRecord.getDegreeSought());
    	result.setTerm(currentRecord.getTerm());
    	result.setAdvisors(currentRecord.getAdvisors());
    	result.setCommittee(currentRecord.getCommittee());
    	result.setRequirementCheckResults(requirementCheck(userId));
    	
    	currentRecord.setCoursesTaken(oldCourseTaken); // Replace back
    	return result;
    }
    
    /**
     * Calculates the GPA of a specified type for a specific student
     * @param userId the student to calculate the GPA for
     * @param gpaType the type of GPA to calculate; one of:
     *          - OVERALL
     *          - IN_PROGRAM
     *          - BREADTH_REQUIREMENT
     * @return the calculated GPA value of the type specified by the input
     *         parameter
     */
    protected double calculateGPA(String userId, String gpaType) throws Exception {
    	checkDataExist();
    	
    	double runningTotal = 0, totalCredits = 0;
    	
    	if (gpaType != "OVERALL" && gpaType != "IN_PROGRAM" && gpaType != "BREADTH_REQUIREMENT") {
    		throw new ExInvalidGpaType();
    	}
    	
    	if(getTranscript(userId).getCoursesTaken() == null) {
    		return 0;
    	}
    	
    	for (CourseTaken currentCourse : getTranscript(userId).getCoursesTaken()) {
    		if (currentCourse.grade == Grade.S) continue;
    		/* If overall gpa was requested, we want all courses to count. */
    		boolean courseCounted = gpaType.equals("OVERALL");

    		/* If it's in program, we only count it if it's a csci course. */
    		if (gpaType.equals("IN_PROGRAM") && currentCourse.course.id.startsWith("csci")) {
    			courseCounted = true;
    		} else if(gpaType.equals("BREADTH_REQUIREMENT")) {
    			/* If we want a breadth requirement GPA, we must make sure that this is a breadth requirement course. */
    			/* Now check every CourseMapping to find a match. This is inefficient, 
				   but it matches the class structure we were told to use, and realistically,
				   it is unlikely that anybody will complain about the tiny extra delay that
				   this causes. If we someday have a billion courses, then it might be more 
				   of an issue. */
    				for (CourseMapping mapping : courses) {
    					if (mapping.id.equals(currentCourse.course.id)) {
    						if(mapping.courseArea != null) {
    							courseCounted = true;
    						}
    					}
    				}
    		}

    		/* Update counters if necessary. */
    		if (courseCounted) {
    			totalCredits += Integer.parseInt(currentCourse.course.numCredits);
    			runningTotal += (double)currentCourse.grade.getGpa()*Double.parseDouble(currentCourse.course.numCredits);
    		}
    	}
    	
    	if (totalCredits == 0) return 0;
    	return runningTotal / totalCredits;
    }
    
    /**
     * Calculates the required GPA level of a specified type for a specific 
     * student based on the type of degree they are seeking
     * @param userId the student to calculate the GPA for
     * @param gpaType the type of GPA to calculate; one of:
     *          - OVERALL
     *          - IN_PROGRAM
     *          - BREADTH_REQUIREMENT
     * @return the required GPA value of the type specified by the input
     *         parameter
     */
    protected double calculateRequiredGPALevel(String userId, String gpaType) throws Exception {
    	checkDataExist();
    	
    	StudentRecord currentRecord = getTranscript(userId);
    	double result = 0;
    	if (gpaType.equals("OVERALL")) {
    		switch (currentRecord.getDegreeSought()) {
    			case PHD: result = 3.45; break;
    			case MS_A: result = 3.25; break;
    			case MS_B: result = 3.25; break;
    			case MS_C: result = 3.25; break;
    			default: throw new ExFatalError(); //student must have valid degreeSought
    		}
    	}
    	else if (gpaType.equals("IN_PROGRAM")) {
    		switch (currentRecord.getDegreeSought()) {
    			case PHD: result = 3.45; break;
    			case MS_A: result = 3.25; break;
    			case MS_B: result = 3.25; break;
    			case MS_C: result = 3.25; break;
    			default: throw new ExFatalError(); //student must have valid degreeSought
    		}
    	}
    	else if (gpaType.equals("BREADTH_REQUIREMENT")) {
    		switch (currentRecord.getDegreeSought()) {
    			case PHD: result = 3.45; break;
    			case MS_A: result = 3.25; break;
    			case MS_B: result = 3.25; break;
    			case MS_C: result = 3.25; break;
    			default: throw new ExFatalError(); //student must have valid degreeSought
    		}
    	}
    	else {
    		throw new ExInvalidGpaType(); //input gpaType is not valid
    	}
        return result;
    }
    
    
    /**
     * Retrieves the list of RequirementChecks for a given student specifying 
     * which requirements the student has or has not passed
     * @param userId the student to retrieve RequirementChecks for
     * @param courses the list of courses the student has taken
     * @param milestones the list of milestones for the student's degree sought
     * @return the list of RequirementChecks for the input student
     */
    private List<RequirementCheck> requirementCheck(String userId) throws Exception {
    	checkDataExist();
    	
        StudentRecord currentRecord = getTranscript(userId);
        List<CourseTaken> currentCourses = currentRecord.getCoursesTaken();
        List<Milestone> currentMilestones = currentRecord.getMilestonesSet();
        
        List<RequirementCheck> result = new ArrayList<RequirementCheck>();
        
        // Add final result at first place
        RequirementCheck first = new RequirementCheck();
        first.name = "DEGREE_SOUGHT";
        first.passed = true;
        first.details.notes = currentRecord.getNotes();
        result.add(first);
        // END
        
        switch(currentRecord.getDegreeSought()) {
        	case PHD:
        		checkPHD(userId, currentCourses, currentMilestones, result);
        		break;
        	case MS_A:
        		checkMS_A(userId, currentCourses, currentMilestones, result);
        		break;
        	case MS_B:
        		checkMS_B(userId, currentCourses, currentMilestones, result);
        		break;
        	case MS_C:
        		checkMS_C(userId, currentCourses, currentMilestones, result);
        		break;
        	default:
        		throw new ExFatalError(); // Should not be here;
        }
        
        return result;
    }
    
    // ----------------------------------------------------------
    // Overall Check for PhD
    // ----------------------------------------------------------
    
    /**
      * Performs the overall check for PhD students
      * @param userId the student to perform the PhD check on
      * @param currentCourses courses the student has taken
      * @param currentMilestones milestones the student has completed
      * @param result the list of RequirementChecks the student has completed - will be modified within function
      */
    private void checkPHD(String userId, List<CourseTaken> currentCourses, List<Milestone> currentMilestones, List<RequirementCheck> result) throws Exception {
    	checkDataExist();
    	
    	// 31 credits in total, 16 CSci credits, 5 breath courses, CSci 8970
    	checkCreditsRequirements(userId, currentCourses, 31, 16, 15, result);
    	checkSingleCourse(currentCourses, "COLLOQUIUM", "csci8970", 0, "S", result);
    	
    	// 10 Thesis Credits   	
    	checkSingleCourse(currentCourses, "THESIS_PHD", "csci8888", 10, "S", result);
    	
    	// Out of department credits
    	// --------------------------------------
    	RequirementCheck outOfDepartment = new RequirementCheck();
    	outOfDepartment.name = "OUT_OF_DEPARTMENT";
    	outOfDepartment.details.courses = new ArrayList<CourseTaken>();

    	int outOfDepartmentCredits = 0;
    	for (CourseTaken i : currentCourses) {
    		if (!i.course.id.startsWith("csci")) {
    			if (checkSingleValidCourse(i)) {    				
    	    		outOfDepartment.details.courses.add(i); // Add a course
    				outOfDepartmentCredits += Integer.parseInt(i.course.numCredits);
    			}
    		}
    	}
    	outOfDepartment.passed = (outOfDepartmentCredits >= 6);
    	result.add(outOfDepartment);
    	// --------------------------------------
    	
    	// Introduction to CS Research
    	RequirementCheck introToCSResearch = new RequirementCheck();
    	introToCSResearch.name = "INTRO_TO_RESEARCH";
    	introToCSResearch.details.courses = new ArrayList<CourseTaken> ();
    	
    	for (CourseTaken i : currentCourses) {
    		if (i.course.id.startsWith("csci8001") || i.course.id.startsWith("csci8002")) {
    			if (checkSingleValidCourse(i)) {
    				// introToCSResearch.details.courses = new ArrayList<CourseTaken> ();
    				introToCSResearch.details.courses.add(i);
    			}
    		}
    	}
    	introToCSResearch.passed = (introToCSResearch.details.courses.size() >= 2);	
    	result.add(introToCSResearch);
    	
    	// Milestone PHD
    	RequirementCheck milestonesPHD = new RequirementCheck();
    	milestonesPHD.name = "MILESTONES_PHD";
    	
    	boolean milestonesPHDTemp = true;
    	milestonesPHDTemp = milestonesPHDTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.PRELIM_COMMITTEE_APPOINTED, milestonesPHD);
    	milestonesPHDTemp = milestonesPHDTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.WRITTEN_PE_SUBMITTED, milestonesPHD);
    	milestonesPHDTemp = milestonesPHDTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.WRITTEN_PE_APPROVED, milestonesPHD);
    	milestonesPHDTemp = milestonesPHDTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.ORAL_PE_PASSED, milestonesPHD);
    	milestonesPHDTemp = milestonesPHDTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.DPF_SUBMITTED, milestonesPHD);
    	milestonesPHDTemp = milestonesPHDTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.DPF_APPROVED, milestonesPHD);
    	milestonesPHDTemp = milestonesPHDTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.THESIS_COMMITTEE_APPOINTED, milestonesPHD);
    	milestonesPHDTemp = milestonesPHDTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.PROPOSAL_PASSED, milestonesPHD);
    	milestonesPHDTemp = milestonesPHDTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.GRADUATION_PACKET_REQUESTED, milestonesPHD);
    	milestonesPHDTemp = milestonesPHDTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.THESIS_SUBMITTED, milestonesPHD);
    	milestonesPHDTemp = milestonesPHDTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.THESIS_APPROVED, milestonesPHD);
    	milestonesPHDTemp = milestonesPHDTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.DEFENSE_PASSED, milestonesPHD);
    	
    	milestonesPHD.passed = milestonesPHDTemp;
    	result.add(milestonesPHD);
    	
    	for (RequirementCheck i : result) {
    		if (i.name == "DEGREE_SOUGHT") continue;
    		result.get(0).passed = result.get(0).passed && i.passed;
    	}
    }
    
    // ----------------------------------------------------------
    // Overall Check for MS_A
    // ----------------------------------------------------------
    
    /**
      * Performs the overall check for MS_A students
      * @param userId the student to perform the MS_A check on
      * @param currentCourses courses the student has taken
      * @param currentMilestones milestones the student has completed
      * @param result the list of RequirementChecks the student has completed - will be modified within function
      */
    private void checkMS_A(String userId, List<CourseTaken> currentCourses, List<Milestone> currentMilestones, List<RequirementCheck> result) throws Exception {
    	checkDataExist();
    	
    	// 31 credits in total, 16 CSci credits, 3 breath courses, CSci 8970
    	checkCreditsRequirements(userId, currentCourses, 31, 16, 15, result);
    	checkSingleCourse(currentCourses, "COLLOQUIUM", "csci8970", 0, "S", result);
    	
    	// 10 Thesis Credits
    	checkSingleCourse(currentCourses, "THESIS_MS", "csci8777", 10, "S", result);
    	
    	// PhD level credits
    	// --------------------------------------
    	RequirementCheck phdLevelCredits = new RequirementCheck();
    	phdLevelCredits.name = "PHD_LEVEL_COURSES";
    	phdLevelCredits.details.courses = new ArrayList<CourseTaken>();

    	int phdLevelCreditsCount = 0;
    	for (CourseTaken i : currentCourses) {
    		if (i.course.id.startsWith("csci8") && !i.course.id.startsWith("csci8970") && !i.course.id.startsWith("csci8777")) {
    			if (checkSingleValidCourse(i)) {
    				phdLevelCredits.details.courses.add(i); // Add a course
    				phdLevelCreditsCount += Integer.parseInt(i.course.numCredits);
    			}
    		}
    	}
    	phdLevelCredits.passed = (phdLevelCreditsCount >= 3);
    	result.add(phdLevelCredits);
    	// --------------------------------------
    	
    	// Course Credits
    	RequirementCheck courseCreditsRequirement = new RequirementCheck();
    	courseCreditsRequirement.name = "COURSE_CREDITS";
    	courseCreditsRequirement.details.courses = new ArrayList<CourseTaken>();
    	int courseCredits = 0;
    	
    	for (CourseTaken i : currentCourses) {
    		if (i.course.id.startsWith("csci") && !i.course.id.startsWith("csci8777")) {
    			if (checkSingleValidCourse(i)) {
    	    		courseCreditsRequirement.details.courses.add(i); // Add a course
    	    		courseCredits += Integer.parseInt(i.course.numCredits);
    			}
    		}
    	}
    	courseCreditsRequirement.passed = (courseCredits >= 22);
    	result.add(courseCreditsRequirement);
    	
    	// Milestone MS_A
    	RequirementCheck milestonesMS_A = new RequirementCheck();
    	milestonesMS_A.name = "MILESTONES_MS_A";
    	milestonesMS_A.passed = false;
    	
    	boolean milestonesMS_ATemp = true;
    	milestonesMS_ATemp = milestonesMS_ATemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.DPF_SUBMITTED, milestonesMS_A);
    	milestonesMS_ATemp = milestonesMS_ATemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.DPF_APPROVED, milestonesMS_A);
    	milestonesMS_ATemp = milestonesMS_ATemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.THESIS_COMMITTEE_APPOINTED, milestonesMS_A);
    	milestonesMS_ATemp = milestonesMS_ATemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.GRADUATION_PACKET_REQUESTED, milestonesMS_A);
    	milestonesMS_ATemp = milestonesMS_ATemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.THESIS_SUBMITTED, milestonesMS_A);
    	milestonesMS_ATemp = milestonesMS_ATemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.THESIS_APPROVED, milestonesMS_A);
    	milestonesMS_ATemp = milestonesMS_ATemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.DEFENSE_PASSED, milestonesMS_A);
    	
    	milestonesMS_A.passed = milestonesMS_ATemp;
    	result.add(milestonesMS_A);
    	
    	for (RequirementCheck i : result) {
    		if (i.name == "DEGREE_SOUGHT") continue;
    		result.get(0).passed = result.get(0).passed && i.passed;
    	}
    }
    
    // ----------------------------------------------------------
    // Overall Check for MS_B
    // ----------------------------------------------------------
    
     /**
      * Performs the overall check for MS_B students
      * @param userId the student to perform the MS_B check on
      * @param currentCourses courses the student has taken
      * @param currentMilestones milestones the student has completed
      * @param result the list of RequirementChecks the student has completed - will be modified within function
      */
    private void checkMS_B(String userId, List<CourseTaken> currentCourses, List<Milestone> currentMilestones, List<RequirementCheck> result) throws Exception {
    	checkDataExist();
    	
    	// 31 credits in total, 16 CSci credits, 3 breath courses, CSci 8970    	
    	checkCreditsRequirements(userId, currentCourses, 31, 16, 15, result);
    	checkSingleCourse(currentCourses, "COLLOQUIUM", "csci8970", 0, "S", result);
    	
    	// Plan B project
    	checkSingleCourse(currentCourses, "PLAN_B_PROJECT", "csci8760", 0, "S", result);
    	
    	// PhD level credits
    	// --------------------------------------
    	RequirementCheck phdLevelCredits = new RequirementCheck();
    	phdLevelCredits.name = "PHD_LEVEL_COURSES";
    	phdLevelCredits.details.courses = new ArrayList<CourseTaken>();
    	
    	int phdLevelCreditsCount = 0;
    	for (CourseTaken i : currentCourses) {
    		if (i.course.id.startsWith("csci8") && !i.course.id.startsWith("csci8970")) {
    			if (checkSingleValidCourse(i)) {
    				phdLevelCredits.details.courses.add(i); // Add a course
    				phdLevelCreditsCount += Integer.parseInt(i.course.numCredits);
    			}
    		}
    	}
    	phdLevelCredits.passed = (phdLevelCreditsCount >= 3);
    	result.add(phdLevelCredits);
    	// --------------------------------------
    	
    	// Milestone MS_B
    	RequirementCheck milestonesMS_B = new RequirementCheck();
    	milestonesMS_B.name = "MILESTONES_MS_B";
    	
    	boolean milestonesMS_BTemp = true;
    	milestonesMS_BTemp = milestonesMS_BTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.DPF_SUBMITTED, milestonesMS_B);
    	milestonesMS_BTemp = milestonesMS_BTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.DPF_APPROVED, milestonesMS_B);
    	milestonesMS_BTemp = milestonesMS_BTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.PROJECT_COMMITTEE_APPOINTED, milestonesMS_B);
    	milestonesMS_BTemp = milestonesMS_BTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.GRADUATION_PACKET_REQUESTED, milestonesMS_B);
    	milestonesMS_BTemp = milestonesMS_BTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.DEFENSE_PASSED, milestonesMS_B);
    	
    	milestonesMS_B.passed = milestonesMS_BTemp;
    	result.add(milestonesMS_B);
    	
    	for (RequirementCheck i : result) {
    		if (i.name == "DEGREE_SOUGHT") continue;
    		result.get(0).passed = result.get(0).passed && i.passed;
    	}
    }
    
    // ----------------------------------------------------------
    // Overall Check for MS_C
    // ----------------------------------------------------------
    
     /**
      * Performs the overall check for MS_C students
      * @param userId the student to perform the MS_C check on
      * @param currentCourses courses the student has taken
      * @param currentMilestones milestones the student has completed
      * @param result the list of RequirementChecks the student has completed - will be modified within function
      */
    private void checkMS_C(String userId, List<CourseTaken> currentCourses, List<Milestone> currentMilestones, List<RequirementCheck> result) throws Exception {
    	checkDataExist();
    	
    	// 31 credits in total, 16 CSci credits, 3 breath courses, CSci 8970
    	checkCreditsRequirements(userId, currentCourses, 31, 16, 15, result);
    	checkSingleCourse(currentCourses, "COLLOQUIUM", "csci8970", 0, "S", result);
    	
    	// PhD level credits
    	// --------------------------------------
    	RequirementCheck phdLevelCredits = new RequirementCheck();
    	phdLevelCredits.name = "PHD_LEVEL_COURSES_PLANC";
    	phdLevelCredits.passed = false;
    	phdLevelCredits.details.courses = new ArrayList<CourseTaken>();

    	for (CourseTaken i : currentCourses) {
    		if (i.course.id.startsWith("csci8") && !i.course.id.startsWith("csci8970")) {
    			if (checkSingleValidCourse(i)) {
    				if (Integer.parseInt(i.course.numCredits) >= 3){
    					phdLevelCredits.details.courses.add(i); // Add a course
    				}	
    			}
    		}
    	}
    	phdLevelCredits.passed = (phdLevelCredits.details.courses == null) ? false : (phdLevelCredits.details.courses.size() >= 2);
    	result.add(phdLevelCredits);
    	// --------------------------------------
    	
    	// Milestone MS_C
    	RequirementCheck milestonesMS_C = new RequirementCheck();
    	milestonesMS_C.name = "MILESTONES_MS_C";
    	milestonesMS_C.passed = false;
    	
    	boolean milestonesMS_CTemp = true;
    	milestonesMS_CTemp = milestonesMS_CTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.DPF_SUBMITTED, milestonesMS_C);
    	milestonesMS_CTemp = milestonesMS_CTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.DPF_APPROVED, milestonesMS_C);
    	milestonesMS_CTemp = milestonesMS_CTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.TRACKING_FORM_SUBMITTED, milestonesMS_C);
    	milestonesMS_CTemp = milestonesMS_CTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.TRACKING_FORM_APPROVED, milestonesMS_C);
    	milestonesMS_CTemp = milestonesMS_CTemp && checkSingleMilestone(currentMilestones, Milestones_All_Plans.GRADUATION_PACKET_REQUESTED, milestonesMS_C);
    	
    	milestonesMS_C.passed = milestonesMS_CTemp;
    	result.add(milestonesMS_C);
    	
    	for (RequirementCheck i : result) {
    		if (i.name == "DEGREE_SOUGHT") continue;
    		result.get(0).passed = result.get(0).passed && i.passed;
    	}
    }
    
    /** Check if a course is valid. i.e., above 5000 level and C- or above, or S.
      * @param src the course to check
      * @return boolean value indicating whether course is valid
      */
    private boolean checkSingleValidCourse(CourseTaken src) {
    	int temp = 0;
		while (!Character.isDigit(src.course.id.charAt(temp))) temp++;
		if (src.course.id.charAt(temp) < '5') return false;
		
		return (src.grade == Grade.A || src.grade == Grade.B || src.grade == Grade.C || src.grade == Grade.S);
    }
    
    /** Check if a single milestone exists in the list
      * @param currentMilestones the list current milestones
      * @param mil the milestone to check
      * @param milestones the student has completed
      * @return boolean value denoting whether the milestone is in the list
+     */
    private boolean checkSingleMilestone(List<Milestone> currentMilestones, Milestones_All_Plans mil, RequirementCheck milestones) {
    	if (currentMilestones == null) {
    		return false;
    	}
    	
    	if (milestones.details.milestones == null) milestones.details.milestones = new ArrayList<Milestone>();
    	for (Milestone i : currentMilestones) {
    		if (i.milestone == mil) {
    			milestones.details.milestones.add(i);
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
      * Check if a single course exists in the list
      * checkSingleCourse(currentCourses, "COLLOQUIUM", "csci8970", 0, "S", result);
      * @param currentCourses the list of courses taken
      * @param currentRequirement the current requirement to check against
      * @param courseId id of the course to look for
      * @param credits how many credits the course should have. 0 means there is no limit on this value
      * @param grades the grade received in the course
      * @param result the list of RequirementChecks the student has completed - will be modified within function
      */
    private void checkSingleCourse(List<CourseTaken> currentCourses, String currentRequirement, String courseId, int credits, String grades, List<RequirementCheck> result) {
    	RequirementCheck currentRequirementCheck = new RequirementCheck();
    	currentRequirementCheck.name = currentRequirement;
    	currentRequirementCheck.passed = false;
    	currentRequirementCheck.details.courses = new ArrayList<CourseTaken>();
    	
    	if (currentCourses == null) {
    		result.add(currentRequirementCheck);
    		return;
    	}
    	
    	for (CourseTaken i : currentCourses) {
    		if (i.course.id.equals(courseId)) {
    			currentRequirementCheck.details.courses.add(i);
    			if (grades.equals("S")) {
    				if (i.grade == Grade.S) {
    					if (credits > 0) {
    						currentRequirementCheck.passed = (Integer.parseInt(i.course.numCredits) >= credits);
    					} else {
    						currentRequirementCheck.passed = true;
    					}
    				}
    			} else {
    				if (i.grade == Grade.A || i.grade == Grade.B || i.grade == Grade.C) {
    					if (credits > 0) {
    						currentRequirementCheck.passed = (Integer.parseInt(i.course.numCredits) >= credits);
    					} else {
    						currentRequirementCheck.passed = true;
    					}
    				}
    			}
    		}
    	}
    	
    	result.add(currentRequirementCheck);
    }
    
    /**
      * Check course credits requirement
      * @param userId the id of the student to check the credits requirement for
      * @param currentCourses the list of courses the student has taken
      * @param totalReq the number of credits required
      * @param csciReq the number of CSCI credits required
      * @param breathReq the number of breadth requirement credits required
      * @param result the list of RequirementChecks for the student - will be modified within function
      */
    private void checkCreditsRequirements(String userId, List<CourseTaken> currentCourses, int totalReq, int csciReq, int breathReq, List<RequirementCheck> result) throws Exception {
    	// Check all breadth and total credits requirement
    	RequirementCheck breadthRequirement = new RequirementCheck();
    	breadthRequirement.name = "BREADTH_REQUIREMENT_MS";
    	breadthRequirement.details.courses = new ArrayList<CourseTaken>();
    	breadthRequirement.passed = false;
    	result.add(breadthRequirement);
    	
    	RequirementCheck totalCreditsRequirement = new RequirementCheck();
    	totalCreditsRequirement.name = "TOTAL_CREDITS";
    	totalCreditsRequirement.details.courses = new ArrayList<CourseTaken>();
    	totalCreditsRequirement.passed = false;
    	result.add(totalCreditsRequirement);
    	
    	if (currentCourses != null) {
        	int totalCredits = 0; // Total number of credits
        	int csciCredits = 0; // Total number of CSci credits
        	int breadthCredits = 0; // Total number of breath credits
        	int[] breadthCounts = new int[3]; // Check if at least one breath course in each area
        	breadthCounts[0] = 0; breadthCounts[1] = 0;breadthCounts[2] = 0;
        	
        	for (CourseTaken i : currentCourses) {
        		if (i.grade != Grade.A && i.grade != Grade.B && i.grade != Grade.C && i.grade != Grade.S) continue; // C- or above
        		// ***** Check if it is a 5000 level course
        		int temp = 0;
        		while (!Character.isDigit(i.course.id.charAt(temp))) temp++;
        		if (i.course.id.charAt(temp) < '5') continue;
        		// ***** END
        		
        		totalCreditsRequirement.details.courses.add(i);
        		totalCredits += Integer.parseInt(i.course.numCredits);
        		
        		if (i.course.id.startsWith("csci") && !i.course.id.startsWith("csci8888")&& i.grade != Grade.S)  {
        			csciCredits += Integer.parseInt(i.course.numCredits);
        		}
        		
        		// Decide if it is a breath course
        		for (CourseMapping bcourse : courses) {
        			if (bcourse.id.equals(i.course.id)) {
        				if (bcourse.courseArea != null) {
        					breadthCredits += Integer.parseInt(i.course.numCredits);
        					switch (bcourse.courseArea) {
        						case ARCHITECTURE_SYSTEMS_SOFTWARE:
        							i.course.courseArea = CourseArea.ARCHITECTURE_SYSTEMS_SOFTWARE;
        							breadthRequirement.details.courses.add(i);
        							breadthCounts[0]++;
        							break;
        						case APPLICATIONS:
        							i.course.courseArea = CourseArea.APPLICATIONS;
        							breadthRequirement.details.courses.add(i);
        							breadthCounts[1]++;
        							break;
        						case THEORY_ALGORITHMS:
        							i.course.courseArea = CourseArea.THEORY_ALGORITHMS;
        							breadthRequirement.details.courses.add(i);
        							breadthCounts[2]++;
        							break;
        						default:
        							throw new ExFatalError(); // Should not be here;
        					}
        				}
        			}
        		}
        	}
        	
        	breadthRequirement.passed = (breadthCredits >= breathReq) && (breadthCounts[0] > 0 && breadthCounts[1] > 0 && breadthCounts[2] > 0);
        	totalCreditsRequirement.passed = (totalCredits >= totalReq) && (csciCredits >= csciReq);
    	}
    	
    	// Calculate all kinds of GPAs
    	DegreeSought degreeTemp = getTranscript(userId).getDegreeSought();
    	// Overall GPA
    	RequirementCheck overAllGPA = new RequirementCheck();
    	overAllGPA.name = (degreeTemp == DegreeSought.PHD) ? "OVERALL_GPA_PHD" : "OVERALL_GPA_MS";
    	overAllGPA.passed = false;
    	
    	overAllGPA.details.gpa = calculateGPA(userId, "OVERALL");
    	overAllGPA.passed = (calculateGPA(userId, "OVERALL") >= calculateRequiredGPALevel(userId, "OVERALL"));
    	
    	result.add(overAllGPA);

    	// In-program GPA
    	RequirementCheck inProgramGPA = new RequirementCheck();
    	inProgramGPA.name = (degreeTemp == DegreeSought.PHD) ? "IN_PROGRAM_GPA_PHD" : "IN_PROGRAM_GPA_MS";
    	inProgramGPA.passed = false;
    	
    	inProgramGPA.details.gpa = calculateGPA(userId, "IN_PROGRAM");
    	inProgramGPA.passed = (calculateGPA(userId, "IN_PROGRAM") >= calculateRequiredGPALevel(userId, "IN_PROGRAM"));
    	
    	result.add(inProgramGPA);
    	
    	breadthRequirement.details.gpa = calculateGPA(userId, "BREADTH_REQUIREMENT");
    	totalCreditsRequirement.details.gpa = overAllGPA.details.gpa;
    	
    	// Breadth GPA
    	if (calculateGPA(userId, "BREADTH_REQUIREMENT") < calculateRequiredGPALevel(userId, "BREADTH_REQUIREMENT")) {
    		breadthRequirement.passed = false;
    	}
    }
}
