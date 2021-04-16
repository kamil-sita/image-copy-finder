package pl.ksitarski.icf.core.prototype.jpa.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.ksitarski.icf.core.prototype.comparison.definitions.ImageComparator;
import pl.ksitarski.icf.core.prototype.db.IcfCollection;
import pl.ksitarski.icf.core.prototype.jpa.orm.IcfFileOptOrm;
import pl.ksitarski.icf.core.prototype.util.HibernateUtil;

import java.util.Collection;
import java.util.List;
import java.util.Set;


public class IcfFileOptDao {
    public void save(IcfFileOptOrm icfFileOptOrm) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(icfFileOptOrm);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)  {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void save(Collection<IcfFileOptOrm> icfFileOptOrm) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            for (IcfFileOptOrm optOrm : icfFileOptOrm) {
                session.save(optOrm);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)  {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<IcfFileOptOrm> getWhereIdAndOptimization(IcfCollection database, List<Long> idsToLoad, Set<ImageComparator> comparators) {try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        return session.createQuery(
                "from IcfFileOptOrm as foorm where foorm.icfFileOrm.icfDbOrm.id = " + database.getId() + " and foorm.optimizationType in (" + toStringComparators(comparators) + ") " +
                        "and foorm.icfFileOrm.id in (" + toStringLongs(idsToLoad) + ")",
                IcfFileOptOrm.class).list();
    }
    }

    private String toStringLongs(List<Long> ids) {
        if (ids.size() == 0) {
            return "-1"; //todo consider better workaround
        }

        StringBuilder sb = new StringBuilder();
        boolean appended = false;
        for (Long lon : ids) {
            if (appended) {
                sb.append(", ");
            }
            appended = true;
            sb.append(lon);
        }
        return sb.toString();
    }

    private String toStringComparators(Set<ImageComparator> comparators) {
        if (comparators.size() == 0) {
            return "NULL"; //todo consider better workaround
        }
        StringBuilder sb = new StringBuilder();
        boolean appended = false;
        for (ImageComparator comparator : comparators) {
            if (appended) {
                sb.append(", ");
            }
            appended = true;
            sb.append("'").append(comparator.canonicalName()).append("'");
        }
        return sb.toString();
    }
}
