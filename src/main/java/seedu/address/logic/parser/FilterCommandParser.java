package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input for {@code FilterCommand}.
 * S1: accept args but defer validation/logic to v1.3.
 */
public class FilterCommandParser implements Parser<FilterCommand> {
    @Override
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        // In v1.3: tokenize(args, PREFIX_ROLE) and validate role.
        // For S1, we simply return a stub command.
        ArgumentTokenizer.tokenize(args, PREFIX_ROLE); // no-op to reserve syntax shape
        return new FilterCommand();
    }
}
