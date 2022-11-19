package com.lnduy.agriculture.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SeasonMapperTest {

    private SeasonMapper seasonMapper;

    @BeforeEach
    public void setUp() {
        seasonMapper = new SeasonMapperImpl();
    }
}
