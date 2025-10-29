package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Person;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;

/**
 * Adds a person to a class in the address book.
 */
public class JoinClassCommand extends Command {

    public static final String COMMAND_WORD = "join";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a student or tutor to a class. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_CLASS + "CLASS";

    public static final String MESSAGE_SUCCESS = "%s added to class %s: %s";
    public static final String MESSAGE_PERSON_NOT_EXIST = "This person does not exist in the address book";
    public static final String MESSAGE_CLASS_NOT_EXIST = "This class does not exist in the address book";
    public static final String MESSAGE_STUDENT_ALREADY_IN_CLASS = "This student is already in the class.";
    public static final String MESSAGE_TUTOR_ALREADY_ASSIGNED = "This tutor is already assigned to the class.";

    private static final Logger LOGGER = LogsCenter.getLogger(JoinClassCommand.class);

    private final String personName;
    private final String className;

    /**
     * private final String cls;
     *
     * /**
     * Creates a JoinClassCommand to add the specified {@code Person} to the
     * specified {@code Class}.
     *
     * @param person The person to add to the class.
     * @param cls    The class to which the person will be added.
     */
    public JoinClassCommand(String person, String cls) {
        requireNonNull(person);
        requireNonNull(cls);
        personName = person;
        className = cls;
    }

    /**
     * Executes the join class command to add a person to a class.
     *
     * @param model The model which the command should operate on.
     * @return A CommandResult with the success message.
     * @throws CommandException If the person does not exist in the address book.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        LOGGER.info("Executing JoinClassCommand for person: " + personName + " to class: " + className);

        // Find the person by name
        List<Person> personList = model.getPersonList();
        Person toJoin = personList.stream()
                .filter(p -> p.getName().fullName.equals(personName))
                .findFirst()
                .orElse(null);

        if (toJoin == null) {
            LOGGER.warning("Person not found: " + personName);
            throw new CommandException(MESSAGE_PERSON_NOT_EXIST);
        }

        // Find the class
        List<TuitionClass> classList = model.getFilteredClassList();
        TuitionClass tuitionClass = classList.stream()
                .filter(c -> c.getName().value.equalsIgnoreCase(className))
                .findFirst()
                .orElse(null);

        if (tuitionClass == null) {
            LOGGER.warning("Class not found: " + className);
            throw new CommandException(MESSAGE_CLASS_NOT_EXIST);
        }

        if (toJoin instanceof Student) {
            Student studentToJoin = (Student) toJoin;
            if (tuitionClass.hasStudent(studentToJoin)) {
                LOGGER.warning("Student already in class: " + personName + " in " + className);
                throw new CommandException(MESSAGE_STUDENT_ALREADY_IN_CLASS);
            }
            model.addStudentToClass(studentToJoin, tuitionClass);
            model.updateFilteredClassList(Model.PREDICATE_SHOW_ALL_CLASSES);
            LOGGER.info("Successfully added student " + personName + " to class " + className);
            return new CommandResult(String.format(MESSAGE_SUCCESS, "Student", className, personName),
                    CommandResult.DisplayType.CLASSES);
        } else if (toJoin instanceof Tutor) {
            Tutor tutorToJoin = (Tutor) toJoin;
            if (tuitionClass.hasTutor(tutorToJoin)) {
                LOGGER.warning("Tutor already assigned to class: " + personName + " in " + className);
                throw new CommandException(MESSAGE_TUTOR_ALREADY_ASSIGNED);
            }
            model.assignTutorToClass(tutorToJoin, tuitionClass);
            model.updateFilteredClassList(Model.PREDICATE_SHOW_ALL_CLASSES);
            LOGGER.info("Successfully assigned tutor " + personName + " to class " + className);
            return new CommandResult(String.format(MESSAGE_SUCCESS, "Tutor", className, personName),
                    CommandResult.DisplayType.CLASSES);
        } else {
            LOGGER.warning("Attempted to join non-student/tutor to class: " + personName);
            throw new CommandException("Only students and tutors can join classes.");
        }
    }

    /**
     * Checks if this JoinClassCommand is equal to another object.
     *
     * @param other The object to compare with.
     * @return True if both objects are JoinClassCommands with the same person and
     *         class.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof JoinClassCommand)) {
            return false;
        }

        JoinClassCommand otherJoinCommand = (JoinClassCommand) other;
        return personName.equals(otherJoinCommand.personName) && className.equals(otherJoinCommand.className);
    }

    /**
     * Returns a string representation of this JoinClassCommand.
     *
     * @return A string representation of this command.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toJoin", personName)
                .add("class", className)
                .toString();
    }
}
