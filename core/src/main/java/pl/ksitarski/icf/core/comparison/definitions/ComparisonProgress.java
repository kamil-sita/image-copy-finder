package pl.ksitarski.icf.core.comparison.definitions;

public interface ComparisonProgress {
    void updateLoading(double i);
    void updateProcessing(double i);
}
