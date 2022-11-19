package com.lnduy.agriculture.service.impl;

import com.lnduy.agriculture.domain.EventCategory;
import com.lnduy.agriculture.repository.EventCategoryRepository;
import com.lnduy.agriculture.service.EventCategoryService;
import com.lnduy.agriculture.service.dto.EventCategoryDTO;
import com.lnduy.agriculture.service.mapper.EventCategoryMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link EventCategory}.
 */
@Service
@Transactional
public class EventCategoryServiceImpl implements EventCategoryService {

    private final Logger log = LoggerFactory.getLogger(EventCategoryServiceImpl.class);

    private final EventCategoryRepository eventCategoryRepository;

    private final EventCategoryMapper eventCategoryMapper;

    public EventCategoryServiceImpl(EventCategoryRepository eventCategoryRepository, EventCategoryMapper eventCategoryMapper) {
        this.eventCategoryRepository = eventCategoryRepository;
        this.eventCategoryMapper = eventCategoryMapper;
    }

    @Override
    public EventCategoryDTO save(EventCategoryDTO eventCategoryDTO) {
        log.debug("Request to save EventCategory : {}", eventCategoryDTO);
        EventCategory eventCategory = eventCategoryMapper.toEntity(eventCategoryDTO);
        eventCategory = eventCategoryRepository.save(eventCategory);
        return eventCategoryMapper.toDto(eventCategory);
    }

    @Override
    public EventCategoryDTO update(EventCategoryDTO eventCategoryDTO) {
        log.debug("Request to update EventCategory : {}", eventCategoryDTO);
        EventCategory eventCategory = eventCategoryMapper.toEntity(eventCategoryDTO);
        eventCategory = eventCategoryRepository.save(eventCategory);
        return eventCategoryMapper.toDto(eventCategory);
    }

    @Override
    public Optional<EventCategoryDTO> partialUpdate(EventCategoryDTO eventCategoryDTO) {
        log.debug("Request to partially update EventCategory : {}", eventCategoryDTO);

        return eventCategoryRepository
            .findById(eventCategoryDTO.getId())
            .map(existingEventCategory -> {
                eventCategoryMapper.partialUpdate(existingEventCategory, eventCategoryDTO);

                return existingEventCategory;
            })
            .map(eventCategoryRepository::save)
            .map(eventCategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventCategoryDTO> findAll() {
        log.debug("Request to get all EventCategories");
        return eventCategoryRepository.findAll().stream().map(eventCategoryMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventCategoryDTO> findOne(Long id) {
        log.debug("Request to get EventCategory : {}", id);
        return eventCategoryRepository.findById(id).map(eventCategoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete EventCategory : {}", id);
        eventCategoryRepository.deleteById(id);
    }
}
