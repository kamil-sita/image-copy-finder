package pl.ksitarski.icf.comparator.standard.util;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import static java.lang.Math.E;
import static java.lang.Math.PI;

public class BufferedImageKernelUtil {


    //todo this and below can be optimized - precalculating kernels, 1D kernel

    /**
     * For best results, size should be around sigma * 6 + 1;
     */
    public static BufferedImage blurGaussian(BufferedImage bufferedImage, double sigma, int size) {
        Kernel kernel = generateGaussianKernel(sigma, size);
        return applyKernel(bufferedImage, kernel);
    }

    /**
     * For best results, size should be around sigma * 6 + 1;
     */
    public static BufferedImage blurGaussian(BufferedImage bufferedImage, double sigma) {
        int size = (int) sigma * 6 + 1;
        Kernel kernel = generateGaussianKernel(sigma, size);
        return applyKernel(bufferedImage, kernel);
    }

    private static BufferedImage applyKernel(BufferedImage input, Kernel kernel) {
        var convolveOp = new ConvolveOp(kernel);
        return convolveOp.filter(input, null);
    }

    private static Kernel generateGaussianKernel(double sigma, int size) {
        //rounding to the nearest 2N+1
        size /= 2;
        int center = size;
        size *= 2;
        size += 1;


        final double const1 = ( E / (2 * PI * Math.pow(sigma, 2)) );
        final double const2 = ( 1 / (2 * Math.pow(sigma, 2)) );


        float[] f = new float[size * size];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {

                double value =  Math.pow(
                        const1,
                        - (((x - center) * (x - center) + (y - center) * (y - center)) * const2)
                );


                int pos = y * size + x;
                f[pos] = (float) value;
            }
        }

        //normalization
        float sum = 0;
        for (int i = 0; i < f.length; i++) {
            sum += f[i];
        }

        for (int i = 0; i < f.length; i++) {
            f[i] = f[i] / sum;
        }

        return new Kernel(size, size, f);
    }
}
