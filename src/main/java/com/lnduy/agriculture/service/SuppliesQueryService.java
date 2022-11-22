package com.lnduy.agriculture.service;

import com.lnduy.agriculture.domain.*; // for static metamodels
import com.lnduy.agriculture.domain.Supplies;
import com.lnduy.agriculture.repository.SuppliesRepository;
import com.lnduy.agriculture.service.criteria.SuppliesCriteria;
import com.lnduy.agriculture.service.dto.SuppliesDTO;
import com.lnduy.agriculture.service.mapper.SuppliesMapper;
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
 * Service for executing complex queries for {@link Supplies} entities in the database.
 * The main input is a {@link SuppliesCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SuppliesDTO} or a {@link Page} of {@link SuppliesDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SuppliesQueryService extends QueryService<Supplies> {

    private final Logger log = LoggerFactory.getLogger(SuppliesQueryService.class);

    private final SuppliesRepository suppliesRepository;

    private final SuppliesMapper suppliesMapper;

    public SuppliesQueryService(SuppliesRepository suppliesRepository, SuppliesMapper suppliesMapper) {
        this.suppliesRepository = suppliesRepository;
        this.suppliesMapper = suppliesMapper;
    }

    /**
     * Return a {@link List} of {@link SuppliesDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SuppliesDTO> findByCriteria(SuppliesCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Supplies> specification = createSpecification(criteria);
        return suppliesMapper.toDto(suppliesRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SuppliesDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SuppliesDTO> findByCriteria(SuppliesCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Supplies> specification = createSpecification(criteria);
        return suppliesRepository.findAll(specification, page).map(suppliesMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SuppliesCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Supplies> specification = createSpecification(criteria);
        return suppliesRepository.count(specification);
    }

    /**
     * Function to convert {@link SuppliesCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Supplies> createSpecification(SuppliesCriteria criteria) {
        Specification<Supplies> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Supplies_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Supplies_.name));
            }
            if (criteria.getProperty() != null) {
                specification = specification.and(buildStringSpecification(criteria.getProperty(), Supplies_.property));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), Supplies_.type));
            }
            if (criteria.getVolume() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVolume(), Supplies_.volume));
            }
            if (criteria.getUnit() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUnit(), Supplies_.unit));
            }
            if (criteria.getEnable() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEnable(), Supplies_.enable));
            }
            if (criteria.getWarehouseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getWarehouseId(),
                            root -> root.join(Supplies_.warehouse, JoinType.LEFT).get(Warehouse_.id)
                        )
                    );
            }
            if (criteria.getTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTaskId(), root -> root.join(Supplies_.tasks, JoinType.LEFT).get(Task_.id))
                    );
            }
        }
        return specification;
    }
}
