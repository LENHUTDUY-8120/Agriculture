package com.lnduy.agriculture.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SuppliesMapperTest {

    private SuppliesMapper suppliesMapper;

    @BeforeEach
    public void setUp() {
        suppliesMapper = new SuppliesMapperImpl();
    }
}
