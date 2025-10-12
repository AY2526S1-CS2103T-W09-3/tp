package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RoleTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Role(null));
    }

    @Test
    public void constructor_invalidRole_throwsIllegalArgumentException() {
        String invalidRole = "";
        assertThrows(IllegalArgumentException.class, () -> new Role(invalidRole));
    }

    @Test
    public void isValidRole() {
        // null role
        assertThrows(NullPointerException.class, () -> Role.isValidRole(null));

        // invalid roles
        assertFalse(Role.isValidRole("")); // empty string
        assertFalse(Role.isValidRole(" ")); // spaces only
        assertFalse(Role.isValidRole("teacher")); // not a valid role
        assertFalse(Role.isValidRole("student parent")); // multiple roles
        assertFalse(Role.isValidRole("student123")); // role with numbers
        assertFalse(Role.isValidRole("admin")); // not a valid role

        // valid roles - lowercase
        assertTrue(Role.isValidRole("student"));
        assertTrue(Role.isValidRole("tutor"));
        assertTrue(Role.isValidRole("parent"));

        // valid roles - uppercase
        assertTrue(Role.isValidRole("STUDENT"));
        assertTrue(Role.isValidRole("TUTOR"));
        assertTrue(Role.isValidRole("PARENT"));

        // valid roles - mixed case
        assertTrue(Role.isValidRole("Student"));
        assertTrue(Role.isValidRole("Tutor"));
        assertTrue(Role.isValidRole("Parent"));
        assertTrue(Role.isValidRole("sTuDeNt"));
    }

    @Test
    public void constructor_validRole_normalizesToLowercase() {
        assertEquals("student", new Role("STUDENT").value);
        assertEquals("tutor", new Role("Tutor").value);
        assertEquals("parent", new Role("pArEnT").value);
    }

    @Test
    public void toString_returnsLowercaseValue() {
        assertEquals("student", new Role("STUDENT").toString());
        assertEquals("tutor", new Role("Tutor").toString());
        assertEquals("parent", new Role("parent").toString());
    }

    @Test
    public void equals() {
        Role studentRole = new Role("student");

        // same values -> returns true
        assertTrue(studentRole.equals(new Role("student")));
        assertTrue(studentRole.equals(new Role("STUDENT"))); // case insensitive

        // same object -> returns true
        assertTrue(studentRole.equals(studentRole));

        // null -> returns false
        assertFalse(studentRole.equals(null));

        // different types -> returns false
        assertFalse(studentRole.equals(5.0f));
        assertFalse(studentRole.equals("student"));

        // different values -> returns false
        assertFalse(studentRole.equals(new Role("tutor")));
        assertFalse(studentRole.equals(new Role("parent")));
    }

    @Test
    public void hashCode_sameRole_sameHashCode() {
        Role role1 = new Role("student");
        Role role2 = new Role("STUDENT");
        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    public void hashCode_differentRole_differentHashCode() {
        Role studentRole = new Role("student");
        Role tutorRole = new Role("tutor");
        assertFalse(studentRole.hashCode() == tutorRole.hashCode());
    }
}