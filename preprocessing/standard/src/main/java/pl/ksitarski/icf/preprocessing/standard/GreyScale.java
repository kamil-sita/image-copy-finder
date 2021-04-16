package pl.ksitarski.icf.preprocessing.standard;

import pl.ksitarski.icf.core.prototype.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.prototype.comparison.comparator.ImagePreprocessor;
import pl.ksitarski.icf.core.prototype.util.IntArgb;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import static pl.ksitarski.icf.core.prototype.util.IntArgb.*;

public class GreyScale extends ImagePreprocessor {
    public GreyScale(ImageComparator imageComparator) {
        super("GreyScale", imageComparator);
    }

    @Override
    public BufferedImage preprocess(BufferedImage bufferedImage) {
        BufferedImage output = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        int[] im1 = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        int[] out = ((DataBufferInt) output.getRaster().getDataBuffer()).getData();

        for (int i = 0; i < im1.length; i++) {
            int[] argb1 = IntArgb.asArray(im1[i]);

            double grey1 = (argb1[R] + argb1[G] + argb1[B]) / 3.0;

            int outGrey = (int) (grey1);
            out[i] = IntArgb.toRgbaInteger(outGrey, outGrey, outGrey, 255);
        }

        return output;
    }
}
