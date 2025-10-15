package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

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
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditClassCommand.MESSAGE_USAGE));
        }

        String[] classNames = trimmedArgs.split("\\s+", 2);

        if (classNames.length != 2) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditClassCommand.MESSAGE_USAGE));
        }

        String oldClassName = classNames[0];
        String newClassName = classNames[1];

        return new EditClassCommand(oldClassName, newClassName);
    }
}