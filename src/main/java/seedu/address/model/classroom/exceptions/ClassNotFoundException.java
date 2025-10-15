package seedu.address.model.classroom.exceptions;

/**
 * Signals that the operation is unable to find the specified class in the list.
 */
public class ClassNotFoundException extends RuntimeException {
    public ClassNotFoundException() {
        super("Class not found in list");
    }
}
