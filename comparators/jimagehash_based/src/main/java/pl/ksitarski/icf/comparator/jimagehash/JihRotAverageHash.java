package pl.ksitarski.icf.comparator.jimagehash;

import com.github.kilianB.hashAlgorithms.RotAverageHash;
import pl.ksitarski.icf.comparator.jimagehash.util.JihBase;

public class JihRotAverageHash extends JihBase {
    public JihRotAverageHash(int resolution) {
        super("RotAverageHash_" + resolution, new RotAverageHash(resolution));
    }
}
