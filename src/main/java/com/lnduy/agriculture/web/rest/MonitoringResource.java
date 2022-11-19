package com.lnduy.agriculture.web.rest;

import com.lnduy.agriculture.repository.MonitoringRepository;
import com.lnduy.agriculture.service.MonitoringService;
import com.lnduy.agriculture.service.dto.MonitoringDTO;
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
 * REST controller for managing {@link com.lnduy.agriculture.domain.Monitoring}.
 */
@RestController
@RequestMapping("/api")
public class MonitoringResource {

    private final Logger log = LoggerFactory.getLogger(MonitoringResource.class);

    private static final String ENTITY_NAME = "monitoring";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MonitoringService monitoringService;

    private final MonitoringRepository monitoringRepository;

    public MonitoringResource(MonitoringService monitoringService, MonitoringRepository monitoringRepository) {
        this.monitoringService = monitoringService;
        this.monitoringRepository = monitoringRepository;
    }

    /**
     * {@code POST  /monitorings} : Create a new monitoring.
     *
     * @param monitoringDTO the monitoringDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new monitoringDTO, or with status {@code 400 (Bad Request)} if the monitoring has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/monitorings")
    public ResponseEntity<MonitoringDTO> createMonitoring(@RequestBody MonitoringDTO monitoringDTO) throws URISyntaxException {
        log.debug("REST request to save Monitoring : {}", monitoringDTO);
        if (monitoringDTO.getId() != null) {
            throw new BadRequestAlertException("A new monitoring cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MonitoringDTO result = monitoringService.save(monitoringDTO);
        return ResponseEntity
            .created(new URI("/api/monitorings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /monitorings/:id} : Updates an existing monitoring.
     *
     * @param id the id of the monitoringDTO to save.
     * @param monitoringDTO the monitoringDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated monitoringDTO,
     * or with status {@code 400 (Bad Request)} if the monitoringDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the monitoringDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/monitorings/{id}")
    public ResponseEntity<MonitoringDTO> updateMonitoring(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MonitoringDTO monitoringDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Monitoring : {}, {}", id, monitoringDTO);
        if (monitoringDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, monitoringDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!monitoringRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        MonitoringDTO result = monitoringService.update(monitoringDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, monitoringDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /monitorings/:id} : Partial updates given fields of an existing monitoring, field will ignore if it is null
     *
     * @param id the id of the monitoringDTO to save.
     * @param monitoringDTO the monitoringDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated monitoringDTO,
     * or with status {@code 400 (Bad Request)} if the monitoringDTO is not valid,
     * or with status {@code 404 (Not Found)} if the monitoringDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the monitoringDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/monitorings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MonitoringDTO> partialUpdateMonitoring(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MonitoringDTO monitoringDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Monitoring partially : {}, {}", id, monitoringDTO);
        if (monitoringDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, monitoringDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!monitoringRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MonitoringDTO> result = monitoringService.partialUpdate(monitoringDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, monitoringDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /monitorings} : get all the monitorings.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of monitorings in body.
     */
    @GetMapping("/monitorings")
    public List<MonitoringDTO> getAllMonitorings() {
        log.debug("REST request to get all Monitorings");
        return monitoringService.findAll();
    }

    /**
     * {@code GET  /monitorings/:id} : get the "id" monitoring.
     *
     * @param id the id of the monitoringDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the monitoringDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/monitorings/{id}")
    public ResponseEntity<MonitoringDTO> getMonitoring(@PathVariable Long id) {
        log.debug("REST request to get Monitoring : {}", id);
        Optional<MonitoringDTO> monitoringDTO = monitoringService.findOne(id);
        return ResponseUtil.wrapOrNotFound(monitoringDTO);
    }

    /**
     * {@code DELETE  /monitorings/:id} : delete the "id" monitoring.
     *
     * @param id the id of the monitoringDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/monitorings/{id}")
    public ResponseEntity<Void> deleteMonitoring(@PathVariable Long id) {
        log.debug("REST request to delete Monitoring : {}", id);
        monitoringService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
