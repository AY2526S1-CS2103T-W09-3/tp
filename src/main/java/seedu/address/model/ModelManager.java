package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.classroom.ClassSession;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Person;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<TuitionClass> filteredClasses;
    private final ObservableList<ClassSession> sessionList;
    private final FilteredList<ClassSession> filteredSessions;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredClasses = new FilteredList<>(this.addressBook.getClassList());
        sessionList = FXCollections.observableArrayList();
        filteredSessions = new FilteredList<>(sessionList);

    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    // =========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    // =========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.setSessionList(new ArrayList<>());
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public boolean hasClass(TuitionClass c) {
        requireNonNull(c);
        return addressBook.hasClass(c);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void addClass(TuitionClass c) {
        addressBook.addClass(c);
        updateFilteredClassList(PREDICATE_SHOW_ALL_CLASSES);
    }

    @Override
    public void deleteClass(TuitionClass target) {
        requireNonNull(target);
        ArrayList<Student> studentsToRemove = target.getStudents();
        for (Student s : studentsToRemove) {
            s.unjoinSafely(target);
            target.removeStudent(s);
        }

        if (target.isAssignedToTutor()) {
            target.setTutor(null);
        }

        addressBook.unjoin(target);
    }

    @Override
    public void setClass(TuitionClass target, TuitionClass editedClass) {
        requireAllNonNull(target, editedClass);
        addressBook.setClass(target, editedClass);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    @Override
    public void addStudentToClass(Student student, TuitionClass c) {
        c.addStudent(student);
        addressBook.setClass(c, c);
    }

    @Override
    public void assignTutorToClass(Tutor tutor, TuitionClass c) {
        c.setTutor(tutor);
        addressBook.setClass(c, c);
    }

    // =========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} or list of {@code TuitionClass}
     * backed by the internal list of {@code versionedAddressBook}
     */

    @Override
    public ObservableList<Person> getPersonList() {
        return this.addressBook.getPersonList();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public ObservableList<TuitionClass> getFilteredClassList() {
        return filteredClasses;
    }

    @Override
    public TuitionClass getClassByName(String className) {
        requireNonNull(className);
        for (TuitionClass c : addressBook.getClassList()) {
            if (c.getName().value.equalsIgnoreCase(className)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public void updateFilteredClassList(Predicate<TuitionClass> predicate) {
        requireNonNull(predicate);
        filteredClasses.setPredicate(predicate);
    }

    @Override
    public ObservableList<ClassSession> getFilteredSessionList() {
        return filteredSessions;
    }

    @Override
    public void updateFilteredSessionList(Predicate<ClassSession> predicate) {
        requireNonNull(predicate);
        filteredSessions.setPredicate(predicate);
    }

    @Override
    public void setSession(ClassSession target, ClassSession editedSession) {
        requireAllNonNull(target, editedSession);
        int index = sessionList.indexOf(target);
        if (index != -1) {
            sessionList.set(index, editedSession);
        }
    }

    @Override
    public void setSessionList(java.util.List<ClassSession> sessions) {
        requireNonNull(sessions);
        sessionList.setAll(sessions);
    }

    @Override
    public void updateSessionListForClass(TuitionClass tuitionClass) {
        requireNonNull(tuitionClass);
        List<ClassSession> sessions = tuitionClass.getAllSessions();
        List<ClassSession> sortedSessions = sessions.stream()
                .sorted(Comparator.comparing(ClassSession::getDateTime).reversed())
                .collect(Collectors.toList());
        setSessionList(sortedSessions);
    }

    @Override
    public void clearSessions() {
        setSessionList(new ArrayList<>());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && filteredClasses.equals(otherModelManager.filteredClasses)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

}
