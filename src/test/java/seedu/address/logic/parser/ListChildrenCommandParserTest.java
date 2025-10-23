package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListChildrenCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ListChildrenCommandParserTest {

    private final ListChildrenCommandParser parser = new ListChildrenCommandParser();

    @Test
    public void parse_emptyArg_returnsListAllChildrenCommand() throws Exception {
        ListChildrenCommand result = parser.parse("");
        ListChildrenCommand expected = new ListChildrenCommand();

        assertEquals(expected, result);
    }

    @Test
    public void parse_whitespaceArg_returnsListAllChildrenCommand() throws Exception {
        ListChildrenCommand result = parser.parse("   ");
        ListChildrenCommand expected = new ListChildrenCommand();

        assertEquals(expected, result);
    }

    @Test
    public void parse_validParentName_returnsListChildrenCommand() throws Exception {
        ListChildrenCommand result = parser.parse("n/John Doe");
        ListChildrenCommand expected = new ListChildrenCommand("John Doe");

        assertEquals(expected, result);
    }

    @Test
    public void parse_validParentNameWithWhitespace_returnsListChildrenCommand() throws Exception {
        ListChildrenCommand result = parser.parse("  n/John Doe  ");
        ListChildrenCommand expected = new ListChildrenCommand("John Doe");

        assertEquals(expected, result);
    }

    @Test
    public void parse_emptyParentName_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("n/"));
    }

    @Test
    public void parse_emptyParentNameWithWhitespace_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("n/   "));
    }

    @Test
    public void parse_parentNameWithSpecialCharacters_returnsListChildrenCommand() throws Exception {
        ListChildrenCommand result = parser.parse("n/John O'Brien");
        ListChildrenCommand expected = new ListChildrenCommand("John O'Brien");

        assertEquals(expected, result);
    }

    @Test
    public void parse_textWithoutPrefix_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("some random text"));
    }

    @Test
    public void parse_multipleWordsWithoutPrefix_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("John Doe"));
    }
}
