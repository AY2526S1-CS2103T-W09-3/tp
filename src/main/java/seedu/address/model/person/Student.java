package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Set;

import seedu.address.model.classroom.TuitionClass;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;


/**
 * Represents a Student in the address book. A Student is a {@link Person} that
 * can be associated with one or more {@link Parent} instances.
 * <p>
 * Note: Parents are stored in insertion order. This class does not prevent
 * duplicates. Initially, a newly constructed Student has no associated parents.
 */
public class Student extends Person {

    /**
     * Parents associated with this student. This list is internal to the model
     * and may contain duplicates.
     */
    private ArrayList<Parent> parents = new ArrayList<>();

    /**
     * Tuition classes this student is enrolled in.
     */
    private ArrayList<TuitionClass> tuitionClasses = new ArrayList<>();

    /**
     * Constructs a {@code Student}.
     *
     * @param name A valid name.
     * @param phone A valid phone number.
     * @param email A valid email address.
     * @param address A valid address.
     * @param tags A set of tags.
     */
    public Student(Name name, Phone phone, Email email, Address address,
                    Set<Tag> tags) {
        super(name, phone, email, address, tags);
    }

    /**
     * Adds the given parent to this student and establishes a bidirectional
     * relationship. Appends the parent to this student's list of parents and
     * invokes {@code parent.addChild(this)}.
     * <p>
     * No deduplication is performed; if the parent is already associated, it
     * will be added again.
     *
     * @param parent the parent to associate with this student; must not be null
     */
    public void addParent(Parent parent) {
        if (!parents.contains(parent)) {
            parents.add(parent);
            parent.addChild(this); // maintain bidirectional link
        }
    }

    public ArrayList<Parent> getParents() {
        return parents;
    }

    public ArrayList<TuitionClass> getTuitionClasses() {
        return tuitionClasses;
    }

    /**
     * Adds this student to a tuition class if not already enrolled.
     */
    public void addClass(TuitionClass tuitionClass) {
        if (!tuitionClasses.contains(tuitionClass)) {
            tuitionClasses.add(tuitionClass);
        }
    }

    /**
     * Removes this student from a tuition class.
     * @throws PersonNotFoundException if not enrolled in this class
     */
    public void removeClass(TuitionClass tuitionClass) {
        if (!tuitionClasses.remove(tuitionClass)) {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Safely removes this student from a tuition class without throwing exception.
     * @return true if the class was removed, false if not enrolled
     */
    public boolean removeClassSafely(TuitionClass tuitionClass) {
        return tuitionClasses.remove(tuitionClass);
    }

    /**
     * Removes this student from all tuition classes.
     */
    public void removeFromAllClasses() {
        tuitionClasses.forEach(tuitionClass -> tuitionClass.removeStudent(this));
    }

    /**
     * Changes the parent to the newly appointed parent
     */
    public void setParent(Parent target, Parent editedParent) {
        requireAllNonNull(target, editedParent);

        int index = parents.indexOf(target);
        if (index == -1) {
            throw new PersonNotFoundException();
        }

        if (!target.isSamePerson(editedParent) && parents.contains(editedParent)) {
            throw new DuplicatePersonException();
        }

        parents.set(index, editedParent);
    }


    /**
     * Changes all parent's child to point to the newly editedChild instead
     * and populates the editedChild's parents list.
     */
    public void editParentToChildMappings(Student editedChild) {
        parents.forEach(parent -> {
            parent.setChild(this, editedChild);
            editedChild.parents.add(parent);
        });
    }

    /**
     * Changes all tuition classes' student to point to the newly editedStudent instead
     * and populates the editedStudent's tuitionClasses list.
     */
    public void editTuitionClassMappings(Student editedStudent) {
        tuitionClasses.forEach(tuitionClass -> {
            tuitionClass.setStudent(this, editedStudent);
            editedStudent.tuitionClasses.add(tuitionClass);
        });
    }

    /**
     * Remove parent from parents list
     */
    public void removeParent(Parent parentToRemove) {
        if (!parents.remove(parentToRemove)) {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Remove child from parents' list of children
     */
    public void removeChildFromParents() {
        parents.forEach(parent -> parent.removeChild(this));
    }

    /**
     * Delete student
     */
    @Override
    public void delete() {
        // Remove instance of this student from related parents
        this.removeChildFromParents();
        // Remove instance of student from related TuitionClass-es
        this.removeFromAllClasses();
    }

    /**
     * Handles editing of this student by updating or removing relationships.
     */
    @Override
    public void handleEdit(Person editedPerson, boolean isTypeEdited) {
        if (isTypeEdited) {
            // Type changed, remove this student from all parents and classes
            this.removeChildFromParents();
            this.removeFromAllClasses();
        } else {
            // Type unchanged, update bidirectional relationships
            Student editedStudent = (Student) editedPerson;
            this.editParentToChildMappings(editedStudent);
            this.editTuitionClassMappings(editedStudent);
        }
    }

    /**
     * Returns this person's type.
     *
     * @return {@link PersonType#STUDENT}
     */
    @Override
    public PersonType getPersonType() {
        return PersonType.STUDENT;
    }
}
