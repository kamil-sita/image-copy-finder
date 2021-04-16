package pl.ksitarski.icf.core.prototype.jpa.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.ksitarski.icf.core.prototype.jpa.orm.IcfDbOrm;
import pl.ksitarski.icf.core.prototype.util.HibernateUtil;

import java.util.List;


public class IcfDbDao {
    public void save(IcfDbOrm icfDbOrm) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(icfDbOrm);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)  {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<IcfDbOrm> lazyGetDatabases() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from IcfDbOrm", IcfDbOrm.class).list();
        }
    }

    public IcfDbOrm getWhereName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from IcfDbOrm as icfdb where icfdb.name = '" + name + "'", IcfDbOrm.class).uniqueResult();
        }
    }

    public void remove(IcfDbOrm database) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(database);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)  {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
