package com.lnduy.agriculture.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CropsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CropsDTO.class);
        CropsDTO cropsDTO1 = new CropsDTO();
        cropsDTO1.setId(1L);
        CropsDTO cropsDTO2 = new CropsDTO();
        assertThat(cropsDTO1).isNotEqualTo(cropsDTO2);
        cropsDTO2.setId(cropsDTO1.getId());
        assertThat(cropsDTO1).isEqualTo(cropsDTO2);
        cropsDTO2.setId(2L);
        assertThat(cropsDTO1).isNotEqualTo(cropsDTO2);
        cropsDTO1.setId(null);
        assertThat(cropsDTO1).isNotEqualTo(cropsDTO2);
    }
}
