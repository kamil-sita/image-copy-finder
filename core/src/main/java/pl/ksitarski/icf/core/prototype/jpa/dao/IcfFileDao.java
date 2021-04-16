package pl.ksitarski.icf.core.prototype.jpa.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.ksitarski.icf.core.prototype.db.IcfCollection;
import pl.ksitarski.icf.core.prototype.jpa.orm.IcfFileOrm;
import pl.ksitarski.icf.core.prototype.util.HibernateUtil;

import java.util.Collection;
import java.util.List;

public class IcfFileDao {
    public void save(IcfFileOrm icfFileOrm) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(icfFileOrm);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)  {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void save(Collection<IcfFileOrm> fileOrms) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            for (IcfFileOrm fileOrm : fileOrms) {
                session.save(fileOrm);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)  {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void update(Collection<IcfFileOrm> fileOrms) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            for (IcfFileOrm fileOrm : fileOrms) {
                session.update(fileOrm);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)  {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<IcfFileOrm> lazyGetFiles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from IcfFileOrm", IcfFileOrm.class).list();
        }
    }

    public IcfFileOrm getByPath(long dbId, String path) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from IcfFileOrm as icffileorm where icffileorm.filePath = '" + path + "' and icf_db_id_r = '" + dbId + "'", IcfFileOrm.class).uniqueResult();
        }
    }

    public IcfFileOrm getById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from IcfFileOrm as icffileorm where icffileorm.id = " + id, IcfFileOrm.class).uniqueResult();
        }
    }

    public List<IcfFileOrm> getByIds(Collection<Long> ids) {
        if (ids.isEmpty()) {
            throw new IllegalArgumentException("Ids to load cannot be empty");
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String query = "from IcfFileOrm as icffileorm where icffileorm.id in (" + toStringLongs(ids) + ")";
            return session.createQuery(query, IcfFileOrm.class).list();
        }
    }

    public List<Long> getIdList(IcfCollection database) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select id from IcfFileOrm as icffileorm where icffileorm.icfDbOrm.id = " + database.getId(), Long.class).list();
        }
    }

    public List<Long> getIdList(IcfCollection database, int minR, int maxR, int minG, int maxG, int minB, int maxB) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select id from IcfFileOrm as icffileorm where icffileorm.icfDbOrm.id = " + database.getId() + " " +
                            "and ((r > " + minR + " and r < " + maxR + " and g > " + minG + " and g < " + maxG + " and b > " + minB + " and b < " + maxB + ") or (statisticsPresent = false))",
                    Long.class
            ).list();
        }
    }


    private String toStringLongs(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return "'NULL'"; //todo consider better workaround
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


    public void remove(IcfFileOrm icfFileOrm) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(icfFileOrm);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)  {
                transaction.rollback();
            }
        }
    }
}
