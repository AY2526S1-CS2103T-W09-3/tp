package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Name;

/**
 * Contains integration tests (interaction with the Model) and unit tests for AttendCommand.
 */
public class AttendCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validAttendance_success() {
        Name name = new Name("Alice Pauline");
        String sessionId = "1";
        String status = "PRESENT";
        AttendCommand attendCommand = new AttendCommand(name, sessionId, status);

        String expectedMessage = String.format(AttendCommand.MESSAGE_SUCCESS,
                String.format("Name: %s, Session: %s, Status: %s", name, sessionId, status));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(attendCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_absentStatus_success() {
        Name name = new Name("Benson Meier");
        String sessionId = "2";
        String status = "ABSENT";
        AttendCommand attendCommand = new AttendCommand(name, sessionId, status);

        String expectedMessage = String.format(AttendCommand.MESSAGE_SUCCESS,
                String.format("Name: %s, Session: %s, Status: %s", name, sessionId, status));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(attendCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        Name aliceName = new Name("Alice");
        Name bobName = new Name("Bob");
        AttendCommand attendAliceSession1 = new AttendCommand(aliceName, "1", "PRESENT");
        AttendCommand attendAliceSession2 = new AttendCommand(aliceName, "2", "PRESENT");
        AttendCommand attendBobSession1 = new AttendCommand(bobName, "1", "PRESENT");
        AttendCommand attendAliceSession1Absent = new AttendCommand(aliceName, "1", "ABSENT");

        // same object -> returns true
        assertTrue(attendAliceSession1.equals(attendAliceSession1));

        // same values -> returns true
        AttendCommand attendAliceSession1Copy = new AttendCommand(aliceName, "1", "PRESENT");
        assertTrue(attendAliceSession1.equals(attendAliceSession1Copy));

        // different types -> returns false
        assertFalse(attendAliceSession1.equals(1));

        // null -> returns false
        assertFalse(attendAliceSession1.equals(null));

        // different name -> returns false
        assertFalse(attendAliceSession1.equals(attendBobSession1));

        // different session -> returns false
        assertFalse(attendAliceSession1.equals(attendAliceSession2));

        // different status -> returns false
        assertFalse(attendAliceSession1.equals(attendAliceSession1Absent));
    }

    @Test
    public void toStringMethod() {
        Name name = new Name("Alice");
        String sessionId = "1";
        String status = "PRESENT";
        AttendCommand attendCommand = new AttendCommand(name, sessionId, status);
        String expected = AttendCommand.class.getCanonicalName() + "{name=" + name
                + ", sessionId=" + sessionId + ", status=" + status + "}";
        assertEquals(expected, attendCommand.toString());
    }
}
