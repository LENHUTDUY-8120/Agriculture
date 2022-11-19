package com.lnduy.agriculture.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SuppliesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SuppliesDTO.class);
        SuppliesDTO suppliesDTO1 = new SuppliesDTO();
        suppliesDTO1.setId(1L);
        SuppliesDTO suppliesDTO2 = new SuppliesDTO();
        assertThat(suppliesDTO1).isNotEqualTo(suppliesDTO2);
        suppliesDTO2.setId(suppliesDTO1.getId());
        assertThat(suppliesDTO1).isEqualTo(suppliesDTO2);
        suppliesDTO2.setId(2L);
        assertThat(suppliesDTO1).isNotEqualTo(suppliesDTO2);
        suppliesDTO1.setId(null);
        assertThat(suppliesDTO1).isNotEqualTo(suppliesDTO2);
    }
}
