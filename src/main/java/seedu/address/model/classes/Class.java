package seedu.address.model.classes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;

/**
 * Represents a class, consisting of a tutor, a class name, and a roster of
 * students. Stores a mutable list of students enrolled under the given tutor.
 */
public class Class {
    /** Tutor responsible for this class. */
    private Tutor tutor;

    /** Roster of students in this class. */
    private final ArrayList<Student> students;

    /** Human-readable name of the class. */
    private final String className;

    /** List of sessions conducted under this class. */
    private final ArrayList<ClassSession> sessions;

    /**
     * Constructs a class with the specified tutor and name.
     *
     * @param tutor the tutor in charge of the class
     * @param className the human-readable class name (e.g., "CS2103T T12")
     */
    public Class(Tutor tutor, String className) {
        this.tutor = tutor;
        this.students = new ArrayList<>();
        this.sessions = new ArrayList<>();
        this.className = className;
    }

    /**
     * Adds a student to this class's roster.
     *
     * @param student the student to add
     */
    public void addStudent(Student student) {
        students.add(student);
    }

    /**
     * Removes a student from this class's roster.
     *
     * @param student the student to remove
     */
    public void removeStudent(Student student) {
        students.remove(student);
    }

    /**
     * Returns the name of this class.
     *
     * @return the class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Returns the tutor in charge of this class.
     *
     * @return the tutor
     */
    public Tutor getTutor() {
        return tutor;
    }


    /**
     * Sets the tutor in charge of this class.
     *
     * @param tutor the tutor
     */
    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    /**
     * Returns the list of students enrolled in this class. Note: The returned
     * list is the internal mutable list; modifications affect this instance.
     *
     * @return the list of students
     */
    public ArrayList<Student> getStudents() {
        return students;
    }

    /**
     * Creates and adds a new session for this class.
     * Automatically initializes attendance for all current students.
     *
     * @param sessionName the name or title of the session (e.g., "Week 3 Tutorial")
     * @param dateTime the date and time of the session
     * @param location the location where the session will be held
     * @return the newly created session
     */
    public ClassSession addSession(String sessionName, LocalDateTime dateTime, String location) {
        ClassSession session = new ClassSession(this, sessionName, dateTime, location);
        sessions.add(session);
        return session;
    }

    /**
     * Removes a session from this class.
     *
     * @param session the session to remove
     */
    public void removeSession(ClassSession session) {
        sessions.remove(session);
    }

    /**
     * Returns all sessions conducted under this class.
     *
     * @return the list of sessions
     */
    public List<ClassSession> getAllSessions() {
        return new ArrayList<>(sessions);
    }

    /**
     * Returns all upcoming (future) sessions for this class.
     * A session is considered future if its scheduled time is after now.
     *
     * @return a list of future sessions
     */
    public List<ClassSession> getFutureSessions() {
        LocalDateTime now = LocalDateTime.now();
        return sessions.stream()
                .filter(session -> session.getDateTime().isAfter(now))
                .collect(Collectors.toList());
    }

    /**
     * Returns all past sessions for this class.
     * A session is considered past if its scheduled time is before now.
     *
     * @return a list of past sessions
     */
    public List<ClassSession> getPastSessions() {
        LocalDateTime now = LocalDateTime.now();
        return sessions.stream()
                .filter(session -> session.getDateTime().isBefore(now))
                .collect(Collectors.toList());
    }
}
