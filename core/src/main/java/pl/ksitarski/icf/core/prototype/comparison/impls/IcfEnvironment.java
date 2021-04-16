package pl.ksitarski.icf.core.prototype.comparison.impls;

import pl.ksitarski.icf.core.prototype.comparison.definitions.LifetimeManager;
import pl.ksitarski.icf.core.prototype.comparison.definitions.IcfEnvironmentInt;
import pl.ksitarski.icf.core.prototype.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.prototype.comparison.definitions.Loader;
import pl.ksitarski.icf.core.prototype.db.DatabaseConnection;
import pl.ksitarski.icf.core.prototype.db.IcfCollection;
import pl.ksitarski.icf.core.prototype.image.IcfImage;
import pl.ksitarski.icf.core.prototype.image.IcfOptimizedImage;
import pl.ksitarski.icf.core.prototype.image.IcfOptimizedImages;

import java.util.*;
import java.util.function.Consumer;

public class IcfEnvironment implements IcfEnvironmentInt {

    private final Set<ImageComparator> comparators;
    private final IcfCollection database;
    private final boolean isAggressiveOptimization;
    private final Consumer<Double> loadingProgress;
    private final int batchSize;
    private final Loader<IcfOptimizedImages> loader;

    public IcfEnvironment(Set<ImageComparator> comparators, IcfCollection database, boolean isAggressiveOptimization, Consumer<Double> loadingProgress, int batchSize, Loader<IcfOptimizedImages> loader) {
        this.comparators = comparators;
        this.database = database;
        this.isAggressiveOptimization = isAggressiveOptimization;
        this.loadingProgress = loadingProgress;
        this.batchSize = batchSize;
        this.loader = loader;
    }

    @Override
    public IcfOptimizedImages optimizeImage(IcfImage image) {

        Map<ImageComparator, IcfOptimizedImage> icfOptimizedImageMap = new HashMap<>();

        for (ImageComparator comparator : comparators) {
            icfOptimizedImageMap.put(comparator, comparator.optimizeImage(image));
        }

        return new IcfOptimizedImages(icfOptimizedImageMap, image.getParent());
    }

    @Override
    public List<Long> loadFilesIdFromDatabase() {
        return loadFilesIdFromDatabase(null);
    }

    private static final int RGB_RANGE = 50;

    /**
     *
     * @param optimizationHint - hint for aggressive optimizations
     * @return
     */
    @Override
    public List<Long> loadFilesIdFromDatabase(IcfImage optimizationHint) {
        if (isAggressiveOptimization && optimizationHint != null) {
            int minR = optimizationHint.getR() - RGB_RANGE;
            int maxR = optimizationHint.getR() + RGB_RANGE;
            int minG = optimizationHint.getG() - RGB_RANGE;
            int maxG = optimizationHint.getG() + RGB_RANGE;
            int minB = optimizationHint.getB() - RGB_RANGE;
            int maxB = optimizationHint.getB() + RGB_RANGE;
            return DatabaseConnection.getAllImagesFromDatabase(database, minR, maxR, minG, maxG, minB, maxB);
        }
        return DatabaseConnection.getAllImagesFromDatabase(database);
    }

    @Override
    public LifetimeManager<IcfOptimizedImages> createBatchLoader(List<Long> imagesInDbIds, long loadsBeforeEviction) {
        return new OptimizationsLifetimeManager(loader, imagesInDbIds, loadsBeforeEviction, loadingProgress, batchSize);
    }
}
