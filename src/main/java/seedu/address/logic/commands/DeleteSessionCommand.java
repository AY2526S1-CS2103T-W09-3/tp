package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SESSION;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classroom.ClassSession;
import seedu.address.model.classroom.TuitionClass;

/**
 * Deletes a session from a class in the address book.
 */
public class DeleteSessionCommand extends Command {

    public static final String COMMAND_WORD = "delsession";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes a session from a class. "
            + "Parameters: "
            + PREFIX_CLASS + "CLASS_NAME "
            + PREFIX_SESSION + "SESSION_NAME\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CLASS + "Math101 "
            + PREFIX_SESSION + "Week 3 Tutorial";

    public static final String MESSAGE_SUCCESS = "Deleted session from class %s: %s";
    public static final String MESSAGE_CLASS_NOT_EXIST = "This class does not exist in the address book";
    public static final String MESSAGE_SESSION_NOT_FOUND = "Session '%s' does not exist in class '%s'";

    private final String className;
    private final String sessionName;

    /**
     * Creates a DeleteSessionCommand to delete the specified session from the specified class.
     *
     * @param className The name of the class.
     * @param sessionName The name of the session to delete.
     */
    public DeleteSessionCommand(String className, String sessionName) {
        requireNonNull(className, "Class name cannot be null");
        requireNonNull(sessionName, "Session name cannot be null");

        this.className = className.trim();
        this.sessionName = sessionName.trim();

        if (this.className.isEmpty()) {
            throw new IllegalArgumentException("Class name cannot be empty");
        }
        if (this.sessionName.isEmpty()) {
            throw new IllegalArgumentException("Session name cannot be empty");
        }
    }

    /**
     * Executes the delete session command to remove a session from a class.
     *
     * @param model The model which the command should operate on.
     * @return A CommandResult with the success message.
     * @throws CommandException If the class or session does not exist.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model, "Model cannot be null");

        // Find the class by name
        List<TuitionClass> classList = model.getFilteredClassList();
        TuitionClass tuitionClass = classList.stream()
                .filter(c -> c.getName().value.equals(className))
                .findFirst()
                .orElse(null);

        if (tuitionClass == null) {
            throw new CommandException(MESSAGE_CLASS_NOT_EXIST);
        }

        // Find the session by name (case-insensitive)
        ClassSession sessionToDelete = tuitionClass.getAllSessions().stream()
                .filter(s -> s.getSessionName().trim().equalsIgnoreCase(sessionName.trim()))
                .findFirst()
                .orElse(null);

        if (sessionToDelete == null) {
            throw new CommandException(String.format(MESSAGE_SESSION_NOT_FOUND, sessionName, className));
        }

        // Remove the session
        tuitionClass.removeSession(sessionToDelete);

        return new CommandResult(String.format(MESSAGE_SUCCESS, className, sessionToDelete.getSessionName()));
    }

    /**
     * Checks if this DeleteSessionCommand is equal to another object.
     *
     * @param other The object to compare with.
     * @return True if both objects are DeleteSessionCommands with the same parameters.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteSessionCommand)) {
            return false;
        }

        DeleteSessionCommand otherCommand = (DeleteSessionCommand) other;
        return className.equals(otherCommand.className)
                && sessionName.equals(otherCommand.sessionName);
    }

    /**
     * Returns a string representation of this DeleteSessionCommand.
     *
     * @return A string representation of this command.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("className", className)
                .add("sessionName", sessionName)
                .toString();
    }

    @Override
    public int hashCode() {
        return className.hashCode() ^ sessionName.hashCode();
    }
}
