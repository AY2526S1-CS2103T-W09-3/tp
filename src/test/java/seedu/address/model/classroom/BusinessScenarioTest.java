package seedu.address.model.classroom;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;
import seedu.address.model.person.Parent;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Business Scenario Test Suite
 * Tests real-world usage patterns and business logic violations
 * Focus: What can go wrong when users actually use the system?
 */
public class BusinessScenarioTest {

    private Student createStudent(String name) {
        return new Student(
            new Name(name),
            new Phone("12345678"),
            new Email(name.toLowerCase().replace(" ", "") + "@test.com"),
            new Address("123 Test St"),
            new HashSet<>()
        );
    }

    private Tutor createTutor(String name) {
        return new Tutor(
            new Name(name),
            new Phone("87654321"),
            new Email(name.toLowerCase().replace(" ", "") + "@tutor.com"),
            new Address("456 Tutor Ave"),
            new HashSet<>()
        );
    }

    private Parent createParent(String name) {
        return new Parent(
            new Name(name),
            new Phone("99999999"),
            new Email(name.toLowerCase().replace(" ", "") + "@parent.com"),
            new Address("789 Parent Rd"),
            new HashSet<>()
        );
    }

    /**
     * SCENARIO 1: Tutor Teaching Conflicting Classes
     * Business Rule: A tutor shouldn't teach two classes at the same time
     */
    @Test
    public void testTutorScheduleConflict() {
        System.out.println("\n=== SCENARIO: Tutor Double-Booked ===");

        Tutor mrSmith = createTutor("Mr Smith");
        TuitionClass mathClass = new TuitionClass(new ClassName("Mathematics"));
        TuitionClass physicsClass = new TuitionClass(new ClassName("Physics"));

        mathClass.setTutor(mrSmith);
        physicsClass.setTutor(mrSmith);

        // Same time sessions
        LocalDateTime sameTime = LocalDateTime.of(2025, 11, 30, 14, 0);
        ClassSession mathSession = mathClass.addSession("Week1", sameTime, "Room A");
        ClassSession physicsSession = physicsClass.addSession("Week1", sameTime, "Room B");

        // PROBLEM: System allows tutor to be in two places at once!
        System.out.println("Math session at: " + sameTime);
        System.out.println("Physics session at: " + sameTime);
        System.out.println("Same tutor for both: " + mrSmith.getName());
        System.out.println("❌ BUG: No conflict detection - tutor double-booked!");

        // This should be prevented but isn't
        assertTrue(mathSession.getDateTime().equals(physicsSession.getDateTime()));
        assertEquals(mathClass.getTutor(), physicsClass.getTutor());
    }

    /**
     * SCENARIO 2: Student Enrolled in Overlapping Classes
     * Business Rule: A student can't attend two classes simultaneously
     */
    @Test
    public void testStudentScheduleConflict() {
        System.out.println("\n=== SCENARIO: Student in Two Places ===");

        Student alice = createStudent("Alice");
        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        TuitionClass scienceClass = new TuitionClass(new ClassName("Science"));

        mathClass.addStudent(alice);
        scienceClass.addStudent(alice);

        LocalDateTime conflictTime = LocalDateTime.of(2025, 11, 30, 16, 0);
        ClassSession mathSession = mathClass.addSession("Tutorial", conflictTime, "Room 1");
        ClassSession scienceSession = scienceClass.addSession("Lab", conflictTime, "Lab 2");

        // Mark Alice present in BOTH sessions at the same time
        mathSession.markPresent(alice);
        scienceSession.markPresent(alice);

        System.out.println("Alice marked present in Math at: " + conflictTime);
        System.out.println("Alice marked present in Science at: " + conflictTime);
        System.out.println("❌ BUG: Student can be in two places simultaneously!");

        assertTrue(mathSession.hasAttended(alice));
        assertTrue(scienceSession.hasAttended(alice));
    }

    /**
     * SCENARIO 3: Retroactive Attendance Manipulation
     * Business Rule: Shouldn't mark attendance for past sessions (audit trail)
     */
    @Test
    public void testRetroactiveAttendanceManipulation() {
        System.out.println("\n=== SCENARIO: Backdating Attendance ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        Student bob = createStudent("Bob");
        mathClass.addStudent(bob);

        // Create a session from last month
        LocalDateTime pastDate = LocalDateTime.now().minusMonths(1);
        ClassSession oldSession = mathClass.addSession("OldSession", pastDate, "Room");

        // Try to mark attendance for old session today
        oldSession.markPresent(bob);

        System.out.println("Session date: " + pastDate);
        System.out.println("Current date: " + LocalDateTime.now());
        System.out.println("Can still mark attendance: true");
        System.out.println("❌ BUG: Can manipulate historical attendance records!");

        assertTrue(oldSession.hasAttended(bob));
        // Should have audit timestamp of when attendance was marked
    }

    /**
     * SCENARIO 4: Attendance Without Class in Session
     * Business Rule: Can't have attendance if class isn't in session
     */
    @Test
    public void testFutureSessionAttendance() {
        System.out.println("\n=== SCENARIO: Future Session Attendance ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        Student charlie = createStudent("Charlie");
        mathClass.addStudent(charlie);

        // Create future session
        LocalDateTime futureDate = LocalDateTime.now().plusDays(7);
        ClassSession futureSession = mathClass.addSession("NextWeek", futureDate, "Room");

        // Mark attendance for session that hasn't happened
        futureSession.markPresent(charlie);

        System.out.println("Session scheduled for: " + futureDate);
        System.out.println("Attendance marked on: " + LocalDateTime.now());
        System.out.println("❌ BUG: Can mark attendance for future sessions!");

        assertTrue(futureSession.hasAttended(charlie));
    }

    /**
     * SCENARIO 5: Class Deletion Cascade Effects
     * Business Rule: Deleting a class should clean up all relationships
     */
    @Test
    public void testClassDeletionCascade() {
        System.out.println("\n=== SCENARIO: Class Deletion Ripple Effects ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        Tutor tutor = createTutor("Ms Johnson");
        Student alice = createStudent("Alice");
        Student bob = createStudent("Bob");

        mathClass.setTutor(tutor);
        mathClass.addStudent(alice);
        mathClass.addStudent(bob);

        // Create sessions with attendance
        ClassSession session1 = mathClass.addSession("Week1", LocalDateTime.now(), "Room");
        session1.markPresent(alice);
        session1.markAbsent(bob);

        // Store references
        int tutorClassesBefore = tutor.getTuitionClasses().size();
        boolean aliceHadClass = alice.getTuitionClasses().contains(mathClass);

        // Simulate class deletion (what should happen)
        // Note: Actual deletion logic might be in command layer
        System.out.println("Before deletion:");
        System.out.println("  Tutor has " + tutorClassesBefore + " classes");
        System.out.println("  Alice enrolled: " + aliceHadClass);
        System.out.println("  Session exists: true");

        // After deletion, these should all be cleaned up
        System.out.println("❌ ISSUE: No proper cascade deletion implementation");
        System.out.println("  - Tutor still references deleted class?");
        System.out.println("  - Students still reference deleted class?");
        System.out.println("  - Attendance records orphaned?");
    }

    /**
     * SCENARIO 6: Person Type Change Mid-Semester
     * Business Rule: What happens to attendance when Student becomes Tutor?
     */
    @Test
    public void testPersonTypeTransition() {
        System.out.println("\n=== SCENARIO: Student Becomes Tutor ===");

        Student david = createStudent("David");
        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        mathClass.addStudent(david);

        // Create attendance history
        ClassSession session1 = mathClass.addSession("Week1", LocalDateTime.now().minusDays(7), "Room");
        ClassSession session2 = mathClass.addSession("Week2", LocalDateTime.now(), "Room");
        session1.markPresent(david);
        session2.markPresent(david);

        System.out.println("David's attendance before transition:");
        System.out.println("  Week1: " + session1.hasAttended(david));
        System.out.println("  Week2: " + session2.hasAttended(david));

        // Now David becomes a tutor (simulate role change)
        // In real system, this would be through EditCommand
        System.out.println("\n❌ ISSUE: Role transition not handled properly");
        System.out.println("  - Historical attendance data lost?");
        System.out.println("  - Can't be student in one class and tutor in another?");
        System.out.println("  - No graduated student → teaching assistant workflow?");
    }

    /**
     * SCENARIO 7: Bulk Attendance Operations
     * Business Rule: Common operation - mark all present/absent
     */
    @Test
    public void testBulkAttendanceMarking() {
        System.out.println("\n=== SCENARIO: Fire Drill - Mark All Absent ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));

        // Add 30 students
        List<Student> students = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            Student s = createStudent("Student" + i);
            students.add(s);
            mathClass.addStudent(s);
        }

        ClassSession session = mathClass.addSession("FireDrill", LocalDateTime.now(), "Room");

        // Need to mark all absent one by one
        System.out.println("Marking 30 students absent individually...");
        for (Student s : students) {
            session.markAbsent(s);
        }

        System.out.println("❌ MISSING FEATURE: No bulk operations");
        System.out.println("  - No 'mark all present/absent'");
        System.out.println("  - No 'copy from previous session'");
        System.out.println("  - Time-consuming for large classes");

        assertEquals(0, session.getAttendanceCount());
    }

    /**
     * SCENARIO 8: Substitute Teacher Tracking
     * Business Rule: Regular tutor sick, substitute teaches
     */
    @Test
    public void testSubstituteTeacher() {
        System.out.println("\n=== SCENARIO: Substitute Teacher ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        Tutor regularTutor = createTutor("Mr Regular");
        Tutor substituteTutor = createTutor("Ms Substitute");

        mathClass.setTutor(regularTutor);

        ClassSession normalSession = mathClass.addSession("Week1", LocalDateTime.now().minusDays(7), "Room");
        // Regular tutor is sick for Week2
        ClassSession substituteSession = mathClass.addSession("Week2", LocalDateTime.now(), "Room");

        System.out.println("Regular tutor: " + regularTutor.getName());
        System.out.println("Need substitute for Week2: " + substituteTutor.getName());

        // Try to temporarily assign substitute
        mathClass.setTutor(substituteTutor); // This changes permanently!

        System.out.println("❌ BUG: Can't track per-session tutor");
        System.out.println("  - No way to record who actually taught each session");
        System.out.println("  - Changing tutor affects all sessions");
        System.out.println("  - No substitute teacher support");

        assertEquals(substituteTutor, mathClass.getTutor()); // Permanent change
    }

    /**
     * SCENARIO 9: Attendance Percentage Calculation
     * Business Rule: Parents want to see attendance rate
     */
    @Test
    public void testAttendancePercentageAccuracy() {
        System.out.println("\n=== SCENARIO: Attendance Report Card ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        Student alice = createStudent("Alice");
        mathClass.addStudent(alice);

        // Create 10 sessions
        List<ClassSession> sessions = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ClassSession session = mathClass.addSession(
                "Week" + i,
                LocalDateTime.now().minusDays(10 - i),
                "Room"
            );
            sessions.add(session);
        }

        // Alice attended 7 out of 10
        for (int i = 0; i < 7; i++) {
            sessions.get(i).markPresent(alice);
        }
        for (int i = 7; i < 10; i++) {
            sessions.get(i).markAbsent(alice);
        }

        // Calculate attendance
        int totalSessions = sessions.size();
        int attendedSessions = 0;
        for (ClassSession s : sessions) {
            if (s.hasAttended(alice)) attendedSessions++;
        }

        double attendanceRate = (double) attendedSessions / totalSessions * 100;
        System.out.println("Alice's attendance: " + attendedSessions + "/" + totalSessions);
        System.out.println("Attendance rate: " + String.format("%.1f%%", attendanceRate));

        System.out.println("\n❌ MISSING FEATURE: No attendance analytics");
        System.out.println("  - No built-in percentage calculation");
        System.out.println("  - No attendance trends");
        System.out.println("  - No warning for low attendance");

        assertEquals(70.0, attendanceRate, 0.1);
    }

    /**
     * SCENARIO 10: Class Transfer Mid-Semester
     * Business Rule: Student moves from Math-A to Math-B
     */
    @Test
    public void testStudentClassTransfer() {
        System.out.println("\n=== SCENARIO: Student Class Transfer ===");

        Student emma = createStudent("Emma");
        TuitionClass mathA = new TuitionClass(new ClassName("Math-A"));
        TuitionClass mathB = new TuitionClass(new ClassName("Math-B"));

        mathA.addStudent(emma);

        // Emma attends 3 sessions in Math-A
        for (int i = 1; i <= 3; i++) {
            ClassSession session = mathA.addSession(
                "Week" + i,
                LocalDateTime.now().minusWeeks(4 - i),
                "Room A"
            );
            session.markPresent(emma);
        }

        // Transfer Emma to Math-B
        mathA.removeStudent(emma);
        mathB.addStudent(emma);

        // Continue in Math-B
        ClassSession mathBSession = mathB.addSession("Week4", LocalDateTime.now(), "Room B");
        mathBSession.markPresent(emma);

        System.out.println("Emma's journey:");
        System.out.println("  Weeks 1-3: Math-A");
        System.out.println("  Week 4: Transferred to Math-B");

        System.out.println("\n❌ ISSUES with transfer:");
        System.out.println("  - Historical attendance in Math-A lost from records");
        System.out.println("  - No transfer date tracking");
        System.out.println("  - Can't generate complete attendance history");
        System.out.println("  - No pro-rated fee calculation support");

        // Emma's Math-A attendance is orphaned
        assertFalse(mathA.hasStudent(emma));
        assertTrue(mathB.hasStudent(emma));
    }

    /**
     * SCENARIO 11: Session Cancellation
     * Business Rule: Class cancelled due to holiday/emergency
     */
    @Test
    public void testSessionCancellation() {
        System.out.println("\n=== SCENARIO: Class Cancellation ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        Student alice = createStudent("Alice");
        Student bob = createStudent("Bob");
        mathClass.addStudent(alice);
        mathClass.addStudent(bob);

        ClassSession session = mathClass.addSession("Week5", LocalDateTime.now(), "Room");

        System.out.println("Session created for Week5");
        System.out.println("Need to mark as CANCELLED (e.g., public holiday)");

        // No way to mark session as cancelled
        // Current options: Delete (loses record) or mark all absent (misleading)
        mathClass.removeSession(session);

        System.out.println("\n❌ MISSING STATUS: No session states");
        System.out.println("  - Can't mark as: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED");
        System.out.println("  - Deleting session loses audit trail");
        System.out.println("  - Marking all absent is misleading");
    }

    /**
     * SCENARIO 12: Attendance Correction After Parent Complaint
     * Business Rule: Parent says child was present but marked absent
     */
    @Test
    public void testAttendanceCorrection() {
        System.out.println("\n=== SCENARIO: Attendance Dispute ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));
        Student alice = createStudent("Alice");
        mathClass.addStudent(alice);

        ClassSession session = mathClass.addSession("Week6", LocalDateTime.now().minusDays(3), "Room");
        session.markAbsent(alice);

        System.out.println("Day 1: Alice marked absent");
        System.out.println("Day 3: Parent complains - Alice was present");

        // Correct the attendance
        session.markPresent(alice);

        System.out.println("Attendance corrected to present");
        System.out.println("\n❌ MISSING AUDIT: No correction tracking");
        System.out.println("  - No record of who made the change");
        System.out.println("  - No timestamp of correction");
        System.out.println("  - No reason for change");
        System.out.println("  - No original vs corrected value history");

        assertTrue(session.hasAttended(alice));
    }

    /**
     * SCENARIO 13: Maximum Class Size
     * Business Rule: Fire safety - room capacity limits
     */
    @Test
    public void testClassCapacityLimit() {
        System.out.println("\n=== SCENARIO: Class Oversubscription ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));

        // Try to add 100 students (room capacity: 30)
        for (int i = 1; i <= 100; i++) {
            Student s = createStudent("Student" + i);
            mathClass.addStudent(s);
        }

        System.out.println("Added students: " + mathClass.getStudents().size());
        System.out.println("❌ BUG: No capacity limit enforced");
        System.out.println("  - Fire safety violation");
        System.out.println("  - Resource planning issues");
        System.out.println("  - No waiting list feature");

        assertEquals(100, mathClass.getStudents().size());
    }

    /**
     * SCENARIO 14: Daylight Saving Time
     * Business Rule: Sessions during DST change
     */
    @Test
    public void testDaylightSavingTime() {
        System.out.println("\n=== SCENARIO: Daylight Saving Time ===");

        TuitionClass mathClass = new TuitionClass(new ClassName("Math"));

        // Session during DST change (example: last Sunday of March)
        LocalDateTime dstDate = LocalDateTime.of(2025, 3, 30, 2, 30);
        ClassSession session = mathClass.addSession("DSTSession", dstDate, "Room");

        System.out.println("Session at 2:30 AM during DST change");
        System.out.println("❌ ISSUE: No timezone handling");
        System.out.println("  - LocalDateTime doesn't handle DST");
        System.out.println("  - Could cause scheduling confusion");
        System.out.println("  - Should use ZonedDateTime");

        assertEquals(dstDate, session.getDateTime());
    }

    /**
     * Run comprehensive test suite
     */
    public static void main(String[] args) {
        BusinessScenarioTest test = new BusinessScenarioTest();

        System.out.println("==========================================");
        System.out.println("  BUSINESS SCENARIO TEST SUITE");
        System.out.println("  Testing Real-World Usage Patterns");
        System.out.println("==========================================");

        try { test.testTutorScheduleConflict(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testStudentScheduleConflict(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testRetroactiveAttendanceManipulation(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testFutureSessionAttendance(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testClassDeletionCascade(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testPersonTypeTransition(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testBulkAttendanceMarking(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testSubstituteTeacher(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testAttendancePercentageAccuracy(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testStudentClassTransfer(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testSessionCancellation(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testAttendanceCorrection(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testClassCapacityLimit(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try { test.testDaylightSavingTime(); } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\n==========================================");
        System.out.println("  BUSINESS SCENARIO TESTS COMPLETE");
        System.out.println("==========================================");
    }
}