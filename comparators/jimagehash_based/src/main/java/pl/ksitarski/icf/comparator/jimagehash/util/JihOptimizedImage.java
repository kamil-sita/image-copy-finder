package pl.ksitarski.icf.comparator.jimagehash.util;

import com.github.kilianB.hash.Hash;
import pl.ksitarski.icf.core.prototype.accessing.LoadableImage;
import pl.ksitarski.icf.core.prototype.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.prototype.image.IcfOptimizedImage;

import java.util.Objects;


public class JihOptimizedImage extends IcfOptimizedImage {

    private final LoadableImage parent;
    private final ImageComparator comparator;
    private final Hash imageHash;

    public JihOptimizedImage(LoadableImage parent, ImageComparator comparator, Hash imageHash) {
        Objects.requireNonNull(parent);
        Objects.requireNonNull(comparator);
        Objects.requireNonNull(imageHash);
        this.parent = parent;
        this.comparator = comparator;
        this.imageHash = imageHash;
    }

    @Override
    public LoadableImage getParent() {
        return parent;
    }

    @Override
    public ImageComparator getComparator() {
        return comparator;
    }

    @Override
    public byte[] getSerialized() {
        return imageHash.toByteArray();
    }

    public Hash getImageHash() {
        return imageHash;
    }
}
