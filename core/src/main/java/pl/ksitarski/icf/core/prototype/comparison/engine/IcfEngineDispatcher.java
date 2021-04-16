package pl.ksitarski.icf.core.prototype.comparison.engine;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksitarski.icf.core.prototype.accessing.LoadableImage;
import pl.ksitarski.icf.core.prototype.comparison.ImageComparingResults;
import pl.ksitarski.icf.core.prototype.comparison.comparator.ComplexImageComparator;
import pl.ksitarski.icf.core.prototype.comparison.definitions.ComparisonProgress;
import pl.ksitarski.icf.core.prototype.comparison.definitions.Loader;
import pl.ksitarski.icf.core.prototype.comparison.impls.IcfEnvironment;
import pl.ksitarski.icf.core.prototype.comparison.impls.OptimizationsLoader;
import pl.ksitarski.icf.core.prototype.db.IcfCollection;
import pl.ksitarski.icf.core.prototype.exc.AlgorithmFailureException;
import pl.ksitarski.icf.core.prototype.image.IcfOptimizedImages;
import pl.ksitarski.icf.core.prototype.imageloader.ImageLoader;
import pl.ksitarski.icf.core.prototype.imageloader.ImageLoaderImpl;
import pl.ksitarski.icf.core.prototype.util.MultipleJobExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class IcfEngineDispatcher {

    private final static Logger logger = LoggerFactory.getLogger(IcfEngineDispatcher.class);

    public ImageComparingResults findDuplicatesInLibrary(IcfCollection collection, ComplexImageComparator comparator, IcfSettings settings) {
        return findDuplicatesInLibrary(collection, comparator, settings, getNullComparisonProgress(), getImageLoader());
    }

    public ImageComparingResults findDuplicatesInLibrary(IcfCollection collection, ComplexImageComparator comparator, IcfSettings settings, ComparisonProgress comparisonProgress, ImageLoader imageLoader) {
        Loader<IcfOptimizedImages> loader = new OptimizationsLoader(comparator.getImageComparators(), collection, imageLoader);

        IcfEnvironment environment = new IcfEnvironment(
                comparator.getImageComparators(),
                collection,
                settings.isAggressiveOptimizations(),
                comparisonProgress::updateLoading,
                settings.getBatchSize(),
                loader);

        StopWatch watch = new StopWatch();
        watch.start();

        ImageComparingResults results = IcfDefaultEngine.compareEachWithEach(
                environment,
                comparator,
                comparisonProgress,
                new Function<Integer, ExecutorService>() {
                    @Override
                    public ExecutorService apply(Integer jobCount) {
                        return new MultipleJobExecutor(jobCount, settings.getThreadCount(), settings.getJobBatchSize(), settings.isJobsSingleThreaded());
                    }
                },
                settings.getCutoffStrategy(),
                settings.toExceptionsBehaviour()
        );
        watch.stop();
        logger.info("found copies in: " + watch.getTime(TimeUnit.MILLISECONDS) + "ms");
        return results;
    }



    public ImageComparingResults findDuplicateOfInLibrary(IcfCollection collection, LoadableImage loadableImage, ComplexImageComparator comparator, IcfSettings settings) {
        return findDuplicateOfInLibrary(collection, loadableImage, comparator, settings, getNullComparisonProgress(), getImageLoader());
    }

    /**
     *
     * @param loadableImage
     * @param comparator
     * @param settings
     * @throws AlgorithmFailureException if loadableImage could not be loaded
     * @return
     */
    public ImageComparingResults findDuplicateOfInLibrary(IcfCollection collection, LoadableImage loadableImage, ComplexImageComparator comparator, IcfSettings settings, ComparisonProgress comparisonProgress, ImageLoader imageLoader) {

        Loader<IcfOptimizedImages> loader = new OptimizationsLoader(comparator.getImageComparators(), collection, imageLoader);
        IcfEnvironment environment = new IcfEnvironment(
                comparator.getImageComparators(),
                collection,
                settings.isAggressiveOptimizations(),
                comparisonProgress::updateLoading,
                settings.getBatchSize(), loader);

        StopWatch watch = new StopWatch();
        watch.start();

        ImageComparingResults results = IcfDefaultEngine.compareOneWithEach(
                environment,
                loadableImage,
                comparator,
                comparisonProgress,
                new Function<Integer, ExecutorService>() {
                    @Override
                    public ExecutorService apply(Integer aLong) {
                        return new MultipleJobExecutor(aLong, settings.getThreadCount(), settings.getJobBatchSize(), settings.isJobsSingleThreaded());
                    }
                },
                settings.getCutoffStrategy(),
                settings.toExceptionsBehaviour()
        );
        watch.stop();
        logger.info("found copies in: " + watch.getTime(TimeUnit.MILLISECONDS) + "ms");
        return results;
    }


    public static ComparisonProgress getNullComparisonProgress() {
        return new ComparisonProgress() {
            @Override
            public void updateLoading(double i) {

            }

            @Override
            public void updateProcessing(double i) {

            }
        };
    }

    private static ImageLoader imageLoader;

    public synchronized static ImageLoader getImageLoader() {
        if (imageLoader != null) {
            return imageLoader;
        }
        imageLoader = new ImageLoaderImpl(Runtime.getRuntime().availableProcessors());
        return imageLoader;
    }

}
