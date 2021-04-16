package pl.ksitarski.icf.core.prototype.imageloader;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.function.Consumer;

public interface ImageLoader {
    void loadAsync(Path path, Consumer<Exception> exceptionConsumer, Consumer<BufferedImage> bufferedImageConsumer);
}
