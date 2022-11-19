package com.lnduy.agriculture.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeviceCategoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeviceCategoryDTO.class);
        DeviceCategoryDTO deviceCategoryDTO1 = new DeviceCategoryDTO();
        deviceCategoryDTO1.setId(1L);
        DeviceCategoryDTO deviceCategoryDTO2 = new DeviceCategoryDTO();
        assertThat(deviceCategoryDTO1).isNotEqualTo(deviceCategoryDTO2);
        deviceCategoryDTO2.setId(deviceCategoryDTO1.getId());
        assertThat(deviceCategoryDTO1).isEqualTo(deviceCategoryDTO2);
        deviceCategoryDTO2.setId(2L);
        assertThat(deviceCategoryDTO1).isNotEqualTo(deviceCategoryDTO2);
        deviceCategoryDTO1.setId(null);
        assertThat(deviceCategoryDTO1).isNotEqualTo(deviceCategoryDTO2);
    }
}
