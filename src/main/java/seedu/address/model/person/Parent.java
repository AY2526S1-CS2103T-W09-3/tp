package seedu.address.model.person;

import java.util.ArrayList;
import java.util.Set;

import seedu.address.model.tag.Tag;

public class Parent extends Person {
    ArrayList<Student> children = new ArrayList<>();

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
     * Package private method children to add child to parent Not supposed to be
     * called outside of model Should only be called by addParent in Student
     */
    void addChild(Student child) {
        children.add(child);
    }
}
