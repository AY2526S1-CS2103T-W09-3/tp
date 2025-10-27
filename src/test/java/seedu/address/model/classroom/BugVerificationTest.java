package seedu.address.model.classroom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;
import seedu.address.model.tag.Tag;

/**
 * Bug Verification Test Suite
 * Tests to confirm or deny the existence of suspected bugs in TutBook
 */
public class BugVerificationTest {

    // Helper method to create a test student
    private Student createStudent(String name) {
        return new Student(
            new Name(name),
            new Phone("12345678"),
            new Email("test@test.com"),
            new Address("123 Test St"),
            new HashSet<>()
        );
    }

    // Helper method to create a test tutor
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
     * BUG #1: Test if TuitionClass constructor creates duplicate class entries in tutor
     * SUSPECTED: Constructor calls tutor.addClass() and setTutor() might do it again
     */
    @Test
    public void testBug1_BidirectionalLinkDuplicate() {
        System.out.println("\n=== Testing Bug #1: Bidirectional Link Duplicate ===");

        Tutor tutor = createTutor("John Smith");
        ClassName className = new ClassName("Math101");

        // Create class with tutor in constructor
        TuitionClass mathClass = new TuitionClass(className, tutor);

        // Check how many times this class appears in tutor's list
        int count = 0;
        for (TuitionClass tc : tutor.getTuitionClasses()) {
            if (tc.equals(mathClass)) {
                count++;
            }
        }

        System.out.println("Classes in tutor list after constructor: " + count);
        System.out.println("Expected: 1, Actual: " + count);

        if (count != 1) {
            System.out.println("❌ BUG CONFIRMED: Constructor creates duplicate entries");
        } else {
            System.out.println("✓ Bug NOT found: Constructor works correctly");

            // Test if setTutor creates duplicates
            mathClass.setTutor(tutor);
            count = 0;
            for (TuitionClass tc : tutor.getTuitionClasses()) {
                if (tc.equals(mathClass)) {
                    count++;
                }
            }
            System.out.println("After setTutor with same tutor: " + count);
            if (count != 1) {
                System.out.println("❌ BUG CONFIRMED: setTutor creates duplicate");
            } else {
                System.out.println("✓ Bug NOT found: setTutor handles same tutor correctly");
            }
        }
    }

    /**
     * BUG #2: Test Unicode normalization inconsistency
     * SUSPECTED: ClassName normalizes but name comparisons don't
     */
    @Test
    public void testBug2_UnicodeNormalization() {
        System.out.println("\n=== Testing Bug #2: Unicode Normalization ===");

        // Create two representations of "Café" - one with combining accent
        String composed = "Café"; // é as single character
        String decomposed = "Cafe\u0301"; // e + combining acute accent

        System.out.println("Composed equals decomposed (raw): " + composed.equals(decomposed));
        System.out.println("Composed: " + composed + " (length=" + composed.length() + ")");
        System.out.println("Decomposed: " + decomposed + " (length=" + decomposed.length() + ")");

        try {
            ClassName class1 = new ClassName(composed);
            ClassName class2 = new ClassName(decomposed);

            boolean equal = class1.equals(class2);
            System.out.println("ClassName handles Unicode normalization: " + equal);

            if (equal) {
                System.out.println("✓ ClassName correctly normalizes Unicode");
            } else {
                System.out.println("❌ BUG CONFIRMED: ClassName doesn't normalize properly");
            }
        } catch (Exception e) {
            System.out.println("Exception creating class names: " + e.getMessage());
        }

        // Test Name class
        try {
            Name name1 = new Name(composed);
            Name name2 = new Name(decomposed);

            boolean nameEqual = name1.equals(name2);
            System.out.println("Name class handles Unicode: " + nameEqual);

            if (!nameEqual) {
                System.out.println("❌ BUG CONFIRMED: Name doesn't normalize Unicode");
            } else {
                System.out.println("✓ Name correctly handles Unicode");
            }
        } catch (Exception e) {
            System.out.println("Exception with names: " + e.getMessage());
        }
    }

    /**
     * BUG #3: Test whitespace handling inconsistency
     * SUSPECTED: Different whitespace handling in different parts
     */
    @Test
    public void testBug3_WhitespaceHandling() {
        System.out.println("\n=== Testing Bug #3: Whitespace Handling ===");

        String singleSpace = "John Doe";
        String doubleSpace = "John  Doe";
        String leadingSpace = " John Doe";
        String trailingSpace = "John Doe ";
        String tabSpace = "John\tDoe";

        // Test ClassName whitespace handling
        try {
            ClassName cn1 = new ClassName(singleSpace);
            ClassName cn2 = new ClassName(doubleSpace);
            ClassName cn3 = new ClassName(leadingSpace);
            ClassName cn4 = new ClassName(trailingSpace);

            System.out.println("ClassName comparison:");
            System.out.println("  Single vs Double space: " + cn1.equals(cn2));
            System.out.println("  Single vs Leading space: " + cn1.equals(cn3));
            System.out.println("  Single vs Trailing space: " + cn1.equals(cn4));

            if (cn1.equals(cn2) && cn1.equals(cn3) && cn1.equals(cn4)) {
                System.out.println("✓ ClassName normalizes whitespace correctly");
            } else {
                System.out.println("⚠ ClassName whitespace handling inconsistent");
            }
        } catch (Exception e) {
            System.out.println("Exception in ClassName: " + e.getMessage());
        }

        // Test Name whitespace handling
        try {
            Name n1 = new Name(singleSpace);
            Name n2 = new Name(doubleSpace);

            System.out.println("\nName comparison:");
            System.out.println("  'John Doe' vs 'John  Doe': " + n1.equals(n2));
            System.out.println("  fullName comparison: " + n1.fullName.equals(n2.fullName));

            if (!n1.equals(n2)) {
                System.out.println("❌ BUG CONFIRMED: Name doesn't normalize internal whitespace");
            } else {
                System.out.println("✓ Name handles whitespace correctly");
            }
        } catch (Exception e) {
            System.out.println("Exception in Name: " + e.getMessage());
        }
    }

    /**
     * BUG #6: Test if students added after session creation appear in attendance
     * SUSPECTED: initializeAttendance() only called on creation
     */
    @Test
    public void testBug6_StudentsAddedAfterSession() {
        System.out.println("\n=== Testing Bug #6: Students Added After Session ===");

        ClassName className = new ClassName("Physics");
        TuitionClass physics = new TuitionClass(className);

        // Add two students
        Student alice = createStudent("Alice");
        Student bob = createStudent("Bob");
        physics.addStudent(alice);
        physics.addStudent(bob);

        // Create a session
        ClassSession session = physics.addSession("Week1", LocalDateTime.now(), "Room 101");
        Map<Student, Boolean> attendance = session.getAttendanceRecord();

        System.out.println("Initial attendance size: " + attendance.size());
        System.out.println("Alice in attendance: " + attendance.containsKey(alice));
        System.out.println("Bob in attendance: " + attendance.containsKey(bob));

        // Now add a third student AFTER session creation
        Student charlie = createStudent("Charlie");
        physics.addStudent(charlie);

        // Check if Charlie appears in the session
        boolean charlieInAttendance = attendance.containsKey(charlie);
        System.out.println("\nAfter adding Charlie:");
        System.out.println("Charlie in attendance: " + charlieInAttendance);
        System.out.println("Attendance size: " + attendance.size());

        // Try to mark Charlie present
        try {
            session.markPresent(charlie);
            boolean charliePresent = session.hasAttended(charlie);
            System.out.println("Can mark Charlie present: true");
            System.out.println("Charlie marked as present: " + charliePresent);

            if (!charlieInAttendance && charliePresent) {
                System.out.println("❌ BUG CONFIRMED: Can mark attendance for student not in initial record");
            }
        } catch (Exception e) {
            System.out.println("Exception marking Charlie: " + e.getMessage());
        }

        if (!charlieInAttendance) {
            System.out.println("❌ BUG CONFIRMED: Students added after session don't appear in attendance");
        } else {
            System.out.println("✓ Bug NOT found: Late students are added to attendance");
        }
    }

    /**
     * BUG #7: Test session name case sensitivity
     * SUSPECTED: Inconsistent case handling in session operations
     */
    @Test
    public void testBug7_SessionNameCaseSensitivity() {
        System.out.println("\n=== Testing Bug #7: Session Name Case Sensitivity ===");

        ClassName className = new ClassName("Chemistry");
        TuitionClass chemistry = new TuitionClass(className);

        // Create session with mixed case
        ClassSession session = chemistry.addSession("Week1Tutorial", LocalDateTime.now(), "Lab");

        // Test different case variations
        System.out.println("Original session name: Week1Tutorial");
        System.out.println("hasSessionName('Week1Tutorial'): " + chemistry.hasSessionName("Week1Tutorial"));
        System.out.println("hasSessionName('week1tutorial'): " + chemistry.hasSessionName("week1tutorial"));
        System.out.println("hasSessionName('WEEK1TUTORIAL'): " + chemistry.hasSessionName("WEEK1TUTORIAL"));

        // Test getSession
        boolean found1 = chemistry.getSession("Week1Tutorial").isPresent();
        boolean found2 = chemistry.getSession("week1tutorial").isPresent();
        boolean found3 = chemistry.getSession("WEEK1TUTORIAL").isPresent();

        System.out.println("\ngetSession results:");
        System.out.println("getSession('Week1Tutorial'): " + found1);
        System.out.println("getSession('week1tutorial'): " + found2);
        System.out.println("getSession('WEEK1TUTORIAL'): " + found3);

        if (found1 && found2 && found3) {
            System.out.println("✓ Session names are case-insensitive");
        } else if (found1 && !found2 && !found3) {
            System.out.println("❌ BUG CONFIRMED: Session names are case-sensitive");
        } else {
            System.out.println("⚠ Inconsistent case sensitivity behavior");
        }
    }

    /**
     * BUG #8: Test attendance marking for non-enrolled students
     * SUSPECTED: No validation of enrollment before marking attendance
     */
    @Test
    public void testBug8_NonEnrolledStudentAttendance() {
        System.out.println("\n=== Testing Bug #8: Non-Enrolled Student Attendance ===");

        ClassName className = new ClassName("Biology");
        TuitionClass biology = new TuitionClass(className);

        // Add one student
        Student alice = createStudent("Alice");
        biology.addStudent(alice);

        // Create a session
        ClassSession session = biology.addSession("Lab1", LocalDateTime.now(), "Bio Lab");

        // Create a student NOT in the class
        Student bob = createStudent("Bob");

        System.out.println("Bob enrolled in Biology: " + biology.hasStudent(bob));
        System.out.println("Bob in initial attendance: " + session.getAttendanceRecord().containsKey(bob));

        // Try to mark Bob (not enrolled) as present
        try {
            session.markPresent(bob);
            boolean bobMarked = session.hasAttended(bob);
            System.out.println("Successfully marked Bob present: true");
            System.out.println("Bob attendance status: " + bobMarked);
            System.out.println("Bob now in attendance record: " + session.getAttendanceRecord().containsKey(bob));

            if (bobMarked && !biology.hasStudent(bob)) {
                System.out.println("❌ BUG CONFIRMED: Can mark attendance for non-enrolled student");
            }
        } catch (Exception e) {
            System.out.println("✓ Exception thrown when marking non-enrolled: " + e.getMessage());
        }
    }

    /**
     * BUG #10: Test if attendance records persist after student removal
     * SUSPECTED: Removing student from class doesn't update session attendance
     */
    @Test
    public void testBug10_AttendanceAfterRemoval() {
        System.out.println("\n=== Testing Bug #10: Attendance After Student Removal ===");

        ClassName className = new ClassName("History");
        TuitionClass history = new TuitionClass(className);

        // Add students
        Student alice = createStudent("Alice");
        Student bob = createStudent("Bob");
        history.addStudent(alice);
        history.addStudent(bob);

        // Create session and mark attendance
        ClassSession session = history.addSession("Lecture1", LocalDateTime.now(), "Hall A");
        session.markPresent(alice);
        session.markPresent(bob);

        System.out.println("Initial attendance count: " + session.getAttendanceCount());
        System.out.println("Alice in attendance: " + session.getAttendanceRecord().containsKey(alice));

        // Remove Alice from class
        history.removeStudent(alice);

        System.out.println("\nAfter removing Alice from class:");
        System.out.println("Alice still in class: " + history.hasStudent(alice));
        System.out.println("Alice still in attendance: " + session.getAttendanceRecord().containsKey(alice));
        System.out.println("Attendance count: " + session.getAttendanceCount());

        if (!history.hasStudent(alice) && session.getAttendanceRecord().containsKey(alice)) {
            System.out.println("❌ BUG CONFIRMED: Removed student persists in attendance records");
        } else {
            System.out.println("✓ Bug NOT found: Attendance updated on removal");
        }
    }

    /**
     * Run all bug tests
     */
    public static void main(String[] args) {
        BugVerificationTest test = new BugVerificationTest();

        System.out.println("========================================");
        System.out.println("    TutBook Bug Verification Suite");
        System.out.println("========================================");

        try {
            test.testBug1_BidirectionalLinkDuplicate();
        } catch (Exception e) {
            System.out.println("Bug 1 test failed: " + e.getMessage());
        }

        try {
            test.testBug2_UnicodeNormalization();
        } catch (Exception e) {
            System.out.println("Bug 2 test failed: " + e.getMessage());
        }

        try {
            test.testBug3_WhitespaceHandling();
        } catch (Exception e) {
            System.out.println("Bug 3 test failed: " + e.getMessage());
        }

        try {
            test.testBug6_StudentsAddedAfterSession();
        } catch (Exception e) {
            System.out.println("Bug 6 test failed: " + e.getMessage());
        }

        try {
            test.testBug7_SessionNameCaseSensitivity();
        } catch (Exception e) {
            System.out.println("Bug 7 test failed: " + e.getMessage());
        }

        try {
            test.testBug8_NonEnrolledStudentAttendance();
        } catch (Exception e) {
            System.out.println("Bug 8 test failed: " + e.getMessage());
        }

        try {
            test.testBug10_AttendanceAfterRemoval();
        } catch (Exception e) {
            System.out.println("Bug 10 test failed: " + e.getMessage());
        }

        System.out.println("\n========================================");
        System.out.println("         Test Suite Complete");
        System.out.println("========================================");
    }
}