package pl.ksitarski.icf.comparator.jimagehash;

import com.github.kilianB.hashAlgorithms.AverageHash;
import pl.ksitarski.icf.comparator.jimagehash.util.JihBase;

public class JihAverageHash extends JihBase {
    public JihAverageHash(int resolution) {
        super("AverageHash_" + resolution, new AverageHash(resolution));
    }
}
