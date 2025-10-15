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
 * Contains integration tests (interaction with the Model) for {@code EditClassCommand}.
 */
public class EditClassCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new AddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newClassAcceptedByModel_editSuccessful() throws CommandException {
        // Add initial class
        TuitionClass validClass = new TuitionClass(new ClassName("Math101"));
        model.addClass(validClass);

        // Edit the class
        EditClassCommand editClassCommand = new EditClassCommand("Math101", "Mathematics101");
        CommandResult result = editClassCommand.execute(model);

        String expectedMessage = String.format(EditClassCommand.MESSAGE_EDIT_CLASS_SUCCESS,
                "Math101", "Mathematics101");
        assertEquals(expectedMessage, result.getFeedbackToUser());

        // Verify the class is edited
        TuitionClass editedClass = new TuitionClass(new ClassName("Mathematics101"));
        assert(model.hasClass(editedClass));
    }

    @Test
    public void execute_duplicateClass_throwsCommandException() {
        // Add two classes
        TuitionClass class1 = new TuitionClass(new ClassName("Math101"));
        TuitionClass class2 = new TuitionClass(new ClassName("Physics201"));
        model.addClass(class1);
        model.addClass(class2);

        // Try to edit class1 to have the same name as class2
        EditClassCommand editClassCommand = new EditClassCommand("Math101", "Physics201");

        assertThrows(CommandException.class, String.format(EditClassCommand.MESSAGE_DUPLICATE_CLASS,
                "Physics201"), () -> editClassCommand.execute(model));
    }

    @Test
    public void execute_classNotFound_throwsCommandException() {
        EditClassCommand editClassCommand = new EditClassCommand("NonExistentClass", "NewName");

        assertThrows(CommandException.class, String.format(EditClassCommand.MESSAGE_CLASS_NOT_FOUND,
                "NonExistentClass"), () -> editClassCommand.execute(model));
    }

    @Test
    public void execute_multipleClassesEditSequentially_allSuccessful() throws CommandException {
        // Add multiple classes
        model.addClass(new TuitionClass(new ClassName("Math101")));
        model.addClass(new TuitionClass(new ClassName("Physics201")));
        model.addClass(new TuitionClass(new ClassName("Chemistry301")));

        // Edit them sequentially
        new EditClassCommand("Math101", "Mathematics101").execute(model);
        new EditClassCommand("Physics201", "Physics202").execute(model);
        new EditClassCommand("Chemistry301", "Chem301").execute(model);

        // Verify all edits
        assert(model.hasClass(new TuitionClass(new ClassName("Mathematics101"))));
        assert(model.hasClass(new TuitionClass(new ClassName("Physics202"))));
        assert(model.hasClass(new TuitionClass(new ClassName("Chem301"))));
        assert(!model.hasClass(new TuitionClass(new ClassName("Math101"))));
        assert(!model.hasClass(new TuitionClass(new ClassName("Physics201"))));
        assert(!model.hasClass(new TuitionClass(new ClassName("Chemistry301"))));
    }

    @Test
    public void execute_editClassNameWithSpaces_successful() throws CommandException {
        // Add class
        model.addClass(new TuitionClass(new ClassName("Math101")));

        // Edit with spaces in name
        EditClassCommand editClassCommand = new EditClassCommand("Math101", "Advanced Math 101");
        editClassCommand.execute(model);

        // Verify
        assert(model.hasClass(new TuitionClass(new ClassName("Advanced Math 101"))));
    }

    @Test
    public void execute_editClassWithSpacesInBothNames_successful() throws CommandException {
        // Add class with spaces
        model.addClass(new TuitionClass(new ClassName("Advanced Math 101")));

        // Edit to another name with spaces
        EditClassCommand editClassCommand = new EditClassCommand("Advanced Math 101", "Honors Mathematics 201");
        editClassCommand.execute(model);

        // Verify
        assert(!model.hasClass(new TuitionClass(new ClassName("Advanced Math 101"))));
        assert(model.hasClass(new TuitionClass(new ClassName("Honors Mathematics 201"))));
    }
}
