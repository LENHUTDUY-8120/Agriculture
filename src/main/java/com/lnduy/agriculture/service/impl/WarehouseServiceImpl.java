package com.lnduy.agriculture.service.impl;

import com.lnduy.agriculture.domain.Warehouse;
import com.lnduy.agriculture.repository.WarehouseRepository;
import com.lnduy.agriculture.service.WarehouseService;
import com.lnduy.agriculture.service.dto.WarehouseDTO;
import com.lnduy.agriculture.service.mapper.WarehouseMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Warehouse}.
 */
@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final Logger log = LoggerFactory.getLogger(WarehouseServiceImpl.class);

    private final WarehouseRepository warehouseRepository;

    private final WarehouseMapper warehouseMapper;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }

    @Override
    public WarehouseDTO save(WarehouseDTO warehouseDTO) {
        log.debug("Request to save Warehouse : {}", warehouseDTO);
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDTO);
        warehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    public WarehouseDTO update(WarehouseDTO warehouseDTO) {
        log.debug("Request to update Warehouse : {}", warehouseDTO);
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDTO);
        warehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    public Optional<WarehouseDTO> partialUpdate(WarehouseDTO warehouseDTO) {
        log.debug("Request to partially update Warehouse : {}", warehouseDTO);

        return warehouseRepository
            .findById(warehouseDTO.getId())
            .map(existingWarehouse -> {
                warehouseMapper.partialUpdate(existingWarehouse, warehouseDTO);

                return existingWarehouse;
            })
            .map(warehouseRepository::save)
            .map(warehouseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDTO> findAll() {
        log.debug("Request to get all Warehouses");
        return warehouseRepository.findAll().stream().map(warehouseMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseDTO> findOne(Long id) {
        log.debug("Request to get Warehouse : {}", id);
        return warehouseRepository.findById(id).map(warehouseMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Warehouse : {}", id);
        warehouseRepository.deleteById(id);
    }
}
