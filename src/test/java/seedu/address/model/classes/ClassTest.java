package seedu.address.model.classes;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Email;
import seedu.address.model.person.Address;
import seedu.address.model.tag.Tag;

/**
 * Unit tests for {@link Class}.
 */
public class ClassTest {

    private Tutor tutor;
    private Class tutorialClass;
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
                emptyTags
        );

        tutorialClass = new Class(tutor, "CS2103T T12");

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
    }

    @Test
    @DisplayName("Add and remove students from class")
    void addRemoveStudents() {
        tutorialClass.addStudent(alice);
        tutorialClass.addStudent(bob);

        assertTrue(tutorialClass.getStudents().contains(alice));
        assertTrue(tutorialClass.getStudents().contains(bob));
        assertEquals(2, tutorialClass.getStudents().size());

        tutorialClass.removeStudent(alice);
        assertFalse(tutorialClass.getStudents().contains(alice));
        assertEquals(1, tutorialClass.getStudents().size());
    }

    @Test
    @DisplayName("Add and remove sessions from class")
    void addRemoveSessions() {
        tutorialClass.addStudent(alice);
        tutorialClass.addStudent(bob);

        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);

        ClassSession session1 = tutorialClass.addSession("Future Session", futureTime, "COM1-B103");
        ClassSession session2 = tutorialClass.addSession("Past Session", pastTime, "COM1-B104");

        assertEquals(2, tutorialClass.getAllSessions().size());
        assertTrue(tutorialClass.getAllSessions().contains(session1));
        assertTrue(tutorialClass.getAllSessions().contains(session2));

        tutorialClass.removeSession(session1);
        assertEquals(1, tutorialClass.getAllSessions().size());
        assertFalse(tutorialClass.getAllSessions().contains(session1));
        assertTrue(tutorialClass.getAllSessions().contains(session2));
    }

    @Test
    @DisplayName("Retrieve future and past sessions correctly")
    void getFutureAndPastSessions() {
        tutorialClass.addStudent(alice);
        tutorialClass.addStudent(bob);

        LocalDateTime futureTime1 = LocalDateTime.now().plusDays(2);
        LocalDateTime futureTime2 = LocalDateTime.now().plusHours(3);
        LocalDateTime pastTime1 = LocalDateTime.now().minusDays(1);

        ClassSession futureSession1 = tutorialClass.addSession("Future 1", futureTime1, "COM1-B101");
        ClassSession futureSession2 = tutorialClass.addSession("Future 2", futureTime2, "COM1-B102");
        ClassSession pastSession = tutorialClass.addSession("Past 1", pastTime1, "COM1-B103");

        assertTrue(tutorialClass.getFutureSessions().contains(futureSession1));
        assertTrue(tutorialClass.getFutureSessions().contains(futureSession2));
        assertFalse(tutorialClass.getFutureSessions().contains(pastSession));

        assertTrue(tutorialClass.getPastSessions().contains(pastSession));
        assertFalse(tutorialClass.getPastSessions().contains(futureSession1));
        assertFalse(tutorialClass.getPastSessions().contains(futureSession2));
    }

    @Test
    @DisplayName("Get and set tutor")
    void getSetTutor() {
        assertEquals(tutor, tutorialClass.getTutor());

        Tutor newTutor = new Tutor(
                new Name("Ms Lee"),
                new Phone("91239876"),
                new Email("lee@example.com"),
                new Address("5 Learning Street"),
                new HashSet<>()
        );

        tutorialClass.setTutor(newTutor);
        assertEquals(newTutor, tutorialClass.getTutor());
    }

    @Test
    @DisplayName("Get class name and students")
    void getClassNameAndStudents() {
        tutorialClass.addStudent(alice);
        tutorialClass.addStudent(bob);

        assertEquals("CS2103T T12", tutorialClass.getClassName());
        assertTrue(tutorialClass.getStudents().contains(alice));
        assertTrue(tutorialClass.getStudents().contains(bob));
    }
}
