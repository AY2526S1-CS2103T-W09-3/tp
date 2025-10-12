package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SESSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AttendCommand;
import seedu.address.model.person.Name;

/**
 * Contains unit tests for AttendCommandParser.
 */
public class AttendCommandParserTest {

    private AttendCommandParser parser = new AttendCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Name expectedName = new Name("Alice Pauline");
        String expectedSessionId = "1";
        String expectedStatus = "PRESENT";

        // normal case
        assertParseSuccess(parser,
                        " " + PREFIX_NAME + "Alice Pauline " + PREFIX_SESSION + "1 " + PREFIX_STATUS + "PRESENT",
                        new AttendCommand(expectedName, expectedSessionId, expectedStatus));

        // lowercase status gets converted to uppercase
        assertParseSuccess(parser,
                        " " + PREFIX_NAME + "Alice Pauline " + PREFIX_SESSION + "1 " + PREFIX_STATUS + "present",
                        new AttendCommand(expectedName, expectedSessionId, expectedStatus));

        // mixed case status gets converted to uppercase
        assertParseSuccess(parser,
                        " " + PREFIX_NAME + "Alice Pauline " + PREFIX_SESSION + "1 " + PREFIX_STATUS + "PrEsEnT",
                        new AttendCommand(expectedName, expectedSessionId, expectedStatus));
    }

    @Test
    public void parse_absentStatus_success() {
        Name expectedName = new Name("Bob");
        String expectedSessionId = "2";
        String expectedStatus = "ABSENT";

        assertParseSuccess(parser, " " + PREFIX_NAME + "Bob " + PREFIX_SESSION + "2 " + PREFIX_STATUS + "ABSENT ",
                        new AttendCommand(expectedName, expectedSessionId, expectedStatus));

        // lowercase absent
        assertParseSuccess(parser, " " + PREFIX_NAME + "Bob " + PREFIX_SESSION + "2 " + PREFIX_STATUS + "absent ",
                        new AttendCommand(expectedName, expectedSessionId, expectedStatus));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, " " + PREFIX_SESSION + "1 " + PREFIX_STATUS + "PRESENT", expectedMessage);

        // missing session prefix
        assertParseFailure(parser, " " + PREFIX_NAME + "Alice " + PREFIX_STATUS + "PRESENT", expectedMessage);

        // missing status prefix
        assertParseFailure(parser, " " + PREFIX_NAME + "Alice " + PREFIX_SESSION + "1", expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, " Alice 1 PRESENT", expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name (contains special characters not allowed)
        assertParseFailure(parser, " " + PREFIX_NAME + "Alice@#$ " + PREFIX_SESSION + "1 " + PREFIX_STATUS + "PRESENT",
                        Name.MESSAGE_CONSTRAINTS);

        // invalid status (not PRESENT or ABSENT)
        assertParseFailure(parser, " " + PREFIX_NAME + "Alice " + PREFIX_SESSION + "1 " + PREFIX_STATUS + "MAYBE",
                        "Status must be either PRESENT or ABSENT");

        assertParseFailure(parser, " " + PREFIX_NAME + "Alice " + PREFIX_SESSION + "1 " + PREFIX_STATUS + "YES",
                        "Status must be either PRESENT or ABSENT");
    }

    @Test
    public void parse_preamblePresent_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendCommand.MESSAGE_USAGE);

        // non-empty preamble
        assertParseFailure(parser, "some random text " + PREFIX_NAME + "Alice " + PREFIX_SESSION + "1 " + PREFIX_STATUS
                        + "PRESENT", expectedMessage);
    }
}
