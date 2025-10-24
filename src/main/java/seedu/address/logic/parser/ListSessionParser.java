package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;

import java.util.stream.Stream;

import seedu.address.logic.commands.ListSessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListSessionCommand object.
 * This parser validates that the class name prefix is present and contains a valid value.
 */
public class ListSessionParser implements Parser<ListSessionCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ListSessionCommand
     * and returns a ListSessionCommand object for execution.
     *
     * @param args the user input string containing the command arguments
     * @return a ListSessionCommand object with the parsed class name
     * @throws ParseException if the user input does not conform to the expected format,
     *         contains duplicate prefixes, or has an empty class name
     */
    public ListSessionCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_CLASS);

        if (!arePrefixesPresent(argMultimap, PREFIX_CLASS)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ListSessionCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CLASS);

        String className = argMultimap.getValue(PREFIX_CLASS).get().trim();

        if (className.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ListSessionCommand.MESSAGE_USAGE));
        }

        return new ListSessionCommand(className);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     *
     * @param argumentMultimap the argument multimap containing parsed command arguments
     * @param prefixes the prefixes to check for presence
     * @return {@code true} if all specified prefixes are present and have values, {@code false} otherwise
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
