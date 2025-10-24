package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classroom.ClassSession;
import seedu.address.model.classroom.TuitionClass;

/**
 * Lists all sessions for a specific tuition class.
 */
public class ListSessionCommand extends Command {

    public static final String COMMAND_WORD = "sessions";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all sessions for a specific class.\n"
            + "Parameters: " + PREFIX_CLASS + "CLASS_NAME\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_CLASS + "Math101";

    public static final String MESSAGE_SUCCESS = "Listed all sessions for class: %s";
    public static final String MESSAGE_CLASS_NOT_FOUND = "Class not found: %s";

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final String className;

    /**
     * Creates a ListSessionCommand to list all sessions for a specific class.
     *
     * @param className the name of the class whose sessions should be listed
     * @throws NullPointerException if className is null
     */
    public ListSessionCommand(String className) {
        this.className = requireNonNull(className, "className cannot be null");
    }

    /**
     * Executes the list session command to display all sessions for the specified class.
     *
     * @param model the model containing the class list
     * @return the command result with the list of sessions
     * @throws CommandException if the class is not found
     * @throws NullPointerException if model is null
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model, "model cannot be null");

        List<TuitionClass> classList = model.getFilteredClassList();

        TuitionClass tuitionClass = classList.stream()
                .filter(c -> c.getClassName().equalsIgnoreCase(className))
                .findFirst()
                .orElse(null);

        if (tuitionClass == null) {
            throw new CommandException(String.format(MESSAGE_CLASS_NOT_FOUND, className));
        }

        List<ClassSession> sessions = tuitionClass.getAllSessions();

        String output;
        if (sessions.isEmpty()) {
            output = "[No sessions]";
        } else {
            output = sessions.stream()
                    .map(this::formatSession)
                    .collect(Collectors.joining("\n"));
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, className) + "\n" + output);
    }

    /**
     * Formats a session for display.
     *
     * @param session the session to format
     * @return a formatted string representation of the session
     */
    private String formatSession(ClassSession session) {
        return String.format("- %s | %s | %s",
                session.getSessionName(),
                session.getDateTime().format(DATE_TIME_FORMATTER),
                session.getLocation());
    }

    /**
     * Checks if this ListSessionCommand is equal to another object.
     *
     * @param other the object to compare with
     * @return {@code true} if both objects are ListSessionCommand instances
     *         with the same class name (case-insensitive)
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ListSessionCommand o)) {
            return false;
        }
        return className.equalsIgnoreCase(o.className);
    }

    @Override
    public int hashCode() {
        return className.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return String.format("ListSessionCommand[className=%s]", className);
    }
}
