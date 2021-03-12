package pl.ksitarski.icf.comparator.standard;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import pl.ksitarski.icf.core.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.accessing.LoadableImage;
import pl.ksitarski.icf.core.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.image.IcfImage;
import pl.ksitarski.icf.core.image.IcfOptimizedImage;
import pl.ksitarski.icf.core.util.ConversionUtil;
import pl.ksitarski.icf.core.util.IntArgb;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import static pl.ksitarski.icf.core.util.IntArgb.*;

public class YiqComparatorMe extends ImageComparator {
    public YiqComparatorMe() {
        super("YIQ_COMPARATOR_ME");
    }

    @Override
    public double compare(IcfOptimizedImage image0, IcfOptimizedImage image1) {
        final float yMin = 0;
        final float yMax = 1;
        final float iMin = -0.5957f;
        final float iMax = 0.5957f;
        final float qMin = -0.5226f;
        final float qMax = 0.5226f;

        final double adjustment =
                Math.abs(yMax - yMin) + //max difference for Y
                Math.abs(iMax - iMin) + //max difference for I
                Math.abs(qMax - qMin);  //max difference for Q



        YiqOptimizedImage yiq0 = (YiqOptimizedImage) image0;
        YiqOptimizedImage yiq1 = (YiqOptimizedImage) image1;

        float y0 = yiq0.y;
        float i0 = yiq0.i;
        float q0 = yiq0.q;
        float y1 = yiq1.y;
        float i1 = yiq1.i;
        float q1 = yiq1.q;

        double yError = Math.abs(y0 - y1);
        double iError = Math.abs(i0 - i1);
        double qError = Math.abs(q0 - q1);

        double error = (yError + iError + qError);

        return Math.pow(error/adjustment, 1/16.0);
    }

    @Override
    public IcfOptimizedImage optimizeImage(IcfImage image) {
        BufferedImage bufferedImage = image.getImage();
        int[] intImage = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

        DescriptiveStatistics dsR = new DescriptiveStatistics();
        DescriptiveStatistics dsG = new DescriptiveStatistics();
        DescriptiveStatistics dsB = new DescriptiveStatistics();

        int[] argb = new int[4];

        for (int i = 0; i < intImage.length; i++) {
            IntArgb.asArray(intImage[i], argb);
            dsR.addValue(argb[R]);
            dsG.addValue(argb[G]);
            dsB.addValue(argb[B]);
        }

        double avgR = (dsR.getMean() / 255.0);
        double avgG = (dsG.getMean() / 255.0);
        double avgB = (dsB.getMean() / 255.0);

        float y = (float) (0.299 * avgR + 0.587 * avgG + 0.114 * avgB);
        float i = (float) (0.74 * (avgR - y) - 0.27 * (avgB - y));
        float q = (float) (0.48 * (avgR - y) + 0.41 * (avgB - y));

        return new YiqOptimizedImage(y, i, q, image.getParent());
    }

    @Override
    public boolean isSerializable() {
        return true;
    }

    @Override
    public IcfOptimizedImage deserialize(byte[] data, ImageInDatabase imageInDatabase) {
        float[] dData = ConversionUtil.toFloatArray(data);
        return new YiqOptimizedImage(dData[0], dData[1], dData[2], imageInDatabase);
    }

    private class YiqOptimizedImage extends IcfOptimizedImage {
        private final float y;
        private final float i;
        private final float q;
        private final LoadableImage parent;

        private YiqOptimizedImage(float y, float i, float q, LoadableImage parent) {
            this.y = y;
            this.i = i;
            this.q = q;
            this.parent = parent;
        }

        @Override
        public LoadableImage getParent() {
            return parent;
        }

        @Override
        public ImageComparator getComparator() {
            return YiqComparatorMe.this;
        }

        @Override
        public byte[] getSerialized() {
            return ConversionUtil.toByteArray(new float[]{y, i, q});
        }
    }
}
