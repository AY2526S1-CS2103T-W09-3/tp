package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_ATTENDANCE_STATUS;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SESSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AttendCommand;
import seedu.address.model.person.Name;

/**
 * Contains unit tests for {@code AttendCommandParser}.
 */
public class AttendCommandParserTest {

    private AttendCommandParser parser = new AttendCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        String userInput = " " + PREFIX_NAME + "Alice Pauline "
                + PREFIX_CLASS + "Math101 "
                + PREFIX_SESSION + "Session1 "
                + PREFIX_STATUS + "PRESENT";

        AttendCommand expectedCommand = new AttendCommand(
                new Name("Alice Pauline"),
                "Math101",
                "Session1",
                true
        );

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_allFieldsPresentAbsentStatus_success() {
        String userInput = " " + PREFIX_NAME + "Bob Tan "
                + PREFIX_CLASS + "Science101 "
                + PREFIX_SESSION + "Session2 "
                + PREFIX_STATUS + "ABSENT";

        AttendCommand expectedCommand = new AttendCommand(
                new Name("Bob Tan"),
                "Science101",
                "Session2",
                false
        );

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_allFieldsPresentLowercaseStatus_success() {
        String userInput = " " + PREFIX_NAME + "Charlie Lee "
                + PREFIX_CLASS + "English101 "
                + PREFIX_SESSION + "Session3 "
                + PREFIX_STATUS + "PRESENT";

        AttendCommand expectedCommand = new AttendCommand(
                new Name("Charlie Lee"),
                "English101",
                "Session3",
                true
        );

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_allFieldsPresentWithWhitespace_success() {
        String userInput = " " + PREFIX_NAME + "David Ng "
                + PREFIX_CLASS + "  History101  "
                + PREFIX_SESSION + "  Session4  "
                + PREFIX_STATUS + "  ABSENT  ";

        AttendCommand expectedCommand = new AttendCommand(
                new Name("David Ng"),
                "History101",
                "Session4",
                false
        );

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingNamePrefix_failure() {
        String userInput = " " + PREFIX_CLASS + "Math101 "
                + PREFIX_SESSION + "Session1 "
                + PREFIX_STATUS + "PRESENT";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingClassPrefix_failure() {
        String userInput = " " + PREFIX_NAME + "Alice Pauline "
                + PREFIX_SESSION + "Session1 "
                + PREFIX_STATUS + "PRESENT";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingSessionPrefix_failure() {
        String userInput = " " + PREFIX_NAME + "Alice Pauline "
                + PREFIX_CLASS + "Math101 "
                + PREFIX_STATUS + "PRESENT";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingStatusPrefix_failure() {
        String userInput = " " + PREFIX_NAME + "Alice Pauline "
                + PREFIX_CLASS + "Math101 "
                + PREFIX_SESSION + "Session1";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingAllPrefixes_failure() {
        String userInput = " Alice Pauline Math101 Session1 PRESENT";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_preamblePresent_failure() {
        String userInput = "some preamble " + PREFIX_NAME + "Alice Pauline "
                + PREFIX_CLASS + "Math101 "
                + PREFIX_SESSION + "Session1 "
                + PREFIX_STATUS + "PRESENT";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidName_failure() {
        String userInput = " " + PREFIX_NAME + " "
                + PREFIX_CLASS + "Math101 "
                + PREFIX_SESSION + "Session1 "
                + PREFIX_STATUS + "PRESENT";

        assertParseFailure(parser, userInput, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidStatus_failure() {
        String userInput = " " + PREFIX_NAME + "Alice Pauline "
                + PREFIX_CLASS + "Math101 "
                + PREFIX_SESSION + "Session1 "
                + PREFIX_STATUS + "MAYBE";

        assertParseFailure(parser, userInput, MESSAGE_INVALID_ATTENDANCE_STATUS);
    }

    @Test
    public void parse_emptyClassName_failure() {
        String userInput = " " + PREFIX_NAME + "Alice Pauline "
                + PREFIX_CLASS
                + PREFIX_SESSION + "Session1 "
                + PREFIX_STATUS + "PRESENT";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptySessionName_failure() {
        String userInput = " " + PREFIX_NAME + "Alice Pauline "
                + PREFIX_CLASS + "Math101 "
                + PREFIX_SESSION
                + PREFIX_STATUS + "PRESENT";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyStatus_failure() {
        String userInput = " " + PREFIX_NAME + "Alice Pauline "
                + PREFIX_CLASS + "Math101 "
                + PREFIX_SESSION + "Session1 "
                + PREFIX_STATUS;

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_ATTENDANCE_STATUS, AttendCommand.MESSAGE_USAGE));
    }
}
