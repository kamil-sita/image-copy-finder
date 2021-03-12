package pl.ksitarski.icf.core.comparison;

import pl.ksitarski.icf.core.accessing.LoadableImage;

import java.util.Objects;

public final class ImageComparingResult {
    private final double equality;
    private final LoadableImage image1;
    private final LoadableImage image2;
    private final ResultType additionalInfo;

    public static ImageComparingResult createSuccessful(
            double equality,
            LoadableImage image1,
            LoadableImage image2
    ) {
        Objects.requireNonNull(image1);
        Objects.requireNonNull(image2);
        return new ImageComparingResult(equality, image1, image2, ResultType.SUCCESSFUL);
    }

    public static ImageComparingResult createFailure(
            LoadableImage image1,
            LoadableImage image2,
            ResultType resultType
    ) {
        Objects.requireNonNull(image1);
        Objects.requireNonNull(image2);
        return new ImageComparingResult(-1, image1, image2, resultType);
    }

    private ImageComparingResult(
            double equality,
            LoadableImage image1,
            LoadableImage image2,
            ResultType resultType
    ) {
        this.equality = equality;
        this.image1 = image1;
        this.image2 = image2;
        this.additionalInfo = resultType;
    }

    public double getEquality() {
        return equality;
    }

    public LoadableImage getImage1() {
        return image1;
    }

    public LoadableImage getImage2() {
        return image2;
    }

    public ResultType getResultType() {
        return additionalInfo;
    }

    @Override
    public String toString() {
        return "ImageComparingResult{" +
                ",\r\n equality=" + equality +
                ",\r\n image1=" + image1 +
                ",\r\n image2=" + image2 +
                ",\r\n resultType='" + additionalInfo + '\'' +
                "\r\n}";
    }

    public enum ResultType {
        SUCCESSFUL,
        FAILURE_EXCEPTION, FAILURE_GENERAL
    }
}
