package com.lnduy.agriculture.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProtectionProductMapperTest {

    private ProtectionProductMapper protectionProductMapper;

    @BeforeEach
    public void setUp() {
        protectionProductMapper = new ProtectionProductMapperImpl();
    }
}
