package pl.ksitarski.icf.core.prototype.comparison.definitions;

import java.util.List;

public interface Loader<T> {
    void load(List<Long> idsToLoad, LoaderTarget<T> loaderTarget);
}
