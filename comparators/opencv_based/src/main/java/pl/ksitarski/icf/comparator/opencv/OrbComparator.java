package pl.ksitarski.icf.comparator.opencv;

import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.ORB;
import pl.ksitarski.icf.core.prototype.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.prototype.accessing.LoadableImage;
import pl.ksitarski.icf.core.prototype.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.prototype.image.IcfImage;
import pl.ksitarski.icf.core.prototype.image.IcfOptimizedImage;

import java.util.ArrayList;
import java.util.List;

public class OrbComparator extends ImageComparator {
    private final ORB orb;

    public OrbComparator(int features) {
        super("OPENCV_ORB_" + features);
        orb = ORB.create(features);
    }


    @Override
    public double compare(IcfOptimizedImage image0, IcfOptimizedImage image1) {
        OrbOptimizedImage img0 = (OrbOptimizedImage) image0;
        OrbOptimizedImage img1 = (OrbOptimizedImage) image1;
        if (img0.imageDescriptors.empty() || img1.imageDescriptors.empty()) {
            return -1;
        }

        List<MatOfDMatch> dmatchesListOfMat = new ArrayList<>();
        DescriptorMatcher descriptorMatcher;
        descriptorMatcher=DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        descriptorMatcher.knnMatch(img0.imageDescriptors, img1.imageDescriptors, dmatchesListOfMat, 2);

        final double ratio = 0.8; //ratio test coefficient
        final boolean full = false;
        int goodMatchesCount = 0;

        if (full) {
            List<DMatch> goodMatchesList = new ArrayList<>();
            for (int matchIndex = 0; matchIndex < dmatchesListOfMat.size() ; matchIndex++) {
                DMatch[] arr = dmatchesListOfMat.get(matchIndex).toArray();
                if (arr[0].distance  < ratio * arr[1].distance) {
                    goodMatchesList.add(arr[0]);
                }
            }
            goodMatchesCount = goodMatchesList.size();
        } else {
            for (int matchIndex = 0; matchIndex < dmatchesListOfMat.size() ; matchIndex++) {
                DMatch[] arr = dmatchesListOfMat.get(matchIndex).toArray();
                if (arr.length >= 2) {
                    if (arr[0].distance  < ratio * arr[1].distance) {
                        goodMatchesCount++;
                    }
                }
            }
        }

        double score = (goodMatchesCount)/(Math.min(img0.imageDescriptors.size().height, img1.imageDescriptors.size().height));

        return Math.min(1, score);
    }

    @Override
    public IcfOptimizedImage optimizeImage(IcfImage image) {
        Mat matImage = OpenCvUtil.toGrayscaleMat(image.getImage());

        MatOfKeyPoint matOfKeyPoints = new MatOfKeyPoint();
        Feature2D descriptor = orb;
        descriptor.detect(matImage, matOfKeyPoints);

        Mat imageDescriptors = new Mat();
        Feature2D extractor = orb;
        extractor.compute(matImage, matOfKeyPoints, imageDescriptors);

        if (imageDescriptors.empty()) {
            throw new UnsupportedOperationException();
        }

        return new OrbOptimizedImage(
                this,
                image.getParent(),
                imageDescriptors
        );
    }

    @Override
    public boolean isSerializable() {
        return true;
    }

    @Override
    public IcfOptimizedImage deserialize(byte[] data, ImageInDatabase imageInDatabase) {
        return new OrbOptimizedImage(
                this,
                imageInDatabase,
                OpenCvUtil.deserializeFromData(0, data)
        );
    }

    private static class OrbOptimizedImage extends IcfOptimizedImage {
        private final OrbComparator comparator;
        private final LoadableImage parent;
        private final Mat imageDescriptors;

        public OrbOptimizedImage(OrbComparator comparator, LoadableImage parent, Mat imageDescriptors) {
            this.comparator = comparator;
            this.parent = parent;
            this.imageDescriptors = imageDescriptors;
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
            return OpenCvUtil.serializeFromMat(imageDescriptors);
        }
    }
}
