package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.ListChildrenCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListChildrenCommand object.
 */
public class ListChildrenCommandParser implements Parser<ListChildrenCommand> {

    private static final Logger logger = LogsCenter.getLogger(ListChildrenCommandParser.class);

    @Override
    public ListChildrenCommand parse(String args) throws ParseException {
        requireNonNull(args);
        logger.fine("Parsing ListChildrenCommand with args: " + args);

        String trimmed = args.trim();

        if (trimmed.isEmpty()) {
            return new ListChildrenCommand();
        }

        ArgumentMultimap map = ArgumentTokenizer.tokenize(" " + trimmed, PREFIX_NAME);

        // Check if the input contains the n/ prefix
        if (map.getValue(PREFIX_NAME).isPresent()) {
            String parentName = map.getValue(PREFIX_NAME).get().trim();

            if (parentName.isEmpty()) {
                logger.warning("Parent name is empty in ListChildrenCommand");
                throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        "Parent name cannot be empty. " + ListChildrenCommand.MESSAGE_USAGE));
            }

            logger.info("Parsed ListChildrenCommand for parent: " + parentName);
            return new ListChildrenCommand(parentName);
        }

        // If there's input but no n/ prefix, it's invalid syntax
        if (!trimmed.isEmpty()) {
            logger.warning("Invalid syntax in ListChildrenCommand: " + trimmed);
            throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    "Invalid syntax. " + ListChildrenCommand.MESSAGE_USAGE));
        }

        return new ListChildrenCommand();
    }
}
