package se.zeroplusx.musicapi.util;

/**
 * Container to ease passing around a tuple of two objects. This object provides a sensible
 * implementation of equals(), returning true if equals() is true on each of the contained
 * objects.
 * <p>
 * Note from xplodwild: This class has been copied from Android 4.4.4 - the reason behind that is
 * that Android 4.1 (and below) throws a NullPointerException when null objects are stored in the
 * pair (and it might happen, for instance in MusicBrainsClient.getAlbum).
 */
public class Pair<F, S> {
    public final F first;
    public final S second;

    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Convenience method for creating an appropriately typed pair.
     *
     * @param a the first object in the Pair
     * @param b the second object in the pair
     * @return a Pair that is templatized with the types of a and b
     */
    public static <A, B> Pair<A, B> create(A a, B b) {
        return new Pair<>(a, b);
    }

    /**
     * Checks the two objects for equality by delegating to their respective
     * {@link Object#equals(Object)} methods.
     *
     * @param o the {@link Pair} to which this one is to be checked for equality
     * @return true if the underlying objects of the Pair are both considered
     * equal
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> p = (Pair<?, ?>) o;

        if (first != null && second != null) {
            return first.equals(p.first) && second.equals(p.second);
        } else if (first != null) {
            return first.equals(p.first) && p.second == null;
        } else if (second != null) {
            return p.first == null && second.equals(p.second);
        } else {
            return p.first == null && p.second == null;
        }
    }

    /**
     * Compute a hash code using the hash codes of the underlying objects
     *
     * @return a hashcode of the Pair
     */
    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode());
    }
}