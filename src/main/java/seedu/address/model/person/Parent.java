package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Set;

import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;

/**
 * Represents a Parent in the address book. A Parent is a {@link Person} who can
 * be associated with one or more {@link Student} children.
 */
public class Parent extends Person {
    /**
     * The list of children associated with this parent. Managed internally by
     * the model; external callers should not modify it directly.
     */
    private ArrayList<Student> children = new ArrayList<>();

    /**
     * Constructs a {@code Parent}.
     *
     * @param name A valid name.
     * @param phone A valid phone number.
     * @param email A valid email address.
     * @param address A valid address.
     * @param tags A set of tags.
     */
    public Parent(Name name, Phone phone, Email email, Address address,
                    Set<Tag> tags) {
        super(name, phone, email, address, tags);
    }

    /**
     * Adds the given student as a child of this parent. Intended for internal
     * model use; not for external callers.
     *
     * @param child the student to associate with this parent
     */
    public void addChild(Student child) {
        if (!children.contains(child)) {
            children.add(child);
            child.addParent(this);
        }
    }

    public ArrayList<Student> getChildren() {
        return children;
    }

    public void setChild(Student target, Student editedChild) {
        requireAllNonNull(target, editedChild);

        int index = children.indexOf(target);
        if (index == -1) {
            throw new PersonNotFoundException();
        }

        if (!target.isSamePerson(editedChild) && children.contains(editedChild)) {
            throw new DuplicatePersonException();
        }

        children.set(index, editedChild);
    }


    /**
     * Changes all children's parent to point to the newly editedParent instead
     * and populates the editedParent's children list.
     */
    public void editChildToParentMappings(Parent editedParent) {
        children.forEach(child -> {
            child.setParent(this, editedParent);
            editedParent.children.add(child);
        });
    }

    /**
     * Remove this parent from children's parents list
     */
    public void removeParentFromChildren() {
        children.forEach(child -> child.removeParent(this));
    }

    /**
     * Remove child from children list
     */
    public void removeChild(Student childToRemove) {
        int index = children.indexOf(childToRemove);
        if (index == -1) {
            throw new PersonNotFoundException();
        }

        children.remove(index);
    }



    /**
     * Returns the type of this person.
     *
     * @return {@link PersonType#PARENT}
     */
    @Override
    public PersonType getPersonType() {
        return PersonType.PARENT;
    }
}
