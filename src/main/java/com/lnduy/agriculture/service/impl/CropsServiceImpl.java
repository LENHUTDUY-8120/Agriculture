package com.lnduy.agriculture.service.impl;

import com.lnduy.agriculture.domain.Crops;
import com.lnduy.agriculture.repository.CropsRepository;
import com.lnduy.agriculture.service.CropsService;
import com.lnduy.agriculture.service.dto.CropsDTO;
import com.lnduy.agriculture.service.mapper.CropsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Crops}.
 */
@Service
@Transactional
public class CropsServiceImpl implements CropsService {

    private final Logger log = LoggerFactory.getLogger(CropsServiceImpl.class);

    private final CropsRepository cropsRepository;

    private final CropsMapper cropsMapper;

    public CropsServiceImpl(CropsRepository cropsRepository, CropsMapper cropsMapper) {
        this.cropsRepository = cropsRepository;
        this.cropsMapper = cropsMapper;
    }

    @Override
    public CropsDTO save(CropsDTO cropsDTO) {
        log.debug("Request to save Crops : {}", cropsDTO);
        Crops crops = cropsMapper.toEntity(cropsDTO);
        crops = cropsRepository.save(crops);
        return cropsMapper.toDto(crops);
    }

    @Override
    public CropsDTO update(CropsDTO cropsDTO) {
        log.debug("Request to update Crops : {}", cropsDTO);
        Crops crops = cropsMapper.toEntity(cropsDTO);
        crops = cropsRepository.save(crops);
        return cropsMapper.toDto(crops);
    }

    @Override
    public Optional<CropsDTO> partialUpdate(CropsDTO cropsDTO) {
        log.debug("Request to partially update Crops : {}", cropsDTO);

        return cropsRepository
            .findById(cropsDTO.getId())
            .map(existingCrops -> {
                cropsMapper.partialUpdate(existingCrops, cropsDTO);

                return existingCrops;
            })
            .map(cropsRepository::save)
            .map(cropsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CropsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Crops");
        return cropsRepository.findAll(pageable).map(cropsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CropsDTO> findOne(Long id) {
        log.debug("Request to get Crops : {}", id);
        return cropsRepository.findById(id).map(cropsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Crops : {}", id);
        cropsRepository.deleteById(id);
    }
}
