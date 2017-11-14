package com.endicott.edu.simulators;
import com.endicott.edu.datalayer.*;
import com.endicott.edu.models.*;

import java.util.List;
import java.util.Random;

public class StudentManager {
    StudentDao dao = new StudentDao();
    CollegeDao collegeDao = new CollegeDao();
    FacultyDao facultyDao = new FacultyDao();
    DormManager dormManager = new DormManager();
    CollegeModel college = new CollegeModel();
    List<StudentModel> students;
    List<FacultyModel> faculty;
    Random rand = new Random();


    public void handleTimeChange(String runId, int hoursAlive) {
        students = dao.getStudents(runId);
        addNewStudents(runId, hoursAlive, false);
        runningTuitionOfStudent(runId, hoursAlive);
        removeStudents(runId, hoursAlive);
        updateStudentsTime(hoursAlive);
        dao.saveAllStudents(runId, students);
        faculty = facultyDao.getFaculty(runId);
        college = collegeDao.getCollege(runId);
        college.setStudentBodyHappiness(calculateStudentsHappiness(college, faculty));
        collegeDao.saveCollege(college);
    }

    private void runningTuitionOfStudent(String runId, int hoursAlive) {
        college = collegeDao.getCollege(runId);
        int dailyTuitionSum = (college.getYearlyTuitionCost() / 365) * students.size();
        Accountant.studentIncome(runId,"Student tuition received $ " + dailyTuitionSum ,dailyTuitionSum);
    }

    public void addNewStudents(String runId, int hoursAlive, boolean initial) {
        int openBeds = dormManager.getOpenBeds(runId);
        int numNewStudents = 0;
        if(initial == true){
            numNewStudents = 100;
        } else {
            numNewStudents = rand.nextInt(openBeds);
        }
        for (int i = 0; i < numNewStudents; i++) {
            StudentModel student = new StudentModel();
            if(rand.nextInt(10) + 1 > 5){
                student.setName(NameGenDao.generateName(false));
                student.setGender("Male");
            } else {
                student.setName(NameGenDao.generateName(true));
                student.setGender("Female");
            }
            student.setIdNumber(IdNumberGenDao.getID(runId));
            student.setHappinessLevel(70);
            student.setAthlete(false);
            student.setAthleticAbility(rand.nextInt(10));
            student.setTeam("");
            student.setDorm(dormManager.assignDorm(runId));
            student.setRunId(runId);
            dao.saveNewStudent(runId, student); //students gets used many times in file, don't know state when called, must save each student as created
        }

        NewsManager.createNews(runId, hoursAlive, Integer.toString(numNewStudents) + " students joined the college.", NewsType.GENERAL_NOTE);
    }

    private void removeStudents(String runId, int hoursAlive) {
        float scalingFactor = .001f;
        int currentSize = students.size();

        // scroll through students list and remove student based upon a probability determined by their happiness level
        // the lower the happiness the greater the chance they have of withdrawing
        for (int i = 0; i < students.size(); i++){
            int h = students.get(i).getHappinessLevel();
            float odds = (100f - h) * scalingFactor;
            if (didItHappen(odds)) {
                dormManager.removeStudent(runId, students.get(i).getDorm());
                students.remove(i);

            }
        }
        // Don't create a news story if no students leave
        if ((currentSize - students.size()) > 0) {
            NewsManager.createNews(runId, hoursAlive, Integer.toString(currentSize - students.size()) + " students withdrew from college.", NewsType.GENERAL_NOTE);
        }

    }

    private boolean didItHappen(float oddsBetween0And1) {
        return (Math.random() < oddsBetween0And1);
    }

    private int calculateStudentsHappiness(CollegeModel college, List <FacultyModel> faculty) {
        //calculate affects of college stats on individual student happiness
        calculateCollegeAffect(college.getReputation(), faculty.size(), college.getYearlyTuitionCost());

        int happinessSum = 0;
        int happinessLevel;
        for (int i = 0; i < students.size(); i++) {
            happinessSum += students.get(i).getHappinessLevel();
        }

        // Make sure happiness is never less than 0.
        happinessLevel = Math.max(0,happinessSum / students.size());

        return happinessLevel;

    }

    private void calculateCollegeAffect(int reputation, int numberOfFaculty, int tuitionCost){
        int reputationAffect = (reputation - 60)/10;
        int ratioAffect = -((students.size() / numberOfFaculty) - 13)/5;
        int tuitionAffect = -(tuitionCost - 40000)/1000;

        for(int i = 0; i < students.size(); i++){
            students.get(i).setHappinessLevel(students.get(i).getHappinessLevel() + reputationAffect + ratioAffect + tuitionAffect);
        }
    }

    private void updateStudentsTime(int hoursAlive){
        for(int i = 0; i < students.size(); i++){
            students.get(i).setHourLastUpdated(hoursAlive);
        }

    }

}