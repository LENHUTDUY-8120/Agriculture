package com.lnduy.agriculture.service;

import com.lnduy.agriculture.domain.*; // for static metamodels
import com.lnduy.agriculture.domain.SoilType;
import com.lnduy.agriculture.repository.SoilTypeRepository;
import com.lnduy.agriculture.service.criteria.SoilTypeCriteria;
import com.lnduy.agriculture.service.dto.SoilTypeDTO;
import com.lnduy.agriculture.service.mapper.SoilTypeMapper;
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
 * Service for executing complex queries for {@link SoilType} entities in the database.
 * The main input is a {@link SoilTypeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SoilTypeDTO} or a {@link Page} of {@link SoilTypeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SoilTypeQueryService extends QueryService<SoilType> {

    private final Logger log = LoggerFactory.getLogger(SoilTypeQueryService.class);

    private final SoilTypeRepository soilTypeRepository;

    private final SoilTypeMapper soilTypeMapper;

    public SoilTypeQueryService(SoilTypeRepository soilTypeRepository, SoilTypeMapper soilTypeMapper) {
        this.soilTypeRepository = soilTypeRepository;
        this.soilTypeMapper = soilTypeMapper;
    }

    /**
     * Return a {@link List} of {@link SoilTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SoilTypeDTO> findByCriteria(SoilTypeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SoilType> specification = createSpecification(criteria);
        return soilTypeMapper.toDto(soilTypeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SoilTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SoilTypeDTO> findByCriteria(SoilTypeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SoilType> specification = createSpecification(criteria);
        return soilTypeRepository.findAll(specification, page).map(soilTypeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SoilTypeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SoilType> specification = createSpecification(criteria);
        return soilTypeRepository.count(specification);
    }

    /**
     * Function to convert {@link SoilTypeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SoilType> createSpecification(SoilTypeCriteria criteria) {
        Specification<SoilType> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SoilType_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), SoilType_.name));
            }
            if (criteria.getDescriptions() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescriptions(), SoilType_.descriptions));
            }
        }
        return specification;
    }
}
