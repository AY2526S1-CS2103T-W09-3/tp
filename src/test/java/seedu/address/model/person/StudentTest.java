package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class StudentTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                        .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Student.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                        + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress() + ", tags="
                        + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());
    }

    @Test
    public void addParent_validParent_success() {
        Student student = (Student) new PersonBuilder().withPersonType(PersonType.STUDENT).build();
        Parent parent = (Parent) new PersonBuilder().withPersonType(PersonType.PARENT).build();

        student.addParent(parent);

        assertTrue(student.getParents().contains(parent));
        assertEquals(1, student.getParents().size());
    }

    @Test
    public void addParent_duplicateParent_notAdded() {
        Student student = (Student) new PersonBuilder().withPersonType(PersonType.STUDENT).build();
        Parent parent = (Parent) new PersonBuilder().withPersonType(PersonType.PARENT).build();

        student.addParent(parent);
        student.addParent(parent); // Try to add duplicate

        assertEquals(1, student.getParents().size());
    }

    @Test
    public void setParent_validParent_success() {
        Student student = (Student) new PersonBuilder().withPersonType(PersonType.STUDENT).build();
        Parent oldParent = (Parent) new PersonBuilder().withName("Old Parent")
                .withPersonType(PersonType.PARENT).build();
        Parent newParent = (Parent) new PersonBuilder().withName("New Parent")
                .withPersonType(PersonType.PARENT).build();

        student.addParent(oldParent);
        student.setParent(oldParent, newParent);

        assertFalse(student.getParents().contains(oldParent));
        assertTrue(student.getParents().contains(newParent));
        assertEquals(1, student.getParents().size());
    }

    @Test
    public void setParent_nonExistentParent_throwsPersonNotFoundException() {
        Student student = (Student) new PersonBuilder().withPersonType(PersonType.STUDENT).build();
        Parent existingParent = (Parent) new PersonBuilder().withName("Existing")
                .withPersonType(PersonType.PARENT).build();
        Parent nonExistentParent = (Parent) new PersonBuilder().withName("Non Existent")
                .withPersonType(PersonType.PARENT).build();
        Parent newParent = (Parent) new PersonBuilder().withName("New")
                .withPersonType(PersonType.PARENT).build();

        student.addParent(existingParent);

        assertThrows(seedu.address.model.person.exceptions.PersonNotFoundException.class, () ->
                student.setParent(nonExistentParent, newParent));
    }

    @Test
    public void setParent_duplicateParent_throwsDuplicatePersonException() {
        Student student = (Student) new PersonBuilder().withPersonType(PersonType.STUDENT).build();
        Parent parent1 = (Parent) new PersonBuilder().withName("Parent 1")
                .withPersonType(PersonType.PARENT).build();
        Parent parent2 = (Parent) new PersonBuilder().withName("Parent 2")
                .withPersonType(PersonType.PARENT).build();

        student.addParent(parent1);
        student.addParent(parent2);

        assertThrows(seedu.address.model.person.exceptions.DuplicatePersonException.class, () ->
                student.setParent(parent1, parent2));
    }

    @Test
    public void editParentToChildMappings_updatesParentsAndPopulatesNewStudent() {
        Student oldStudent = (Student) new PersonBuilder().withName("Old Student")
                .withPersonType(PersonType.STUDENT).build();
        Student newStudent = (Student) new PersonBuilder().withName("New Student")
                .withPersonType(PersonType.STUDENT).build();
        Parent parent1 = (Parent) new PersonBuilder().withName("Parent 1")
                .withPersonType(PersonType.PARENT).build();
        Parent parent2 = (Parent) new PersonBuilder().withName("Parent 2")
                .withPersonType(PersonType.PARENT).build();

        // Set up bidirectional relationships
        oldStudent.addParent(parent1);
        oldStudent.addParent(parent2);

        // Edit student
        oldStudent.editParentToChildMappings(newStudent);

        // Verify new student has all parents
        assertEquals(2, newStudent.getParents().size());
        assertTrue(newStudent.getParents().contains(parent1));
        assertTrue(newStudent.getParents().contains(parent2));
    }

    @Test
    public void removeParent_validParent_success() {
        Student student = (Student) new PersonBuilder().withPersonType(PersonType.STUDENT).build();
        Parent parent = (Parent) new PersonBuilder().withPersonType(PersonType.PARENT).build();

        student.addParent(parent);
        student.removeParent(parent);

        assertFalse(student.getParents().contains(parent));
        assertEquals(0, student.getParents().size());
    }

    @Test
    public void removeParent_nonExistentParent_throwsPersonNotFoundException() {
        Student student = (Student) new PersonBuilder().withPersonType(PersonType.STUDENT).build();
        Parent parent = (Parent) new PersonBuilder().withPersonType(PersonType.PARENT).build();

        assertThrows(seedu.address.model.person.exceptions.PersonNotFoundException.class, () ->
                student.removeParent(parent));
    }

    @Test
    public void removeChildFromParents_removesChildFromAllParents() {
        Student student = (Student) new PersonBuilder().withPersonType(PersonType.STUDENT).build();
        Parent parent1 = (Parent) new PersonBuilder().withName("Parent 1")
                .withPersonType(PersonType.PARENT).build();
        Parent parent2 = (Parent) new PersonBuilder().withName("Parent 2")
                .withPersonType(PersonType.PARENT).build();

        // Set up relationships
        student.addParent(parent1);
        student.addParent(parent2);

        // Verify parents have student
        assertTrue(parent1.getChildren().contains(student));
        assertTrue(parent2.getChildren().contains(student));

        // Remove student from parents
        student.removeChildFromParents();

        // Verify student removed from all parents
        assertFalse(parent1.getChildren().contains(student));
        assertFalse(parent2.getChildren().contains(student));
    }

    @Test
    public void getPersonType_returnsStudent() {
        Student student = (Student) new PersonBuilder().withPersonType(PersonType.STUDENT).build();
        assertEquals(PersonType.STUDENT, student.getPersonType());
    }
}
