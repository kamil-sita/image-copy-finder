package pl.ksitarski.icf.gui;

import pl.ksitarski.icf.core.accessing.LoadableImage;
import pl.ksitarski.icf.core.comparison.ImageComparingResult;

public class ComparisonListElement {
    private LoadableImage loadableImage0;
    private LoadableImage loadableImage1;
    private double similarity;

    public ComparisonListElement(ImageComparingResult imageComparingResult) {
        this.loadableImage0 = imageComparingResult.getImage1();
        this.loadableImage1 = imageComparingResult.getImage2();
        this.similarity = imageComparingResult.getEquality();
    }

    public LoadableImage getLoadableImage0() {
        return loadableImage0;
    }

    public LoadableImage getLoadableImage1() {
        return loadableImage1;
    }

    public double getSimilarity() {
        return similarity;
    }

    @Override
    public String toString() {
        return "[" + (int) (1000 * similarity) / 1000.0 + "]" + getLoadableImage0().getName() + ", " + getLoadableImage1().getName();
    }
}
