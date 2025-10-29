package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Utility class to reduce redundancy in equals() and hashCode() testing.
 * This consolidates the repetitive test patterns found across Email, Phone, Name, Address tests.
 */
public class EqualsAndHashCodeTestUtil {

    /**
     * Tests standard equals() contract for value objects.
     * Reduces redundancy found in EmailTest, PhoneTest, NameTest, AddressTest.
     *
     * @param obj1 First object instance
     * @param obj2 Second object with same value
     * @param obj3 Object with different value
     */
    public static <T> void testEqualsAndHashCode(T obj1, T obj2, T obj3) {
        // Test reflexivity: x.equals(x) should return true
        assertTrue(obj1.equals(obj1), "Object should equal itself");

        // Test symmetry: x.equals(y) should return true if and only if y.equals(x) returns true
        assertTrue(obj1.equals(obj2), "Objects with same value should be equal");
        assertTrue(obj2.equals(obj1), "Equals should be symmetric");

        // Test transitivity would require more objects, skipped for simplicity

        // Test consistency: multiple invocations should return same result
        assertTrue(obj1.equals(obj2), "Equals should be consistent");
        assertTrue(obj1.equals(obj2), "Equals should be consistent on repeated calls");

        // Test null comparison: x.equals(null) should return false
        assertFalse(obj1.equals(null), "Object should not equal null");

        // Test type safety: comparing with different type should return false
        assertFalse(obj1.equals("String"), "Object should not equal different type");
        assertFalse(obj1.equals(5.0f), "Object should not equal different type");
        assertFalse(obj1.equals(123), "Object should not equal different type");

        // Test inequality with different value
        assertFalse(obj1.equals(obj3), "Objects with different values should not be equal");

        // Test hashCode contract
        testHashCodeContract(obj1, obj2, obj3);
    }

    /**
     * Tests hashCode() contract.
     * If two objects are equal, they must have the same hashCode.
     */
    private static <T> void testHashCodeContract(T obj1, T obj2, T obj3) {
        // Equal objects must have equal hash codes
        assertEquals(obj1.hashCode(), obj2.hashCode(),
            "Equal objects must have equal hash codes");

        // Consistent: multiple calls should return same value
        int hash1 = obj1.hashCode();
        int hash2 = obj1.hashCode();
        assertEquals(hash1, hash2, "HashCode should be consistent");

        // Different objects MAY have different hash codes (not required but good)
        // We don't assert this as it's not required by the contract
    }

    /**
     * Tests that null constructor parameter throws NullPointerException.
     * Consolidates null checks across all entity tests.
     */
    public static void testConstructorNullThrows(Runnable constructorCall) {
        try {
            constructorCall.run();
            throw new AssertionError("Expected NullPointerException was not thrown");
        } catch (NullPointerException e) {
            // Expected exception
        }
    }

    /**
     * Tests that invalid constructor parameter throws IllegalArgumentException.
     * Consolidates validation checks across all entity tests.
     */
    public static void testConstructorInvalidThrows(Runnable constructorCall) {
        try {
            constructorCall.run();
            throw new AssertionError("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }
}