package pl.ksitarski.icf.core.prototype.imageloader;

import pl.ksitarski.icf.core.prototype.util.BufferedImageUtil;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public class ImageLoaderImpl implements ImageLoader {

    private final ExecutorService executorService;

    public ImageLoaderImpl(int threads) {
        if (threads < 1) {
            throw new IllegalArgumentException("Cannot use less that one thread.");
        }
        executorService = Executors.newFixedThreadPool(threads, r -> new Thread(r, "ImageLoaderImpl-thread"));
        semaphore = new Semaphore(threads);
    }

    private final Semaphore semaphore;

    @Override
    public void loadAsync(Path path, Consumer<Exception> exceptionConsumer, Consumer<BufferedImage> bufferedImageConsumer) {
        semaphore.acquireUninterruptibly();
        executorService.submit(() -> {
            try {
                bufferedImageConsumer.accept(BufferedImageUtil.load(path));
            } catch (Exception e) {
                exceptionConsumer.accept(e);
                bufferedImageConsumer.accept(null);
            } finally {
                semaphore.release();
            }
        });
    }
}
