package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListSessionCommand;

/**
 * Unit tests for {@link ListSessionParser}.
 */
public class ListSessionParserTest {

    private ListSessionParser parser = new ListSessionParser();

    @Test
    @DisplayName("Parse valid args with class prefix succeeds")
    void parse_validArgs_returnsListSessionCommand() {
        ListSessionCommand expectedCommand = new ListSessionCommand("Math101");
        assertParseSuccess(parser, " c/Math101", expectedCommand);
    }

    @Test
    @DisplayName("Parse valid args with whitespace around class name succeeds")
    void parse_validArgsWithWhitespace_returnsListSessionCommand() {
        ListSessionCommand expectedCommand = new ListSessionCommand("Math101");
        assertParseSuccess(parser, " c/  Math101  ", expectedCommand);
    }

    @Test
    @DisplayName("Parse valid args with different class name succeeds")
    void parse_differentClassName_returnsListSessionCommand() {
        ListSessionCommand expectedCommand = new ListSessionCommand("Science101");
        assertParseSuccess(parser, " c/Science101", expectedCommand);
    }

    @Test
    @DisplayName("Parse valid args with complex class name succeeds")
    void parse_complexClassName_returnsListSessionCommand() {
        ListSessionCommand expectedCommand = new ListSessionCommand("CS2103T T12");
        assertParseSuccess(parser, " c/CS2103T T12", expectedCommand);
    }

    @Test
    @DisplayName("Parse fails when class prefix is missing")
    void parse_missingClassPrefix_throwsParseException() {
        assertParseFailure(parser, " Math101",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListSessionCommand.MESSAGE_USAGE));
    }

    @Test
    @DisplayName("Parse fails when no arguments provided")
    void parse_noArgs_throwsParseException() {
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListSessionCommand.MESSAGE_USAGE));
    }

    @Test
    @DisplayName("Parse fails when only whitespace provided")
    void parse_whitespaceOnly_throwsParseException() {
        assertParseFailure(parser, "   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListSessionCommand.MESSAGE_USAGE));
    }

    @Test
    @DisplayName("Parse fails when preamble is present")
    void parse_preamblePresent_throwsParseException() {
        assertParseFailure(parser, "some preamble c/Math101",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListSessionCommand.MESSAGE_USAGE));
    }

    @Test
    @DisplayName("Parse fails when class name is empty")
    void parse_emptyClassName_throwsParseException() {
        assertParseFailure(parser, " c/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListSessionCommand.MESSAGE_USAGE));
    }

    @Test
    @DisplayName("Parse fails when class name is only whitespace")
    void parse_whitespaceClassName_throwsParseException() {
        assertParseFailure(parser, " c/   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListSessionCommand.MESSAGE_USAGE));
    }

    @Test
    @DisplayName("arePrefixesPresent method works correctly for missing prefix")
    void arePrefixesPresent_missingPrefix_returnsFalse() {
        // This tests the arePrefixesPresent method indirectly
        assertParseFailure(parser, " Math101",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListSessionCommand.MESSAGE_USAGE));
    }

    @Test
    @DisplayName("Parse handles class names with special characters")
    void parse_classNameWithSpecialChars_returnsListSessionCommand() {
        ListSessionCommand expectedCommand = new ListSessionCommand("CS2103T-T12");
        assertParseSuccess(parser, " c/CS2103T-T12", expectedCommand);
    }

    @Test
    @DisplayName("Parse handles class names with numbers")
    void parse_classNameWithNumbers_returnsListSessionCommand() {
        ListSessionCommand expectedCommand = new ListSessionCommand("Math101A");
        assertParseSuccess(parser, " c/Math101A", expectedCommand);
    }

    @Test
    @DisplayName("Trim functionality works correctly")
    void parse_leadingTrailingSpaces_trimmedCorrectly() {
        // The parser should trim whitespace from the class name
        ListSessionCommand expectedCommand = new ListSessionCommand("Math101");
        assertParseSuccess(parser, " c/   Math101   ", expectedCommand);
    }
}
