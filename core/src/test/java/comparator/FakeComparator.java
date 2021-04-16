package comparator;

import pl.ksitarski.icf.core.prototype.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.prototype.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.prototype.image.IcfImage;
import pl.ksitarski.icf.core.prototype.image.IcfOptimizedImage;

public class FakeComparator extends ImageComparator {

    protected FakeComparator() {
        super("FakeComparator");
    }

    @Override
    public double compare(IcfOptimizedImage image0, IcfOptimizedImage image1) {
        return 0;
    }

    @Override
    public IcfOptimizedImage optimizeImage(IcfImage image) {
        return null;
    }

    @Override
    public boolean isSerializable() {
        return false;
    }

    @Override
    public IcfOptimizedImage deserialize(byte[] data, ImageInDatabase imageInDatabase) {
        return null;
    }
}
