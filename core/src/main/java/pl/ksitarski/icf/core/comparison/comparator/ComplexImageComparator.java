package pl.ksitarski.icf.core.comparison.comparator;

import pl.ksitarski.icf.core.comparison.ImageComparingResult;
import pl.ksitarski.icf.core.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.image.IcfOptimizedImages;

import java.util.Set;

public abstract class ComplexImageComparator implements IcfNamedComparator {
    public abstract Set<ImageComparator> getImageComparators();
    public abstract ImageComparingResult compare(IcfOptimizedImages images1, IcfOptimizedImages images2);
    public abstract double getRecommendedCutoff();

    protected static double compare(IcfOptimizedImages im0, IcfOptimizedImages im1, ImageComparator comparator) {
        return comparator.compare(im0.getForComparator(comparator), im1.getForComparator(comparator));
    }

    public static ComplexImageComparator toComplexImageComparator(ImageComparator comparator) {
        return new ComplexImageComparator() {
            @Override
            public Set<ImageComparator> getImageComparators() {
                return Set.of(comparator);
            }

            @Override
            public ImageComparingResult compare(IcfOptimizedImages images1, IcfOptimizedImages images2) {
                return ImageComparingResult.createSuccessful(
                        comparator.compare(images1.getForComparator(comparator), images2.getForComparator(comparator)),
                        images1.getParent(),
                        images2.getParent()
                );
            }

            @Override
            public double getRecommendedCutoff() {
                return 0.7;
            }

            @Override
            public String getName() {
                return comparator.canonicalName();
            }
        };
    }

}
