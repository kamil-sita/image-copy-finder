package pl.ksitarski.icf.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ksitarski.icf.core.prototype.db.IcfCollection;

import java.util.List;

@RestController
@RequestMapping(path = "/collections")
public class CollectionsController {

    @GetMapping
    public List<String> get() {
        return IcfCollection.getCollectionsNames();
    }

}
