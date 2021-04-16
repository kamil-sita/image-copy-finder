package pl.ksitarski.icf.core.prototype.accessing;

import pl.ksitarski.icf.core.prototype.image.IcfImage;
import pl.ksitarski.icf.core.prototype.util.BufferedImageUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class LoadableImageFile extends LoadableImage {

    private final Path path;

    public LoadableImageFile(Path path) {
        Objects.requireNonNull(path);
        this.path = path;
    }

    @Override
    protected IcfImage loadExpensive() throws IOException {
        return new IcfImage(this,
                BufferedImageUtil.load(path)
        );
    }

    @Override
    public String readableInfo() {
        return "path: " + path.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoadableImageFile that = (LoadableImageFile) o;

        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }

    public Path getPath() {
        return path;
    }
}
