package seedu.address.ui;

import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonType;
import seedu.address.model.person.Student;
import seedu.address.model.person.Tutor;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label role;
    @FXML
    private Label enrolledClasses;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);

        // Display the person's role (type) as a chip with role-specific styling
        String roleType = person.getPersonType().toString();
        role.setText(roleType);
        role.getStyleClass().add("role-" + roleType.toLowerCase());

        // Display enrolled classes for students and tutors
        if (person.getPersonType() == PersonType.STUDENT) {
            Student student = (Student) person;
            String classes = student.getTuitionClasses().stream()
                    .map(tc -> tc.getClassName())
                    .collect(Collectors.joining(", "));
            enrolledClasses.setText(classes.isEmpty() ? "No classes" : classes);
            enrolledClasses.setVisible(true);
            enrolledClasses.setManaged(true);
        } else if (person.getPersonType() == PersonType.TUTOR) {
            Tutor tutor = (Tutor) person;
            String classes = tutor.getTuitionClasses().stream()
                    .map(tc -> tc.getClassName())
                    .collect(Collectors.joining(", "));
            enrolledClasses.setText(classes.isEmpty() ? "No classes" : classes);
            enrolledClasses.setVisible(true);
            enrolledClasses.setManaged(true);
        } else {
            enrolledClasses.setVisible(false);
            enrolledClasses.setManaged(false);
        }
    }
}
