package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.DeviceCategory;
import com.lnduy.agriculture.repository.DeviceCategoryRepository;
import com.lnduy.agriculture.service.dto.DeviceCategoryDTO;
import com.lnduy.agriculture.service.mapper.DeviceCategoryMapper;
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
 * Integration tests for the {@link DeviceCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DeviceCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/device-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DeviceCategoryRepository deviceCategoryRepository;

    @Autowired
    private DeviceCategoryMapper deviceCategoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeviceCategoryMockMvc;

    private DeviceCategory deviceCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DeviceCategory createEntity(EntityManager em) {
        DeviceCategory deviceCategory = new DeviceCategory().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return deviceCategory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DeviceCategory createUpdatedEntity(EntityManager em) {
        DeviceCategory deviceCategory = new DeviceCategory().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return deviceCategory;
    }

    @BeforeEach
    public void initTest() {
        deviceCategory = createEntity(em);
    }

    @Test
    @Transactional
    void createDeviceCategory() throws Exception {
        int databaseSizeBeforeCreate = deviceCategoryRepository.findAll().size();
        // Create the DeviceCategory
        DeviceCategoryDTO deviceCategoryDTO = deviceCategoryMapper.toDto(deviceCategory);
        restDeviceCategoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deviceCategoryDTO))
            )
            .andExpect(status().isCreated());

        // Validate the DeviceCategory in the database
        List<DeviceCategory> deviceCategoryList = deviceCategoryRepository.findAll();
        assertThat(deviceCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        DeviceCategory testDeviceCategory = deviceCategoryList.get(deviceCategoryList.size() - 1);
        assertThat(testDeviceCategory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDeviceCategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createDeviceCategoryWithExistingId() throws Exception {
        // Create the DeviceCategory with an existing ID
        deviceCategory.setId(1L);
        DeviceCategoryDTO deviceCategoryDTO = deviceCategoryMapper.toDto(deviceCategory);

        int databaseSizeBeforeCreate = deviceCategoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeviceCategoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deviceCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeviceCategory in the database
        List<DeviceCategory> deviceCategoryList = deviceCategoryRepository.findAll();
        assertThat(deviceCategoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDeviceCategories() throws Exception {
        // Initialize the database
        deviceCategoryRepository.saveAndFlush(deviceCategory);

        // Get all the deviceCategoryList
        restDeviceCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deviceCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getDeviceCategory() throws Exception {
        // Initialize the database
        deviceCategoryRepository.saveAndFlush(deviceCategory);

        // Get the deviceCategory
        restDeviceCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, deviceCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(deviceCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingDeviceCategory() throws Exception {
        // Get the deviceCategory
        restDeviceCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDeviceCategory() throws Exception {
        // Initialize the database
        deviceCategoryRepository.saveAndFlush(deviceCategory);

        int databaseSizeBeforeUpdate = deviceCategoryRepository.findAll().size();

        // Update the deviceCategory
        DeviceCategory updatedDeviceCategory = deviceCategoryRepository.findById(deviceCategory.getId()).get();
        // Disconnect from session so that the updates on updatedDeviceCategory are not directly saved in db
        em.detach(updatedDeviceCategory);
        updatedDeviceCategory.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        DeviceCategoryDTO deviceCategoryDTO = deviceCategoryMapper.toDto(updatedDeviceCategory);

        restDeviceCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deviceCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deviceCategoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the DeviceCategory in the database
        List<DeviceCategory> deviceCategoryList = deviceCategoryRepository.findAll();
        assertThat(deviceCategoryList).hasSize(databaseSizeBeforeUpdate);
        DeviceCategory testDeviceCategory = deviceCategoryList.get(deviceCategoryList.size() - 1);
        assertThat(testDeviceCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDeviceCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingDeviceCategory() throws Exception {
        int databaseSizeBeforeUpdate = deviceCategoryRepository.findAll().size();
        deviceCategory.setId(count.incrementAndGet());

        // Create the DeviceCategory
        DeviceCategoryDTO deviceCategoryDTO = deviceCategoryMapper.toDto(deviceCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeviceCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deviceCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deviceCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeviceCategory in the database
        List<DeviceCategory> deviceCategoryList = deviceCategoryRepository.findAll();
        assertThat(deviceCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDeviceCategory() throws Exception {
        int databaseSizeBeforeUpdate = deviceCategoryRepository.findAll().size();
        deviceCategory.setId(count.incrementAndGet());

        // Create the DeviceCategory
        DeviceCategoryDTO deviceCategoryDTO = deviceCategoryMapper.toDto(deviceCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeviceCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deviceCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeviceCategory in the database
        List<DeviceCategory> deviceCategoryList = deviceCategoryRepository.findAll();
        assertThat(deviceCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDeviceCategory() throws Exception {
        int databaseSizeBeforeUpdate = deviceCategoryRepository.findAll().size();
        deviceCategory.setId(count.incrementAndGet());

        // Create the DeviceCategory
        DeviceCategoryDTO deviceCategoryDTO = deviceCategoryMapper.toDto(deviceCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeviceCategoryMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deviceCategoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DeviceCategory in the database
        List<DeviceCategory> deviceCategoryList = deviceCategoryRepository.findAll();
        assertThat(deviceCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDeviceCategoryWithPatch() throws Exception {
        // Initialize the database
        deviceCategoryRepository.saveAndFlush(deviceCategory);

        int databaseSizeBeforeUpdate = deviceCategoryRepository.findAll().size();

        // Update the deviceCategory using partial update
        DeviceCategory partialUpdatedDeviceCategory = new DeviceCategory();
        partialUpdatedDeviceCategory.setId(deviceCategory.getId());

        partialUpdatedDeviceCategory.name(UPDATED_NAME);

        restDeviceCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDeviceCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDeviceCategory))
            )
            .andExpect(status().isOk());

        // Validate the DeviceCategory in the database
        List<DeviceCategory> deviceCategoryList = deviceCategoryRepository.findAll();
        assertThat(deviceCategoryList).hasSize(databaseSizeBeforeUpdate);
        DeviceCategory testDeviceCategory = deviceCategoryList.get(deviceCategoryList.size() - 1);
        assertThat(testDeviceCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDeviceCategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateDeviceCategoryWithPatch() throws Exception {
        // Initialize the database
        deviceCategoryRepository.saveAndFlush(deviceCategory);

        int databaseSizeBeforeUpdate = deviceCategoryRepository.findAll().size();

        // Update the deviceCategory using partial update
        DeviceCategory partialUpdatedDeviceCategory = new DeviceCategory();
        partialUpdatedDeviceCategory.setId(deviceCategory.getId());

        partialUpdatedDeviceCategory.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restDeviceCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDeviceCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDeviceCategory))
            )
            .andExpect(status().isOk());

        // Validate the DeviceCategory in the database
        List<DeviceCategory> deviceCategoryList = deviceCategoryRepository.findAll();
        assertThat(deviceCategoryList).hasSize(databaseSizeBeforeUpdate);
        DeviceCategory testDeviceCategory = deviceCategoryList.get(deviceCategoryList.size() - 1);
        assertThat(testDeviceCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDeviceCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingDeviceCategory() throws Exception {
        int databaseSizeBeforeUpdate = deviceCategoryRepository.findAll().size();
        deviceCategory.setId(count.incrementAndGet());

        // Create the DeviceCategory
        DeviceCategoryDTO deviceCategoryDTO = deviceCategoryMapper.toDto(deviceCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeviceCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, deviceCategoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(deviceCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeviceCategory in the database
        List<DeviceCategory> deviceCategoryList = deviceCategoryRepository.findAll();
        assertThat(deviceCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDeviceCategory() throws Exception {
        int databaseSizeBeforeUpdate = deviceCategoryRepository.findAll().size();
        deviceCategory.setId(count.incrementAndGet());

        // Create the DeviceCategory
        DeviceCategoryDTO deviceCategoryDTO = deviceCategoryMapper.toDto(deviceCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeviceCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(deviceCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeviceCategory in the database
        List<DeviceCategory> deviceCategoryList = deviceCategoryRepository.findAll();
        assertThat(deviceCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDeviceCategory() throws Exception {
        int databaseSizeBeforeUpdate = deviceCategoryRepository.findAll().size();
        deviceCategory.setId(count.incrementAndGet());

        // Create the DeviceCategory
        DeviceCategoryDTO deviceCategoryDTO = deviceCategoryMapper.toDto(deviceCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeviceCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(deviceCategoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DeviceCategory in the database
        List<DeviceCategory> deviceCategoryList = deviceCategoryRepository.findAll();
        assertThat(deviceCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDeviceCategory() throws Exception {
        // Initialize the database
        deviceCategoryRepository.saveAndFlush(deviceCategory);

        int databaseSizeBeforeDelete = deviceCategoryRepository.findAll().size();

        // Delete the deviceCategory
        restDeviceCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, deviceCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DeviceCategory> deviceCategoryList = deviceCategoryRepository.findAll();
        assertThat(deviceCategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
