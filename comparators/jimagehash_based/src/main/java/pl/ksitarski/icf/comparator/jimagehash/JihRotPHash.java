package pl.ksitarski.icf.comparator.jimagehash;

import com.github.kilianB.hashAlgorithms.RotPHash;
import pl.ksitarski.icf.comparator.jimagehash.util.JihBase;

public class JihRotPHash extends JihBase {
    public JihRotPHash(int resolution) {
        super("RotPHash_" + resolution, new RotPHash(resolution));
    }
}
