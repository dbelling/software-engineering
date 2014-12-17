package edu.umn.csci5801.testing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.umn.csci5801.GRADS;
import edu.umn.csci5801.GRADSIntf;
import edu.umn.csci5801.exceptions.*;
import edu.umn.csci5801.model.*;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

//import junit.framework.Assert;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class GRADSTestDriver {
	private GRADSIntf grads;
	
	@Before
	public void init() throws Exception {
		grads = new GRADS();
		grads.loadUsers("data/users.txt");
		grads.loadCourses("data/courses.txt");
		grads.loadRecords("data/students.txt");
	}
	
	@After
	public void close() {
	}
	
	// For detailed explanation about what these test cases do, please refer to the document attached.
	
	@Test
	public void testLoadUsers() throws Exception {
		grads.loadUsers("data/users.txt");
	}
	
	@Test(expected = ExCantLoadUsers.class)
	public void testLoadUsersException() throws Exception {
		grads.loadUsers("thisFileShouldNotExist.txt");
	}
	
	@Test
	public void testLoadCourses() throws Exception {
		grads.loadCourses("data/courses.txt");
	}
	
	@Test(expected = ExCantLoadCourses.class)
	public void testLoadCoursesException() throws Exception {
		grads.loadCourses("thisFileShouldNotExist.txt");
	}
	
	@Test
	public void testLoadRecords() throws Exception {
		grads.loadRecords("data/students.txt");
	}
	
	@Test(expected = ExCantLoadRecords.class)
	public void testLoadRecordsException() throws Exception {
		grads.loadRecords("thisFileShouldNotExist.txt");
	}
	
	@Test
	public void testSetUser() throws Exception {
		grads.setUser("tolas9999");
		Assert.assertEquals("tolas9999", grads.getUser());
	}
	
	@Test(expected = ExIdNotFound.class)
	public void testSetUserException() throws Exception {
		grads.setUser("thisUserShouldNotExist");
	}
	
	@Test
	public void testGetUser() throws Exception {
		grads.setUser("smith0001");
		Assert.assertEquals("smith0001", grads.getUser());
	}
	
	@Test
	public void testGetStudentIDsGPCUser() throws Exception {
		grads.setUser("tolas9999"); //set current user to GPC
		List<String> studentIDs = grads.getStudentIDs();
		Assert.assertNotNull(studentIDs);
	}
	
	@Test(expected = ExIncorrectPermissions.class)
	public void testGetStudentIDsStudent() throws Exception {
		grads.setUser("gayxx067"); //set current user to Student
		List<String> studentIDs = grads.getStudentIDs();
		Assert.assertNotNull(studentIDs);
	}

	@Test
	public void testGetTranscriptGPC() throws Exception {
		grads.loadRecords("data/newStudentsPHD.txt");
		grads.setUser("tolas9999");
		
		StudentRecord testRecord = grads.getTranscript("group15PHD_PASSED");
		StudentRecord truthRecord = new Gson().fromJson( new FileReader( new File("test/transcriptPHD_PASSED.txt")), new TypeToken<StudentRecord>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test
	public void testGetTranscriptStudent() throws Exception {
		grads.setUser("nguy0621");
		
		StudentRecord testRecord = grads.getTranscript("nguy0621");
		List<StudentRecord> truthRecord = new Gson().fromJson( new FileReader( new File("test/recordNguy0621.txt")), new TypeToken<List<StudentRecord>>(){}.getType());
		//StudentRecord truthRecord = new Gson().fromJson( new FileReader( new File("test/transcriptNguy0621.txt")), new TypeToken<StudentRecord>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord.get(0));
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test(expected = ExIncorrectPermissions.class)
	public void testGetTranscriptStudentInCorrectPermissionsException() throws Exception {
		grads.setUser("nguy0621");
		grads.getTranscript("gayxx067");
	}
	
	@Test(expected = ExIdNotFound.class)
	public void testGetTranscriptIdNotFoundException() throws Exception {
		grads.setUser("nguy0621");
		grads.getTranscript("thisIdShouldNotExist");
	}
	
	@Test
	public void testUpdateTranscript() throws Exception {
		grads.loadRecords("data/newStudentsPHD.txt");
		grads.setUser("tolas9999");
		
		StudentRecord testRecord = grads.getTranscript("group15PHD_PASSED");
		testRecord.getCoursesTaken().get(0).course.id = "test1234";
		
		grads.updateTranscript("group15PHD_PASSED", testRecord);
		testRecord = grads.getTranscript("group15PHD_PASSED");
		StudentRecord truthRecord = new Gson().fromJson( new FileReader( new File("test/updatedTranscriptPHD_PASSED.txt")), new TypeToken<StudentRecord>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
		
		testRecord.getCoursesTaken().get(0).course.id = "csci5511";
		grads.updateTranscript("group15PHD_PASSED", testRecord);
		testRecord = grads.getTranscript("group15PHD_PASSED");
		truthRecord = new Gson().fromJson( new FileReader( new File("test/transcriptPHD_PASSED.txt")), new TypeToken<StudentRecord>(){}.getType());
		testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test(expected = ExIncorrectPermissions.class)
	public void testUpdateTranscriptIncorrectPermissionsException() throws Exception {
		grads.setUser("nguy0621");	
		StudentRecord testRecord = grads.getTranscript("nguy0621");
		testRecord.getCoursesTaken().get(0).course.id = "test1234";
		grads.updateTranscript("group15PHD_PASSED", testRecord);
	}
	
	@Test
	public void testAddNote() throws Exception {
		grads.loadRecords("data/newStudentsPHD.txt");
		grads.setUser("tolas9999");
		
		grads.addNote("group15PHD_PASSED", "CSCI 5801 Test");
		StudentRecord testRecord = grads.getTranscript("group15PHD_PASSED");
		StudentRecord truthRecord = new Gson().fromJson( new FileReader( new File("test/transcriptWithNotesPHD_PASSED.txt")), new TypeToken<StudentRecord>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
		
		testRecord = grads.getTranscript("group15PHD_PASSED");
		testRecord.getNotes().remove(2);
		grads.updateTranscript("group15PHD_PASSED", testRecord);
	}
	
	@Test(expected = ExIncorrectPermissions.class)
	public void testAddNoteIncorrectPermissionsException() throws Exception {
		grads.loadRecords("data/newStudentsPHD.txt");
		grads.setUser("nguy0621");
		grads.addNote("group15PHD_PASSED", "CSCI 5801 Test");
	}
	
	@Test
	public void testGenerateProgressSummaryPHD() throws Exception {
		grads.loadRecords("data/newStudentsPHD.txt");
		grads.setUser("tolas9999");
		
		ProgressSummary testRecord = grads.generateProgressSummary("group15PHD_PASSED");
		ProgressSummary truthRecord = new Gson().fromJson( new FileReader( new File("test/progressSummaryPHD_PASSED.txt")), new TypeToken<ProgressSummary>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test
	public void testGenerateProgressSummaryPHDStudentUser() throws Exception {
		grads.setUser("nguy0621");
		ProgressSummary testRecord = grads.generateProgressSummary("nguy0621");
		ProgressSummary truthRecord = new Gson().fromJson( new FileReader( new File("test/ProgressNguy0621.txt")), new TypeToken<ProgressSummary>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test
	public void testGenerateProgressSummaryMS_A() throws Exception {
		grads.loadRecords("data/newStudentsMS_A.txt");
		grads.setUser("tolas9999");
		
		ProgressSummary testRecord = grads.generateProgressSummary("group15MS_A_PASSED");
		ProgressSummary truthRecord = new Gson().fromJson( new FileReader( new File("test/progressSummaryMS_A_PASSED.txt")), new TypeToken<ProgressSummary>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test
	public void testGenerateProgressSummaryMS_AStudentUser() throws Exception {
		grads.loadRecords("data/newStudentsMS_A.txt");
		grads.loadUsers("data/newUsers.txt");
		grads.setUser("group15MS_A_PASSED");
		
		ProgressSummary testRecord = grads.generateProgressSummary("group15MS_A_PASSED");
		ProgressSummary truthRecord = new Gson().fromJson( new FileReader( new File("test/progressSummaryMS_A_PASSED.txt")), new TypeToken<ProgressSummary>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test
	public void testGenerateProgressSummaryMS_B() throws Exception {
		grads.loadRecords("data/newStudentsMS_B.txt");
		grads.setUser("tolas9999");
		
		ProgressSummary testRecord = grads.generateProgressSummary("group15MS_B_PASSED");
		ProgressSummary truthRecord = new Gson().fromJson( new FileReader( new File("test/progressSummaryMS_B_PASSED.txt")), new TypeToken<ProgressSummary>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test
	public void testGenerateProgressSummaryMS_BStudent() throws Exception {
		grads.loadRecords("data/newStudentsMS_B.txt");
		grads.loadUsers("data/newUsers.txt");
		grads.setUser("group15MS_B_PASSED");
		
		ProgressSummary testRecord = grads.generateProgressSummary("group15MS_B_PASSED");
		ProgressSummary truthRecord = new Gson().fromJson( new FileReader( new File("test/progressSummaryMS_B_PASSED.txt")), new TypeToken<ProgressSummary>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test
	public void testGenerateProgressSummaryMS_C() throws Exception {
		grads.loadRecords("data/newStudentsMS_C.txt");
		grads.setUser("tolas9999");
		
		ProgressSummary testRecord = grads.generateProgressSummary("group15MS_C_PASSED");
		ProgressSummary truthRecord = new Gson().fromJson( new FileReader( new File("test/progressSummaryMS_C_PASSED.txt")), new TypeToken<ProgressSummary>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test
	public void testGenerateProgressSummaryMS_CStudent() throws Exception {
		grads.loadRecords("data/newStudentsMS_C.txt");
		grads.loadUsers("data/newUsers.txt");
		grads.setUser("group15MS_C_PASSED");
		
		ProgressSummary testRecord = grads.generateProgressSummary("group15MS_C_PASSED");
		ProgressSummary truthRecord = new Gson().fromJson( new FileReader( new File("test/progressSummaryMS_C_PASSED.txt")), new TypeToken<ProgressSummary>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test
	public void checkNoCourseImpact() throws Exception {
		grads.setUser("nguy0621");
		grads.loadRecords("test/recordNguy0621.txt");
		List<CourseTaken> courses = new ArrayList<CourseTaken>();
		ProgressSummary oldSummary = grads.generateProgressSummary("nguy0621");
		ProgressSummary newSummary = grads.simulateCourses("nguy0621", courses);
		
		/* All of these attributes should be the same. */
		Assert.assertEquals(oldSummary.getStudent(), newSummary.getStudent());
		Assert.assertEquals(oldSummary.getDepartment(), newSummary.getDepartment());
		Assert.assertEquals(oldSummary.getDegreeSought(), newSummary.getDegreeSought());
		Assert.assertEquals(oldSummary.getTerm(), newSummary.getTerm());
		Assert.assertEquals(oldSummary.getAdvisors(), newSummary.getAdvisors());
		Assert.assertEquals(oldSummary.getCommittee(), newSummary.getCommittee());
		
		/* Iterate through the requirement checks and make sure we find the same
		 * names, and that the same requirements pass or fail. */
		List<RequirementCheck> oldResults = oldSummary.getRequirementCheckResults();
		List<RequirementCheck> newResults = newSummary.getRequirementCheckResults();
		boolean differenceFound = (oldResults.size() != newResults.size());
		for(int i = 0; i < newResults.size(); i++) {
			boolean matchFound = false;
			for(int j = 0; j < oldResults.size(); j++) {
				RequirementCheck curResult = oldResults.get(j);
				if(curResult.name == newResults.get(i).name &&
				   curResult.passed == newResults.get(i).passed) {
					matchFound = true;
				}
			}
			if(!matchFound) {
				differenceFound = true;
				break;
			}
		}
		Assert.assertFalse(differenceFound);
	}
	
	@Test
	public void checkSingleCourseWithNoImpact() throws Exception {
		grads.setUser("nguy0621");
		grads.loadRecords("test/recordNguy0621.txt");
		Course courseA = new Course("Functional Genomics, Systems Biology, and Bioinformatics", "csci5461", "3", CourseArea.ARCHITECTURE_SYSTEMS_SOFTWARE);
		CourseTaken courseAtaken = new CourseTaken(courseA, new Term(Semester.SPRING,2015), Grade.B);
		List<CourseTaken> courses = new ArrayList<CourseTaken>();
		courses.add(courseAtaken);
		ProgressSummary oldSummary = grads.generateProgressSummary("nguy0621");
		ProgressSummary newSummary = grads.simulateCourses("nguy0621", courses);
		
		/* Iterate through the requirement checks and make sure we find the same
		 * names, and that the same requirements pass or fail. */
		List<RequirementCheck> oldResults = oldSummary.getRequirementCheckResults();
		List<RequirementCheck> newResults = newSummary.getRequirementCheckResults();
		boolean differenceFound = (oldResults.size() != newResults.size());
		for(int i = 0; i < newResults.size(); i++) {
			boolean matchFound = false;
			for(int j = 0; j < oldResults.size(); j++) {
				RequirementCheck curResult = oldResults.get(j);
				if(curResult.name == newResults.get(i).name &&
				   curResult.passed == newResults.get(i).passed) {
					matchFound = true;
				}
			}
			if(!matchFound) {
				differenceFound = true;
				break;
			}
		}
		Assert.assertFalse(differenceFound);

	}
	
	@Test
	public void checkSingleCourseImpact() throws Exception {
		grads.setUser("nguy0621");
		
		grads.loadRecords("test/recordNguy0621-MissingRecords.txt");
		Course courseA = new Course("Thesis Credit: Doctoral", "csci8888", "10", CourseArea.ARCHITECTURE_SYSTEMS_SOFTWARE);
		CourseTaken courseAtaken = new CourseTaken(courseA, new Term(Semester.SPRING,2008), Grade.S);
		List<CourseTaken> courses = new ArrayList<CourseTaken>();
		courses.add(courseAtaken);
		ProgressSummary oldSummary = grads.generateProgressSummary("nguy0621");
		ProgressSummary newSummary = grads.simulateCourses("nguy0621", courses);
		
		/* Iterate through the requirement checks and make sure we find the same
		 * names, and that the same requirements pass or fail. */
		List<RequirementCheck> oldResults = oldSummary.getRequirementCheckResults();
		List<RequirementCheck> newResults = newSummary.getRequirementCheckResults();
		boolean differenceFound = (oldResults.size() != newResults.size());
		for(int i = 0; i < newResults.size(); i++) {
			boolean matchFound = false;
			for(int j = 0; j < oldResults.size(); j++) {
				RequirementCheck curResult = oldResults.get(j);
				if(curResult.name == newResults.get(i).name &&
				   curResult.passed == newResults.get(i).passed) {
					matchFound = true;
				}
			}
			if(!matchFound) {
				differenceFound = true;
				break;
			}
		}
		Assert.assertTrue(differenceFound);

	}
	
	@Test
	public void checkMultipleCourseImpact() throws Exception {
		grads.setUser("nguy0621");
		grads.loadRecords("test/recordNguy0621-MissingRecords.txt");
		Course courseA = new Course("Understanding the Social Web", "csci8117", "3", null);
		Course courseB = new Course("Advanced Compiler Techniques", "csci8161", "3", null);
		CourseTaken courseAtaken = new CourseTaken(courseA, new Term(Semester.SPRING,2015), Grade.B);
		CourseTaken courseBtaken = new CourseTaken(courseB, new Term(Semester.SPRING,2015), Grade.A);
		List<CourseTaken> courses = new ArrayList<CourseTaken>();
		courses.add(courseAtaken);
		courses.add(courseBtaken);
		ProgressSummary oldSummary = grads.generateProgressSummary("nguy0621");
		ProgressSummary newSummary = grads.simulateCourses("nguy0621", courses);

		/* Iterate through the requirement checks and make sure we find the same
		 * names, and that the same requirements pass or fail. */
		List<RequirementCheck> oldResults = oldSummary.getRequirementCheckResults();
		List<RequirementCheck> newResults = newSummary.getRequirementCheckResults();
		boolean differenceFound = (oldResults.size() != newResults.size());
		for(int i = 0; i < newResults.size(); i++) {
			boolean matchFound = false;
			for(int j = 0; j < oldResults.size(); j++) {
				RequirementCheck curResult = oldResults.get(j);
				if(curResult.name == newResults.get(i).name &&
				   curResult.passed == newResults.get(i).passed) {
					matchFound = true;
				}
			}
			if(!matchFound) {
				differenceFound = true;
				break;
			}
		}
		Assert.assertTrue(differenceFound);

	}
	
	@Test(expected = ExIncorrectPermissions.class)
	public void checkImpactIncorrectPermission() throws Exception {
		grads.setUser("nguy0621");
		Course courseA = new Course("Functional Genomics, Systems Biology, and Bioinformatics", "csci5461", "3", CourseArea.ARCHITECTURE_SYSTEMS_SOFTWARE);
		CourseTaken courseAtaken = new CourseTaken(courseA, new Term(Semester.SPRING,2015), Grade.B);
		List<CourseTaken> courses = new ArrayList<CourseTaken>();
		courses.add(courseAtaken);
		grads.simulateCourses("belli058", courses);
	}
	
	@Test
	public void checkEmptyTranscriptPHD() throws Exception {
		grads.loadRecords("data/newStudentsPHD.txt");
		grads.setUser("tolas9999");
		
		ProgressSummary testRecord = grads.generateProgressSummary("group15PHD_EMPTY");
		ProgressSummary truthRecord = new Gson().fromJson( new FileReader( new File("test/progressSummaryPHD_EMPTY.txt")), new TypeToken<ProgressSummary>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test
	public void checkEmptyTranscriptMS_A() throws Exception {
		grads.loadRecords("data/newStudentsMS_A.txt");
		grads.setUser("tolas9999");
		
		ProgressSummary testRecord = grads.generateProgressSummary("group15MS_A_EMPTY");
		ProgressSummary truthRecord = new Gson().fromJson( new FileReader( new File("test/progressSummaryMS_A_EMPTY.txt")), new TypeToken<ProgressSummary>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test
	public void checkEmptyTranscriptMS_B() throws Exception {
		grads.loadRecords("data/newStudentsMS_B.txt");
		grads.setUser("tolas9999");
		
		ProgressSummary testRecord = grads.generateProgressSummary("group15MS_B_EMPTY");
		ProgressSummary truthRecord = new Gson().fromJson( new FileReader( new File("test/progressSummaryMS_B_EMPTY.txt")), new TypeToken<ProgressSummary>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
	
	@Test
	public void checkEmptyTranscriptMS_C() throws Exception {
		grads.loadRecords("data/newStudentsMS_C.txt");
		grads.setUser("tolas9999");
		
		ProgressSummary testRecord = grads.generateProgressSummary("group15MS_C_EMPTY");
		ProgressSummary truthRecord = new Gson().fromJson( new FileReader( new File("test/progressSummaryMS_C_EMPTY.txt")), new TypeToken<ProgressSummary>(){}.getType());
		String testRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(testRecord);
		String truthRecordRep = new GsonBuilder().setPrettyPrinting().create().toJson(truthRecord);
		Assert.assertEquals(testRecordRep, truthRecordRep);
	}
}
