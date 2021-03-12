package pl.ksitarski.icf.core.image;


import pl.ksitarski.icf.core.accessing.LoadableImage;
import pl.ksitarski.icf.core.jpa.orm.IcfDbOrm;
import pl.ksitarski.icf.core.jpa.orm.IcfFileOrm;
import pl.ksitarski.icf.core.util.BufferedImageUtil;

import java.awt.image.BufferedImage;
import java.util.Objects;

import static pl.ksitarski.icf.core.util.IntArgb.*;

public final class IcfImage {

    private final LoadableImage parent;
    private final BufferedImage image;
    private final int width;
    private final int height;

    private final double hue;
    private final double saturation;
    private final double value;

    private final int r;
    private final int g;
    private final int b;

    //constructor used only if image does not have a parent (yet)
    public IcfImage(BufferedImage image) {
        Objects.requireNonNull(image);
        this.parent = null;
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();

        int[] rgb  = BufferedImageUtil.calculateRgb(image);
        r = rgb[R];
        g = rgb[G];
        b = rgb[B];

        float[] hsv = convertRgbToHsb(rgb);
        hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];
    }

    public IcfImage(LoadableImage parent, BufferedImage image) {
        Objects.requireNonNull(image);
        Objects.requireNonNull(parent);
        this.parent = parent;
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();

        int[] rgb  = BufferedImageUtil.calculateRgb(image);
        r = rgb[R];
        g = rgb[G];
        b = rgb[B];

        float[] hsv = convertRgbToHsb(rgb);
        hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];
    }

    public LoadableImage getParent() {
        return parent;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getWidthHeightRatio() {
        return (1.0 * width) / height;
    }

    public double getHue() {
        return hue;
    }

    public double getSaturation() {
        return saturation;
    }

    public double getValue() {
        return value;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IcfImage icfImage = (IcfImage) o;

        return parent.equals(icfImage.parent);
    }

    @Override
    public int hashCode() {
        return parent.hashCode();
    }
}
