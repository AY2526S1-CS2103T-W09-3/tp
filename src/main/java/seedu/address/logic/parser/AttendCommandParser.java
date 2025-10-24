package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SESSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import java.util.stream.Stream;

import seedu.address.logic.commands.AttendCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;

/**
 * Parses input arguments and creates a new AttendCommand object.
 * This parser validates that all required prefixes are present and contain valid values
 * before constructing the command.
 */
public class AttendCommandParser implements Parser<AttendCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AttendCommand
     * and returns an AttendCommand object for execution.
     *
     * @param args the user input string containing the command arguments
     * @return an AttendCommand object with parsed parameters
     * @throws ParseException if the user input does not conform to the expected format,
     *         contains duplicate prefixes, or has invalid parameter values
     */
    public AttendCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CLASS, PREFIX_SESSION, PREFIX_STATUS);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_CLASS, PREFIX_SESSION, PREFIX_STATUS)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CLASS, PREFIX_SESSION, PREFIX_STATUS);

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        String className = argMultimap.getValue(PREFIX_CLASS).get().trim();
        String sessionName = argMultimap.getValue(PREFIX_SESSION).get().trim();
        Boolean present = ParserUtil.parseAttendanceStatus(argMultimap.getValue(PREFIX_STATUS).get());

        return new AttendCommand(name, className, sessionName, present);
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
