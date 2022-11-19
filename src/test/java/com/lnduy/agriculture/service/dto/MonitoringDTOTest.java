package com.lnduy.agriculture.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MonitoringDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MonitoringDTO.class);
        MonitoringDTO monitoringDTO1 = new MonitoringDTO();
        monitoringDTO1.setId(1L);
        MonitoringDTO monitoringDTO2 = new MonitoringDTO();
        assertThat(monitoringDTO1).isNotEqualTo(monitoringDTO2);
        monitoringDTO2.setId(monitoringDTO1.getId());
        assertThat(monitoringDTO1).isEqualTo(monitoringDTO2);
        monitoringDTO2.setId(2L);
        assertThat(monitoringDTO1).isNotEqualTo(monitoringDTO2);
        monitoringDTO1.setId(null);
        assertThat(monitoringDTO1).isNotEqualTo(monitoringDTO2);
    }
}
