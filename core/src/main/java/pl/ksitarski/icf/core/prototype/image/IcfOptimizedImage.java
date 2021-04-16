package pl.ksitarski.icf.core.prototype.image;
import pl.ksitarski.icf.core.prototype.accessing.LoadableImage;
import pl.ksitarski.icf.core.prototype.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.prototype.exc.IllegalAccessException;
import pl.ksitarski.icf.core.prototype.exc.NotYetImplementedException;

/**
 * Represents optimized version of image for given comparator. Each comparator uses their own optimized image, and must
 * have it, even if optimized image is same as non-optimized image.
 *
 * Each optimized image has two "parents" - Loadable Image (which defines a way it was loaded) and comparator (which
 * defines its layout).
 */
public abstract class IcfOptimizedImage {

    public abstract LoadableImage getParent();

    public abstract ImageComparator getComparator();

    /**
     * Provides serializable representation of this ImageComparator. Needs to be implemented only if ImageComparator
     * for this image is also serializable - otherwise the behaviour is undefined.
     */
    public byte[] getSerialized() {
        if (getComparator().isSerializable()) {
            throw new NotYetImplementedException("You need to implement this method for serializable comparators! This is a programmer error.");
        } else {
            throw new IllegalAccessException();
        }
    }
}
