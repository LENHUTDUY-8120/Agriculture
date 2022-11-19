package com.lnduy.agriculture.service;

import com.lnduy.agriculture.service.dto.ProtectionProductDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lnduy.agriculture.domain.ProtectionProduct}.
 */
public interface ProtectionProductService {
    /**
     * Save a protectionProduct.
     *
     * @param protectionProductDTO the entity to save.
     * @return the persisted entity.
     */
    ProtectionProductDTO save(ProtectionProductDTO protectionProductDTO);

    /**
     * Updates a protectionProduct.
     *
     * @param protectionProductDTO the entity to update.
     * @return the persisted entity.
     */
    ProtectionProductDTO update(ProtectionProductDTO protectionProductDTO);

    /**
     * Partially updates a protectionProduct.
     *
     * @param protectionProductDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProtectionProductDTO> partialUpdate(ProtectionProductDTO protectionProductDTO);

    /**
     * Get all the protectionProducts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProtectionProductDTO> findAll(Pageable pageable);

    /**
     * Get the "id" protectionProduct.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProtectionProductDTO> findOne(Long id);

    /**
     * Delete the "id" protectionProduct.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
