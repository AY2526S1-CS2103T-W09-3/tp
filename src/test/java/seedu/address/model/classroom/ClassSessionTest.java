package seedu.address.model.classroom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import seedu.address.model.Attendance;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;
import seedu.address.model.tag.Tag;

/**
 * Unit tests for {@link ClassSession}.
 */
public class ClassSessionTest {

    private Tutor tutor;
    private TuitionClass parentClass;
    private Student alice;
    private Student bob;

    @BeforeEach
    void setUp() {
        Set<Tag> emptyTags = new HashSet<>();

        tutor = new Tutor(
                new Name("Mr Smith"),
                new Phone("91234567"),
                new Email("smith@example.com"),
                new Address("1 Tutor Lane"),
                emptyTags);

        alice = new Student(
                new Name("Alice Tan"),
                new Phone("98765432"),
                new Email("alice@example.com"),
                new Address("10 Kent Ridge Road"),
                emptyTags);

        bob = new Student(
                new Name("Bob Lim"),
                new Phone("92345678"),
                new Email("bob@example.com"),
                new Address("20 Clementi Ave"),
                emptyTags);

        parentClass = new TuitionClass(new ClassName("CS2103T T12"), tutor);
        parentClass.addStudent(alice);
        parentClass.addStudent(bob);
    }

    // --- Constructor validation ---

    @Test
    @DisplayName("Throws exception when sessionName is null")
    void constructor_nullSessionName_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new ClassSession(parentClass, null, LocalDateTime.now(), "COM1-B103"));
        assertTrue(ex.getMessage().contains("Session name cannot be null or empty"));
    }

    @Test
    @DisplayName("Throws exception when sessionName is empty")
    void constructor_emptySessionName_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new ClassSession(parentClass, "", LocalDateTime.now(), "COM1-B103"));
        assertTrue(ex.getMessage().contains("Session name cannot be null or empty"));
    }

    @Test
    @DisplayName("Throws exception when sessionName is whitespace only")
    void constructor_whitespaceSessionName_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new ClassSession(parentClass, "   ", LocalDateTime.now(), "COM1-B103"));
        assertTrue(ex.getMessage().contains("Session name cannot be null or empty"));
    }

    @Test
    @DisplayName("Throws exception when dateTime is null")
    void constructor_nullDateTime_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new ClassSession(parentClass, "Week 1 Tutorial", null, "COM1-B103"));
        assertTrue(ex.getMessage().contains("Date/time cannot be null"));
    }

    @Test
    @DisplayName("Throws exception when parentClass is null")
    void constructor_nullParent_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new ClassSession(null, "Week 1", LocalDateTime.now(), "COM1"));
    }

    @Test
    @DisplayName("Validation order: parentClass before sessionName")
    void constructor_validationOrder_parentClassFirst() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new ClassSession(null, null, LocalDateTime.now(), "COM1-B103"));
        assertTrue(ex.getMessage().contains("Parent class cannot be null"));
    }

    @Test
    @DisplayName("Validation order: sessionName before dateTime")
    void constructor_validationOrder_sessionNameFirst() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new ClassSession(parentClass, null, null, "COM1-B103"));
        assertTrue(ex.getMessage().contains("Session name"));
        assertFalse(ex.getMessage().contains("Date/time"));
    }

    @Test
    @DisplayName("Throws exception when duplicate session name exists")
    void constructor_validationOrder_duplicateCheckLast() {
        parentClass.addSession("Week 1 Tutorial", LocalDateTime.now(), "COM1");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new ClassSession(parentClass, "Week 1 Tutorial", LocalDateTime.now().plusDays(1), "COM2"));
        assertTrue(ex.getMessage().contains("session name already exists"));
    }

    @Test
    @DisplayName("Allows null location")
    void constructor_nullLocation_succeeds() {
        ClassSession session = new ClassSession(
                parentClass, "Week 1 Tutorial",
                LocalDateTime.of(2025, 10, 15, 14, 0), null);
        assertEquals("Week 1 Tutorial", session.getSessionName());
        assertNull(session.getLocation());
    }

    // --- Getters / Setters ---

    @Test
    @DisplayName("Getters and setters work correctly")
    void gettersSetters_workCorrectly() {
        LocalDateTime time = LocalDateTime.of(2025, 10, 15, 14, 0);
        ClassSession session = new ClassSession(parentClass, "Week 5 Tutorial", time, "COM1-B201");

        session.setSessionName("Updated Week 5 Tutorial");
        session.setDateTime(time.plusHours(2));
        session.setLocation("COM1-B202");

        assertEquals("Updated Week 5 Tutorial", session.getSessionName());
        assertEquals(time.plusHours(2), session.getDateTime());
        assertEquals("COM1-B202", session.getLocation());
        assertEquals(parentClass, session.getParentClass());
    }

    @Test
    @DisplayName("setDateTime allows null but causes NPE in toString")
    void setDateTime_nullValue_causesNpeInToString() {
        ClassSession session = new ClassSession(
                parentClass, "Week 1 Tutorial", LocalDateTime.now(), "COM1-B103");
        session.setDateTime(null);
        assertThrows(NullPointerException.class, session::toString);
    }

    // --- Attendance logic ---

    @Test
    @DisplayName("Initializes attendance for all enrolled students")
    void constructor_initializesAttendance() {
        ClassSession session = new ClassSession(
                parentClass, "Week 3 Tutorial",
                LocalDateTime.of(2025, 10, 13, 10, 0), "COM1-B103");

        Map<Student, Attendance> attendance = session.getAttendanceRecord();

        assertEquals(2, attendance.size());
        assertFalse(attendance.get(alice).isPresent());
        assertEquals(LocalDateTime.MIN, attendance.get(alice).getTimestamp());
        assertFalse(attendance.get(bob).isPresent());
        assertEquals(LocalDateTime.MIN, attendance.get(bob).getTimestamp());
    }

    @Test
    @DisplayName("Marking attendance updates correctly")
    void markAttendance_worksCorrectly() {
        ClassSession session = new ClassSession(
                parentClass, "Week 3 Tutorial", LocalDateTime.now(), "COM1-B103");

        session.markPresent(alice);
        session.markAbsent(bob);

        assertTrue(session.hasAttended(alice));
        assertFalse(session.hasAttended(bob));
        assertEquals(1, session.getAttendanceCount());
    }

    @Test
    @DisplayName("initializeAttendance preserves existing entries and adds new students")
    void initializeAttendance_preservesExistingEntries() {
        ClassSession session = new ClassSession(
                parentClass, "Week 3 Tutorial", LocalDateTime.now(), "COM1-B103");
        session.markPresent(alice);

        Student charlie = new Student(
                new Name("Charlie Goh"), new Phone("99887766"),
                new Email("charlie@example.com"), new Address("30 Science Drive"), new HashSet<>());
        parentClass.addStudent(charlie);

        session.initializeAttendance();

        Map<Student, Attendance> attendance = session.getAttendanceRecord();

        assertTrue(attendance.containsKey(charlie));
        assertFalse(attendance.get(charlie).isPresent()); // default false
        assertEquals(LocalDateTime.MIN, attendance.get(charlie).getTimestamp());
        assertTrue(attendance.get(alice).isPresent());
    }

    @Test
    @DisplayName("getAttendanceCount returns correct counts as attendance changes")
    void getAttendanceCount_multipleStudents_returnsCorrectCount() {
        ClassSession session = new ClassSession(
                parentClass, "Week 1 Tutorial",
                LocalDateTime.of(2024, 3, 15, 14, 30), "COM1-B103");

        assertEquals(0, session.getAttendanceCount());
        session.markPresent(alice);
        session.markPresent(bob);
        assertEquals(2, session.getAttendanceCount());
        session.markAbsent(alice);
        assertEquals(1, session.getAttendanceCount());
    }

    // --- Equality ---

    @Test
    @DisplayName("Sessions with same class, name, and time are equal")
    void equals_sameData_returnsTrue() {
        LocalDateTime time = LocalDateTime.of(2025, 10, 13, 10, 0);
        ClassSession s1 = new ClassSession(parentClass, "Week 3", time, "COM1");
        ClassSession s2 = new ClassSession(parentClass, "Week 3", time, "COM1");
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    @DisplayName("equals returns false for different attributes")
    void equals_differences_returnFalse() {
        ClassSession base = new ClassSession(parentClass, "Lesson X", LocalDateTime.now(), "COM1");
        TuitionClass another = new TuitionClass(new ClassName("CS2103T T13"), tutor);

        assertFalse(base.equals(null));
        assertFalse(base.equals("Not a session"));
        assertFalse(base.equals(new ClassSession(another, "Lesson X", base.getDateTime(), "COM1")));
        assertFalse(base.equals(new ClassSession(parentClass, "Lesson Y", base.getDateTime(), "COM1")));
        assertFalse(base.equals(new ClassSession(parentClass, "Lesson X", base.getDateTime().plusDays(1), "COM1")));
    }

    // --- String representations ---

    @Test
    @DisplayName("toString contains session details and attendance summary")
    void toString_containsSessionDetails() {
        ClassSession session = new ClassSession(
                parentClass, "Week 3 Tutorial",
                LocalDateTime.of(2025, 10, 13, 10, 0), "COM1-B103");

        String summary = session.toString();
        assertTrue(summary.contains("Week 3 Tutorial"));
        assertTrue(summary.contains("COM1-B103"));
        assertTrue(summary.contains("(0/2 present)"));
    }

    @Test
    @DisplayName("toString omits location when null")
    void toString_omitsLocation_whenNull() {
        ClassSession s = new ClassSession(parentClass, "Lesson 3", LocalDateTime.now(), null);
        s.initializeAttendance();
        String result = s.toString();
        assertFalse(result.contains("@"));
    }

    @Test
    @DisplayName("toString omits attendance summary when record empty")
    void toString_omitsAttendanceSummary_whenEmptyRecord() {
        ClassSession s = new ClassSession(parentClass, "Lesson 4", LocalDateTime.now(), "COM1");
        s.getAttendanceRecord().clear();
        String result = s.toString();
        assertFalse(result.contains("present"));
    }

    // --- TuitionClass session management ---

    @Test
    @DisplayName("addSession enforces name uniqueness and case-insensitivity")
    void addSession_duplicateSessionName_throwsException() {
        parentClass.addSession("Week 1 Tutorial", LocalDateTime.now(), "COM1");
        assertThrows(IllegalArgumentException.class, () ->
                parentClass.addSession("Week 1 Tutorial", LocalDateTime.now().plusDays(1), "COM1"));
        assertThrows(IllegalArgumentException.class, () ->
                parentClass.addSession("  Week 1 Tutorial  ", LocalDateTime.now().plusDays(1), "COM1"));
        assertThrows(IllegalArgumentException.class, () ->
                parentClass.addSession("week 1 tutorial", LocalDateTime.now().plusDays(1), "COM1"));
    }

    @Test
    @DisplayName("addSession allows distinct session names")
    void addSession_differentSessionNames_success() {
        parentClass.addSession("Week 1 Tutorial", LocalDateTime.now(), "COM1");
        parentClass.addSession("Week 2 Tutorial", LocalDateTime.now().plusDays(7), "COM1");
        assertEquals(2, parentClass.getAllSessions().size());
    }

    // --- Session detail display ---

    @Test
    @DisplayName("getSessionDetails displays session info and attendance")
    void getSessionDetails_returnsFormattedDetails() {
        ClassSession session = new ClassSession(
                parentClass, "Week 1 Tutorial",
                LocalDateTime.of(2024, 3, 15, 14, 30), "COM1-B103");

        session.markPresent(alice);
        String details = session.getSessionDetails();

        assertTrue(details.contains("Session: Week 1 Tutorial"));
        assertTrue(details.contains("Date/Time: 2024-03-15 14:30"));
        assertTrue(details.contains("Attendance: 1/2 present"));
    }

    @Test
    @DisplayName("getSessionDetails omits location when null")
    void getSessionDetails_omitsLocationWhenNull() {
        ClassSession session = new ClassSession(
                parentClass, "Week 1 Tutorial",
                LocalDateTime.of(2024, 3, 15, 14, 30), null);
        String details = session.getSessionDetails();
        assertFalse(details.contains("Location:"));
    }

    @Test
    @DisplayName("getSessionDetails shows None when no present/absent students")
    void getSessionDetails_showsNoneWhenNoStudentsMarked() {
        ClassSession session = new ClassSession(
                parentClass, "Week 1 Tutorial",
                LocalDateTime.of(2024, 3, 15, 14, 30), "COM1-B103");
        String details = session.getSessionDetails();
        assertTrue(details.contains("Present:" + System.lineSeparator() + "None"));
        assertTrue(details.contains("Absent:" + System.lineSeparator() + "- Alice Tan"));
    }

    @Test
    @DisplayName("getSessionDetails handles null student entries gracefully")
    void getSessionDetails_handlesNullStudent() {
        ClassSession session = new ClassSession(
                parentClass,
                "Week 1 Tutorial",
                LocalDateTime.of(2024, 3, 15, 14, 30),
                "COM1-B103");

        // Manually add a null entry to simulate edge case
        session.getAttendanceRecord().put(null, new Attendance(true, LocalDateTime.now()));

        String details = session.getSessionDetails();
        assertTrue(details.contains("Unknown student"));
    }
}
