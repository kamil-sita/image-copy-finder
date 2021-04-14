package pl.ksitarski.icf.core.comparison.engine;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksitarski.icf.core.accessing.LoadableImage;
import pl.ksitarski.icf.core.comparison.ImageComparingResults;
import pl.ksitarski.icf.core.comparison.comparator.ComplexImageComparator;
import pl.ksitarski.icf.core.comparison.definitions.ComparisonProgress;
import pl.ksitarski.icf.core.comparison.definitions.Loader;
import pl.ksitarski.icf.core.comparison.impls.IcfEnvironment;
import pl.ksitarski.icf.core.comparison.impls.OptimizationsLoader;
import pl.ksitarski.icf.core.db.IcfCollection;
import pl.ksitarski.icf.core.exc.AlgorithmFailureException;
import pl.ksitarski.icf.core.image.IcfOptimizedImages;
import pl.ksitarski.icf.core.util.MultipleJobExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class IcfEngineDispatcher {

    private final static Logger logger = LoggerFactory.getLogger(IcfEngineDispatcher.class);

    public ImageComparingResults findDuplicatesInLibrary(IcfCollection collection, ComplexImageComparator comparator, IcfSettings settings) {
        return findDuplicatesInLibrary(collection, comparator, settings, getNullComparisonProgress());
    }

    public ImageComparingResults findDuplicatesInLibrary(IcfCollection collection, ComplexImageComparator comparator, IcfSettings settings, ComparisonProgress comparisonProgress) {
        Loader<IcfOptimizedImages> loader = new OptimizationsLoader(comparator.getImageComparators(), collection);

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
        return findDuplicateOfInLibrary(collection, loadableImage, comparator, settings, getNullComparisonProgress());
    }

    /**
     *
     * @param loadableImage
     * @param comparator
     * @param settings
     * @throws AlgorithmFailureException if loadableImage could not be loaded
     * @return
     */
    public ImageComparingResults findDuplicateOfInLibrary(IcfCollection collection, LoadableImage loadableImage, ComplexImageComparator comparator, IcfSettings settings, ComparisonProgress comparisonProgress) {

        Loader<IcfOptimizedImages> loader = new OptimizationsLoader(comparator.getImageComparators(), collection);
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

}
