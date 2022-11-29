package com.lnduy.agriculture.service;

import com.lnduy.agriculture.domain.*; // for static metamodels
import com.lnduy.agriculture.domain.Season;
import com.lnduy.agriculture.repository.SeasonRepository;
import com.lnduy.agriculture.service.criteria.SeasonCriteria;
import com.lnduy.agriculture.service.dto.SeasonDTO;
import com.lnduy.agriculture.service.mapper.SeasonMapper;
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
 * Service for executing complex queries for {@link Season} entities in the database.
 * The main input is a {@link SeasonCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SeasonDTO} or a {@link Page} of {@link SeasonDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SeasonQueryService extends QueryService<Season> {

    private final Logger log = LoggerFactory.getLogger(SeasonQueryService.class);

    private final SeasonRepository seasonRepository;

    private final SeasonMapper seasonMapper;

    public SeasonQueryService(SeasonRepository seasonRepository, SeasonMapper seasonMapper) {
        this.seasonRepository = seasonRepository;
        this.seasonMapper = seasonMapper;
    }

    /**
     * Return a {@link List} of {@link SeasonDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SeasonDTO> findByCriteria(SeasonCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Season> specification = createSpecification(criteria);
        return seasonMapper.toDto(seasonRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SeasonDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SeasonDTO> findByCriteria(SeasonCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Season> specification = createSpecification(criteria);
        return seasonRepository.findAll(specification, page).map(seasonMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SeasonCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Season> specification = createSpecification(criteria);
        return seasonRepository.count(specification);
    }

    /**
     * Function to convert {@link SeasonCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Season> createSpecification(SeasonCriteria criteria) {
        Specification<Season> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Season_.id));
            }
            if (criteria.getCrops() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCrops(), Season_.crops));
            }
            if (criteria.getTotalCost() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalCost(), Season_.totalCost));
            }
            if (criteria.getCropYields() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCropYields(), Season_.cropYields));
            }
            if (criteria.getEnable() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEnable(), Season_.enable));
            }
            if (criteria.getVolume() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVolume(), Season_.volume));
            }
            if (criteria.getUnit() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUnit(), Season_.unit));
            }
            if (criteria.getDone() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDone(), Season_.done));
            }
            if (criteria.getStartAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartAt(), Season_.startAt));
            }
            if (criteria.getEndAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndAt(), Season_.endAt));
            }
            if (criteria.getTransactionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransactionId(),
                            root -> root.join(Season_.transactions, JoinType.LEFT).get(Transaction_.id)
                        )
                    );
            }
            if (criteria.getCropId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCropId(), root -> root.join(Season_.crop, JoinType.LEFT).get(Crops_.id))
                    );
            }
            if (criteria.getFieldId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getFieldId(), root -> root.join(Season_.field, JoinType.LEFT).get(Field_.id))
                    );
            }
        }
        return specification;
    }
}
