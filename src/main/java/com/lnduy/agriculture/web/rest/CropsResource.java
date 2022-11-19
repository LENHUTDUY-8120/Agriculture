package com.lnduy.agriculture.web.rest;

import com.lnduy.agriculture.repository.CropsRepository;
import com.lnduy.agriculture.service.CropsService;
import com.lnduy.agriculture.service.dto.CropsDTO;
import com.lnduy.agriculture.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lnduy.agriculture.domain.Crops}.
 */
@RestController
@RequestMapping("/api")
public class CropsResource {

    private final Logger log = LoggerFactory.getLogger(CropsResource.class);

    private static final String ENTITY_NAME = "crops";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CropsService cropsService;

    private final CropsRepository cropsRepository;

    public CropsResource(CropsService cropsService, CropsRepository cropsRepository) {
        this.cropsService = cropsService;
        this.cropsRepository = cropsRepository;
    }

    /**
     * {@code POST  /crops} : Create a new crops.
     *
     * @param cropsDTO the cropsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cropsDTO, or with status {@code 400 (Bad Request)} if the crops has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/crops")
    public ResponseEntity<CropsDTO> createCrops(@RequestBody CropsDTO cropsDTO) throws URISyntaxException {
        log.debug("REST request to save Crops : {}", cropsDTO);
        if (cropsDTO.getId() != null) {
            throw new BadRequestAlertException("A new crops cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CropsDTO result = cropsService.save(cropsDTO);
        return ResponseEntity
            .created(new URI("/api/crops/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /crops/:id} : Updates an existing crops.
     *
     * @param id the id of the cropsDTO to save.
     * @param cropsDTO the cropsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cropsDTO,
     * or with status {@code 400 (Bad Request)} if the cropsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cropsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/crops/{id}")
    public ResponseEntity<CropsDTO> updateCrops(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CropsDTO cropsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Crops : {}, {}", id, cropsDTO);
        if (cropsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cropsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cropsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CropsDTO result = cropsService.update(cropsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cropsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /crops/:id} : Partial updates given fields of an existing crops, field will ignore if it is null
     *
     * @param id the id of the cropsDTO to save.
     * @param cropsDTO the cropsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cropsDTO,
     * or with status {@code 400 (Bad Request)} if the cropsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cropsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cropsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/crops/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CropsDTO> partialUpdateCrops(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CropsDTO cropsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Crops partially : {}, {}", id, cropsDTO);
        if (cropsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cropsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cropsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CropsDTO> result = cropsService.partialUpdate(cropsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cropsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /crops} : get all the crops.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of crops in body.
     */
    @GetMapping("/crops")
    public ResponseEntity<List<CropsDTO>> getAllCrops(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Crops");
        Page<CropsDTO> page = cropsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /crops/:id} : get the "id" crops.
     *
     * @param id the id of the cropsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cropsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/crops/{id}")
    public ResponseEntity<CropsDTO> getCrops(@PathVariable Long id) {
        log.debug("REST request to get Crops : {}", id);
        Optional<CropsDTO> cropsDTO = cropsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cropsDTO);
    }

    /**
     * {@code DELETE  /crops/:id} : delete the "id" crops.
     *
     * @param id the id of the cropsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/crops/{id}")
    public ResponseEntity<Void> deleteCrops(@PathVariable Long id) {
        log.debug("REST request to delete Crops : {}", id);
        cropsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
