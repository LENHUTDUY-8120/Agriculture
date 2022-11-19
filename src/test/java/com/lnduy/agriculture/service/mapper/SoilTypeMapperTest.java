package com.lnduy.agriculture.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SoilTypeMapperTest {

    private SoilTypeMapper soilTypeMapper;

    @BeforeEach
    public void setUp() {
        soilTypeMapper = new SoilTypeMapperImpl();
    }
}
