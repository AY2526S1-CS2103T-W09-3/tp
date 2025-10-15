package seedu.address.model.classroom.exceptions;

/**
 * Signals that the operation would result in duplicate classes.
 */
public class DuplicateClassException extends RuntimeException {
    public DuplicateClassException() {
        super("Operation would result in duplicate classes");
    }
}
