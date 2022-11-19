package com.lnduy.agriculture.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventCategory.class);
        EventCategory eventCategory1 = new EventCategory();
        eventCategory1.setId(1L);
        EventCategory eventCategory2 = new EventCategory();
        eventCategory2.setId(eventCategory1.getId());
        assertThat(eventCategory1).isEqualTo(eventCategory2);
        eventCategory2.setId(2L);
        assertThat(eventCategory1).isNotEqualTo(eventCategory2);
        eventCategory1.setId(null);
        assertThat(eventCategory1).isNotEqualTo(eventCategory2);
    }
}
