package com.lnduy.agriculture.service;

import com.lnduy.agriculture.domain.*; // for static metamodels
import com.lnduy.agriculture.domain.Field;
import com.lnduy.agriculture.repository.FieldRepository;
import com.lnduy.agriculture.service.criteria.FieldCriteria;
import com.lnduy.agriculture.service.dto.FieldDTO;
import com.lnduy.agriculture.service.mapper.FieldMapper;
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
 * Service for executing complex queries for {@link Field} entities in the database.
 * The main input is a {@link FieldCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FieldDTO} or a {@link Page} of {@link FieldDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FieldQueryService extends QueryService<Field> {

    private final Logger log = LoggerFactory.getLogger(FieldQueryService.class);

    private final FieldRepository fieldRepository;

    private final FieldMapper fieldMapper;

    public FieldQueryService(FieldRepository fieldRepository, FieldMapper fieldMapper) {
        this.fieldRepository = fieldRepository;
        this.fieldMapper = fieldMapper;
    }

    /**
     * Return a {@link List} of {@link FieldDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FieldDTO> findByCriteria(FieldCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Field> specification = createSpecification(criteria);
        return fieldMapper.toDto(fieldRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FieldDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FieldDTO> findByCriteria(FieldCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Field> specification = createSpecification(criteria);
        return fieldRepository.findAll(specification, page).map(fieldMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FieldCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Field> specification = createSpecification(criteria);
        return fieldRepository.count(specification);
    }

    /**
     * Function to convert {@link FieldCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Field> createSpecification(FieldCriteria criteria) {
        Specification<Field> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Field_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Field_.code));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Field_.name));
            }
            if (criteria.getGeoJson() != null) {
                specification = specification.and(buildStringSpecification(criteria.getGeoJson(), Field_.geoJson));
            }
            if (criteria.getArea() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getArea(), Field_.area));
            }
            if (criteria.getDescriptions() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescriptions(), Field_.descriptions));
            }
            if (criteria.getEnable() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEnable(), Field_.enable));
            }
            if (criteria.getLatitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLatitude(), Field_.latitude));
            }
            if (criteria.getLongitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLongitude(), Field_.longitude));
            }
            if (criteria.getSeasonId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSeasonId(), root -> root.join(Field_.seasons, JoinType.LEFT).get(Season_.id))
                    );
            }
            if (criteria.getDeviceId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDeviceId(), root -> root.join(Field_.devices, JoinType.LEFT).get(Device_.id))
                    );
            }
            if (criteria.getSoilId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSoilId(), root -> root.join(Field_.soil, JoinType.LEFT).get(SoilType_.id))
                    );
            }
        }
        return specification;
    }
}
