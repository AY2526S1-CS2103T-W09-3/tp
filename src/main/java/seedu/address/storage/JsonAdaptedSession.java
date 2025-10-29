package seedu.address.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.Attendance;
import seedu.address.model.classroom.ClassSession;
import seedu.address.model.person.Student;

/**
 * Jackson-friendly version of {@link ClassSession}.
 */
public class JsonAdaptedSession {
    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Session's %s field is missing!";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final String sessionName;
    private final String dateTime;
    private final String location;
    private final List<List<String>> presentStudents;
    private final List<List<String>> absentStudents;

    /**
     * Constructs a {@code JsonAdaptedSession} with the given session details.
     */
    @JsonCreator
    public JsonAdaptedSession(@JsonProperty("sessionName") String sessionName,
            @JsonProperty("dateTime") String dateTime,
            @JsonProperty("location") String location,
            @JsonProperty("presentStudents") List<List<String>> presentStudents,
            @JsonProperty("absentStudents") List<List<String>> absentStudents) {
        this.sessionName = sessionName;
        this.dateTime = dateTime;
        this.location = location;
        this.presentStudents = presentStudents != null ? presentStudents : new ArrayList<>();
        this.absentStudents = absentStudents != null ? absentStudents : new ArrayList<>();
    }

    /**
     * Converts a given {@code ClassSession} into this class for Jackson use.
     */
    public JsonAdaptedSession(ClassSession source) {
        sessionName = source.getSessionName();
        dateTime = source.getDateTime().format(FORMATTER);
        location = source.getLocation();

        presentStudents = new ArrayList<>();
        absentStudents = new ArrayList<>();

        // Convert attendance record to present/absent lists
        Map<Student, Attendance> attendanceRecord = source.getAttendanceRecord();
        for (Map.Entry<Student, Attendance> entry : attendanceRecord.entrySet()) {
            Student student = entry.getKey();
            Boolean isPresent = entry.getValue().isPresent();
            LocalDateTime timestamp = entry.getValue().getTimestamp();
            String studentName = student.getName().fullName;
            String timestampStr = timestamp != null ? timestamp.format(FORMATTER)
                    : LocalDateTime.MIN.format(FORMATTER);

            if (isPresent != null && isPresent) {
                presentStudents.add(new ArrayList<>(List.of(studentName, timestampStr)));
            } else {
                absentStudents.add(new ArrayList<>(List.of(studentName, timestampStr)));
            }
        }
    }

    /**
     * Returns the session name for linking purposes.
     */
    public String getSessionName() {
        return sessionName;
    }

    /**
     * Returns the date time string.
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * Returns the location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the list of present students.
     */
    public List<List<String>> getPresentStudents() {
        return presentStudents;
    }

    /**
     * Returns the list of absent students.
     */
    public List<List<String>> getAbsentStudents() {
        return absentStudents;
    }

    /**
     * Converts this Jackson-friendly adapted session object into the model's {@code ClassSession} object.
     * Note: This requires the parent TuitionClass to construct the session properly.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted session.
     */
    public LocalDateTime toModelDateTime() throws IllegalValueException {
        if (dateTime == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "dateTime"));
        }

        try {
            return LocalDateTime.parse(dateTime, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalValueException("Invalid date time format: " + dateTime);
        }
    }

    /**
     * Validates that all required fields are present.
     */
    public void validate() throws IllegalValueException {
        if (sessionName == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "sessionName"));
        }
        toModelDateTime(); // Validates dateTime
    }
}
