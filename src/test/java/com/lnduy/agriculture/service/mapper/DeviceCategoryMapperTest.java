package com.lnduy.agriculture.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeviceCategoryMapperTest {

    private DeviceCategoryMapper deviceCategoryMapper;

    @BeforeEach
    public void setUp() {
        deviceCategoryMapper = new DeviceCategoryMapperImpl();
    }
}
