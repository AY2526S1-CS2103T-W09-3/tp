package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Parent;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonType;
import seedu.address.model.person.Student;

/**
 * Lists all parents in the address book to the user.
 * Optionally filters by child name if provided.
 */
public class ListParentsCommand extends Command {

    public static final String COMMAND_WORD = "parents";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all parents or parents of a specific child.\n"
            + "Parameters: [" + PREFIX_NAME + "CHILD_NAME]\n"
            + "Examples:\n"
            + "  " + COMMAND_WORD + "\n"
            + "  " + COMMAND_WORD + " " + PREFIX_NAME + "Jane Smith";

    public static final String MESSAGE_SUCCESS = "Listed all parents";
    public static final String MESSAGE_SUCCESS_FILTERED = "Listed parents for child: %s";
    public static final String MESSAGE_CHILD_NOT_FOUND = "Child not found: %s";

    private static final Logger logger = LogsCenter.getLogger(ListParentsCommand.class);

    private final String childName;

    /**
     * Creates a ListParentsCommand to list all parents.
     */
    public ListParentsCommand() {
        this.childName = null;
    }

    /**
     * Creates a ListParentsCommand to list parents of a specific child.
     */
    public ListParentsCommand(String childName) {
        this.childName = childName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        logger.info("Executing ListParentsCommand" + (childName != null ? " for child: " + childName : ""));

        if (childName == null) {
            return listAllParents(model);
        } else {
            return listParentsByChild(model);
        }
    }

    /**
     * Lists all parents in the address book.
     */
    private CommandResult listAllParents(Model model) {
        String output = model.getFilteredPersonList().stream()
                .filter(person -> person.getPersonType() == PersonType.PARENT)
                .map(Person::getName)
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        if (output.isEmpty()) {
            output = "[No parents]";
        }

        return new CommandResult(MESSAGE_SUCCESS + "\n" + output);
    }

    /**
     * Lists parents for a specific child.
     */
    private CommandResult listParentsByChild(Model model) throws CommandException {
        List<Person> personList = model.getFilteredPersonList();

        Student child = personList.stream()
                .filter(p -> p.getPersonType() == PersonType.STUDENT)
                .filter(p -> p.getName().fullName.equalsIgnoreCase(childName))
                .map(p -> (Student) p)
                .findFirst()
                .orElse(null);

        if (child == null) {
            logger.warning("Child not found: " + childName);
            throw new CommandException(String.format(MESSAGE_CHILD_NOT_FOUND, childName));
        }

        List<Parent> parents = child.getParents();

        String output;
        if (parents.isEmpty()) {
            output = "[No parents]";
        } else {
            output = parents.stream()
                    .map(parent -> parent.getName().fullName)
                    .collect(Collectors.joining(", "));
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS_FILTERED, childName) + "\n" + output);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ListParentsCommand)) {
            return false;
        }
        ListParentsCommand o = (ListParentsCommand) other;
        return (childName == null && o.childName == null)
                || (childName != null && childName.equals(o.childName));
    }
}
