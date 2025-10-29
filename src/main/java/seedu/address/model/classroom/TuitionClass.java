package seedu.address.model.classroom;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
    private StringProperty tutorName = new SimpleStringProperty("Unassigned");

    /** Mutable roster of enrolled students. */
    private final ArrayList<Student> students = new ArrayList<>();
    private final IntegerProperty studentCount = new SimpleIntegerProperty(0);

    /** Mutable list of sessions conducted under this class. */
    private final ArrayList<ClassSession> sessions = new ArrayList<>();
    private final IntegerProperty sessionCount = new SimpleIntegerProperty(0);

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
     * @param name  non-null class name
     * @param tutor tutor in charge (nullable)
     */
    public TuitionClass(ClassName name, Tutor tutor) {
        requireNonNull(name);
        this.name = name;
        this.tutor = tutor;
        this.tutorName = tutor != null && tutor.getName().fullName != null
                ? new SimpleStringProperty(tutor.getName().fullName)
                : new SimpleStringProperty("Unassigned");
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
     * Transfers all details from another class
     * Used in EditClassCommand
     */
    public void transferDetailsFromClass(TuitionClass oldClass) {

        // Transfer student roster
        for (Student student : new ArrayList<>(oldClass.getStudents())) {
            student.unjoinSafely(oldClass);
            this.addStudent(student);
        }

        // Transfer assigned tutor
        if (oldClass.isAssignedToTutor()) {
            Tutor tutor = oldClass.getTutor();
            tutor.unjoinSafely(oldClass);
            this.setTutor(tutor);
        }

        // Copy over sessions
        this.copySessions(oldClass);
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
        // Check for self-assignment
        if (this.tutor != null && this.tutor.equals(tutor)) {
            return;
        }

        // Remove from old tutor's class list if exists
        if (this.tutor != null) {
            this.tutor.unjoin(this);
            this.tutorName.set("Unassigned");
        }
        // Assign new tutor
        this.tutor = tutor;
        // Add to new tutor's class list if not null
        if (tutor != null) {
            tutor.addClass(this);
            this.tutorName.set(tutor.getName().fullName);
        }
    }

    /**
     * Removes the tutor from this tuition class.
     *
     * @throws PersonNotFoundException if no tutor is assigned
     */
    public void removeTutor(Tutor tutorToRemove) {
        if (this.tutor == null || !this.tutor.equals(tutorToRemove)) {
            throw new PersonNotFoundException();
        }
        this.tutor = null;
        this.tutorName.set("Unassigned");
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
        return new ArrayList<>(students);
    }

    /**
     * Adds a student to this tuition class if not already present.
     */
    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
            student.addClass(this);
            this.studentCount.set(students.size());
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
     *
     * @throws PersonNotFoundException if student is not in this class
     */
    public void removeStudent(Student student) {
        requireNonNull(student);
        if (!students.remove(student)) {
            throw new PersonNotFoundException();
        }
        this.studentCount.set(students.size());
    }

    /**
     * Replaces the target student with the edited student.
     *
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
        this.sessionCount.set(sessions.size());
        return session;
    }

    /**
     * Remove session from session list
     */
    public void removeSession(ClassSession session) {
        sessions.remove(session);
        this.sessionCount.set(sessions.size());
    }

    /**
     * Return session list
     */
    public List<ClassSession> getAllSessions() {
        return new ArrayList<>(sessions);
    }

    /**
     * Copies sessions from another class
     */
    public void copySessions(TuitionClass target) {
        List<ClassSession> sessionsToCopy = target.getAllSessions();
        this.sessions.addAll(sessionsToCopy);
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

    /**
     * Returns the a ClassSession with the matching Name
     */
    public Optional<ClassSession> getSession(String sessionName) {
        requireNonNull(sessionName);

        return sessions.stream()
                .filter(s -> s.getSessionName().trim().equalsIgnoreCase(sessionName.trim()))
                .findFirst();
    }

    public IntegerProperty getStudentCountProperty() {
        return this.studentCount;
    }

    public IntegerProperty getSessionCountProperty() {
        return this.sessionCount;
    }

    public StringProperty getTutorProperty() {
        return tutorName;
    }
}
