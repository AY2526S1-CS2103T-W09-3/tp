package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classroom.ClassName;
import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;

public class ListClassCommandTest {

    @Test
    public void execute_emptyClassList_success() throws Exception {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        ListClassCommand command = new ListClassCommand();

        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("Listed all classes"));
        assertTrue(result.getFeedbackToUser().contains("[No classes]"));
    }

    @Test
    public void execute_classWithNoStudents_success() throws Exception {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        TuitionClass tuitionClass = new TuitionClass(new ClassName("Sec1-Math-A"));
        model.addClass(tuitionClass);

        ListClassCommand command = new ListClassCommand();
        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("Listed all classes"));
        assertTrue(result.getFeedbackToUser().contains("Sec1-Math-A"));
        assertTrue(result.getFeedbackToUser().contains("[No students]"));
    }

    @Test
    public void execute_classWithStudents_success() throws Exception {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());

        Student student1 = new Student(
                new Name("Alice Tan"),
                new Phone("91234567"),
                new Email("alice@example.com"),
                new Address("123 Main St"),
                new java.util.HashSet<>()
        );
        Student student2 = new Student(
                new Name("Bob Lee"),
                new Phone("98765432"),
                new Email("bob@example.com"),
                new Address("456 Second St"),
                new java.util.HashSet<>()
        );

        model.addPerson(student1);
        model.addPerson(student2);

        TuitionClass tuitionClass = new TuitionClass(new ClassName("Sec1-Math-A"));
        model.addClass(tuitionClass);

        model.addStudentToClass(student1, tuitionClass);
        model.addStudentToClass(student2, tuitionClass);

        ListClassCommand command = new ListClassCommand();
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed all classes"));
        assertTrue(feedback.contains("Sec1-Math-A"));
        assertTrue(feedback.contains("Alice Tan"));
        assertTrue(feedback.contains("Bob Lee"));
    }

    @Test
    public void execute_multipleClasses_success() throws Exception {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());

        Student student1 = new Student(
                new Name("Charlie Wong"),
                new Phone("91234567"),
                new Email("charlie@example.com"),
                new Address("123 Main St"),
                new java.util.HashSet<>()
        );

        model.addPerson(student1);

        TuitionClass class1 = new TuitionClass(new ClassName("Sec1-Math-A"));
        TuitionClass class2 = new TuitionClass(new ClassName("Sec2-Science-B"));

        model.addClass(class1);
        model.addClass(class2);

        model.addStudentToClass(student1, class1);

        ListClassCommand command = new ListClassCommand();
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Listed all classes"));
        assertTrue(feedback.contains("Sec1-Math-A"));
        assertTrue(feedback.contains("Sec2-Science-B"));
        assertTrue(feedback.contains("Charlie Wong"));
        assertTrue(feedback.contains("[No students]"));
    }

    @Test
    public void execute_formatClassWithStudents_correctFormat() throws Exception {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());

        Student student = new Student(
                new Name("David Lim"),
                new Phone("91234567"),
                new Email("david@example.com"),
                new Address("123 Main St"),
                new java.util.HashSet<>()
        );

        model.addPerson(student);

        TuitionClass tuitionClass = new TuitionClass(new ClassName("Sec1-Math-A"));
        model.addClass(tuitionClass);
        model.addStudentToClass(student, tuitionClass);

        ListClassCommand command = new ListClassCommand();
        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("Sec1-Math-A (Tutor: Unassigned): David Lim"));
    }
}
