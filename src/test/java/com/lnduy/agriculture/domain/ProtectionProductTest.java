package com.lnduy.agriculture.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProtectionProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProtectionProduct.class);
        ProtectionProduct protectionProduct1 = new ProtectionProduct();
        protectionProduct1.setId(1L);
        ProtectionProduct protectionProduct2 = new ProtectionProduct();
        protectionProduct2.setId(protectionProduct1.getId());
        assertThat(protectionProduct1).isEqualTo(protectionProduct2);
        protectionProduct2.setId(2L);
        assertThat(protectionProduct1).isNotEqualTo(protectionProduct2);
        protectionProduct1.setId(null);
        assertThat(protectionProduct1).isNotEqualTo(protectionProduct2);
    }
}
