package com.lnduy.agriculture.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CropsMapperTest {

    private CropsMapper cropsMapper;

    @BeforeEach
    public void setUp() {
        cropsMapper = new CropsMapperImpl();
    }
}
