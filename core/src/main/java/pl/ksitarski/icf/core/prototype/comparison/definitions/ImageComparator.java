package pl.ksitarski.icf.core.prototype.comparison.definitions;

import pl.ksitarski.icf.core.prototype.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.prototype.comparison.comparator.IcfNamedComparator;
import pl.ksitarski.icf.core.prototype.image.IcfOptimizedImage;
import pl.ksitarski.icf.core.prototype.image.IcfImage;


/**
 * Defines an image comparator.
 */
public abstract class ImageComparator implements IcfNamedComparator {

    private final String canonicalName;

    protected ImageComparator(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    /**
     * Compares two images.
     */
    public abstract double compare(IcfOptimizedImage image0, IcfOptimizedImage image1);

    /**e
     * Returns optimized image for this comparator.
     */
    public abstract IcfOptimizedImage optimizeImage(IcfImage image);


    /**
     * Decides whether given comparator is serializable (instances of IcfOptimizedImage returns something on serialize()).
     */
    public abstract boolean isSerializable();

    /**
     * Deserializes IcfOptimizedImage from given data. Needs to be implemented only if this ImageComparator is serializable.
     */
    public abstract IcfOptimizedImage deserialize(byte[] data, ImageInDatabase imageInDatabase);

    /**
     * Canonical name that is used to distinguish between image comparators. Comparators with same name are always equal.
     */
    public final String canonicalName() {
        return canonicalName;
    }

    @Override
    public String getName() {
        return canonicalName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageComparator that = (ImageComparator) o;

        return canonicalName.equals(that.canonicalName);
    }

    @Override
    public int hashCode() {
        return canonicalName.hashCode();
    }
}
