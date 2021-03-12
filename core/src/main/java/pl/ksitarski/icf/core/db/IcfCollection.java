package pl.ksitarski.icf.core.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksitarski.icf.core.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.accessing.ImageInDatabaseImpl;
import pl.ksitarski.icf.core.jpa.dao.IcfFileDao;
import pl.ksitarski.icf.core.jpa.orm.IcfDbOrm;
import pl.ksitarski.icf.core.jpa.orm.IcfFileOrm;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides abstraction over ICF Database management and connection.
 */
public class IcfCollection {
    private final static Logger logger = LoggerFactory.getLogger(IcfCollection.class);
    private final String name;
    private final String path;
    private final long id;
    private final IcfDbOrm icfDbOrm;

    IcfCollection(String name, String path, long id, IcfDbOrm icfDbOrm) {
        this.name = name;
        this.path = path;
        this.id = id;
        this.icfDbOrm = icfDbOrm;
    }

    public static List<String> getCollectionsNames() {
        return DatabaseConnection.getDatabases();
    }

    public static IcfCollection getCollection(String name, String path) {
        IcfCollection database = DatabaseConnection.getIfExistOrNull(name, path);
        if (database != null) {
            return database;
        }
        throw new IllegalStateException("Cannot get database as it does not exist!");
    }

    public static IcfCollection getOrCreateCollection(String name, String path) {
        IcfCollection database = DatabaseConnection.getIfExistOrNull(name, path);
        if (database != null) {
            return database;
        }
        return DatabaseConnection.createDatabase(name, path);
    }

    public static boolean doesCollectionExist(String name) {
        return getCollectionsNames().contains(name);
    }

    public static void removeCollection(String chosenDatabase) {
        DatabaseConnection.remove(chosenDatabase);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public ImageInDatabase addOrFindFile(Path fileImageDiscriminator) {
        return DatabaseConnection.addOrFindFile(icfDbOrm, path, fileImageDiscriminator.toString());
    }

    public List<ImageInDatabase> addOrFindFiles(List<Path> fileImageDiscriminator) {
        List<String> paths = new ArrayList<>();
        for (Path path : fileImageDiscriminator) {
            paths.add(path.toString());
        }
        return DatabaseConnection.addOrFindFiles(icfDbOrm, path, paths);
    }

    public void removeFile(ImageInDatabase imageInDatabase) {
        ImageInDatabaseImpl impl = (ImageInDatabaseImpl) imageInDatabase;
        DatabaseConnection.remove(impl);
     }

    public List<ImageInDatabase> getFiles() {
        List<Long> ids = DatabaseConnection.getAllImagesFromDatabase(this);
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<IcfFileOrm> icfFileOrms = new IcfFileDao().getByIds(ids);

        List<ImageInDatabase> imagesInDatabase = new ArrayList<>();

        for (IcfFileOrm icfFileOrm : icfFileOrms) {
            imagesInDatabase.add(
                    new ImageInDatabaseImpl(icfDbOrm, icfFileOrm)
            );
        }

        return imagesInDatabase;
    }
}
