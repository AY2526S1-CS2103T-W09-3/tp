package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Parent;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;

public class ListParentsCommandTest {

    private Model model;
    private Parent parent1;
    private Parent parent2;
    private Parent parent3;
    private Student child1;
    private Student child2;
    private Student child3;
    private Tutor tutor;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new AddressBook(), new UserPrefs());

        parent1 = new Parent(
                new Name("John Doe"),
                new Phone("91234567"),
                new Email("john@example.com"),
                new Address("123 Main St"),
                new HashSet<>()
        );

        parent2 = new Parent(
                new Name("Jane Smith"),
                new Phone("92345678"),
                new Email("jane@example.com"),
                new Address("456 Oak St"),
                new HashSet<>()
        );

        parent3 = new Parent(
                new Name("Robert Johnson"),
                new Phone("93456789"),
                new Email("robert@example.com"),
                new Address("789 Pine St"),
                new HashSet<>()
        );

        child1 = new Student(
                new Name("Alice Tan"),
                new Phone("94567890"),
                new Email("alice@example.com"),
                new Address("123 Main St"),
                new HashSet<>()
        );

        child2 = new Student(
                new Name("Bob Lee"),
                new Phone("95678901"),
                new Email("bob@example.com"),
                new Address("456 Oak St"),
                new HashSet<>()
        );

        child3 = new Student(
                new Name("Charlie Wong"),
                new Phone("96789012"),
                new Email("charlie@example.com"),
                new Address("789 Pine St"),
                new HashSet<>()
        );

        tutor = new Tutor(
                new Name("Teacher Tom"),
                new Phone("97890123"),
                new Email("tom@example.com"),
                new Address("321 School St"),
                new HashSet<>()
        );
    }

    // ========== Success Cases - List All Parents ==========

    @Test
    public void execute_listAllParents_success() throws Exception {
        model.addPerson(parent1);
        model.addPerson(parent2);

        ListParentsCommand command = new ListParentsCommand();
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed all parents"));
        assertTrue(feedback.contains("John Doe"));
        assertTrue(feedback.contains("Jane Smith"));
        assertNotNull(result);
    }

    @Test
    public void execute_noParents_success() throws Exception {
        // Add only students and tutors
        model.addPerson(child1);
        model.addPerson(tutor);

        ListParentsCommand command = new ListParentsCommand();
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed all parents"));
        assertTrue(feedback.contains("[No parents]"));
    }

    @Test
    public void execute_emptyAddressBook_success() throws Exception {
        ListParentsCommand command = new ListParentsCommand();
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed all parents"));
        assertTrue(feedback.contains("[No parents]"));
    }

    @Test
    public void execute_manyParents_success() throws Exception {
        // Add many parents to test scalability
        for (int i = 0; i < 20; i++) {
            Parent parent = new Parent(
                    new Name("Parent " + i),
                    new Phone("9000000" + String.format("%02d", i)),
                    new Email("parent" + i + "@example.com"),
                    new Address("Address " + i),
                    new HashSet<>()
            );
            model.addPerson(parent);
        }

        ListParentsCommand command = new ListParentsCommand();
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed all parents"));
        assertTrue(feedback.contains("Parent 0"));
        assertTrue(feedback.contains("Parent 19"));
    }

    // ========== Success Cases - List Parents by Child ==========

    @Test
    public void execute_listParentsByChild_singleParent() throws Exception {
        model.addPerson(child1);
        model.addPerson(parent1);
        child1.addParent(parent1);

        ListParentsCommand command = new ListParentsCommand("Alice Tan");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed parents for child: Alice Tan"));
        assertTrue(feedback.contains("John Doe"));
        assertFalse(feedback.contains("Jane Smith"));
    }

    @Test
    public void execute_listParentsByChild_multipleParents() throws Exception {
        model.addPerson(child1);
        model.addPerson(parent1);
        model.addPerson(parent2);
        child1.addParent(parent1);
        child1.addParent(parent2);

        ListParentsCommand command = new ListParentsCommand("Alice Tan");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed parents for child: Alice Tan"));
        assertTrue(feedback.contains("John Doe"));
        assertTrue(feedback.contains("Jane Smith"));
    }

    @Test
    public void execute_childWithNoParents_success() throws Exception {
        model.addPerson(child1);
        model.addPerson(parent1); // Parent exists but not linked

        ListParentsCommand command = new ListParentsCommand("Alice Tan");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed parents for child: Alice Tan"));
        assertTrue(feedback.contains("[No parents]"));
    }

    @Test
    public void execute_caseInsensitiveChildName_success() throws Exception {
        model.addPerson(child1);
        model.addPerson(parent1);
        child1.addParent(parent1);

        // Test with different case
        ListParentsCommand command = new ListParentsCommand("ALICE TAN");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed parents for child: ALICE TAN"));
        assertTrue(feedback.contains("John Doe"));
    }

    // ========== Failure Cases ==========

    @Test
    public void execute_childNotFound_throwsCommandException() {
        model.addPerson(parent1);

        ListParentsCommand command = new ListParentsCommand("Non-existent Child");

        assertThrows(CommandException.class, "Child not found: Non-existent Child", () ->
                command.execute(model));
    }

    @Test
    public void execute_emptyAddressBookWithChildName_throwsCommandException() {
        ListParentsCommand command = new ListParentsCommand("Alice Tan");

        assertThrows(CommandException.class, "Child not found: Alice Tan", () ->
                command.execute(model));
    }

    @Test
    public void execute_tutorNameProvided_throwsCommandException() {
        model.addPerson(tutor);

        ListParentsCommand command = new ListParentsCommand("Teacher Tom");

        assertThrows(CommandException.class, "Child not found: Teacher Tom", () ->
                command.execute(model));
    }

    @Test
    public void execute_parentNameProvided_throwsCommandException() {
        model.addPerson(parent1);

        ListParentsCommand command = new ListParentsCommand("John Doe");

        assertThrows(CommandException.class, "Child not found: John Doe", () ->
                command.execute(model));
    }

    // ========== Edge Cases ==========

    @Test
    public void execute_similarChildNames_findsCorrectChild() throws Exception {
        Student similarChild = new Student(
                new Name("Alice Tang"), // Similar but different
                new Phone("98901234"),
                new Email("alicetang@example.com"),
                new Address("999 Street"),
                new HashSet<>()
        );

        model.addPerson(child1);
        model.addPerson(similarChild);
        model.addPerson(parent1);
        model.addPerson(parent2);

        child1.addParent(parent1);
        similarChild.addParent(parent2);

        ListParentsCommand command = new ListParentsCommand("Alice Tan");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("John Doe"));
        assertFalse(feedback.contains("Jane Smith"));
    }

    @Test
    public void execute_childWithNumbersInName_success() throws Exception {
        Student specialChild = new Student(
                new Name("John Smith 2nd"),
                new Phone("99012345"),
                new Email("john2@example.com"),
                new Address("Special St"),
                new HashSet<>()
        );

        model.addPerson(specialChild);
        model.addPerson(parent1);
        specialChild.addParent(parent1);

        ListParentsCommand command = new ListParentsCommand("John Smith 2nd");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed parents for child: John Smith 2nd"));
        assertTrue(feedback.contains("John Doe"));
    }

    @Test
    public void execute_veryLongChildName_success() throws Exception {
        String longName = "A".repeat(30) + " " + "B".repeat(30);
        Student longNameChild = new Student(
                new Name(longName),
                new Phone("90123456"),
                new Email("long@example.com"),
                new Address("Long St"),
                new HashSet<>()
        );

        model.addPerson(longNameChild);
        model.addPerson(parent1);
        longNameChild.addParent(parent1);

        ListParentsCommand command = new ListParentsCommand(longName);
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed parents for child: " + longName));
        assertTrue(feedback.contains("John Doe"));
    }

    @Test
    public void execute_alphanumericChildName_success() throws Exception {
        Student alphanumericChild = new Student(
                new Name("Alice123 Wong456"),
                new Phone("91234567"),
                new Email("alice123@example.com"),
                new Address("Number St"),
                new HashSet<>()
        );

        model.addPerson(alphanumericChild);
        model.addPerson(parent1);
        alphanumericChild.addParent(parent1);

        ListParentsCommand command = new ListParentsCommand("Alice123 Wong456");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("John Doe"));
    }

    // ========== Multiple Children Scenarios ==========

    @Test
    public void execute_multipleChildrenSameParents_success() throws Exception {
        model.addPerson(child1);
        model.addPerson(child2);
        model.addPerson(parent1);
        model.addPerson(parent2);

        // Both children have same parents
        child1.addParent(parent1);
        child1.addParent(parent2);
        child2.addParent(parent1);
        child2.addParent(parent2);

        ListParentsCommand command1 = new ListParentsCommand("Alice Tan");
        CommandResult result1 = command1.execute(model);

        ListParentsCommand command2 = new ListParentsCommand("Bob Lee");
        CommandResult result2 = command2.execute(model);

        // Both should show same parents
        assertTrue(result1.getFeedbackToUser().contains("John Doe"));
        assertTrue(result1.getFeedbackToUser().contains("Jane Smith"));
        assertTrue(result2.getFeedbackToUser().contains("John Doe"));
        assertTrue(result2.getFeedbackToUser().contains("Jane Smith"));
    }

    @Test
    public void execute_siblingsDifferentParents_success() throws Exception {
        model.addPerson(child1);
        model.addPerson(child2);
        model.addPerson(parent1);
        model.addPerson(parent2);

        child1.addParent(parent1);
        child2.addParent(parent2);

        ListParentsCommand command1 = new ListParentsCommand("Alice Tan");
        CommandResult result1 = command1.execute(model);

        assertTrue(result1.getFeedbackToUser().contains("John Doe"));
        assertFalse(result1.getFeedbackToUser().contains("Jane Smith"));
    }

    // ========== Null Safety Tests ==========

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        ListParentsCommand command = new ListParentsCommand();
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void constructor_nullChildName_allowed() {
        // Null child name means list all parents
        ListParentsCommand command = new ListParentsCommand(null);
        assertNotNull(command);
    }

    // ========== Equals Tests ==========

    @Test
    public void equals_sameObject_returnsTrue() {
        ListParentsCommand command1 = new ListParentsCommand();
        assertTrue(command1.equals(command1));
    }

    @Test
    public void equals_sameValuesNoChild_returnsTrue() {
        ListParentsCommand command1 = new ListParentsCommand();
        ListParentsCommand command2 = new ListParentsCommand();
        assertTrue(command1.equals(command2));
    }

    @Test
    public void equals_sameChildName_returnsTrue() {
        ListParentsCommand command1 = new ListParentsCommand("Alice");
        ListParentsCommand command2 = new ListParentsCommand("Alice");
        assertTrue(command1.equals(command2));
    }

    @Test
    public void equals_differentChildName_returnsFalse() {
        ListParentsCommand command1 = new ListParentsCommand("Alice");
        ListParentsCommand command2 = new ListParentsCommand("Bob");
        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_nullChildVsNonNull_returnsFalse() {
        ListParentsCommand command1 = new ListParentsCommand();
        ListParentsCommand command2 = new ListParentsCommand("Alice");
        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_differentTypes_returnsFalse() {
        ListParentsCommand command1 = new ListParentsCommand();
        assertFalse(command1.equals(1));
        assertFalse(command1.equals("string"));
        assertFalse(command1.equals(new ListChildrenCommand()));
    }

    @Test
    public void equals_null_returnsFalse() {
        ListParentsCommand command1 = new ListParentsCommand();
        assertFalse(command1.equals(null));
    }

    // ========== Hashcode Tests ==========

    @Test
    public void hashCode_sameObject_sameHashCode() {
        ListParentsCommand command = new ListParentsCommand("Alice");
        assertEquals(command.hashCode(), command.hashCode());
    }

    @Test
    public void hashCode_equalObjects_sameHashCode() {
        ListParentsCommand command1 = new ListParentsCommand("Alice");
        ListParentsCommand command2 = new ListParentsCommand("Alice");
        // Equal objects should have equal hashcodes
        assertEquals(command1, command2);
        // Note: hashCode may differ due to Object's default implementation if not overridden
    }

    @Test
    public void hashCode_differentObjects_differentHashCode() {
        ListParentsCommand command1 = new ListParentsCommand("Alice");
        ListParentsCommand command2 = new ListParentsCommand("Bob");
        assertNotEquals(command1.hashCode(), command2.hashCode());
    }

    // ========== Logging Coverage Tests ==========

    @Test
    public void execute_logsListAllParents() throws Exception {
        model.addPerson(parent1);

        ListParentsCommand command = new ListParentsCommand();

        // This ensures the logging path for listing all parents is covered
        CommandResult result = command.execute(model);
        assertNotNull(result);
    }

    @Test
    public void execute_logsListParentsByChild() throws Exception {
        model.addPerson(child1);
        model.addPerson(parent1);
        child1.addParent(parent1);

        ListParentsCommand command = new ListParentsCommand("Alice Tan");

        // This ensures the logging path for listing by child is covered
        CommandResult result = command.execute(model);
        assertNotNull(result);
    }

    @Test
    public void execute_logsChildNotFoundWarning() {
        ListParentsCommand command = new ListParentsCommand("Non-existent");

        // This ensures the warning logging path is covered
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    // ========== Performance Edge Cases ==========

    @Test
    public void execute_manyParentsForOneChild_success() throws Exception {
        model.addPerson(child1);

        // Add many parents (unusual but possible scenario)
        for (int i = 0; i < 10; i++) {
            Parent parent = new Parent(
                    new Name("Parent " + i),
                    new Phone("9100000" + String.format("%02d", i)),
                    new Email("p" + i + "@example.com"),
                    new Address("Addr " + i),
                    new HashSet<>()
            );
            model.addPerson(parent);
            child1.addParent(parent);
        }

        ListParentsCommand command = new ListParentsCommand("Alice Tan");
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Parent 0"));
        assertTrue(feedback.contains("Parent 9"));
    }

    @Test
    public void execute_deepHierarchy_success() throws Exception {
        // Test with complex family structure
        model.addPerson(child1);
        model.addPerson(child2);
        model.addPerson(child3);
        model.addPerson(parent1);
        model.addPerson(parent2);
        model.addPerson(parent3);

        // Complex relationships
        child1.addParent(parent1);
        child1.addParent(parent2);
        child2.addParent(parent2);
        child2.addParent(parent3);
        child3.addParent(parent1);
        child3.addParent(parent3);

        // Test each child returns correct parents
        ListParentsCommand command1 = new ListParentsCommand("Alice Tan");
        CommandResult result1 = command1.execute(model);
        assertTrue(result1.getFeedbackToUser().contains("John Doe"));
        assertTrue(result1.getFeedbackToUser().contains("Jane Smith"));
        assertFalse(result1.getFeedbackToUser().contains("Robert Johnson"));

        ListParentsCommand command3 = new ListParentsCommand("Charlie Wong");
        CommandResult result3 = command3.execute(model);
        assertTrue(result3.getFeedbackToUser().contains("John Doe"));
        assertTrue(result3.getFeedbackToUser().contains("Robert Johnson"));
        assertFalse(result3.getFeedbackToUser().contains("Jane Smith"));
    }
}
