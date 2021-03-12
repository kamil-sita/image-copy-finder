package pl.ksitarski.icf.core.comparison.definitions;

public interface LoaderTarget<T> {
    void provide(T t, long id);
    void reportException(Exception e);
}
