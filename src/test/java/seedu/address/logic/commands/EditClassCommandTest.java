package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classroom.ClassName;
import seedu.address.model.classroom.TuitionClass;

public class EditClassCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new AddressBook(), new UserPrefs());
    }

    @Test
    public void execute_classNotFound_throwsCommandException() {
        EditClassCommand editClassCommand = new EditClassCommand("NonExistentClass", "NewClass");
        assertThrows(CommandException.class, () -> editClassCommand.execute(model));
    }

    @Test
    public void execute_duplicateClass_throwsCommandException() throws CommandException {
        // Add two classes
        model.addClass(new TuitionClass(new ClassName("OldClass")));
        model.addClass(new TuitionClass(new ClassName("ExistingClass")));

        EditClassCommand editClassCommand = new EditClassCommand("OldClass", "ExistingClass");
        assertThrows(CommandException.class, () -> editClassCommand.execute(model));
    }

    @Test
    public void execute_validClass_editSuccessful() throws CommandException {
        // Add a class
        model.addClass(new TuitionClass(new ClassName("OldClass")));

        EditClassCommand editClassCommand = new EditClassCommand("OldClass", "NewClass");
        CommandResult result = editClassCommand.execute(model);

        String expectedMessage = String.format(EditClassCommand.MESSAGE_EDIT_CLASS_SUCCESS,
                "OldClass", "NewClass");
        assertEquals(expectedMessage, result.getFeedbackToUser());

        // Verify the old class is replaced with new one
        assertFalse(model.hasClass(new TuitionClass(new ClassName("OldClass"))));
        assertTrue(model.hasClass(new TuitionClass(new ClassName("NewClass"))));
    }

    @Test
    public void equals() {
        EditClassCommand editClass1 = new EditClassCommand("Class1", "ClassA");
        EditClassCommand editClass2 = new EditClassCommand("Class2", "ClassB");
        EditClassCommand editClass1Copy = new EditClassCommand("Class1", "ClassA");

        // same object -> returns true
        assertTrue(editClass1.equals(editClass1));

        // same values -> returns true
        assertTrue(editClass1.equals(editClass1Copy));

        // different values -> returns false
        assertFalse(editClass1.equals(editClass2));

        // null -> returns false
        assertFalse(editClass1.equals(null));

        // different type -> returns false
        assertFalse(editClass1.equals(new ClearCommand()));
    }
}
