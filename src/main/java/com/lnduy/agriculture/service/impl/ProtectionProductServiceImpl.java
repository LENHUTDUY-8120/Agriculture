package com.lnduy.agriculture.service.impl;

import com.lnduy.agriculture.domain.ProtectionProduct;
import com.lnduy.agriculture.repository.ProtectionProductRepository;
import com.lnduy.agriculture.service.ProtectionProductService;
import com.lnduy.agriculture.service.dto.ProtectionProductDTO;
import com.lnduy.agriculture.service.mapper.ProtectionProductMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProtectionProduct}.
 */
@Service
@Transactional
public class ProtectionProductServiceImpl implements ProtectionProductService {

    private final Logger log = LoggerFactory.getLogger(ProtectionProductServiceImpl.class);

    private final ProtectionProductRepository protectionProductRepository;

    private final ProtectionProductMapper protectionProductMapper;

    public ProtectionProductServiceImpl(
        ProtectionProductRepository protectionProductRepository,
        ProtectionProductMapper protectionProductMapper
    ) {
        this.protectionProductRepository = protectionProductRepository;
        this.protectionProductMapper = protectionProductMapper;
    }

    @Override
    public ProtectionProductDTO save(ProtectionProductDTO protectionProductDTO) {
        log.debug("Request to save ProtectionProduct : {}", protectionProductDTO);
        ProtectionProduct protectionProduct = protectionProductMapper.toEntity(protectionProductDTO);
        protectionProduct = protectionProductRepository.save(protectionProduct);
        return protectionProductMapper.toDto(protectionProduct);
    }

    @Override
    public ProtectionProductDTO update(ProtectionProductDTO protectionProductDTO) {
        log.debug("Request to update ProtectionProduct : {}", protectionProductDTO);
        ProtectionProduct protectionProduct = protectionProductMapper.toEntity(protectionProductDTO);
        protectionProduct = protectionProductRepository.save(protectionProduct);
        return protectionProductMapper.toDto(protectionProduct);
    }

    @Override
    public Optional<ProtectionProductDTO> partialUpdate(ProtectionProductDTO protectionProductDTO) {
        log.debug("Request to partially update ProtectionProduct : {}", protectionProductDTO);

        return protectionProductRepository
            .findById(protectionProductDTO.getId())
            .map(existingProtectionProduct -> {
                protectionProductMapper.partialUpdate(existingProtectionProduct, protectionProductDTO);

                return existingProtectionProduct;
            })
            .map(protectionProductRepository::save)
            .map(protectionProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProtectionProductDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProtectionProducts");
        return protectionProductRepository.findAll(pageable).map(protectionProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProtectionProductDTO> findOne(Long id) {
        log.debug("Request to get ProtectionProduct : {}", id);
        return protectionProductRepository.findById(id).map(protectionProductMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProtectionProduct : {}", id);
        protectionProductRepository.deleteById(id);
    }
}
