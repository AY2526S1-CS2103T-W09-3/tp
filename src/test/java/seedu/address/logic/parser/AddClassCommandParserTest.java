package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddClassCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class AddClassCommandParserTest {

    private AddClassCommandParser parser = new AddClassCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("  "));
    }

    @Test
    public void parse_validArgs_returnsAddClassCommand() throws Exception {
        // no leading and trailing whitespaces
        AddClassCommand expectedAddClassCommand = new AddClassCommand("Sec1-Math-A");
        assertEquals(expectedAddClassCommand, parser.parse("Sec1-Math-A"));

        // multiple whitespaces between words
        assertEquals(expectedAddClassCommand, parser.parse("   Sec1-Math-A   "));
    }
}
