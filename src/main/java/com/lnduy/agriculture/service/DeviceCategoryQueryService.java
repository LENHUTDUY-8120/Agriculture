package com.lnduy.agriculture.service;

import com.lnduy.agriculture.domain.*; // for static metamodels
import com.lnduy.agriculture.domain.DeviceCategory;
import com.lnduy.agriculture.repository.DeviceCategoryRepository;
import com.lnduy.agriculture.service.criteria.DeviceCategoryCriteria;
import com.lnduy.agriculture.service.dto.DeviceCategoryDTO;
import com.lnduy.agriculture.service.mapper.DeviceCategoryMapper;
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
 * Service for executing complex queries for {@link DeviceCategory} entities in the database.
 * The main input is a {@link DeviceCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DeviceCategoryDTO} or a {@link Page} of {@link DeviceCategoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DeviceCategoryQueryService extends QueryService<DeviceCategory> {

    private final Logger log = LoggerFactory.getLogger(DeviceCategoryQueryService.class);

    private final DeviceCategoryRepository deviceCategoryRepository;

    private final DeviceCategoryMapper deviceCategoryMapper;

    public DeviceCategoryQueryService(DeviceCategoryRepository deviceCategoryRepository, DeviceCategoryMapper deviceCategoryMapper) {
        this.deviceCategoryRepository = deviceCategoryRepository;
        this.deviceCategoryMapper = deviceCategoryMapper;
    }

    /**
     * Return a {@link List} of {@link DeviceCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DeviceCategoryDTO> findByCriteria(DeviceCategoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<DeviceCategory> specification = createSpecification(criteria);
        return deviceCategoryMapper.toDto(deviceCategoryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DeviceCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DeviceCategoryDTO> findByCriteria(DeviceCategoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DeviceCategory> specification = createSpecification(criteria);
        return deviceCategoryRepository.findAll(specification, page).map(deviceCategoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DeviceCategoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<DeviceCategory> specification = createSpecification(criteria);
        return deviceCategoryRepository.count(specification);
    }

    /**
     * Function to convert {@link DeviceCategoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DeviceCategory> createSpecification(DeviceCategoryCriteria criteria) {
        Specification<DeviceCategory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), DeviceCategory_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), DeviceCategory_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), DeviceCategory_.description));
            }
        }
        return specification;
    }
}
