package com.lnduy.agriculture.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WarehouseMapperTest {

    private WarehouseMapper warehouseMapper;

    @BeforeEach
    public void setUp() {
        warehouseMapper = new WarehouseMapperImpl();
    }
}
