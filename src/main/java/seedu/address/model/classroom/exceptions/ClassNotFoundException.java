package seedu.address.model.classroom.exceptions;

public class ClassNotFoundException extends RuntimeException {
    public ClassNotFoundException() {
        super("Class not found in list");
    }
}
