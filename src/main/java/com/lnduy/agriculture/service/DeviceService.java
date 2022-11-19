package com.lnduy.agriculture.service;

import com.lnduy.agriculture.service.dto.DeviceDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lnduy.agriculture.domain.Device}.
 */
public interface DeviceService {
    /**
     * Save a device.
     *
     * @param deviceDTO the entity to save.
     * @return the persisted entity.
     */
    DeviceDTO save(DeviceDTO deviceDTO);

    /**
     * Updates a device.
     *
     * @param deviceDTO the entity to update.
     * @return the persisted entity.
     */
    DeviceDTO update(DeviceDTO deviceDTO);

    /**
     * Partially updates a device.
     *
     * @param deviceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DeviceDTO> partialUpdate(DeviceDTO deviceDTO);

    /**
     * Get all the devices.
     *
     * @return the list of entities.
     */
    List<DeviceDTO> findAll();

    /**
     * Get the "id" device.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DeviceDTO> findOne(Long id);

    /**
     * Delete the "id" device.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
