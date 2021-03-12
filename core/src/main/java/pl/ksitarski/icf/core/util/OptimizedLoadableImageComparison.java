package pl.ksitarski.icf.core.util;

import pl.ksitarski.icf.core.accessing.LoadableImage;

public class OptimizedLoadableImageComparison {
    // min, max - hashes of loadable images that are in database
    // minHash, maxHash - hashes of loadable images that are not in database

    private long minHash = 0;
    private long maxHash = 0;
    private final LoadableImage first;
    private final LoadableImage second;

    public OptimizedLoadableImageComparison(LoadableImage first, LoadableImage second) {
        minHash = first.hashCode();
        maxHash = second.hashCode();
        if (minHash > maxHash) {
            long tmp = maxHash;
            maxHash = minHash;
            minHash = tmp;
        }
        this.first = first;
        this.second = second;
    }

    public static OptimizedLoadableImageComparison of(LoadableImage first, LoadableImage second) {
        return new OptimizedLoadableImageComparison(first, second);
    }

    public LoadableImage getFirst() {
        return first;
    }

    public LoadableImage getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptimizedLoadableImageComparison that = (OptimizedLoadableImageComparison) o;

        if (first.equals(that.first) && second.equals(that.second)) {
            return true;
        }

        if (first.equals(that.second) && second.equals(that.first)) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = (int) (minHash ^ (minHash >>> 32));
        result = 31 * result + (int) (maxHash ^ (maxHash >>> 32));
        return result;
    }
}
