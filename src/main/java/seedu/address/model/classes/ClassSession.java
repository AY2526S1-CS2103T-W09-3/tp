package seedu.address.model.classes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import seedu.address.model.person.Student;

/**
 * Represents a single teaching session under a {@link Class}.
 * <p>
 * Each session contains identifying information (name, time, location),
 * a reference to its parent class, and a record of student attendance.
 * <p>
 * Attendance is tracked via a mutable mapping of {@link Student} to a boolean flag,
 * where {@code true} indicates presence and {@code false} indicates absence.
 * <p>
 * Attendance is automatically initialized upon session creation based on
 * the roster of students in the parent {@link Class}.
 */
public class ClassSession {

    /** The class this session belongs to. */
    private final Class parentClass;

    /** A descriptive name for the session (e.g., "Week 3 Tutorial"). */
    private String sessionName;

    /** The scheduled date and time of the session. */
    private LocalDateTime dateTime;

    /** Optional location of the session (e.g., "COM1-B103"). */
    private String location;

    /**
     * Tracks the attendance of students for this session.
     * Key: {@link Student} instance enrolled in the parent class.
     * Value: {@code true} if attended, {@code false} if absent.
     */
    private Map<Student, Boolean> attendanceRecord;

    /**
     * Constructs a ClassSession with the given parameters.
     * <p>
     * Attendance is automatically initialized for all students
     * currently enrolled in the specified parent class.
     *
     * @param parentClass the class this session belongs to
     * @param sessionName the name of the session
     * @param dateTime the scheduled date and time
     * @param location the session's location (nullable)
     * @throws IllegalArgumentException if the parent class is null
     */
    public ClassSession(Class parentClass, String sessionName, LocalDateTime dateTime, String location) {
        if (parentClass == null) {
            throw new IllegalArgumentException("Parent class cannot be null for session: " + sessionName);
        }

        this.parentClass = parentClass;
        this.sessionName = sessionName;
        this.dateTime = dateTime;
        this.location = location;
        this.attendanceRecord = new HashMap<>();

        // Automatically initialize attendance records
        initializeAttendance();
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    /**
     * Returns the parent class this session belongs to.
     *
     * @return the parent {@link Class}
     */
    public Class getParentClass() {
        return parentClass;
    }

    /**
     * Returns the name of the session.
     *
     * @return the session name
     */
    public String getSessionName() {
        return sessionName;
    }

    /**
     * Updates the session name.
     *
     * @param sessionName the new name of the session
     */
    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    /**
     * Returns the scheduled date and time of the session.
     *
     * @return the session {@link LocalDateTime}
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Updates the scheduled date and time of the session.
     *
     * @param dateTime the new {@link LocalDateTime} for the session
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Returns the location of the session.
     *
     * @return the session location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Updates the location of the session.
     *
     * @param location the new session location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    // -------------------------------------------------------------------------
    // Attendance Management
    // -------------------------------------------------------------------------

    /**
     * Initializes attendance records for all students in the parent class.
     * <p>
     * All students are marked absent ({@code false}) by default.
     * Existing attendance entries are preserved for previously initialized students.
     * <p>
     * This method is automatically called during construction.
     */
    public void initializeAttendance() {
        for (Student s : parentClass.getStudents()) {
            attendanceRecord.putIfAbsent(s, false);
        }
    }

    /**
     * Marks a student's attendance as present.
     *
     * @param student the student to mark present
     */
    public void markPresent(Student student) {
        attendanceRecord.put(student, true);
    }

    /**
     * Marks a student's attendance as absent.
     *
     * @param student the student to mark absent
     */
    public void markAbsent(Student student) {
        attendanceRecord.put(student, false);
    }

    /**
     * Checks if a student attended this session.
     *
     * @param student the student to check
     * @return {@code true} if attended, {@code false} if absent or uninitialized
     */
    public boolean hasAttended(Student student) {
        return attendanceRecord.getOrDefault(student, false);
    }

    /**
     * Returns the full attendance record mapping.
     *
     * @return a map of students to their attendance status
     */
    public Map<Student, Boolean> getAttendanceRecord() {
        return attendanceRecord;
    }

    /**
     * Returns the number of students marked as present.
     *
     * @return the count of present students
     */
    public long getAttendanceCount() {
        return attendanceRecord.values().stream().filter(Boolean::booleanValue).count();
    }

    // -------------------------------------------------------------------------
    // Utility and Overrides
    // -------------------------------------------------------------------------

    /**
     * Returns a formatted string representation of this session.
     * <p>
     * Includes session name, date/time, location, and attendance summary.
     *
     * @return a human-readable summary of the session
     */
    @Override
    public String toString() {
        String dateFormatted = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String attendanceSummary = String.format("(%d/%d present)",
                getAttendanceCount(), attendanceRecord.size());

        return String.format(
                "%s on %s%s %s",
                sessionName,
                dateFormatted, (location != null && !location.isEmpty()) ? " @ " + location : "",
                attendanceRecord.isEmpty() ? "" : attendanceSummary
        ).trim();
    }

    /**
     * Two sessions are considered equal if they have the same parent class,
     * session name, and scheduled date/time.
     *
     * @param other the object to compare
     * @return {@code true} if both sessions are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ClassSession)) {
            return false;
        }
        ClassSession that = (ClassSession) other;
        return parentClass.equals(that.parentClass)
                && sessionName.equals(that.sessionName)
                && dateTime.equals(that.dateTime);
    }

    /**
     * Generates a hash code based on parent class, session name, and date/time.
     *
     * @return the computed hash code
     */
    @Override
    public int hashCode() {
        return parentClass.hashCode() ^ sessionName.hashCode() ^ dateTime.hashCode();
    }
}
