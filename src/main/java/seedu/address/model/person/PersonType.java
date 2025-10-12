package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Enumerates the types of people in the address book.
 */
public enum PersonType {
    STUDENT, TUTOR, PARENT;

    /**
     * Parses a case-insensitive string into a PersonType.
     *
     * @throws IllegalArgumentException if the string does not match a valid
     *             type
     */
    public static PersonType fromString(String value) {
        requireNonNull(value);
        switch (value.trim().toLowerCase()) {
        case "student":
            return STUDENT;
        case "tutor":
            return TUTOR;
        case "parent":
            return PARENT;
        default:
            throw new IllegalArgumentException("Invalid person type: " + value);
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
