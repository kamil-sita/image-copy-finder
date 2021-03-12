package pl.ksitarski.icf.comparator.standard;

import pl.ksitarski.icf.comparator.standard.util.BufferedImageKernelUtil;
import pl.ksitarski.icf.core.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.accessing.LoadableImage;
import pl.ksitarski.icf.core.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.image.IcfImage;
import pl.ksitarski.icf.core.image.IcfOptimizedImage;
import pl.ksitarski.icf.core.util.BufferedImageUtil;
import pl.ksitarski.icf.core.util.ConversionUtil;
import pl.ksitarski.icf.core.util.IntArgb;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class SimpleDifferentialScalingComparator extends ImageComparator {
    private static final int SIZE = 16;

    public SimpleDifferentialScalingComparator() {
        super("ICF_SimpleBlurComparator");
    }

    @Override
    public double compare(IcfOptimizedImage image0, IcfOptimizedImage image1) {
        var optimized0 = (SimpleBlurScalingOptimizedImage) image0;
        var optimized1 = (SimpleBlurScalingOptimizedImage) image1;

        int[] opImage0 = optimized0.image;
        int[] opImage1 = optimized1.image;

        int[] rgb0Arr = new int[4];
        int[] rgb1Arr = new int[4];

        double similarity = 0;

        int[] oldRgb0 = null;
        int[] oldRgb1 = null;

        for (int i = 0; i < SIZE * SIZE; i++) {
            int rgb0 = opImage0[i];
            int rgb1 = opImage1[i];

            IntArgb.asArray(rgb0, rgb0Arr);
            IntArgb.asArray(rgb1, rgb1Arr);

            if (i == 0) {
                similarity += IntArgb.compare(rgb0Arr, rgb1Arr);
            } else {
                int[] rgb0Diff = IntArgb.diff(oldRgb0, rgb0Arr);
                int[] rgb1Diff = IntArgb.diff(oldRgb1, rgb1Arr);
                similarity += IntArgb.compare(rgb0Diff, rgb1Diff);
            }

            oldRgb0 = rgb0Arr;
            oldRgb1 = rgb1Arr;

        }

        similarity /= (SIZE * SIZE);

        return similarity;
    }

    @Override
    public IcfOptimizedImage optimizeImage(IcfImage image) {
        BufferedImage bufferedImage = image.getImage();
        bufferedImage = BufferedImageKernelUtil.blurGaussian(bufferedImage, 3);
        bufferedImage = BufferedImageUtil.getHighQualityScaledImage(bufferedImage, SIZE, SIZE);


        return new SimpleBlurScalingOptimizedImage(
                bufferedImage,
                image.getParent(),
                this);
    }

    @Override
    public boolean isSerializable() {
        return true;
    }

    @Override
    public IcfOptimizedImage deserialize(byte[] data, ImageInDatabase imageInDatabase) {
        return new SimpleBlurScalingOptimizedImage(data, imageInDatabase, this);
    }

    private static class SimpleBlurScalingOptimizedImage extends IcfOptimizedImage {
        private final int[] image;
        private final LoadableImage parent;
        private final SimpleDifferentialScalingComparator simpleBlurScalingOptimizedImage;

        public SimpleBlurScalingOptimizedImage(byte[] in, LoadableImage parent, SimpleDifferentialScalingComparator simpleBlurScalingOptimizedImage) {
            this.parent = parent;
            image = ConversionUtil.convertToIntArray(in);
            this.simpleBlurScalingOptimizedImage = simpleBlurScalingOptimizedImage;
        }

        public SimpleBlurScalingOptimizedImage(BufferedImage image, LoadableImage parent, SimpleDifferentialScalingComparator simpleBlurScalingOptimizedImage) {
            this.image = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
            this.parent = parent;
            this.simpleBlurScalingOptimizedImage = simpleBlurScalingOptimizedImage;
        }

        @Override
        public LoadableImage getParent() {
            return parent;
        }

        @Override
        public ImageComparator getComparator() {
            return simpleBlurScalingOptimizedImage;
        }

        @Override
        public byte[] getSerialized() {
            return ConversionUtil.convertToByteArray(image);
        }
    }
}
