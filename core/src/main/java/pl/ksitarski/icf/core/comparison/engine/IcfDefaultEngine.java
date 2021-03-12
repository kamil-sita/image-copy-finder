package pl.ksitarski.icf.core.comparison.engine;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksitarski.icf.core.accessing.LoadableImage;
import pl.ksitarski.icf.core.accessing.NonLoadableImageInDatabase;
import pl.ksitarski.icf.core.comparison.ImageComparingResult;
import pl.ksitarski.icf.core.comparison.ImageComparingResults;
import pl.ksitarski.icf.core.comparison.comparator.ComplexImageComparator;
import pl.ksitarski.icf.core.comparison.definitions.LifetimeManager;
import pl.ksitarski.icf.core.comparison.definitions.ComparisonProgress;
import pl.ksitarski.icf.core.comparison.impls.IcfEnvironment;
import pl.ksitarski.icf.core.exc.AlgorithmFailureException;
import pl.ksitarski.icf.core.image.IcfImage;
import pl.ksitarski.icf.core.image.IcfOptimizedImages;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public final class IcfDefaultEngine {

    private final static Logger logger = LoggerFactory.getLogger(IcfDefaultEngine.class);

    public static ImageComparingResults compareOneWithEach(
            IcfEnvironment environment,
            LoadableImage template,
            ComplexImageComparator comparator,
            ComparisonProgress comparisonProgress,
            Function<Integer, ExecutorService> executorServiceProvider,
            CutoffStrategy cutoffStrategy,
            ExceptionsBehaviour exceptionsBehaviour
    ) {
        List<ImageComparingResult> allResults = Collections.synchronizedList(new ArrayList<>());

        Optional<IcfImage> optionalIcfImage = template.getImage();

        if (optionalIcfImage.isEmpty()) {
            throw new AlgorithmFailureException("Cannot compare image to Library, since image cannot be loaded.");
        }

        IcfImage icfImage = optionalIcfImage.get();

        List<Long> imagesInDb = environment.loadFilesIdFromDatabase(icfImage);

        IcfOptimizedImages optimizedTemplate = environment.optimizeImage(icfImage);

        LifetimeManager<IcfOptimizedImages> optimizationsLifetimeManager = environment.createBatchLoader(imagesInDb, 1);

        ExecutorService executor = executorServiceProvider.apply(imagesInDb.size());

        AtomicInteger imagesProcessed = new AtomicInteger(0);

        for (int i = 0, othersSize = imagesInDb.size(); i < othersSize; i++) {
            Long image = imagesInDb.get(i);
            executor.submit(() -> {
                try {
                    imagesProcessed.incrementAndGet();
                    comparisonProgress.updateProcessing(1.0 * imagesProcessed.get()/ othersSize);
                    IcfOptimizedImages optimizedImages = optimizationsLifetimeManager.get(image);
                    if (optimizedImages == null) {
                        return;
                    }
                    ImageComparingResult results = comparator.compare(
                            optimizedTemplate,
                            optimizedImages
                    );
                    if (cutoffStrategy.accept(results.getEquality(), comparator.getRecommendedCutoff())) {
                        allResults.add(results);
                    }
                } catch (Exception e) {
                    if (exceptionsBehaviour.isExceptionsStackTrace()) {
                        e.printStackTrace();
                    }
                    if (exceptionsBehaviour.isIncludeErrors()) {
                        allResults.add(ImageComparingResult.createFailure(
                                optimizedTemplate.getParent(),
                                new NonLoadableImageInDatabase(image),
                                ImageComparingResult.ResultType.FAILURE_EXCEPTION
                        ));
                    }
                }
            });
        }

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        allResults.sort(Comparator.comparingDouble(ImageComparingResult::getEquality).reversed());

        return new ImageComparingResults(allResults, optimizationsLifetimeManager.getExceptions());
    }


    public static ImageComparingResults compareEachWithEach(
            IcfEnvironment environment,
            ComplexImageComparator comparator,
            ComparisonProgress comparisonProgress,
            Function<Integer, ExecutorService> executorServiceProvider,
            CutoffStrategy cutoffStrategy,
            ExceptionsBehaviour exceptionsBehaviour
    ) {

        List<ImageComparingResult> allResults = Collections.synchronizedList(new ArrayList<>());

        List<Long> imagesInDb = environment.loadFilesIdFromDatabase();

        int jobCount = (imagesInDb.size() * (imagesInDb.size() - 1)) / 2;

        ExecutorService executor = executorServiceProvider.apply(jobCount);

        LifetimeManager<IcfOptimizedImages> optimizationsLifetimeManager = environment.createBatchLoader(imagesInDb, imagesInDb.size() - 1);

        //we do not want to load damaged objects too much
        Set<Integer> damagedImages = Collections.synchronizedSet(new HashSet<>());

        StopWatch watch = new StopWatch();
        watch.start();
        AtomicInteger imagesProcessed = new AtomicInteger(0);

        for (int i = 0; i <= imagesInDb.size(); i++) {
            for (int j = i + 1; j < imagesInDb.size(); j++) {
                int finalI = i;
                int finalJ = j;
                executor.submit(() -> {
                    try {
                        imagesProcessed.incrementAndGet();
                        comparisonProgress.updateProcessing(1.0 * imagesProcessed.get()/ jobCount);

                        if (damagedImages.contains(finalI) || damagedImages.contains(finalJ)) {
                            if (exceptionsBehaviour.isIncludeErrors()) {
                                allResults.add(ImageComparingResult.createFailure(
                                        new NonLoadableImageInDatabase(finalI),
                                        new NonLoadableImageInDatabase(finalJ),
                                        ImageComparingResult.ResultType.FAILURE_EXCEPTION
                                ));
                            }
                            return;
                        }

                        var im1 = optimizationsLifetimeManager.get(imagesInDb.get(finalI));
                        var im2 = optimizationsLifetimeManager.get(imagesInDb.get(finalJ));

                        if (im1 == null) {
                            damagedImages.add(finalI);
                        }
                        if (im2 == null) {
                            damagedImages.add(finalJ);
                        }
                        if (im1 == null || im2 == null) {
                            return;
                        }

                        ImageComparingResult results = comparator.compare(
                                im1,
                                im2
                        );
                        if (cutoffStrategy.accept(results.getEquality(), comparator.getRecommendedCutoff())) {
                            allResults.add(results);
                        }
                    } catch (Exception e) {
                        if (exceptionsBehaviour.isExceptionsStackTrace()) {
                            e.printStackTrace();
                        }
                        if (exceptionsBehaviour.isIncludeErrors()) {
                            allResults.add(ImageComparingResult.createFailure(
                                    new NonLoadableImageInDatabase(finalI),
                                    new NonLoadableImageInDatabase(finalJ),
                                    ImageComparingResult.ResultType.FAILURE_EXCEPTION
                            ));
                        }
                    }
                });
            }
        }

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        watch.stop();
        int fileCount = imagesInDb.size();
        int compCount = fileCount * (fileCount - 1) / 2;
        double avgTime =  1.0 * watch.getTime(TimeUnit.MICROSECONDS) / compCount;
        logger.info("Found pairs in {}ms for {}. Average time {}us", watch.getTime(TimeUnit.MILLISECONDS), comparator.getName(), avgTime);

        allResults.sort(Comparator.comparingDouble(ImageComparingResult::getEquality).reversed());

        return new ImageComparingResults(allResults, optimizationsLifetimeManager.getExceptions());
    }


}
