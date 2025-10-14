package seedu.address.model.classes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a collection of {@link Class} objects.
 * Provides methods to manage and access classes within the system.
 */
public class ClassList implements Iterable<Class> {

    /** Internal list of classes. */
    private final List<Class> classes;

    /** Constructs an empty ClassList. */
    public ClassList() {
        this.classes = new ArrayList<>();
    }

    /**
     * Adds a class to the list if it does not already exist.
     *
     * @param newClass the class to add
     * @throws IllegalArgumentException if the class already exists
     */
    public void addClass(Class newClass) {
        if (contains(newClass)) {
            throw new IllegalArgumentException("Duplicate class: " + newClass.getClassName());
        }
        classes.add(newClass);
    }

    /**
     * Removes a class from the list.
     *
     * @param target the class to remove
     * @return true if the class was removed successfully
     */
    public boolean removeClass(Class target) {
        return classes.remove(target);
    }

    /**
     * Checks whether a class already exists in the list.
     *
     * @param toCheck the class to check
     * @return true if the class exists, false otherwise
     */
    public boolean contains(Class toCheck) {
        return classes.contains(toCheck);
    }

    /**
     * Returns the list of all classes.
     * Note: The returned list is unmodifiable to prevent external modification.
     *
     * @return an unmodifiable list of classes
     */
    public List<Class> getClassList() {
        return List.copyOf(classes);
    }

    /**
     * Returns an iterator over the classes in this list.
     *
     * @return iterator of classes
     */
    @Override
    public Iterator<Class> iterator() {
        return classes.iterator();
    }

    /**
     * Returns the number of classes in the list.
     *
     * @return the size of the class list
     */
    public int size() {
        return classes.size();
    }

    /**
     * Clears all classes from the list.
     */
    public void clear() {
        classes.clear();
    }


    /**
     * Returns a string representation of all classes in this list.
     *
     * @return formatted string of class names and tutors
     */
    @Override
    public String toString() {
        if (classes.isEmpty()) {
            return "[No classes available]";
        }

        StringBuilder sb = new StringBuilder("ClassList:\n");
        for (Class c : classes) {
            sb.append(" - ")
                    .append(c.getClassName())
                    .append(" (Tutor: ")
                    .append(c.getTutor().getName())
                    .append(")\n");
        }
        return sb.toString().trim();
    }
}
