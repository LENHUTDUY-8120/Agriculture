package com.lnduy.agriculture.service;

import com.lnduy.agriculture.service.dto.EventCategoryDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lnduy.agriculture.domain.EventCategory}.
 */
public interface EventCategoryService {
    /**
     * Save a eventCategory.
     *
     * @param eventCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    EventCategoryDTO save(EventCategoryDTO eventCategoryDTO);

    /**
     * Updates a eventCategory.
     *
     * @param eventCategoryDTO the entity to update.
     * @return the persisted entity.
     */
    EventCategoryDTO update(EventCategoryDTO eventCategoryDTO);

    /**
     * Partially updates a eventCategory.
     *
     * @param eventCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<EventCategoryDTO> partialUpdate(EventCategoryDTO eventCategoryDTO);

    /**
     * Get all the eventCategories.
     *
     * @return the list of entities.
     */
    List<EventCategoryDTO> findAll();

    /**
     * Get the "id" eventCategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventCategoryDTO> findOne(Long id);

    /**
     * Delete the "id" eventCategory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
