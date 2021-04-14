package comparator;

import pl.ksitarski.icf.core.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.image.IcfImage;
import pl.ksitarski.icf.core.image.IcfOptimizedImage;

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
