package com.lnduy.agriculture.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FertilizersMapperTest {

    private FertilizersMapper fertilizersMapper;

    @BeforeEach
    public void setUp() {
        fertilizersMapper = new FertilizersMapperImpl();
    }
}
