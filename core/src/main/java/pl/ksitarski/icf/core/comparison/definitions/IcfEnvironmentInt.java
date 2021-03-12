package pl.ksitarski.icf.core.comparison.definitions;

import pl.ksitarski.icf.core.image.IcfImage;
import pl.ksitarski.icf.core.image.IcfOptimizedImages;

import java.util.List;

public interface IcfEnvironmentInt {
    IcfOptimizedImages optimizeImage(IcfImage image);

    default List<Long> loadFilesIdFromDatabase() {
        return loadFilesIdFromDatabase(null);
    }

    List<Long> loadFilesIdFromDatabase(IcfImage optimizationHint);

    LifetimeManager createBatchLoader(List<Long> imagesInDbIds, long loadsBeforeEviction);
}
