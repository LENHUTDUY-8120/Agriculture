package com.lnduy.agriculture.service.impl;

import com.lnduy.agriculture.domain.Field;
import com.lnduy.agriculture.repository.FieldRepository;
import com.lnduy.agriculture.service.FieldService;
import com.lnduy.agriculture.service.dto.FieldDTO;
import com.lnduy.agriculture.service.mapper.FieldMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Field}.
 */
@Service
@Transactional
public class FieldServiceImpl implements FieldService {

    private final Logger log = LoggerFactory.getLogger(FieldServiceImpl.class);

    private final FieldRepository fieldRepository;

    private final FieldMapper fieldMapper;

    public FieldServiceImpl(FieldRepository fieldRepository, FieldMapper fieldMapper) {
        this.fieldRepository = fieldRepository;
        this.fieldMapper = fieldMapper;
    }

    @Override
    public FieldDTO save(FieldDTO fieldDTO) {
        log.debug("Request to save Field : {}", fieldDTO);
        Field field = fieldMapper.toEntity(fieldDTO);
        field = fieldRepository.save(field);
        return fieldMapper.toDto(field);
    }

    @Override
    public FieldDTO update(FieldDTO fieldDTO) {
        log.debug("Request to update Field : {}", fieldDTO);
        Field field = fieldMapper.toEntity(fieldDTO);
        field = fieldRepository.save(field);
        return fieldMapper.toDto(field);
    }

    @Override
    public Optional<FieldDTO> partialUpdate(FieldDTO fieldDTO) {
        log.debug("Request to partially update Field : {}", fieldDTO);

        return fieldRepository
            .findById(fieldDTO.getId())
            .map(existingField -> {
                fieldMapper.partialUpdate(existingField, fieldDTO);

                return existingField;
            })
            .map(fieldRepository::save)
            .map(fieldMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldDTO> findAll() {
        log.debug("Request to get all Fields");
        return fieldRepository.findAll().stream().map(fieldMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FieldDTO> findOne(Long id) {
        log.debug("Request to get Field : {}", id);
        return fieldRepository.findById(id).map(fieldMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Field : {}", id);
        fieldRepository.deleteById(id);
    }
}
