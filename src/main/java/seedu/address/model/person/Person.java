package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public abstract class Person {

    // Constants
    public static final int MAX_TAGS_PER_PERSON = 20;

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Constructs a {@code Person}.
     *
     * @param name Name of the person. Must not be null.
     * @param phone Phone number of the person. Must not be null.
     * @param email Email of the person. Must not be null.
     * @param address Address of the person. Must not be null.
     * @param tags Set of tags associated with the person. Must not be null.
     * @throws NullPointerException if any parameter is null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        // Limit number of tags per person
        if (tags.size() > MAX_TAGS_PER_PERSON) {
            throw new IllegalArgumentException(
                String.format("Too many tags. Maximum %d tags allowed per person.", MAX_TAGS_PER_PERSON));
        }
        this.tags.addAll(tags);
    }

    /**
     * Factory method to create a concrete {@code Person} based on role.
     *
     * @param name Name of the person.
     * @param phone Phone number of the person.
     * @param email Email of the person.
     * @param address Address of the person.
     * @param tags Set of tags associated with the person.
     * @param role The role determining the concrete subtype to instantiate.
     * @return A {@link Student}, {@link Tutor}, or {@link Parent} instance as specified by {@code role}.
     * @throws IllegalArgumentException if {@code role} is not a supported {@link PersonType}.
     * @throws NullPointerException if any parameter is null.
     */
    public static Person newPerson(Name name, Phone phone, Email email, Address address, Set<Tag> tags,
                    PersonType role) {
        switch (role) {
        case STUDENT:
            return new Student(name, phone, email, address, tags);
        case TUTOR:
            return new Tutor(name, phone, email, address, tags);
        case PARENT:
            return new Parent(name, phone, email, address, tags);
        default:
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    /**
     * Returns the person's name.
     *
     * @return the {@link Name}.
     */
    public Name getName() {
        return name;
    }

    /**
     * Returns the person's phone number.
     *
     * @return the {@link Phone}.
     */
    public Phone getPhone() {
        return phone;
    }

    /**
     * Returns the person's email.
     *
     * @return the {@link Email}.
     */
    public Email getEmail() {
        return email;
    }

    /**
     * Returns the person's address.
     *
     * @return the {@link Address}.
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws
     * {@code UnsupportedOperationException} if modification is attempted.
     *
     * @return an unmodifiable {@link Set} of {@link Tag}s.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same name. This defines a weaker
     * notion of equality between two persons.
     *
     * @param otherPerson The other person to compare against.
     * @return true if both refer to the same instance, or both have the same {@link Name}.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null && otherPerson.getName().equals(getName());
    }
    // Abstract method for deletion
    public abstract void delete();

    /**
     * Handles the editing of this person by updating or removing relationships.
     *
     * @param editedPerson The newly edited person with updated details.
     * @param isTypeEdited Whether the person's type has changed.
     */
    public abstract void handleEdit(Person editedPerson, boolean isTypeEdited);

    /**
     * Returns true if both persons have the same identity and data fields. This
     * defines a stronger notion of equality between two persons.
     *
     * @param other The other object to compare against.
     * @return true if all identity and data fields are equal.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name) && phone.equals(otherPerson.phone) && email.equals(otherPerson.email)
                        && address.equals(otherPerson.address) && tags.equals(otherPerson.tags);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return hash code computed from all identity and data fields.
     */
    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing
        // your own
        return Objects.hash(name, phone, email, address, tags);
    }

    /**
     * Returns a string representation of this person for debugging.
     *
     * @return a string containing name, phone, email, address, and tags.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).add("name", name).add("phone", phone).add("email", email)
                        .add("address", address).add("tags", tags).toString();
    }

    /**
     * Returns this person's type (role).
     *
     * @return the {@link PersonType} of this person.
     */
    public abstract PersonType getPersonType();

}
