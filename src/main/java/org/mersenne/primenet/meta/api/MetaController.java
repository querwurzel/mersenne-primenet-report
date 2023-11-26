package org.mersenne.primenet.meta.api;

import org.mersenne.primenet.meta.application.MetaService;
import org.mersenne.primenet.meta.application.model.MetaInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/results/meta")
public class MetaController {

    private final MetaService metaService;

    @Autowired
    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    MetaInformation getMetaInformation() {
        return metaService.getMeta()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
    }
}
