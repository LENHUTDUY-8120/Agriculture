package com.lnduy.agriculture.repository;

import com.lnduy.agriculture.domain.Monitoring;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Monitoring entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MonitoringRepository extends JpaRepository<Monitoring, Long> {}
