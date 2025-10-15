package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Set;

import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
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

    public ArrayList<Parent> getParents() {
        return parents;
    }

    /**
     * Changes the parent to the newly appointed parent
     */
    public void setParent(Parent target, Parent editedParent) {
        requireAllNonNull(target, editedParent);

        int index = parents.indexOf(target);
        if (index == -1) {
            throw new PersonNotFoundException();
        }

        if (!target.isSamePerson(editedParent) && parents.contains(editedParent)) {
            throw new DuplicatePersonException();
        }

        parents.set(index, editedParent);
    }


    /**
     * Changes all parent's child to point to the newly editedChild instead
     * and populates the editedChild's parents list.
     */
    public void editParentToChildMappings(Student editedChild) {
        parents.forEach(parent -> {
            parent.setChild(this, editedChild);
            editedChild.parents.add(parent);
        });
    }

    /**
     * Remove parent from parents list
     */
    public void removeParent(Parent parentToRemove) {

        int index = parents.indexOf(parentToRemove);
        if (index == -1) {
            throw new PersonNotFoundException();
        }

        parents.remove(index);
    }

    /**
     * Remove child from parents' list of children
     */
    public void removeChildFromParents() {
        parents.forEach(parent -> parent.removeChild(this));
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
