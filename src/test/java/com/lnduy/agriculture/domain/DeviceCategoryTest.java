package com.lnduy.agriculture.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeviceCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeviceCategory.class);
        DeviceCategory deviceCategory1 = new DeviceCategory();
        deviceCategory1.setId(1L);
        DeviceCategory deviceCategory2 = new DeviceCategory();
        deviceCategory2.setId(deviceCategory1.getId());
        assertThat(deviceCategory1).isEqualTo(deviceCategory2);
        deviceCategory2.setId(2L);
        assertThat(deviceCategory1).isNotEqualTo(deviceCategory2);
        deviceCategory1.setId(null);
        assertThat(deviceCategory1).isNotEqualTo(deviceCategory2);
    }
}
