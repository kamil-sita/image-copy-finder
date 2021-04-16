package pl.ksitarski.icf.core.prototype.comparison.definitions;

public interface LoaderTarget<T> {
    void provide(T t, long id);
    void reportException(Exception e);
}
