package pl.ksitarski.icf.core.comparison.comparator;

import pl.ksitarski.icf.core.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.accessing.LoadableImage;
import pl.ksitarski.icf.core.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.image.IcfImage;
import pl.ksitarski.icf.core.image.IcfOptimizedImage;

import java.awt.image.BufferedImage;
import java.util.Objects;

public abstract class ImagePreprocessor extends ImageComparator {

    private final ImageComparator imageComparator;

    protected ImagePreprocessor(String preprocessingName, ImageComparator imageComparator) {
        super(imageComparator.canonicalName() + "+" + preprocessingName);
        Objects.requireNonNull(imageComparator);
        this.imageComparator = imageComparator;
    }

    @Override
    public double compare(IcfOptimizedImage image0, IcfOptimizedImage image1) {
        return imageComparator.compare(
                ((ImagePreprocessorOptimizedImage) (image0)).getInternalIcfOptimizedImage(),
                ((ImagePreprocessorOptimizedImage) (image1)).getInternalIcfOptimizedImage()
        );
    }

    @Override
    public IcfOptimizedImage optimizeImage(IcfImage image) {
        BufferedImage bi = preprocess(image.getImage());
        IcfImage icfImage = new IcfImage(image.getParent(), bi);
        IcfOptimizedImage optimizedImage = imageComparator.optimizeImage(icfImage);
        return new ImagePreprocessorOptimizedImage(
                optimizedImage
        );
    }

    @Override
    public boolean isSerializable() {
        return imageComparator.isSerializable();
    }

    @Override
    public IcfOptimizedImage deserialize(byte[] data, ImageInDatabase imageInDatabase) {
        return new ImagePreprocessorOptimizedImage(
                imageComparator.deserialize(data, imageInDatabase)
        );
    }

    public abstract BufferedImage preprocess(BufferedImage bufferedImage);

    private class ImagePreprocessorOptimizedImage extends IcfOptimizedImage {
        private final IcfOptimizedImage internalIcfOptimizedImage;

        private ImagePreprocessorOptimizedImage(IcfOptimizedImage internalIcfOptimizedImage) {
            this.internalIcfOptimizedImage = internalIcfOptimizedImage;
        }

        @Override
        public LoadableImage getParent() {
            return internalIcfOptimizedImage.getParent();
        }

        @Override
        public ImageComparator getComparator() {
            return ImagePreprocessor.this;
        }

        @Override
        public byte[] getSerialized() {
            return internalIcfOptimizedImage.getSerialized();
        }

        public IcfOptimizedImage getInternalIcfOptimizedImage() {
            return internalIcfOptimizedImage;
        }
    }

}
