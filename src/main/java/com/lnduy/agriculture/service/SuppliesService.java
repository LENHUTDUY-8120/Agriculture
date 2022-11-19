package com.lnduy.agriculture.service;

import com.lnduy.agriculture.service.dto.SuppliesDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lnduy.agriculture.domain.Supplies}.
 */
public interface SuppliesService {
    /**
     * Save a supplies.
     *
     * @param suppliesDTO the entity to save.
     * @return the persisted entity.
     */
    SuppliesDTO save(SuppliesDTO suppliesDTO);

    /**
     * Updates a supplies.
     *
     * @param suppliesDTO the entity to update.
     * @return the persisted entity.
     */
    SuppliesDTO update(SuppliesDTO suppliesDTO);

    /**
     * Partially updates a supplies.
     *
     * @param suppliesDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SuppliesDTO> partialUpdate(SuppliesDTO suppliesDTO);

    /**
     * Get all the supplies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SuppliesDTO> findAll(Pageable pageable);

    /**
     * Get the "id" supplies.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SuppliesDTO> findOne(Long id);

    /**
     * Delete the "id" supplies.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
