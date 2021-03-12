package pl.ksitarski.icf.core.accessing;

import pl.ksitarski.icf.core.IcfNamedElement;

public abstract class ImageInDatabase extends LoadableImage implements DatabasePointer, IcfNamedElement {
    public abstract long getId();
    public abstract String getFullPath();
}
