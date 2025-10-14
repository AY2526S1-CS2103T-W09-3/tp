package seedu.address.model.classes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Tutor;
import seedu.address.model.tag.Tag;

/**
 * Unit tests for {@link ClassList}.
 */
public class ClassListTest {

    private ClassList classList;
    private Tutor tutor1;
    private Tutor tutor2;
    private Class class1;
    private Class class2;

    @BeforeEach
    void setUp() {
        classList = new ClassList();
        Set<Tag> emptyTags = new HashSet<>();

        tutor1 = new Tutor(
                new Name("Mr Smith"),
                new Phone("91234567"),
                new Email("smith@example.com"),
                new Address("1 Tutor Lane"),
                emptyTags
        );

        tutor2 = new Tutor(
                new Name("Ms Lee"),
                new Phone("91239876"),
                new Email("lee@example.com"),
                new Address("5 Learning Street"),
                emptyTags
        );

        class1 = new Class(tutor1, "CS2103T T12");
        class2 = new Class(tutor2, "CS2103T T13");
    }

    @Test
    @DisplayName("Add class successfully")
    void addClass_success() {
        classList.addClass(class1);
        assertTrue(classList.contains(class1));
        assertEquals(1, classList.size());
    }

    @Test
    @DisplayName("Adding duplicate class throws exception")
    void addClass_duplicateThrows() {
        classList.addClass(class1);
        assertThrows(IllegalArgumentException.class, () -> classList.addClass(class1));
    }

    @Test
    @DisplayName("Remove class successfully")
    void removeClass_success() {
        classList.addClass(class1);
        classList.addClass(class2);

        boolean removed = classList.removeClass(class1);
        assertTrue(removed);
        assertFalse(classList.contains(class1));
        assertEquals(1, classList.size());
    }

    @Test
    @DisplayName("Remove class that does not exist returns false")
    void removeClass_nonExistent() {
        boolean removed = classList.removeClass(class1);
        assertFalse(removed);
    }

    @Test
    @DisplayName("Contains check works correctly")
    void contains_check() {
        classList.addClass(class1);
        assertTrue(classList.contains(class1));
        assertFalse(classList.contains(class2));
    }

    @Test
    @DisplayName("Clear removes all classes")
    void clear_removesAll() {
        classList.addClass(class1);
        classList.addClass(class2);

        classList.clear();
        assertEquals(0, classList.size());
        assertFalse(classList.contains(class1));
        assertFalse(classList.contains(class2));
    }

    @Test
    @DisplayName("Iterator iterates over all classes")
    void iterator_works() {
        classList.addClass(class1);
        classList.addClass(class2);

        Iterator<Class> it = classList.iterator();
        assertTrue(it.hasNext());
        assertEquals(class1, it.next());
        assertTrue(it.hasNext());
        assertEquals(class2, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    @DisplayName("toString returns formatted class list")
    void toString_formatsCorrectly() {
        assertEquals("[No classes available]", classList.toString());

        classList.addClass(class1);
        classList.addClass(class2);

        String output = classList.toString();
        assertTrue(output.contains("CS2103T T12"));
        assertTrue(output.contains("CS2103T T13"));
        assertTrue(output.contains("Mr Smith"));
        assertTrue(output.contains("Ms Lee"));
    }

    @Test
    @DisplayName("Size returns number of classes")
    void size_returnsCorrectValue() {
        assertEquals(0, classList.size());
        classList.addClass(class1);
        assertEquals(1, classList.size());
        classList.addClass(class2);
        assertEquals(2, classList.size());
    }
}
