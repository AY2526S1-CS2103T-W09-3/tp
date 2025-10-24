package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddSessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class AddSessionCommandParserTest {

    private final AddSessionCommandParser parser = new AddSessionCommandParser();

    @Test
    public void parse_allFieldsPresent_success() throws Exception {
        String userInput = "c/Math101 session/Week 1 Tutorial dt/2024-03-15 14:30 lo/COM1-B103";
        AddSessionCommand expectedCommand = new AddSessionCommand(
                "Math101",
                "Week 1 Tutorial",
                LocalDateTime.of(2024, 3, 15, 14, 30),
                "COM1-B103"
        );

        AddSessionCommand result = parser.parse(userInput);
        assertEquals(expectedCommand, result);
    }

    @Test
    public void parse_optionalLocationMissing_success() throws Exception {
        String userInput = "c/Math101 session/Week 2 Tutorial dt/2024-03-16 10:00";
        AddSessionCommand expectedCommand = new AddSessionCommand(
                "Math101",
                "Week 2 Tutorial",
                LocalDateTime.of(2024, 3, 16, 10, 0),
                null
        );

        AddSessionCommand result = parser.parse(userInput);
        assertEquals(expectedCommand, result);
    }

    @Test
    public void parse_whitespaceInValues_trimmedCorrectly() throws Exception {
        String userInput = "c/  Math101  session/  Week 3 Tutorial  dt/2024-03-17 15:30 lo/  COM1-B104  ";
        AddSessionCommand expectedCommand = new AddSessionCommand(
                "Math101",
                "Week 3 Tutorial",
                LocalDateTime.of(2024, 3, 17, 15, 30),
                "COM1-B104"
        );

        AddSessionCommand result = parser.parse(userInput);
        assertEquals(expectedCommand, result);
    }

    @Test
    public void parse_missingClassName_throwsParseException() {
        String userInput = "session/Week 1 Tutorial dt/2024-03-15 14:30";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_missingSessionName_throwsParseException() {
        String userInput = "c/Math101 dt/2024-03-15 14:30";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_missingDateTime_throwsParseException() {
        String userInput = "c/Math101 session/Week 1 Tutorial";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_emptyClassName_throwsParseException() {
        String userInput = "c/   session/Week 1 Tutorial dt/2024-03-15 14:30";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_emptySessionName_throwsParseException() {
        String userInput = "c/Math101 session/   dt/2024-03-15 14:30";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_invalidDateTimeFormat_throwsParseException() {
        // Invalid format - should be yyyy-MM-dd HH:mm
        String userInput = "c/Math101 session/Week 1 dt/15-03-2024 14:30";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_invalidDateTimeFormatNoTime_throwsParseException() {
        // Missing time component
        String userInput = "c/Math101 session/Week 1 dt/2024-03-15";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_invalidDateTimeFormatWrongSeparator_throwsParseException() {
        // Using slash instead of hyphen for date
        String userInput = "c/Math101 session/Week 1 dt/2024/03/15 14:30";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_preamblePresent_throwsParseException() {
        String userInput = "some preamble c/Math101 session/Week 1 dt/2024-03-15 14:30";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_duplicateClassPrefix_throwsParseException() {
        String userInput = "c/Math101 c/Math102 session/Week 1 dt/2024-03-15 14:30";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_duplicateSessionPrefix_throwsParseException() {
        String userInput = "c/Math101 session/Week 1 session/Week 2 dt/2024-03-15 14:30";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_duplicateDateTimePrefix_throwsParseException() {
        String userInput = "c/Math101 session/Week 1 dt/2024-03-15 14:30 dt/2024-03-16 10:00";
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_duplicateLocationPrefix_throwsParseException() {
        String userInput = "c/Math101 session/Week 1 dt/2024-03-15 14:30 lo/COM1-B103 lo/COM1-B104";
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
    public void parse_validDateTimeEdgeCase_success() throws Exception {
        // Test edge case: single digit hour
        String userInput = "c/Math101 session/Early Morning dt/2024-01-01 09:05";
        AddSessionCommand expectedCommand = new AddSessionCommand(
                "Math101",
                "Early Morning",
                LocalDateTime.of(2024, 1, 1, 9, 5),
                null
        );

        AddSessionCommand result = parser.parse(userInput);
        assertEquals(expectedCommand, result);
    }

    @Test
    public void parse_emptyLocation_treatedAsNull() throws Exception {
        String userInput = "c/Math101 session/Week 1 dt/2024-03-15 14:30 lo/  ";
        AddSessionCommand expectedCommand = new AddSessionCommand(
                "Math101",
                "Week 1",
                LocalDateTime.of(2024, 3, 15, 14, 30),
                null
        );

        AddSessionCommand result = parser.parse(userInput);
        assertEquals(expectedCommand, result);
    }
}
