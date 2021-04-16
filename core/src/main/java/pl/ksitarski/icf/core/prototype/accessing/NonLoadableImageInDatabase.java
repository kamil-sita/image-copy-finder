package pl.ksitarski.icf.core.prototype.accessing;

import pl.ksitarski.icf.core.prototype.image.IcfImage;

public class NonLoadableImageInDatabase extends LoadableImage implements DatabasePointer {
    private final long fileId;

    public NonLoadableImageInDatabase(long fileId) {
        this.fileId = fileId;
    }

    @Override
    protected IcfImage loadExpensive() {
        throw new IllegalStateException("");
    }

    @Override
    public String readableInfo() {
        return toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NonLoadableImageInDatabase that = (NonLoadableImageInDatabase) o;

        return fileId == that.fileId;
    }

    @Override
    public int hashCode() {
        return (int) (fileId ^ (fileId >>> 32));
    }

    @Override
    public long getFileId() {
        return fileId;
    }

    @Override
    public String toString() {
        return "NonloadableImageInDatabase{" +
                "fileId=" + fileId +
                '}';
    }
}
