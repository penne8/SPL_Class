package bgu.spl.net.impl.BGRSServer;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class User {

    private final List<Integer> COURSES;
    private final String USERNAME;
    private final String PASSWORD;
    private final boolean IS_ADMIN;
    private boolean loggedIn;

    public User(String username, String password, boolean isAdmin) {
        COURSES = new LinkedList<>();
        USERNAME = username;
        PASSWORD = password;
        IS_ADMIN = isAdmin;
        loggedIn = false;
    }

    public List<Integer> getCourses() {
        return COURSES;
    }

    public String getUsername() {
        return USERNAME;
    }

    public boolean isAdmin() {
        return IS_ADMIN;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void COURSEREG(Integer courseNumber) {
        COURSES.add(courseNumber);
    }

    public String STUDENTSTAT(Comparator<Integer> coursesRowComparator) {
        List<Integer> courses = new LinkedList<>(COURSES);
        courses.sort(coursesRowComparator);
        return "Student: " + USERNAME + "\n" +
                "Courses: " + Database.listToString(courses);
    }

    public boolean LOGIN(String password) {
        if (password.equals(PASSWORD)) {
            loggedIn = true;
        }
        return loggedIn;
    }

    public void LOGOUT() {
        loggedIn = false;
    }

    public void UNREGISTER(Integer courseNum) {
        COURSES.remove(courseNum);
    }
}
