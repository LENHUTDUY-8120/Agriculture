package com.lnduy.agriculture.repository;

import com.lnduy.agriculture.domain.EventCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EventCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory, Long>, JpaSpecificationExecutor<EventCategory> {}
