package com.lnduy.agriculture.service;

import com.lnduy.agriculture.domain.*; // for static metamodels
import com.lnduy.agriculture.domain.Monitoring;
import com.lnduy.agriculture.repository.MonitoringRepository;
import com.lnduy.agriculture.service.criteria.MonitoringCriteria;
import com.lnduy.agriculture.service.dto.MonitoringDTO;
import com.lnduy.agriculture.service.mapper.MonitoringMapper;
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
 * Service for executing complex queries for {@link Monitoring} entities in the database.
 * The main input is a {@link MonitoringCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MonitoringDTO} or a {@link Page} of {@link MonitoringDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MonitoringQueryService extends QueryService<Monitoring> {

    private final Logger log = LoggerFactory.getLogger(MonitoringQueryService.class);

    private final MonitoringRepository monitoringRepository;

    private final MonitoringMapper monitoringMapper;

    public MonitoringQueryService(MonitoringRepository monitoringRepository, MonitoringMapper monitoringMapper) {
        this.monitoringRepository = monitoringRepository;
        this.monitoringMapper = monitoringMapper;
    }

    /**
     * Return a {@link List} of {@link MonitoringDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MonitoringDTO> findByCriteria(MonitoringCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Monitoring> specification = createSpecification(criteria);
        return monitoringMapper.toDto(monitoringRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MonitoringDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MonitoringDTO> findByCriteria(MonitoringCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Monitoring> specification = createSpecification(criteria);
        return monitoringRepository.findAll(specification, page).map(monitoringMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MonitoringCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Monitoring> specification = createSpecification(criteria);
        return monitoringRepository.count(specification);
    }

    /**
     * Function to convert {@link MonitoringCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Monitoring> createSpecification(MonitoringCriteria criteria) {
        Specification<Monitoring> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Monitoring_.id));
            }
            if (criteria.getDataJson() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDataJson(), Monitoring_.dataJson));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Monitoring_.createdAt));
            }
            if (criteria.getFieldId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getFieldId(), root -> root.join(Monitoring_.field, JoinType.LEFT).get(Field_.id))
                    );
            }
            if (criteria.getDeviceId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDeviceId(), root -> root.join(Monitoring_.device, JoinType.LEFT).get(Device_.id))
                    );
            }
        }
        return specification;
    }
}
