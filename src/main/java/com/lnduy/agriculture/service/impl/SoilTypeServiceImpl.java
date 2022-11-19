package com.lnduy.agriculture.service.impl;

import com.lnduy.agriculture.domain.SoilType;
import com.lnduy.agriculture.repository.SoilTypeRepository;
import com.lnduy.agriculture.service.SoilTypeService;
import com.lnduy.agriculture.service.dto.SoilTypeDTO;
import com.lnduy.agriculture.service.mapper.SoilTypeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SoilType}.
 */
@Service
@Transactional
public class SoilTypeServiceImpl implements SoilTypeService {

    private final Logger log = LoggerFactory.getLogger(SoilTypeServiceImpl.class);

    private final SoilTypeRepository soilTypeRepository;

    private final SoilTypeMapper soilTypeMapper;

    public SoilTypeServiceImpl(SoilTypeRepository soilTypeRepository, SoilTypeMapper soilTypeMapper) {
        this.soilTypeRepository = soilTypeRepository;
        this.soilTypeMapper = soilTypeMapper;
    }

    @Override
    public SoilTypeDTO save(SoilTypeDTO soilTypeDTO) {
        log.debug("Request to save SoilType : {}", soilTypeDTO);
        SoilType soilType = soilTypeMapper.toEntity(soilTypeDTO);
        soilType = soilTypeRepository.save(soilType);
        return soilTypeMapper.toDto(soilType);
    }

    @Override
    public SoilTypeDTO update(SoilTypeDTO soilTypeDTO) {
        log.debug("Request to update SoilType : {}", soilTypeDTO);
        SoilType soilType = soilTypeMapper.toEntity(soilTypeDTO);
        soilType = soilTypeRepository.save(soilType);
        return soilTypeMapper.toDto(soilType);
    }

    @Override
    public Optional<SoilTypeDTO> partialUpdate(SoilTypeDTO soilTypeDTO) {
        log.debug("Request to partially update SoilType : {}", soilTypeDTO);

        return soilTypeRepository
            .findById(soilTypeDTO.getId())
            .map(existingSoilType -> {
                soilTypeMapper.partialUpdate(existingSoilType, soilTypeDTO);

                return existingSoilType;
            })
            .map(soilTypeRepository::save)
            .map(soilTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoilTypeDTO> findAll() {
        log.debug("Request to get all SoilTypes");
        return soilTypeRepository.findAll().stream().map(soilTypeMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SoilTypeDTO> findOne(Long id) {
        log.debug("Request to get SoilType : {}", id);
        return soilTypeRepository.findById(id).map(soilTypeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SoilType : {}", id);
        soilTypeRepository.deleteById(id);
    }
}
