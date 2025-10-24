package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.classroom.ClassName;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.AddressBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setAddressBookFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookFilePath(path);
        assertEquals(path, modelManager.getAddressBookFilePath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));

        // different filteredClasses -> returns false
        ModelManager modelManagerWithClass = new ModelManager(addressBook, userPrefs);
        TuitionClass testClass = new TuitionClass(new ClassName("TestClass"));
        modelManagerWithClass.addClass(testClass);
        modelManagerWithClass.updateFilteredClassList(c -> c.getName().value.equals("TestClass"));
        assertFalse(modelManager.equals(modelManagerWithClass));
    }

    @Test
    public void addStudentToClass_validStudentAndClass_success() {
        Student student = new Student(
                new Name("Alice Tan"),
                new Phone("98765432"),
                new Email("alice@example.com"),
                new Address("10 Kent Ridge Road"),
                new HashSet<>());
        TuitionClass tuitionClass = new TuitionClass(new ClassName("CS2103T"));

        modelManager.addPerson(student);
        modelManager.addClass(tuitionClass);
        modelManager.addStudentToClass(student, tuitionClass);

        assertTrue(tuitionClass.hasStudent(student));
    }

    @Test
    public void assignTutorToClass_validTutorAndClass_success() {
        Tutor tutor = new Tutor(
                new Name("Mr Smith"),
                new Phone("91234567"),
                new Email("smith@example.com"),
                new Address("1 Tutor Lane"),
                new HashSet<>());
        TuitionClass tuitionClass = new TuitionClass(new ClassName("CS2103T"));

        modelManager.addPerson(tutor);
        modelManager.addClass(tuitionClass);
        modelManager.assignTutorToClass(tutor, tuitionClass);

        assertTrue(tuitionClass.hasTutor(tutor));
    }

    @Test
    public void deleteClass_removesAllReferences() {
        // Setup
        ModelManager testModelManager = new ModelManager();

        // Create a student
        Student student = new Student(
            new Name("Alice"),
            new Phone("12345678"),
            new Email("alice@example.com"),
            new Address("123 Street"),
            new HashSet<Tag>()
        );
        testModelManager.addPerson(student);

        // Create a tutor
        Tutor tutor = new Tutor(
            new Name("Bob"),
            new Phone("87654321"),
            new Email("bob@example.com"),
            new Address("456 Avenue"),
            new HashSet<Tag>()
        );
        testModelManager.addPerson(tutor);

        // Create a class
        TuitionClass mathClass = new TuitionClass(new ClassName("Math101"));
        testModelManager.addClass(mathClass);

        // Link student and tutor to class
        testModelManager.addStudentToClass(student, mathClass);
        testModelManager.assignTutorToClass(tutor, mathClass);

        // Verify relationships are established
        assertTrue(student.getTuitionClasses().contains(mathClass),
                   "Student should be enrolled in Math101");
        assertTrue(tutor.getTuitionClasses().contains(mathClass),
                   "Tutor should be teaching Math101");
        assertTrue(mathClass.hasStudent(student),
                   "Math101 should contain student");
        assertTrue(mathClass.hasTutor(tutor),
                   "Math101 should have tutor");

        // Delete the class
        testModelManager.deleteClass(mathClass);

        // Verify all references are cleaned up
        assertFalse(student.getTuitionClasses().contains(mathClass),
                    "Student should NOT have reference to deleted Math101");
        assertFalse(tutor.getTuitionClasses().contains(mathClass),
                    "Tutor should NOT have reference to deleted Math101");
        assertFalse(testModelManager.hasClass(mathClass),
                    "ModelManager should NOT contain Math101");

        // Verify no stale references
        assertEquals(0, student.getTuitionClasses().size(),
                     "Student should have 0 classes");
        assertEquals(0, tutor.getTuitionClasses().size(),
                     "Tutor should have 0 classes");
    }

    @Test
    public void deleteClass_withMultipleStudents_removesAllReferences() {
        // Setup
        ModelManager testModelManager = new ModelManager();

        // Create multiple students
        Student alice = new Student(
            new Name("Alice"),
            new Phone("11111111"),
            new Email("alice@example.com"),
            new Address("1 Street"),
            new HashSet<Tag>()
        );
        Student bob = new Student(
            new Name("Bob Student"),
            new Phone("22222222"),
            new Email("bobstudent@example.com"),
            new Address("2 Street"),
            new HashSet<Tag>()
        );
        Student charlie = new Student(
            new Name("Charlie"),
            new Phone("33333333"),
            new Email("charlie@example.com"),
            new Address("3 Street"),
            new HashSet<Tag>()
        );

        testModelManager.addPerson(alice);
        testModelManager.addPerson(bob);
        testModelManager.addPerson(charlie);

        // Create a class and enroll all students
        TuitionClass mathClass = new TuitionClass(new ClassName("Math101"));
        testModelManager.addClass(mathClass);

        testModelManager.addStudentToClass(alice, mathClass);
        testModelManager.addStudentToClass(bob, mathClass);
        testModelManager.addStudentToClass(charlie, mathClass);

        // Verify all enrolled
        assertEquals(3, mathClass.getStudents().size());
        assertTrue(alice.getTuitionClasses().contains(mathClass));
        assertTrue(bob.getTuitionClasses().contains(mathClass));
        assertTrue(charlie.getTuitionClasses().contains(mathClass));

        // Delete the class
        testModelManager.deleteClass(mathClass);

        // Verify all students have no references
        assertEquals(0, alice.getTuitionClasses().size(),
                     "Alice should have no classes");
        assertEquals(0, bob.getTuitionClasses().size(),
                     "Bob should have no classes");
        assertEquals(0, charlie.getTuitionClasses().size(),
                     "Charlie should have no classes");
        assertFalse(testModelManager.hasClass(mathClass),
                    "Class should be removed from ModelManager");
    }

    @Test
    public void deleteClass_withNoStudentsOrTutor_success() {
        // Setup - class with no relationships
        ModelManager testModelManager = new ModelManager();
        TuitionClass emptyClass = new TuitionClass(new ClassName("EmptyClass"));
        testModelManager.addClass(emptyClass);

        assertTrue(testModelManager.hasClass(emptyClass));

        // Delete the class
        testModelManager.deleteClass(emptyClass);

        // Verify deletion
        assertFalse(testModelManager.hasClass(emptyClass),
                    "Empty class should be deleted successfully");
    }

    @Test
    public void deleteClass_withOnlyTutor_removesReferences() {
        // Setup
        ModelManager testModelManager = new ModelManager();

        Tutor tutor = new Tutor(
            new Name("Mr Teacher"),
            new Phone("99999999"),
            new Email("teacher@example.com"),
            new Address("Teacher Street"),
            new HashSet<Tag>()
        );
        testModelManager.addPerson(tutor);

        TuitionClass mathClass = new TuitionClass(new ClassName("Math101"));
        testModelManager.addClass(mathClass);
        testModelManager.assignTutorToClass(tutor, mathClass);

        // Verify relationship
        assertTrue(tutor.getTuitionClasses().contains(mathClass));
        assertTrue(mathClass.hasTutor(tutor));

        // Delete class
        testModelManager.deleteClass(mathClass);

        // Verify cleanup
        assertEquals(0, tutor.getTuitionClasses().size(),
                     "Tutor should have no classes after deletion");
        assertFalse(testModelManager.hasClass(mathClass));
    }
}
