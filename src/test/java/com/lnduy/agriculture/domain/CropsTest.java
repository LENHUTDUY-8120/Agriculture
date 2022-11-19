package com.lnduy.agriculture.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CropsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Crops.class);
        Crops crops1 = new Crops();
        crops1.setId(1L);
        Crops crops2 = new Crops();
        crops2.setId(crops1.getId());
        assertThat(crops1).isEqualTo(crops2);
        crops2.setId(2L);
        assertThat(crops1).isNotEqualTo(crops2);
        crops1.setId(null);
        assertThat(crops1).isNotEqualTo(crops2);
    }
}
