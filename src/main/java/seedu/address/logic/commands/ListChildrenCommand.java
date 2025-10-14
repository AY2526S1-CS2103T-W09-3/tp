package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.stream.Collectors;

import seedu.address.model.Model;
import seedu.address.model.person.PersonType;
import seedu.address.model.person.Student;
import seedu.address.model.person.Person;

/**
 * Lists all children in the address book to the user.
 */
public class ListChildrenCommand extends Command {

    public static final String COMMAND_WORD = "children";
    public static final String MESSAGE_SUCCESS = "Listed all children";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        String output = model.getFilteredPersonList().stream()
                .filter(person -> person.getPersonType() == PersonType.STUDENT)
                .map(Person::getName)
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        if (output.isEmpty()) {
            output = "[No children]";
        }

        return new CommandResult(MESSAGE_SUCCESS + "\n" + output);
    }
}
