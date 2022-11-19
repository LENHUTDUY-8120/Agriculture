package com.lnduy.agriculture.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SoilTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SoilTypeDTO.class);
        SoilTypeDTO soilTypeDTO1 = new SoilTypeDTO();
        soilTypeDTO1.setId(1L);
        SoilTypeDTO soilTypeDTO2 = new SoilTypeDTO();
        assertThat(soilTypeDTO1).isNotEqualTo(soilTypeDTO2);
        soilTypeDTO2.setId(soilTypeDTO1.getId());
        assertThat(soilTypeDTO1).isEqualTo(soilTypeDTO2);
        soilTypeDTO2.setId(2L);
        assertThat(soilTypeDTO1).isNotEqualTo(soilTypeDTO2);
        soilTypeDTO1.setId(null);
        assertThat(soilTypeDTO1).isNotEqualTo(soilTypeDTO2);
    }
}
