package pl.ksitarski.icf.preprocessing.standard;

import pl.ksitarski.icf.core.prototype.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.prototype.comparison.comparator.ImagePreprocessor;
import pl.ksitarski.icf.core.prototype.util.IntArgb;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferInt;
import java.awt.image.Kernel;

import static pl.ksitarski.icf.core.prototype.util.IntArgb.*;

public class EdgeDetection extends ImagePreprocessor {
    public EdgeDetection(ImageComparator imageComparator) {
        super("EdgeDetection", imageComparator);
    }

    @Override
    public BufferedImage preprocess(BufferedImage bufferedImage) {
        final float[] sobelHorizontal = new float[]{
                1, 0, -1,
                2, 0, -2,
                1, 0, -1
        };
        final float[] sobelVertical = new float[]{
                1, 2, 1,
                0, 0, 0,
                -1, -2, -1
        };

        Kernel sobelHorizontalKernel = new Kernel(3, 3, sobelHorizontal);
        Kernel sobelVerticalKernel = new Kernel(3, 3, sobelVertical);

        ConvolveOp sobelHorizontalConvolve = new ConvolveOp(sobelHorizontalKernel);
        ConvolveOp sobelVerticalConvolve = new ConvolveOp(sobelVerticalKernel);

        BufferedImage bufferedImage1 = sobelHorizontalConvolve.filter(bufferedImage, null);
        BufferedImage bufferedImage2 = sobelVerticalConvolve.filter(bufferedImage, null);

        int[] im1 = ((DataBufferInt) bufferedImage1.getRaster().getDataBuffer()).getData();
        int[] im2 = ((DataBufferInt) bufferedImage2.getRaster().getDataBuffer()).getData();

        BufferedImage output = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        int[] out = ((DataBufferInt) output.getRaster().getDataBuffer()).getData();

        for (int i = 0; i < im1.length; i++) {
            int[] argb1 = IntArgb.asArray(im1[i]);
            int[] argb2 = IntArgb.asArray(im2[i]);

            double grey1 = (argb1[R] + argb1[G] + argb1[B]) / 3.0;
            double grey2 = (argb2[R] + argb2[G] + argb2[B]) / 3.0;

            int outGrey = (int) (Math.sqrt(grey1 * grey1 + grey2 * grey2));
            out[i] = IntArgb.toRgbaInteger(outGrey, outGrey, outGrey, 255);
        }

        return output;
    }
}
