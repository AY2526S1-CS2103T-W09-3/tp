package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.logic.commands.ListParentsCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListParentsCommand object.
 */
public class ListParentsCommandParser implements Parser<ListParentsCommand> {

    @Override
    public ListParentsCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String trimmed = args.trim();

        if (trimmed.isEmpty()) {
            return new ListParentsCommand();
        }

        ArgumentMultimap map = ArgumentTokenizer.tokenize(" " + trimmed, PREFIX_NAME);

        if (map.getValue(PREFIX_NAME).isPresent()) {
            String childName = map.getValue(PREFIX_NAME).get().trim();

            if (childName.isEmpty()) {
                throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        "Child name cannot be empty. " + ListParentsCommand.MESSAGE_USAGE));
            }

            return new ListParentsCommand(childName);
        }

        if (!trimmed.isEmpty()) {
            throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    "Invalid syntax. " + ListParentsCommand.MESSAGE_USAGE));
        }

        return new ListParentsCommand();
    }
}
