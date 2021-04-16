package pl.ksitarski.icf.core.prototype.accessing;

import pl.ksitarski.icf.core.prototype.image.IcfImage;
import pl.ksitarski.icf.core.prototype.jpa.orm.IcfDbOrm;
import pl.ksitarski.icf.core.prototype.jpa.orm.IcfFileOrm;
import pl.ksitarski.icf.core.prototype.util.BufferedImageUtil;

import java.io.IOException;
import java.nio.file.Path;

public class ImageInDatabaseImpl extends ImageInDatabase  {
    private final IcfDbOrm dbOrm;
    private final IcfFileOrm fileOrm;
    private final long fileOrmId;

    public ImageInDatabaseImpl(IcfDbOrm dbOrm, IcfFileOrm fileOrm) {
        this.dbOrm = dbOrm;
        this.fileOrm = fileOrm;
        fileOrmId = fileOrm.getId();
    }

    @Override
    public long getId() {
        return fileOrm.getId();
    }

    @Override
    public String getFullPath() {
        return fileOrm.getFilePath();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageInDatabaseImpl that = (ImageInDatabaseImpl) o;

        return fileOrmId == that.fileOrmId;
    }

    @Override
    public int hashCode() {
        return (int) (fileOrmId ^ (fileOrmId >>> 32));
    }

    @Override
    public String toString() {
        return "ImageInDatabaseImpl{" +
                "fileOrmId=" + fileOrm.getId() +
                ", path=" + fileOrm.getFilePath() +
                '}';
    }

    @Override
    protected IcfImage loadExpensive() throws IOException {
        return new IcfImage(
                this,
                BufferedImageUtil.load(Path.of(fileOrm.getFilePath()))
        );
    }

    @Override
    public String readableInfo() {
        String fp = getFullPath();
        String fpa[] = fp.split("/");
        fp = fpa[fpa.length - 1];
        fpa = fp.split("\\\\");
        fp = fpa[fpa.length - 1];
        return fp;
    }

    @Override
    public long getFileId() {
        return fileOrmId;
    }

    public IcfFileOrm getFileOrm() {
        return fileOrm;
    }
}
