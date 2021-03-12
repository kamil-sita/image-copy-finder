package pl.ksitarski.icf.gui;

import com.github.kilianB.hashAlgorithms.DifferenceHash;
import pl.ksitarski.icf.comparator.jimagehash.JihDHash;
import pl.ksitarski.icf.comparator.jimagehash.JihPHash;
import pl.ksitarski.icf.comparator.jimagehash.JihWaveletHash;
import pl.ksitarski.icf.core.comparison.ImageComparingResult;
import pl.ksitarski.icf.core.comparison.comparator.ComplexImageComparator;
import pl.ksitarski.icf.core.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.image.IcfOptimizedImages;

import java.util.Set;

public class Comparators {
    private final static JihDHash JIH_D_HASH_64_TRIPLE = new JihDHash(64, DifferenceHash.Precision.Triple);
    private final static JihWaveletHash JIH_WAVELET_HASH_64_3 = new JihWaveletHash(64, 3);
    private final static JihPHash PHASH_64 = new JihPHash(64);

    public static final ComplexImageComparator cic5 = new ComplexImageComparator() {
        @Override
        public Set<ImageComparator> getImageComparators() {
            return Set.of(JIH_WAVELET_HASH_64_3, JIH_D_HASH_64_TRIPLE, PHASH_64);
        }

        @Override
        public ImageComparingResult compare(IcfOptimizedImages images1, IcfOptimizedImages images2) {
            double eq2 = compare(images1, images2, JIH_WAVELET_HASH_64_3);

            double eq3 = compare(images1, images2, JIH_D_HASH_64_TRIPLE);

            double eq4 = compare(images1, images2, PHASH_64);

            return ImageComparingResult.createSuccessful(
                    eq2 * eq3 * eq4,
                    images1.getParent(),
                    images2.getParent()
            );
        }

        @Override
        public double getRecommendedCutoff() {
            return 0.3;
        }

        @Override
        public String getName() {
            return "cic5";
        }

        @Override
        public String toString() {
            return getName();
        }
    };
}
