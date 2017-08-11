package com.endicott.edu.simulators;

import com.endicott.edu.datalayer.CollegeDao;
import com.endicott.edu.datalayer.DormitoryDao;
import com.endicott.edu.datalayer.NewsFeedDao;
import com.endicott.edu.models.CollegeModel;
import com.endicott.edu.models.DormitoryModel;
import com.endicott.edu.models.NewsFeedItemModel;
import com.endicott.edu.models.NewsType;

/**
 * Created by abrocken on 7/24/2017.
 */
public class CollegeManager {
    static public final int STARTUP_FUNDING = 100000;


    static public CollegeModel establishCollege(String runId) {
        CollegeDao collegeDao = new CollegeDao();

        // See if there already is a college for this run.
        // We don't expect this, but if so, just return it.
        try {
            CollegeModel college = collegeDao.getCollege(runId);
            return college;
        } catch (Exception e) {
        }

        // Create the college.
        CollegeModel college = new CollegeModel();
        college.setRunId(runId);
        college.setHoursAlive(1);
        college.setAvailableCash(STARTUP_FUNDING);
        collegeDao.saveCollege(college);

        // Create a News Feed item about establishing the college.
        NewsFeedItemModel note = new NewsFeedItemModel();
        note.setHour(college.getCurrentDay());
        note.setMessage("The college was established today!");
        note.setNoteType(NewsType.GENERAL_NOTE);
        NewsFeedDao noteDao = new NewsFeedDao();
        noteDao.saveNote(runId, note);

        // Create a dorm
        DormitoryModel dorm = new DormitoryModel(100, 10, 0, "Frates", runId);
        DormitoryDao dormDao = new DormitoryDao();
        dormDao.saveNewDorm(runId, dorm);

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
