package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemoveFromClassCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classroom.ClassName;

public class RemoveFromClassCommandParserTest {

    private final RemoveFromClassCommandParser parser = new RemoveFromClassCommandParser();

    // ========== Positive test cases ==========

    @Test
    public void parse_allFieldsPresent_success() throws Exception {
        String personName = "John Doe";
        String className = "Math 101";
        RemoveFromClassCommand result = parser.parse(" n/" + personName + " c/" + className);
        RemoveFromClassCommand expected = new RemoveFromClassCommand(personName, new ClassName(className));
        assertEquals(expected, result);
    }

    @Test
    public void parse_extraWhitespace_success() throws Exception {
        String personName = "John Doe";
        String className = "Math 101";
        RemoveFromClassCommand result = parser.parse("  n/  " + personName + "  c/  " + className + "  ");
        RemoveFromClassCommand expected = new RemoveFromClassCommand(personName, new ClassName(className));
        assertEquals(expected, result);
    }

    @Test
    public void parse_multipleNames_usesLast() throws Exception {
        String firstName = "John Doe";
        String lastName = "Jane Smith";
        String className = "Math 101";
        RemoveFromClassCommand result = parser.parse(" n/" + firstName + " n/" + lastName + " c/" + className);
        RemoveFromClassCommand expected = new RemoveFromClassCommand(lastName, new ClassName(className));
        assertEquals(expected, result);
    }

    @Test
    public void parse_multipleClasses_usesLast() throws Exception {
        String personName = "John Doe";
        String firstClass = "Math 101";
        String lastClass = "Science 202";
        RemoveFromClassCommand result = parser.parse(" n/" + personName + " c/" + firstClass + " c/" + lastClass);
        RemoveFromClassCommand expected = new RemoveFromClassCommand(personName, new ClassName(lastClass));
        assertEquals(expected, result);
    }

    @Test
    public void parse_namesWithSpecialCharacters_success() throws Exception {
        String personName = "John Smith 2nd";
        String className = "Math Advanced";
        RemoveFromClassCommand result = parser.parse(" n/" + personName + " c/" + className);
        RemoveFromClassCommand expected = new RemoveFromClassCommand(personName, new ClassName(className));
        assertEquals(expected, result);
    }

    @Test
    public void parse_longNames_success() throws Exception {
        String personName = "A".repeat(50);
        String className = "B".repeat(50);
        RemoveFromClassCommand result = parser.parse(" n/" + personName + " c/" + className);
        RemoveFromClassCommand expected = new RemoveFromClassCommand(personName, new ClassName(className));
        assertEquals(expected, result);
    }

    // ========== Negative test cases (should throw ParseException) ==========

    @Test
    public void parse_missingPersonName_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "removeClass: Removes a student or tutor from a class.\n"
                + "Parameters: n/PERSON_NAME c/CLASS_NAME\n"
                + "Example: removeClass n/John Doe c/Math 101", () ->
                parser.parse(" c/Math 101"));
    }

    @Test
    public void parse_missingClassName_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "removeClass: Removes a student or tutor from a class.\n"
                + "Parameters: n/PERSON_NAME c/CLASS_NAME\n"
                + "Example: removeClass n/John Doe c/Math 101", () ->
                parser.parse(" n/John Doe"));
    }

    @Test
    public void parse_missingBothFields_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "removeClass: Removes a student or tutor from a class.\n"
                + "Parameters: n/PERSON_NAME c/CLASS_NAME\n"
                + "Example: removeClass n/John Doe c/Math 101", () ->
                parser.parse(""));
    }

    @Test
    public void parse_emptyPersonName_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Person name cannot be empty. removeClass: Removes a student or tutor from a class.\n"
                + "Parameters: n/PERSON_NAME c/CLASS_NAME\n"
                + "Example: removeClass n/John Doe c/Math 101", () ->
                parser.parse(" n/ c/Math 101"));
    }

    @Test
    public void parse_emptyClassName_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Class name cannot be empty. removeClass: Removes a student or tutor from a class.\n"
                + "Parameters: n/PERSON_NAME c/CLASS_NAME\n"
                + "Example: removeClass n/John Doe c/Math 101", () ->
                parser.parse(" n/John Doe c/"));
    }

    @Test
    public void parse_emptyBothFields_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Person name cannot be empty. removeClass: Removes a student or tutor from a class.\n"
                + "Parameters: n/PERSON_NAME c/CLASS_NAME\n"
                + "Example: removeClass n/John Doe c/Math 101", () ->
                parser.parse(" n/ c/"));
    }

    @Test
    public void parse_whitespaceOnlyPersonName_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Person name cannot be empty. removeClass: Removes a student or tutor from a class.\n"
                + "Parameters: n/PERSON_NAME c/CLASS_NAME\n"
                + "Example: removeClass n/John Doe c/Math 101", () ->
                parser.parse(" n/   c/Math 101"));
    }

    @Test
    public void parse_whitespaceOnlyClassName_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "Class name cannot be empty. removeClass: Removes a student or tutor from a class.\n"
                + "Parameters: n/PERSON_NAME c/CLASS_NAME\n"
                + "Example: removeClass n/John Doe c/Math 101", () ->
                parser.parse(" n/John Doe c/   "));
    }

    @Test
    public void parse_wrongPrefixForName_throwsParseException() {
        assertThrows(ParseException.class, () ->
                parser.parse(" p/John Doe c/Math 101"));
    }

    @Test
    public void parse_wrongPrefixForClass_throwsParseException() {
        assertThrows(ParseException.class, () ->
                parser.parse(" n/John Doe cl/Math 101"));
    }

    @Test
    public void parse_invalidPreamble_throwsParseException() {
        assertThrows(ParseException.class, "Invalid command format! \n"
                + "removeClass: Removes a student or tutor from a class.\n"
                + "Parameters: n/PERSON_NAME c/CLASS_NAME\n"
                + "Example: removeClass n/John Doe c/Math 101", () ->
                parser.parse("extra text n/John Doe c/Math 101"));
    }

    @Test
    public void parse_noSpaceBeforePrefix_throwsParseException() {
        // Without leading space, preamble won't be empty
        assertThrows(ParseException.class, () ->
                parser.parse("n/John Doe c/Math 101"));
    }

    // ========== Edge cases and boundary tests ==========

    @Test
    public void parse_nullArg_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_singleCharacterNames_success() throws Exception {
        RemoveFromClassCommand result = parser.parse(" n/A c/B");
        RemoveFromClassCommand expected = new RemoveFromClassCommand("A", new ClassName("B"));
        assertEquals(expected, result);
    }

    @Test
    public void parse_casePreservation_success() throws Exception {
        String personName = "JoHn DoE";
        String className = "MaTh 101";
        RemoveFromClassCommand result = parser.parse(" n/" + personName + " c/" + className);
        RemoveFromClassCommand expected = new RemoveFromClassCommand(personName, new ClassName(className));
        assertEquals(expected, result);
    }

    @Test
    public void parse_leadingAndTrailingSpaces_trimmed() throws Exception {
        RemoveFromClassCommand result = parser.parse(" n/   John Doe   c/   Math 101   ");
        RemoveFromClassCommand expected = new RemoveFromClassCommand("John Doe", new ClassName("Math 101"));
        assertEquals(expected, result);
    }
}
