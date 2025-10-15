package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.classroom.UniqueClassList;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address book level.
 * <p>
 * Duplicates are not allowed (by {@code Person#isSamePerson} and {@code TuitionClass#isSameClass} comparisons).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons;
    private final UniqueClassList classes;

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are generally not recommended for production code. There are cleaner ways
     * to avoid duplication among constructors (e.g., private helper methods).
     */
    {
        persons = new UniquePersonList();
        classes = new UniqueClassList();
    }

    /**
     * Constructs an empty {@code AddressBook}.
     */
    public AddressBook() {}

    /**
     * Creates an {@code AddressBook} using the data in {@code toBeCopied}.
     *
     * @param toBeCopied A read-only address book to copy data from.
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    // =============================================================
    // Person list operations
    // =============================================================

    /**
     * Replaces the contents of the person list with {@code persons}.
     * <p>
     * {@code persons} must not contain duplicate persons.
     *
     * @param persons List of persons to set.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Returns {@code true} if a person with the same identity as {@code person}
     * exists in the address book.
     *
     * @param person Person to check.
     * @return {@code true} if the person exists, {@code false} otherwise.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * <p>
     * The person must not already exist in the address book.
     *
     * @param p Person to add.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * <p>
     * {@code target} must exist in the address book.
     * The identity of {@code editedPerson} must not be the same as another existing person.
     *
     * @param target       Person to replace.
     * @param editedPerson New person replacing the target.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);
        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * <p>
     * {@code key} must exist in the address book.
     *
     * @param key Person to remove.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    // =============================================================
    // Class list operations
    // =============================================================

    /**
     * Replaces the contents of the class list with {@code classes}.
     * <p>
     * {@code classes} must not contain duplicate classes.
     *
     * @param classes List of classes to set.
     */
    public void setClasses(List<TuitionClass> classes) {
        this.classes.setClasses(classes);
    }

    /**
     * Returns {@code true} if a class with the same identity as {@code tuitionClass}
     * exists in the address book.
     *
     * @param tuitionClass Class to check.
     * @return {@code true} if the class exists, {@code false} otherwise.
     */
    public boolean hasClass(TuitionClass tuitionClass) {
        requireNonNull(tuitionClass);
        return classes.contains(tuitionClass);
    }

    /**
     * Adds a class to the address book.
     * <p>
     * The class must not already exist in the address book.
     *
     * @param tuitionClass Class to add.
     */
    public void addClass(TuitionClass tuitionClass) {
        classes.add(tuitionClass);
    }

    /**
     * Replaces the given tuition class {@code target} in the list with {@code editedClass}.
     * <p>
     * {@code target} must exist in the address book.
     * The identity of {@code editedClass} must not be the same as another existing class.
     *
     * @param target       Class to replace.
     * @param editedClass New class replacing the target.
     */
    public void setClass(TuitionClass target, TuitionClass editedClass) {
        requireNonNull(editedClass);
        classes.setClass(target, editedClass);
    }

    /**
     * Removes {@code tuitionClass} from this {@code AddressBook}.
     * <p>
     * {@code tuitionClass} must exist in the address book.
     *
     * @param tuitionClass Class to remove.
     */
    public void removeClass(TuitionClass tuitionClass) {
        classes.remove(tuitionClass);
    }

    // =============================================================
    // Utility and reset
    // =============================================================

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     *
     * @param newData A read-only address book containing new data.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);
        setPersons(newData.getPersonList());
        setClasses(newData.getClassList());
    }

    // =============================================================
    // Observable list getters
    // =============================================================

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<TuitionClass> getClassList() {
        return classes.asUnmodifiableObservableList();
    }

    // =============================================================
    // Utility overrides
    // =============================================================

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("classes", classes)
                .toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook)) {
            return false;
        }

        AddressBook otherAddressBook = (AddressBook) other;
        return persons.equals(otherAddressBook.persons)
                && classes.equals(otherAddressBook.classes);
    }

    @Override
    public int hashCode() {
        return persons.hashCode() ^ classes.hashCode();
    }
}
