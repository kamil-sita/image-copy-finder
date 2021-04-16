package pl.ksitarski.icf.core.prototype.accessing;

import pl.ksitarski.icf.core.prototype.IcfNamedElement;

public abstract class ImageInDatabase extends LoadableImage implements DatabasePointer, IcfNamedElement {
    public abstract long getId();
    public abstract String getFullPath();
}
