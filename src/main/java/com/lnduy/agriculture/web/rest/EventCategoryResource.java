package com.lnduy.agriculture.web.rest;

import com.lnduy.agriculture.repository.EventCategoryRepository;
import com.lnduy.agriculture.service.EventCategoryQueryService;
import com.lnduy.agriculture.service.EventCategoryService;
import com.lnduy.agriculture.service.criteria.EventCategoryCriteria;
import com.lnduy.agriculture.service.dto.EventCategoryDTO;
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
 * REST controller for managing {@link com.lnduy.agriculture.domain.EventCategory}.
 */
@RestController
@RequestMapping("/api")
public class EventCategoryResource {

    private final Logger log = LoggerFactory.getLogger(EventCategoryResource.class);

    private static final String ENTITY_NAME = "eventCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventCategoryService eventCategoryService;

    private final EventCategoryRepository eventCategoryRepository;

    private final EventCategoryQueryService eventCategoryQueryService;

    public EventCategoryResource(
        EventCategoryService eventCategoryService,
        EventCategoryRepository eventCategoryRepository,
        EventCategoryQueryService eventCategoryQueryService
    ) {
        this.eventCategoryService = eventCategoryService;
        this.eventCategoryRepository = eventCategoryRepository;
        this.eventCategoryQueryService = eventCategoryQueryService;
    }

    /**
     * {@code POST  /event-categories} : Create a new eventCategory.
     *
     * @param eventCategoryDTO the eventCategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventCategoryDTO, or with status {@code 400 (Bad Request)} if the eventCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/event-categories")
    public ResponseEntity<EventCategoryDTO> createEventCategory(@RequestBody EventCategoryDTO eventCategoryDTO) throws URISyntaxException {
        log.debug("REST request to save EventCategory : {}", eventCategoryDTO);
        if (eventCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EventCategoryDTO result = eventCategoryService.save(eventCategoryDTO);
        return ResponseEntity
            .created(new URI("/api/event-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /event-categories/:id} : Updates an existing eventCategory.
     *
     * @param id the id of the eventCategoryDTO to save.
     * @param eventCategoryDTO the eventCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the eventCategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/event-categories/{id}")
    public ResponseEntity<EventCategoryDTO> updateEventCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EventCategoryDTO eventCategoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update EventCategory : {}, {}", id, eventCategoryDTO);
        if (eventCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        EventCategoryDTO result = eventCategoryService.update(eventCategoryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventCategoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /event-categories/:id} : Partial updates given fields of an existing eventCategory, field will ignore if it is null
     *
     * @param id the id of the eventCategoryDTO to save.
     * @param eventCategoryDTO the eventCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the eventCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the eventCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the eventCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/event-categories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EventCategoryDTO> partialUpdateEventCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EventCategoryDTO eventCategoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update EventCategory partially : {}, {}", id, eventCategoryDTO);
        if (eventCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EventCategoryDTO> result = eventCategoryService.partialUpdate(eventCategoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventCategoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /event-categories} : get all the eventCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventCategories in body.
     */
    @GetMapping("/event-categories")
    public ResponseEntity<List<EventCategoryDTO>> getAllEventCategories(EventCategoryCriteria criteria) {
        log.debug("REST request to get EventCategories by criteria: {}", criteria);
        List<EventCategoryDTO> entityList = eventCategoryQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /event-categories/count} : count all the eventCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/event-categories/count")
    public ResponseEntity<Long> countEventCategories(EventCategoryCriteria criteria) {
        log.debug("REST request to count EventCategories by criteria: {}", criteria);
        return ResponseEntity.ok().body(eventCategoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /event-categories/:id} : get the "id" eventCategory.
     *
     * @param id the id of the eventCategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventCategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/event-categories/{id}")
    public ResponseEntity<EventCategoryDTO> getEventCategory(@PathVariable Long id) {
        log.debug("REST request to get EventCategory : {}", id);
        Optional<EventCategoryDTO> eventCategoryDTO = eventCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventCategoryDTO);
    }

    /**
     * {@code DELETE  /event-categories/:id} : delete the "id" eventCategory.
     *
     * @param id the id of the eventCategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/event-categories/{id}")
    public ResponseEntity<Void> deleteEventCategory(@PathVariable Long id) {
        log.debug("REST request to delete EventCategory : {}", id);
        eventCategoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
