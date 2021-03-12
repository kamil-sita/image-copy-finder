package pl.ksitarski.icf.core.accessing;

import pl.ksitarski.icf.core.IcfNamedElement;
import pl.ksitarski.icf.core.image.IcfImage;

import java.util.Optional;

public abstract class LoadableImage implements IcfNamedElement {
    private IcfImage icfImage;
    private boolean loaded = false;

    protected abstract IcfImage loadExpensive() throws Exception;

    public abstract String readableInfo();

    @Override
    public String getName() {
        return readableInfo();
    }

    public Optional<IcfImage> getImage() {
        if (!loaded) {
            try {
                icfImage = loadExpensive();
            } catch (Exception e) {
                e.printStackTrace();
            }
            loaded = true;
            return Optional.ofNullable(icfImage);
        }
        return Optional.ofNullable(icfImage);
    }

    public abstract int hashCode();

    public abstract boolean equals(Object o);
}
