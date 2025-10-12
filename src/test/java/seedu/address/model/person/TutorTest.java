package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.testutil.TypicalPersons.BENSON;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class TutorTest {

    @Test
    public void constructor_validInputs_success() {
        Tutor tutor = new Tutor(BENSON.getName(), BENSON.getPhone(),
                        BENSON.getEmail(), BENSON.getAddress(),
                        BENSON.getTags());
        assertNotNull(tutor);
    }

    @Test
    public void getPersonType_returnsTutor() {
        Tutor tutor = new Tutor(BENSON.getName(), BENSON.getPhone(),
                        BENSON.getEmail(), BENSON.getAddress(),
                        BENSON.getTags());
        assertEquals(PersonType.TUTOR, tutor.getPersonType());
    }

    @Test
    public void equals_sameTutor_returnsTrue() {
        Tutor tutor = (Tutor) new PersonBuilder().withName("Benson").build();
        assertEquals(tutor, tutor);
    }
}
