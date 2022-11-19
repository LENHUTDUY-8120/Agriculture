package com.lnduy.agriculture.service;

import com.lnduy.agriculture.service.dto.SoilTypeDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lnduy.agriculture.domain.SoilType}.
 */
public interface SoilTypeService {
    /**
     * Save a soilType.
     *
     * @param soilTypeDTO the entity to save.
     * @return the persisted entity.
     */
    SoilTypeDTO save(SoilTypeDTO soilTypeDTO);

    /**
     * Updates a soilType.
     *
     * @param soilTypeDTO the entity to update.
     * @return the persisted entity.
     */
    SoilTypeDTO update(SoilTypeDTO soilTypeDTO);

    /**
     * Partially updates a soilType.
     *
     * @param soilTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SoilTypeDTO> partialUpdate(SoilTypeDTO soilTypeDTO);

    /**
     * Get all the soilTypes.
     *
     * @return the list of entities.
     */
    List<SoilTypeDTO> findAll();

    /**
     * Get the "id" soilType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SoilTypeDTO> findOne(Long id);

    /**
     * Delete the "id" soilType.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
