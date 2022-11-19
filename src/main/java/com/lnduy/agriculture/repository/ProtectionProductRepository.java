package com.lnduy.agriculture.repository;

import com.lnduy.agriculture.domain.ProtectionProduct;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProtectionProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProtectionProductRepository extends JpaRepository<ProtectionProduct, Long> {}
