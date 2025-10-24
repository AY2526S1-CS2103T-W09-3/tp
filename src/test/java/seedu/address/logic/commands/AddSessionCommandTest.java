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
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;

public class AddSessionCommandTest {

    private Model model;
    private TuitionClass tuitionClass;
    private LocalDateTime testDateTime;

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

        testDateTime = LocalDateTime.of(2024, 3, 15, 14, 30);
    }

    @Test
    public void constructor_nullClassName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AddSessionCommand(null, "Week 1", testDateTime, "COM1-B103"));
    }

    @Test
    public void constructor_nullSessionName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AddSessionCommand("Math101", null, testDateTime, "COM1-B103"));
    }

    @Test
    public void constructor_nullDateTime_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AddSessionCommand("Math101", "Week 1", null, "COM1-B103"));
    }

    @Test
    public void execute_sessionAcceptedByModel_addSuccessful() throws Exception {
        AddSessionCommand command = new AddSessionCommand("Math101", "Week 1 Tutorial", testDateTime, "COM1-B103");
        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("New session added to class Math101"));
        assertTrue(result.getFeedbackToUser().contains("Week 1 Tutorial"));
        assertTrue(tuitionClass.hasSessionName("Week 1 Tutorial"));
        assertEquals(1, tuitionClass.getAllSessions().size());
    }

    @Test
    public void execute_sessionWithoutLocation_addSuccessful() throws Exception {
        AddSessionCommand command = new AddSessionCommand("Math101", "Week 2 Tutorial", testDateTime, null);
        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("New session added to class Math101"));
        assertTrue(tuitionClass.hasSessionName("Week 2 Tutorial"));
        assertEquals(1, tuitionClass.getAllSessions().size());
    }

    @Test
    public void execute_classNotFound_throwsCommandException() {
        AddSessionCommand command = new AddSessionCommand("NonExistentClass", "Week 1", testDateTime, "COM1-B103");
        assertThrows(CommandException.class, AddSessionCommand.MESSAGE_CLASS_NOT_EXIST, () ->
                command.execute(model));
    }

    @Test
    public void execute_duplicateSessionName_throwsCommandException() throws Exception {
        // Add first session
        new AddSessionCommand("Math101", "Week 1 Tutorial", testDateTime, "COM1-B103").execute(model);

        // Try to add session with same name
        AddSessionCommand command = new AddSessionCommand("Math101", "Week 1 Tutorial",
                testDateTime.plusDays(1), "COM1-B104");
        assertThrows(CommandException.class, AddSessionCommand.MESSAGE_DUPLICATE_SESSION, () ->
                command.execute(model));
    }

    @Test
    public void execute_duplicateSessionNameWithWhitespace_throwsCommandException() throws Exception {
        // Add first session
        new AddSessionCommand("Math101", "Week 1 Tutorial", testDateTime, "COM1-B103").execute(model);

        // Try to add session with same name but different whitespace
        AddSessionCommand command = new AddSessionCommand("Math101", " Week 1 Tutorial ",
                testDateTime.plusDays(1), "COM1-B104");
        assertThrows(CommandException.class, AddSessionCommand.MESSAGE_DUPLICATE_SESSION, () ->
                command.execute(model));
    }

    @Test
    public void execute_multipleSessionsWithDifferentNames_addSuccessful() throws Exception {
        AddSessionCommand command1 = new AddSessionCommand("Math101", "Week 1 Tutorial", testDateTime, "COM1-B103");
        AddSessionCommand command2 = new AddSessionCommand("Math101", "Week 2 Tutorial",
                testDateTime.plusDays(7), "COM1-B103");
        AddSessionCommand command3 = new AddSessionCommand("Math101", "Week 3 Tutorial",
                testDateTime.plusDays(14), "COM1-B103");

        command1.execute(model);
        command2.execute(model);
        command3.execute(model);

        assertEquals(3, tuitionClass.getAllSessions().size());
        assertTrue(tuitionClass.hasSessionName("Week 1 Tutorial"));
        assertTrue(tuitionClass.hasSessionName("Week 2 Tutorial"));
        assertTrue(tuitionClass.hasSessionName("Week 3 Tutorial"));
    }

    @Test
    public void equals() {
        LocalDateTime dateTime1 = LocalDateTime.of(2024, 3, 15, 14, 30);
        LocalDateTime dateTime2 = LocalDateTime.of(2024, 3, 16, 14, 30);

        AddSessionCommand command1 = new AddSessionCommand("Math101", "Week 1", dateTime1, "COM1-B103");
        AddSessionCommand command2 = new AddSessionCommand("Math101", "Week 1", dateTime1, "COM1-B103");
        AddSessionCommand command3 = new AddSessionCommand("Math102", "Week 1", dateTime1, "COM1-B103");
        AddSessionCommand command4 = new AddSessionCommand("Math101", "Week 2", dateTime1, "COM1-B103");
        AddSessionCommand command5 = new AddSessionCommand("Math101", "Week 1", dateTime2, "COM1-B103");
        AddSessionCommand command6 = new AddSessionCommand("Math101", "Week 1", dateTime1, "COM1-B104");
        AddSessionCommand command7 = new AddSessionCommand("Math101", "Week 1", dateTime1, null);

        // same object -> returns true
        assertTrue(command1.equals(command1));

        // same values -> returns true
        assertTrue(command1.equals(command2));

        // different class -> returns false
        assertFalse(command1.equals(command3));

        // different session name -> returns false
        assertFalse(command1.equals(command4));

        // different date time -> returns false
        assertFalse(command1.equals(command5));

        // different location -> returns false
        assertFalse(command1.equals(command6));

        // null location vs non-null location -> returns false
        assertFalse(command1.equals(command7));

        // null -> returns false
        assertFalse(command1.equals(null));

        // different type -> returns false
        assertFalse(command1.equals(1));
    }

    @Test
    public void equals_bothLocationsNull_returnsTrue() {
        AddSessionCommand command1 = new AddSessionCommand("Math101", "Week 1", testDateTime, null);
        AddSessionCommand command2 = new AddSessionCommand("Math101", "Week 1", testDateTime, null);

        assertTrue(command1.equals(command2));
    }

    @Test
    public void toString_returnsCorrectFormat() {
        AddSessionCommand command = new AddSessionCommand("Math101", "Week 1 Tutorial", testDateTime, "COM1-B103");
        String result = command.toString();

        assertTrue(result.contains("Math101"));
        assertTrue(result.contains("Week 1 Tutorial"));
        assertTrue(result.contains("2024-03-15"));
        assertTrue(result.contains("COM1-B103"));
    }
}
