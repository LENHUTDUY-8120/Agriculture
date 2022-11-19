package com.lnduy.agriculture.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FertilizersDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FertilizersDTO.class);
        FertilizersDTO fertilizersDTO1 = new FertilizersDTO();
        fertilizersDTO1.setId(1L);
        FertilizersDTO fertilizersDTO2 = new FertilizersDTO();
        assertThat(fertilizersDTO1).isNotEqualTo(fertilizersDTO2);
        fertilizersDTO2.setId(fertilizersDTO1.getId());
        assertThat(fertilizersDTO1).isEqualTo(fertilizersDTO2);
        fertilizersDTO2.setId(2L);
        assertThat(fertilizersDTO1).isNotEqualTo(fertilizersDTO2);
        fertilizersDTO1.setId(null);
        assertThat(fertilizersDTO1).isNotEqualTo(fertilizersDTO2);
    }
}
