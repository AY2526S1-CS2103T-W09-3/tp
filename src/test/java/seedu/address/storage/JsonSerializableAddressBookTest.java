package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.classroom.ClassSession;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Student;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");
    private static final Path CLASS_WITH_ATTENDANCE_FILE = TEST_DATA_FOLDER
            .resolve("classWithAttendanceAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_classWithAttendance_attendanceRestored() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(CLASS_WITH_ATTENDANCE_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBook = dataFromFile.toModelType();

        // Get the class and session
        TuitionClass mathClass = addressBook.getClassList().stream()
                .filter(c -> c.getClassName().equals("Math101"))
                .findFirst()
                .orElse(null);

        assertEquals("Math101", mathClass.getClassName());
        assertEquals(1, mathClass.getAllSessions().size());

        ClassSession session = mathClass.getAllSessions().get(0);
        assertEquals("Week 1", session.getSessionName());
        assertEquals(1, session.getAttendanceCount());

        // Verify attendance for specific students
        Student alice = (Student) addressBook.getPersonList().stream()
                .filter(p -> p.getName().fullName.equals("Alice Student"))
                .findFirst()
                .orElse(null);
        Student bob = (Student) addressBook.getPersonList().stream()
                .filter(p -> p.getName().fullName.equals("Bob Student"))
                .findFirst()
                .orElse(null);

        assertTrue(session.hasAttended(alice));
        assertTrue(!session.hasAttended(bob));
    }

}
