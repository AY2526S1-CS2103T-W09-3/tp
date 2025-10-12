package seedu.address.model.person;

import java.util.ArrayList;
import java.util.Set;

import seedu.address.model.tag.Tag;

public class Student extends Person {
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
     *
     * @param parent the parent to associate with this student; must not be null
     */
    public void addParent(Parent parent) {
        parents.add(parent);
        parent.addChild(this);
    }

    @Override
    public PersonType getPersonType() {
        return PersonType.STUDENT;
    }
}
