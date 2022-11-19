package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Fertilizers;
import com.lnduy.agriculture.repository.FertilizersRepository;
import com.lnduy.agriculture.service.dto.FertilizersDTO;
import com.lnduy.agriculture.service.mapper.FertilizersMapper;
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
 * Integration tests for the {@link FertilizersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FertilizersResourceIT {

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

    private static final String ENTITY_API_URL = "/api/fertilizers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FertilizersRepository fertilizersRepository;

    @Autowired
    private FertilizersMapper fertilizersMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFertilizersMockMvc;

    private Fertilizers fertilizers;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Fertilizers createEntity(EntityManager em) {
        Fertilizers fertilizers = new Fertilizers()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .type(DEFAULT_TYPE)
            .volume(DEFAULT_VOLUME)
            .unit(DEFAULT_UNIT)
            .enable(DEFAULT_ENABLE);
        return fertilizers;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Fertilizers createUpdatedEntity(EntityManager em) {
        Fertilizers fertilizers = new Fertilizers()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .enable(UPDATED_ENABLE);
        return fertilizers;
    }

    @BeforeEach
    public void initTest() {
        fertilizers = createEntity(em);
    }

    @Test
    @Transactional
    void createFertilizers() throws Exception {
        int databaseSizeBeforeCreate = fertilizersRepository.findAll().size();
        // Create the Fertilizers
        FertilizersDTO fertilizersDTO = fertilizersMapper.toDto(fertilizers);
        restFertilizersMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fertilizersDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Fertilizers in the database
        List<Fertilizers> fertilizersList = fertilizersRepository.findAll();
        assertThat(fertilizersList).hasSize(databaseSizeBeforeCreate + 1);
        Fertilizers testFertilizers = fertilizersList.get(fertilizersList.size() - 1);
        assertThat(testFertilizers.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFertilizers.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFertilizers.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testFertilizers.getVolume()).isEqualTo(DEFAULT_VOLUME);
        assertThat(testFertilizers.getUnit()).isEqualTo(DEFAULT_UNIT);
        assertThat(testFertilizers.getEnable()).isEqualTo(DEFAULT_ENABLE);
    }

    @Test
    @Transactional
    void createFertilizersWithExistingId() throws Exception {
        // Create the Fertilizers with an existing ID
        fertilizers.setId(1L);
        FertilizersDTO fertilizersDTO = fertilizersMapper.toDto(fertilizers);

        int databaseSizeBeforeCreate = fertilizersRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFertilizersMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fertilizersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fertilizers in the database
        List<Fertilizers> fertilizersList = fertilizersRepository.findAll();
        assertThat(fertilizersList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFertilizers() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList
        restFertilizersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fertilizers.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.doubleValue())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)));
    }

    @Test
    @Transactional
    void getFertilizers() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get the fertilizers
        restFertilizersMockMvc
            .perform(get(ENTITY_API_URL_ID, fertilizers.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fertilizers.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.volume").value(DEFAULT_VOLUME.doubleValue()))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.enable").value(DEFAULT_ENABLE));
    }

    @Test
    @Transactional
    void getNonExistingFertilizers() throws Exception {
        // Get the fertilizers
        restFertilizersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFertilizers() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        int databaseSizeBeforeUpdate = fertilizersRepository.findAll().size();

        // Update the fertilizers
        Fertilizers updatedFertilizers = fertilizersRepository.findById(fertilizers.getId()).get();
        // Disconnect from session so that the updates on updatedFertilizers are not directly saved in db
        em.detach(updatedFertilizers);
        updatedFertilizers
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .enable(UPDATED_ENABLE);
        FertilizersDTO fertilizersDTO = fertilizersMapper.toDto(updatedFertilizers);

        restFertilizersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fertilizersDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fertilizersDTO))
            )
            .andExpect(status().isOk());

        // Validate the Fertilizers in the database
        List<Fertilizers> fertilizersList = fertilizersRepository.findAll();
        assertThat(fertilizersList).hasSize(databaseSizeBeforeUpdate);
        Fertilizers testFertilizers = fertilizersList.get(fertilizersList.size() - 1);
        assertThat(testFertilizers.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFertilizers.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFertilizers.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testFertilizers.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testFertilizers.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testFertilizers.getEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void putNonExistingFertilizers() throws Exception {
        int databaseSizeBeforeUpdate = fertilizersRepository.findAll().size();
        fertilizers.setId(count.incrementAndGet());

        // Create the Fertilizers
        FertilizersDTO fertilizersDTO = fertilizersMapper.toDto(fertilizers);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFertilizersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fertilizersDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fertilizersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fertilizers in the database
        List<Fertilizers> fertilizersList = fertilizersRepository.findAll();
        assertThat(fertilizersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFertilizers() throws Exception {
        int databaseSizeBeforeUpdate = fertilizersRepository.findAll().size();
        fertilizers.setId(count.incrementAndGet());

        // Create the Fertilizers
        FertilizersDTO fertilizersDTO = fertilizersMapper.toDto(fertilizers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFertilizersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fertilizersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fertilizers in the database
        List<Fertilizers> fertilizersList = fertilizersRepository.findAll();
        assertThat(fertilizersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFertilizers() throws Exception {
        int databaseSizeBeforeUpdate = fertilizersRepository.findAll().size();
        fertilizers.setId(count.incrementAndGet());

        // Create the Fertilizers
        FertilizersDTO fertilizersDTO = fertilizersMapper.toDto(fertilizers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFertilizersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fertilizersDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Fertilizers in the database
        List<Fertilizers> fertilizersList = fertilizersRepository.findAll();
        assertThat(fertilizersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFertilizersWithPatch() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        int databaseSizeBeforeUpdate = fertilizersRepository.findAll().size();

        // Update the fertilizers using partial update
        Fertilizers partialUpdatedFertilizers = new Fertilizers();
        partialUpdatedFertilizers.setId(fertilizers.getId());

        partialUpdatedFertilizers.name(UPDATED_NAME).type(UPDATED_TYPE).volume(UPDATED_VOLUME).unit(UPDATED_UNIT).enable(UPDATED_ENABLE);

        restFertilizersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFertilizers.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFertilizers))
            )
            .andExpect(status().isOk());

        // Validate the Fertilizers in the database
        List<Fertilizers> fertilizersList = fertilizersRepository.findAll();
        assertThat(fertilizersList).hasSize(databaseSizeBeforeUpdate);
        Fertilizers testFertilizers = fertilizersList.get(fertilizersList.size() - 1);
        assertThat(testFertilizers.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFertilizers.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFertilizers.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testFertilizers.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testFertilizers.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testFertilizers.getEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void fullUpdateFertilizersWithPatch() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        int databaseSizeBeforeUpdate = fertilizersRepository.findAll().size();

        // Update the fertilizers using partial update
        Fertilizers partialUpdatedFertilizers = new Fertilizers();
        partialUpdatedFertilizers.setId(fertilizers.getId());

        partialUpdatedFertilizers
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .enable(UPDATED_ENABLE);

        restFertilizersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFertilizers.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFertilizers))
            )
            .andExpect(status().isOk());

        // Validate the Fertilizers in the database
        List<Fertilizers> fertilizersList = fertilizersRepository.findAll();
        assertThat(fertilizersList).hasSize(databaseSizeBeforeUpdate);
        Fertilizers testFertilizers = fertilizersList.get(fertilizersList.size() - 1);
        assertThat(testFertilizers.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFertilizers.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFertilizers.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testFertilizers.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testFertilizers.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testFertilizers.getEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void patchNonExistingFertilizers() throws Exception {
        int databaseSizeBeforeUpdate = fertilizersRepository.findAll().size();
        fertilizers.setId(count.incrementAndGet());

        // Create the Fertilizers
        FertilizersDTO fertilizersDTO = fertilizersMapper.toDto(fertilizers);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFertilizersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fertilizersDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fertilizersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fertilizers in the database
        List<Fertilizers> fertilizersList = fertilizersRepository.findAll();
        assertThat(fertilizersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFertilizers() throws Exception {
        int databaseSizeBeforeUpdate = fertilizersRepository.findAll().size();
        fertilizers.setId(count.incrementAndGet());

        // Create the Fertilizers
        FertilizersDTO fertilizersDTO = fertilizersMapper.toDto(fertilizers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFertilizersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fertilizersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fertilizers in the database
        List<Fertilizers> fertilizersList = fertilizersRepository.findAll();
        assertThat(fertilizersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFertilizers() throws Exception {
        int databaseSizeBeforeUpdate = fertilizersRepository.findAll().size();
        fertilizers.setId(count.incrementAndGet());

        // Create the Fertilizers
        FertilizersDTO fertilizersDTO = fertilizersMapper.toDto(fertilizers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFertilizersMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(fertilizersDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Fertilizers in the database
        List<Fertilizers> fertilizersList = fertilizersRepository.findAll();
        assertThat(fertilizersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFertilizers() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        int databaseSizeBeforeDelete = fertilizersRepository.findAll().size();

        // Delete the fertilizers
        restFertilizersMockMvc
            .perform(delete(ENTITY_API_URL_ID, fertilizers.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Fertilizers> fertilizersList = fertilizersRepository.findAll();
        assertThat(fertilizersList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
