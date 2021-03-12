package pl.ksitarski.icf.core.util;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static pl.ksitarski.icf.core.util.IntArgb.*;

public final class BufferedImageUtil {
    private final static Logger logger = LoggerFactory.getLogger(BufferedImageUtil.class);

    public static BufferedImage load(Path path) throws IOException {
        logger.info("Loading file: {}", path.toString());
        BufferedImage read = ImageIO.read(path.toFile());
        if (read == null) {
            logger.info("Failed to load file: {}", path.toString());
            throw new IOException("Unreadable file!: " + path);
        }
        return copyWithType(read, BufferedImage.TYPE_INT_ARGB);
    }

    public static BufferedImage getHighQualityScaledImage(BufferedImage source, double width, double height) {
        Objects.requireNonNull(source);
        return Scalr.resize(source, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, (int) width, (int) height, (BufferedImageOp) null);
    }

    public static BufferedImage getHighQualityScaledImage(BufferedImage source, double scale) {
        Objects.requireNonNull(source);
        return Scalr.resize(source, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, (int) (source.getWidth() * scale), (int) (source.getHeight() * scale), (BufferedImageOp) null);
    }

    public static int[] calculateRgb(BufferedImage image) {
        int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        double rVal = 0;
        double gVal = 0;
        double bVal = 0;

        for (int i = 0; i < data.length; i++) {
            int rgbInt = data[i];
            int[] rgbArray = IntArgb.asArray(rgbInt);
            rVal += rgbArray[R];
            gVal += rgbArray[G];
            bVal += rgbArray[B];
        }

        int rInt = (int) (rVal/data.length);
        int gInt = (int) (gVal/data.length);
        int bInt = (int) (bVal/data.length);

        return IntArgb.pack(255, rInt, gInt, bInt);
    }

    public static BufferedImage copy(BufferedImage bufferedImage) {
        return copyWithType(bufferedImage, bufferedImage.getType());
    }

    private static BufferedImage copyWithType(BufferedImage bufferedImage, int type) {
        BufferedImage copy = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), type);
        var graphics = (Graphics2D) copy.getGraphics();
        graphics.drawImage(bufferedImage, 0, 0, null);
        graphics.dispose();
        return copy;
    }
}
