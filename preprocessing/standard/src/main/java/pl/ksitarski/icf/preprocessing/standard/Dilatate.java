package pl.ksitarski.icf.preprocessing.standard;

import pl.ksitarski.icf.core.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.comparison.comparator.ImagePreprocessor;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * This operator assumes that this image is in greyscale and binarized
 */
public class Dilatate extends ImagePreprocessor {
    public Dilatate(ImageComparator imageComparator) {
        super("Dilatate", imageComparator);
    }

    @Override
    public BufferedImage preprocess(BufferedImage bufferedImage) {
        float[] dilatateKernel = new float[]{
                1, 1, 1,
                1, 1, 1,
                1, 1, 1
        };

        Kernel kernel = new Kernel(3 ,3, dilatateKernel);

        ConvolveOp convolveOp = new ConvolveOp(kernel);
        return convolveOp.filter(bufferedImage, null);
    }
}
