package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classroom.ClassName;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.tag.Tag;

/**
 * Tests for boundary values and edge cases across the application.
 */
public class BoundaryValueTest {

    @Test
    public void index_maxValue_throwsException() {
        // Integer.MAX_VALUE is actually valid for Index.fromOneBased
        // The parser prevents it, but the Index class itself accepts it
        Index index = Index.fromOneBased(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE - 1, index.getZeroBased());

        // Parser should reject it though
        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex(String.valueOf(Integer.MAX_VALUE));
        });
    }

    @Test
    public void index_zero_throwsException() {
        // Test that zero is invalid for one-based index
        assertThrows(IndexOutOfBoundsException.class, () -> {
            Index.fromOneBased(0);
        });
    }

    @Test
    public void index_negative_throwsException() {
        // Test that negative values are invalid
        assertThrows(IndexOutOfBoundsException.class, () -> {
            Index.fromOneBased(-1);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            Index.fromZeroBased(-1);
        });
    }

    @Test
    public void index_largeButValid_success() {
        // Test large but valid index values
        Index index = Index.fromOneBased(1000000);
        assertEquals(999999, index.getZeroBased());
        assertEquals(1000000, index.getOneBased());
    }

    @Test
    public void className_empty_throwsException() {
        // Test empty class name validation
        assertThrows(IllegalArgumentException.class, () -> {
            new ClassName("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new ClassName("   ");
        });
    }

    @Test
    public void className_veryLong_success() {
        // Test very long class names (should have reasonable limit)
        String longName = "A".repeat(100);
        ClassName className = new ClassName(longName);
        assertEquals(longName, className.value);
    }

    @Test
    public void name_empty_invalid() {
        // Test empty name validation
        assertThrows(IllegalArgumentException.class, () -> {
            new Name("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Name("   ");
        });
    }

    @Test
    public void name_singleCharacter_valid() {
        // Test single character names
        Name name = new Name("A");
        assertEquals("A", name.fullName);
    }

    @Test
    public void name_veryLong_success() {
        // Test very long names
        String longName = "A".repeat(50) + " " + "B".repeat(50);
        Name name = new Name(longName);
        assertEquals(longName, name.fullName);
    }

    @Test
    public void phone_minimumDigits_valid() {
        // Test minimum valid phone number
        Phone phone = new Phone("123");
        assertEquals("123", phone.value);
    }

    @Test
    public void phone_tooShort_invalid() {
        // Test phone number that's too short
        assertThrows(IllegalArgumentException.class, () -> {
            new Phone("12");
        });
    }

    @Test
    public void phone_veryLong_valid() {
        // Test very long phone number
        String longPhone = "9".repeat(20);
        Phone phone = new Phone(longPhone);
        assertEquals(longPhone, phone.value);
    }

    @Test
    public void email_minimumValid_success() {
        // Test minimal valid email
        Email email = new Email("a@b");
        assertEquals("a@b", email.value);
    }

    @Test
    public void email_veryLong_success() {
        // Test very long email address
        String longLocal = "a".repeat(50);
        String longDomain = "b".repeat(50) + ".com";
        Email email = new Email(longLocal + "@" + longDomain);
        assertTrue(email.value.contains("@"));
    }

    @Test
    public void address_empty_invalid() {
        // Test empty address
        assertThrows(IllegalArgumentException.class, () -> {
            new Address("");
        });
    }

    @Test
    public void tags_excessive_shouldLimit() {
        // Bug fixed - now limits to 20 tags
        Set<Tag> tags = IntStream.range(0, 100)
                .mapToObj(i -> new Tag("tag" + i))
                .collect(Collectors.toSet());

        // Should throw exception when creating person with >20 tags
        assertThrows(IllegalArgumentException.class, () -> {
            Student student = new Student(
                new Name("Test"),
                new Phone("12345678"),
                new Email("test@test.com"),
                new Address("Test Address"),
                tags
            );
        });
    }

    @Test
    public void tags_duplicate_ignored() {
        // Test duplicate tags are handled correctly
        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag("duplicate"));
        tags.add(new Tag("duplicate"));
        tags.add(new Tag("duplicate"));

        assertEquals(1, tags.size());
    }

    @Test
    public void parserUtil_parseIndex_maxValue() throws ParseException {
        // Test parsing maximum integer value
        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex(String.valueOf(Integer.MAX_VALUE));
        });
    }

    @Test
    public void parserUtil_parseIndex_zero() {
        // Test parsing zero
        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex("0");
        });
    }

    @Test
    public void parserUtil_parseIndex_negative() {
        // Test parsing negative numbers
        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex("-1");
        });
    }

    @Test
    public void parserUtil_parseIndex_nonNumeric() {
        // Test parsing non-numeric strings
        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex("abc");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex("1.5");
        });

        assertThrows(ParseException.class, () -> {
            ParserUtil.parseIndex("1 2");
        });
    }

    @Test
    public void addressBook_emptyCollections_valid() {
        // Test operations on empty address book
        AddressBook addressBook = new AddressBook();
        assertEquals(0, addressBook.getPersonList().size());
        assertEquals(0, addressBook.getClassList().size());
    }

    @Test
    public void uniquePersonList_emptyList_valid() {
        // Test operations on empty person list
        AddressBook addressBook = new AddressBook();
        Person person = ALICE;

        // Should be able to add to empty list
        addressBook.addPerson(person);
        assertEquals(1, addressBook.getPersonList().size());

        // Should be able to remove from single-element list
        addressBook.removePerson(person);
        assertEquals(0, addressBook.getPersonList().size());
    }

    @Test
    public void collections_nullElements_rejected() {
        // Test that null elements are rejected in collections
        Set<Tag> tags = new HashSet<>();
        assertThrows(NullPointerException.class, () -> {
            tags.add(null);
            new Student(
                new Name("Test"),
                new Phone("12345678"),
                new Email("test@test.com"),
                new Address("Test Address"),
                tags
            );
        });
    }

    @Test
    public void stringFields_leadingTrailingSpaces_trimmed() throws ParseException {
        // Test that leading/trailing spaces are trimmed
        Name name = ParserUtil.parseName("  John Doe  ");
        assertEquals("John Doe", name.fullName);

        Phone phone = ParserUtil.parsePhone("  12345678  ");
        assertEquals("12345678", phone.value);

        Email email = ParserUtil.parseEmail("  test@test.com  ");
        assertEquals("test@test.com", email.value);

        Address address = ParserUtil.parseAddress("  123 Main St  ");
        assertEquals("123 Main St", address.value);
    }

    @Test
    public void stringFields_unicodeCharacters_handled() {
        // Test Unicode characters in various fields
        Name name = new Name("李明 Wong");
        assertEquals("李明 Wong", name.fullName);

        Address address = new Address("Блок 123, Москва");
        assertEquals("Блок 123, Москва", address.value);

        Tag tag = new Tag("数学");
        assertEquals("数学", tag.tagName);
    }

    @Test
    public void specialCharacters_inFields_handled() {
        // Test special characters in fields
        Address address = new Address("123 & 456, St. John's #01-01");
        assertTrue(address.value.contains("&"));
        assertTrue(address.value.contains("#"));
        assertTrue(address.value.contains("'"));

        Email email = new Email("test+filter@sub-domain.example.com");
        assertTrue(email.value.contains("+"));
        assertTrue(email.value.contains("-"));
    }
}
