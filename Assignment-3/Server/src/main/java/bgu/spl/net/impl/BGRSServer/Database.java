package bgu.spl.net.impl.BGRSServer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {

    private final static Database singleton = new Database();
    private final ConcurrentHashMap<Integer, Course> AVAILABLE_COURSES;
    private final ConcurrentHashMap<String, User> REGISTERED_USERS;
    private int courseRow;

    //to prevent user from creating new bgu.spl.net.impl.BGRSServer.Database
    private Database() {
        AVAILABLE_COURSES = new ConcurrentHashMap<>();
        REGISTERED_USERS = new ConcurrentHashMap<>();
        courseRow = 0;
        initialize("./Courses.txt");
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return singleton;
    }

    /**
     * Used by the different operations to prepare an answer for the client.
     */
    public static <T> String listToString(List<T> list) {
        if (!list.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for (T item : list) {
                stringBuilder.append(item).append(",");
            }
            stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "]");
            return stringBuilder.toString();
        }
        return "[]";
    }

    /**
     * loads the courses from the file path specified
     * into the Database, returns true if successful.
     */
    boolean initialize(String coursesFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(coursesFilePath))) {
            String courseData = br.readLine();
            while (courseData != null) {
                // We assume that all provided courses in the txt file are valid
                courseRow++;
                Course course = new Course(courseData, courseRow);
                AVAILABLE_COURSES.putIfAbsent(course.getCourseNum(), course);
                courseData = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean REG(String username, String password, boolean isAdmin) {
        // putIfAbsent will return null, only if the username doesn't exist in the REGISTERED_USERS map
        return REGISTERED_USERS.putIfAbsent(username, new User(username, password, isAdmin)) == null;
    }

    public boolean LOGIN(String username, String password) {
        // User must be registered to login
        if (REGISTERED_USERS.containsKey(username)) {
            synchronized (REGISTERED_USERS.get(username)) {
                User user = REGISTERED_USERS.get(username);
                // User must not be logged-in
                if (!user.isLoggedIn()) {
                    // Password must be correct
                    return user.LOGIN(password);
                }
            }
        }
        return false;
    }

    public boolean LOGOUT(String activeUsername) {
        User activeUser = REGISTERED_USERS.get(activeUsername);
        // User must be logged-in
        if (isLoggedStudent(activeUser) || isLoggedAdmin(activeUser)) {
            activeUser.LOGOUT();
            return true;
        }
        return false;
    }

    public boolean COURSEREG(String activeUsername, Integer courseNumber) {
        User activeUser = REGISTERED_USERS.get(activeUsername);
        // User must be logged-in and NOT be an Admin
        if (isLoggedStudent(activeUser)) {
            Course course = AVAILABLE_COURSES.get(courseNumber);
            // Course must exist
            if (course != null) {
                return course.COURSEREG(activeUser, activeUser.getCourses());
            }
        }
        return false;
    }

    public String KDAMCHECK(String activeUsername, Integer courseNumber) {
        User activeUser = REGISTERED_USERS.get(activeUsername);
        // User must be logged-in and NOT be an Admin
        if (isLoggedStudent(activeUser)) {
            Course course = AVAILABLE_COURSES.get(courseNumber);
            // Course must exist
            if (course != null) {
                List<Integer> kdamCourses = course.getKdamCourses();
                kdamCourses.sort(Comparator.comparingInt(c -> AVAILABLE_COURSES.get(c).getRowNumber()));
                return listToString(kdamCourses);
            }
        }
        return null;
    }

    public String COURSESTAT(String activeUsername, Integer courseNumber) {
        User activeUser = REGISTERED_USERS.get(activeUsername);
        // User must be logged-in and BE an Admin
        if (isLoggedAdmin(activeUser)) {
            Course course = AVAILABLE_COURSES.get(courseNumber);
            // Course must exist
            if (course != null) {
                return course.COURSESTAT();
            }
        }
        return null;
    }

    public String STUDENTSTAT(String activeUsername, String username) {
        User activeUser = REGISTERED_USERS.get(activeUsername);
        // Active user must be logged-in and BE an Admin
        if (isLoggedAdmin(activeUser)) {
            User user = REGISTERED_USERS.get(username);
            // Inquired user must exist and be a student
            if (user != null && !user.isAdmin()) {
                return user.STUDENTSTAT(Comparator.comparingInt(c -> AVAILABLE_COURSES.get(c).getRowNumber()));
            }
        }
        return null;
    }

    public String ISREGISTERED(String activeUsername, Integer courseNumber) {
        User activeUser = REGISTERED_USERS.get(activeUsername);
        // User must be logged in and NOT be an Admin
        if (isLoggedStudent(activeUser)) {
            Course course = AVAILABLE_COURSES.get(courseNumber);
            if (course != null) {
                if (course.ISREGISTERED(activeUsername)) {
                    return "REGISTERED";
                }
                return "NOT REGISTERED";
            }
        }
        return null;
    }

    public boolean UNREGISTER(String activeUsername, Integer courseNumber) {
        User activeUser = REGISTERED_USERS.get(activeUsername);
        // User must be logged in and NOT be an Admin
        if (isLoggedStudent(activeUser)) {
            Course course = AVAILABLE_COURSES.get(courseNumber);
            // Course must exist
            if (course != null) {
                return course.UNREGISTER(activeUser);
            }
        }
        return false;
    }

    public String MYCOURSES(String activeUsername) {
        User activeUser = REGISTERED_USERS.get(activeUsername);
        // User must be logged in and NOT be an Admin
        if (isLoggedStudent(activeUser)) {
            List<Integer> myCourses = activeUser.getCourses();
            myCourses.sort(Comparator.comparingInt(c -> AVAILABLE_COURSES.get(c).getRowNumber()));
            return listToString(myCourses);
        }
        return null;
    }

    private boolean isLoggedStudent(User user) {
        return user != null && user.isLoggedIn() && !user.isAdmin();
    }

    private boolean isLoggedAdmin(User user) {
        return user != null && user.isLoggedIn() && user.isAdmin();
    }
}
