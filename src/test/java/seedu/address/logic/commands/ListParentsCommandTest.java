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
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Parent;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;

public class ListParentsCommandTest {

    @Test
    public void execute_listAllParents_success() throws Exception {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());

        Parent parent1 = new Parent(
                new Name("John Doe"),
                new Phone("91234567"),
                new Email("john@example.com"),
                new Address("123 Main St"),
                new java.util.HashSet<>()
        );
        Parent parent2 = new Parent(
                new Name("Jane Smith"),
                new Phone("98765432"),
                new Email("jane@example.com"),
                new Address("456 Second St"),
                new java.util.HashSet<>()
        );

        model.addPerson(parent1);
        model.addPerson(parent2);

        ListParentsCommand command = new ListParentsCommand();
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed all parents"));
        assertTrue(feedback.contains("John Doe"));
        assertTrue(feedback.contains("Jane Smith"));
    }

    @Test
    public void execute_noParents_success() throws Exception {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());

        ListParentsCommand command = new ListParentsCommand();
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed all parents"));
        assertTrue(feedback.contains("[No parents]"));
    }

    @Test
    public void execute_listParentsByChild_success() throws Exception {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());

        Student student = new Student(
                new Name("Alice Tan"),
                new Phone("91234567"),
                new Email("alice@example.com"),
                new Address("123 Main St"),
                new java.util.HashSet<>()
        );
        Parent parent = new Parent(
                new Name("John Tan"),
                new Phone("98765432"),
                new Email("john@example.com"),
                new Address("123 Main St"),
                new java.util.HashSet<>()
        );

        model.addPerson(student);
        model.addPerson(parent);
        student.addParent(parent);

        ListParentsCommand command = new ListParentsCommand("Alice Tan");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed parents for child: Alice Tan"));
        assertTrue(feedback.contains("John Tan"));
    }

    @Test
    public void execute_childNotFound_throwsCommandException() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());

        ListParentsCommand command = new ListParentsCommand("Non-existent Child");
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void equals() {
        ListParentsCommand command1 = new ListParentsCommand();
        ListParentsCommand command2 = new ListParentsCommand();
        ListParentsCommand command3 = new ListParentsCommand("Alice");
        ListParentsCommand command4 = new ListParentsCommand("Alice");
        ListParentsCommand command5 = new ListParentsCommand("Bob");

        // Same object -> returns true
        assertTrue(command1.equals(command1));

        // Same values -> returns true
        assertTrue(command1.equals(command2));
        assertTrue(command3.equals(command4));

        // Different values -> returns false
        assertFalse(command1.equals(command3));
        assertFalse(command3.equals(command5));

        // Different types -> returns false
        assertFalse(command1.equals(1));

        // null -> returns false
        assertFalse(command1.equals(null));
    }
}
