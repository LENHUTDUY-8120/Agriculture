package com.lnduy.agriculture.service;

import com.lnduy.agriculture.service.dto.CropsDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lnduy.agriculture.domain.Crops}.
 */
public interface CropsService {
    /**
     * Save a crops.
     *
     * @param cropsDTO the entity to save.
     * @return the persisted entity.
     */
    CropsDTO save(CropsDTO cropsDTO);

    /**
     * Updates a crops.
     *
     * @param cropsDTO the entity to update.
     * @return the persisted entity.
     */
    CropsDTO update(CropsDTO cropsDTO);

    /**
     * Partially updates a crops.
     *
     * @param cropsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CropsDTO> partialUpdate(CropsDTO cropsDTO);

    /**
     * Get all the crops.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CropsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" crops.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CropsDTO> findOne(Long id);

    /**
     * Delete the "id" crops.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
