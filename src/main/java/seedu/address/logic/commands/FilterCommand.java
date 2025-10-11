package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Placeholder for role-based filtering (MVP v1.3).
 */
public class FilterCommand extends Command {
    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Filters contacts by criteria.\n"
            + "For MVP v1.3, planned: filter ro/STUDENT|TUTOR|PARENT\n"
            + "Example: " + COMMAND_WORD + " ro/student";

    public static final String MESSAGE_NOT_IMPLEMENTED_YET =
            "Filter command (by role) not implemented yet (planned for v1.3).";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        throw new CommandException(MESSAGE_NOT_IMPLEMENTED_YET);
    }
}
