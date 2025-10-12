package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;

public class PersonFactoryTest {

    @Test
    public void newPerson_student_returnsStudent() {
        Person p = Person.newPerson(new Name("Stu"), new Phone("81111111"),
                        new Email("stu@example.com"), new Address("Addr 1"),
                        Set.of(new Tag("t1")), PersonType.STUDENT);
        assertTrue(p instanceof Student);
        assertEquals(PersonType.STUDENT, p.getPersonType());
    }

    @Test
    public void newPerson_tutor_returnsTutor() {
        Person p = Person.newPerson(new Name("Tut"), new Phone("82222222"),
                        new Email("tut@example.com"), new Address("Addr 2"),
                        Set.of(new Tag("t2")), PersonType.TUTOR);
        assertTrue(p instanceof Tutor);
        assertEquals(PersonType.TUTOR, p.getPersonType());
    }

    @Test
    public void newPerson_parent_returnsParent() {
        Person p = Person.newPerson(new Name("Par"), new Phone("83333333"),
                        new Email("par@example.com"), new Address("Addr 3"),
                        Set.of(new Tag("t3")), PersonType.PARENT);
        assertTrue(p instanceof Parent);
        assertEquals(PersonType.PARENT, p.getPersonType());
    }
}
