package org.mersenne.primenet.imports.api;

import org.mersenne.primenet.imports.application.MetaService;
import org.mersenne.primenet.imports.application.model.MetaInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/results/meta")
class MetaController {

    private final MetaService metaService;

    @Autowired
    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MetaInformation> getMetaInformation() {
        return metaService.getMeta()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
