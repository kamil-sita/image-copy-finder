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
