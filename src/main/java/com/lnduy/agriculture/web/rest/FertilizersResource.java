package com.lnduy.agriculture.web.rest;

import com.lnduy.agriculture.repository.FertilizersRepository;
import com.lnduy.agriculture.service.FertilizersQueryService;
import com.lnduy.agriculture.service.FertilizersService;
import com.lnduy.agriculture.service.criteria.FertilizersCriteria;
import com.lnduy.agriculture.service.dto.FertilizersDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lnduy.agriculture.domain.Fertilizers}.
 */
@RestController
@RequestMapping("/api")
public class FertilizersResource {

    private final Logger log = LoggerFactory.getLogger(FertilizersResource.class);

    private static final String ENTITY_NAME = "fertilizers";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FertilizersService fertilizersService;

    private final FertilizersRepository fertilizersRepository;

    private final FertilizersQueryService fertilizersQueryService;

    public FertilizersResource(
        FertilizersService fertilizersService,
        FertilizersRepository fertilizersRepository,
        FertilizersQueryService fertilizersQueryService
    ) {
        this.fertilizersService = fertilizersService;
        this.fertilizersRepository = fertilizersRepository;
        this.fertilizersQueryService = fertilizersQueryService;
    }

    /**
     * {@code POST  /fertilizers} : Create a new fertilizers.
     *
     * @param fertilizersDTO the fertilizersDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fertilizersDTO, or with status {@code 400 (Bad Request)} if the fertilizers has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/fertilizers")
    public ResponseEntity<FertilizersDTO> createFertilizers(@RequestBody FertilizersDTO fertilizersDTO) throws URISyntaxException {
        log.debug("REST request to save Fertilizers : {}", fertilizersDTO);
        if (fertilizersDTO.getId() != null) {
            throw new BadRequestAlertException("A new fertilizers cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FertilizersDTO result = fertilizersService.save(fertilizersDTO);
        return ResponseEntity
            .created(new URI("/api/fertilizers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /fertilizers/:id} : Updates an existing fertilizers.
     *
     * @param id the id of the fertilizersDTO to save.
     * @param fertilizersDTO the fertilizersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fertilizersDTO,
     * or with status {@code 400 (Bad Request)} if the fertilizersDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fertilizersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/fertilizers/{id}")
    public ResponseEntity<FertilizersDTO> updateFertilizers(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FertilizersDTO fertilizersDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Fertilizers : {}, {}", id, fertilizersDTO);
        if (fertilizersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fertilizersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fertilizersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FertilizersDTO result = fertilizersService.update(fertilizersDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fertilizersDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /fertilizers/:id} : Partial updates given fields of an existing fertilizers, field will ignore if it is null
     *
     * @param id the id of the fertilizersDTO to save.
     * @param fertilizersDTO the fertilizersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fertilizersDTO,
     * or with status {@code 400 (Bad Request)} if the fertilizersDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fertilizersDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fertilizersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/fertilizers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FertilizersDTO> partialUpdateFertilizers(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FertilizersDTO fertilizersDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Fertilizers partially : {}, {}", id, fertilizersDTO);
        if (fertilizersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fertilizersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fertilizersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FertilizersDTO> result = fertilizersService.partialUpdate(fertilizersDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fertilizersDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /fertilizers} : get all the fertilizers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fertilizers in body.
     */
    @GetMapping("/fertilizers")
    public ResponseEntity<List<FertilizersDTO>> getAllFertilizers(
        FertilizersCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Fertilizers by criteria: {}", criteria);
        Page<FertilizersDTO> page = fertilizersQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /fertilizers/count} : count all the fertilizers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/fertilizers/count")
    public ResponseEntity<Long> countFertilizers(FertilizersCriteria criteria) {
        log.debug("REST request to count Fertilizers by criteria: {}", criteria);
        return ResponseEntity.ok().body(fertilizersQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /fertilizers/:id} : get the "id" fertilizers.
     *
     * @param id the id of the fertilizersDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fertilizersDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/fertilizers/{id}")
    public ResponseEntity<FertilizersDTO> getFertilizers(@PathVariable Long id) {
        log.debug("REST request to get Fertilizers : {}", id);
        Optional<FertilizersDTO> fertilizersDTO = fertilizersService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fertilizersDTO);
    }

    /**
     * {@code DELETE  /fertilizers/:id} : delete the "id" fertilizers.
     *
     * @param id the id of the fertilizersDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/fertilizers/{id}")
    public ResponseEntity<Void> deleteFertilizers(@PathVariable Long id) {
        log.debug("REST request to delete Fertilizers : {}", id);
        fertilizersService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
