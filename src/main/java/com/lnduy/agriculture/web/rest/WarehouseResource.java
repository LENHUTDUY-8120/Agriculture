package com.lnduy.agriculture.web.rest;

import com.lnduy.agriculture.repository.WarehouseRepository;
import com.lnduy.agriculture.service.WarehouseQueryService;
import com.lnduy.agriculture.service.WarehouseService;
import com.lnduy.agriculture.service.criteria.WarehouseCriteria;
import com.lnduy.agriculture.service.dto.WarehouseDTO;
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
 * REST controller for managing {@link com.lnduy.agriculture.domain.Warehouse}.
 */
@RestController
@RequestMapping("/api")
public class WarehouseResource {

    private final Logger log = LoggerFactory.getLogger(WarehouseResource.class);

    private static final String ENTITY_NAME = "warehouse";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WarehouseService warehouseService;

    private final WarehouseRepository warehouseRepository;

    private final WarehouseQueryService warehouseQueryService;

    public WarehouseResource(
        WarehouseService warehouseService,
        WarehouseRepository warehouseRepository,
        WarehouseQueryService warehouseQueryService
    ) {
        this.warehouseService = warehouseService;
        this.warehouseRepository = warehouseRepository;
        this.warehouseQueryService = warehouseQueryService;
    }

    /**
     * {@code POST  /warehouses} : Create a new warehouse.
     *
     * @param warehouseDTO the warehouseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new warehouseDTO, or with status {@code 400 (Bad Request)} if the warehouse has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/warehouses")
    public ResponseEntity<WarehouseDTO> createWarehouse(@RequestBody WarehouseDTO warehouseDTO) throws URISyntaxException {
        log.debug("REST request to save Warehouse : {}", warehouseDTO);
        if (warehouseDTO.getId() != null) {
            throw new BadRequestAlertException("A new warehouse cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WarehouseDTO result = warehouseService.save(warehouseDTO);
        return ResponseEntity
            .created(new URI("/api/warehouses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /warehouses/:id} : Updates an existing warehouse.
     *
     * @param id the id of the warehouseDTO to save.
     * @param warehouseDTO the warehouseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated warehouseDTO,
     * or with status {@code 400 (Bad Request)} if the warehouseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the warehouseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/warehouses/{id}")
    public ResponseEntity<WarehouseDTO> updateWarehouse(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody WarehouseDTO warehouseDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Warehouse : {}, {}", id, warehouseDTO);
        if (warehouseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, warehouseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!warehouseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WarehouseDTO result = warehouseService.update(warehouseDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, warehouseDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /warehouses/:id} : Partial updates given fields of an existing warehouse, field will ignore if it is null
     *
     * @param id the id of the warehouseDTO to save.
     * @param warehouseDTO the warehouseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated warehouseDTO,
     * or with status {@code 400 (Bad Request)} if the warehouseDTO is not valid,
     * or with status {@code 404 (Not Found)} if the warehouseDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the warehouseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/warehouses/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WarehouseDTO> partialUpdateWarehouse(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody WarehouseDTO warehouseDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Warehouse partially : {}, {}", id, warehouseDTO);
        if (warehouseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, warehouseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!warehouseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WarehouseDTO> result = warehouseService.partialUpdate(warehouseDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, warehouseDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /warehouses} : get all the warehouses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of warehouses in body.
     */
    @GetMapping("/warehouses")
    public ResponseEntity<List<WarehouseDTO>> getAllWarehouses(WarehouseCriteria criteria) {
        log.debug("REST request to get Warehouses by criteria: {}", criteria);
        List<WarehouseDTO> entityList = warehouseQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /warehouses/count} : count all the warehouses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/warehouses/count")
    public ResponseEntity<Long> countWarehouses(WarehouseCriteria criteria) {
        log.debug("REST request to count Warehouses by criteria: {}", criteria);
        return ResponseEntity.ok().body(warehouseQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /warehouses/:id} : get the "id" warehouse.
     *
     * @param id the id of the warehouseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the warehouseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/warehouses/{id}")
    public ResponseEntity<WarehouseDTO> getWarehouse(@PathVariable Long id) {
        log.debug("REST request to get Warehouse : {}", id);
        Optional<WarehouseDTO> warehouseDTO = warehouseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(warehouseDTO);
    }

    /**
     * {@code DELETE  /warehouses/:id} : delete the "id" warehouse.
     *
     * @param id the id of the warehouseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/warehouses/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        log.debug("REST request to delete Warehouse : {}", id);
        warehouseService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
