package pl.ksitarski.icf.core.perceptual;

import java.util.ArrayList;
import java.util.List;

public class PerceptualSettings {
    private String perceptualCollectionTable = "PERCEPTUAL_COLLECTIONS";
    private String perceptualCollectionId = "ID";
    private String perceptualCollectionName = "NAME";

    private String perceptualHashesTable = "PERCEPTUAL_HASHES";
    private String perceptualHashesForeignKey = "PERCEPTUAL_COLLECTIONS_ID";
    private List<String> hashesColumns = new ArrayList<>();
}
