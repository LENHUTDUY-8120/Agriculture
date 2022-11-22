package com.lnduy.agriculture.service;

import com.lnduy.agriculture.domain.*; // for static metamodels
import com.lnduy.agriculture.domain.EventCategory;
import com.lnduy.agriculture.repository.EventCategoryRepository;
import com.lnduy.agriculture.service.criteria.EventCategoryCriteria;
import com.lnduy.agriculture.service.dto.EventCategoryDTO;
import com.lnduy.agriculture.service.mapper.EventCategoryMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link EventCategory} entities in the database.
 * The main input is a {@link EventCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EventCategoryDTO} or a {@link Page} of {@link EventCategoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventCategoryQueryService extends QueryService<EventCategory> {

    private final Logger log = LoggerFactory.getLogger(EventCategoryQueryService.class);

    private final EventCategoryRepository eventCategoryRepository;

    private final EventCategoryMapper eventCategoryMapper;

    public EventCategoryQueryService(EventCategoryRepository eventCategoryRepository, EventCategoryMapper eventCategoryMapper) {
        this.eventCategoryRepository = eventCategoryRepository;
        this.eventCategoryMapper = eventCategoryMapper;
    }

    /**
     * Return a {@link List} of {@link EventCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EventCategoryDTO> findByCriteria(EventCategoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<EventCategory> specification = createSpecification(criteria);
        return eventCategoryMapper.toDto(eventCategoryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EventCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EventCategoryDTO> findByCriteria(EventCategoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<EventCategory> specification = createSpecification(criteria);
        return eventCategoryRepository.findAll(specification, page).map(eventCategoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EventCategoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<EventCategory> specification = createSpecification(criteria);
        return eventCategoryRepository.count(specification);
    }

    /**
     * Function to convert {@link EventCategoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<EventCategory> createSpecification(EventCategoryCriteria criteria) {
        Specification<EventCategory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), EventCategory_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), EventCategory_.name));
            }
        }
        return specification;
    }
}
