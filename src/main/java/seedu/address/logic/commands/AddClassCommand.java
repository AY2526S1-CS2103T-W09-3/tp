package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classroom.ClassName;
import seedu.address.model.classroom.TuitionClass;

/**
 * Adds a new class (lesson/group) to TutBook.
 */
public class AddClassCommand extends Command {

    public static final String COMMAND_WORD = "addclass";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a new class to TutBook.\n"
            + "Parameters: cn/CLASS_NAME\n"
            + "Example: " + COMMAND_WORD + " cn/Sec2-Math-A";

    public static final String MESSAGE_SUCCESS = "New class added: %1$s";
    public static final String MESSAGE_DUPLICATE_CLASS = "This class already exists";

    private final String className;

    /**
     * Creates an AddClassCommand to add the specified {@code TuitionClass}
     */
    public AddClassCommand(String className) {
        requireNonNull(className);
        this.className = className;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        TuitionClass toAdd = new TuitionClass(new ClassName(className));
        if (model.hasClass(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_CLASS);
        }
        model.addClass(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd.getName().value));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof AddClassCommand
                && className.equals(((AddClassCommand) other).className));
    }
}
