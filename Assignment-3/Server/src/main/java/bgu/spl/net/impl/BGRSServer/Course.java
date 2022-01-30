package bgu.spl.net.impl.BGRSServer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Course {

    private final Integer COURSE_NUM;
    private final String COURSE_NAME;
    private final BlockingQueue<Integer> KDAM_COURSES_LIST;
    private final int NUM_OF_MAX_STUDENTS;
    private final BlockingQueue<String> REGISTERED_STUDENTS;
    private final int ROW_NUMBER;

    public Course(String data, int rowNumber) {
        String[] courseDetails = data.split("\\|");

        COURSE_NUM = Integer.parseInt(courseDetails[0]);

        COURSE_NAME = courseDetails[1];

        KDAM_COURSES_LIST = new LinkedBlockingQueue<>();
        initKdamCoursesList(courseDetails[2].substring(1, courseDetails[2].length() - 1));

        NUM_OF_MAX_STUDENTS = Integer.parseInt(courseDetails[3]);

        REGISTERED_STUDENTS = new LinkedBlockingQueue<>();

        ROW_NUMBER = rowNumber;
    }

    private void initKdamCoursesList(String KdamCoursesString) {
        if (!KdamCoursesString.isEmpty()) {
            String[] KdamCoursesArray = KdamCoursesString.split(",");
            for (String KdamCourse : KdamCoursesArray) {
                KDAM_COURSES_LIST.add(Integer.parseInt(KdamCourse));
            }
        }
    }

    public int getCourseNum() {
        return COURSE_NUM;
    }

    public List<Integer> getKdamCourses() {
        return new LinkedList<>(KDAM_COURSES_LIST);
    }

    public int getRowNumber() {
        return ROW_NUMBER;
    }

    public synchronized boolean COURSEREG(User student, List<Integer> studentCourses) {
        // Student must have all of the kdam courses and course must have available seats
        if (studentCourses.containsAll(KDAM_COURSES_LIST) && REGISTERED_STUDENTS.size() < NUM_OF_MAX_STUDENTS) {
            // Making sure that the student isn't already registered to that course
            String username = student.getUsername();
            if (!REGISTERED_STUDENTS.contains(username)) {
                REGISTERED_STUDENTS.add(username);
                student.COURSEREG(COURSE_NUM);
                return true;
            }
        }
        return false;
    }

    public String COURSESTAT() {
        List<String> registeredStudents = new LinkedList<>(REGISTERED_STUDENTS);
        Collections.sort(registeredStudents);
        return "Course: " + "(" + COURSE_NUM + ") " + COURSE_NAME + "\n" +
                "Seats Available: " + (NUM_OF_MAX_STUDENTS - REGISTERED_STUDENTS.size()) + "/" + NUM_OF_MAX_STUDENTS + "\n" +
                "Students Registered: " + Database.listToString(registeredStudents);
    }

    public boolean ISREGISTERED(String username) {
        return REGISTERED_STUDENTS.contains(username);
    }

    public boolean UNREGISTER(User student) {
        String username = student.getUsername();
        // Student must be registered to the course in order to unregister from it
        if (REGISTERED_STUDENTS.contains(username)) {
            REGISTERED_STUDENTS.remove(username);
            student.UNREGISTER(COURSE_NUM);
            return true;
        }
        return false;
    }
}
