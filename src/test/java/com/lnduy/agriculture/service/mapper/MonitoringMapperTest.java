package com.lnduy.agriculture.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MonitoringMapperTest {

    private MonitoringMapper monitoringMapper;

    @BeforeEach
    public void setUp() {
        monitoringMapper = new MonitoringMapperImpl();
    }
}
