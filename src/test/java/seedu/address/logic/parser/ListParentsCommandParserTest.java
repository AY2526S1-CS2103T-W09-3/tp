package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListParentsCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ListParentsCommandParserTest {

    private final ListParentsCommandParser parser = new ListParentsCommandParser();

    // ========== Positive test cases ==========

    @Test
    public void parse_emptyArg_returnsListAllParentsCommand() throws Exception {
        ListParentsCommand result = parser.parse("");
        ListParentsCommand expected = new ListParentsCommand();
        assertEquals(expected, result);
        assertNotNull(result);
    }

    @Test
    public void parse_whitespaceOnlyArg_returnsListAllParentsCommand() throws Exception {
        ListParentsCommand result = parser.parse("   ");
        ListParentsCommand expected = new ListParentsCommand();
        assertEquals(expected, result);
    }

    @Test
    public void parse_validChildName_returnsListParentsCommand() throws Exception {
        String childName = "Alice Tan";
        ListParentsCommand result = parser.parse("n/" + childName);
        ListParentsCommand expected = new ListParentsCommand(childName);
        assertEquals(expected, result);
    }

    @Test
    public void parse_validChildNameWithExtraWhitespace_returnsListParentsCommand() throws Exception {
        ListParentsCommand result = parser.parse("  n/Alice Tan  ");
        ListParentsCommand expected = new ListParentsCommand("Alice Tan");
        assertEquals(expected, result);
    }

    @Test
    public void parse_childNameWithSpecialCharacters_returnsListParentsCommand() throws Exception {
        String childName = "Alice Smith 2nd";
        ListParentsCommand result = parser.parse("n/" + childName);
        ListParentsCommand expected = new ListParentsCommand(childName);
        assertEquals(expected, result);
    }

    @Test
    public void parse_childNameWithNumbers_returnsListParentsCommand() throws Exception {
        String childName = "Child 123";
        ListParentsCommand result = parser.parse("n/" + childName);
        ListParentsCommand expected = new ListParentsCommand(childName);
        assertEquals(expected, result);
    }

    @Test
    public void parse_childNameWithUnicodeCharacters_returnsListParentsCommand() throws Exception {
        String childName = "李明 (Li Ming)";
        ListParentsCommand result = parser.parse("n/" + childName);
        ListParentsCommand expected = new ListParentsCommand(childName);
        assertEquals(expected, result);
    }

    @Test
    public void parse_veryLongChildName_returnsListParentsCommand() throws Exception {
        String childName = "A".repeat(100);
        ListParentsCommand result = parser.parse("n/" + childName);
        ListParentsCommand expected = new ListParentsCommand(childName);
        assertEquals(expected, result);
    }

    // ========== Negative test cases (should throw ParseException) ==========

    @Test
    public void parse_emptyChildName_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Child name cannot be empty. parents: Lists all parents or parents of a specific child.\n"
                + "Parameters: [n/CHILD_NAME]\n"
                + "Examples:\n"
                + "  parents\n"
                + "  parents n/Jane Smith", () ->
                parser.parse("n/"));
    }

    @Test
    public void parse_emptyChildNameWithWhitespace_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Child name cannot be empty. parents: Lists all parents or parents of a specific child.\n"
                + "Parameters: [n/CHILD_NAME]\n"
                + "Examples:\n"
                + "  parents\n"
                + "  parents n/Jane Smith", () ->
                parser.parse("n/   "));
    }

    @Test
    public void parse_textWithoutPrefix_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Invalid syntax. parents: Lists all parents or parents of a specific child.\n"
                + "Parameters: [n/CHILD_NAME]\n"
                + "Examples:\n"
                + "  parents\n"
                + "  parents n/Jane Smith", () ->
                parser.parse("some random text"));
    }

    @Test
    public void parse_multipleWordsWithoutPrefix_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Invalid syntax. parents: Lists all parents or parents of a specific child.\n"
                + "Parameters: [n/CHILD_NAME]\n"
                + "Examples:\n"
                + "  parents\n"
                + "  parents n/Jane Smith", () ->
                parser.parse("Alice Tan"));
    }

    @Test
    public void parse_wrongPrefix_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Invalid syntax. parents: Lists all parents or parents of a specific child.\n"
                + "Parameters: [n/CHILD_NAME]\n"
                + "Examples:\n"
                + "  parents\n"
                + "  parents n/Jane Smith", () ->
                parser.parse("p/Alice Tan"));
    }

    @Test
    public void parse_multiplePrefixes_usesLastValue() throws Exception {
        // ArgumentTokenizer takes the last value when duplicates exist
        ListParentsCommand result = parser.parse(" n/Alice n/Bob");
        ListParentsCommand expected = new ListParentsCommand("Bob");
        assertEquals(expected, result);
    }

    @Test
    public void parse_prefixOnly_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Child name cannot be empty. parents: Lists all parents or parents of a specific child.\n"
                + "Parameters: [n/CHILD_NAME]\n"
                + "Examples:\n"
                + "  parents\n"
                + "  parents n/Jane Smith", () ->
                parser.parse("n/"));
    }

    @Test
    public void parse_textBeforePrefix_usesPrefix() throws Exception {
        // With space prepended, tokenizer accepts text before prefix
        ListParentsCommand result = parser.parse("invalid n/Alice");
        ListParentsCommand expected = new ListParentsCommand("Alice");
        assertEquals(expected, result);
    }

    // ========== Edge cases and boundary tests ==========

    @Test
    public void parse_nullArg_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_singleCharacterChildName_returnsListParentsCommand() throws Exception {
        ListParentsCommand result = parser.parse("n/A");
        ListParentsCommand expected = new ListParentsCommand("A");
        assertEquals(expected, result);
    }

    @Test
    public void parse_childNameWithLeadingSpaces_trimmed() throws Exception {
        ListParentsCommand result = parser.parse("n/   Alice Tan");
        ListParentsCommand expected = new ListParentsCommand("Alice Tan");
        assertEquals(expected, result);
    }

    @Test
    public void parse_childNameWithTrailingSpaces_trimmed() throws Exception {
        ListParentsCommand result = parser.parse("n/Alice Tan   ");
        ListParentsCommand expected = new ListParentsCommand("Alice Tan");
        assertEquals(expected, result);
    }

    @Test
    public void parse_casePreservation_returnsExactCase() throws Exception {
        String childName = "ALiCe TaN";
        ListParentsCommand result = parser.parse("n/" + childName);
        ListParentsCommand expected = new ListParentsCommand(childName);
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
