package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Supplies;
import com.lnduy.agriculture.repository.SuppliesRepository;
import com.lnduy.agriculture.service.dto.SuppliesDTO;
import com.lnduy.agriculture.service.mapper.SuppliesMapper;
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
 * Integration tests for the {@link SuppliesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SuppliesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PROPERTY = "AAAAAAAAAA";
    private static final String UPDATED_PROPERTY = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_ENABLE = 1;
    private static final Integer UPDATED_ENABLE = 2;

    private static final String ENTITY_API_URL = "/api/supplies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SuppliesRepository suppliesRepository;

    @Autowired
    private SuppliesMapper suppliesMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSuppliesMockMvc;

    private Supplies supplies;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Supplies createEntity(EntityManager em) {
        Supplies supplies = new Supplies().name(DEFAULT_NAME).property(DEFAULT_PROPERTY).type(DEFAULT_TYPE).enable(DEFAULT_ENABLE);
        return supplies;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Supplies createUpdatedEntity(EntityManager em) {
        Supplies supplies = new Supplies().name(UPDATED_NAME).property(UPDATED_PROPERTY).type(UPDATED_TYPE).enable(UPDATED_ENABLE);
        return supplies;
    }

    @BeforeEach
    public void initTest() {
        supplies = createEntity(em);
    }

    @Test
    @Transactional
    void createSupplies() throws Exception {
        int databaseSizeBeforeCreate = suppliesRepository.findAll().size();
        // Create the Supplies
        SuppliesDTO suppliesDTO = suppliesMapper.toDto(supplies);
        restSuppliesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(suppliesDTO)))
            .andExpect(status().isCreated());

        // Validate the Supplies in the database
        List<Supplies> suppliesList = suppliesRepository.findAll();
        assertThat(suppliesList).hasSize(databaseSizeBeforeCreate + 1);
        Supplies testSupplies = suppliesList.get(suppliesList.size() - 1);
        assertThat(testSupplies.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSupplies.getProperty()).isEqualTo(DEFAULT_PROPERTY);
        assertThat(testSupplies.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSupplies.getEnable()).isEqualTo(DEFAULT_ENABLE);
    }

    @Test
    @Transactional
    void createSuppliesWithExistingId() throws Exception {
        // Create the Supplies with an existing ID
        supplies.setId(1L);
        SuppliesDTO suppliesDTO = suppliesMapper.toDto(supplies);

        int databaseSizeBeforeCreate = suppliesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSuppliesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(suppliesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Supplies in the database
        List<Supplies> suppliesList = suppliesRepository.findAll();
        assertThat(suppliesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSupplies() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList
        restSuppliesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplies.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].property").value(hasItem(DEFAULT_PROPERTY)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)));
    }

    @Test
    @Transactional
    void getSupplies() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get the supplies
        restSuppliesMockMvc
            .perform(get(ENTITY_API_URL_ID, supplies.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(supplies.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.property").value(DEFAULT_PROPERTY))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.enable").value(DEFAULT_ENABLE));
    }

    @Test
    @Transactional
    void getNonExistingSupplies() throws Exception {
        // Get the supplies
        restSuppliesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSupplies() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        int databaseSizeBeforeUpdate = suppliesRepository.findAll().size();

        // Update the supplies
        Supplies updatedSupplies = suppliesRepository.findById(supplies.getId()).get();
        // Disconnect from session so that the updates on updatedSupplies are not directly saved in db
        em.detach(updatedSupplies);
        updatedSupplies.name(UPDATED_NAME).property(UPDATED_PROPERTY).type(UPDATED_TYPE).enable(UPDATED_ENABLE);
        SuppliesDTO suppliesDTO = suppliesMapper.toDto(updatedSupplies);

        restSuppliesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, suppliesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(suppliesDTO))
            )
            .andExpect(status().isOk());

        // Validate the Supplies in the database
        List<Supplies> suppliesList = suppliesRepository.findAll();
        assertThat(suppliesList).hasSize(databaseSizeBeforeUpdate);
        Supplies testSupplies = suppliesList.get(suppliesList.size() - 1);
        assertThat(testSupplies.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSupplies.getProperty()).isEqualTo(UPDATED_PROPERTY);
        assertThat(testSupplies.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSupplies.getEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void putNonExistingSupplies() throws Exception {
        int databaseSizeBeforeUpdate = suppliesRepository.findAll().size();
        supplies.setId(count.incrementAndGet());

        // Create the Supplies
        SuppliesDTO suppliesDTO = suppliesMapper.toDto(supplies);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSuppliesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, suppliesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(suppliesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplies in the database
        List<Supplies> suppliesList = suppliesRepository.findAll();
        assertThat(suppliesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSupplies() throws Exception {
        int databaseSizeBeforeUpdate = suppliesRepository.findAll().size();
        supplies.setId(count.incrementAndGet());

        // Create the Supplies
        SuppliesDTO suppliesDTO = suppliesMapper.toDto(supplies);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSuppliesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(suppliesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplies in the database
        List<Supplies> suppliesList = suppliesRepository.findAll();
        assertThat(suppliesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSupplies() throws Exception {
        int databaseSizeBeforeUpdate = suppliesRepository.findAll().size();
        supplies.setId(count.incrementAndGet());

        // Create the Supplies
        SuppliesDTO suppliesDTO = suppliesMapper.toDto(supplies);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSuppliesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(suppliesDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Supplies in the database
        List<Supplies> suppliesList = suppliesRepository.findAll();
        assertThat(suppliesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSuppliesWithPatch() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        int databaseSizeBeforeUpdate = suppliesRepository.findAll().size();

        // Update the supplies using partial update
        Supplies partialUpdatedSupplies = new Supplies();
        partialUpdatedSupplies.setId(supplies.getId());

        partialUpdatedSupplies.property(UPDATED_PROPERTY);

        restSuppliesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplies.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSupplies))
            )
            .andExpect(status().isOk());

        // Validate the Supplies in the database
        List<Supplies> suppliesList = suppliesRepository.findAll();
        assertThat(suppliesList).hasSize(databaseSizeBeforeUpdate);
        Supplies testSupplies = suppliesList.get(suppliesList.size() - 1);
        assertThat(testSupplies.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSupplies.getProperty()).isEqualTo(UPDATED_PROPERTY);
        assertThat(testSupplies.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSupplies.getEnable()).isEqualTo(DEFAULT_ENABLE);
    }

    @Test
    @Transactional
    void fullUpdateSuppliesWithPatch() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        int databaseSizeBeforeUpdate = suppliesRepository.findAll().size();

        // Update the supplies using partial update
        Supplies partialUpdatedSupplies = new Supplies();
        partialUpdatedSupplies.setId(supplies.getId());

        partialUpdatedSupplies.name(UPDATED_NAME).property(UPDATED_PROPERTY).type(UPDATED_TYPE).enable(UPDATED_ENABLE);

        restSuppliesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplies.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSupplies))
            )
            .andExpect(status().isOk());

        // Validate the Supplies in the database
        List<Supplies> suppliesList = suppliesRepository.findAll();
        assertThat(suppliesList).hasSize(databaseSizeBeforeUpdate);
        Supplies testSupplies = suppliesList.get(suppliesList.size() - 1);
        assertThat(testSupplies.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSupplies.getProperty()).isEqualTo(UPDATED_PROPERTY);
        assertThat(testSupplies.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSupplies.getEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void patchNonExistingSupplies() throws Exception {
        int databaseSizeBeforeUpdate = suppliesRepository.findAll().size();
        supplies.setId(count.incrementAndGet());

        // Create the Supplies
        SuppliesDTO suppliesDTO = suppliesMapper.toDto(supplies);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSuppliesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, suppliesDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(suppliesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplies in the database
        List<Supplies> suppliesList = suppliesRepository.findAll();
        assertThat(suppliesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSupplies() throws Exception {
        int databaseSizeBeforeUpdate = suppliesRepository.findAll().size();
        supplies.setId(count.incrementAndGet());

        // Create the Supplies
        SuppliesDTO suppliesDTO = suppliesMapper.toDto(supplies);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSuppliesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(suppliesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplies in the database
        List<Supplies> suppliesList = suppliesRepository.findAll();
        assertThat(suppliesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSupplies() throws Exception {
        int databaseSizeBeforeUpdate = suppliesRepository.findAll().size();
        supplies.setId(count.incrementAndGet());

        // Create the Supplies
        SuppliesDTO suppliesDTO = suppliesMapper.toDto(supplies);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSuppliesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(suppliesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Supplies in the database
        List<Supplies> suppliesList = suppliesRepository.findAll();
        assertThat(suppliesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSupplies() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        int databaseSizeBeforeDelete = suppliesRepository.findAll().size();

        // Delete the supplies
        restSuppliesMockMvc
            .perform(delete(ENTITY_API_URL_ID, supplies.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Supplies> suppliesList = suppliesRepository.findAll();
        assertThat(suppliesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
