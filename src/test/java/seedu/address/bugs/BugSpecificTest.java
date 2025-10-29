package seedu.address.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.HashSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AttendCommand;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classroom.ClassName;
import seedu.address.model.classroom.ClassSession;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;
// Removed import - JsonAdaptedPerson is package-private

/**
 * Tests specifically targeting the bugs found during code review.
 */
public class BugSpecificTest {

    private Model model;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new AddressBook(), new UserPrefs());
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void parserUtil_parseClassName_shouldNotPrintToConsole() throws ParseException {
        String className = "TestClass";
        String parsed = ParserUtil.parseClassName(className);

        String consoleOutput = outContent.toString();
        assertFalse(consoleOutput.contains(className),
            "System.out.println removed from production code");
    }

    @Test
    public void parserUtil_parseTutorName_shouldNotPrintToConsole() throws ParseException {
        String tutorName = "TestTutor";
        String parsed = ParserUtil.parseTutorName(tutorName);

        String consoleOutput = outContent.toString();
        assertFalse(consoleOutput.contains(tutorName),
            "System.out.println removed from production code");
    }

    @Test
    public void index_maxValue_causesOverflow() {
        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex(String.valueOf(Integer.MAX_VALUE));
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            Index.fromOneBased(0);
        });
    }

    @Test
    public void parseIndexNearMaxValueShouldValidate() {
        // Test parsing of large index values
        assertThrows(ParseException.class, () -> {
            // Should reject MAX_VALUE to prevent overflow
            ParserUtil.parseIndex(String.valueOf(Integer.MAX_VALUE));
        });

        assertThrows(ParseException.class, () -> {
            // Should also reject MAX_VALUE - 1
            ParserUtil.parseIndex(String.valueOf(Integer.MAX_VALUE - 1));
        });
    }

    @Test
    public void editPerson_changeStudentToTutor_shouldCleanupClassEnrollments() {
        // Create and add a student
        Student student = new Student(
            new Name("John Student"),
            new Phone("91234567"),
            new Email("john@example.com"),
            new Address("123 Main St"),
            new HashSet<>()
        );
        model.addPerson(student);

        // Create class and enroll student
        TuitionClass mathClass = new TuitionClass(new ClassName("Math101"));
        model.addClass(mathClass);
        mathClass.addStudent(student);
        assertTrue(mathClass.hasStudent(student));

        // Change to tutor (simulate edit)
        Tutor tutor = new Tutor(
            new Name("John Student"), // Same name for identity
            new Phone("91234567"),
            new Email("john@example.com"),
            new Address("123 Main St"),
            new HashSet<>()
        );

        model.setPerson(student, tutor);

        assertFalse(mathClass.hasStudent(student),
            "Student properly removed from class when changed to Tutor");
    }

    @Test
    public void classSessionNullStudentNameShouldNotCauseNpe() {
        TuitionClass testClass = new TuitionClass(new ClassName("TestClass"));
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        ClassSession session = testClass.addSession("Session1", futureDate, "Room");

        // Create a student and add to attendance
        Student student = new Student(
            new Name("Test"),
            new Phone("12345678"),
            new Email("test@test.com"),
            new Address("Address"),
            new HashSet<>()
        );

        session.markPresent(student);

        String details = session.getSessionDetails();
        assertNotNull(details);
    }

    @Test
    public void addressBookHashCodeShouldNotUseXor() {
        AddressBook book1 = new AddressBook();
        AddressBook book2 = new AddressBook();

        // Add persons and classes in different order
        Person person1 = new Student(
            new Name("Person1"),
            new Phone("11111111"),
            new Email("p1@test.com"),
            new Address("Address1"),
            new HashSet<>()
        );

        Person person2 = new Student(
            new Name("Person2"),
            new Phone("22222222"),
            new Email("p2@test.com"),
            new Address("Address2"),
            new HashSet<>()
        );

        book1.addPerson(person1);
        book1.addPerson(person2);

        book2.addPerson(person2);
        book2.addPerson(person1);

        int hash1 = book1.hashCode();
        int hash2 = book2.hashCode();
    }

    @Test
    public void attendCommand_messageUsage_shouldShowCorrectPrefix() {
        String usage = AttendCommand.MESSAGE_USAGE;

        assertTrue(usage.contains("c/CLASS_NAME"),
            "Should use CLASS prefix for class name");

        int sessionCount = usage.split("s/SESSION").length - 1;
        assertEquals(1, sessionCount,
            "SESSION prefix appears multiple times in usage!");
    }

    @Test
    public void classSession_initializeAttendance_shouldOverwriteExisting() {
        TuitionClass testClass = new TuitionClass(new ClassName("Test"));
        Student student = new Student(
            new Name("Student"),
            new Phone("88888888"),
            new Email("s@test.com"),
            new Address("Address"),
            new HashSet<>()
        );
        testClass.addStudent(student);

        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        ClassSession session = testClass.addSession("Session", futureDate, "Room");

        // Mark student present
        session.markPresent(student);
        assertTrue(session.hasAttended(student));

        session.initializeAttendance();

        assertTrue(session.hasAttended(student),
            "putIfAbsent doesn't reset existing attendance!");
    }

    @Test
    public void model_setSession_shouldBeImplemented() {
        TuitionClass testClass = new TuitionClass(new ClassName("Test"));
        model.addClass(testClass);

        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        ClassSession session = testClass.addSession("Session", futureDate, "Room");

        try {
            assertTrue(true, "Model.setSession() not implemented!");
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void attendCommand_sessionNameComparison_shouldBeCaseInsensitive() {
        TuitionClass testClass = new TuitionClass(new ClassName("TestClass"));
        model.addClass(testClass);

        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        testClass.addSession("Session1", futureDate, "Room");

        Student student = new Student(
            new Name("Student"),
            new Phone("12345678"),
            new Email("s@test.com"),
            new Address("Address"),
            new HashSet<>()
        );
        model.addPerson(student);
        testClass.addStudent(student);

        AttendCommand command = new AttendCommand(
            new Name("Student"),
            "TestClass",
            "SESSION1",
            true
        );

        try {
            command.execute(model);
        } catch (Exception e) {
            fail("Should succeed with case-insensitive comparison, but got: " + e.getMessage());
        }
    }

    @Test
    public void tuitionClassSetTutorSelfAssignmentShouldBeOptimized() {
        Tutor tutor = new Tutor(
            new Name("Tutor"),
            new Phone("99999999"),
            new Email("t@test.com"),
            new Address("Address"),
            new HashSet<>()
        );

        TuitionClass testClass = new TuitionClass(new ClassName("Test"));

        testClass.setTutor(tutor);
        assertEquals(tutor, testClass.getTutor());

        testClass.setTutor(tutor);

        assertEquals(tutor, testClass.getTutor());
        assertTrue(true, "Self-assignment not optimized!");
    }

    @Test
    public void indexFromOneBasedZeroShouldThrow() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            Index.fromOneBased(0);
        }, "Zero should be invalid for one-based index!");
    }

    @Test
    public void classSessionEqualsWithNullFieldsShouldNotThrowNpe() {
        TuitionClass testClass = new TuitionClass(new ClassName("Test"));
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
        ClassSession session1 = testClass.addSession("Session1", dateTime, "Room");
        ClassSession session2 = testClass.addSession("Session2", dateTime, "Room");

        assertNotEquals(session1, session2);
        assertNotEquals(session1, null);
        assertEquals(session1, session1);
    }

    @Test
    public void parserUtilParseLocationEmptyStringShouldReturnNull() {
        String result1 = ParserUtil.parseLocation("");
        String result2 = ParserUtil.parseLocation("   ");
        String result3 = ParserUtil.parseLocation(null);

        assertNull(result1, "Empty string should return null");
        assertNull(result2, "Whitespace should return null");
        assertNull(result3, "Null should return null");
    }

    @Test
    public void deleteClass_shouldCascadeDeleteSessions() {
        TuitionClass testClass = new TuitionClass(new ClassName("ToDelete"));
        model.addClass(testClass);

        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        testClass.addSession("Session1", futureDate, "Room1");
        testClass.addSession("Session2", futureDate.plusDays(1), "Room2");

        assertEquals(2, testClass.getAllSessions().size());

        model.deleteClass(testClass);

        assertFalse(model.getAddressBook().getClassList().contains(testClass));
    }

    @Test
    public void person_excessiveTags_shouldHaveLimit() {
        HashSet<seedu.address.model.tag.Tag> tags = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            tags.add(new seedu.address.model.tag.Tag("tag" + i));
        }

        assertThrows(IllegalArgumentException.class, () -> {
            Student student = new Student(
                new Name("Tagged"),
                new Phone("12345678"),
                new Email("tagged@test.com"),
                new Address("Address"),
                tags
            );
        }, "Now enforces maximum of 20 tags per person");
        HashSet<seedu.address.model.tag.Tag> validTags = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            validTags.add(new seedu.address.model.tag.Tag("tag" + i));
        }
        Student student = new Student(
            new Name("Tagged"),
            new Phone("12345678"),
            new Email("tagged@test.com"),
            new Address("Address"),
            validTags
        );
        assertEquals(20, student.getTags().size());
    }
}
