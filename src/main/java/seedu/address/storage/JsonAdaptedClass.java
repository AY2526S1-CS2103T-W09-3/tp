package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.classroom.ClassName;
import seedu.address.model.classroom.TuitionClass;

public class JsonAdaptedClass {
    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Class's %s field is missing!";

    private final String name;

    @JsonCreator
    public JsonAdaptedClass(@JsonProperty("name") String name) {
        this.name = name;
    }

    public JsonAdaptedClass(TuitionClass source) {
        this.name = source.getName().value;
    }

    public TuitionClass toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "name"));
        }
        try {
            return new TuitionClass(new ClassName(name));
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(e.getMessage());
        }
    }
}
