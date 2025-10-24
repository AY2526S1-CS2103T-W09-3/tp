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
 * Lists all children in the address book to the user.
 * Optionally filters by parent name if provided.
 */
public class ListChildrenCommand extends Command {

    public static final String COMMAND_WORD = "children";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all children or children of a specific parent.\n"
            + "Parameters: [" + PREFIX_NAME + "PARENT_NAME]\n"
            + "Examples:\n"
            + "  " + COMMAND_WORD + "\n"
            + "  " + COMMAND_WORD + " " + PREFIX_NAME + "John Doe";

    public static final String MESSAGE_SUCCESS = "Listed all children";
    public static final String MESSAGE_SUCCESS_FILTERED = "Listed children for parent: %s";
    public static final String MESSAGE_PARENT_NOT_FOUND = "Parent not found: %s";

    private static final Logger logger = LogsCenter.getLogger(ListChildrenCommand.class);

    private final String parentName;

    /**
     * Creates a ListChildrenCommand to list all children.
     */
    public ListChildrenCommand() {
        this.parentName = null;
    }

    /**
     * Creates a ListChildrenCommand to list children of a specific parent.
     */
    public ListChildrenCommand(String parentName) {
        this.parentName = parentName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        logger.info("Executing ListChildrenCommand" + (parentName != null ? " for parent: " + parentName : ""));

        if (parentName == null) {
            return listAllChildren(model);
        } else {
            return listChildrenByParent(model);
        }
    }

    /**
     * Lists all children in the address book.
     */
    private CommandResult listAllChildren(Model model) {
        String output = model.getPersonList().stream()
                .filter(person -> person.getPersonType() == PersonType.STUDENT)
                .map(Person::getName)
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        if (output.isEmpty()) {
            output = "[No children]";
        }

        return new CommandResult(MESSAGE_SUCCESS + "\n" + output);
    }

    /**
     * Lists children for a specific parent.
     */
    private CommandResult listChildrenByParent(Model model) throws CommandException {
        List<Person> personList = model.getPersonList();

        Parent parent = personList.stream()
                .filter(p -> p.getPersonType() == PersonType.PARENT)
                .filter(p -> p.getName().fullName.equalsIgnoreCase(parentName))
                .map(p -> (Parent) p)
                .findFirst()
                .orElse(null);

        if (parent == null) {
            logger.warning("Parent not found: " + parentName);
            throw new CommandException(String.format(MESSAGE_PARENT_NOT_FOUND, parentName));
        }

        List<Student> children = parent.getChildren();

        String output;
        if (children.isEmpty()) {
            output = "[No children]";
        } else {
            output = children.stream()
                    .map(student -> student.getName().fullName)
                    .collect(Collectors.joining(", "));
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS_FILTERED, parentName) + "\n" + output);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ListChildrenCommand)) {
            return false;
        }
        ListChildrenCommand o = (ListChildrenCommand) other;
        return (parentName == null && o.parentName == null)
                || (parentName != null && parentName.equals(o.parentName));
    }
}
