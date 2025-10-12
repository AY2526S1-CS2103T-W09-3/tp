package seedu.address.model.person;

import java.util.Set;

import seedu.address.model.tag.Tag;

public class Tutor extends Person {
    /**
     * Constructs a {@code Tutor}.
     *
     * @param name A valid name.
     * @param phone A valid phone number.
     * @param email A valid email address.
     * @param address A valid address.
     * @param tags A set of tags.
     */
    public Tutor(Name name, Phone phone, Email email, Address address,
                    Set<Tag> tags) {
        super(name, phone, email, address, tags);
    }

    @Override
    public PersonType getPersonType() {
        return PersonType.TUTOR;
    }
}
