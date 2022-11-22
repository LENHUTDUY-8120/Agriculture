package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.SoilType;
import com.lnduy.agriculture.repository.SoilTypeRepository;
import com.lnduy.agriculture.service.criteria.SoilTypeCriteria;
import com.lnduy.agriculture.service.dto.SoilTypeDTO;
import com.lnduy.agriculture.service.mapper.SoilTypeMapper;
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
 * Integration tests for the {@link SoilTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SoilTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTIONS = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTIONS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/soil-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SoilTypeRepository soilTypeRepository;

    @Autowired
    private SoilTypeMapper soilTypeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSoilTypeMockMvc;

    private SoilType soilType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SoilType createEntity(EntityManager em) {
        SoilType soilType = new SoilType().name(DEFAULT_NAME).descriptions(DEFAULT_DESCRIPTIONS);
        return soilType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SoilType createUpdatedEntity(EntityManager em) {
        SoilType soilType = new SoilType().name(UPDATED_NAME).descriptions(UPDATED_DESCRIPTIONS);
        return soilType;
    }

    @BeforeEach
    public void initTest() {
        soilType = createEntity(em);
    }

    @Test
    @Transactional
    void createSoilType() throws Exception {
        int databaseSizeBeforeCreate = soilTypeRepository.findAll().size();
        // Create the SoilType
        SoilTypeDTO soilTypeDTO = soilTypeMapper.toDto(soilType);
        restSoilTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(soilTypeDTO)))
            .andExpect(status().isCreated());

        // Validate the SoilType in the database
        List<SoilType> soilTypeList = soilTypeRepository.findAll();
        assertThat(soilTypeList).hasSize(databaseSizeBeforeCreate + 1);
        SoilType testSoilType = soilTypeList.get(soilTypeList.size() - 1);
        assertThat(testSoilType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSoilType.getDescriptions()).isEqualTo(DEFAULT_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void createSoilTypeWithExistingId() throws Exception {
        // Create the SoilType with an existing ID
        soilType.setId(1L);
        SoilTypeDTO soilTypeDTO = soilTypeMapper.toDto(soilType);

        int databaseSizeBeforeCreate = soilTypeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSoilTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(soilTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SoilType in the database
        List<SoilType> soilTypeList = soilTypeRepository.findAll();
        assertThat(soilTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSoilTypes() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        // Get all the soilTypeList
        restSoilTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(soilType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].descriptions").value(hasItem(DEFAULT_DESCRIPTIONS)));
    }

    @Test
    @Transactional
    void getSoilType() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        // Get the soilType
        restSoilTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, soilType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(soilType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.descriptions").value(DEFAULT_DESCRIPTIONS));
    }

    @Test
    @Transactional
    void getSoilTypesByIdFiltering() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        Long id = soilType.getId();

        defaultSoilTypeShouldBeFound("id.equals=" + id);
        defaultSoilTypeShouldNotBeFound("id.notEquals=" + id);

        defaultSoilTypeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSoilTypeShouldNotBeFound("id.greaterThan=" + id);

        defaultSoilTypeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSoilTypeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSoilTypesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        // Get all the soilTypeList where name equals to DEFAULT_NAME
        defaultSoilTypeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the soilTypeList where name equals to UPDATED_NAME
        defaultSoilTypeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSoilTypesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        // Get all the soilTypeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSoilTypeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the soilTypeList where name equals to UPDATED_NAME
        defaultSoilTypeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSoilTypesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        // Get all the soilTypeList where name is not null
        defaultSoilTypeShouldBeFound("name.specified=true");

        // Get all the soilTypeList where name is null
        defaultSoilTypeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllSoilTypesByNameContainsSomething() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        // Get all the soilTypeList where name contains DEFAULT_NAME
        defaultSoilTypeShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the soilTypeList where name contains UPDATED_NAME
        defaultSoilTypeShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSoilTypesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        // Get all the soilTypeList where name does not contain DEFAULT_NAME
        defaultSoilTypeShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the soilTypeList where name does not contain UPDATED_NAME
        defaultSoilTypeShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSoilTypesByDescriptionsIsEqualToSomething() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        // Get all the soilTypeList where descriptions equals to DEFAULT_DESCRIPTIONS
        defaultSoilTypeShouldBeFound("descriptions.equals=" + DEFAULT_DESCRIPTIONS);

        // Get all the soilTypeList where descriptions equals to UPDATED_DESCRIPTIONS
        defaultSoilTypeShouldNotBeFound("descriptions.equals=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllSoilTypesByDescriptionsIsInShouldWork() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        // Get all the soilTypeList where descriptions in DEFAULT_DESCRIPTIONS or UPDATED_DESCRIPTIONS
        defaultSoilTypeShouldBeFound("descriptions.in=" + DEFAULT_DESCRIPTIONS + "," + UPDATED_DESCRIPTIONS);

        // Get all the soilTypeList where descriptions equals to UPDATED_DESCRIPTIONS
        defaultSoilTypeShouldNotBeFound("descriptions.in=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllSoilTypesByDescriptionsIsNullOrNotNull() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        // Get all the soilTypeList where descriptions is not null
        defaultSoilTypeShouldBeFound("descriptions.specified=true");

        // Get all the soilTypeList where descriptions is null
        defaultSoilTypeShouldNotBeFound("descriptions.specified=false");
    }

    @Test
    @Transactional
    void getAllSoilTypesByDescriptionsContainsSomething() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        // Get all the soilTypeList where descriptions contains DEFAULT_DESCRIPTIONS
        defaultSoilTypeShouldBeFound("descriptions.contains=" + DEFAULT_DESCRIPTIONS);

        // Get all the soilTypeList where descriptions contains UPDATED_DESCRIPTIONS
        defaultSoilTypeShouldNotBeFound("descriptions.contains=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllSoilTypesByDescriptionsNotContainsSomething() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        // Get all the soilTypeList where descriptions does not contain DEFAULT_DESCRIPTIONS
        defaultSoilTypeShouldNotBeFound("descriptions.doesNotContain=" + DEFAULT_DESCRIPTIONS);

        // Get all the soilTypeList where descriptions does not contain UPDATED_DESCRIPTIONS
        defaultSoilTypeShouldBeFound("descriptions.doesNotContain=" + UPDATED_DESCRIPTIONS);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSoilTypeShouldBeFound(String filter) throws Exception {
        restSoilTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(soilType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].descriptions").value(hasItem(DEFAULT_DESCRIPTIONS)));

        // Check, that the count call also returns 1
        restSoilTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSoilTypeShouldNotBeFound(String filter) throws Exception {
        restSoilTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSoilTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSoilType() throws Exception {
        // Get the soilType
        restSoilTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSoilType() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        int databaseSizeBeforeUpdate = soilTypeRepository.findAll().size();

        // Update the soilType
        SoilType updatedSoilType = soilTypeRepository.findById(soilType.getId()).get();
        // Disconnect from session so that the updates on updatedSoilType are not directly saved in db
        em.detach(updatedSoilType);
        updatedSoilType.name(UPDATED_NAME).descriptions(UPDATED_DESCRIPTIONS);
        SoilTypeDTO soilTypeDTO = soilTypeMapper.toDto(updatedSoilType);

        restSoilTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, soilTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(soilTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the SoilType in the database
        List<SoilType> soilTypeList = soilTypeRepository.findAll();
        assertThat(soilTypeList).hasSize(databaseSizeBeforeUpdate);
        SoilType testSoilType = soilTypeList.get(soilTypeList.size() - 1);
        assertThat(testSoilType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSoilType.getDescriptions()).isEqualTo(UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void putNonExistingSoilType() throws Exception {
        int databaseSizeBeforeUpdate = soilTypeRepository.findAll().size();
        soilType.setId(count.incrementAndGet());

        // Create the SoilType
        SoilTypeDTO soilTypeDTO = soilTypeMapper.toDto(soilType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSoilTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, soilTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(soilTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SoilType in the database
        List<SoilType> soilTypeList = soilTypeRepository.findAll();
        assertThat(soilTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSoilType() throws Exception {
        int databaseSizeBeforeUpdate = soilTypeRepository.findAll().size();
        soilType.setId(count.incrementAndGet());

        // Create the SoilType
        SoilTypeDTO soilTypeDTO = soilTypeMapper.toDto(soilType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoilTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(soilTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SoilType in the database
        List<SoilType> soilTypeList = soilTypeRepository.findAll();
        assertThat(soilTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSoilType() throws Exception {
        int databaseSizeBeforeUpdate = soilTypeRepository.findAll().size();
        soilType.setId(count.incrementAndGet());

        // Create the SoilType
        SoilTypeDTO soilTypeDTO = soilTypeMapper.toDto(soilType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoilTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(soilTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SoilType in the database
        List<SoilType> soilTypeList = soilTypeRepository.findAll();
        assertThat(soilTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSoilTypeWithPatch() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        int databaseSizeBeforeUpdate = soilTypeRepository.findAll().size();

        // Update the soilType using partial update
        SoilType partialUpdatedSoilType = new SoilType();
        partialUpdatedSoilType.setId(soilType.getId());

        restSoilTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSoilType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSoilType))
            )
            .andExpect(status().isOk());

        // Validate the SoilType in the database
        List<SoilType> soilTypeList = soilTypeRepository.findAll();
        assertThat(soilTypeList).hasSize(databaseSizeBeforeUpdate);
        SoilType testSoilType = soilTypeList.get(soilTypeList.size() - 1);
        assertThat(testSoilType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSoilType.getDescriptions()).isEqualTo(DEFAULT_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void fullUpdateSoilTypeWithPatch() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        int databaseSizeBeforeUpdate = soilTypeRepository.findAll().size();

        // Update the soilType using partial update
        SoilType partialUpdatedSoilType = new SoilType();
        partialUpdatedSoilType.setId(soilType.getId());

        partialUpdatedSoilType.name(UPDATED_NAME).descriptions(UPDATED_DESCRIPTIONS);

        restSoilTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSoilType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSoilType))
            )
            .andExpect(status().isOk());

        // Validate the SoilType in the database
        List<SoilType> soilTypeList = soilTypeRepository.findAll();
        assertThat(soilTypeList).hasSize(databaseSizeBeforeUpdate);
        SoilType testSoilType = soilTypeList.get(soilTypeList.size() - 1);
        assertThat(testSoilType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSoilType.getDescriptions()).isEqualTo(UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void patchNonExistingSoilType() throws Exception {
        int databaseSizeBeforeUpdate = soilTypeRepository.findAll().size();
        soilType.setId(count.incrementAndGet());

        // Create the SoilType
        SoilTypeDTO soilTypeDTO = soilTypeMapper.toDto(soilType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSoilTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, soilTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(soilTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SoilType in the database
        List<SoilType> soilTypeList = soilTypeRepository.findAll();
        assertThat(soilTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSoilType() throws Exception {
        int databaseSizeBeforeUpdate = soilTypeRepository.findAll().size();
        soilType.setId(count.incrementAndGet());

        // Create the SoilType
        SoilTypeDTO soilTypeDTO = soilTypeMapper.toDto(soilType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoilTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(soilTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SoilType in the database
        List<SoilType> soilTypeList = soilTypeRepository.findAll();
        assertThat(soilTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSoilType() throws Exception {
        int databaseSizeBeforeUpdate = soilTypeRepository.findAll().size();
        soilType.setId(count.incrementAndGet());

        // Create the SoilType
        SoilTypeDTO soilTypeDTO = soilTypeMapper.toDto(soilType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoilTypeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(soilTypeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SoilType in the database
        List<SoilType> soilTypeList = soilTypeRepository.findAll();
        assertThat(soilTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSoilType() throws Exception {
        // Initialize the database
        soilTypeRepository.saveAndFlush(soilType);

        int databaseSizeBeforeDelete = soilTypeRepository.findAll().size();

        // Delete the soilType
        restSoilTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, soilType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SoilType> soilTypeList = soilTypeRepository.findAll();
        assertThat(soilTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
