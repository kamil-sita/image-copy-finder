package pl.ksitarski.icf.core.comparison.definitions;

import java.util.List;

public interface LifetimeManager<T> {
    T get(long id);
    List<Exception> getExceptions();
}
