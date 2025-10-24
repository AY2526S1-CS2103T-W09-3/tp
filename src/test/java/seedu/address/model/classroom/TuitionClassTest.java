package seedu.address.model.classroom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashSet;
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
 * Unit tests for {@link TuitionClass}.
 */
public class TuitionClassTest {

    private Tutor tutor;
    private TuitionClass tuitionClass;
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

        tuitionClass = new TuitionClass(new ClassName("CS2103T T12"), tutor);

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
    }

    @Test
    @DisplayName("Add and remove students from class")
    void addRemoveStudents() {
        tuitionClass.addStudent(alice);
        tuitionClass.addStudent(bob);

        assertTrue(tuitionClass.getStudents().contains(alice));
        assertTrue(tuitionClass.getStudents().contains(bob));
        assertEquals(2, tuitionClass.getStudents().size());

        tuitionClass.removeStudent(alice);
        assertFalse(tuitionClass.getStudents().contains(alice));
        assertEquals(1, tuitionClass.getStudents().size());
    }

    @Test
    @DisplayName("Add and remove sessions from class")
    void addRemoveSessions() {
        tuitionClass.addStudent(alice);
        tuitionClass.addStudent(bob);

        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);

        ClassSession session1 = tuitionClass.addSession("Future Session", futureTime, "COM1-B103");
        ClassSession session2 = tuitionClass.addSession("Past Session", pastTime, "COM1-B104");

        assertEquals(2, tuitionClass.getAllSessions().size());
        assertTrue(tuitionClass.getAllSessions().contains(session1));
        assertTrue(tuitionClass.getAllSessions().contains(session2));

        tuitionClass.removeSession(session1);
        assertEquals(1, tuitionClass.getAllSessions().size());
        assertFalse(tuitionClass.getAllSessions().contains(session1));
        assertTrue(tuitionClass.getAllSessions().contains(session2));
    }

    @Test
    @DisplayName("Retrieve future and past sessions correctly")
    void getFutureAndPastSessions() {
        tuitionClass.addStudent(alice);
        tuitionClass.addStudent(bob);

        LocalDateTime futureTime1 = LocalDateTime.now().plusDays(2);
        LocalDateTime futureTime2 = LocalDateTime.now().plusHours(3);
        LocalDateTime pastTime1 = LocalDateTime.now().minusDays(1);

        ClassSession futureSession1 = tuitionClass.addSession("Future 1", futureTime1, "COM1-B101");
        ClassSession futureSession2 = tuitionClass.addSession("Future 2", futureTime2, "COM1-B102");
        ClassSession pastSession = tuitionClass.addSession("Past 1", pastTime1, "COM1-B103");

        assertTrue(tuitionClass.getFutureSessions().contains(futureSession1));
        assertTrue(tuitionClass.getFutureSessions().contains(futureSession2));
        assertFalse(tuitionClass.getFutureSessions().contains(pastSession));

        assertTrue(tuitionClass.getPastSessions().contains(pastSession));
        assertFalse(tuitionClass.getPastSessions().contains(futureSession1));
        assertFalse(tuitionClass.getPastSessions().contains(futureSession2));
    }

    @Test
    @DisplayName("hasStudent returns true only for enrolled students")
    void hasStudentBehavior() {
        // initially not enrolled
        assertFalse(tuitionClass.hasStudent(alice));

        tuitionClass.addStudent(alice);
        assertTrue(tuitionClass.hasStudent(alice));
    }

    @Test
    @DisplayName("hasTutor returns true when tutor is assigned and false otherwise")
    void hasTutorBehavior() {
        // tutor assigned in setUp
        assertTrue(tuitionClass.hasTutor(tutor));

        Tutor other = new Tutor(new Name("Other Tutor"), new Phone("90000000"), new Email("o@example.com"),
                new Address("Somewhere"), new HashSet<>());
        assertFalse(tuitionClass.hasTutor(other));
    }

    @Test
    @DisplayName("setTutor updates tutor successfully")
    void setTutor_updatesTutor() {
        Set<Tag> emptyTags = new HashSet<>();
        Tutor newTutor = new Tutor(
                new Name("Mr Brown"),
                new Phone("99887766"),
                new Email("brown@example.com"),
                new Address("123 New Street"),
                emptyTags
        );

        tuitionClass.setTutor(newTutor);
        assertEquals(newTutor, tuitionClass.getTutor());
    }

    @Test
    @DisplayName("getTutor returns correct tutor")
    void getTutor_returnsCorrectTutor() {
        assertEquals(tutor, tuitionClass.getTutor());
    }

    @Test
    @DisplayName("getName returns correct ClassName object")
    void getName_returnsCorrectClassName() {
        assertEquals(new ClassName("CS2103T T12"), tuitionClass.getName());
    }

    @Test
    @DisplayName("getClassName returns correct string value")
    void getClassName_returnsCorrectString() {
        assertEquals("CS2103T T12", tuitionClass.getClassName());
    }

    @Test
    @DisplayName("isSameClass returns true for same class name")
    void isSameClass_sameClassName_returnsTrue() {
        TuitionClass otherClass = new TuitionClass(new ClassName("CS2103T T12"));
        assertTrue(tuitionClass.isSameClass(otherClass));
    }

    @Test
    @DisplayName("isSameClass returns false for different class name")
    void isSameClass_differentClassName_returnsFalse() {
        TuitionClass otherClass = new TuitionClass(new ClassName("CS2103T T13"));
        assertFalse(tuitionClass.isSameClass(otherClass));
    }

    @Test
    @DisplayName("isSameClass returns false for null")
    void isSameClass_null_returnsFalse() {
        assertFalse(tuitionClass.isSameClass(null));
    }

    @Test
    @DisplayName("equals returns true for same object")
    void equals_sameObject_returnsTrue() {
        assertTrue(tuitionClass.equals(tuitionClass));
    }

    @Test
    @DisplayName("equals returns false for null")
    void equals_null_returnsFalse() {
        assertFalse(tuitionClass.equals(null));
    }

    @Test
    @DisplayName("equals returns false for different type")
    void equals_differentType_returnsFalse() {
        assertFalse(tuitionClass.equals("not a class"));
    }

    @Test
    @DisplayName("equals returns true for same class name")
    void equals_sameClassName_returnsTrue() {
        TuitionClass otherClass = new TuitionClass(new ClassName("CS2103T T12"));
        assertTrue(tuitionClass.equals(otherClass));
    }

    @Test
    @DisplayName("equals returns false for different class name")
    void equals_differentClassName_returnsFalse() {
        TuitionClass otherClass = new TuitionClass(new ClassName("CS2103T T13"));
        assertFalse(tuitionClass.equals(otherClass));
    }

    @Test
    @DisplayName("hashCode is consistent")
    void hashCode_isConsistent() {
        int hash1 = tuitionClass.hashCode();
        int hash2 = tuitionClass.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("hashCode is same for equal objects")
    void hashCode_equalObjects_sameHashCode() {
        TuitionClass otherClass = new TuitionClass(new ClassName("CS2103T T12"));
        assertEquals(tuitionClass.hashCode(), otherClass.hashCode());
    }

    @Test
    @DisplayName("toString contains class information")
    void toString_containsClassInfo() {
        String result = tuitionClass.toString();
        assertTrue(result.contains("CS2103T T12"));
        assertTrue(result.contains("Mr Smith"));
    }

    @Test
    @DisplayName("toString shows Unassigned for null tutor")
    void toString_nullTutor_showsUnassigned() {
        TuitionClass classWithoutTutor = new TuitionClass(new ClassName("Math101"));
        String result = classWithoutTutor.toString();
        assertTrue(result.contains("Unassigned"));
    }

    @Test
    @DisplayName("Constructor without tutor initializes correctly")
    void constructor_withoutTutor_initializesCorrectly() {
        TuitionClass classWithoutTutor = new TuitionClass(new ClassName("Math101"));
        assertEquals("Math101", classWithoutTutor.getClassName());
        assertEquals(null, classWithoutTutor.getTutor());
        assertTrue(classWithoutTutor.getStudents().isEmpty());
        assertTrue(classWithoutTutor.getAllSessions().isEmpty());
    }

    @Test
    @DisplayName("hasSessionName returns true when session exists")
    void hasSessionName_sessionExists_returnsTrue() {
        tuitionClass.addStudent(alice);
        tuitionClass.addSession("Week 1 Tutorial", LocalDateTime.now(), "COM1-B103");

        assertTrue(tuitionClass.hasSessionName("Week 1 Tutorial"));
    }

    @Test
    @DisplayName("hasSessionName returns false when session does not exist")
    void hasSessionName_sessionDoesNotExist_returnsFalse() {
        tuitionClass.addStudent(alice);
        tuitionClass.addSession("Week 1 Tutorial", LocalDateTime.now(), "COM1-B103");

        assertFalse(tuitionClass.hasSessionName("Week 2 Tutorial"));
    }

    @Test
    @DisplayName("hasSessionName handles whitespace correctly")
    void hasSessionName_withWhitespace_handlesCorrectly() {
        tuitionClass.addStudent(alice);
        tuitionClass.addSession("Week 1 Tutorial", LocalDateTime.now(), "COM1-B103");

        // Should match even with extra whitespace
        assertTrue(tuitionClass.hasSessionName("  Week 1 Tutorial  "));
        assertTrue(tuitionClass.hasSessionName(" Week 1 Tutorial"));
        assertTrue(tuitionClass.hasSessionName("Week 1 Tutorial "));
    }

    @Test
    @DisplayName("hasSessionName returns false for empty list")
    void hasSessionName_emptySessionList_returnsFalse() {
        assertFalse(tuitionClass.hasSessionName("Week 1 Tutorial"));
    }

    @Test
    @DisplayName("hasSessionName is case insensitive")
    void hasSessionName_caseInsensitive() {
        tuitionClass.addStudent(alice);
        tuitionClass.addSession("Week 1 Tutorial", LocalDateTime.now(), "COM1-B103");

        assertTrue(tuitionClass.hasSessionName("week 1 tutorial"));
        assertTrue(tuitionClass.hasSessionName("WEEK 1 TUTORIAL"));
        assertTrue(tuitionClass.hasSessionName("WeeK 1 TuToRiAl"));
    }

    @Test
    @DisplayName("hasSessionName works with multiple sessions")
    void hasSessionName_multipleSessions_worksCorrectly() {
        tuitionClass.addStudent(alice);
        tuitionClass.addSession("Week 1 Tutorial", LocalDateTime.now(), "COM1-B103");
        tuitionClass.addSession("Week 2 Tutorial", LocalDateTime.now().plusDays(7), "COM1-B103");
        tuitionClass.addSession("Week 3 Tutorial", LocalDateTime.now().plusDays(14), "COM1-B103");

        assertTrue(tuitionClass.hasSessionName("Week 1 Tutorial"));
        assertTrue(tuitionClass.hasSessionName("Week 2 Tutorial"));
        assertTrue(tuitionClass.hasSessionName("Week 3 Tutorial"));
        assertFalse(tuitionClass.hasSessionName("Week 4 Tutorial"));
    }
}
