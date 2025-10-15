package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteClassCommand;

public class DeleteClassCommandParserTest {

    private DeleteClassCommandParser parser = new DeleteClassCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteClassCommand() {
        // Single word class name
        assertParseSuccess(parser, "Math101", new DeleteClassCommand("Math101"));

        // Class name with spaces (treated as single argument)
        assertParseSuccess(parser, "Advanced Math", new DeleteClassCommand("Advanced Math"));

        // With leading/trailing spaces
        assertParseSuccess(parser, "   Math101   ", new DeleteClassCommand("Math101"));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // Empty arguments
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteClassCommand.MESSAGE_USAGE));

        // Only spaces
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteClassCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_classNameWithSpecialCharacters_successful() {
        // Class name with hyphens
        assertParseSuccess(parser, "Sec2-Math-A", new DeleteClassCommand("Sec2-Math-A"));

        // Class name with underscores
        assertParseSuccess(parser, "CS_101", new DeleteClassCommand("CS_101"));

        // Class name with numbers
        assertParseSuccess(parser, "Class123", new DeleteClassCommand("Class123"));

        // Class name with combination of special chars
        assertParseSuccess(parser, "Math-101_Section-A", new DeleteClassCommand("Math-101_Section-A"));
    }
}
