package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class PersonTypeTest {

    @Test
    public void fromString_validStudent_returnsStudent() {
        assertEquals(PersonType.STUDENT, PersonType.fromString("student"));
        assertEquals(PersonType.STUDENT, PersonType.fromString("Student"));
        assertEquals(PersonType.STUDENT, PersonType.fromString("STUDENT"));
    }

    @Test
    public void fromString_validTutor_returnsTutor() {
        assertEquals(PersonType.TUTOR, PersonType.fromString("tutor"));
        assertEquals(PersonType.TUTOR, PersonType.fromString("Tutor"));
        assertEquals(PersonType.TUTOR, PersonType.fromString("TUTOR"));
    }

    @Test
    public void fromString_validParent_returnsParent() {
        assertEquals(PersonType.PARENT, PersonType.fromString("parent"));
        assertEquals(PersonType.PARENT, PersonType.fromString("Parent"));
        assertEquals(PersonType.PARENT, PersonType.fromString("PARENT"));
    }

    @Test
    public void toString_validPersonTypes_returnsCorrectString() {
        assertEquals("student", PersonType.STUDENT.toString());
        assertEquals("tutor", PersonType.TUTOR.toString());
        assertEquals("parent", PersonType.PARENT.toString());
    }

    @Test
    public void equals_samePersonType_returnsTrue() {
        assertEquals(PersonType.STUDENT, PersonType.STUDENT);
        assertEquals(PersonType.TUTOR, PersonType.TUTOR);
        assertEquals(PersonType.PARENT, PersonType.PARENT);
        assertNotEquals(PersonType.STUDENT, PersonType.TUTOR);
        assertNotEquals(PersonType.TUTOR, PersonType.PARENT);
    }
}
