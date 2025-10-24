package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteSessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class DeleteSessionCommandParserTest {

    private final DeleteSessionCommandParser parser = new DeleteSessionCommandParser();

    @Test
    public void parse_allFieldsPresent_success() throws Exception {
        String userInput = "c/Math101 session/Week 1 Tutorial";
        DeleteSessionCommand expectedCommand = new DeleteSessionCommand("Math101", "Week 1 Tutorial");

        DeleteSessionCommand result = parser.parse(userInput);
        assertEquals(expectedCommand, result);
    }

    @Test
    public void parse_whitespaceInValues_trimmedCorrectly() throws Exception {
        String userInput = "c/  Math101  session/  Week 3 Tutorial  ";
        DeleteSessionCommand expectedCommand = new DeleteSessionCommand("Math101", "Week 3 Tutorial");

        DeleteSessionCommand result = parser.parse(userInput);
        assertEquals(expectedCommand, result);
    }

    @Test
    public void parse_missingClassName_throwsParseException() {
        String userInput = "session/Week 1 Tutorial";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_missingSessionName_throwsParseException() {
        String userInput = "c/Math101";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_emptyClassName_throwsParseException() {
        String userInput = "c/   session/Week 1 Tutorial";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_emptySessionName_throwsParseException() {
        String userInput = "c/Math101 session/   ";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_preamblePresent_throwsParseException() {
        String userInput = "some preamble c/Math101 session/Week 1";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_duplicateClassPrefix_throwsParseException() {
        String userInput = "c/Math101 c/Math102 session/Week 1";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_duplicateSessionPrefix_throwsParseException() {
        String userInput = "c/Math101 session/Week 1 session/Week 2";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(""));
    }

    @Test
    public void parse_whitespaceOnlyArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("   "));
    }

    @Test
    public void parse_validArgsWithExtraWhitespace_success() throws Exception {
        String userInput = "  c/Math101   session/Week 1 Tutorial  ";
        DeleteSessionCommand expectedCommand = new DeleteSessionCommand("Math101", "Week 1 Tutorial");

        DeleteSessionCommand result = parser.parse(userInput);
        assertEquals(expectedCommand, result);
    }

    @Test
    public void parse_sessionNameWithSpecialCharacters_success() throws Exception {
        String userInput = "c/Math101 session/Week 1 - Advanced Tutorial (Part A)";
        DeleteSessionCommand expectedCommand = new DeleteSessionCommand("Math101",
                "Week 1 - Advanced Tutorial (Part A)");

        DeleteSessionCommand result = parser.parse(userInput);
        assertEquals(expectedCommand, result);
    }

    @Test
    public void parse_classNameWithSpaces_success() throws Exception {
        String userInput = "c/Advanced Math 101 session/Week 1 Tutorial";
        DeleteSessionCommand expectedCommand = new DeleteSessionCommand("Advanced Math 101", "Week 1 Tutorial");

        DeleteSessionCommand result = parser.parse(userInput);
        assertEquals(expectedCommand, result);
    }
}
