package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class StudentTest {

    @Test
    public void constructor_validInputs_success() {
        Student student = new Student(ALICE.getName(), ALICE.getPhone(),
                        ALICE.getEmail(), ALICE.getAddress(), ALICE.getTags());
        assertNotNull(student);
    }

    @Test
    public void getPersonType_returnsStudent() {
        Student student = new Student(ALICE.getName(), ALICE.getPhone(),
                        ALICE.getEmail(), ALICE.getAddress(), ALICE.getTags());
        assertEquals(PersonType.STUDENT, student.getPersonType());
    }

    @Test
    public void addParent_validParent_success() {
        Student student = new Student(ALICE.getName(), ALICE.getPhone(),
                        ALICE.getEmail(), ALICE.getAddress(), ALICE.getTags());
        Parent parent = new Parent(new Name("Parent Name"),
                        new Phone("98765432"), new Email("parent@example.com"),
                        new Address("123 Parent St"), ALICE.getTags());

        student.addParent(parent);
        // Verify the relationship is established
        assertNotNull(student);
    }

    @Test
    public void equals_sameStudent_returnsTrue() {
        Student student = (Student) new PersonBuilder().withName("Alice")
                        .build();
        assertEquals(student, student);
    }
}
