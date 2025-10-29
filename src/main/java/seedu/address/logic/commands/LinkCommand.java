package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CHILD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENT;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Parent;
import seedu.address.model.person.Person;
import seedu.address.model.person.Student;

/**
 * Links a parent and a child (student) in the address book, establishing a bidirectional relationship.
 */
public class LinkCommand extends Command {

    public static final String COMMAND_WORD = "link";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Links a parent to a child (student). "
            + "Parameters: "
            + PREFIX_PARENT + "PARENT_NAME "
            + PREFIX_CHILD + "CHILD_NAME\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_PARENT + "John Doe "
            + PREFIX_CHILD + "Jane Doe";

    public static final String MESSAGE_SUCCESS = "Successfully linked parent %s to child %s";
    public static final String MESSAGE_PARENT_NOT_EXIST = "This parent does not exist in the address book";
    public static final String MESSAGE_CHILD_NOT_EXIST = "This child does not exist in the address book";
    public static final String MESSAGE_NOT_PARENT = "The person specified is not a parent";
    public static final String MESSAGE_NOT_STUDENT = "The person specified is not a student";
    public static final String MESSAGE_ALREADY_LINKED = "This parent and child are already linked";

    private static final Logger LOGGER = LogsCenter.getLogger(LinkCommand.class);

    private final String parentName;
    private final String childName;

    /**
     * Creates a LinkCommand to link the specified parent to the specified child.
     *
     * @param parentName The name of the parent.
     * @param childName  The name of the child (student).
     */
    public LinkCommand(String parentName, String childName) {
        requireNonNull(parentName);
        requireNonNull(childName);
        this.parentName = parentName;
        this.childName = childName;
    }

    /**
     * Executes the link command to establish a bidirectional relationship between a parent and child.
     *
     * @param model The model which the command should operate on.
     * @return A CommandResult with the success message.
     * @throws CommandException If the parent or child does not exist, or if they're not the correct type.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        LOGGER.info("Executing LinkCommand to link parent: " + parentName + " with child: " + childName);

        // Find the parent by name
        List<Person> personList = model.getPersonList();
        Person parentPerson = personList.stream()
                .filter(p -> p.getName().fullName.equalsIgnoreCase(parentName))
                .findFirst()
                .orElse(null);

        if (parentPerson == null) {
            LOGGER.warning("Parent not found: " + parentName);
            throw new CommandException(MESSAGE_PARENT_NOT_EXIST);
        }

        if (!(parentPerson instanceof Parent)) {
            LOGGER.warning("Person is not a parent: " + parentName);
            throw new CommandException(MESSAGE_NOT_PARENT);
        }

        // Find the child by name
        Person childPerson = personList.stream()
                .filter(p -> p.getName().fullName.equalsIgnoreCase(childName))
                .findFirst()
                .orElse(null);

        if (childPerson == null) {
            LOGGER.warning("Child not found: " + childName);
            throw new CommandException(MESSAGE_CHILD_NOT_EXIST);
        }

        if (!(childPerson instanceof Student)) {
            LOGGER.warning("Person is not a student: " + childName);
            throw new CommandException(MESSAGE_NOT_STUDENT);
        }

        Parent parent = (Parent) parentPerson;
        Student child = (Student) childPerson;

        // Check if already linked
        if (parent.getChildren().contains(child)) {
            LOGGER.warning("Parent and child already linked: " + parentName + " - " + childName);
            throw new CommandException(MESSAGE_ALREADY_LINKED);
        }

        // Establish bidirectional relationship
        parent.addChild(child);
        LOGGER.info("Successfully linked parent: " + parentName + " with child: " + childName);

        return new CommandResult(String.format(MESSAGE_SUCCESS, parentName, childName));
    }

    /**
     * Checks if this LinkCommand is equal to another object.
     *
     * @param other The object to compare with.
     * @return True if both objects are LinkCommands with the same parent and child names.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof LinkCommand)) {
            return false;
        }

        LinkCommand otherLinkCommand = (LinkCommand) other;
        return parentName.equals(otherLinkCommand.parentName)
                && childName.equals(otherLinkCommand.childName);
    }

    /**
     * Returns a string representation of this LinkCommand.
     *
     * @return A string representation of this command.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("parentName", parentName)
                .add("childName", childName)
                .toString();
    }
}
