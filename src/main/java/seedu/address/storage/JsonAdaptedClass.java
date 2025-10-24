package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.classroom.ClassName;
import seedu.address.model.classroom.TuitionClass;

/**
 * Jackson-friendly version of {@link TuitionClass}.
 */
public class JsonAdaptedClass {
    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Class's %s field is missing!";

    private final String name;
    private final JsonAdaptedPerson tutor;
    private final List<JsonAdaptedPerson> students = new ArrayList<>();
    private final List<JsonAdaptedSession> sessions = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedClass} with the given class details.
     */
    @JsonCreator
    public JsonAdaptedClass(@JsonProperty("name") String name,
                            @JsonProperty("tutor") JsonAdaptedPerson tutor,
                            @JsonProperty("students") List<JsonAdaptedPerson> students,
                            @JsonProperty("sessions") List<JsonAdaptedSession> sessions) {
        this.name = name;
        this.tutor = tutor;
        if (students != null) {
            this.students.addAll(students);
        }
        if (sessions != null) {
            this.sessions.addAll(sessions);
        }
    }

    /**
     * Converts a given {@code TuitionClass} into this class for Jackson use.
     */
    public JsonAdaptedClass(TuitionClass source) {
        this.name = source.getName().value;
        this.tutor = source.isAssignedToTutor()
                ? new JsonAdaptedPerson(source.getTutor())
                : null;
        this.students.addAll(source.getStudents().stream()
                .map(JsonAdaptedPerson::new)
                .collect(Collectors.toList()));
        this.sessions.addAll(source.getAllSessions().stream()
                .map(JsonAdaptedSession::new)
                .collect(Collectors.toList()));
    }

    /**
     * Returns the class name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the tutor JSON object.
     */
    public JsonAdaptedPerson getTutor() {
        return tutor;
    }

    /**
     * Returns the list of student JSON objects.
     */
    public List<JsonAdaptedPerson> getStudents() {
        return students;
    }

    /**
     * Returns the list of session JSON objects.
     */
    public List<JsonAdaptedSession> getSessions() {
        return sessions;
    }

    /**
     * Converts this Jackson-friendly object to the model's {@code TuitionClass}.
     * This creates a basic TuitionClass with just the name.
     * Tutor and students are linked separately in JsonSerializableAddressBook.
     *
     * @throws IllegalValueException if any field is invalid or missing
     */
    public TuitionClass toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "name"));
        }

        try {
            ClassName className = new ClassName(name);
            return new TuitionClass(className);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(e.getMessage());
        }
    }
}
