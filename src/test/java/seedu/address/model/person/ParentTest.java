package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.testutil.PersonBuilder;

public class ParentTest {

    @Test
    public void addChild_validChild_success() {
        Parent parent = (Parent) new PersonBuilder().withPersonType(PersonType.PARENT).build();
        Student child = (Student) new PersonBuilder().withPersonType(PersonType.STUDENT).build();

        parent.addChild(child);

        assertTrue(parent.getChildren().contains(child));
        assertEquals(1, parent.getChildren().size());
    }

    @Test
    public void addChild_duplicateChild_notAdded() {
        Parent parent = (Parent) new PersonBuilder().withPersonType(PersonType.PARENT).build();
        Student child = (Student) new PersonBuilder().withPersonType(PersonType.STUDENT).build();

        parent.addChild(child);
        parent.addChild(child); // Try to add duplicate

        assertEquals(1, parent.getChildren().size());
    }

    @Test
    public void setChild_validChild_success() {
        Parent parent = (Parent) new PersonBuilder().withPersonType(PersonType.PARENT).build();
        Student oldChild = (Student) new PersonBuilder().withName("Old Child")
                .withPersonType(PersonType.STUDENT).build();
        Student newChild = (Student) new PersonBuilder().withName("New Child")
                .withPersonType(PersonType.STUDENT).build();

        parent.addChild(oldChild);
        parent.setChild(oldChild, newChild);

        assertFalse(parent.getChildren().contains(oldChild));
        assertTrue(parent.getChildren().contains(newChild));
        assertEquals(1, parent.getChildren().size());
    }

    @Test
    public void setChild_nonExistentChild_throwsPersonNotFoundException() {
        Parent parent = (Parent) new PersonBuilder().withPersonType(PersonType.PARENT).build();
        Student existingChild = (Student) new PersonBuilder().withName("Existing")
                .withPersonType(PersonType.STUDENT).build();
        Student nonExistentChild = (Student) new PersonBuilder().withName("Non Existent")
                .withPersonType(PersonType.STUDENT).build();
        Student newChild = (Student) new PersonBuilder().withName("New")
                .withPersonType(PersonType.STUDENT).build();

        parent.addChild(existingChild);

        assertThrows(PersonNotFoundException.class, () -> parent.setChild(nonExistentChild, newChild));
    }

    @Test
    public void setChild_duplicateChild_throwsDuplicatePersonException() {
        Parent parent = (Parent) new PersonBuilder().withPersonType(PersonType.PARENT).build();
        Student child1 = (Student) new PersonBuilder().withName("Child 1")
                .withPersonType(PersonType.STUDENT).build();
        Student child2 = (Student) new PersonBuilder().withName("Child 2")
                .withPersonType(PersonType.STUDENT).build();

        parent.addChild(child1);
        parent.addChild(child2);

        assertThrows(DuplicatePersonException.class, () -> parent.setChild(child1, child2));
    }

    @Test
    public void editChildToParentMappings_updatesChildrenAndPopulatesNewParent() {
        Parent oldParent = (Parent) new PersonBuilder().withName("Old Parent")
                .withPersonType(PersonType.PARENT).build();
        Parent newParent = (Parent) new PersonBuilder().withName("New Parent")
                .withPersonType(PersonType.PARENT).build();
        Student child1 = (Student) new PersonBuilder().withName("Child 1")
                .withPersonType(PersonType.STUDENT).build();
        Student child2 = (Student) new PersonBuilder().withName("Child 2")
                .withPersonType(PersonType.STUDENT).build();

        // Set up bidirectional relationships
        oldParent.addChild(child1);
        oldParent.addChild(child2);

        // Edit parent
        oldParent.editChildToParentMappings(newParent);

        // Verify new parent has all children
        assertEquals(2, newParent.getChildren().size());
        assertTrue(newParent.getChildren().contains(child1));
        assertTrue(newParent.getChildren().contains(child2));
    }

    @Test
    public void removeParentFromChildren_removesParentFromAllChildren() {
        Parent parent = (Parent) new PersonBuilder().withPersonType(PersonType.PARENT).build();
        Student child1 = (Student) new PersonBuilder().withName("Child 1")
                .withPersonType(PersonType.STUDENT).build();
        Student child2 = (Student) new PersonBuilder().withName("Child 2")
                .withPersonType(PersonType.STUDENT).build();

        // Set up relationships
        parent.addChild(child1);
        parent.addChild(child2);

        // Verify children have parent
        assertTrue(child1.getParents().contains(parent));
        assertTrue(child2.getParents().contains(parent));

        // Remove parent from children
        parent.removeParentFromChildren();

        // Verify parent removed from all children
        assertFalse(child1.getParents().contains(parent));
        assertFalse(child2.getParents().contains(parent));
    }

    @Test
    public void removeChild_validChild_success() {
        Parent parent = (Parent) new PersonBuilder().withPersonType(PersonType.PARENT).build();
        Student child = (Student) new PersonBuilder().withPersonType(PersonType.STUDENT).build();

        parent.addChild(child);
        parent.removeChild(child);

        assertFalse(parent.getChildren().contains(child));
        assertEquals(0, parent.getChildren().size());
    }

    @Test
    public void removeChild_nonExistentChild_throwsPersonNotFoundException() {
        Parent parent = (Parent) new PersonBuilder().withPersonType(PersonType.PARENT).build();
        Student child = (Student) new PersonBuilder().withPersonType(PersonType.STUDENT).build();

        assertThrows(PersonNotFoundException.class, () -> parent.removeChild(child));
    }

    @Test
    public void getPersonType_returnsParent() {
        Parent parent = (Parent) new PersonBuilder().withPersonType(PersonType.PARENT).build();
        assertEquals(PersonType.PARENT, parent.getPersonType());
    }
}
