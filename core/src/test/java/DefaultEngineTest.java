import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;
import pl.ksitarski.icf.core.comparison.ImageComparingResults;
import pl.ksitarski.icf.core.comparison.definitions.Loader;
import pl.ksitarski.icf.core.comparison.engine.IcfDefaultEngine;
import pl.ksitarski.icf.core.comparison.engine.IcfEngineDispatcher;
import pl.ksitarski.icf.core.comparison.engine.IcfSettings;
import pl.ksitarski.icf.core.comparison.impls.IcfEnvironment;
import pl.ksitarski.icf.core.comparison.impls.OptimizationsLoader;
import pl.ksitarski.icf.core.image.IcfOptimizedImages;
import pl.ksitarski.icf.core.util.MultipleJobExecutor;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class DefaultEngineTest {

    public void runComparison() {

    }

/*
    @Test
    public void test() {

        var comparisonProgress = IcfEngineDispatcher.getNullComparisonProgress();
        var settings = new IcfSettings();

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
    }*/

}
