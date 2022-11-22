package com.lnduy.agriculture.service;

import com.lnduy.agriculture.domain.*; // for static metamodels
import com.lnduy.agriculture.domain.ProtectionProduct;
import com.lnduy.agriculture.repository.ProtectionProductRepository;
import com.lnduy.agriculture.service.criteria.ProtectionProductCriteria;
import com.lnduy.agriculture.service.dto.ProtectionProductDTO;
import com.lnduy.agriculture.service.mapper.ProtectionProductMapper;
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
 * Service for executing complex queries for {@link ProtectionProduct} entities in the database.
 * The main input is a {@link ProtectionProductCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProtectionProductDTO} or a {@link Page} of {@link ProtectionProductDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProtectionProductQueryService extends QueryService<ProtectionProduct> {

    private final Logger log = LoggerFactory.getLogger(ProtectionProductQueryService.class);

    private final ProtectionProductRepository protectionProductRepository;

    private final ProtectionProductMapper protectionProductMapper;

    public ProtectionProductQueryService(
        ProtectionProductRepository protectionProductRepository,
        ProtectionProductMapper protectionProductMapper
    ) {
        this.protectionProductRepository = protectionProductRepository;
        this.protectionProductMapper = protectionProductMapper;
    }

    /**
     * Return a {@link List} of {@link ProtectionProductDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProtectionProductDTO> findByCriteria(ProtectionProductCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProtectionProduct> specification = createSpecification(criteria);
        return protectionProductMapper.toDto(protectionProductRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProtectionProductDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProtectionProductDTO> findByCriteria(ProtectionProductCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProtectionProduct> specification = createSpecification(criteria);
        return protectionProductRepository.findAll(specification, page).map(protectionProductMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProtectionProductCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProtectionProduct> specification = createSpecification(criteria);
        return protectionProductRepository.count(specification);
    }

    /**
     * Function to convert {@link ProtectionProductCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProtectionProduct> createSpecification(ProtectionProductCriteria criteria) {
        Specification<ProtectionProduct> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProtectionProduct_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), ProtectionProduct_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), ProtectionProduct_.description));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), ProtectionProduct_.type));
            }
            if (criteria.getVolume() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVolume(), ProtectionProduct_.volume));
            }
            if (criteria.getUnit() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUnit(), ProtectionProduct_.unit));
            }
            if (criteria.getEnable() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEnable(), ProtectionProduct_.enable));
            }
            if (criteria.getWarehouseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getWarehouseId(),
                            root -> root.join(ProtectionProduct_.warehouse, JoinType.LEFT).get(Warehouse_.id)
                        )
                    );
            }
            if (criteria.getTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTaskId(), root -> root.join(ProtectionProduct_.tasks, JoinType.LEFT).get(Task_.id))
                    );
            }
        }
        return specification;
    }
}
