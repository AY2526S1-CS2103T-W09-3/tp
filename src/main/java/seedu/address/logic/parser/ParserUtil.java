package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ATTENDANCE_STATUS;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classroom.ClassName;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.PersonType;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser
 * classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the specified index is invalid (not non-zero
     *             unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}. Leading and
     * trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags)
                    throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a {@code String role} into a {@code PersonType}. Leading and trailing
     * whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code role} is invalid.
     */
    public static PersonType parsePersonType(String role)
                    throws ParseException {
        requireNonNull(role);
        String trimmedType = role.trim();
        if (!Role.isValidRole(trimmedType)) {
            throw new ParseException(Role.MESSAGE_CONSTRAINTS);
        }
        return PersonType.fromString(trimmedType);
    }

    /**
     * Parses a raw class name string. Trims and validates non-empty.
     */
    public static String parseClassName(String className) throws ParseException {
        requireNonNull(className);
        String trimmed = className.trim();
        System.out.println(trimmed);
        if (trimmed.isEmpty()) {
            throw new ParseException(ClassName.MESSAGE_CONSTRAINTS);
        }
        return trimmed;
    }

    /**
     * Parses a raw tutor name string. Trims and validates non-empty.
     * (Parser should only call this if a tutor value is present.)
     */
    public static String parseTutorName(String tutorName) throws ParseException {
        requireNonNull(tutorName);
        String trimmed = tutorName.trim();
        System.out.println(trimmed);
        if (trimmed.isEmpty()) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return trimmed;
    }

    /**
     * Parses a {@code String dateTime} into a {@code LocalDateTime}.
     * Expected format: yyyy-MM-dd HH:mm
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code dateTime} is invalid.
     */
    public static LocalDateTime parseDateTime(String dateTime) throws ParseException {
        requireNonNull(dateTime);
        String trimmed = dateTime.trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            return LocalDateTime.parse(trimmed, formatter);
        } catch (DateTimeParseException e) {
            throw new ParseException("Invalid date/time format. Expected format: yyyy-MM-dd HH:mm "
                    + "(e.g., 2024-03-15 14:30)");
        }
    }

    /**
     * Parses a {@code String sessionName} into a session name string.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code sessionName} is empty.
     */
    public static String parseSessionName(String sessionName) throws ParseException {
        requireNonNull(sessionName);
        String trimmed = sessionName.trim();
        if (trimmed.isEmpty()) {
            throw new ParseException("Session name cannot be empty.");
        }
        return trimmed;
    }

    /**
     * Parses a {@code String location} into a location string.
     * Leading and trailing whitespaces will be trimmed.
     * Returns null if the location string is empty (optional field).
     */
    public static String parseLocation(String location) {
        if (location == null) {
            return null;
        }
        String trimmed = location.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
    /**
     * Parses a string representation of attendance status and converts it to a Boolean value.
     *
     * @param status the attendance status string to parse (e.g., "present", "absent", "true", "false")
     * @return {@code true} if the status indicates present
     * @throws ParseException if the status string cannot be parsed into a valid attendance status
     */
    public static Boolean parseAttendanceStatus(String status) throws ParseException {
        if (status == null) {
            return null;
        }

        String normalizedStatus = status.trim();

        return switch (normalizedStatus) {
        case "PRESENT" -> true;
        case "ABSENT" -> false;
        default -> throw new ParseException(MESSAGE_INVALID_ATTENDANCE_STATUS);
        };
    }

}
