package pl.ksitarski.icf.core.comparison.engine;

@FunctionalInterface
public interface CutoffStrategy {
    boolean accept(double value, double suggested);
}
