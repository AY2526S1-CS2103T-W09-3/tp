package seedu.address.model.classroom;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Represents a tuition class in the address book.
 * <p>
 * Identity is determined solely by {@link ClassName}. Additional properties such as tutor,
 * roster, and sessions do not affect identity/equality.
 */
public class TuitionClass {

    /** Canonical identity of the class. */
    private final ClassName name;

    /** Tutor responsible for this class (nullable until assigned). */
    private Tutor tutor;

    /** Mutable roster of enrolled students. */
    private final ArrayList<Student> students = new ArrayList<>();

    /** Mutable list of sessions conducted under this class. */
    private final ArrayList<ClassSession> sessions = new ArrayList<>();

    /**
     * Constructs a {@code TuitionClass} with the given name.
     *
     * @param name non-null class name
     */
    public TuitionClass(ClassName name) {
        this(name, null);
    }

    /**
     * Constructs a {@code TuitionClass} with the given name and tutor.
     *
     * @param name non-null class name
     * @param tutor tutor in charge (nullable)
     */
    public TuitionClass(ClassName name, Tutor tutor) {
        requireNonNull(name);
        this.name = name;
        this.tutor = tutor;
        // Register this class with the tutor if not null
        if (tutor != null) {
            tutor.addClass(this);
        }
    }

    // ---------------------------------------------------------------------
    // Identity
    // ---------------------------------------------------------------------

    public ClassName getName() {
        return name;
    }

    /**
     * Back-compat helper for older code/tests that used a String name.
     */
    public String getClassName() {
        return name.value;
    }

    /**
     * Returns true if both classes share the same {@link ClassName}.
     */
    public boolean isSameClass(TuitionClass other) {
        return other != null && name.equals(other.name);
    }

    /**
     * Returns true if both classes have the same identity fields.
     */
    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof TuitionClass && name.equals(((TuitionClass) other).name));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        String tutorStr = !this.isAssignedToTutor() ? "Unassigned" : tutor.getName().fullName;
        return String.format("TuitionClass{name=%s, tutor=%s, students=%d, sessions=%d}",
                name.value, tutorStr, students.size(), sessions.size());
    }

    // ---------------------------------------------------------------------
    // Tutor
    // ---------------------------------------------------------------------

    /**
     * Returns the tutor assigned to this class.
     */
    public Tutor getTutor() {
        return tutor;
    }

    /**
     * Checks if this class has an assigned tutor.
     */
    public boolean isAssignedToTutor() {
        return tutor != null;
    }

    /**
     * Assigns a tutor to this tuition class.
     * Replaces any existing tutor assignment.
     */
    public void setTutor(Tutor tutor) {
        // Remove from old tutor's class list if exists
        if (this.tutor != null) {
            this.tutor.removeClass(this);
        }
        // Assign new tutor
        this.tutor = tutor;
        // Add to new tutor's class list if not null
        if (tutor != null) {
            tutor.addClass(this);
        }
    }

    /**
     * Removes the tutor from this tuition class.
     * @throws PersonNotFoundException if no tutor is assigned
     */
    public void removeTutor(Tutor tutorToRemove) {
        if (this.tutor == null || !this.tutor.equals(tutorToRemove)) {
            throw new PersonNotFoundException();
        }
        this.tutor = null;
    }

    /**
     * Checks if a tutor is assigned to this class.
     */
    public boolean hasTutor(Tutor tutor) {
        requireNonNull(tutor);
        return this.tutor != null && this.tutor.equals(tutor);
    }

    // ---------------------------------------------------------------------
    // Roster
    // ---------------------------------------------------------------------

    /**
     * Returns the internal mutable roster.
     */
    public ArrayList<Student> getStudents() {
        return students;
    }

    /**
     * Adds a student to this tuition class if not already present.
     */
    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
            student.addClass(this);
        }
    }

    /**
     * Checks if a student is already in the class roster.
     */
    public boolean hasStudent(Student student) {
        requireNonNull(student);
        return students.contains(student);
    }

    /**
     * Removes a student from this tuition class.
     * @throws PersonNotFoundException if student is not in this class
     */
    public void removeStudent(Student student) {
        requireNonNull(student);
        if (!students.remove(student)) {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Replaces the target student with the edited student.
     * @throws PersonNotFoundException if target student is not in this class
     */
    public void setStudent(Student target, Student editedStudent) {
        requireNonNull(target);
        requireNonNull(editedStudent);

        int index = students.indexOf(target);
        if (index == -1) {
            throw new PersonNotFoundException();
        }

        students.set(index, editedStudent);
    }

    // ---------------------------------------------------------------------
    // Sessions
    // ---------------------------------------------------------------------

    /**
     * Add session to session list and return the list
     */
    public ClassSession addSession(String sessionName, LocalDateTime dateTime, String location) {
        ClassSession session = new ClassSession(this, sessionName, dateTime, location);
        sessions.add(session);
        return session;
    }

    /**
     * Remove session from session list
     */
    public void removeSession(ClassSession session) {
        sessions.remove(session);
    }

    /**
     * Return session list
     */
    public List<ClassSession> getAllSessions() {
        return new ArrayList<>(sessions);
    }

    /**
     * Returns filtered session list that includes future sessions
     */
    public List<ClassSession> getFutureSessions() {
        LocalDateTime now = LocalDateTime.now();
        return sessions.stream()
                .filter(s -> s.getDateTime().isAfter(now))
                .collect(Collectors.toList());
    }

    /**
     * Returns filtered session list that includes past sessions
     */
    public List<ClassSession> getPastSessions() {
        LocalDateTime now = LocalDateTime.now();
        return sessions.stream()
                .filter(s -> s.getDateTime().isBefore(now))
                .collect(Collectors.toList());
    }

    /**
     * Returns boolean if sessionName already exists in past sessions
     * Ensures sessionName is unique within a class (case-insensitive)
     */
    public boolean hasSessionName(String sessionName) {
        requireNonNull(sessionName);
        return sessions.stream()
            .anyMatch(s -> s.getSessionName().trim().equalsIgnoreCase(sessionName.trim()));
    }
}
