package com.lnduy.agriculture.repository;

import com.lnduy.agriculture.domain.Supplies;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Supplies entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SuppliesRepository extends JpaRepository<Supplies, Long> {}
