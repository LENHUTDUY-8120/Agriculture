package com.lnduy.agriculture.repository;

import com.lnduy.agriculture.domain.Crops;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Crops entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CropsRepository extends JpaRepository<Crops, Long>, JpaSpecificationExecutor<Crops> {}
