package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddCommandParser;
import seedu.address.logic.parser.AttendCommandParser;
import seedu.address.logic.parser.EditCommandParser;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classroom.ClassName;
import seedu.address.model.classroom.ClassSession;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonType;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Tests for error paths and exception handling across the application.
 */
public class ErrorPathTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
    }

    // ===== Command Exception Tests =====

    @Test
    public void deleteCommand_invalidIndex_throwsCommandException() {
        DeleteCommand deleteCommand = new DeleteCommand(Index.fromOneBased(1));

        // Empty model, index 1 doesn't exist
        assertThrows(CommandException.class, () -> {
            deleteCommand.execute(model);
        });
    }

    @Test
    public void deleteCommand_negativeIndex_throwsException() {
        // Should throw during index creation
        assertThrows(IndexOutOfBoundsException.class, () -> {
            Index.fromZeroBased(-1);
        });
    }

    @Test
    public void editCommand_invalidIndex_throwsCommandException() {
        EditCommand.EditPersonDescriptor descriptor = new EditCommand.EditPersonDescriptor();
        descriptor.setName(new Name("New Name"));
        EditCommand editCommand = new EditCommand(Index.fromOneBased(1), descriptor);

        // Empty model, index 1 doesn't exist
        assertThrows(CommandException.class, () -> {
            editCommand.execute(model);
        });
    }

    @Test
    public void editCommand_duplicatePerson_throwsCommandException() {
        // Add two persons
        Person person1 = new Student(
            new Name("John Doe"),
            new Phone("12345678"),
            new Email("john@example.com"),
            new Address("123 Street"),
            new HashSet<>()
        );

        Person person2 = new Student(
            new Name("Jane Doe"),
            new Phone("87654321"),
            new Email("jane@example.com"),
            new Address("456 Avenue"),
            new HashSet<>()
        );

        model.addPerson(person1);
        model.addPerson(person2);

        // Try to edit person2 to have same name as person1
        EditCommand.EditPersonDescriptor descriptor = new EditCommand.EditPersonDescriptor();
        descriptor.setName(new Name("John Doe")); // Same name as person1
        EditCommand editCommand = new EditCommand(Index.fromOneBased(2), descriptor);

        assertThrows(CommandException.class, () -> {
            editCommand.execute(model);
        });
    }

    @Test
    public void addCommand_duplicatePerson_throwsCommandException() {
        Person person = new Student(
            new Name("Duplicate Person"),
            new Phone("99999999"),
            new Email("dup@example.com"),
            new Address("Duplicate Address"),
            new HashSet<>()
        );

        AddCommand addCommand1 = new AddCommand(person);
        AddCommand addCommand2 = new AddCommand(person);

        try {
            addCommand1.execute(model);
            // Second add should fail
            assertThrows(CommandException.class, () -> {
                addCommand2.execute(model);
            });
        } catch (CommandException e) {
            fail("First add should succeed");
        }
    }

    @Test
    public void attendCommand_nonExistentClass_throwsCommandException() {
        AttendCommand attendCommand = new AttendCommand(
            new Name("John"),
            "NonExistentClass",
            "Session1",
            true
        );

        assertThrows(CommandException.class, () -> {
            attendCommand.execute(model);
        });
    }

    @Test
    public void attendCommand_nonExistentSession_throwsCommandException() {
        // Add a class
        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        model.addClass(mathClass);

        AttendCommand attendCommand = new AttendCommand(
            new Name("John"),
            "Math",
            "NonExistentSession",
            true
        );

        assertThrows(CommandException.class, () -> {
            attendCommand.execute(model);
        });
    }

    @Test
    public void attendCommand_studentNotInClass_throwsCommandException() {
        // Add a class with session
        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        model.addClass(mathClass);

        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        mathClass.addSession("Session1", futureDate, "Room 101");

        // Try to mark attendance for non-enrolled student
        AttendCommand attendCommand = new AttendCommand(
            new Name("NonEnrolledStudent"),
            "Math",
            "Session1",
            true
        );

        assertThrows(CommandException.class, () -> {
            attendCommand.execute(model);
        });
    }

    @Test
    public void joinClassCommand_studentNotFound_throwsCommandException() {
        // Add a class
        TuitionClass class1 = new TuitionClass(new ClassName("Class1"));
        model.addClass(class1);

        // Try to join with invalid student name
        JoinClassCommand joinCommand = new JoinClassCommand(
            "NonExistentStudent",
            "Class1"
        );

        assertThrows(CommandException.class, () -> {
            joinCommand.execute(model);
        });
    }

    @Test
    public void joinClassCommand_classNotFound_throwsCommandException() {
        // Add a student
        Student student = new Student(
            new Name("Test Student"),
            new Phone("11111111"),
            new Email("test@test.com"),
            new Address("Test Address"),
            new HashSet<>()
        );
        model.addPerson(student);

        // Try to join non-existent class
        JoinClassCommand joinCommand = new JoinClassCommand(
            "Test Student",
            "NonExistentClass"
        );

        assertThrows(CommandException.class, () -> {
            joinCommand.execute(model);
        });
    }

    // ===== Parser Exception Tests =====

    @Test
    public void parserUtil_parseIndex_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            ParserUtil.parseIndex(null);
        });
    }

    @Test
    public void parserUtil_parseIndex_invalidFormat_throwsParseException() {
        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex("abc");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex("1.5");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex("-1");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex("0");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex("");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex("  ");
        });
    }

    @Test
    public void parserUtil_parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            ParserUtil.parseName(null);
        });
    }

    @Test
    public void parserUtil_parseName_invalid_throwsParseException() {
        assertThrows(ParseException.class, () -> {
            ParserUtil.parseName("");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseName("  ");
        });

        // Test for names with only special characters (if restricted)
        assertThrows(ParseException.class, () -> {
            ParserUtil.parseName("@#$%");
        });
    }

    @Test
    public void parserUtil_parsePhone_invalid_throwsParseException() {
        assertThrows(ParseException.class, () -> {
            ParserUtil.parsePhone("12"); // Too short
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parsePhone("phone"); // Non-numeric
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parsePhone("123-456"); // Contains non-numeric
        });
    }

    @Test
    public void parserUtil_parseEmail_invalid_throwsParseException() {
        assertThrows(ParseException.class, () -> {
            ParserUtil.parseEmail("notanemail");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseEmail("@example.com");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseEmail("user@");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseEmail("user @example.com"); // Space in email
        });
    }

    @Test
    public void parserUtil_parseDateTime_invalid_throwsParseException() {
        assertThrows(ParseException.class, () -> {
            ParserUtil.parseDateTime("not a date");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseDateTime("2024-13-01 25:00"); // Invalid month and hour
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseDateTime("2024/01/01 10:00"); // Wrong format
        });
    }

    @Test
    public void parserUtil_parsePersonType_invalid_throwsParseException() {
        assertThrows(ParseException.class, () -> {
            ParserUtil.parsePersonType("invalid");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parsePersonType("teacher"); // Not a valid type
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parsePersonType("");
        });
    }

    @Test
    public void parserUtil_parseAttendanceStatus_invalid_throwsParseException() {
        assertThrows(ParseException.class, () -> {
            ParserUtil.parseAttendanceStatus("maybe");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseAttendanceStatus("yes");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseAttendanceStatus("1");
        });
    }

    // ===== Model Exception Tests =====

    @Test
    public void model_addDuplicatePerson_throwsException() {
        Person person = new Student(
            new Name("Duplicate"),
            new Phone("88888888"),
            new Email("dup@test.com"),
            new Address("Address"),
            new HashSet<>()
        );

        model.addPerson(person);

        // Adding same person again should fail
        assertThrows(DuplicatePersonException.class, () -> {
            model.addPerson(person);
        });
    }

    @Test
    public void model_deleteNonExistentPerson_throwsException() {
        Person person = new Student(
            new Name("NonExistent"),
            new Phone("77777777"),
            new Email("none@test.com"),
            new Address("Address"),
            new HashSet<>()
        );

        // Deleting non-existent person should fail
        assertThrows(PersonNotFoundException.class, () -> {
            model.deletePerson(person);
        });
    }

    @Test
    public void model_setPersonNotFound_throwsException() {
        Person target = new Student(
            new Name("Target"),
            new Phone("66666666"),
            new Email("target@test.com"),
            new Address("Address"),
            new HashSet<>()
        );

        Person edited = new Student(
            new Name("Edited"),
            new Phone("66666666"),
            new Email("edited@test.com"),
            new Address("Address"),
            new HashSet<>()
        );

        // Setting non-existent person should fail
        assertThrows(PersonNotFoundException.class, () -> {
            model.setPerson(target, edited);
        });
    }

    // ===== Recovery Tests =====

    @Test
    public void recovery_afterFailedAdd_modelUnchanged() {
        Person person1 = new Student(
            new Name("First"),
            new Phone("11111111"),
            new Email("first@test.com"),
            new Address("Address"),
            new HashSet<>()
        );

        model.addPerson(person1);
        int originalSize = model.getFilteredPersonList().size();

        // Try to add duplicate
        try {
            model.addPerson(person1);
            fail("Should throw DuplicatePersonException");
        } catch (DuplicatePersonException e) {
            // Expected exception
        }

        // Model should remain unchanged
        assertEquals(originalSize, model.getFilteredPersonList().size());
        assertTrue(model.hasPerson(person1));
    }

    @Test
    public void recovery_afterFailedDelete_modelUnchanged() {
        int originalSize = model.getFilteredPersonList().size();

        Person nonExistent = new Student(
            new Name("NonExistent"),
            new Phone("99999999"),
            new Email("none@test.com"),
            new Address("Address"),
            new HashSet<>()
        );

        // Try to delete non-existent person
        try {
            model.deletePerson(nonExistent);
            fail("Should throw PersonNotFoundException");
        } catch (PersonNotFoundException e) {
            // Expected exception
        }

        // Model should remain unchanged
        assertEquals(originalSize, model.getFilteredPersonList().size());
    }

    @Test
    public void recovery_afterFailedEdit_modelUnchanged() {
        Person person = new Student(
            new Name("Original"),
            new Phone("55555555"),
            new Email("orig@test.com"),
            new Address("Address"),
            new HashSet<>()
        );

        model.addPerson(person);

        Person nonExistent = new Student(
            new Name("NonExistent"),
            new Phone("44444444"),
            new Email("none@test.com"),
            new Address("Address"),
            new HashSet<>()
        );

        Person edited = new Student(
            new Name("Edited"),
            new Phone("44444444"),
            new Email("edited@test.com"),
            new Address("Address"),
            new HashSet<>()
        );

        // Try to edit non-existent person
        try {
            model.setPerson(nonExistent, edited);
            fail("Should throw PersonNotFoundException");
        } catch (PersonNotFoundException e) {
            // Expected exception
        }

        // Original person should remain unchanged
        assertTrue(model.hasPerson(person));
        assertEquals(1, model.getFilteredPersonList().size());
    }

    // ===== Null Handling Tests =====

    @Test
    public void nullHandling_commandsRejectNull() {
        assertThrows(NullPointerException.class, () -> {
            new AddCommand(null);
        });

        assertThrows(NullPointerException.class, () -> {
            new DeleteCommand(null);
        });

        assertThrows(NullPointerException.class, () -> {
            new EditCommand(null, new EditCommand.EditPersonDescriptor());
        });

        assertThrows(NullPointerException.class, () -> {
            new EditCommand(Index.fromOneBased(1), null);
        });

        assertThrows(NullPointerException.class, () -> {
            new AttendCommand(null, "class", "session", true);
        });

        assertThrows(NullPointerException.class, () -> {
            new AttendCommand(new Name("Name"), null, "session", true);
        });

        assertThrows(NullPointerException.class, () -> {
            new AttendCommand(new Name("Name"), "class", null, true);
        });

        assertThrows(NullPointerException.class, () -> {
            new AttendCommand(new Name("Name"), "class", "session", null);
        });
    }

    @Test
    public void nullHandling_modelRejectsNull() {
        assertThrows(NullPointerException.class, () -> {
            model.addPerson(null);
        });

        assertThrows(NullPointerException.class, () -> {
            model.deletePerson(null);
        });

        assertThrows(NullPointerException.class, () -> {
            model.hasPerson(null);
        });

        assertThrows(NullPointerException.class, () -> {
            model.setPerson(null, new Student(
                new Name("Test"),
                new Phone("12345678"),
                new Email("test@test.com"),
                new Address("Address"),
                new HashSet<>()
            ));
        });

        assertThrows(NullPointerException.class, () -> {
            model.setPerson(new Student(
                new Name("Test"),
                new Phone("12345678"),
                new Email("test@test.com"),
                new Address("Address"),
                new HashSet<>()
            ), null);
        });
    }

    // ===== Partial State Tests =====

    @Test
    public void partialState_classWithNoTutor_handledGracefully() {
        TuitionClass classWithoutTutor = new TuitionClass(new ClassName("NoTutor"));
        model.addClass(classWithoutTutor);

        assertFalse(classWithoutTutor.isAssignedToTutor());
        assertNotNull(classWithoutTutor.getTutorProperty());
        assertEquals("Unassigned", classWithoutTutor.getTutorProperty().get());
    }

    @Test
    public void partialState_sessionWithNoStudents_handledGracefully() {
        TuitionClass emptyClass = new TuitionClass(new ClassName("Empty"));
        model.addClass(emptyClass);

        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        ClassSession session = emptyClass.addSession("EmptySession", futureDate, "Room");

        assertNotNull(session.getAttendanceRecord());
        assertEquals(0, session.getAttendanceCount());
        assertNotNull(session.getSessionDetails());
    }

    @Test
    public void partialState_personWithNoTags_handledGracefully() {
        Person personNoTags = new Student(
            new Name("NoTags"),
            new Phone("33333333"),
            new Email("notags@test.com"),
            new Address("Address"),
            new HashSet<>() // Empty tags
        );

        model.addPerson(personNoTags);

        assertNotNull(personNoTags.getTags());
        assertEquals(0, personNoTags.getTags().size());
    }
}
