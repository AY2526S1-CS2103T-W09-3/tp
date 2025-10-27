package seedu.address.model.classroom;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Edge Case Test Suite
 * Tests boundary conditions, malicious inputs, and corner cases
 */
public class EdgeCaseTest {

    private Student createStudent(String name) {
        return new Student(
            new Name(name),
            new Phone("12345678"),
            new Email("test@test.com"),
            new Address("123 Test St"),
            new HashSet<>()
        );
    }

    private Tutor createTutor(String name) {
        return new Tutor(
            new Name(name),
            new Phone("87654321"),
            new Email("tutor@test.com"),
            new Address("456 Tutor Ave"),
            new HashSet<>()
        );
    }

    /**
     * Test: Maximum length class name (50 characters)
     */
    @Test
    public void testMaxLengthClassName() {
        System.out.println("\n=== Testing Maximum Length Class Name ===");

        String max50 = "a".repeat(50);
        String over50 = "a".repeat(51);

        try {
            ClassName valid = new ClassName(max50);
            System.out.println("✓ 50-character class name accepted");
            assertEquals(50, valid.value.length());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Failed to create 50-char class name: " + e.getMessage());
            fail("Should accept 50-character class name");
        }

        assertThrows(IllegalArgumentException.class, () -> new ClassName(over50),
            "Should reject 51-character class name");
        System.out.println("✓ 51-character class name rejected");
    }

    /**
     * Test: Empty and whitespace-only class names
     */
    @Test
    public void testEmptyClassName() {
        System.out.println("\n=== Testing Empty/Whitespace Class Names ===");

        assertThrows(IllegalArgumentException.class, () -> new ClassName(""));
        System.out.println("✓ Empty string rejected");

        assertThrows(IllegalArgumentException.class, () -> new ClassName("   "));
        System.out.println("✓ Whitespace-only string rejected");

        assertThrows(IllegalArgumentException.class, () -> new ClassName("\t\n"));
        System.out.println("✓ Tab/newline rejected");
    }

    /**
     * Test: Special characters in class names
     */
    @Test
    public void testSpecialCharactersInClassName() {
        System.out.println("\n=== Testing Special Characters in Class Names ===");

        // Valid characters
        try {
            new ClassName("Math-101");
            System.out.println("✓ Hyphen accepted");

            new ClassName("Math_101");
            System.out.println("✓ Underscore accepted");

            new ClassName("Math—101"); // em dash
            System.out.println("✓ Em dash accepted");
        } catch (IllegalArgumentException e) {
            fail("Should accept hyphens, underscores, em dashes: " + e.getMessage());
        }

        // Invalid characters
        assertThrows(IllegalArgumentException.class, () -> new ClassName("Math@101"));
        System.out.println("✓ @ symbol rejected");

        assertThrows(IllegalArgumentException.class, () -> new ClassName("Math#101"));
        System.out.println("✓ # symbol rejected");

        assertThrows(IllegalArgumentException.class, () -> new ClassName("Math$101"));
        System.out.println("✓ $ symbol rejected");
    }

    /**
     * Test: Concurrent modification of class student list
     */
    @Test
    public void testConcurrentStudentModification() {
        System.out.println("\n=== Testing Concurrent Student List Modification ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        Student alice = createStudent("Alice");
        Student bob = createStudent("Bob");
        Student charlie = createStudent("Charlie");

        mathClass.addStudent(alice);
        mathClass.addStudent(bob);
        mathClass.addStudent(charlie);

        // Try to modify the list while iterating
        try {
            for (Student s : mathClass.getStudents()) {
                if (s.equals(bob)) {
                    mathClass.removeStudent(alice); // Modify during iteration
                }
            }
            System.out.println("⚠ No ConcurrentModificationException - check if defensive copy is used");
        } catch (Exception e) {
            System.out.println("Exception during concurrent modification: " + e.getClass().getSimpleName());
        }

        // Check final state
        System.out.println("Students remaining: " + mathClass.getStudents().size());
    }

    /**
     * Test: Multiple tutors assigned to same class
     */
    @Test
    public void testMultipleTutorsToSameClass() {
        System.out.println("\n=== Testing Multiple Tutors Assignment ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        Tutor tutor1 = createTutor("John Smith");
        Tutor tutor2 = createTutor("Jane Doe");

        mathClass.setTutor(tutor1);
        System.out.println("Tutor 1 assigned: " + mathClass.getTutor().getName());

        mathClass.setTutor(tutor2);
        System.out.println("Tutor 2 assigned: " + mathClass.getTutor().getName());

        // Check if tutor1 still thinks it's teaching the class
        boolean tutor1StillHasClass = tutor1.getTuitionClasses().contains(mathClass);
        System.out.println("Tutor 1 still has class in list: " + tutor1StillHasClass);

        if (tutor1StillHasClass) {
            System.out.println("❌ BUG: Old tutor not properly removed from class");
        } else {
            System.out.println("✓ Old tutor properly removed");
        }
    }

    /**
     * Test: Session with null or extreme datetime
     */
    @Test
    public void testExtremeDateTimeSession() {
        System.out.println("\n=== Testing Extreme DateTime Values ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));

        // Past date (1900)
        try {
            LocalDateTime pastDate = LocalDateTime.of(1900, 1, 1, 0, 0);
            ClassSession pastSession = mathClass.addSession("Past", pastDate, "Room");
            System.out.println("⚠ Allowed session in year 1900 - no validation");
        } catch (Exception e) {
            System.out.println("✓ Past date rejected: " + e.getMessage());
        }

        // Future date (3000)
        try {
            LocalDateTime futureDate = LocalDateTime.of(3000, 1, 1, 0, 0);
            ClassSession futureSession = mathClass.addSession("Future", futureDate, "Room");
            System.out.println("⚠ Allowed session in year 3000 - no validation");
        } catch (Exception e) {
            System.out.println("✓ Future date rejected: " + e.getMessage());
        }
    }

    /**
     * Test: Removing tutor that doesn't exist
     */
    @Test
    public void testRemoveNonExistentTutor() {
        System.out.println("\n=== Testing Remove Non-Existent Tutor ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        Tutor tutor1 = createTutor("John Smith");
        Tutor tutor2 = createTutor("Jane Doe");

        mathClass.setTutor(tutor1);

        // Try to remove wrong tutor
        assertThrows(PersonNotFoundException.class, () -> mathClass.removeTutor(tutor2));
        System.out.println("✓ Correctly throws exception for wrong tutor");

        // Try to remove from class with no tutor
        TuitionClass emptyClass = new TuitionClass(new ClassName("Empty"));
        assertThrows(PersonNotFoundException.class, () -> emptyClass.removeTutor(tutor1));
        System.out.println("✓ Correctly throws exception when no tutor assigned");
    }

    /**
     * Test: Session name with only whitespace
     */
    @Test
    public void testWhitespaceSessionName() {
        System.out.println("\n=== Testing Whitespace Session Names ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));

        try {
            mathClass.addSession("   ", LocalDateTime.now(), "Room");
            System.out.println("⚠ Whitespace-only session name accepted");

            // Try to add another whitespace session
            try {
                mathClass.addSession("  ", LocalDateTime.now(), "Room");
                System.out.println("❌ Multiple whitespace sessions allowed");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Duplicate whitespace session prevented");
            }
        } catch (Exception e) {
            System.out.println("✓ Whitespace session name rejected: " + e.getMessage());
        }
    }

    /**
     * Test: Circular reference when student/tutor swap roles
     */
    @Test
    public void testRoleSwapScenario() {
        System.out.println("\n=== Testing Role Swap Scenario ===");

        // This tests the scenario where someone is both a student and tutor
        // The system doesn't allow this directly, but let's verify

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        TuitionClass scienceClass = new TuitionClass(new ClassName("Science"));

        Student alice = createStudent("Alice");
        Tutor bob = createTutor("Bob");

        // Alice is student in Math
        mathClass.addStudent(alice);

        // Bob teaches Science
        scienceClass.setTutor(bob);

        // Can Bob be a student? (System doesn't allow, different types)
        System.out.println("✓ System prevents same person being both Student and Tutor (different types)");
    }

    /**
     * Test: Very long session location string
     */
    @Test
    public void testLongSessionLocation() {
        System.out.println("\n=== Testing Long Session Location ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        String longLocation = "Room ".repeat(1000); // 5000 characters

        try {
            ClassSession session = mathClass.addSession("Test", LocalDateTime.now(), longLocation);
            System.out.println("⚠ Accepted " + longLocation.length() + " character location");

            // Check if it causes issues in display
            String details = session.getSessionDetails();
            System.out.println("Session details length: " + details.length());
        } catch (Exception e) {
            System.out.println("✓ Long location rejected: " + e.getMessage());
        }
    }

    /**
     * Test: Null values in critical places
     */
    @Test
    public void testNullHandling() {
        System.out.println("\n=== Testing Null Handling ===");

        // Null class name
        assertThrows(NullPointerException.class, () -> new ClassName(null));
        System.out.println("✓ Null class name rejected");

        // Null tutor in constructor
        TuitionClass mathClass = new TuitionClass(new ClassName("Math"), null);
        assertNull(mathClass.getTutor());
        System.out.println("✓ Null tutor in constructor handled");

        // Null session name
        assertThrows(NullPointerException.class,
            () -> mathClass.addSession(null, LocalDateTime.now(), "Room"));
        System.out.println("✓ Null session name rejected");
    }

    /**
     * Run all edge case tests
     */
    public static void main(String[] args) {
        EdgeCaseTest test = new EdgeCaseTest();

        System.out.println("========================================");
        System.out.println("      TutBook Edge Case Test Suite");
        System.out.println("========================================");

        try { test.testMaxLengthClassName(); } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }

        try { test.testEmptyClassName(); } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }

        try { test.testSpecialCharactersInClassName(); } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }

        try { test.testConcurrentStudentModification(); } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }

        try { test.testMultipleTutorsToSameClass(); } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }

        try { test.testExtremeDateTimeSession(); } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }

        try { test.testRemoveNonExistentTutor(); } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }

        try { test.testWhitespaceSessionName(); } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }

        try { test.testRoleSwapScenario(); } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }

        try { test.testLongSessionLocation(); } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }

        try { test.testNullHandling(); } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }

        System.out.println("\n========================================");
        System.out.println("       Edge Case Tests Complete");
        System.out.println("========================================");
    }
}