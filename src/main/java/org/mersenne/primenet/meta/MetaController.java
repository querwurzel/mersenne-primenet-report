package org.mersenne.primenet.meta;

import org.mersenne.primenet.meta.MetaService.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/results/meta")
public class MetaController {

    private final MetaService metaService;

    @Autowired
    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    protected Meta getMetadata() {
        return metaService.getMeta();
    }

}
