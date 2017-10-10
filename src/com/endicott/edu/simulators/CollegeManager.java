package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.*;
import com.endicott.edu.models.*;

import java.util.Random;
import java.util.logging.Logger;

// Created by abrocken on 7/24/2017.

public class CollegeManager {
    static public final int STARTUP_FUNDING = 100000;


    static public CollegeModel establishCollege(String runId) {
        CollegeDao collegeDao = new CollegeDao();

        Logger logger = Logger.getLogger("CollegeManager");
        logger.info("Establishing the college");

        // See if there already is a college for this run.
        // We don't expect this, but if so, just return it.
        logger.info("Checking if college exists.");
        try {
            logger.info("College exists.");
            return collegeDao.getCollege(runId);
        } catch (Exception ignored) {
        }

        // Create the college.
        logger.info("Creating college");
        CollegeModel college = new CollegeModel();
        college.setRunId(runId);
        college.setHoursAlive(1);
        college.setAvailableCash(STARTUP_FUNDING);
        collegeDao.saveCollege(college);

        // Create a News Feed item about establishing the college.
        logger.info("Creating newsfeed");
        NewsFeedItemModel note = new NewsFeedItemModel();
        note.setHour(college.getCurrentDay());
        note.setMessage("The college was established today!");
        note.setNoteType(NewsType.GENERAL_NOTE);
        NewsFeedDao noteDao = new NewsFeedDao();
        noteDao.saveNote(runId, note);

        // Create a dorm

        logger.info("Creating dorm");
        DormitoryModel dorm = new DormitoryModel(100, 10, 0, "Hampshire Hall",0, 0, "none", "none",5, runId);
        DormitoryDao dormDao = new DormitoryDao();
        dormDao.saveNewDorm(runId, dorm);

        // Creating students

        logger.info("Creating students");
        StudentModel student = new StudentModel();
        StudentDao studentDao = new StudentDao();
        Random rand = new Random();

        //Create a default sport
        logger.info("Creating sport");
        SportModel sport = new SportModel(15, 30, 10, 0, 0, 0 , 0 , 0, 14, 100, "Men's Soccer", runId );
        SportsDao sportDao = new SportsDao();
        sportDao.saveNewSport(runId, sport);

//        for(int i = 0; i < 100; i++){
            student.setIdNumber(100000 + rand.nextInt(900000));
            student.setHappinessLevel(rand.nextInt(100));
            student.setAthlete(false);
            student.setAthleticAbility(rand.nextInt(100));
            student.setTeam("");
            student.setDorm("");
            if(rand.nextInt(1) == 1){
                student.setGender("Male");
            } else {
                student.setGender("Female");
            }
            student.setRunId(runId);
            studentDao.saveNewStudent(runId, student);
        //}

        logger.info("Done creating college");
        return college;
    }

    static public void sellCollege(String runId) {
        CollegeDao collegeDao = new CollegeDao();
        DormitoryDao dormitoryDao = new DormitoryDao();
        NewsFeedDao noteDao = new NewsFeedDao();

        collegeDao.deleteCollege(runId);
        dormitoryDao.deleteDorms(runId);
        noteDao.deleteNotes(runId);
    }

    static public CollegeModel nextDay(String runId) {
        CollegeDao collegeDao = new CollegeDao();

        // Get the college
        CollegeModel college = collegeDao.getCollege(runId);

        // Advance time
        college.advanceClock(24);
        collegeDao.saveCollege(college);
        int hoursAlive = college.getHoursAlive();

        // Tell everyone about the time change.
        DormManager dormManager = new DormManager();
        dormManager.handleTimeChange(runId, hoursAlive);

        SportManager sportManager = new SportManager();
        sportManager.handleTimeChange(runId, hoursAlive);

        return college;
    }

    static public boolean doesCollegeExist(String runId) {
        CollegeDao collegeDao = new CollegeDao();

        // See if there already is a college for this run.
        // We don't expect this, but if so, just return it.
        try {
            CollegeModel college = collegeDao.getCollege(runId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
