package com.lnduy.agriculture.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.lnduy.agriculture.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FertilizersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Fertilizers.class);
        Fertilizers fertilizers1 = new Fertilizers();
        fertilizers1.setId(1L);
        Fertilizers fertilizers2 = new Fertilizers();
        fertilizers2.setId(fertilizers1.getId());
        assertThat(fertilizers1).isEqualTo(fertilizers2);
        fertilizers2.setId(2L);
        assertThat(fertilizers1).isNotEqualTo(fertilizers2);
        fertilizers1.setId(null);
        assertThat(fertilizers1).isNotEqualTo(fertilizers2);
    }
}
