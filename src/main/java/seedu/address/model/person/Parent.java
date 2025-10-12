package seedu.address.model.person;

import java.util.ArrayList;
import java.util.Set;

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
    void addChild(Student child) {
        children.add(child);
    }

    public ArrayList<Student> getChildren() {
        return children;
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
