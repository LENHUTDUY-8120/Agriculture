package com.lnduy.agriculture.web.rest;

import com.lnduy.agriculture.repository.SoilTypeRepository;
import com.lnduy.agriculture.service.SoilTypeService;
import com.lnduy.agriculture.service.dto.SoilTypeDTO;
import com.lnduy.agriculture.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lnduy.agriculture.domain.SoilType}.
 */
@RestController
@RequestMapping("/api")
public class SoilTypeResource {

    private final Logger log = LoggerFactory.getLogger(SoilTypeResource.class);

    private static final String ENTITY_NAME = "soilType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SoilTypeService soilTypeService;

    private final SoilTypeRepository soilTypeRepository;

    public SoilTypeResource(SoilTypeService soilTypeService, SoilTypeRepository soilTypeRepository) {
        this.soilTypeService = soilTypeService;
        this.soilTypeRepository = soilTypeRepository;
    }

    /**
     * {@code POST  /soil-types} : Create a new soilType.
     *
     * @param soilTypeDTO the soilTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new soilTypeDTO, or with status {@code 400 (Bad Request)} if the soilType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/soil-types")
    public ResponseEntity<SoilTypeDTO> createSoilType(@RequestBody SoilTypeDTO soilTypeDTO) throws URISyntaxException {
        log.debug("REST request to save SoilType : {}", soilTypeDTO);
        if (soilTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new soilType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SoilTypeDTO result = soilTypeService.save(soilTypeDTO);
        return ResponseEntity
            .created(new URI("/api/soil-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /soil-types/:id} : Updates an existing soilType.
     *
     * @param id the id of the soilTypeDTO to save.
     * @param soilTypeDTO the soilTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated soilTypeDTO,
     * or with status {@code 400 (Bad Request)} if the soilTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the soilTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/soil-types/{id}")
    public ResponseEntity<SoilTypeDTO> updateSoilType(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SoilTypeDTO soilTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update SoilType : {}, {}", id, soilTypeDTO);
        if (soilTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, soilTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!soilTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SoilTypeDTO result = soilTypeService.update(soilTypeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, soilTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /soil-types/:id} : Partial updates given fields of an existing soilType, field will ignore if it is null
     *
     * @param id the id of the soilTypeDTO to save.
     * @param soilTypeDTO the soilTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated soilTypeDTO,
     * or with status {@code 400 (Bad Request)} if the soilTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the soilTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the soilTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/soil-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SoilTypeDTO> partialUpdateSoilType(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SoilTypeDTO soilTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SoilType partially : {}, {}", id, soilTypeDTO);
        if (soilTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, soilTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!soilTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SoilTypeDTO> result = soilTypeService.partialUpdate(soilTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, soilTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /soil-types} : get all the soilTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of soilTypes in body.
     */
    @GetMapping("/soil-types")
    public List<SoilTypeDTO> getAllSoilTypes() {
        log.debug("REST request to get all SoilTypes");
        return soilTypeService.findAll();
    }

    /**
     * {@code GET  /soil-types/:id} : get the "id" soilType.
     *
     * @param id the id of the soilTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the soilTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/soil-types/{id}")
    public ResponseEntity<SoilTypeDTO> getSoilType(@PathVariable Long id) {
        log.debug("REST request to get SoilType : {}", id);
        Optional<SoilTypeDTO> soilTypeDTO = soilTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(soilTypeDTO);
    }

    /**
     * {@code DELETE  /soil-types/:id} : delete the "id" soilType.
     *
     * @param id the id of the soilTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/soil-types/{id}")
    public ResponseEntity<Void> deleteSoilType(@PathVariable Long id) {
        log.debug("REST request to delete SoilType : {}", id);
        soilTypeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
