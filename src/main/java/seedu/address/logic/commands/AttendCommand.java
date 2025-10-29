package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SESSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classroom.ClassSession;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Name;
import seedu.address.model.person.Student;

/**
 * Marks attendance for a student in a specific class session.
 * The command updates the attendance record by marking the student as either
 * present or absent.
 */
public class AttendCommand extends Command {

    public static final String COMMAND_WORD = "attend";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Marks attendance for a student in a session. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_CLASS + "CLASS_NAME "
            + PREFIX_SESSION + "SESSION_NAME "
            + PREFIX_STATUS + "STATUS (PRESENT or ABSENT)\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_CLASS + "MATH101 "
            + PREFIX_SESSION + "Session 1 "
            + PREFIX_STATUS + "PRESENT";

    public static final String MESSAGE_SUCCESS = "Attendance marked: %1$s";
    public static final String MESSAGE_SESSION_NOT_FOUND = "Session '%s' not found in class '%s'";
    public static final String MESSAGE_STUDENT_NOT_FOUND = "Student is not found or not enrolled in the class: %s";

    private static final Logger LOGGER = LogsCenter.getLogger(AttendCommand.class);

    private final Name name;
    private final String className;
    private final String sessionName;
    private final Boolean present;

    /**
     * Creates an AttendCommand to mark attendance for the specified student.
     *
     * @param name        the name of the student whose attendance is being marked
     * @param className   the name of the class containing the session
     * @param sessionName the name of the session within the class
     * @param present     {@code true} if marking present, {@code false} if marking
     *                    absent
     * @throws NullPointerException if any parameter is null
     */
    public AttendCommand(Name name, String className, String sessionName, Boolean present) {
        this.name = requireNonNull(name, "name cannot be null");
        this.className = requireNonNull(className, "className cannot be null");
        this.sessionName = requireNonNull(sessionName, "sessionName cannot be null");
        this.present = requireNonNull(present, "present cannot be null");
    }

    /**
     * Executes the attend command to mark a student's attendance in a specific
     * session.
     *
     * @param model the model containing the class list and person list
     * @return the command result with success message
     * @throws CommandException     if the session or student cannot be found
     * @throws NullPointerException if model is null
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model, "model cannot be null");
        LOGGER.info("Executing AttendCommand for student: " + name + " in class: " + className
                + ", session: " + sessionName + ", status: " + (present ? "PRESENT" : "ABSENT"));

        // Find the class and session
        List<TuitionClass> classList = model.getFilteredClassList();
        TuitionClass tuitionClass = classList.stream()
                .filter(c -> c.getClassName().equalsIgnoreCase(this.className))
                .findFirst()
                .orElse(null);

        if (tuitionClass == null) {
            LOGGER.warning("Class not found: " + className);
            throw new CommandException(String.format(MESSAGE_SESSION_NOT_FOUND, sessionName, className));
        }

        ClassSession session = tuitionClass.getAllSessions().stream()
                .filter(s -> s.getSessionName().equalsIgnoreCase(this.sessionName))
                .findFirst()
                .orElse(null);

        if (session == null) {
            LOGGER.warning("Session not found: " + sessionName + " in class: " + className);
            throw new CommandException(String.format(MESSAGE_SESSION_NOT_FOUND, sessionName, className));
        }

        // Find the student by name
        Student student = session.getParentClass().getStudents().stream()
                .filter(s -> s.getName().equals(this.name))
                .findFirst()
                .orElse(null);

        if (student == null) {
            LOGGER.warning("Student not found or not enrolled: " + name + " in class: " + className);
            throw new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, name));
        }

        // Mark attendance based on present flag
        if (present) {
            session.markPresent(student);
        } else {
            session.markAbsent(student);
        }
        LOGGER.info("Successfully marked attendance for student: " + name + " as " + (present ? "PRESENT" : "ABSENT"));

        // Update just this session in the observable session list for UI refresh
        // The attendance data is automatically saved to JSON since session is part of tuitionClass
        model.setSession(session, session);

        String result = String.format("Name: %s, Class: %s, Session: %s, Status: %s",
                name, className, sessionName, present ? "PRESENT" : "ABSENT");

        return new CommandResult(String.format(MESSAGE_SUCCESS, result));
    }

    /**
     * Checks if this AttendCommand is equal to another object.
     *
     * @param other the object to compare with
     * @return {@code true} if both objects are AttendCommand instances with
     *         identical parameters
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AttendCommand)) {
            return false;
        }

        AttendCommand otherCommand = (AttendCommand) other;
        return name.equals(otherCommand.name)
                && className.equals(otherCommand.className)
                && sessionName.equals(otherCommand.sessionName)
                && present.equals(otherCommand.present);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("className", className)
                .add("sessionName", sessionName)
                .add("present", present)
                .toString();
    }
}
