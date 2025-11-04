package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PERSON_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Parent;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonType;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;
import seedu.address.model.tag.Tag;


/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD
                    + ": Edits the details of the person identified "
                    + "by the index number used in the displayed person list. "
                    + "Existing values will be overwritten by the input values.\n"
                    + "Parameters: INDEX (must be a positive integer) " + "["
                    + PREFIX_NAME + "NAME] " + "[" + PREFIX_PHONE + "PHONE] "
                    + "[" + PREFIX_EMAIL + "EMAIL] " + "[" + PREFIX_ADDRESS
                    + "ADDRESS] " + "[" + PREFIX_PERSON_TYPE + "ROLE] " + "[" + PREFIX_TAG + "TAG]...\n" + "Example: "
                    + COMMAND_WORD + " 1 " + PREFIX_PHONE + "91234567 "
                    + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MESSAGE_TYPE_CHANGE_DATA_LOSS = "Warning: Changing from %s to %s will delete:\n%s"
            + "\nTo confirm this change, add --force to your command.\n"
            + "Example: edit 1 ro/tutor --force";

    private static final Logger LOGGER = LogsCenter.getLogger(EditCommand.class);

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;
    private final boolean isForced;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        this(index, editPersonDescriptor, false);
    }

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     * @param isForced whether to force the edit even if it causes data loss
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor, boolean isForced) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
        this.isForced = isForced;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        LOGGER.info("Executing EditCommand for index: " + index.getOneBased());
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            LOGGER.warning("Invalid person index: " + index.getOneBased() + ", list size: " + lastShownList.size());
            throw new CommandException(
                            Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        LOGGER.info("Editing person: " + personToEdit.getName());
        Person editedPerson = createEditedPerson(personToEdit,
                        editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson)
                        && model.hasPerson(editedPerson)) {
            LOGGER.warning("Attempted to edit person to duplicate: " + editedPerson.getName());
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        // Check for data loss when changing person type
        if (editPersonDescriptor.getPersonType().isPresent()
                && !personToEdit.getPersonType().equals(editPersonDescriptor.getPersonType().get())) {
            String dataLossWarning = checkDataLoss(personToEdit, editPersonDescriptor.getPersonType().get());
            if (!dataLossWarning.isEmpty() && !isForced) {
                throw new CommandException(String.format(MESSAGE_TYPE_CHANGE_DATA_LOSS,
                        personToEdit.getPersonType(),
                        editPersonDescriptor.getPersonType().get(),
                        dataLossWarning));
            }
        }

        model.setPerson(personToEdit, editedPerson);

        LOGGER.info("Successfully edited person: " + personToEdit.getName() + " -> " + editedPerson.getName());

        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS,
                        Messages.format(editedPerson)));
    }

    private String checkDataLoss(Person person, PersonType newType) {
        StringBuilder dataLoss = new StringBuilder();

        if (person instanceof Student && !newType.equals(PersonType.STUDENT)) {
            Student student = (Student) person;
            if (!student.getParents().isEmpty()) {
                dataLoss.append("• ").append(student.getParents().size())
                        .append(" parent relationship(s)\n");
            }
            if (!student.getTuitionClasses().isEmpty()) {
                dataLoss.append("• ").append(student.getTuitionClasses().size())
                        .append(" class enrollment(s)\n");
            }
        }

        if (person instanceof Parent && !newType.equals(PersonType.PARENT)) {
            Parent parent = (Parent) person;
            if (!parent.getChildren().isEmpty()) {
                dataLoss.append("• ").append(parent.getChildren().size())
                        .append(" child relationship(s)\n");
            }
        }

        if (person instanceof Tutor && !newType.equals(PersonType.TUTOR)) {
            Tutor tutor = (Tutor) person;
            if (!tutor.getTuitionClasses().isEmpty()) {
                dataLoss.append("• ").append(tutor.getTuitionClasses().size())
                        .append(" class assignment(s)\n");
            }
        }

        return dataLoss.toString();
    }

    /**
     * Creates and returns a {@code Person} with the details of
     * {@code personToEdit} edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit,
                    EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName()
                        .orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone()
                        .orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail()
                        .orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress()
                        .orElse(personToEdit.getAddress());
        Set<Tag> updatedTags = editPersonDescriptor.getTags()
                        .orElse(personToEdit.getTags());
        PersonType role = editPersonDescriptor.getPersonType()
                        .orElse(personToEdit.getPersonType());

        return Person.newPerson(updatedName, updatedPhone, updatedEmail,
                        updatedAddress, updatedTags, role);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index) && editPersonDescriptor
                        .equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("index", index)
                        .add("editPersonDescriptor", editPersonDescriptor)
                        .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value
     * will replace the corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;
        private PersonType role;

        public EditPersonDescriptor() {
        }

        /**
         * Copy constructor. A defensive copy of {@code tags} is used
         * internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
            setPersonType(toCopy.role);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address,
                            tags, role);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}. A defensive copy of
         * {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws
         * {@code UnsupportedOperationException} if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null)
                            ? Optional.of(Collections.unmodifiableSet(tags))
                            : Optional.empty();
        }

        public void setPersonType(PersonType role) {
            this.role = role;
        }

        public Optional<PersonType> getPersonType() {
            return Optional.ofNullable(role);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                            && Objects.equals(phone,
                                            otherEditPersonDescriptor.phone)
                            && Objects.equals(email,
                                            otherEditPersonDescriptor.email)
                            && Objects.equals(address,
                                            otherEditPersonDescriptor.address)
                            && Objects.equals(tags,
                                            otherEditPersonDescriptor.tags)
                            && Objects.equals(role,
                                            otherEditPersonDescriptor.role);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).add("name", name)
                            .add("phone", phone).add("email", email)
                            .add("address", address).add("role", role).add("tags", tags)
                            .toString();
        }
    }
}
