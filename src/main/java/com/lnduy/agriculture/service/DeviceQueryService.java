package com.lnduy.agriculture.service;

import com.lnduy.agriculture.domain.*; // for static metamodels
import com.lnduy.agriculture.domain.Device;
import com.lnduy.agriculture.repository.DeviceRepository;
import com.lnduy.agriculture.service.criteria.DeviceCriteria;
import com.lnduy.agriculture.service.dto.DeviceDTO;
import com.lnduy.agriculture.service.mapper.DeviceMapper;
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
 * Service for executing complex queries for {@link Device} entities in the database.
 * The main input is a {@link DeviceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DeviceDTO} or a {@link Page} of {@link DeviceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DeviceQueryService extends QueryService<Device> {

    private final Logger log = LoggerFactory.getLogger(DeviceQueryService.class);

    private final DeviceRepository deviceRepository;

    private final DeviceMapper deviceMapper;

    public DeviceQueryService(DeviceRepository deviceRepository, DeviceMapper deviceMapper) {
        this.deviceRepository = deviceRepository;
        this.deviceMapper = deviceMapper;
    }

    /**
     * Return a {@link List} of {@link DeviceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DeviceDTO> findByCriteria(DeviceCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Device> specification = createSpecification(criteria);
        return deviceMapper.toDto(deviceRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DeviceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DeviceDTO> findByCriteria(DeviceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Device> specification = createSpecification(criteria);
        return deviceRepository.findAll(specification, page).map(deviceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DeviceCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Device> specification = createSpecification(criteria);
        return deviceRepository.count(specification);
    }

    /**
     * Function to convert {@link DeviceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Device> createSpecification(DeviceCriteria criteria) {
        Specification<Device> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Device_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Device_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Device_.code));
            }
            if (criteria.getIp() != null) {
                specification = specification.and(buildStringSpecification(criteria.getIp(), Device_.ip));
            }
            if (criteria.getProperty() != null) {
                specification = specification.and(buildStringSpecification(criteria.getProperty(), Device_.property));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), Device_.type));
            }
            if (criteria.getEnable() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEnable(), Device_.enable));
            }
            if (criteria.getCategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCategoryId(),
                            root -> root.join(Device_.category, JoinType.LEFT).get(DeviceCategory_.id)
                        )
                    );
            }
            if (criteria.getFieldId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getFieldId(), root -> root.join(Device_.field, JoinType.LEFT).get(Field_.id))
                    );
            }
        }
        return specification;
    }
}
