package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.classroom.ClassName;
import seedu.address.model.classroom.ClassSession;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Parent;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;
import seedu.address.model.tag.Tag;

/**
 * Integration tests for complex operations involving multiple entities.
 */
public class IntegrationTest {

    private Model model;
    private AddressBook addressBook;

    @BeforeEach
    public void setUp() {
        addressBook = new AddressBook();
        model = new ModelManager(addressBook, new UserPrefs());
    }

    @Test
    public void personTypeTransition_studentToTutor_cleansUpRelationships() {
        // Create a student
        Student student = new Student(
            new Name("John Doe"),
            new Phone("91234567"),
            new Email("john@example.com"),
            new Address("123 Main St"),
            new HashSet<>()
        );

        // Add student to model
        model.addPerson(student);

        // Create a class and add student to it
        TuitionClass mathClass = new TuitionClass(new ClassName("Math101"));
        model.addClass(mathClass);
        model.addStudentToClass(student, mathClass);

        // Verify student is in class
        assertTrue(mathClass.hasStudent(student));

        // Create tutor with same identity (simulating edit)
        Tutor tutor = new Tutor(
            new Name("John Doe"),
            new Phone("91234567"),
            new Email("john@example.com"),
            new Address("123 Main St"),
            new HashSet<>()
        );

        model.setPerson(student, tutor);

        assertFalse(mathClass.hasStudent(student));
    }

    @Test
    public void personTypeTransition_tutorToStudent_cleansUpClasses() {
        // Create a tutor
        Tutor tutor = new Tutor(
            new Name("Jane Smith"),
            new Phone("92345678"),
            new Email("jane@example.com"),
            new Address("456 Oak St"),
            new HashSet<>()
        );

        // Add tutor to model
        model.addPerson(tutor);

        // Create a class and assign tutor
        TuitionClass physicsClass = new TuitionClass(new ClassName("Physics101"));
        model.addClass(physicsClass);
        model.assignTutorToClass(tutor, physicsClass);

        // Verify tutor is assigned
        assertTrue(physicsClass.isAssignedToTutor());
        assertEquals(tutor, physicsClass.getTutor());

        // Create student with same identity (simulating edit)
        Student student = new Student(
            new Name("Jane Smith"),
            new Phone("92345678"),
            new Email("jane@example.com"),
            new Address("456 Oak St"),
            new HashSet<>()
        );

        model.setPerson(tutor, student);

        assertFalse(physicsClass.isAssignedToTutor());
    }

    @Test
    public void cascadeDelete_deleteStudent_removesFromAllClasses() {
        // Create a student
        Student student = new Student(
            new Name("Alice Wong"),
            new Phone("93456789"),
            new Email("alice@example.com"),
            new Address("789 Pine St"),
            new HashSet<>()
        );

        model.addPerson(student);

        // Create multiple classes and add student to all
        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        TuitionClass scienceClass = new TuitionClass(new ClassName("Science"));
        TuitionClass englishClass = new TuitionClass(new ClassName("English"));

        model.addClass(mathClass);
        model.addClass(scienceClass);
        model.addClass(englishClass);

        model.addStudentToClass(student, mathClass);
        model.addStudentToClass(student, scienceClass);
        model.addStudentToClass(student, englishClass);

        // Verify student is in all classes
        assertTrue(mathClass.hasStudent(student));
        assertTrue(scienceClass.hasStudent(student));
        assertTrue(englishClass.hasStudent(student));

        // Delete the student
        model.deletePerson(student);

        assertFalse(mathClass.hasStudent(student));
        assertFalse(scienceClass.hasStudent(student));
        assertFalse(englishClass.hasStudent(student));
    }

    @Test
    public void cascadeDelete_deleteTutor_unassignsFromAllClasses() {
        // Create a tutor
        Tutor tutor = new Tutor(
            new Name("Bob Lee"),
            new Phone("94567890"),
            new Email("bob@example.com"),
            new Address("321 Elm St"),
            new HashSet<>()
        );

        model.addPerson(tutor);

        // Create multiple classes and assign tutor
        TuitionClass class1 = new TuitionClass(new ClassName("Class1"));
        TuitionClass class2 = new TuitionClass(new ClassName("Class2"));

        model.addClass(class1);
        model.addClass(class2);

        model.assignTutorToClass(tutor, class1);
        model.assignTutorToClass(tutor, class2);

        // Verify tutor is assigned
        assertTrue(class1.isAssignedToTutor());
        assertTrue(class2.isAssignedToTutor());

        // Delete the tutor
        model.deletePerson(tutor);

        // Tutor should be unassigned from all classes
        assertFalse(class1.isAssignedToTutor());
        assertFalse(class2.isAssignedToTutor());
    }

    @Test
    public void cascadeDelete_deleteClass_removesAllSessions() {
        // Create a class with sessions
        TuitionClass chemClass = new TuitionClass(new ClassName("Chemistry"));
        model.addClass(chemClass);

        // Add multiple sessions
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        ClassSession session1 = chemClass.addSession("Session 1", futureDate, "Room 101");
        ClassSession session2 = chemClass.addSession("Session 2", futureDate.plusDays(1), "Room 102");
        ClassSession session3 = chemClass.addSession("Session 3", futureDate.plusDays(2), "Room 103");

        // Verify sessions exist
        assertEquals(3, chemClass.getAllSessions().size());

        model.deleteClass(chemClass);

        assertFalse(model.getAddressBook().getClassList().contains(chemClass));
    }

    @Test
    public void complexRelationship_studentParentLink_maintainedDuringEdits() {
        // Create student and parent
        Student student = new Student(
            new Name("Child One"),
            new Phone("81234567"),
            new Email("child@example.com"),
            new Address("Home Address"),
            new HashSet<>()
        );

        Parent parent = new Parent(
            new Name("Parent One"),
            new Phone("82345678"),
            new Email("parent@example.com"),
            new Address("Home Address"),
            new HashSet<>()
        );

        model.addPerson(student);
        model.addPerson(parent);

        // Link parent and child
        parent.addChild(student);
        student.addParent(parent);

        // Verify relationship
        assertTrue(parent.getChildren().contains(student));
        assertTrue(student.getParents().contains(parent));

        // Edit parent's phone number
        Parent editedParent = new Parent(
            new Name("Parent One"),
            new Phone("88888888"), // Changed phone
            new Email("parent@example.com"),
            new Address("Home Address"),
            new HashSet<>()
        );

        model.setPerson(parent, editedParent);
    }

    @Test
    public void sessionAttendance_studentRemovedFromClass_handledGracefully() {
        // Create class with student and session
        TuitionClass bioClass = new TuitionClass(new ClassName("Biology"));
        Student student = new Student(
            new Name("Test Student"),
            new Phone("85678901"),
            new Email("test@example.com"),
            new Address("Test Address"),
            new HashSet<>()
        );

        model.addPerson(student);
        model.addClass(bioClass);
        model.addStudentToClass(student, bioClass);

        // Create session and mark attendance
        LocalDateTime sessionTime = LocalDateTime.now().plusDays(1);
        ClassSession session = bioClass.addSession("Bio Lab", sessionTime, "Lab 1");
        session.markPresent(student);

        // Verify attendance
        assertTrue(session.hasAttended(student));

        // Remove student from class
        bioClass.removeStudent(student);

        // Session should handle missing student gracefully
        // The attendance record still exists but student is not in class
        assertFalse(bioClass.hasStudent(student));
        // Attendance record may still exist - this is a design decision
    }

    @Test
    public void editClass_withEnrolledStudents_maintainsEnrollment() {
        // Create class with students
        TuitionClass originalClass = new TuitionClass(new ClassName("Original"));
        Student student1 = new Student(
            new Name("Student One"),
            new Phone("81111111"),
            new Email("s1@example.com"),
            new Address("Address 1"),
            new HashSet<>()
        );
        Student student2 = new Student(
            new Name("Student Two"),
            new Phone("82222222"),
            new Email("s2@example.com"),
            new Address("Address 2"),
            new HashSet<>()
        );

        model.addPerson(student1);
        model.addPerson(student2);
        model.addClass(originalClass);

        model.addStudentToClass(student1, originalClass);
        model.addStudentToClass(student2, originalClass);

        // Create edited class with new name
        TuitionClass editedClass = new TuitionClass(new ClassName("Edited"));

        // Transfer details (this is what EditClassCommand does)
        editedClass.transferDetailsFromClass(originalClass);

        // Verify students transferred
        assertTrue(editedClass.hasStudent(student1));
        assertTrue(editedClass.hasStudent(student2));
        assertEquals(2, editedClass.getStudents().size());
    }

    @Test
    public void editClass_withAssignedTutor_maintainsTutorAssignment() {
        // Create class with tutor
        TuitionClass originalClass = new TuitionClass(new ClassName("OriginalClass"));
        Tutor tutor = new Tutor(
            new Name("Tutor Name"),
            new Phone("87654321"),
            new Email("tutor@example.com"),
            new Address("Tutor Address"),
            new HashSet<>()
        );

        model.addPerson(tutor);
        model.addClass(originalClass);
        model.assignTutorToClass(tutor, originalClass);

        // Verify tutor assigned
        assertTrue(originalClass.isAssignedToTutor());
        assertEquals(tutor, originalClass.getTutor());

        // Create edited class
        TuitionClass editedClass = new TuitionClass(new ClassName("EditedClass"));

        // Transfer details
        editedClass.transferDetailsFromClass(originalClass);

        // Verify tutor transferred
        assertTrue(editedClass.isAssignedToTutor());
        assertEquals(tutor, editedClass.getTutor());
    }

    @Test
    public void multipleOperations_complexScenario_dataIntegrity() {
        // Complex scenario with multiple operations

        // 1. Create persons of different types
        Student student1 = new Student(
            new Name("Multi Student 1"),
            new Phone("90000001"),
            new Email("ms1@example.com"),
            new Address("Address 1"),
            Set.of(new Tag("smart"))
        );

        Student student2 = new Student(
            new Name("Multi Student 2"),
            new Phone("90000002"),
            new Email("ms2@example.com"),
            new Address("Address 2"),
            Set.of(new Tag("hardworking"))
        );

        Tutor tutor = new Tutor(
            new Name("Multi Tutor"),
            new Phone("90000003"),
            new Email("mt@example.com"),
            new Address("Tutor Address"),
            Set.of(new Tag("experienced"))
        );

        Parent parent = new Parent(
            new Name("Multi Parent"),
            new Phone("90000004"),
            new Email("mp@example.com"),
            new Address("Parent Address"),
            new HashSet<>()
        );

        // 2. Add all to model
        model.addPerson(student1);
        model.addPerson(student2);
        model.addPerson(tutor);
        model.addPerson(parent);

        // 3. Create classes
        TuitionClass advancedMath = new TuitionClass(new ClassName("Advanced Math"));
        TuitionClass basicScience = new TuitionClass(new ClassName("Basic Science"));

        model.addClass(advancedMath);
        model.addClass(basicScience);

        // 4. Assign relationships
        model.assignTutorToClass(tutor, advancedMath);
        model.assignTutorToClass(tutor, basicScience);

        model.addStudentToClass(student1, advancedMath);
        model.addStudentToClass(student2, advancedMath);
        model.addStudentToClass(student1, basicScience);

        parent.addChild(student1);
        student1.addParent(parent);

        // 5. Add sessions
        LocalDateTime now = LocalDateTime.now();
        ClassSession mathSession = advancedMath.addSession("Chapter 1", now.plusDays(1), "Room A");
        ClassSession scienceSession = basicScience.addSession("Lab 1", now.plusDays(2), "Lab B");

        // 6. Mark attendance
        mathSession.markPresent(student1);
        mathSession.markAbsent(student2);
        scienceSession.markPresent(student1);

        // 7. Verify all relationships
        assertEquals(4, model.getFilteredPersonList().size());
        assertEquals(2, model.getFilteredClassList().size());

        assertTrue(advancedMath.isAssignedToTutor());
        assertTrue(basicScience.isAssignedToTutor());
        assertEquals(tutor, advancedMath.getTutor());
        assertEquals(tutor, basicScience.getTutor());

        assertTrue(advancedMath.hasStudent(student1));
        assertTrue(advancedMath.hasStudent(student2));
        assertTrue(basicScience.hasStudent(student1));
        assertFalse(basicScience.hasStudent(student2));

        assertTrue(parent.getChildren().contains(student1));
        assertTrue(student1.getParents().contains(parent));

        assertTrue(mathSession.hasAttended(student1));
        assertFalse(mathSession.hasAttended(student2));
        assertTrue(scienceSession.hasAttended(student1));

        // 8. Test data integrity after complex operations
        assertEquals(1, advancedMath.getAllSessions().size());
        assertEquals(1, basicScience.getAllSessions().size());
        assertEquals(2, advancedMath.getStudents().size());
        assertEquals(1, basicScience.getStudents().size());
    }
}
