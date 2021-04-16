package pl.ksitarski.icf.comparator.jimagehash.util;

import com.github.kilianB.hash.Hash;
import com.github.kilianB.hashAlgorithms.HashingAlgorithm;
import pl.ksitarski.icf.core.prototype.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.prototype.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.prototype.image.IcfImage;
import pl.ksitarski.icf.core.prototype.image.IcfOptimizedImage;

import java.math.BigInteger;

public abstract class JihBase extends ImageComparator {

    public JihBase(String algorithmName, HashingAlgorithm hashingAlgorithm) {
        super(algorithmName);
        this.algorithmName = algorithmName;
        this.hashingAlgorithm = hashingAlgorithm;
    }

    private final String algorithmName;
    private final HashingAlgorithm hashingAlgorithm;

    @Override
    public double compare(IcfOptimizedImage image0, IcfOptimizedImage image1) {
        JihOptimizedImage im0 = (JihOptimizedImage) image0;
        JihOptimizedImage im1 = (JihOptimizedImage) image1;

        return 1 - im0.getImageHash().normalizedHammingDistanceFast(im1.getImageHash());
    }

    @Override
    public IcfOptimizedImage optimizeImage(IcfImage image) {
        return new JihOptimizedImage(
                image.getParent(),
                this,
                hashingAlgorithm.hash(image.getImage())
        );
    }

    @Override
    public boolean isSerializable() {
        return true;
    }

    @Override
    public IcfOptimizedImage deserialize(byte[] data, ImageInDatabase imageInDatabase) {
        byte[] dataWithSign = new byte[data.length + 1];
        System.arraycopy(data, 0, dataWithSign, 1, data.length);
        BigInteger hashValue = new BigInteger(dataWithSign);
        return new JihOptimizedImage(
                imageInDatabase,
                this,
                new Hash(
                        hashValue,
                        hashingAlgorithm.getKeyResolution(),
                        hashingAlgorithm.algorithmId()
                )
        );
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public HashingAlgorithm getHashingAlgorithm() {
        return hashingAlgorithm;
    }
}
