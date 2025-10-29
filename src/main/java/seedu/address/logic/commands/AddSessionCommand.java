package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SESSION;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classroom.ClassSession;
import seedu.address.model.classroom.TuitionClass;

/**
 * Adds a session to a class in the address book.
 */
public class AddSessionCommand extends Command {

    public static final String COMMAND_WORD = "addsession";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a session to a class. "
            + "Parameters: "
            + PREFIX_CLASS + "CLASS_NAME "
            + PREFIX_SESSION + "SESSION_NAME "
            + PREFIX_DATETIME + "DATETIME "
            + "[" + PREFIX_LOCATION + "LOCATION]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CLASS + "Math101 "
            + PREFIX_SESSION + "Week 3 Tutorial "
            + PREFIX_DATETIME + "2024-03-15 14:30 "
            + PREFIX_LOCATION + "COM1-B103";

    public static final String MESSAGE_SUCCESS = "New session added to class %s: %s";
    public static final String MESSAGE_CLASS_NOT_EXIST = "This class does not exist in the address book";
    public static final String MESSAGE_DUPLICATE_SESSION = "This session name already exists for this class";

    private static final Logger LOGGER = LogsCenter.getLogger(AddSessionCommand.class);

    private final String className;
    private final String sessionName;
    private final LocalDateTime dateTime;
    private final String location;

    /**
     * Creates an AddSessionCommand to add the specified session to the specified class.
     *
     * @param className   The name of the class.
     * @param sessionName The name of the session.
     * @param dateTime    The date and time of the session.
     * @param location    The location of the session (can be null).
     */
    public AddSessionCommand(String className, String sessionName, LocalDateTime dateTime, String location) {
        requireNonNull(className);
        requireNonNull(sessionName);
        requireNonNull(dateTime);
        this.className = className;
        this.sessionName = sessionName;
        this.dateTime = dateTime;
        this.location = location;
    }

    /**
     * Executes the add session command to add a session to a class.
     *
     * @param model The model which the command should operate on.
     * @return A CommandResult with the success message.
     * @throws CommandException If the class does not exist or session name already exists.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        LOGGER.info("Executing AddSessionCommand for session: " + sessionName + " in class: " + className
                + " at " + dateTime + (location != null ? " at " + location : ""));

        // Find the class by name
        List<TuitionClass> classList = model.getFilteredClassList();
        TuitionClass tuitionClass = classList.stream()
                .filter(c -> c.getName().value.equalsIgnoreCase(className))
                .findFirst()
                .orElse(null);

        if (tuitionClass == null) {
            LOGGER.warning("Class not found: " + className);
            throw new CommandException(MESSAGE_CLASS_NOT_EXIST);
        }

        // Check if session name already exists
        if (tuitionClass.hasSessionName(sessionName)) {
            LOGGER.warning("Duplicate session name: " + sessionName + " in class: " + className);
            throw new CommandException(MESSAGE_DUPLICATE_SESSION);
        }

        // Add session to the class
        ClassSession session = tuitionClass.addSession(sessionName, dateTime, location);
        LOGGER.info("Successfully added session: " + sessionName + " to class: " + className);

        // Update the UI by refreshing the session list
        model.setClass(tuitionClass, tuitionClass);
        model.updateSessionListForClass(tuitionClass);

        return new CommandResult(String.format(MESSAGE_SUCCESS, className, session.toString()),
                CommandResult.DisplayType.SESSIONS);
    }

    /**
     * Checks if this AddSessionCommand is equal to another object.
     *
     * @param other The object to compare with.
     * @return True if both objects are AddSessionCommands with the same parameters.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddSessionCommand)) {
            return false;
        }

        AddSessionCommand otherCommand = (AddSessionCommand) other;
        return className.equals(otherCommand.className)
                && sessionName.equals(otherCommand.sessionName)
                && dateTime.equals(otherCommand.dateTime)
                && ((location == null && otherCommand.location == null)
                        || (location != null && location.equals(otherCommand.location)));
    }

    /**
     * Returns a string representation of this AddSessionCommand.
     *
     * @return A string representation of this command.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("className", className)
                .add("sessionName", sessionName)
                .add("dateTime", dateTime)
                .add("location", location)
                .toString();
    }
}
