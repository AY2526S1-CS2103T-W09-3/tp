package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditClassCommand;

public class EditClassCommandParserTest {

    private EditClassCommandParser parser = new EditClassCommandParser();

    @Test
    public void parse_validArgs_returnsEditClassCommand() {
        // Single word class names
        assertParseSuccess(parser, " o/Math101 c/Mathematics101",
                new EditClassCommand("Math101", "Mathematics101"));

        // With leading/trailing spaces
        assertParseSuccess(parser, "   o/Math101   c/Mathematics101   ",
                new EditClassCommand("Math101", "Mathematics101"));

        // Class names with spaces
        assertParseSuccess(parser, " o/Advanced Math c/Advanced Mathematics",
                new EditClassCommand("Advanced Math", "Advanced Mathematics"));

        // Different order of prefixes
        assertParseSuccess(parser, " c/Mathematics101 o/Math101",
                new EditClassCommand("Math101", "Mathematics101"));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // Empty arguments
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditClassCommand.MESSAGE_USAGE));

        // Missing old class prefix
        assertParseFailure(parser, "Math101 c/Mathematics101",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditClassCommand.MESSAGE_USAGE));

        // Missing new class prefix
        assertParseFailure(parser, "o/Math101 Mathematics101",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditClassCommand.MESSAGE_USAGE));

        // Only one prefix
        assertParseFailure(parser, "o/Math101",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditClassCommand.MESSAGE_USAGE));

        // Only spaces
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditClassCommand.MESSAGE_USAGE));

        // Preamble present
        assertParseFailure(parser, "extra o/Math101 c/Mathematics101",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditClassCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_classNamesWithSpecialCharacters_successful() {
        // Class names with hyphens
        assertParseSuccess(parser, " o/Sec2-Math-A c/Sec3-Math-A",
                new EditClassCommand("Sec2-Math-A", "Sec3-Math-A"));

        // Class names with underscores
        assertParseSuccess(parser, " o/CS_101 c/CS_201",
                new EditClassCommand("CS_101", "CS_201"));

        // Class names with numbers
        assertParseSuccess(parser, " o/Class1 c/Class2",
                new EditClassCommand("Class1", "Class2"));
    }
}
