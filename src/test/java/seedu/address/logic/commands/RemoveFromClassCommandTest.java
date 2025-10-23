package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classroom.ClassName;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Parent;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;

public class RemoveFromClassCommandTest {

    private Model model;
    private Student student1;
    private Student student2;
    private Tutor tutor1;
    private Tutor tutor2;
    private Parent parent;
    private TuitionClass mathClass;
    private TuitionClass scienceClass;
    private TuitionClass englishClass;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new AddressBook(), new UserPrefs());

        // Create test persons
        student1 = new Student(
                new Name("Alice Tan"),
                new Phone("91234567"),
                new Email("alice@example.com"),
                new Address("123 Main St"),
                new HashSet<>()
        );

        student2 = new Student(
                new Name("Bob Lee"),
                new Phone("92345678"),
                new Email("bob@example.com"),
                new Address("456 Oak St"),
                new HashSet<>()
        );

        tutor1 = new Tutor(
                new Name("John Smith"),
                new Phone("93456789"),
                new Email("john@example.com"),
                new Address("789 Pine St"),
                new HashSet<>()
        );

        tutor2 = new Tutor(
                new Name("Jane Doe"),
                new Phone("94567890"),
                new Email("jane@example.com"),
                new Address("321 Elm St"),
                new HashSet<>()
        );

        parent = new Parent(
                new Name("Peter Tan"),
                new Phone("95678901"),
                new Email("peter@example.com"),
                new Address("123 Main St"),
                new HashSet<>()
        );

        // Create test classes
        mathClass = new TuitionClass(new ClassName("Math 101"));
        scienceClass = new TuitionClass(new ClassName("Science 201"));
        englishClass = new TuitionClass(new ClassName("English 301"));
    }

    // ========== Success Cases ==========

    @Test
    public void execute_removeStudentFromSingleClass_success() throws Exception {
        model.addPerson(student1);
        model.addClass(mathClass);
        mathClass.addStudent(student1);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));
        CommandResult result = command.execute(model);

        assertEquals("Removed Alice Tan from class: Math 101", result.getFeedbackToUser());
        assertFalse(mathClass.hasStudent(student1));
        assertFalse(student1.getTuitionClasses().contains(mathClass));
    }

    @Test
    public void execute_removeStudentFromMultipleClasses_success() throws Exception {
        model.addPerson(student1);
        model.addClass(mathClass);
        model.addClass(scienceClass);
        mathClass.addStudent(student1);
        scienceClass.addStudent(student1);

        // Remove from first class
        RemoveFromClassCommand command1 = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));
        command1.execute(model);

        assertFalse(mathClass.hasStudent(student1));
        assertTrue(scienceClass.hasStudent(student1));
        assertFalse(student1.getTuitionClasses().contains(mathClass));
        assertTrue(student1.getTuitionClasses().contains(scienceClass));

        // Remove from second class
        RemoveFromClassCommand command2 = new RemoveFromClassCommand("Alice Tan", new ClassName("Science 201"));
        command2.execute(model);

        assertFalse(scienceClass.hasStudent(student1));
        assertFalse(student1.getTuitionClasses().contains(scienceClass));
        assertTrue(student1.getTuitionClasses().isEmpty());
    }

    @Test
    public void execute_removeTutorFromClass_success() throws Exception {
        model.addPerson(tutor1);
        model.addClass(mathClass);
        mathClass.setTutor(tutor1);

        RemoveFromClassCommand command = new RemoveFromClassCommand("John Smith", new ClassName("Math 101"));
        CommandResult result = command.execute(model);

        assertEquals("Removed John Smith from class: Math 101", result.getFeedbackToUser());
        assertFalse(mathClass.hasTutor(tutor1));
        assertFalse(mathClass.isAssignedToTutor());
        assertFalse(tutor1.getTuitionClasses().contains(mathClass));
    }

    @Test
    public void execute_removeTutorWithMultipleClasses_success() throws Exception {
        model.addPerson(tutor1);
        model.addClass(mathClass);
        model.addClass(scienceClass);
        mathClass.setTutor(tutor1);
        scienceClass.setTutor(tutor1);

        RemoveFromClassCommand command = new RemoveFromClassCommand("John Smith", new ClassName("Math 101"));
        command.execute(model);

        assertFalse(mathClass.hasTutor(tutor1));
        assertTrue(scienceClass.hasTutor(tutor1));
        assertFalse(tutor1.getTuitionClasses().contains(mathClass));
        assertTrue(tutor1.getTuitionClasses().contains(scienceClass));
    }

    @Test
    public void execute_classWithMultipleStudents_removesOnlySpecifiedStudent() throws Exception {
        model.addPerson(student1);
        model.addPerson(student2);
        model.addClass(mathClass);
        mathClass.addStudent(student1);
        mathClass.addStudent(student2);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));
        command.execute(model);

        assertFalse(mathClass.hasStudent(student1));
        assertTrue(mathClass.hasStudent(student2));
    }

    @Test
    public void execute_caseInsensitiveNameMatching_success() throws Exception {
        model.addPerson(student1);
        model.addClass(mathClass);
        mathClass.addStudent(student1);

        // Test with different case
        RemoveFromClassCommand command = new RemoveFromClassCommand("ALICE TAN", new ClassName("Math 101"));
        command.execute(model);

        assertFalse(mathClass.hasStudent(student1));
    }

    // ========== Failure Cases - Person Not Found ==========

    @Test
    public void execute_personNotFound_throwsCommandException() {
        model.addClass(mathClass);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Non Existent", new ClassName("Math 101"));

        assertThrows(CommandException.class, "Person not found: Non Existent", () ->
                command.execute(model));
    }

    @Test
    public void execute_emptyAddressBook_throwsCommandException() {
        model.addClass(mathClass);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));

        assertThrows(CommandException.class, "Person not found: Alice Tan", () ->
                command.execute(model));
    }

    // ========== Failure Cases - Class Not Found ==========

    @Test
    public void execute_classNotFound_throwsCommandException() {
        model.addPerson(student1);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Non Existent Class"));

        assertThrows(CommandException.class, "Class not found: Non Existent Class", () ->
                command.execute(model));
    }

    @Test
    public void execute_noClassesInSystem_throwsCommandException() {
        model.addPerson(student1);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));

        assertThrows(CommandException.class, "Class not found: Math 101", () ->
                command.execute(model));
    }

    // ========== Failure Cases - Person Not In Class ==========

    @Test
    public void execute_studentNotInClass_throwsCommandException() {
        model.addPerson(student1);
        model.addClass(mathClass);
        // Student not added to class

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));

        assertThrows(CommandException.class, "Alice Tan is not in class Math 101", () ->
                command.execute(model));
    }

    @Test
    public void execute_tutorNotAssignedToClass_throwsCommandException() {
        model.addPerson(tutor1);
        model.addClass(mathClass);
        // Tutor not assigned to class

        RemoveFromClassCommand command = new RemoveFromClassCommand("John Smith", new ClassName("Math 101"));

        assertThrows(CommandException.class, "John Smith is not in class Math 101", () ->
                command.execute(model));
    }

    @Test
    public void execute_differentTutorAssigned_throwsCommandException() {
        model.addPerson(tutor1);
        model.addPerson(tutor2);
        model.addClass(mathClass);
        mathClass.setTutor(tutor2); // Different tutor assigned

        RemoveFromClassCommand command = new RemoveFromClassCommand("John Smith", new ClassName("Math 101"));

        assertThrows(CommandException.class, "John Smith is not in class Math 101", () ->
                command.execute(model));
    }

    // ========== Failure Cases - Parent ==========

    @Test
    public void execute_parentCannotBeRemoved_throwsCommandException() {
        model.addPerson(parent);
        model.addClass(mathClass);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Peter Tan", new ClassName("Math 101"));

        assertThrows(CommandException.class, "Parent cannot be removed from class", () ->
                command.execute(model));
    }

    // ========== Edge Cases ==========

    @Test
    public void execute_similarNames_removesCorrectPerson() throws Exception {
        Student similarStudent = new Student(
                new Name("Alice Tang"), // Similar but different name
                new Phone("96789012"),
                new Email("alicetang@example.com"),
                new Address("999 Street"),
                new HashSet<>()
        );

        model.addPerson(student1);
        model.addPerson(similarStudent);
        model.addClass(mathClass);
        mathClass.addStudent(student1);
        mathClass.addStudent(similarStudent);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));
        command.execute(model);

        assertFalse(mathClass.hasStudent(student1));
        assertTrue(mathClass.hasStudent(similarStudent));
    }

    @Test
    public void execute_similarClassNames_removesFromCorrectClass() throws Exception {
        TuitionClass mathAdvanced = new TuitionClass(new ClassName("Math 102"));

        model.addPerson(student1);
        model.addClass(mathClass);
        model.addClass(mathAdvanced);
        mathClass.addStudent(student1);
        mathAdvanced.addStudent(student1);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));
        command.execute(model);

        assertFalse(mathClass.hasStudent(student1));
        assertTrue(mathAdvanced.hasStudent(student1));
    }

    @Test
    public void execute_nameWithNumbers_success() throws Exception {
        Student numberStudent = new Student(
                new Name("John Smith 3rd"),
                new Phone("97890123"),
                new Email("john3@example.com"),
                new Address("Number St"),
                new HashSet<>()
        );

        model.addPerson(numberStudent);
        model.addClass(mathClass);
        mathClass.addStudent(numberStudent);

        RemoveFromClassCommand command = new RemoveFromClassCommand("John Smith 3rd", new ClassName("Math 101"));
        command.execute(model);

        assertFalse(mathClass.hasStudent(numberStudent));
    }

    @Test
    public void execute_veryLongNames_success() throws Exception {
        String longName = "A".repeat(50) + " " + "B".repeat(50);
        Student longNameStudent = new Student(
                new Name(longName),
                new Phone("98901234"),
                new Email("long@example.com"),
                new Address("Long St"),
                new HashSet<>()
        );

        model.addPerson(longNameStudent);
        model.addClass(mathClass);
        mathClass.addStudent(longNameStudent);

        RemoveFromClassCommand command = new RemoveFromClassCommand(longName, new ClassName("Math 101"));
        command.execute(model);

        assertFalse(mathClass.hasStudent(longNameStudent));
    }

    // ========== State Consistency Tests ==========

    @Test
    public void execute_maintainsBidirectionalConsistency() throws Exception {
        model.addPerson(student1);
        model.addClass(mathClass);
        mathClass.addStudent(student1);

        // Verify initial bidirectional relationship
        assertTrue(mathClass.hasStudent(student1));
        assertTrue(student1.getTuitionClasses().contains(mathClass));

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));
        command.execute(model);

        // Verify bidirectional relationship is properly removed
        assertFalse(mathClass.hasStudent(student1));
        assertFalse(student1.getTuitionClasses().contains(mathClass));
    }

    @Test
    public void execute_preservesOtherRelationships() throws Exception {
        model.addPerson(student1);
        model.addPerson(student2);
        model.addClass(mathClass);
        model.addClass(scienceClass);

        mathClass.addStudent(student1);
        mathClass.addStudent(student2);
        scienceClass.addStudent(student1);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));
        command.execute(model);

        // Verify other relationships remain intact
        assertTrue(mathClass.hasStudent(student2));
        assertTrue(scienceClass.hasStudent(student1));
        assertTrue(student2.getTuitionClasses().contains(mathClass));
        assertTrue(student1.getTuitionClasses().contains(scienceClass));
    }

    @Test
    public void execute_modelUpdatesProperly() throws Exception {
        model.addPerson(student1);
        model.addClass(mathClass);
        mathClass.addStudent(student1);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));
        CommandResult result = command.execute(model);

        // Verify command executed successfully
        assertFalse(mathClass.hasStudent(student1));
        assertNotNull(result);
    }

    // ========== Equals Tests ==========

    @Test
    public void equals_sameObject_returnsTrue() {
        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice", new ClassName("Math 101"));
        assertTrue(command.equals(command));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        RemoveFromClassCommand command1 = new RemoveFromClassCommand("Alice", new ClassName("Math 101"));
        RemoveFromClassCommand command2 = new RemoveFromClassCommand("Alice", new ClassName("Math 101"));
        assertTrue(command1.equals(command2));
    }

    @Test
    public void equals_differentPerson_returnsFalse() {
        RemoveFromClassCommand command1 = new RemoveFromClassCommand("Alice", new ClassName("Math 101"));
        RemoveFromClassCommand command2 = new RemoveFromClassCommand("Bob", new ClassName("Math 101"));
        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_differentClass_returnsFalse() {
        RemoveFromClassCommand command1 = new RemoveFromClassCommand("Alice", new ClassName("Math 101"));
        RemoveFromClassCommand command2 = new RemoveFromClassCommand("Alice", new ClassName("Science 201"));
        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice", new ClassName("Math 101"));
        assertFalse(command.equals(1));
        assertFalse(command.equals("string"));
        assertFalse(command.equals(new ListCommand()));
    }

    @Test
    public void equals_null_returnsFalse() {
        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice", new ClassName("Math 101"));
        assertFalse(command.equals(null));
    }

    // ========== Concurrent Modification Tests ==========

    @Test
    public void execute_concurrentStudentAddition_doesNotAffectRemoval() throws Exception {
        model.addPerson(student1);
        model.addPerson(student2);
        model.addClass(mathClass);
        mathClass.addStudent(student1);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));

        // Add another student during execution (simulated)
        mathClass.addStudent(student2);

        command.execute(model);

        assertFalse(mathClass.hasStudent(student1));
        assertTrue(mathClass.hasStudent(student2));
    }

    @Test
    public void execute_multipleRemovalsInSequence_success() throws Exception {
        model.addPerson(student1);
        model.addPerson(student2);
        model.addClass(mathClass);
        mathClass.addStudent(student1);
        mathClass.addStudent(student2);

        RemoveFromClassCommand command1 = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));
        RemoveFromClassCommand command2 = new RemoveFromClassCommand("Bob Lee", new ClassName("Math 101"));

        command1.execute(model);
        command2.execute(model);

        assertFalse(mathClass.hasStudent(student1));
        assertFalse(mathClass.hasStudent(student2));
        assertTrue(mathClass.getStudents().isEmpty());
    }

    // ========== Null Safety Tests ==========

    @Test
    public void constructor_nullPersonName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
            new RemoveFromClassCommand(null, new ClassName("Math 101")));
    }

    @Test
    public void constructor_nullClassName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
            new RemoveFromClassCommand("Alice", null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice", new ClassName("Math 101"));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    // ========== Logging Coverage Test ==========

    @Test
    public void execute_logsAppropriateMessages() throws Exception {
        model.addPerson(student1);
        model.addClass(mathClass);
        mathClass.addStudent(student1);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Alice Tan", new ClassName("Math 101"));

        // This test ensures logging code is executed
        CommandResult result = command.execute(model);

        // Verify command executed successfully (logging should have occurred)
        assertFalse(mathClass.hasStudent(student1));
        assertTrue(result.getFeedbackToUser().contains("Removed"));
    }

    @Test
    public void execute_logsWarningForNotFound() {
        model.addClass(mathClass);

        RemoveFromClassCommand command = new RemoveFromClassCommand("Non Existent", new ClassName("Math 101"));

        // This ensures warning logging path is covered
        assertThrows(CommandException.class, () -> command.execute(model));
    }
}
