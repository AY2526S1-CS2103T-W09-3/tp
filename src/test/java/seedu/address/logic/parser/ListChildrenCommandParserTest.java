package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListChildrenCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ListChildrenCommandParserTest {

    private final ListChildrenCommandParser parser = new ListChildrenCommandParser();

    // ========== Positive test cases ==========

    @Test
    public void parse_emptyArg_returnsListAllChildrenCommand() throws Exception {
        ListChildrenCommand result = parser.parse("");
        ListChildrenCommand expected = new ListChildrenCommand();
        assertEquals(expected, result);
        assertNotNull(result);
    }

    @Test
    public void parse_whitespaceOnlyArg_returnsListAllChildrenCommand() throws Exception {
        ListChildrenCommand result = parser.parse("   ");
        ListChildrenCommand expected = new ListChildrenCommand();
        assertEquals(expected, result);
    }

    @Test
    public void parse_validParentName_returnsListChildrenCommand() throws Exception {
        String parentName = "John Doe";
        ListChildrenCommand result = parser.parse("n/" + parentName);
        ListChildrenCommand expected = new ListChildrenCommand(parentName);
        assertEquals(expected, result);
    }

    @Test
    public void parse_validParentNameWithExtraWhitespace_returnsListChildrenCommand() throws Exception {
        ListChildrenCommand result = parser.parse("  n/John Doe  ");
        ListChildrenCommand expected = new ListChildrenCommand("John Doe");
        assertEquals(expected, result);
    }

    @Test
    public void parse_parentNameWithSpecialCharacters_returnsListChildrenCommand() throws Exception {
        String parentName = "John O'Brien-Smith";
        ListChildrenCommand result = parser.parse("n/" + parentName);
        ListChildrenCommand expected = new ListChildrenCommand(parentName);
        assertEquals(expected, result);
    }

    @Test
    public void parse_parentNameWithNumbers_returnsListChildrenCommand() throws Exception {
        String parentName = "Parent 123";
        ListChildrenCommand result = parser.parse("n/" + parentName);
        ListChildrenCommand expected = new ListChildrenCommand(parentName);
        assertEquals(expected, result);
    }

    @Test
    public void parse_parentNameWithUnicodeCharacters_returnsListChildrenCommand() throws Exception {
        String parentName = "李明 (Li Ming)";
        ListChildrenCommand result = parser.parse("n/" + parentName);
        ListChildrenCommand expected = new ListChildrenCommand(parentName);
        assertEquals(expected, result);
    }

    @Test
    public void parse_veryLongParentName_returnsListChildrenCommand() throws Exception {
        String parentName = "A".repeat(100);
        ListChildrenCommand result = parser.parse("n/" + parentName);
        ListChildrenCommand expected = new ListChildrenCommand(parentName);
        assertEquals(expected, result);
    }

    // ========== Negative test cases (should throw ParseException) ==========

    @Test
    public void parse_emptyParentName_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Parent name cannot be empty. " + ListChildrenCommand.MESSAGE_USAGE, () ->
                parser.parse("n/"));
    }

    @Test
    public void parse_emptyParentNameWithWhitespace_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Parent name cannot be empty. " + ListChildrenCommand.MESSAGE_USAGE, () ->
                parser.parse("n/   "));
    }

    @Test
    public void parse_textWithoutPrefix_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Invalid syntax. " + ListChildrenCommand.MESSAGE_USAGE, () ->
                parser.parse("some random text"));
    }

    @Test
    public void parse_multipleWordsWithoutPrefix_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Invalid syntax. " + ListChildrenCommand.MESSAGE_USAGE, () ->
                parser.parse("John Doe"));
    }

    @Test
    public void parse_wrongPrefix_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Invalid syntax. " + ListChildrenCommand.MESSAGE_USAGE, () ->
                parser.parse("p/John Doe"));
    }

    @Test
    public void parse_multiplePrefixes_usesLastValue() throws Exception {
        // ArgumentTokenizer takes the last value when duplicates exist
        ListChildrenCommand result = parser.parse(" n/John n/Jane");
        ListChildrenCommand expected = new ListChildrenCommand("Jane");
        assertEquals(expected, result);
    }

    @Test
    public void parse_prefixOnly_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Parent name cannot be empty. " + ListChildrenCommand.MESSAGE_USAGE, () ->
                parser.parse("n/"));
    }

    @Test
    public void parse_textBeforePrefix_usesPrefix() throws Exception {
        // With space prepended, tokenizer accepts text before prefix
        ListChildrenCommand result = parser.parse("invalid n/John");
        ListChildrenCommand expected = new ListChildrenCommand("John");
        assertEquals(expected, result);
    }

    // ========== Edge cases and boundary tests ==========

    @Test
    public void parse_nullArg_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_singleCharacterParentName_returnsListChildrenCommand() throws Exception {
        ListChildrenCommand result = parser.parse("n/A");
        ListChildrenCommand expected = new ListChildrenCommand("A");
        assertEquals(expected, result);
    }

    @Test
    public void parse_parentNameWithLeadingSpaces_trimmed() throws Exception {
        ListChildrenCommand result = parser.parse("n/   John Doe");
        ListChildrenCommand expected = new ListChildrenCommand("John Doe");
        assertEquals(expected, result);
    }

    @Test
    public void parse_parentNameWithTrailingSpaces_trimmed() throws Exception {
        ListChildrenCommand result = parser.parse("n/John Doe   ");
        ListChildrenCommand expected = new ListChildrenCommand("John Doe");
        assertEquals(expected, result);
    }

    @Test
    public void parse_casePreservation_returnsExactCase() throws Exception {
        String parentName = "JoHn DoE";
        ListChildrenCommand result = parser.parse("n/" + parentName);
        ListChildrenCommand expected = new ListChildrenCommand(parentName);
        assertEquals(expected, result);
    }

    // ========== Integration with error messages ==========

    @Test
    public void parse_invalidInput_containsUsageMessage() {
        assertThrows(ParseException.class, () -> parser.parse("invalid"));
    }

    @Test
    public void parse_emptyNameAfterPrefix_containsUsageMessage() {
        assertThrows(ParseException.class, () -> parser.parse("n/"));
    }

    @Test
    public void parse_invalidSyntax_containsCorrectErrorFormat() {
        assertThrows(ParseException.class, () -> parser.parse("wrong syntax"));
    }
}
