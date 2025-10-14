package seedu.address.model.classes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    private Class parentClass;
    private Student alice;
    private Student bob;

    @BeforeEach
    void setUp() {
        // Reusable test data
        Set<Tag> emptyTags = new HashSet<>();

        tutor = new Tutor(
                new Name("Mr Smith"),
                new Phone("91234567"),
                new Email("smith@example.com"),
                new Address("1 Tutor Lane"),
                emptyTags
        );

        alice = new Student(
                new Name("Alice Tan"),
                new Phone("98765432"),
                new Email("alice@example.com"),
                new Address("10 Kent Ridge Road"),
                emptyTags
        );

        bob = new Student(
                new Name("Bob Lim"),
                new Phone("92345678"),
                new Email("bob@example.com"),
                new Address("20 Clementi Ave"),
                emptyTags
        );

        parentClass = new Class(tutor, "CS2103T T12");
        parentClass.addStudent(alice);
        parentClass.addStudent(bob);
    }

    @Test
    @DisplayName("Constructor initializes attendance for all enrolled students")
    void constructor_initializesAttendance() {
        ClassSession session = new ClassSession(
                parentClass,
                "Week 3 Tutorial",
                LocalDateTime.of(2025, 10, 13, 10, 0),
                "COM1-B103"
        );

        Map<Student, Boolean> attendance = session.getAttendanceRecord();

        assertEquals(2, attendance.size());
        assertFalse(attendance.get(alice));
        assertFalse(attendance.get(bob));
    }

    @Test
    @DisplayName("Marking attendance updates status correctly")
    void markAttendance_worksCorrectly() {
        ClassSession session = new ClassSession(
                parentClass,
                "Week 3 Tutorial",
                LocalDateTime.now(),
                "COM1-B103"
        );

        session.markPresent(alice);
        session.markAbsent(bob);

        assertTrue(session.hasAttended(alice));
        assertFalse(session.hasAttended(bob));
        assertEquals(1, session.getAttendanceCount());
    }

    @Test
    @DisplayName("initializeAttendance adds new students but preserves existing data")
    void initializeAttendance_preservesExistingEntries() {
        ClassSession session = new ClassSession(
                parentClass,
                "Week 3 Tutorial",
                LocalDateTime.now(),
                "COM1-B103"
        );

        session.markPresent(alice);

        // Add a new student and reinitialize
        Student charlie = new Student(
                new Name("Charlie Goh"),
                new Phone("99887766"),
                new Email("charlie@example.com"),
                new Address("30 Science Drive"),
                new HashSet<>()
        );

        parentClass.addStudent(charlie);
        session.initializeAttendance();

        Map<Student, Boolean> attendance = session.getAttendanceRecord();

        assertTrue(attendance.containsKey(charlie));
        assertFalse(attendance.get(charlie)); // default false
        assertTrue(attendance.get(alice));
    }

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
    @DisplayName("Sessions with different times are not equal")
    void equals_differentTime_returnsFalse() {
        ClassSession s1 = new ClassSession(parentClass, "Week 3", LocalDateTime.now(), "COM1");
        ClassSession s2 = new ClassSession(parentClass, "Week 3", LocalDateTime.now().plusDays(1), "COM1");

        assertNotEquals(s1, s2);
    }

    @Test
    @DisplayName("toString() produces human-readable summary")
    void toString_containsSessionDetails() {
        ClassSession session = new ClassSession(
                parentClass,
                "Week 3 Tutorial",
                LocalDateTime.of(2025, 10, 13, 10, 0),
                "COM1-B103"
        );

        String summary = session.toString();

        assertTrue(summary.contains("Week 3 Tutorial"));
        assertTrue(summary.contains("COM1-B103"));
        assertTrue(summary.contains("(0/2 present)"));
    }

    @Test
    @DisplayName("Throws exception when constructed with null parent class")
    void constructor_nullParent_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new ClassSession(null, "Week 1", LocalDateTime.now(), "COM1")
        );
    }
}
