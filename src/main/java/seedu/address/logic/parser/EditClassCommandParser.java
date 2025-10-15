package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_OLD_CLASS;

import seedu.address.logic.commands.EditClassCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditClassCommand object
 */
public class EditClassCommandParser implements Parser<EditClassCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditClassCommand
     * and returns an EditClassCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditClassCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_OLD_CLASS, PREFIX_CLASS);

        if (!argMultimap.getValue(PREFIX_OLD_CLASS).isPresent()
                || !argMultimap.getValue(PREFIX_CLASS).isPresent()
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditClassCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_OLD_CLASS, PREFIX_CLASS);

        String oldClassName = argMultimap.getValue(PREFIX_OLD_CLASS).get();
        String newClassName = argMultimap.getValue(PREFIX_CLASS).get();

        return new EditClassCommand(oldClassName, newClassName);
    }
}
