package com.lnduy.agriculture.repository;

import com.lnduy.agriculture.domain.SoilType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SoilType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SoilTypeRepository extends JpaRepository<SoilType, Long>, JpaSpecificationExecutor<SoilType> {}
