package pl.ksitarski.icf.core.util;

import org.hibernate.SessionFactory;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import org.hibernate.cfg.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksitarski.icf.core.jpa.orm.*;

public class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static StandardServiceRegistry ssRegistry;
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {

        if (sessionFactory == null) {
            logger.info("Initializing SessionFactory");

            try {
                Configuration configuration = new Configuration();

                ssRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();

                //sources for MetadataSources
                MetadataSources sources = new MetadataSources(ssRegistry);

                sources.addAnnotatedClass(IcfDbOrm.class);
                sources.addAnnotatedClass(IcfFileOptOrm.class);
                sources.addAnnotatedClass(IcfFileOrm.class);

                // Create Metadata
                Metadata metadata = sources.getMetadataBuilder().build();

                // Create SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                e.printStackTrace();
                if (ssRegistry != null) {
                    StandardServiceRegistryBuilder.destroy(ssRegistry);
                }
            }
        }
        return sessionFactory;
    }
}