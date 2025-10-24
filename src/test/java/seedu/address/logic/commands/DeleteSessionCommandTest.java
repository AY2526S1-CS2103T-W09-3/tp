package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
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
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;

public class DeleteSessionCommandTest {

    private Model model;
    private TuitionClass tuitionClass;
    private ClassSession session1;
    private ClassSession session2;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new AddressBook(), new UserPrefs());
        tuitionClass = new TuitionClass(new ClassName("Math101"));
        model.addClass(tuitionClass);

        // Add a student to the class for session initialization
        Student student = new Student(
                new Name("Alice Tan"),
                new Phone("98765432"),
                new Email("alice@example.com"),
                new Address("10 Kent Ridge Road"),
                new HashSet<>());
        model.addPerson(student);
        model.addStudentToClass(student, tuitionClass);

        // Add sessions
        session1 = tuitionClass.addSession("Week 1 Tutorial", LocalDateTime.of(2024, 3, 15, 14, 30), "COM1-B103");
        session2 = tuitionClass.addSession("Week 2 Tutorial", LocalDateTime.of(2024, 3, 22, 14, 30), "COM1-B103");
    }

    @Test
    public void constructor_nullClassName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new DeleteSessionCommand(null, "Week 1 Tutorial"));
    }

    @Test
    public void constructor_nullSessionName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new DeleteSessionCommand("Math101", null));
    }

    @Test
    public void constructor_emptyClassName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new DeleteSessionCommand("", "Week 1 Tutorial"));
    }

    @Test
    public void constructor_emptySessionName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new DeleteSessionCommand("Math101", ""));
    }

    @Test
    public void constructor_whitespaceTrimmed() {
        DeleteSessionCommand command = new DeleteSessionCommand("  Math101  ", "  Week 1 Tutorial  ");
        // Should not throw exception and whitespace should be trimmed
        assertEquals("Math101", command.toString().split("className=")[1].split(",")[0]);
    }

    @Test
    public void execute_validSessionDelete_success() throws Exception {
        DeleteSessionCommand command = new DeleteSessionCommand("Math101", "Week 1 Tutorial");
        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("Deleted session from class Math101"));
        assertTrue(result.getFeedbackToUser().contains("Week 1 Tutorial"));
        assertFalse(tuitionClass.getAllSessions().contains(session1));
        assertEquals(1, tuitionClass.getAllSessions().size());
        assertTrue(tuitionClass.getAllSessions().contains(session2));
    }

    @Test
    public void execute_caseInsensitiveSessionName_success() throws Exception {
        DeleteSessionCommand command = new DeleteSessionCommand("Math101", "week 1 tutorial");
        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("Deleted session from class Math101"));
        assertFalse(tuitionClass.getAllSessions().contains(session1));
        assertEquals(1, tuitionClass.getAllSessions().size());
    }

    @Test
    public void execute_sessionNameWithWhitespace_success() throws Exception {
        DeleteSessionCommand command = new DeleteSessionCommand("Math101", "  Week 1 Tutorial  ");
        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("Deleted session from class Math101"));
        assertFalse(tuitionClass.getAllSessions().contains(session1));
        assertEquals(1, tuitionClass.getAllSessions().size());
    }

    @Test
    public void execute_classNotFound_throwsCommandException() {
        DeleteSessionCommand command = new DeleteSessionCommand("NonExistentClass", "Week 1 Tutorial");
        assertThrows(CommandException.class, DeleteSessionCommand.MESSAGE_CLASS_NOT_EXIST, () ->
                command.execute(model));
    }

    @Test
    public void execute_sessionNotFound_throwsCommandException() {
        DeleteSessionCommand command = new DeleteSessionCommand("Math101", "NonExistent Session");
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_deleteLastSession_success() throws Exception {
        // Delete first session
        new DeleteSessionCommand("Math101", "Week 1 Tutorial").execute(model);

        // Delete second session
        DeleteSessionCommand command = new DeleteSessionCommand("Math101", "Week 2 Tutorial");
        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("Deleted session from class Math101"));
        assertEquals(0, tuitionClass.getAllSessions().size());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        DeleteSessionCommand command = new DeleteSessionCommand("Math101", "Week 1 Tutorial");
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals() {
        DeleteSessionCommand command1 = new DeleteSessionCommand("Math101", "Week 1 Tutorial");
        DeleteSessionCommand command2 = new DeleteSessionCommand("Math101", "Week 1 Tutorial");
        DeleteSessionCommand command3 = new DeleteSessionCommand("Math102", "Week 1 Tutorial");
        DeleteSessionCommand command4 = new DeleteSessionCommand("Math101", "Week 2 Tutorial");

        // same object -> returns true
        assertTrue(command1.equals(command1));

        // same values -> returns true
        assertTrue(command1.equals(command2));

        // different class -> returns false
        assertFalse(command1.equals(command3));

        // different session name -> returns false
        assertFalse(command1.equals(command4));

        // null -> returns false
        assertFalse(command1.equals(null));

        // different type -> returns false
        assertFalse(command1.equals(1));
    }

    @Test
    public void hashCode_sameValues_sameHashCode() {
        DeleteSessionCommand command1 = new DeleteSessionCommand("Math101", "Week 1 Tutorial");
        DeleteSessionCommand command2 = new DeleteSessionCommand("Math101", "Week 1 Tutorial");

        assertEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    public void toString_returnsCorrectFormat() {
        DeleteSessionCommand command = new DeleteSessionCommand("Math101", "Week 1 Tutorial");
        String result = command.toString();

        assertTrue(result.contains("Math101"));
        assertTrue(result.contains("Week 1 Tutorial"));
    }
}
