package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.ProtectionProduct;
import com.lnduy.agriculture.repository.ProtectionProductRepository;
import com.lnduy.agriculture.service.dto.ProtectionProductDTO;
import com.lnduy.agriculture.service.mapper.ProtectionProductMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProtectionProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProtectionProductResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Float DEFAULT_VOLUME = 1F;
    private static final Float UPDATED_VOLUME = 2F;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final Integer DEFAULT_ENABLE = 1;
    private static final Integer UPDATED_ENABLE = 2;

    private static final String ENTITY_API_URL = "/api/protection-products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProtectionProductRepository protectionProductRepository;

    @Autowired
    private ProtectionProductMapper protectionProductMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProtectionProductMockMvc;

    private ProtectionProduct protectionProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProtectionProduct createEntity(EntityManager em) {
        ProtectionProduct protectionProduct = new ProtectionProduct()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .type(DEFAULT_TYPE)
            .volume(DEFAULT_VOLUME)
            .unit(DEFAULT_UNIT)
            .enable(DEFAULT_ENABLE);
        return protectionProduct;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProtectionProduct createUpdatedEntity(EntityManager em) {
        ProtectionProduct protectionProduct = new ProtectionProduct()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .enable(UPDATED_ENABLE);
        return protectionProduct;
    }

    @BeforeEach
    public void initTest() {
        protectionProduct = createEntity(em);
    }

    @Test
    @Transactional
    void createProtectionProduct() throws Exception {
        int databaseSizeBeforeCreate = protectionProductRepository.findAll().size();
        // Create the ProtectionProduct
        ProtectionProductDTO protectionProductDTO = protectionProductMapper.toDto(protectionProduct);
        restProtectionProductMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(protectionProductDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ProtectionProduct in the database
        List<ProtectionProduct> protectionProductList = protectionProductRepository.findAll();
        assertThat(protectionProductList).hasSize(databaseSizeBeforeCreate + 1);
        ProtectionProduct testProtectionProduct = protectionProductList.get(protectionProductList.size() - 1);
        assertThat(testProtectionProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProtectionProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProtectionProduct.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testProtectionProduct.getVolume()).isEqualTo(DEFAULT_VOLUME);
        assertThat(testProtectionProduct.getUnit()).isEqualTo(DEFAULT_UNIT);
        assertThat(testProtectionProduct.getEnable()).isEqualTo(DEFAULT_ENABLE);
    }

    @Test
    @Transactional
    void createProtectionProductWithExistingId() throws Exception {
        // Create the ProtectionProduct with an existing ID
        protectionProduct.setId(1L);
        ProtectionProductDTO protectionProductDTO = protectionProductMapper.toDto(protectionProduct);

        int databaseSizeBeforeCreate = protectionProductRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProtectionProductMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(protectionProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProtectionProduct in the database
        List<ProtectionProduct> protectionProductList = protectionProductRepository.findAll();
        assertThat(protectionProductList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProtectionProducts() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList
        restProtectionProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(protectionProduct.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.doubleValue())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)));
    }

    @Test
    @Transactional
    void getProtectionProduct() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get the protectionProduct
        restProtectionProductMockMvc
            .perform(get(ENTITY_API_URL_ID, protectionProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(protectionProduct.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.volume").value(DEFAULT_VOLUME.doubleValue()))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.enable").value(DEFAULT_ENABLE));
    }

    @Test
    @Transactional
    void getNonExistingProtectionProduct() throws Exception {
        // Get the protectionProduct
        restProtectionProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProtectionProduct() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        int databaseSizeBeforeUpdate = protectionProductRepository.findAll().size();

        // Update the protectionProduct
        ProtectionProduct updatedProtectionProduct = protectionProductRepository.findById(protectionProduct.getId()).get();
        // Disconnect from session so that the updates on updatedProtectionProduct are not directly saved in db
        em.detach(updatedProtectionProduct);
        updatedProtectionProduct
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .enable(UPDATED_ENABLE);
        ProtectionProductDTO protectionProductDTO = protectionProductMapper.toDto(updatedProtectionProduct);

        restProtectionProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, protectionProductDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(protectionProductDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProtectionProduct in the database
        List<ProtectionProduct> protectionProductList = protectionProductRepository.findAll();
        assertThat(protectionProductList).hasSize(databaseSizeBeforeUpdate);
        ProtectionProduct testProtectionProduct = protectionProductList.get(protectionProductList.size() - 1);
        assertThat(testProtectionProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProtectionProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProtectionProduct.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testProtectionProduct.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testProtectionProduct.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testProtectionProduct.getEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void putNonExistingProtectionProduct() throws Exception {
        int databaseSizeBeforeUpdate = protectionProductRepository.findAll().size();
        protectionProduct.setId(count.incrementAndGet());

        // Create the ProtectionProduct
        ProtectionProductDTO protectionProductDTO = protectionProductMapper.toDto(protectionProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProtectionProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, protectionProductDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(protectionProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProtectionProduct in the database
        List<ProtectionProduct> protectionProductList = protectionProductRepository.findAll();
        assertThat(protectionProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProtectionProduct() throws Exception {
        int databaseSizeBeforeUpdate = protectionProductRepository.findAll().size();
        protectionProduct.setId(count.incrementAndGet());

        // Create the ProtectionProduct
        ProtectionProductDTO protectionProductDTO = protectionProductMapper.toDto(protectionProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProtectionProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(protectionProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProtectionProduct in the database
        List<ProtectionProduct> protectionProductList = protectionProductRepository.findAll();
        assertThat(protectionProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProtectionProduct() throws Exception {
        int databaseSizeBeforeUpdate = protectionProductRepository.findAll().size();
        protectionProduct.setId(count.incrementAndGet());

        // Create the ProtectionProduct
        ProtectionProductDTO protectionProductDTO = protectionProductMapper.toDto(protectionProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProtectionProductMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(protectionProductDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProtectionProduct in the database
        List<ProtectionProduct> protectionProductList = protectionProductRepository.findAll();
        assertThat(protectionProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProtectionProductWithPatch() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        int databaseSizeBeforeUpdate = protectionProductRepository.findAll().size();

        // Update the protectionProduct using partial update
        ProtectionProduct partialUpdatedProtectionProduct = new ProtectionProduct();
        partialUpdatedProtectionProduct.setId(protectionProduct.getId());

        partialUpdatedProtectionProduct.unit(UPDATED_UNIT).enable(UPDATED_ENABLE);

        restProtectionProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProtectionProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProtectionProduct))
            )
            .andExpect(status().isOk());

        // Validate the ProtectionProduct in the database
        List<ProtectionProduct> protectionProductList = protectionProductRepository.findAll();
        assertThat(protectionProductList).hasSize(databaseSizeBeforeUpdate);
        ProtectionProduct testProtectionProduct = protectionProductList.get(protectionProductList.size() - 1);
        assertThat(testProtectionProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProtectionProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProtectionProduct.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testProtectionProduct.getVolume()).isEqualTo(DEFAULT_VOLUME);
        assertThat(testProtectionProduct.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testProtectionProduct.getEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void fullUpdateProtectionProductWithPatch() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        int databaseSizeBeforeUpdate = protectionProductRepository.findAll().size();

        // Update the protectionProduct using partial update
        ProtectionProduct partialUpdatedProtectionProduct = new ProtectionProduct();
        partialUpdatedProtectionProduct.setId(protectionProduct.getId());

        partialUpdatedProtectionProduct
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .enable(UPDATED_ENABLE);

        restProtectionProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProtectionProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProtectionProduct))
            )
            .andExpect(status().isOk());

        // Validate the ProtectionProduct in the database
        List<ProtectionProduct> protectionProductList = protectionProductRepository.findAll();
        assertThat(protectionProductList).hasSize(databaseSizeBeforeUpdate);
        ProtectionProduct testProtectionProduct = protectionProductList.get(protectionProductList.size() - 1);
        assertThat(testProtectionProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProtectionProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProtectionProduct.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testProtectionProduct.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testProtectionProduct.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testProtectionProduct.getEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void patchNonExistingProtectionProduct() throws Exception {
        int databaseSizeBeforeUpdate = protectionProductRepository.findAll().size();
        protectionProduct.setId(count.incrementAndGet());

        // Create the ProtectionProduct
        ProtectionProductDTO protectionProductDTO = protectionProductMapper.toDto(protectionProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProtectionProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, protectionProductDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(protectionProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProtectionProduct in the database
        List<ProtectionProduct> protectionProductList = protectionProductRepository.findAll();
        assertThat(protectionProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProtectionProduct() throws Exception {
        int databaseSizeBeforeUpdate = protectionProductRepository.findAll().size();
        protectionProduct.setId(count.incrementAndGet());

        // Create the ProtectionProduct
        ProtectionProductDTO protectionProductDTO = protectionProductMapper.toDto(protectionProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProtectionProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(protectionProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProtectionProduct in the database
        List<ProtectionProduct> protectionProductList = protectionProductRepository.findAll();
        assertThat(protectionProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProtectionProduct() throws Exception {
        int databaseSizeBeforeUpdate = protectionProductRepository.findAll().size();
        protectionProduct.setId(count.incrementAndGet());

        // Create the ProtectionProduct
        ProtectionProductDTO protectionProductDTO = protectionProductMapper.toDto(protectionProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProtectionProductMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(protectionProductDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProtectionProduct in the database
        List<ProtectionProduct> protectionProductList = protectionProductRepository.findAll();
        assertThat(protectionProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProtectionProduct() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        int databaseSizeBeforeDelete = protectionProductRepository.findAll().size();

        // Delete the protectionProduct
        restProtectionProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, protectionProduct.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProtectionProduct> protectionProductList = protectionProductRepository.findAll();
        assertThat(protectionProductList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
