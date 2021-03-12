package pl.ksitarski.icf.comparator.jimagehash;

import com.github.kilianB.hashAlgorithms.WaveletHash;
import pl.ksitarski.icf.comparator.jimagehash.util.JihBase;

public class JihWaveletHash extends JihBase {
    public JihWaveletHash(int resolution, int cycles) {
        super("HAAR_" + resolution + "_" + cycles, new WaveletHash(resolution, cycles));
    }
}
