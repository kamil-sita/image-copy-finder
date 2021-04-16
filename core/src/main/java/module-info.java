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

    exports pl.ksitarski.icf.core.prototype.db;
    exports pl.ksitarski.icf.core.prototype.accessing;
    exports pl.ksitarski.icf.core.prototype.comparison;
    exports pl.ksitarski.icf.core.prototype.image;
    exports pl.ksitarski.icf.core.prototype.exc;
    exports pl.ksitarski.icf.core.prototype.comparison.engine;
    exports pl.ksitarski.icf.core.prototype.comparison.comparator;
    exports pl.ksitarski.icf.core.prototype.comparison.definitions;
    exports pl.ksitarski.icf.core.prototype.comparison.impls;
    exports pl.ksitarski.icf.core.prototype.imageloader;
}