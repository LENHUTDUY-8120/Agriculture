package com.lnduy.agriculture.service;

import com.lnduy.agriculture.domain.*; // for static metamodels
import com.lnduy.agriculture.domain.Fertilizers;
import com.lnduy.agriculture.repository.FertilizersRepository;
import com.lnduy.agriculture.service.criteria.FertilizersCriteria;
import com.lnduy.agriculture.service.dto.FertilizersDTO;
import com.lnduy.agriculture.service.mapper.FertilizersMapper;
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
 * Service for executing complex queries for {@link Fertilizers} entities in the database.
 * The main input is a {@link FertilizersCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FertilizersDTO} or a {@link Page} of {@link FertilizersDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FertilizersQueryService extends QueryService<Fertilizers> {

    private final Logger log = LoggerFactory.getLogger(FertilizersQueryService.class);

    private final FertilizersRepository fertilizersRepository;

    private final FertilizersMapper fertilizersMapper;

    public FertilizersQueryService(FertilizersRepository fertilizersRepository, FertilizersMapper fertilizersMapper) {
        this.fertilizersRepository = fertilizersRepository;
        this.fertilizersMapper = fertilizersMapper;
    }

    /**
     * Return a {@link List} of {@link FertilizersDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FertilizersDTO> findByCriteria(FertilizersCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Fertilizers> specification = createSpecification(criteria);
        return fertilizersMapper.toDto(fertilizersRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FertilizersDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FertilizersDTO> findByCriteria(FertilizersCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Fertilizers> specification = createSpecification(criteria);
        return fertilizersRepository.findAll(specification, page).map(fertilizersMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FertilizersCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Fertilizers> specification = createSpecification(criteria);
        return fertilizersRepository.count(specification);
    }

    /**
     * Function to convert {@link FertilizersCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Fertilizers> createSpecification(FertilizersCriteria criteria) {
        Specification<Fertilizers> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Fertilizers_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Fertilizers_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Fertilizers_.description));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), Fertilizers_.type));
            }
            if (criteria.getVolume() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVolume(), Fertilizers_.volume));
            }
            if (criteria.getUnit() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUnit(), Fertilizers_.unit));
            }
            if (criteria.getEnable() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEnable(), Fertilizers_.enable));
            }
            if (criteria.getWarehouseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getWarehouseId(),
                            root -> root.join(Fertilizers_.warehouse, JoinType.LEFT).get(Warehouse_.id)
                        )
                    );
            }
            if (criteria.getTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTaskId(), root -> root.join(Fertilizers_.tasks, JoinType.LEFT).get(Task_.id))
                    );
            }
        }
        return specification;
    }
}
