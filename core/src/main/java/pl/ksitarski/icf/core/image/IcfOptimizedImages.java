package pl.ksitarski.icf.core.image;

import pl.ksitarski.icf.core.accessing.LoadableImage;
import pl.ksitarski.icf.core.comparison.definitions.ImageComparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IcfOptimizedImages {
    private final Map<ImageComparator, IcfOptimizedImage> map;
    private final LoadableImage parent;

    public IcfOptimizedImages(Map<ImageComparator, IcfOptimizedImage> map, LoadableImage parent) {
        Objects.requireNonNull(map);
        this.map = new HashMap<>(map.size(), 1f);
        for (var entry : map.entrySet()) {
            this.map.put(entry.getKey(), entry.getValue());
        }
        this.parent = parent;
    }

    public IcfOptimizedImage getForComparator(ImageComparator imageComparator) {
        return map.get(imageComparator);
    }

    public LoadableImage getParent() {
        return parent;
    }
}
