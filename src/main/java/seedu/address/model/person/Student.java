package seedu.address.model.person;

import java.util.ArrayList;
import java.util.Set;

import seedu.address.model.tag.Tag;

/**
 * Represents a Student in the address book. A Student is a {@link Person} that
 * can be associated with one or more {@link Parent} instances.
 * <p>
 * Note: Parents are stored in insertion order. This class does not prevent
 * duplicates. Initially, a newly constructed Student has no associated parents.
 */
public class Student extends Person {

    /**
     * Parents associated with this student. This list is internal to the model
     * and may contain duplicates.
     */
    private ArrayList<Parent> parents = new ArrayList<>();

    /**
     * Constructs a {@code Student}.
     *
     * @param name A valid name.
     * @param phone A valid phone number.
     * @param email A valid email address.
     * @param address A valid address.
     * @param tags A set of tags.
     */
    public Student(Name name, Phone phone, Email email, Address address,
                    Set<Tag> tags) {
        super(name, phone, email, address, tags);
    }

    /**
     * Adds the given parent to this student and establishes a bidirectional
     * relationship. Appends the parent to this student's list of parents and
     * invokes {@code parent.addChild(this)}.
     * <p>
     * No deduplication is performed; if the parent is already associated, it
     * will be added again.
     *
     * @param parent the parent to associate with this student; must not be null
     */
    public void addParent(Parent parent) {
        if (!parents.contains(parent)) {
            parents.add(parent);
            parent.addChild(this); // maintain bidirectional link
        }
    }

    /**
     * Returns this person's type.
     *
     * @return {@link PersonType#STUDENT}
     */
    @Override
    public PersonType getPersonType() {
        return PersonType.STUDENT;
    }
}
