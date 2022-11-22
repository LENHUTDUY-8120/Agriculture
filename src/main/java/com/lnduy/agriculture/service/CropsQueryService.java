package com.lnduy.agriculture.service;

import com.lnduy.agriculture.domain.*; // for static metamodels
import com.lnduy.agriculture.domain.Crops;
import com.lnduy.agriculture.repository.CropsRepository;
import com.lnduy.agriculture.service.criteria.CropsCriteria;
import com.lnduy.agriculture.service.dto.CropsDTO;
import com.lnduy.agriculture.service.mapper.CropsMapper;
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
 * Service for executing complex queries for {@link Crops} entities in the database.
 * The main input is a {@link CropsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CropsDTO} or a {@link Page} of {@link CropsDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CropsQueryService extends QueryService<Crops> {

    private final Logger log = LoggerFactory.getLogger(CropsQueryService.class);

    private final CropsRepository cropsRepository;

    private final CropsMapper cropsMapper;

    public CropsQueryService(CropsRepository cropsRepository, CropsMapper cropsMapper) {
        this.cropsRepository = cropsRepository;
        this.cropsMapper = cropsMapper;
    }

    /**
     * Return a {@link List} of {@link CropsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CropsDTO> findByCriteria(CropsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Crops> specification = createSpecification(criteria);
        return cropsMapper.toDto(cropsRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CropsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CropsDTO> findByCriteria(CropsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Crops> specification = createSpecification(criteria);
        return cropsRepository.findAll(specification, page).map(cropsMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CropsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Crops> specification = createSpecification(criteria);
        return cropsRepository.count(specification);
    }

    /**
     * Function to convert {@link CropsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Crops> createSpecification(CropsCriteria criteria) {
        Specification<Crops> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Crops_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Crops_.name));
            }
            if (criteria.getVolume() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVolume(), Crops_.volume));
            }
            if (criteria.getUnit() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUnit(), Crops_.unit));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Crops_.description));
            }
            if (criteria.getEnable() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEnable(), Crops_.enable));
            }
            if (criteria.getWarehouseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getWarehouseId(), root -> root.join(Crops_.warehouse, JoinType.LEFT).get(Warehouse_.id))
                    );
            }
        }
        return specification;
    }
}
