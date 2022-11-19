package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Crops;
import com.lnduy.agriculture.repository.CropsRepository;
import com.lnduy.agriculture.service.dto.CropsDTO;
import com.lnduy.agriculture.service.mapper.CropsMapper;
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
 * Integration tests for the {@link CropsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CropsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Float DEFAULT_VOLUME = 1F;
    private static final Float UPDATED_VOLUME = 2F;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/crops";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CropsRepository cropsRepository;

    @Autowired
    private CropsMapper cropsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCropsMockMvc;

    private Crops crops;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Crops createEntity(EntityManager em) {
        Crops crops = new Crops().name(DEFAULT_NAME).volume(DEFAULT_VOLUME).unit(DEFAULT_UNIT).description(DEFAULT_DESCRIPTION);
        return crops;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Crops createUpdatedEntity(EntityManager em) {
        Crops crops = new Crops().name(UPDATED_NAME).volume(UPDATED_VOLUME).unit(UPDATED_UNIT).description(UPDATED_DESCRIPTION);
        return crops;
    }

    @BeforeEach
    public void initTest() {
        crops = createEntity(em);
    }

    @Test
    @Transactional
    void createCrops() throws Exception {
        int databaseSizeBeforeCreate = cropsRepository.findAll().size();
        // Create the Crops
        CropsDTO cropsDTO = cropsMapper.toDto(crops);
        restCropsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cropsDTO)))
            .andExpect(status().isCreated());

        // Validate the Crops in the database
        List<Crops> cropsList = cropsRepository.findAll();
        assertThat(cropsList).hasSize(databaseSizeBeforeCreate + 1);
        Crops testCrops = cropsList.get(cropsList.size() - 1);
        assertThat(testCrops.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCrops.getVolume()).isEqualTo(DEFAULT_VOLUME);
        assertThat(testCrops.getUnit()).isEqualTo(DEFAULT_UNIT);
        assertThat(testCrops.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createCropsWithExistingId() throws Exception {
        // Create the Crops with an existing ID
        crops.setId(1L);
        CropsDTO cropsDTO = cropsMapper.toDto(crops);

        int databaseSizeBeforeCreate = cropsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCropsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cropsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Crops in the database
        List<Crops> cropsList = cropsRepository.findAll();
        assertThat(cropsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCrops() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList
        restCropsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(crops.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.doubleValue())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getCrops() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get the crops
        restCropsMockMvc
            .perform(get(ENTITY_API_URL_ID, crops.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(crops.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.volume").value(DEFAULT_VOLUME.doubleValue()))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingCrops() throws Exception {
        // Get the crops
        restCropsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCrops() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        int databaseSizeBeforeUpdate = cropsRepository.findAll().size();

        // Update the crops
        Crops updatedCrops = cropsRepository.findById(crops.getId()).get();
        // Disconnect from session so that the updates on updatedCrops are not directly saved in db
        em.detach(updatedCrops);
        updatedCrops.name(UPDATED_NAME).volume(UPDATED_VOLUME).unit(UPDATED_UNIT).description(UPDATED_DESCRIPTION);
        CropsDTO cropsDTO = cropsMapper.toDto(updatedCrops);

        restCropsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cropsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cropsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Crops in the database
        List<Crops> cropsList = cropsRepository.findAll();
        assertThat(cropsList).hasSize(databaseSizeBeforeUpdate);
        Crops testCrops = cropsList.get(cropsList.size() - 1);
        assertThat(testCrops.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCrops.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testCrops.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testCrops.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingCrops() throws Exception {
        int databaseSizeBeforeUpdate = cropsRepository.findAll().size();
        crops.setId(count.incrementAndGet());

        // Create the Crops
        CropsDTO cropsDTO = cropsMapper.toDto(crops);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCropsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cropsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Crops in the database
        List<Crops> cropsList = cropsRepository.findAll();
        assertThat(cropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCrops() throws Exception {
        int databaseSizeBeforeUpdate = cropsRepository.findAll().size();
        crops.setId(count.incrementAndGet());

        // Create the Crops
        CropsDTO cropsDTO = cropsMapper.toDto(crops);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCropsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Crops in the database
        List<Crops> cropsList = cropsRepository.findAll();
        assertThat(cropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCrops() throws Exception {
        int databaseSizeBeforeUpdate = cropsRepository.findAll().size();
        crops.setId(count.incrementAndGet());

        // Create the Crops
        CropsDTO cropsDTO = cropsMapper.toDto(crops);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCropsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cropsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Crops in the database
        List<Crops> cropsList = cropsRepository.findAll();
        assertThat(cropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCropsWithPatch() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        int databaseSizeBeforeUpdate = cropsRepository.findAll().size();

        // Update the crops using partial update
        Crops partialUpdatedCrops = new Crops();
        partialUpdatedCrops.setId(crops.getId());

        partialUpdatedCrops.volume(UPDATED_VOLUME).unit(UPDATED_UNIT);

        restCropsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCrops.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCrops))
            )
            .andExpect(status().isOk());

        // Validate the Crops in the database
        List<Crops> cropsList = cropsRepository.findAll();
        assertThat(cropsList).hasSize(databaseSizeBeforeUpdate);
        Crops testCrops = cropsList.get(cropsList.size() - 1);
        assertThat(testCrops.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCrops.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testCrops.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testCrops.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateCropsWithPatch() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        int databaseSizeBeforeUpdate = cropsRepository.findAll().size();

        // Update the crops using partial update
        Crops partialUpdatedCrops = new Crops();
        partialUpdatedCrops.setId(crops.getId());

        partialUpdatedCrops.name(UPDATED_NAME).volume(UPDATED_VOLUME).unit(UPDATED_UNIT).description(UPDATED_DESCRIPTION);

        restCropsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCrops.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCrops))
            )
            .andExpect(status().isOk());

        // Validate the Crops in the database
        List<Crops> cropsList = cropsRepository.findAll();
        assertThat(cropsList).hasSize(databaseSizeBeforeUpdate);
        Crops testCrops = cropsList.get(cropsList.size() - 1);
        assertThat(testCrops.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCrops.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testCrops.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testCrops.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingCrops() throws Exception {
        int databaseSizeBeforeUpdate = cropsRepository.findAll().size();
        crops.setId(count.incrementAndGet());

        // Create the Crops
        CropsDTO cropsDTO = cropsMapper.toDto(crops);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCropsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cropsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Crops in the database
        List<Crops> cropsList = cropsRepository.findAll();
        assertThat(cropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCrops() throws Exception {
        int databaseSizeBeforeUpdate = cropsRepository.findAll().size();
        crops.setId(count.incrementAndGet());

        // Create the Crops
        CropsDTO cropsDTO = cropsMapper.toDto(crops);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCropsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Crops in the database
        List<Crops> cropsList = cropsRepository.findAll();
        assertThat(cropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCrops() throws Exception {
        int databaseSizeBeforeUpdate = cropsRepository.findAll().size();
        crops.setId(count.incrementAndGet());

        // Create the Crops
        CropsDTO cropsDTO = cropsMapper.toDto(crops);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCropsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cropsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Crops in the database
        List<Crops> cropsList = cropsRepository.findAll();
        assertThat(cropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCrops() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        int databaseSizeBeforeDelete = cropsRepository.findAll().size();

        // Delete the crops
        restCropsMockMvc
            .perform(delete(ENTITY_API_URL_ID, crops.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Crops> cropsList = cropsRepository.findAll();
        assertThat(cropsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
