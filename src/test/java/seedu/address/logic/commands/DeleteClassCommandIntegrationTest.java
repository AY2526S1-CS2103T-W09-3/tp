package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

/**
 * Contains integration tests (interaction with the Model) for {@code DeleteClassCommand}.
 */
public class DeleteClassCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new AddressBook(), new UserPrefs());
    }

    @Test
    public void execute_existingClass_deleteSuccessful() throws CommandException {
        // Add class
        TuitionClass classToDelete = new TuitionClass(new ClassName("Math101"));
        model.addClass(classToDelete);

        // Delete the class
        DeleteClassCommand deleteClassCommand = new DeleteClassCommand("Math101");
        CommandResult result = deleteClassCommand.execute(model);

        String expectedMessage = String.format(DeleteClassCommand.MESSAGE_DELETE_CLASS_SUCCESS, "Math101");
        assertEquals(expectedMessage, result.getFeedbackToUser());

        // Verify the class is deleted
        assert(!model.hasClass(classToDelete));
    }

    @Test
    public void execute_nonExistentClass_throwsCommandException() {
        DeleteClassCommand deleteClassCommand = new DeleteClassCommand("NonExistentClass");

        assertThrows(CommandException.class, String.format(DeleteClassCommand.MESSAGE_CLASS_NOT_FOUND,
                "NonExistentClass"), () -> deleteClassCommand.execute(model));
    }

    @Test
    public void execute_multipleClassesDeleteSequentially_allSuccessful() throws CommandException {
        // Add multiple classes
        TuitionClass class1 = new TuitionClass(new ClassName("Math101"));
        TuitionClass class2 = new TuitionClass(new ClassName("Physics201"));
        TuitionClass class3 = new TuitionClass(new ClassName("Chemistry301"));
        model.addClass(class1);
        model.addClass(class2);
        model.addClass(class3);

        // Delete them sequentially
        new DeleteClassCommand("Math101").execute(model);
        new DeleteClassCommand("Physics201").execute(model);
        new DeleteClassCommand("Chemistry301").execute(model);

        // Verify all are deleted
        assert(!model.hasClass(class1));
        assert(!model.hasClass(class2));
        assert(!model.hasClass(class3));
    }

    @Test
    public void execute_deleteClassTwice_throwsCommandException() throws CommandException {
        // Add and delete a class
        model.addClass(new TuitionClass(new ClassName("Math101")));
        new DeleteClassCommand("Math101").execute(model);

        // Try to delete again
        DeleteClassCommand deleteClassCommand = new DeleteClassCommand("Math101");
        assertThrows(CommandException.class, String.format(DeleteClassCommand.MESSAGE_CLASS_NOT_FOUND,
                "Math101"), () -> deleteClassCommand.execute(model));
    }

    @Test
    public void execute_deleteClassWithSpacesInName_successful() throws CommandException {
        // Add class with spaces
        TuitionClass classWithSpaces = new TuitionClass(new ClassName("Advanced Math 101"));
        model.addClass(classWithSpaces);

        // Delete the class
        DeleteClassCommand deleteClassCommand = new DeleteClassCommand("Advanced Math 101");
        deleteClassCommand.execute(model);

        // Verify deletion
        assert(!model.hasClass(classWithSpaces));
    }

    @Test
    public void execute_deleteFromEmptyClassList_throwsCommandException() {
        // Model starts with empty class list
        DeleteClassCommand deleteClassCommand = new DeleteClassCommand("AnyClass");

        assertThrows(CommandException.class, String.format(DeleteClassCommand.MESSAGE_CLASS_NOT_FOUND,
                "AnyClass"), () -> deleteClassCommand.execute(model));
    }
}
