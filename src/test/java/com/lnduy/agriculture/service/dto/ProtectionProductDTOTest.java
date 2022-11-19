package com.lnduy.agriculture.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProtectionProductDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProtectionProductDTO.class);
        ProtectionProductDTO protectionProductDTO1 = new ProtectionProductDTO();
        protectionProductDTO1.setId(1L);
        ProtectionProductDTO protectionProductDTO2 = new ProtectionProductDTO();
        assertThat(protectionProductDTO1).isNotEqualTo(protectionProductDTO2);
        protectionProductDTO2.setId(protectionProductDTO1.getId());
        assertThat(protectionProductDTO1).isEqualTo(protectionProductDTO2);
        protectionProductDTO2.setId(2L);
        assertThat(protectionProductDTO1).isNotEqualTo(protectionProductDTO2);
        protectionProductDTO1.setId(null);
        assertThat(protectionProductDTO1).isNotEqualTo(protectionProductDTO2);
    }
}
