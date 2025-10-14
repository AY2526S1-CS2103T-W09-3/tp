package seedu.address.model.classroom;

import static java.util.Objects.requireNonNull;

public class TuitionClass {
    private final ClassName name;

    public TuitionClass(ClassName name) {
        requireNonNull(name);
        this.name = name;
    }

    public ClassName getName() { return name; }

    /** Identity check: same class name means same class. */
    public boolean isSameClass(TuitionClass other) {
        return other != null && name.equals(other.name);
    }

    @Override public String toString() { 
        return name.toString(); }

    @Override public boolean equals(Object o) {
        return o == this || (o instanceof TuitionClass && name.equals(((TuitionClass) o).name));
    }

    @Override public int hashCode() { 
        return name.hashCode(); }
}
