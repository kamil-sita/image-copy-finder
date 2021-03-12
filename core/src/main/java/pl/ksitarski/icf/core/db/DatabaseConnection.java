package pl.ksitarski.icf.core.db;

import org.hibernate.Session;
import pl.ksitarski.icf.core.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.accessing.ImageInDatabaseImpl;
import pl.ksitarski.icf.core.jpa.dao.IcfDbDao;
import pl.ksitarski.icf.core.jpa.dao.IcfFileDao;
import pl.ksitarski.icf.core.jpa.orm.IcfDbOrm;
import pl.ksitarski.icf.core.jpa.orm.IcfFileOrm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DatabaseConnection implements AutoCloseable {

    private final Session session;

    public DatabaseConnection(Session session) {
        this.session = session;
    }

    public static IcfCollection getIfExistOrNull(String name, String path) {
        IcfDbOrm database = new IcfDbDao().getWhereName(name);

        if (database == null) {
            return null;
        }

        return new IcfCollection(name, path, database.getId(), database);
    }

    public static IcfCollection createDatabase(String name, String path) {
        IcfDbOrm dbOrm = new IcfDbOrm(name);
        new IcfDbDao().save(dbOrm);
        IcfDbOrm databaseOrm = new IcfDbDao().getWhereName(name);
        return new IcfCollection(name, path, databaseOrm.getId(), databaseOrm);
    }

    public static ImageInDatabase addOrFindFile(IcfDbOrm database, String pathBase, String path) {
        var fileDao = new IcfFileDao();

        IcfFileOrm fileOrm = fileDao.getByPath(database.getId(), path);
        if (fileOrm != null) {
            return fileOrm.toImageInDatabase();
        }

        String fullPath = "";
        if (pathBase != null && !pathBase.isBlank()) {
            fullPath += pathBase + File.separatorChar;
        }
        fullPath += path;

        fileOrm = new IcfFileOrm(
                fullPath,
                database
        );

        fileDao.save(fileOrm);

        return fileOrm.toImageInDatabase();
    }


    public static List<ImageInDatabase> addOrFindFiles(IcfDbOrm database, String pathBase, List<String> paths) {
        var fileDao = new IcfFileDao();
        List<ImageInDatabase> returnList = new ArrayList<>();

        for (String path : paths) {
            IcfFileOrm fileOrm = fileDao.getByPath(database.getId(), path);
            if (fileOrm != null) {
                returnList.add(fileOrm.toImageInDatabase());
            }

            String fullPath = "";
            if (pathBase != null && !pathBase.isBlank()) {
                fullPath += pathBase + File.separatorChar;
            }
            fullPath += path;

            fileOrm = new IcfFileOrm(
                    fullPath,
                    database
            );

            fileDao.save(fileOrm);

            returnList.add(fileOrm.toImageInDatabase());
        }
        return returnList;
    }

    public static List<Long> getAllImagesFromDatabase(IcfCollection database) {
        return new IcfFileDao().getIdList(database);
    }

    public static List<Long> getAllImagesFromDatabase(IcfCollection database, int minR, int maxR, int minG, int maxG, int minB, int maxB) {
        return new IcfFileDao().getIdList(database, minR, maxR, minG, maxG, minB, maxB);
    }

    public static List<String> getDatabases() {
        List<IcfDbOrm> databases = new IcfDbDao().lazyGetDatabases();
        List<String> names = new ArrayList<>();

        databases.forEach(new Consumer<IcfDbOrm>() {
            @Override
            public void accept(IcfDbOrm icfDbOrm) {
                names.add(icfDbOrm.getName());
            }
        });
        return names;
    }

    public static void remove(String chosenDatabase) {
        IcfDbOrm database = new IcfDbDao().getWhereName(chosenDatabase);

        if (database == null) {
            return;
        }

        new IcfDbDao().remove(database);
    }

    public static void remove(ImageInDatabaseImpl impl) {
        new IcfFileDao().remove(impl.getFileOrm());
    }

    @Override
    public void close() {
        session.close();
    }
}
