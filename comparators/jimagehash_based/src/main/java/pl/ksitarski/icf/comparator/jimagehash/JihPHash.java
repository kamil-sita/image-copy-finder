package pl.ksitarski.icf.comparator.jimagehash;

import com.github.kilianB.hashAlgorithms.PerceptiveHash;
import pl.ksitarski.icf.comparator.jimagehash.util.JihBase;

public class JihPHash extends JihBase {
    public JihPHash(int resolution) {
        super("PHASH_" + resolution, new PerceptiveHash(resolution));
    }
}
