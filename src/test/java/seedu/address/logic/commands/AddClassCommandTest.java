package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

public class AddClassCommandTest {

    @Test
    public void constructor_nullClassName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddClassCommand(null));
    }

    @Test
    public void execute_classAcceptedByModel_addSuccessful() throws Exception {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        TuitionClass validClass = new TuitionClass(new ClassName("Sec1-Math-A"));

        CommandResult commandResult = new AddClassCommand("Sec1-Math-A").execute(model);

        assertEquals(String.format(AddClassCommand.MESSAGE_SUCCESS, validClass.getName().value),
                commandResult.getFeedbackToUser());
        assertTrue(model.hasClass(validClass));
    }

    @Test
    public void execute_duplicateClass_throwsCommandException() {
        TuitionClass validClass = new TuitionClass(new ClassName("Sec1-Math-A"));
        AddClassCommand addClassCommand = new AddClassCommand("Sec1-Math-A");
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        model.addClass(validClass);

        assertThrows(CommandException.class, AddClassCommand.MESSAGE_DUPLICATE_CLASS, (
                ) -> addClassCommand.execute(model));
    }

    @Test
    public void equals() {
        AddClassCommand addClassCommand = new AddClassCommand("Sec1-Math-A");
        AddClassCommand addClassCommandCopy = new AddClassCommand("Sec1-Math-A");
        AddClassCommand addClassCommandDifferent = new AddClassCommand("Sec2-Math-B");

        // same object -> returns true
        assertTrue(addClassCommand.equals(addClassCommand));

        // same values -> returns true
        assertTrue(addClassCommand.equals(addClassCommandCopy));

        // different types -> returns false
        assertFalse(addClassCommand.equals(1));

        // null -> returns false
        assertFalse(addClassCommand.equals(null));

        // different class name -> returns false
        assertFalse(addClassCommand.equals(addClassCommandDifferent));
    }
}
