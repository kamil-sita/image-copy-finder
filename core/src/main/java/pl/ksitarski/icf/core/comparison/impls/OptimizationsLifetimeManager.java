package pl.ksitarski.icf.core.comparison.impls;


import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksitarski.icf.core.comparison.definitions.LifetimeManager;
import pl.ksitarski.icf.core.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.comparison.definitions.Loader;
import pl.ksitarski.icf.core.comparison.definitions.LoaderTarget;
import pl.ksitarski.icf.core.db.IcfCollection;
import pl.ksitarski.icf.core.image.IcfOptimizedImages;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class OptimizationsLifetimeManager implements LifetimeManager<IcfOptimizedImages>, LoaderTarget<IcfOptimizedImages> {
    private final static Logger logger = LoggerFactory.getLogger(OptimizationsLifetimeManager.class);
    private final static AtomicInteger idGenerator = new AtomicInteger();
    private final int id; //id of this batch loader for logging purposes
    private final Loader<IcfOptimizedImages> loader;


    private final Consumer<Double> loadingProgress;

    //On the first load of image, it should be present in icfOptimizedImages array, and it's accessCount in accessCount
    //map should be set to loadsBeforeEvictions.

    //If image has not been loaded, icfOptimizedImages entry should be null, and so should be the data in access count


    //BatchLoader config
    private final List<Long> ids;
    private final long loadsBeforeEvictions;
    private final int batchSize;

    //
    private final IcfOptimizedImages[] icfOptimizedImages; //batch loaded optimized images
    private final Map<Long, Long> accessCount; //number of times images can be accessed before eviction. if empty, has not been loaded yet
    private final List<Exception> exceptions = new ArrayList<>();
    private final boolean[] failedToLoad;

    //lookup
    private final Map<Long, Integer> indexOfId;

    //statistics
    private final AtomicInteger imagesLoaded = new AtomicInteger(0);

    public OptimizationsLifetimeManager(Loader<IcfOptimizedImages> loader, List<Long> ids, long loadsBeforeEvictions, Consumer<Double> loadingProgress, int batchSize) {
        this.loader = loader;
        this.loadingProgress = loadingProgress;
        this.batchSize = batchSize;
        this.id = idGenerator.getAndIncrement();
        logger.info("[{}] Batch loader with id {} created", id, id);

        this.ids = ids;
        this.icfOptimizedImages = new IcfOptimizedImages[ids.size()];
        this.failedToLoad = new boolean[ids.size()];
        this.loadsBeforeEvictions = loadsBeforeEvictions;
        indexOfId = new HashMap<>(ids.size() * 3, 1f);
        accessCount = new HashMap<>(ids.size() * 3, 1f);

        for (int i = 0; i < ids.size(); i++) {
            indexOfId.put(ids.get(i), i);
        }
    }

    @Override
    public synchronized IcfOptimizedImages get(long id) {
        if (isEvicted(id)) {
            throw new IllegalStateException("loading id which was evicted: " + id);
        }
        int index = indexOfId.get(id);

        if (icfOptimizedImages[index] == null) {
            if (failedToLoad[index]) {
                return loadExists(id, index);
            }

            StopWatch watch = new StopWatch();
            watch.start();

            loader.load(getIdToLoadInBatch(index), this);

            watch.stop();
            logger.info("[{}] batch loaded in: [{}] ms", id, watch.getTime(TimeUnit.MILLISECONDS));
        }
        return loadExists(id, index);
    }

    private List<Long> getIdToLoadInBatch(int index) {
        int bucket = index / batchSize;

        int min = bucket * batchSize;
        int max = (bucket + 1) * batchSize;

        logger.info("[{}] For index {} loading batch: ({}, {})", id, index, min, max);

        max = Math.min(max, ids.size());

        List<Long> idsToLoad = new ArrayList<>();
        for (int i = min; i < max; i++) {
            idsToLoad.add(
                    ids.get(i)
            );
        }

        return idsToLoad;
    }

    @Override
    public void provide(IcfOptimizedImages images, long id) {
        int index = indexOfId.get(id);
        if (images == null) {
            failedToLoad[index] = true;
            imagesLoaded.incrementAndGet();
            accessCount.put(id, loadsBeforeEvictions);
            loadingProgress.accept(imagesLoaded.get() / ids.size() * 1.0);
        } else {
            icfOptimizedImages[index] = images;
            imagesLoaded.incrementAndGet();
            accessCount.put(id, loadsBeforeEvictions);
            loadingProgress.accept(imagesLoaded.get() / ids.size() * 1.0);
        }
    }

    @Override
    public void reportException(Exception e) {
        exceptions.add(e);
    }

    private boolean isEvicted(long id) {
        if (!wasEverLoaded(id)) {
            return false;
        }
        return accessCount.get(id) == 0;
    }

    private boolean wasEverLoaded(long id) {
        return accessCount.containsKey(id);
    }

    private IcfOptimizedImages loadExists(long id, int index) {
        IcfOptimizedImages images = icfOptimizedImages[index];
        long accessesLeft = accessCount.get(id);
        accessesLeft = accessesLeft - 1;
        if (accessesLeft == 0) {
            icfOptimizedImages[index] = null;
        }
        accessCount.put(id, accessesLeft);
        return images;
    }


    @Override
    public List<Exception> getExceptions() {
        return exceptions;
    }
}