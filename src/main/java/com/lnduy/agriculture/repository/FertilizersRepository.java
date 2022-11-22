package com.lnduy.agriculture.repository;

import com.lnduy.agriculture.domain.Fertilizers;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Fertilizers entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FertilizersRepository extends JpaRepository<Fertilizers, Long>, JpaSpecificationExecutor<Fertilizers> {}
