package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.RemoveFromClassCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classroom.ClassName;

/**
 * Parses input arguments and creates a new RemoveFromClassCommand object
 */
public class RemoveFromClassCommandParser implements Parser<RemoveFromClassCommand> {

    private static final Logger logger = LogsCenter.getLogger(RemoveFromClassCommandParser.class);

    /**
     * Parses the given {@code String} of arguments in the context of the RemoveFromClassCommand
     * and returns a RemoveFromClassCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public RemoveFromClassCommand parse(String args) throws ParseException {
        requireNonNull(args);
        logger.fine("Parsing RemoveFromClassCommand with args: " + args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CLASS);

        if (!argMultimap.getValue(PREFIX_NAME).isPresent()
                || !argMultimap.getValue(PREFIX_CLASS).isPresent()
                || !argMultimap.getPreamble().isEmpty()) {
            logger.warning("Invalid command format for RemoveFromClassCommand");
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveFromClassCommand.MESSAGE_USAGE));
        }

        String personName = argMultimap.getValue(PREFIX_NAME).get().trim();
        String classNameValue = argMultimap.getValue(PREFIX_CLASS).get().trim();

        if (personName.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        "Person name cannot be empty. " + RemoveFromClassCommand.MESSAGE_USAGE));
        }

        if (classNameValue.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        "Class name cannot be empty. " + RemoveFromClassCommand.MESSAGE_USAGE));
        }

        String parsedClassName = ParserUtil.parseClassName(classNameValue);
        ClassName className = new ClassName(parsedClassName);
        logger.info("Parsed RemoveFromClassCommand for person: " + personName + " and class: " + className);

        return new RemoveFromClassCommand(personName, className);
    }
}
