package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Utility to eliminate redundant equals() and hashCode() test patterns
 * found across EmailTest, PhoneTest, NameTest, AddressTest, etc.
 */
public class ModelTestUtil {

    /**
     * Standard equals() contract test used by all value objects.
     * Eliminates duplication across 5+ test files.
     */
    public static <T> void testEquals(T obj1, T sameAsObj1, T differentObj) {
        // same values -> returns true
        assertTrue(obj1.equals(sameAsObj1));

        // same object -> returns true
        assertTrue(obj1.equals(obj1));

        // null -> returns false
        assertFalse(obj1.equals(null));

        // different types -> returns false
        assertFalse(obj1.equals(5.0f));

        // different values -> returns false
        assertFalse(obj1.equals(differentObj));
    }

    /**
     * Standard hashCode() contract test.
     * Equal objects must have equal hashcodes.
     */
    public static <T> void testHashCode(T obj1, T sameAsObj1) {
        assertTrue(obj1.equals(sameAsObj1));
        assertEquals(obj1.hashCode(), sameAsObj1.hashCode());
    }
}