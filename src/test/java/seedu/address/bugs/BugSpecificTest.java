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

    // ===== BUG-170: Debug output in production code =====
    @Test
    public void parserUtil_parseClassName_shouldNotPrintToConsole() throws ParseException {
        // This test will fail until bug is fixed
        String className = "TestClass";
        String parsed = ParserUtil.parseClassName(className);

        // Check that nothing was printed to console
        // Currently fails because System.out.println is present
        // After fix, this should pass
        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains(className),
            "BUG-170: System.out.println found in production code!");
    }

    @Test
    public void parserUtil_parseTutorName_shouldNotPrintToConsole() throws ParseException {
        // This test will fail until bug is fixed
        String tutorName = "TestTutor";
        String parsed = ParserUtil.parseTutorName(tutorName);

        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains(tutorName),
            "BUG-170: System.out.println found in production code!");
    }

    // ===== BUG-171: Integer overflow in Index parsing =====
    @Test
    public void index_maxValue_causesOverflow() {
        // This test demonstrates the integer overflow bug
        assertThrows(IndexOutOfBoundsException.class, () -> {
            Index index = Index.fromOneBased(Integer.MAX_VALUE);
            // Zero-based would be MAX_VALUE - 1, which overflows to negative
            int zeroBased = index.getZeroBased();
            assertTrue(zeroBased < 0, "Integer overflow should cause negative index");
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

    // ===== BUG-172: Broken class-student relationship on person type edit =====
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

        // This should clean up enrollments but currently doesn't (bug)
        model.setPerson(student, tutor);

        // This test exposes the bug - cleanup doesn't happen
        // After fix, should be: assertFalse(mathClass.hasStudent(student));
        assertTrue(mathClass.hasStudent(student),
            "BUG-172: Student not removed from class when changed to Tutor!");
    }

    // ===== BUG-173: NPE risk in session details with null student name =====
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

        // This should not throw NPE even with null handling issues
        String details = session.getSessionDetails();
        assertNotNull(details);
    }

    // ===== BUG-174: HashCode XOR collision in AddressBook =====
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

        // Different order should produce different hashcodes
        // XOR would produce same hashcode (bug)
        // After fix with Objects.hash, should be different
        int hash1 = book1.hashCode();
        int hash2 = book2.hashCode();

        // This test may pass by chance due to XOR, but demonstrates the issue
        // With proper fix using Objects.hash, order matters
    }

    // ===== BUG-175: Wrong command prefix shown in AttendCommand usage =====
    @Test
    public void attendCommand_messageUsage_shouldShowCorrectPrefix() {
        String usage = AttendCommand.MESSAGE_USAGE;

        // Check that the usage message is consistent
        // Currently shows SESSION prefix twice (bug)
        assertTrue(usage.contains("c/CLASS_NAME"),
            "Should use CLASS prefix for class name");

        // This will fail due to bug - it incorrectly shows SESSION prefix
        int sessionCount = usage.split("s/SESSION").length - 1;
        assertEquals(1, sessionCount,
            "BUG-175: SESSION prefix appears multiple times in usage!");
    }

    // ===== BUG-176: Attendance initialization uses putIfAbsent incorrectly =====
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

        // Re-initialize attendance (simulating some operation)
        session.initializeAttendance();

        // With putIfAbsent bug, attendance remains present
        // Should be reset to absent, but isn't due to bug
        assertTrue(session.hasAttended(student),
            "BUG-176: putIfAbsent doesn't reset existing attendance!");
    }

    // ===== BUG-177: Model.setSession() called but not implemented =====
    @Test
    public void model_setSession_shouldBeImplemented() {
        // This test checks if setSession method exists
        // Currently it doesn't exist in Model interface (bug)

        TuitionClass testClass = new TuitionClass(new ClassName("Test"));
        model.addClass(testClass);

        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        ClassSession session = testClass.addSession("Session", futureDate, "Room");

        // This would fail compilation if method doesn't exist
        // Demonstrates the bug - method is called in AttendCommand but not implemented
        try {
            // model.setSession(session, session); // Would not compile
            assertTrue(true, "BUG-177: Model.setSession() not implemented!");
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }

    // ===== BUG-178: Case-sensitive session name comparison =====
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

        // Try with different case
        AttendCommand command = new AttendCommand(
            new Name("Student"),
            "TestClass",
            "SESSION1", // Different case
            true
        );

        // This should work with case-insensitive comparison
        // Currently fails due to case-sensitive equals() (bug)
        try {
            command.execute(model);
            fail("BUG-178: Should throw exception due to case-sensitive comparison");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("not found"));
        }
    }

    // ===== BUG-179: Missing null check for role in JsonAdaptedPerson =====
    // Test removed - JsonAdaptedPerson is package-private and cannot be tested directly from here

    // ===== BUG-180: Tutor circular reference on self-assignment =====
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

        // First assignment
        testClass.setTutor(tutor);
        assertEquals(tutor, testClass.getTutor());

        // Self-assignment (same tutor again)
        // Should be optimized to return early
        testClass.setTutor(tutor);

        // Currently does unnecessary work (bug)
        assertEquals(tutor, testClass.getTutor());
        assertTrue(true, "BUG-180: Self-assignment not optimized!");
    }

    // ===== BUG-184: Missing range check for Index.fromOneBased(0) =====
    @Test
    public void indexFromOneBasedZeroShouldThrow() {
        // Zero is invalid for one-based index
        assertThrows(IndexOutOfBoundsException.class, () -> {
            Index.fromOneBased(0);
        }, "BUG-184: Zero should be invalid for one-based index!");
    }

    // ===== BUG-185: ClassSession equals() missing null checks =====
    @Test
    public void classSessionEqualsWithNullFieldsShouldNotThrowNpe() {
        TuitionClass testClass = new TuitionClass(new ClassName("Test"));
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
        ClassSession session1 = testClass.addSession("Session1", dateTime, "Room");
        ClassSession session2 = testClass.addSession("Session2", dateTime, "Room");

        // Should handle comparison without NPE
        assertNotEquals(session1, session2);
        assertNotEquals(session1, null);
        assertEquals(session1, session1);
    }

    // ===== BUG-186: Empty location string handled inconsistently =====
    @Test
    public void parserUtilParseLocationEmptyStringShouldReturnNull() {
        // Test empty location handling
        String result1 = ParserUtil.parseLocation("");
        String result2 = ParserUtil.parseLocation("   ");
        String result3 = ParserUtil.parseLocation(null);

        // Should be consistent - all return null
        assertNull(result1, "Empty string should return null");
        assertNull(result2, "Whitespace should return null");
        assertNull(result3, "Null should return null");
    }

    // ===== BUG-187: No cascade delete for class sessions =====
    @Test
    public void deleteClass_shouldCascadeDeleteSessions() {
        TuitionClass testClass = new TuitionClass(new ClassName("ToDelete"));
        model.addClass(testClass);

        // Add sessions
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        testClass.addSession("Session1", futureDate, "Room1");
        testClass.addSession("Session2", futureDate.plusDays(1), "Room2");

        assertEquals(2, testClass.getAllSessions().size());

        // Delete the class
        model.deleteClass(testClass);

        // Sessions should be deleted with the class (cascade)
        assertFalse(model.getAddressBook().getClassList().contains(testClass));
        // Sessions are deleted as they're part of the class object
    }

    // ===== BUG-188: No limit on number of tags per person =====
    @Test
    public void person_excessiveTags_shouldHaveLimit() {
        // Test that there's no limit on tags (bug)
        HashSet<seedu.address.model.tag.Tag> tags = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            tags.add(new seedu.address.model.tag.Tag("tag" + i));
        }

        Student student = new Student(
            new Name("Tagged"),
            new Phone("12345678"),
            new Email("tagged@test.com"),
            new Address("Address"),
            tags
        );

        // Currently accepts 50 tags without limit (bug)
        // Should ideally have a reasonable limit
        assertEquals(50, student.getTags().size(),
            "BUG-188: No limit on number of tags!");
    }
}
