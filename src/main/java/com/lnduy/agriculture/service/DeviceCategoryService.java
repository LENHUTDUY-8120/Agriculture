package com.lnduy.agriculture.service;

import com.lnduy.agriculture.service.dto.DeviceCategoryDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lnduy.agriculture.domain.DeviceCategory}.
 */
public interface DeviceCategoryService {
    /**
     * Save a deviceCategory.
     *
     * @param deviceCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    DeviceCategoryDTO save(DeviceCategoryDTO deviceCategoryDTO);

    /**
     * Updates a deviceCategory.
     *
     * @param deviceCategoryDTO the entity to update.
     * @return the persisted entity.
     */
    DeviceCategoryDTO update(DeviceCategoryDTO deviceCategoryDTO);

    /**
     * Partially updates a deviceCategory.
     *
     * @param deviceCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DeviceCategoryDTO> partialUpdate(DeviceCategoryDTO deviceCategoryDTO);

    /**
     * Get all the deviceCategories.
     *
     * @return the list of entities.
     */
    List<DeviceCategoryDTO> findAll();

    /**
     * Get the "id" deviceCategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DeviceCategoryDTO> findOne(Long id);

    /**
     * Delete the "id" deviceCategory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
