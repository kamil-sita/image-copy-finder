package pl.ksitarski.icf.comparator.standard;

import pl.ksitarski.icf.core.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.accessing.LoadableImage;
import pl.ksitarski.icf.core.image.IcfOptimizedImage;
import pl.ksitarski.icf.core.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.image.IcfImage;
import pl.ksitarski.icf.core.util.BufferedImageUtil;
import pl.ksitarski.icf.core.util.ConversionUtil;
import pl.ksitarski.icf.core.util.IntArgb;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class SimpleScalingComparator extends ImageComparator {
    private final int size;

    public SimpleScalingComparator(int size) {
        super( "Average" + size);
        this.size = size;
    }

    @Override
    public double compare(IcfOptimizedImage image0, IcfOptimizedImage image1) {
        var optimized0 = (SimpleScalingOptimizedImage) image0;
        var optimized1 = (SimpleScalingOptimizedImage) image1;

        int[] opImage0 = optimized0.image;
        int[] opImage1 = optimized1.image;

        int[] rgb0Arr = new int[4];
        int[] rgb1Arr = new int[4];

        double similarity = 0;

        for (int i = 0; i < size * size; i++) {
            int rgb0 = opImage0[i];
            int rgb1 = opImage1[i];

            IntArgb.asArray(rgb0, rgb0Arr);
            IntArgb.asArray(rgb1, rgb1Arr);

            similarity += IntArgb.compare(rgb0Arr, rgb1Arr);

        }

        similarity /= (size * size);

        return similarity;
    }

    @Override
    public IcfOptimizedImage optimizeImage(IcfImage image) {
        return new SimpleScalingOptimizedImage(
                BufferedImageUtil.getHighQualityScaledImage(image.getImage(), size, size),
                image.getParent(),
                this);
    }

    @Override
    public boolean isSerializable() {
        return true;
    }

    @Override
    public IcfOptimizedImage deserialize(byte[] data, ImageInDatabase imageInDatabase) {
        return new SimpleScalingOptimizedImage(data, imageInDatabase, this);
    }


    private static class SimpleScalingOptimizedImage extends IcfOptimizedImage {
        private final int[] image;
        private final LoadableImage parent;
        private final SimpleScalingComparator simpleScalingComparator;

        public SimpleScalingOptimizedImage(byte[] in, LoadableImage parent, SimpleScalingComparator simpleScalingComparator) {
            this.parent = parent;
            image = ConversionUtil.convertToIntArray(in);
            this.simpleScalingComparator = simpleScalingComparator;
        }

        public SimpleScalingOptimizedImage(BufferedImage image, LoadableImage parent, SimpleScalingComparator simpleScalingComparator) {
            this.image = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
            this.parent = parent;
            this.simpleScalingComparator = simpleScalingComparator;
        }

        @Override
        public LoadableImage getParent() {
            return parent;
        }

        @Override
        public SimpleScalingComparator getComparator() {
            return simpleScalingComparator;
        }

        @Override
        public byte[] getSerialized() {
            return ConversionUtil.convertToByteArray(image);
        }
    }
}
