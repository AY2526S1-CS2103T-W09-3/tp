package seedu.address.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.classroom.ClassSession;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Parent;
import seedu.address.model.person.Person;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_CLASS = "Tuition Class list contains duplicate class(es).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedClass> classes = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and
     * classes.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("classes") List<JsonAdaptedClass> classes) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (classes != null) {
            this.classes.addAll(classes);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created
     *               {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream()
                .map(JsonAdaptedPerson::new)
                .collect(Collectors.toList()));
        classes.addAll(source.getClassList().stream()
                .map(JsonAdaptedClass::new)
                .collect(Collectors.toList()));
    }


    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();

        // Step 1: Add all persons first
        Map<String, Person> personMap = new HashMap<>();
        Map<String, JsonAdaptedPerson> jsonPersonMap = new HashMap<>();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
            // Store person by their unique identifier (name) for lookup
            personMap.put(person.getName().fullName, person);
            jsonPersonMap.put(person.getName().fullName, jsonAdaptedPerson);
        }

        // Step 1.5: Restore parent-child relationships
        for (JsonAdaptedPerson jsonPerson : persons) {
            Person person = personMap.get(jsonPerson.toModelType().getName().fullName);

            // Restore children for parents
            if (person instanceof Parent) {
                Parent parent = (Parent) person;
                for (String childName : jsonPerson.getChildrenNames()) {
                    Person child = personMap.get(childName);
                    if (child instanceof Student) {
                        parent.addChild((Student) child);
                    }
                }
            }
        }

        // Step 2: Add all classes (without tutor/students initially)
        Map<String, TuitionClass> classMap = new HashMap<>();
        for (JsonAdaptedClass jsonClass : classes) {
            TuitionClass tuitionClass = jsonClass.toModelType();
            if (addressBook.hasClass(tuitionClass)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_CLASS);
            }
            addressBook.addClass(tuitionClass);
            classMap.put(tuitionClass.getName().value, tuitionClass);
        }

        // Step 3: Link tutors and students to classes
        for (JsonAdaptedClass jsonClass : classes) {
            TuitionClass tuitionClass = classMap.get(jsonClass.getName());

            // Link tutor
            if (jsonClass.getTutor() != null) {
                Person tutorPerson = jsonClass.getTutor().toModelType();
                Person matchingTutor = personMap.get(tutorPerson.getName().fullName);
                if (matchingTutor instanceof Tutor) {
                    tuitionClass.setTutor((Tutor) matchingTutor);
                } else {
                    throw new IllegalValueException(
                            "Tutor " + tutorPerson.getName().fullName + " not found or not a Tutor");
                }
            }

            // Link sessions
            for (JsonAdaptedSession jsonSession : jsonClass.getSessions()) {
                jsonSession.validate();
                tuitionClass.addSession(
                        jsonSession.getSessionName(),
                        jsonSession.toModelDateTime(),
                        jsonSession.getLocation());

                // Restore attendance data
                ClassSession session = tuitionClass.getAllSessions().stream()
                        .filter(s -> s.getSessionName().equals(jsonSession.getSessionName()))
                        .findFirst()
                        .orElse(null);

                if (session != null) {
                    // Mark students as present based on saved data
                    for (List<String> record : jsonSession.getPresentStudents()) {
                        String studentName = record.get(0);
                        Person student = personMap.get(studentName);
                        if (student instanceof Student) {
                            session.markPresentAt((Student) student,
                                    LocalDateTime.parse(record.get(1), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        }
                    }

                    // Mark students as absent based on saved data
                    for (List<String> record : jsonSession.getAbsentStudents()) {
                        String studentName = record.get(0);
                        Person student = personMap.get(studentName);
                        if (student instanceof Student) {
                            session.markAbsentAt((Student) student,
                                    LocalDateTime.parse(record.get(1), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        }
                    }
                }
            }

            // Link students
            for (JsonAdaptedPerson jsonStudent : jsonClass.getStudents()) {
                Person studentPerson = jsonStudent.toModelType();
                Person matchingStudent = personMap.get(studentPerson.getName().fullName);
                if (matchingStudent instanceof Student) {
                    tuitionClass.addStudent((Student) matchingStudent);
                } else {
                    throw new IllegalValueException(
                            "Student " + studentPerson.getName().fullName + " not found or not a Student");
                }
            }
        }
        return addressBook;
    }
}
