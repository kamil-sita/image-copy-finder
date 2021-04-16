package pl.ksitarski.icf.preprocessing.standard;

import pl.ksitarski.icf.core.prototype.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.prototype.comparison.comparator.ImagePreprocessor;
import pl.ksitarski.icf.core.prototype.util.IntArgb;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;

import static pl.ksitarski.icf.core.prototype.util.IntArgb.*;

public class Binarize extends ImagePreprocessor {
    public Binarize(ImageComparator imageComparator) {
        super("Binarize2", imageComparator);
    }

    @Override
    public BufferedImage preprocess(BufferedImage bufferedImage) {
        BufferedImage output = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        int[] in = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        int[] out = ((DataBufferInt) output.getRaster().getDataBuffer()).getData();

        List<Integer> valuesList = new ArrayList<>(in.length);

        for (int i = 0; i < in.length; i++) {
            int[] argb1 = IntArgb.asArray(in[i]);
            valuesList.add(argb1[R] + argb1[G] + argb1[B]);
        }

        valuesList.sort(Integer::compare);
        int median = valuesList.get(valuesList.size() / 2);

        for (int i = 0; i < in.length; i++) {
            int[] argb1 = IntArgb.asArray(in[i]);
            int val;
            if (argb1[R] + argb1[G] + argb1[B] < median) {
                val = IntArgb.toRgbaInteger(255, 255, 255, 255);
            } else {
                val = IntArgb.toRgbaInteger(0, 0, 0, 255);
            }
            out[i] = val;
        }

        return output;
    }
}
