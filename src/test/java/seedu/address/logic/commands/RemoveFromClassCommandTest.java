package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

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
import seedu.address.model.person.Parent;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;

public class RemoveFromClassCommandTest {

    @Test
    public void execute_removeStudentFromClass_success() throws Exception {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());

        Student student = new Student(
                new Name("Alice Tan"),
                new Phone("91234567"),
                new Email("alice@example.com"),
                new Address("123 Main St"),
                new java.util.HashSet<>()
        );
        TuitionClass mathClass = new TuitionClass(new ClassName("Math 101"));

        model.addPerson(student);
        model.addClass(mathClass);
        mathClass.addStudent(student);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Removed Alice Tan from class: Math 101"));
        assertFalse(mathClass.hasStudent(student));
        assertFalse(student.getTuitionClasses().contains(mathClass));
    }

    @Test
    public void execute_removeTutorFromClass_success() throws Exception {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());

        Tutor tutor = new Tutor(
                new Name("John Smith"),
                new Phone("91234567"),
                new Email("john@example.com"),
                new Address("123 Main St"),
                new java.util.HashSet<>()
        );
        TuitionClass mathClass = new TuitionClass(new ClassName("Math 101"));

        model.addPerson(tutor);
        model.addClass(mathClass);
        mathClass.setTutor(tutor);

        RemoveFromClassCommand command = new RemoveFromClassCommand("John Smith", new ClassName("Math 101"));
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Removed John Smith from class: Math 101"));
        assertFalse(mathClass.hasTutor(tutor));
        assertFalse(tutor.getTuitionClasses().contains(mathClass));
    }

    @Test
    public void execute_personNotFound_throwsCommandException() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        TuitionClass mathClass = new TuitionClass(new ClassName("Math 101"));
        model.addClass(mathClass);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Non-existent Person", new ClassName("Math 101"));
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_classNotFound_throwsCommandException() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        Student student = new Student(
                new Name("Alice Tan"),
                new Phone("91234567"),
                new Email("alice@example.com"),
                new Address("123 Main St"),
                new java.util.HashSet<>()
        );
        model.addPerson(student);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Non-existent Class"));
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_studentNotInClass_throwsCommandException() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        Student student = new Student(
                new Name("Alice Tan"),
                new Phone("91234567"),
                new Email("alice@example.com"),
                new Address("123 Main St"),
                new java.util.HashSet<>()
        );
        TuitionClass mathClass = new TuitionClass(new ClassName("Math 101"));

        model.addPerson(student);
        model.addClass(mathClass);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_parentCannotBeRemoved_throwsCommandException() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        Parent parent = new Parent(
                new Name("John Doe"),
                new Phone("91234567"),
                new Email("john@example.com"),
                new Address("123 Main St"),
                new java.util.HashSet<>()
        );
        TuitionClass mathClass = new TuitionClass(new ClassName("Math 101"));

        model.addPerson(parent);
        model.addClass(mathClass);

        RemoveFromClassCommand command = new RemoveFromClassCommand("John Doe", new ClassName("Math 101"));
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void equals() {
        RemoveFromClassCommand command1 = new RemoveFromClassCommand("Alice", new ClassName("Math 101"));
        RemoveFromClassCommand command2 = new RemoveFromClassCommand("Alice", new ClassName("Math 101"));
        RemoveFromClassCommand command3 = new RemoveFromClassCommand("Bob", new ClassName("Math 101"));
        RemoveFromClassCommand command4 = new RemoveFromClassCommand("Alice", new ClassName("Science 201"));

        // Same object -> returns true
        assertTrue(command1.equals(command1));

        // Same values -> returns true
        assertTrue(command1.equals(command2));

        // Different person -> returns false
        assertFalse(command1.equals(command3));

        // Different class -> returns false
        assertFalse(command1.equals(command4));

        // Different types -> returns false
        assertFalse(command1.equals(1));

        // null -> returns false
        assertFalse(command1.equals(null));
    }
}
