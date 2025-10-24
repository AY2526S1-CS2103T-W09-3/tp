package seedu.address.model.classroom;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import seedu.address.model.person.Student;

/**
 * Represents a single teaching session under a {@link TuitionClass}.
 * Tracks attendance for enrolled students.
 */
public class ClassSession {

    /** The class this session belongs to. */
    private final TuitionClass parentClass;

    /** Descriptive name for the session (e.g., "Week 3 Tutorial"). */
    private String sessionName;

    /** Scheduled date and time of the session. */
    private LocalDateTime dateTime;

    /** Optional location (e.g., "COM1-B103"). */
    private String location;

    /** Attendance record: true = present, false = absent. */
    private Map<Student, Boolean> attendanceRecord;

    /**
     * Constructs a {@code ClassSession}. Attendance is initialized for all current students in the parent class.
     *
     * @param parentClass parent class
     * @param sessionName session title
     * @param dateTime scheduled time
     * @param location location (nullable)
     */
    public ClassSession(TuitionClass parentClass, String sessionName, LocalDateTime dateTime, String location) {
        if (parentClass == null) {
            throw new IllegalArgumentException("Parent class cannot be null for session: " + sessionName);
        }
        if (parentClass.hasSessionName(sessionName)) {
            throw new IllegalArgumentException("This session name already exists for this class: " + sessionName);
        }
        this.parentClass = parentClass;
        this.sessionName = sessionName;
        this.dateTime = dateTime;
        this.location = location;
        this.attendanceRecord = new HashMap<>();
        initializeAttendance();
    }

    // Getters / setters

    public TuitionClass getParentClass() {
        return parentClass;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Attendance

    /** Initializes attendance records for all students (default: absent). */
    public void initializeAttendance() {
        for (Student s : parentClass.getStudents()) {
            attendanceRecord.putIfAbsent(s, false);
        }
    }

    public void markPresent(Student student) {
        attendanceRecord.put(student, true);
    }

    public void markAbsent(Student student) {
        attendanceRecord.put(student, false);
    }

    public boolean hasAttended(Student student) {
        return attendanceRecord.getOrDefault(student, false);
    }

    public Map<Student, Boolean> getAttendanceRecord() {
        return attendanceRecord;
    }

    public long getAttendanceCount() {
        return attendanceRecord.values().stream().filter(Boolean::booleanValue).count();
    }

    @Override
    public String toString() {
        String dateFormatted = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String attendanceSummary = String.format("(%d/%d present)", getAttendanceCount(), attendanceRecord.size());
        String loc = (location != null && !location.isEmpty()) ? " @ " + location : "";
        String tail = attendanceRecord.isEmpty() ? "" : " " + attendanceSummary;
        return (sessionName + " on " + dateFormatted + loc + tail).trim();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
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

    @Override
    public int hashCode() {
        return parentClass.hashCode() ^ sessionName.hashCode() ^ dateTime.hashCode();
    }
}
