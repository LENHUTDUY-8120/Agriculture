package com.lnduy.agriculture.service;

import com.lnduy.agriculture.service.dto.FertilizersDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lnduy.agriculture.domain.Fertilizers}.
 */
public interface FertilizersService {
    /**
     * Save a fertilizers.
     *
     * @param fertilizersDTO the entity to save.
     * @return the persisted entity.
     */
    FertilizersDTO save(FertilizersDTO fertilizersDTO);

    /**
     * Updates a fertilizers.
     *
     * @param fertilizersDTO the entity to update.
     * @return the persisted entity.
     */
    FertilizersDTO update(FertilizersDTO fertilizersDTO);

    /**
     * Partially updates a fertilizers.
     *
     * @param fertilizersDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FertilizersDTO> partialUpdate(FertilizersDTO fertilizersDTO);

    /**
     * Get all the fertilizers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FertilizersDTO> findAll(Pageable pageable);

    /**
     * Get the "id" fertilizers.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FertilizersDTO> findOne(Long id);

    /**
     * Delete the "id" fertilizers.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
