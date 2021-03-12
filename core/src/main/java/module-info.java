module icf.core {
    requires java.desktop;
    requires commons.math3;
    requires java.persistence;
    requires org.hibernate.commons.annotations;
    requires org.hibernate.orm.core;
    requires org.apache.commons.lang3;
    requires imgscalr.lib;
    requires org.apache.commons.io;
    requires java.naming;
    requires org.slf4j;

    exports pl.ksitarski.icf.core.db;
    exports pl.ksitarski.icf.core.accessing;
    exports pl.ksitarski.icf.core.comparison;
    exports pl.ksitarski.icf.core.image;
    exports pl.ksitarski.icf.core.exc;
    exports pl.ksitarski.icf.core.comparison.engine;
    exports pl.ksitarski.icf.core.comparison.comparator;
    exports pl.ksitarski.icf.core.comparison.definitions;
    exports pl.ksitarski.icf.core.comparison.impls;
}