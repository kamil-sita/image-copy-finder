package pl.ksitarski.icf.core.comparison.impls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksitarski.icf.core.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.comparison.definitions.Loader;
import pl.ksitarski.icf.core.comparison.definitions.LoaderTarget;
import pl.ksitarski.icf.core.db.IcfCollection;
import pl.ksitarski.icf.core.image.IcfImage;
import pl.ksitarski.icf.core.image.IcfOptimizedImage;
import pl.ksitarski.icf.core.image.IcfOptimizedImages;
import pl.ksitarski.icf.core.imageloader.ImageLoader;
import pl.ksitarski.icf.core.jpa.dao.IcfFileDao;
import pl.ksitarski.icf.core.jpa.dao.IcfFileOptDao;
import pl.ksitarski.icf.core.jpa.orm.IcfFileOptOrm;
import pl.ksitarski.icf.core.jpa.orm.IcfFileOrm;
import pl.ksitarski.icf.core.util.BufferedImageUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OptimizationsLoader implements Loader<IcfOptimizedImages> {
    private final static Logger logger = LoggerFactory.getLogger(OptimizationsLoader.class);
    private final static AtomicInteger idGenerator = new AtomicInteger();

    private final Set<ImageComparator> comparators;
    private final IcfCollection database;

    //comparators in DB, optimizations might be in DB
    private final Set<ImageComparator> serializableComparators;

    //comparators not in DB, optimizations cannot be in DB
    private final Set<ImageComparator> nonSerializableComparators;

    private final int id; //id of this batch loader for logging purposes

    private final ImageLoader imageLoader;

    public OptimizationsLoader(Set<ImageComparator> comparators, IcfCollection database, ImageLoader imageLoader) {
        this.comparators = comparators;
        this.database = database;
        this.imageLoader = imageLoader;


        serializableComparators = new HashSet<>();
        nonSerializableComparators = new HashSet<>();

        for (ImageComparator comparator : comparators) {
            if (comparator.isSerializable()) {
                serializableComparators.add(comparator);
            } else {
                nonSerializableComparators.add(comparator);
            }
        }

        this.id = idGenerator.getAndIncrement();
        logger.info("[{}] Optimizations loader with id {} created", id, id);
    }


    @Override
    public void load(List<Long> idsToLoad, LoaderTarget<IcfOptimizedImages> loaderTarget) {
        Map<Long, Map<ImageComparator, IcfOptimizedImage>> optimizations = new HashMap<>(); //this map contains information about which image has which optimizations loaded
        Map<Long, Set<ImageComparator>> comparatorsNeededPerFile = new HashMap<>(); //which Comparator optimization needs to be run per life


        //loading optimizations already present in database
        List<IcfFileOptOrm> optimizationsPresentInDb = loadOptimizationsFromDatabase(idsToLoad, serializableComparators, optimizations);

        //checking what still needs to be loaded from disk - not present in DB
        Set<Long> filesThatWillNeedToBeLoaded = checkWhatNeedsToBeLoaded(idsToLoad, serializableComparators, nonSerializableComparators, optimizationsPresentInDb, comparatorsNeededPerFile);

        loadNotLoadedFilesIntoDb(optimizations, filesThatWillNeedToBeLoaded, comparatorsNeededPerFile, loaderTarget);

        List<IcfFileOrm> files = new IcfFileDao().getByIds(idsToLoad);

        for (IcfFileOrm file : files) {
            ImageInDatabase imageInDatabase = file.toImageInDatabase();

            long id = file.getId();
            var optimizationMap = optimizations.get(id);
            if (optimizationMap == null) {
                loaderTarget.provide(null, id);
            } else {
                loaderTarget.provide(new IcfOptimizedImages(optimizationMap, imageInDatabase), id);
            }
        }
    }

    private Set<Long> checkWhatNeedsToBeLoaded(List<Long> idsToLoad, Set<ImageComparator> comparatorsInDb, Set<ImageComparator> comparatorsNotInDb, List<IcfFileOptOrm> optimizationsPresentInDb, Map<Long, Set<ImageComparator>> comparatorsNeededPerFile) {
        Set<Long> filesThatWillNeedToBeLoaded = new HashSet<>();
        for (long idToLoad : idsToLoad) {
            for (ImageComparator comparator : comparatorsInDb) {
                if (!containsIdAndComparator(optimizationsPresentInDb, idToLoad, comparator)) {
                    updateToLoadCollections(filesThatWillNeedToBeLoaded, comparatorsNeededPerFile, idToLoad, comparator);
                }
            }
            for (ImageComparator comparator : comparatorsNotInDb) {
                updateToLoadCollections(filesThatWillNeedToBeLoaded, comparatorsNeededPerFile, idToLoad, comparator);
            }
        }
        return filesThatWillNeedToBeLoaded;
    }

    private void loadNotLoadedFilesIntoDb(Map<Long, Map<ImageComparator, IcfOptimizedImage>> optimizations, Set<Long> filesThatWillNeedToBeLoaded, Map<Long, Set<ImageComparator>> comparatorsNeededPerFile, LoaderTarget<IcfOptimizedImages> loaderTarget) {
        if (filesThatWillNeedToBeLoaded.isEmpty()) {
            return;
        }

        List<IcfFileOrm> filesThatNeedToBeLoaded = new IcfFileDao().getByIds(filesThatWillNeedToBeLoaded);


        List<IcfFileOptOrm> optsToSaveInDb = new ArrayList<>();
        List<IcfFileOrm> filesToUpdateInDb = new ArrayList<>();

        for (int i = 0; i < filesThatNeedToBeLoaded.size(); i++) {

        }

        for (IcfFileOrm file : filesThatNeedToBeLoaded) {
            String path = file.getFilePath();
            String fullPath = "";
            if (database.getPath() != null && !database.getPath().isBlank()) {
                fullPath += database.getPath() + File.separatorChar;
            }

            fullPath += path;


            imageLoader.loadAsync(
                    Path.of(fullPath),
                    exception -> {
                        exception.printStackTrace();
                        loaderTarget.reportException(exception);
                    },
                    bufferedImage -> {

                        IcfImage icfImage = new IcfImage(file.toImageInDatabase(), bufferedImage);

                        //settings statistics for files.
                        //since first load does not have any comparators for sure, this path will fire for the first file load
                        if (!file.getStatisticsPresent()) {
                            file.setHue(icfImage.getHue());
                            file.setSaturation(icfImage.getSaturation());
                            file.setValue(icfImage.getValue());

                            file.setR(icfImage.getR());
                            file.setG(icfImage.getG());
                            file.setB(icfImage.getB());

                            file.setWidthHeightRatio(icfImage.getWidthHeightRatio());
                            file.setHeight(icfImage.getHeight());
                            file.setWidth(icfImage.getWidth());

                            file.setStatisticsPresent(true);

                            filesToUpdateInDb.add(file);
                        }


                        for (ImageComparator comparator : comparatorsNeededPerFile.get(file.getId())) {
                            IcfOptimizedImage image = null;
                            try {
                                image = comparator.optimizeImage(icfImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                                loaderTarget.reportException(e);
                                continue;
                            }
                            put(optimizations, file.getId(), comparator, image);
                            if (comparator.isSerializable()) {
                                IcfFileOptOrm optOrm = new IcfFileOptOrm(image.getSerialized(), comparator.canonicalName(), file);
                                optsToSaveInDb.add(optOrm);
                            }
                        }
            });
        }

        new IcfFileDao().update(filesToUpdateInDb);
        new IcfFileOptDao().save(optsToSaveInDb);
    }

    private List<IcfFileOptOrm> loadOptimizationsFromDatabase(List<Long> idsToLoad, Set<ImageComparator> comparatorsInDb, Map<Long, Map<ImageComparator, IcfOptimizedImage>> optimizations) {
        //map of image comparators - we need a way to differentiate comparators via String
        Map<String, ImageComparator> map = new HashMap<>();

        for (ImageComparator comparator : comparators) {
            if (map.put(comparator.canonicalName(), comparator) != null) {
                throw new IllegalStateException("Multiple comparators share the same keys!");
            }
        }

        List<IcfFileOptOrm> optimizationsPresentInDb = new IcfFileOptDao().getWhereIdAndOptimization(database, idsToLoad, comparatorsInDb);

        for (IcfFileOptOrm optOrm : optimizationsPresentInDb) {
            long fileId = optOrm.getIcfFileOrm().getId();
            ImageComparator comparator = map.get(optOrm.getOptimizationType());
            IcfOptimizedImage image = comparator.deserialize(optOrm.getData(), optOrm.getIcfFileOrm().toImageInDatabase());
            put(optimizations, fileId, comparator, image);
        }
        return optimizationsPresentInDb;
    }

    private void updateToLoadCollections(Set<Long> filesThatWillNeedToBeLoaded, Map<Long, Set<ImageComparator>> comparatorsNeededPerFile, long idToLoad, ImageComparator comparator) {
        boolean isNeededToBeLoaded = filesThatWillNeedToBeLoaded.contains(idToLoad);
        if (!isNeededToBeLoaded) {
            filesThatWillNeedToBeLoaded.add(idToLoad);
            Set<ImageComparator> comparators = new HashSet<>();
            comparators.add(comparator);
            comparatorsNeededPerFile.put(idToLoad, comparators);
        } else {
            comparatorsNeededPerFile.get(idToLoad).add(comparator);
        }
    }

    private void put(Map<Long, Map<ImageComparator, IcfOptimizedImage>> optimizations, long id, ImageComparator comparator, IcfOptimizedImage optimizedImage) {
        if (!optimizations.containsKey(id)) {
            optimizations.put(id, new HashMap<>());
        }
        optimizations.get(id).put(comparator, optimizedImage);
    }

    private boolean containsIdAndComparator(Collection<IcfFileOptOrm> optOrms, long fileId, ImageComparator comparator) {
        for (IcfFileOptOrm optOrm : optOrms) {
            if (optOrm.getIcfFileOrm().getId() == fileId && optOrm.getOptimizationType().equals(comparator.canonicalName())) {
                return true;
            }
        }
        return false;
    }

}
