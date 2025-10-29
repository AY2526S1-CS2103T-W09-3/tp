package seedu.address.model.classroom;

import static java.util.Objects.requireNonNull;

import java.text.Normalizer;

/**
 * Represents a class name in the address book.
 * Constraints: 1–50 characters; letters, digits, spaces, '-', '–', '—', and '_' allowed.
 */
public class ClassName {
    public static final String MESSAGE_CONSTRAINTS =
            "Class name should be 1-50 characters. Allowed: letters, digits, spaces, -, -, —, _.";

    private static final String VALIDATION_REGEX = "^[\\p{L}\\p{N} _\\--—]{1,50}$";

    public final String value;

    /**
     * Constructs a {@code ClassName} from the given input after normalizing whitespace.
     * @param input raw class name string
     * @throws IllegalArgumentException if constraints are violated
     */
    public ClassName(String input) {
        requireNonNull(input);
        // Normalize (NFC), collapse whitespace to single spaces, trim ends
        String s = Normalizer.normalize(input, Normalizer.Form.NFC)
                .replaceAll("\\s+", " ")
                .trim();
        if (!s.matches(VALIDATION_REGEX)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        this.value = s;
    }

    @Override public String toString() {
        return value; }

    @Override public boolean equals(Object other) {
        return other == this || (other instanceof ClassName && value.equalsIgnoreCase(((ClassName) other).value));
    }

    @Override public int hashCode() {
        return value.hashCode(); }
}
