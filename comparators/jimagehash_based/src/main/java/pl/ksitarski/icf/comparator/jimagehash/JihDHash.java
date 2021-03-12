package pl.ksitarski.icf.comparator.jimagehash;

import com.github.kilianB.hashAlgorithms.DifferenceHash;
import pl.ksitarski.icf.comparator.jimagehash.util.JihBase;

public class JihDHash extends JihBase {
    public JihDHash(int resolution, DifferenceHash.Precision precision) {
        super("DHASH_" + resolution + "_" + precision.name(), new DifferenceHash(resolution, precision));
    }
}
