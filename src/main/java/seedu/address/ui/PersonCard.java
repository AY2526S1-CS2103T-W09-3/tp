package seedu.address.ui;

import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    @FXML
    private ImageView roleIcon;

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

        // Set role-specific icon
        setRoleIcon(roleType);

        // Apply role-specific styling to the icon
        roleIcon.getStyleClass().add("role-icon-" + roleType.toLowerCase());

        // Apply modern styling to the card
        cardPane.getStyleClass().add("modern-card");
    }

    /**
     * Sets the appropriate icon for the person's role.
     */
    private void setRoleIcon(String roleType) {
        String imagePath;
        switch (roleType.toLowerCase()) {
        case "student":
            imagePath = "/images/student_icon.png";
            break;
        case "tutor":
            imagePath = "/images/tutor_icon.png";
            break;
        case "parent":
            imagePath = "/images/parent_icon.png";
            break;
        default:
            imagePath = "/images/info_icon.png";
            break;
        }

        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            roleIcon.setImage(image);
        } catch (Exception e) {
            // Fallback to info icon if the specific role icon is not found
            try {
                Image fallbackImage = new Image(getClass().getResourceAsStream("/images/info_icon.png"));
                roleIcon.setImage(fallbackImage);
            } catch (Exception fallbackException) {
                // If even the fallback fails, set no image
                roleIcon.setImage(null);
            }
        }
    }
}
