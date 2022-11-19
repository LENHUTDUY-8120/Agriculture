package com.lnduy.agriculture.service.impl;

import com.lnduy.agriculture.domain.DeviceCategory;
import com.lnduy.agriculture.repository.DeviceCategoryRepository;
import com.lnduy.agriculture.service.DeviceCategoryService;
import com.lnduy.agriculture.service.dto.DeviceCategoryDTO;
import com.lnduy.agriculture.service.mapper.DeviceCategoryMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link DeviceCategory}.
 */
@Service
@Transactional
public class DeviceCategoryServiceImpl implements DeviceCategoryService {

    private final Logger log = LoggerFactory.getLogger(DeviceCategoryServiceImpl.class);

    private final DeviceCategoryRepository deviceCategoryRepository;

    private final DeviceCategoryMapper deviceCategoryMapper;

    public DeviceCategoryServiceImpl(DeviceCategoryRepository deviceCategoryRepository, DeviceCategoryMapper deviceCategoryMapper) {
        this.deviceCategoryRepository = deviceCategoryRepository;
        this.deviceCategoryMapper = deviceCategoryMapper;
    }

    @Override
    public DeviceCategoryDTO save(DeviceCategoryDTO deviceCategoryDTO) {
        log.debug("Request to save DeviceCategory : {}", deviceCategoryDTO);
        DeviceCategory deviceCategory = deviceCategoryMapper.toEntity(deviceCategoryDTO);
        deviceCategory = deviceCategoryRepository.save(deviceCategory);
        return deviceCategoryMapper.toDto(deviceCategory);
    }

    @Override
    public DeviceCategoryDTO update(DeviceCategoryDTO deviceCategoryDTO) {
        log.debug("Request to update DeviceCategory : {}", deviceCategoryDTO);
        DeviceCategory deviceCategory = deviceCategoryMapper.toEntity(deviceCategoryDTO);
        deviceCategory = deviceCategoryRepository.save(deviceCategory);
        return deviceCategoryMapper.toDto(deviceCategory);
    }

    @Override
    public Optional<DeviceCategoryDTO> partialUpdate(DeviceCategoryDTO deviceCategoryDTO) {
        log.debug("Request to partially update DeviceCategory : {}", deviceCategoryDTO);

        return deviceCategoryRepository
            .findById(deviceCategoryDTO.getId())
            .map(existingDeviceCategory -> {
                deviceCategoryMapper.partialUpdate(existingDeviceCategory, deviceCategoryDTO);

                return existingDeviceCategory;
            })
            .map(deviceCategoryRepository::save)
            .map(deviceCategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceCategoryDTO> findAll() {
        log.debug("Request to get all DeviceCategories");
        return deviceCategoryRepository
            .findAll()
            .stream()
            .map(deviceCategoryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeviceCategoryDTO> findOne(Long id) {
        log.debug("Request to get DeviceCategory : {}", id);
        return deviceCategoryRepository.findById(id).map(deviceCategoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete DeviceCategory : {}", id);
        deviceCategoryRepository.deleteById(id);
    }
}
