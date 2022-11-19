package com.lnduy.agriculture.service;

import com.lnduy.agriculture.service.dto.MonitoringDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lnduy.agriculture.domain.Monitoring}.
 */
public interface MonitoringService {
    /**
     * Save a monitoring.
     *
     * @param monitoringDTO the entity to save.
     * @return the persisted entity.
     */
    MonitoringDTO save(MonitoringDTO monitoringDTO);

    /**
     * Updates a monitoring.
     *
     * @param monitoringDTO the entity to update.
     * @return the persisted entity.
     */
    MonitoringDTO update(MonitoringDTO monitoringDTO);

    /**
     * Partially updates a monitoring.
     *
     * @param monitoringDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MonitoringDTO> partialUpdate(MonitoringDTO monitoringDTO);

    /**
     * Get all the monitorings.
     *
     * @return the list of entities.
     */
    List<MonitoringDTO> findAll();

    /**
     * Get the "id" monitoring.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MonitoringDTO> findOne(Long id);

    /**
     * Delete the "id" monitoring.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
