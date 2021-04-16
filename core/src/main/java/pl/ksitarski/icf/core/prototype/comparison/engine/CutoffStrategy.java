package pl.ksitarski.icf.core.prototype.comparison.engine;

@FunctionalInterface
public interface CutoffStrategy {
    boolean accept(double value, double suggested);
}
