package com.lnduy.agriculture.service.impl;

import com.lnduy.agriculture.domain.Fertilizers;
import com.lnduy.agriculture.repository.FertilizersRepository;
import com.lnduy.agriculture.service.FertilizersService;
import com.lnduy.agriculture.service.dto.FertilizersDTO;
import com.lnduy.agriculture.service.mapper.FertilizersMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Fertilizers}.
 */
@Service
@Transactional
public class FertilizersServiceImpl implements FertilizersService {

    private final Logger log = LoggerFactory.getLogger(FertilizersServiceImpl.class);

    private final FertilizersRepository fertilizersRepository;

    private final FertilizersMapper fertilizersMapper;

    public FertilizersServiceImpl(FertilizersRepository fertilizersRepository, FertilizersMapper fertilizersMapper) {
        this.fertilizersRepository = fertilizersRepository;
        this.fertilizersMapper = fertilizersMapper;
    }

    @Override
    public FertilizersDTO save(FertilizersDTO fertilizersDTO) {
        log.debug("Request to save Fertilizers : {}", fertilizersDTO);
        Fertilizers fertilizers = fertilizersMapper.toEntity(fertilizersDTO);
        fertilizers = fertilizersRepository.save(fertilizers);
        return fertilizersMapper.toDto(fertilizers);
    }

    @Override
    public FertilizersDTO update(FertilizersDTO fertilizersDTO) {
        log.debug("Request to update Fertilizers : {}", fertilizersDTO);
        Fertilizers fertilizers = fertilizersMapper.toEntity(fertilizersDTO);
        fertilizers = fertilizersRepository.save(fertilizers);
        return fertilizersMapper.toDto(fertilizers);
    }

    @Override
    public Optional<FertilizersDTO> partialUpdate(FertilizersDTO fertilizersDTO) {
        log.debug("Request to partially update Fertilizers : {}", fertilizersDTO);

        return fertilizersRepository
            .findById(fertilizersDTO.getId())
            .map(existingFertilizers -> {
                fertilizersMapper.partialUpdate(existingFertilizers, fertilizersDTO);

                return existingFertilizers;
            })
            .map(fertilizersRepository::save)
            .map(fertilizersMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FertilizersDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Fertilizers");
        return fertilizersRepository.findAll(pageable).map(fertilizersMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FertilizersDTO> findOne(Long id) {
        log.debug("Request to get Fertilizers : {}", id);
        return fertilizersRepository.findById(id).map(fertilizersMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Fertilizers : {}", id);
        fertilizersRepository.deleteById(id);
    }
}
