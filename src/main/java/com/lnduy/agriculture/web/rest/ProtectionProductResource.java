package com.lnduy.agriculture.web.rest;

import com.lnduy.agriculture.repository.ProtectionProductRepository;
import com.lnduy.agriculture.service.ProtectionProductService;
import com.lnduy.agriculture.service.dto.ProtectionProductDTO;
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
 * REST controller for managing {@link com.lnduy.agriculture.domain.ProtectionProduct}.
 */
@RestController
@RequestMapping("/api")
public class ProtectionProductResource {

    private final Logger log = LoggerFactory.getLogger(ProtectionProductResource.class);

    private static final String ENTITY_NAME = "protectionProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProtectionProductService protectionProductService;

    private final ProtectionProductRepository protectionProductRepository;

    public ProtectionProductResource(
        ProtectionProductService protectionProductService,
        ProtectionProductRepository protectionProductRepository
    ) {
        this.protectionProductService = protectionProductService;
        this.protectionProductRepository = protectionProductRepository;
    }

    /**
     * {@code POST  /protection-products} : Create a new protectionProduct.
     *
     * @param protectionProductDTO the protectionProductDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new protectionProductDTO, or with status {@code 400 (Bad Request)} if the protectionProduct has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/protection-products")
    public ResponseEntity<ProtectionProductDTO> createProtectionProduct(@RequestBody ProtectionProductDTO protectionProductDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProtectionProduct : {}", protectionProductDTO);
        if (protectionProductDTO.getId() != null) {
            throw new BadRequestAlertException("A new protectionProduct cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProtectionProductDTO result = protectionProductService.save(protectionProductDTO);
        return ResponseEntity
            .created(new URI("/api/protection-products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /protection-products/:id} : Updates an existing protectionProduct.
     *
     * @param id the id of the protectionProductDTO to save.
     * @param protectionProductDTO the protectionProductDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated protectionProductDTO,
     * or with status {@code 400 (Bad Request)} if the protectionProductDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the protectionProductDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/protection-products/{id}")
    public ResponseEntity<ProtectionProductDTO> updateProtectionProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProtectionProductDTO protectionProductDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProtectionProduct : {}, {}", id, protectionProductDTO);
        if (protectionProductDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, protectionProductDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!protectionProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProtectionProductDTO result = protectionProductService.update(protectionProductDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, protectionProductDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /protection-products/:id} : Partial updates given fields of an existing protectionProduct, field will ignore if it is null
     *
     * @param id the id of the protectionProductDTO to save.
     * @param protectionProductDTO the protectionProductDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated protectionProductDTO,
     * or with status {@code 400 (Bad Request)} if the protectionProductDTO is not valid,
     * or with status {@code 404 (Not Found)} if the protectionProductDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the protectionProductDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/protection-products/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProtectionProductDTO> partialUpdateProtectionProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProtectionProductDTO protectionProductDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProtectionProduct partially : {}, {}", id, protectionProductDTO);
        if (protectionProductDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, protectionProductDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!protectionProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProtectionProductDTO> result = protectionProductService.partialUpdate(protectionProductDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, protectionProductDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /protection-products} : get all the protectionProducts.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of protectionProducts in body.
     */
    @GetMapping("/protection-products")
    public ResponseEntity<List<ProtectionProductDTO>> getAllProtectionProducts(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of ProtectionProducts");
        Page<ProtectionProductDTO> page = protectionProductService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /protection-products/:id} : get the "id" protectionProduct.
     *
     * @param id the id of the protectionProductDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the protectionProductDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/protection-products/{id}")
    public ResponseEntity<ProtectionProductDTO> getProtectionProduct(@PathVariable Long id) {
        log.debug("REST request to get ProtectionProduct : {}", id);
        Optional<ProtectionProductDTO> protectionProductDTO = protectionProductService.findOne(id);
        return ResponseUtil.wrapOrNotFound(protectionProductDTO);
    }

    /**
     * {@code DELETE  /protection-products/:id} : delete the "id" protectionProduct.
     *
     * @param id the id of the protectionProductDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/protection-products/{id}")
    public ResponseEntity<Void> deleteProtectionProduct(@PathVariable Long id) {
        log.debug("REST request to delete ProtectionProduct : {}", id);
        protectionProductService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
