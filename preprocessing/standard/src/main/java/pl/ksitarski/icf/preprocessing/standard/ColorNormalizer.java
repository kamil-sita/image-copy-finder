package pl.ksitarski.icf.preprocessing.standard;

import pl.ksitarski.icf.core.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.comparison.comparator.ImagePreprocessor;
import pl.ksitarski.icf.core.util.IntArgb;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import static pl.ksitarski.icf.core.util.IntArgb.*;

public class ColorNormalizer extends ImagePreprocessor {
    public ColorNormalizer(ImageComparator imageComparator) {
        super("ColorNormalizer", imageComparator);
    }

    @Override
    public BufferedImage preprocess(BufferedImage bufferedImage) {
        BufferedImage bufferedImage1 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        int[] image = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        int[] outImage = ((DataBufferInt) bufferedImage1.getRaster().getDataBuffer()).getData();
        int rMin = 255;
        int gMin = 255;
        int bMin = 255;
        int rMax = 0;
        int gMax = 0;
        int bMax = 0;

        int[] rgbArr = new int[4];

        for (int i : image) {
            IntArgb.asArray(i, rgbArr);
            int r = rgbArr[R];
            int g = rgbArr[G];
            int b = rgbArr[B];
            if (r < rMin)
                rMin = r;
            if (r > rMax)
                rMax = r;
            if (g < gMin)
                gMin = g;
            if (g > gMax)
                gMax = g;
            if (b < bMin)
                bMin = b;
            if (b > bMax)
                bMax = b;
        }

        //todo no point in scaling if Max == 255 and Min = 0

        double rScale = 255.0 / (rMax - rMin);
        double gScale = 255.0 / (gMax - gMin);
        double bScale = 255.0 / (bMax - bMin);

        for (int i = 0; i < image.length; i++) {
            IntArgb.asArray(image[i], rgbArr);
            int r = rgbArr[R];
            int g = rgbArr[G];
            int b = rgbArr[B];

            double rn = (r - rMin) * rScale;
            double gn = (g - gMin) * gScale;
            double bn = (b - bMin) * bScale;

            int rf = (int) Math.min(255, rn);
            int gf = (int) Math.min(255, gn);
            int bf = (int) Math.min(255, bn);

            int out = IntArgb.toRgbaInteger(rf, gf, bf, rgbArr[A]);
            outImage[i] = out;
        }

        return bufferedImage1;
    }
}
