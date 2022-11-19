package com.lnduy.agriculture.service;

import com.lnduy.agriculture.service.dto.SeasonDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lnduy.agriculture.domain.Season}.
 */
public interface SeasonService {
    /**
     * Save a season.
     *
     * @param seasonDTO the entity to save.
     * @return the persisted entity.
     */
    SeasonDTO save(SeasonDTO seasonDTO);

    /**
     * Updates a season.
     *
     * @param seasonDTO the entity to update.
     * @return the persisted entity.
     */
    SeasonDTO update(SeasonDTO seasonDTO);

    /**
     * Partially updates a season.
     *
     * @param seasonDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SeasonDTO> partialUpdate(SeasonDTO seasonDTO);

    /**
     * Get all the seasons.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SeasonDTO> findAll(Pageable pageable);

    /**
     * Get the "id" season.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SeasonDTO> findOne(Long id);

    /**
     * Delete the "id" season.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
