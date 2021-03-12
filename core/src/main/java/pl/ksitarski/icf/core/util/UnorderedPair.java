package pl.ksitarski.icf.core.util;

import java.util.HashSet;
import java.util.Set;

public class UnorderedPair<T> {
    private Set<T> t = new HashSet<>();

    public UnorderedPair(T first, T second) {
        t.add(first);
        t.add(second);
    }

    public static <T> UnorderedPair<T> of(T first, T second) {
        return new UnorderedPair<>(first, second);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnorderedPair<?> that = (UnorderedPair<?>) o;

        return t != null ? t.equals(that.t) : that.t == null;
    }

    @Override
    public int hashCode() {
        return t != null ? t.hashCode() : 0;
    }

}
