package com.lnduy.agriculture.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MonitoringTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Monitoring.class);
        Monitoring monitoring1 = new Monitoring();
        monitoring1.setId(1L);
        Monitoring monitoring2 = new Monitoring();
        monitoring2.setId(monitoring1.getId());
        assertThat(monitoring1).isEqualTo(monitoring2);
        monitoring2.setId(2L);
        assertThat(monitoring1).isNotEqualTo(monitoring2);
        monitoring1.setId(null);
        assertThat(monitoring1).isNotEqualTo(monitoring2);
    }
}
