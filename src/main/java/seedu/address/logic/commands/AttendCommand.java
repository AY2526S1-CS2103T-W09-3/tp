package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SESSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;

/**
 * Marks attendance for a person in a specific session.
 */
public class AttendCommand extends Command {

    public static final String COMMAND_WORD = "attend";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Marks attendance for a person in a session. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_SESSION + "SESSION_ID "
            + PREFIX_STATUS + "STATUS (PRESENT or ABSENT)\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_SESSION + "1 "
            + PREFIX_STATUS + "PRESENT";

    public static final String MESSAGE_SUCCESS = "Attendance marked: %1$s";

    private final Name name;
    private final String sessionId;
    private final String status;

    /**
     * Creates an AttendCommand to mark attendance for the specified person.
     * Note: sessionId should ideally reference the ID of a Session instance in the model.
     * Future implementation should replace String sessionId with a proper Session object
     * or validate that the sessionId corresponds to an existing Session.
     */
    public AttendCommand(Name name, String sessionId, String status) {
        requireNonNull(name);
        requireNonNull(sessionId);
        requireNonNull(status);
        this.name = name;
        this.sessionId = sessionId;
        this.status = status;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // TODO: Implement actual attendance marking logic
        String result = String.format("Name: %s, Session: %s, Status: %s",
                name, sessionId, status);

        return new CommandResult(String.format(MESSAGE_SUCCESS, result));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AttendCommand)) {
            return false;
        }

        AttendCommand otherCommand = (AttendCommand) other;
        return name.equals(otherCommand.name)
                && sessionId.equals(otherCommand.sessionId)
                && status.equals(otherCommand.status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("sessionId", sessionId)
                .add("status", status)
                .toString();
    }
}
