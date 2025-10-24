package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classroom.ClassName;
import seedu.address.model.classroom.ClassSession;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;

/**
 * Contains unit tests for {@code AttendCommand}.
 */
public class AttendCommandTest {

    private Model model;
    private Student testStudent;
    private TuitionClass testClass;
    private ClassSession testSession;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        // Set up test data
        testStudent = new Student(
                new Name("Alice Pauline"),
                new Phone("98765432"),
                new Email("david@example.com"),
                new Address("456 Second St"),
                new java.util.HashSet<>()
        );
        testClass = new TuitionClass(new ClassName("Math101"));
        testClass.addSession(
                "Session1",
                LocalDateTime.of(2025, 10, 25, 14, 0),
                "Room 101"
        );

        model.addClass(testClass);
    }

    @Test
    public void constructor_nullName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AttendCommand(null, "Math101", "Session1", true));
    }

    @Test
    public void constructor_nullClassName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AttendCommand(new Name("Alice Pauline"), null, "Session1", true));
    }

    @Test
    public void constructor_nullSessionName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AttendCommand(new Name("Alice Pauline"), "Math101", null, true));
    }

    @Test
    public void constructor_nullPresent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AttendCommand(new Name("Alice Pauline"), "Math101", "Session1", null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AttendCommand command = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", true);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_validStudentAndSessionPresent_success() throws Exception {
        AttendCommand command = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", true);

        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("Alice Pauline"));
        assertTrue(result.getFeedbackToUser().contains("Math101"));
        assertTrue(result.getFeedbackToUser().contains("Session1"));
        assertTrue(result.getFeedbackToUser().contains("PRESENT"));
    }

    @Test
    public void execute_validStudentAndSessionAbsent_success() throws Exception {
        AttendCommand command = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", false);

        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("Alice Pauline"));
        assertTrue(result.getFeedbackToUser().contains("Math101"));
        assertTrue(result.getFeedbackToUser().contains("Session1"));
        assertTrue(result.getFeedbackToUser().contains("ABSENT"));
    }

    @Test
    public void execute_sessionNotFound_throwsCommandException() {
        AttendCommand command = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "NonExistentSession", true);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertTrue(exception.getMessage().contains("Session"));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void execute_classNotFound_throwsCommandException() {
        AttendCommand command = new AttendCommand(
                new Name("Alice Pauline"), "NonExistentClass", "Session1", true);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertTrue(exception.getMessage().contains("Session"));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void execute_studentNotFound_throwsCommandException() {
        AttendCommand command = new AttendCommand(
                new Name("NonExistent Student"), "Math101", "Session1", true);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertTrue(exception.getMessage().contains("Student"));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        AttendCommand command = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", true);
        assertTrue(command.equals(command));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        AttendCommand command1 = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", true);
        AttendCommand command2 = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", true);
        assertTrue(command1.equals(command2));
    }

    @Test
    public void equals_differentName_returnsFalse() {
        AttendCommand command1 = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", true);
        AttendCommand command2 = new AttendCommand(
                new Name("Bob Tan"), "Math101", "Session1", true);
        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_differentClassName_returnsFalse() {
        AttendCommand command1 = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", true);
        AttendCommand command2 = new AttendCommand(
                new Name("Alice Pauline"), "Science101", "Session1", true);
        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_differentSessionName_returnsFalse() {
        AttendCommand command1 = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", true);
        AttendCommand command2 = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session2", true);
        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_differentPresent_returnsFalse() {
        AttendCommand command1 = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", true);
        AttendCommand command2 = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", false);
        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_null_returnsFalse() {
        AttendCommand command = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", true);
        assertFalse(command.equals(null));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        AttendCommand command = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", true);
        assertFalse(command.equals("Not a command"));
    }

    @Test
    public void toString_validCommand_correctFormat() {
        AttendCommand command = new AttendCommand(
                new Name("Alice Pauline"), "Math101", "Session1", true);
        String result = command.toString();

        assertTrue(result.contains("Alice Pauline"));
        assertTrue(result.contains("Math101"));
        assertTrue(result.contains("Session1"));
        assertTrue(result.contains("true"));
    }
}
