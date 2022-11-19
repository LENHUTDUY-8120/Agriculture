package com.lnduy.agriculture.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SoilTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SoilType.class);
        SoilType soilType1 = new SoilType();
        soilType1.setId(1L);
        SoilType soilType2 = new SoilType();
        soilType2.setId(soilType1.getId());
        assertThat(soilType1).isEqualTo(soilType2);
        soilType2.setId(2L);
        assertThat(soilType1).isNotEqualTo(soilType2);
        soilType1.setId(null);
        assertThat(soilType1).isNotEqualTo(soilType2);
    }
}
