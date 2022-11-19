package com.lnduy.agriculture.web.rest;

import com.lnduy.agriculture.repository.DeviceCategoryRepository;
import com.lnduy.agriculture.service.DeviceCategoryService;
import com.lnduy.agriculture.service.dto.DeviceCategoryDTO;
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
 * REST controller for managing {@link com.lnduy.agriculture.domain.DeviceCategory}.
 */
@RestController
@RequestMapping("/api")
public class DeviceCategoryResource {

    private final Logger log = LoggerFactory.getLogger(DeviceCategoryResource.class);

    private static final String ENTITY_NAME = "deviceCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DeviceCategoryService deviceCategoryService;

    private final DeviceCategoryRepository deviceCategoryRepository;

    public DeviceCategoryResource(DeviceCategoryService deviceCategoryService, DeviceCategoryRepository deviceCategoryRepository) {
        this.deviceCategoryService = deviceCategoryService;
        this.deviceCategoryRepository = deviceCategoryRepository;
    }

    /**
     * {@code POST  /device-categories} : Create a new deviceCategory.
     *
     * @param deviceCategoryDTO the deviceCategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new deviceCategoryDTO, or with status {@code 400 (Bad Request)} if the deviceCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/device-categories")
    public ResponseEntity<DeviceCategoryDTO> createDeviceCategory(@RequestBody DeviceCategoryDTO deviceCategoryDTO)
        throws URISyntaxException {
        log.debug("REST request to save DeviceCategory : {}", deviceCategoryDTO);
        if (deviceCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new deviceCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DeviceCategoryDTO result = deviceCategoryService.save(deviceCategoryDTO);
        return ResponseEntity
            .created(new URI("/api/device-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /device-categories/:id} : Updates an existing deviceCategory.
     *
     * @param id the id of the deviceCategoryDTO to save.
     * @param deviceCategoryDTO the deviceCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deviceCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the deviceCategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the deviceCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/device-categories/{id}")
    public ResponseEntity<DeviceCategoryDTO> updateDeviceCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DeviceCategoryDTO deviceCategoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update DeviceCategory : {}, {}", id, deviceCategoryDTO);
        if (deviceCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deviceCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deviceCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DeviceCategoryDTO result = deviceCategoryService.update(deviceCategoryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, deviceCategoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /device-categories/:id} : Partial updates given fields of an existing deviceCategory, field will ignore if it is null
     *
     * @param id the id of the deviceCategoryDTO to save.
     * @param deviceCategoryDTO the deviceCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deviceCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the deviceCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the deviceCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the deviceCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/device-categories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DeviceCategoryDTO> partialUpdateDeviceCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DeviceCategoryDTO deviceCategoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update DeviceCategory partially : {}, {}", id, deviceCategoryDTO);
        if (deviceCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deviceCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deviceCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DeviceCategoryDTO> result = deviceCategoryService.partialUpdate(deviceCategoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, deviceCategoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /device-categories} : get all the deviceCategories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deviceCategories in body.
     */
    @GetMapping("/device-categories")
    public List<DeviceCategoryDTO> getAllDeviceCategories() {
        log.debug("REST request to get all DeviceCategories");
        return deviceCategoryService.findAll();
    }

    /**
     * {@code GET  /device-categories/:id} : get the "id" deviceCategory.
     *
     * @param id the id of the deviceCategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the deviceCategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/device-categories/{id}")
    public ResponseEntity<DeviceCategoryDTO> getDeviceCategory(@PathVariable Long id) {
        log.debug("REST request to get DeviceCategory : {}", id);
        Optional<DeviceCategoryDTO> deviceCategoryDTO = deviceCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(deviceCategoryDTO);
    }

    /**
     * {@code DELETE  /device-categories/:id} : delete the "id" deviceCategory.
     *
     * @param id the id of the deviceCategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/device-categories/{id}")
    public ResponseEntity<Void> deleteDeviceCategory(@PathVariable Long id) {
        log.debug("REST request to delete DeviceCategory : {}", id);
        deviceCategoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
