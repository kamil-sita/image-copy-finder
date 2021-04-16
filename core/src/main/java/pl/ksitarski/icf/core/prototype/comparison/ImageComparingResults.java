package pl.ksitarski.icf.core.prototype.comparison;

import java.util.List;

public class ImageComparingResults {
    private final List<ImageComparingResult> results;
    private final List<Exception> exceptions;

    public ImageComparingResults(List<ImageComparingResult> results, List<Exception> exceptions) {
        this.results = results;
        this.exceptions = exceptions;
    }

    /**
     * Returns results from the algorithm. This list is guaranteed to be ordered from the most probable, to the least probable
     * element.
     */
    public List<ImageComparingResult> getResults() {
        return results;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }
}
