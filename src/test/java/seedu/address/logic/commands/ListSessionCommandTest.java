package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.classroom.ClassName;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Tutor;

/**
 * Unit tests for {@link ListSessionCommand}.
 */
public class ListSessionCommandTest {

    private Model model;
    private TuitionClass mathClass;
    private TuitionClass scienceClass;
    private TuitionClass emptyClass;
    private Tutor tutor;

    @BeforeEach
    void setUp() {
        model = new ModelManager();

        tutor = new Tutor(
                new Name("Mr Smith"),
                new Phone("91234567"),
                new Email("smith@example.com"),
                new Address("1 Tutor Lane"),
                new HashSet<>()
        );

        // Class with multiple sessions
        mathClass = new TuitionClass(new ClassName("Math101"), tutor);
        mathClass.addSession("Session1", LocalDateTime.of(2025, 10, 25, 14, 0), "Room 101");
        mathClass.addSession("Session2", LocalDateTime.of(2025, 11, 1, 14, 0), "Room 102");

        // Class with one session
        scienceClass = new TuitionClass(new ClassName("Science101"), tutor);
        scienceClass.addSession("Lab1", LocalDateTime.of(2025, 10, 26, 10, 0), "Lab A");

        // Class with no sessions
        emptyClass = new TuitionClass(new ClassName("EmptyClass"), tutor);

        model.addClass(mathClass);
        model.addClass(scienceClass);
        model.addClass(emptyClass);
    }

    @Test
    @DisplayName("Constructor throws exception when className is null")
    void constructor_nullClassName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListSessionCommand(null));
    }

    @Test
    @DisplayName("Execute throws exception when model is null")
    void execute_nullModel_throwsNullPointerException() {
        ListSessionCommand command = new ListSessionCommand("Math101");
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    @DisplayName("Execute lists sessions for valid class with multiple sessions")
    void execute_validClassWithMultipleSessions_success() throws CommandException {
        ListSessionCommand command = new ListSessionCommand("Math101");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Math101"));
        assertTrue(feedback.contains("Session1"));
        assertTrue(feedback.contains("Session2"));
        assertTrue(feedback.contains("Room 101"));
        assertTrue(feedback.contains("Room 102"));
    }

    @Test
    @DisplayName("Execute lists session for valid class with one session")
    void execute_validClassWithOneSession_success() throws CommandException {
        ListSessionCommand command = new ListSessionCommand("Science101");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Science101"));
        assertTrue(feedback.contains("Lab1"));
        assertTrue(feedback.contains("Lab A"));
    }

    @Test
    @DisplayName("Execute shows no sessions message for class without sessions")
    void execute_validClassNoSessions_success() throws CommandException {
        ListSessionCommand command = new ListSessionCommand("EmptyClass");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("EmptyClass"));
        assertTrue(feedback.contains("[No sessions]"));
    }

    @Test
    @DisplayName("Execute is case insensitive for class name (lowercase)")
    void execute_classNameLowercase_success() throws CommandException {
        ListSessionCommand command = new ListSessionCommand("math101");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("math101"));
        assertTrue(feedback.contains("Session1"));
    }

    @Test
    @DisplayName("Execute is case insensitive for class name (uppercase)")
    void execute_classNameUppercase_success() throws CommandException {
        ListSessionCommand command = new ListSessionCommand("MATH101");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("MATH101"));
        assertTrue(feedback.contains("Session1"));
    }

    @Test
    @DisplayName("Execute throws exception when class not found")
    void execute_classNotFound_throwsCommandException() {
        ListSessionCommand command = new ListSessionCommand("NonExistentClass");

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertTrue(exception.getMessage().contains("Class not found"));
        assertTrue(exception.getMessage().contains("NonExistentClass"));
    }

    @Test
    @DisplayName("Equals returns true for same object instance")
    void equals_sameInstance_returnsTrue() {
        ListSessionCommand command = new ListSessionCommand("Math101");
        assertTrue(command.equals(command));
    }

    @Test
    @DisplayName("Equals returns true for same class name")
    void equals_sameClassName_returnsTrue() {
        ListSessionCommand command1 = new ListSessionCommand("Math101");
        ListSessionCommand command2 = new ListSessionCommand("Math101");
        assertTrue(command1.equals(command2));
    }

    @Test
    @DisplayName("Equals returns true for same class name with different case")
    void equals_sameClassNameDifferentCase_returnsTrue() {
        ListSessionCommand command1 = new ListSessionCommand("Math101");
        ListSessionCommand command2 = new ListSessionCommand("math101");
        assertTrue(command1.equals(command2));
    }

    @Test
    @DisplayName("Equals returns false for different class name")
    void equals_differentClassName_returnsFalse() {
        ListSessionCommand command1 = new ListSessionCommand("Math101");
        ListSessionCommand command2 = new ListSessionCommand("Science101");
        assertFalse(command1.equals(command2));
    }

    @Test
    @DisplayName("Equals returns false for null")
    void equals_null_returnsFalse() {
        ListSessionCommand command = new ListSessionCommand("Math101");
        assertFalse(command.equals(null));
    }

    @Test
    @DisplayName("Equals returns false for different type")
    void equals_differentType_returnsFalse() {
        ListSessionCommand command = new ListSessionCommand("Math101");
        assertFalse(command.equals("Not a command"));
    }

    @Test
    @DisplayName("HashCode is same for equal commands")
    void hashCode_equalCommands_sameHashCode() {
        ListSessionCommand command1 = new ListSessionCommand("Math101");
        ListSessionCommand command2 = new ListSessionCommand("Math101");
        assertEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    @DisplayName("HashCode is same for same class name with different case")
    void hashCode_sameClassNameDifferentCase_sameHashCode() {
        ListSessionCommand command1 = new ListSessionCommand("Math101");
        ListSessionCommand command2 = new ListSessionCommand("math101");
        assertEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    @DisplayName("ToString contains class name")
    void toString_containsClassName() {
        ListSessionCommand command = new ListSessionCommand("Math101");
        String result = command.toString();

        assertTrue(result.contains("ListSessionCommand"));
        assertTrue(result.contains("Math101"));
    }

    @Test
    @DisplayName("Execute formats date and time correctly")
    void execute_formatsDateTimeCorrectly() throws CommandException {
        ListSessionCommand command = new ListSessionCommand("Math101");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        // Check date formatting
        assertTrue(feedback.contains("Oct 2025") || feedback.contains("25 Oct 2025"));
        // Check time formatting
        assertTrue(feedback.contains("PM") || feedback.contains("pm") || feedback.contains("02:00"));
    }
}
