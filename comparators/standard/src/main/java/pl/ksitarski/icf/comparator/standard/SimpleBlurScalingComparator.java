package pl.ksitarski.icf.comparator.standard;

import pl.ksitarski.icf.comparator.standard.util.BufferedImageKernelUtil;
import pl.ksitarski.icf.core.prototype.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.prototype.accessing.LoadableImage;
import pl.ksitarski.icf.core.prototype.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.prototype.image.IcfImage;
import pl.ksitarski.icf.core.prototype.image.IcfOptimizedImage;
import pl.ksitarski.icf.core.prototype.util.BufferedImageUtil;
import pl.ksitarski.icf.core.prototype.util.ConversionUtil;
import pl.ksitarski.icf.core.prototype.util.IntArgb;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class SimpleBlurScalingComparator extends ImageComparator {
    private static final int SIZE = 16;

    public SimpleBlurScalingComparator() {
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

        for (int i = 0; i < SIZE * SIZE; i++) {
            int rgb0 = opImage0[i];
            int rgb1 = opImage1[i];

            IntArgb.asArray(rgb0, rgb0Arr);
            IntArgb.asArray(rgb1, rgb1Arr);

            similarity += IntArgb.compare(rgb0Arr, rgb1Arr);

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
        private final SimpleBlurScalingComparator simpleBlurScalingComparator;

        public SimpleBlurScalingOptimizedImage(byte[] in, LoadableImage parent, SimpleBlurScalingComparator simpleBlurScalingComparator) {
            this.parent = parent;
            image = ConversionUtil.convertToIntArray(in);
            this.simpleBlurScalingComparator = simpleBlurScalingComparator;
        }

        public SimpleBlurScalingOptimizedImage(BufferedImage image, LoadableImage parent, SimpleBlurScalingComparator simpleBlurScalingComparator) {
            this.image = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
            this.parent = parent;
            this.simpleBlurScalingComparator = simpleBlurScalingComparator;
        }

        @Override
        public LoadableImage getParent() {
            return parent;
        }

        @Override
        public SimpleBlurScalingComparator getComparator() {
            return simpleBlurScalingComparator;
        }

        @Override
        public byte[] getSerialized() {
            return ConversionUtil.convertToByteArray(image);
        }
    }
}
