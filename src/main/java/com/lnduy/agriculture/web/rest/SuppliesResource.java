package com.lnduy.agriculture.web.rest;

import com.lnduy.agriculture.repository.SuppliesRepository;
import com.lnduy.agriculture.service.SuppliesService;
import com.lnduy.agriculture.service.dto.SuppliesDTO;
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
 * REST controller for managing {@link com.lnduy.agriculture.domain.Supplies}.
 */
@RestController
@RequestMapping("/api")
public class SuppliesResource {

    private final Logger log = LoggerFactory.getLogger(SuppliesResource.class);

    private static final String ENTITY_NAME = "supplies";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SuppliesService suppliesService;

    private final SuppliesRepository suppliesRepository;

    public SuppliesResource(SuppliesService suppliesService, SuppliesRepository suppliesRepository) {
        this.suppliesService = suppliesService;
        this.suppliesRepository = suppliesRepository;
    }

    /**
     * {@code POST  /supplies} : Create a new supplies.
     *
     * @param suppliesDTO the suppliesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new suppliesDTO, or with status {@code 400 (Bad Request)} if the supplies has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/supplies")
    public ResponseEntity<SuppliesDTO> createSupplies(@RequestBody SuppliesDTO suppliesDTO) throws URISyntaxException {
        log.debug("REST request to save Supplies : {}", suppliesDTO);
        if (suppliesDTO.getId() != null) {
            throw new BadRequestAlertException("A new supplies cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SuppliesDTO result = suppliesService.save(suppliesDTO);
        return ResponseEntity
            .created(new URI("/api/supplies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /supplies/:id} : Updates an existing supplies.
     *
     * @param id the id of the suppliesDTO to save.
     * @param suppliesDTO the suppliesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated suppliesDTO,
     * or with status {@code 400 (Bad Request)} if the suppliesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the suppliesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/supplies/{id}")
    public ResponseEntity<SuppliesDTO> updateSupplies(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SuppliesDTO suppliesDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Supplies : {}, {}", id, suppliesDTO);
        if (suppliesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, suppliesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!suppliesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SuppliesDTO result = suppliesService.update(suppliesDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, suppliesDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /supplies/:id} : Partial updates given fields of an existing supplies, field will ignore if it is null
     *
     * @param id the id of the suppliesDTO to save.
     * @param suppliesDTO the suppliesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated suppliesDTO,
     * or with status {@code 400 (Bad Request)} if the suppliesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the suppliesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the suppliesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/supplies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SuppliesDTO> partialUpdateSupplies(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SuppliesDTO suppliesDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Supplies partially : {}, {}", id, suppliesDTO);
        if (suppliesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, suppliesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!suppliesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SuppliesDTO> result = suppliesService.partialUpdate(suppliesDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, suppliesDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /supplies} : get all the supplies.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of supplies in body.
     */
    @GetMapping("/supplies")
    public ResponseEntity<List<SuppliesDTO>> getAllSupplies(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Supplies");
        Page<SuppliesDTO> page = suppliesService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /supplies/:id} : get the "id" supplies.
     *
     * @param id the id of the suppliesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the suppliesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/supplies/{id}")
    public ResponseEntity<SuppliesDTO> getSupplies(@PathVariable Long id) {
        log.debug("REST request to get Supplies : {}", id);
        Optional<SuppliesDTO> suppliesDTO = suppliesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(suppliesDTO);
    }

    /**
     * {@code DELETE  /supplies/:id} : delete the "id" supplies.
     *
     * @param id the id of the suppliesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/supplies/{id}")
    public ResponseEntity<Void> deleteSupplies(@PathVariable Long id) {
        log.debug("REST request to delete Supplies : {}", id);
        suppliesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
