package com.lnduy.agriculture.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventCategoryMapperTest {

    private EventCategoryMapper eventCategoryMapper;

    @BeforeEach
    public void setUp() {
        eventCategoryMapper = new EventCategoryMapperImpl();
    }
}
