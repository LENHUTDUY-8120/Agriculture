package com.lnduy.agriculture.service.impl;

import com.lnduy.agriculture.domain.Supplies;
import com.lnduy.agriculture.repository.SuppliesRepository;
import com.lnduy.agriculture.service.SuppliesService;
import com.lnduy.agriculture.service.dto.SuppliesDTO;
import com.lnduy.agriculture.service.mapper.SuppliesMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Supplies}.
 */
@Service
@Transactional
public class SuppliesServiceImpl implements SuppliesService {

    private final Logger log = LoggerFactory.getLogger(SuppliesServiceImpl.class);

    private final SuppliesRepository suppliesRepository;

    private final SuppliesMapper suppliesMapper;

    public SuppliesServiceImpl(SuppliesRepository suppliesRepository, SuppliesMapper suppliesMapper) {
        this.suppliesRepository = suppliesRepository;
        this.suppliesMapper = suppliesMapper;
    }

    @Override
    public SuppliesDTO save(SuppliesDTO suppliesDTO) {
        log.debug("Request to save Supplies : {}", suppliesDTO);
        Supplies supplies = suppliesMapper.toEntity(suppliesDTO);
        supplies = suppliesRepository.save(supplies);
        return suppliesMapper.toDto(supplies);
    }

    @Override
    public SuppliesDTO update(SuppliesDTO suppliesDTO) {
        log.debug("Request to update Supplies : {}", suppliesDTO);
        Supplies supplies = suppliesMapper.toEntity(suppliesDTO);
        supplies = suppliesRepository.save(supplies);
        return suppliesMapper.toDto(supplies);
    }

    @Override
    public Optional<SuppliesDTO> partialUpdate(SuppliesDTO suppliesDTO) {
        log.debug("Request to partially update Supplies : {}", suppliesDTO);

        return suppliesRepository
            .findById(suppliesDTO.getId())
            .map(existingSupplies -> {
                suppliesMapper.partialUpdate(existingSupplies, suppliesDTO);

                return existingSupplies;
            })
            .map(suppliesRepository::save)
            .map(suppliesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SuppliesDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Supplies");
        return suppliesRepository.findAll(pageable).map(suppliesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SuppliesDTO> findOne(Long id) {
        log.debug("Request to get Supplies : {}", id);
        return suppliesRepository.findById(id).map(suppliesMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Supplies : {}", id);
        suppliesRepository.deleteById(id);
    }
}
