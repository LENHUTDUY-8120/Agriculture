package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Crops;
import com.lnduy.agriculture.domain.Warehouse;
import com.lnduy.agriculture.repository.CropsRepository;
import com.lnduy.agriculture.service.criteria.CropsCriteria;
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
    private static final Float SMALLER_VOLUME = 1F - 1F;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_ENABLE = 1;
    private static final Integer UPDATED_ENABLE = 2;
    private static final Integer SMALLER_ENABLE = 1 - 1;

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
        Crops crops = new Crops()
            .name(DEFAULT_NAME)
            .volume(DEFAULT_VOLUME)
            .unit(DEFAULT_UNIT)
            .description(DEFAULT_DESCRIPTION)
            .enable(DEFAULT_ENABLE);
        return crops;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Crops createUpdatedEntity(EntityManager em) {
        Crops crops = new Crops()
            .name(UPDATED_NAME)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .description(UPDATED_DESCRIPTION)
            .enable(UPDATED_ENABLE);
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
        assertThat(testCrops.getEnable()).isEqualTo(DEFAULT_ENABLE);
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
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)));
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
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.enable").value(DEFAULT_ENABLE));
    }

    @Test
    @Transactional
    void getCropsByIdFiltering() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        Long id = crops.getId();

        defaultCropsShouldBeFound("id.equals=" + id);
        defaultCropsShouldNotBeFound("id.notEquals=" + id);

        defaultCropsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCropsShouldNotBeFound("id.greaterThan=" + id);

        defaultCropsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCropsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCropsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where name equals to DEFAULT_NAME
        defaultCropsShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the cropsList where name equals to UPDATED_NAME
        defaultCropsShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCropsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCropsShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the cropsList where name equals to UPDATED_NAME
        defaultCropsShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCropsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where name is not null
        defaultCropsShouldBeFound("name.specified=true");

        // Get all the cropsList where name is null
        defaultCropsShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllCropsByNameContainsSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where name contains DEFAULT_NAME
        defaultCropsShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the cropsList where name contains UPDATED_NAME
        defaultCropsShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCropsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where name does not contain DEFAULT_NAME
        defaultCropsShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the cropsList where name does not contain UPDATED_NAME
        defaultCropsShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCropsByVolumeIsEqualToSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where volume equals to DEFAULT_VOLUME
        defaultCropsShouldBeFound("volume.equals=" + DEFAULT_VOLUME);

        // Get all the cropsList where volume equals to UPDATED_VOLUME
        defaultCropsShouldNotBeFound("volume.equals=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllCropsByVolumeIsInShouldWork() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where volume in DEFAULT_VOLUME or UPDATED_VOLUME
        defaultCropsShouldBeFound("volume.in=" + DEFAULT_VOLUME + "," + UPDATED_VOLUME);

        // Get all the cropsList where volume equals to UPDATED_VOLUME
        defaultCropsShouldNotBeFound("volume.in=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllCropsByVolumeIsNullOrNotNull() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where volume is not null
        defaultCropsShouldBeFound("volume.specified=true");

        // Get all the cropsList where volume is null
        defaultCropsShouldNotBeFound("volume.specified=false");
    }

    @Test
    @Transactional
    void getAllCropsByVolumeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where volume is greater than or equal to DEFAULT_VOLUME
        defaultCropsShouldBeFound("volume.greaterThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the cropsList where volume is greater than or equal to UPDATED_VOLUME
        defaultCropsShouldNotBeFound("volume.greaterThanOrEqual=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllCropsByVolumeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where volume is less than or equal to DEFAULT_VOLUME
        defaultCropsShouldBeFound("volume.lessThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the cropsList where volume is less than or equal to SMALLER_VOLUME
        defaultCropsShouldNotBeFound("volume.lessThanOrEqual=" + SMALLER_VOLUME);
    }

    @Test
    @Transactional
    void getAllCropsByVolumeIsLessThanSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where volume is less than DEFAULT_VOLUME
        defaultCropsShouldNotBeFound("volume.lessThan=" + DEFAULT_VOLUME);

        // Get all the cropsList where volume is less than UPDATED_VOLUME
        defaultCropsShouldBeFound("volume.lessThan=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllCropsByVolumeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where volume is greater than DEFAULT_VOLUME
        defaultCropsShouldNotBeFound("volume.greaterThan=" + DEFAULT_VOLUME);

        // Get all the cropsList where volume is greater than SMALLER_VOLUME
        defaultCropsShouldBeFound("volume.greaterThan=" + SMALLER_VOLUME);
    }

    @Test
    @Transactional
    void getAllCropsByUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where unit equals to DEFAULT_UNIT
        defaultCropsShouldBeFound("unit.equals=" + DEFAULT_UNIT);

        // Get all the cropsList where unit equals to UPDATED_UNIT
        defaultCropsShouldNotBeFound("unit.equals=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllCropsByUnitIsInShouldWork() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where unit in DEFAULT_UNIT or UPDATED_UNIT
        defaultCropsShouldBeFound("unit.in=" + DEFAULT_UNIT + "," + UPDATED_UNIT);

        // Get all the cropsList where unit equals to UPDATED_UNIT
        defaultCropsShouldNotBeFound("unit.in=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllCropsByUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where unit is not null
        defaultCropsShouldBeFound("unit.specified=true");

        // Get all the cropsList where unit is null
        defaultCropsShouldNotBeFound("unit.specified=false");
    }

    @Test
    @Transactional
    void getAllCropsByUnitContainsSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where unit contains DEFAULT_UNIT
        defaultCropsShouldBeFound("unit.contains=" + DEFAULT_UNIT);

        // Get all the cropsList where unit contains UPDATED_UNIT
        defaultCropsShouldNotBeFound("unit.contains=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllCropsByUnitNotContainsSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where unit does not contain DEFAULT_UNIT
        defaultCropsShouldNotBeFound("unit.doesNotContain=" + DEFAULT_UNIT);

        // Get all the cropsList where unit does not contain UPDATED_UNIT
        defaultCropsShouldBeFound("unit.doesNotContain=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllCropsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where description equals to DEFAULT_DESCRIPTION
        defaultCropsShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the cropsList where description equals to UPDATED_DESCRIPTION
        defaultCropsShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCropsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultCropsShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the cropsList where description equals to UPDATED_DESCRIPTION
        defaultCropsShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCropsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where description is not null
        defaultCropsShouldBeFound("description.specified=true");

        // Get all the cropsList where description is null
        defaultCropsShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllCropsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where description contains DEFAULT_DESCRIPTION
        defaultCropsShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the cropsList where description contains UPDATED_DESCRIPTION
        defaultCropsShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCropsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where description does not contain DEFAULT_DESCRIPTION
        defaultCropsShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the cropsList where description does not contain UPDATED_DESCRIPTION
        defaultCropsShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCropsByEnableIsEqualToSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where enable equals to DEFAULT_ENABLE
        defaultCropsShouldBeFound("enable.equals=" + DEFAULT_ENABLE);

        // Get all the cropsList where enable equals to UPDATED_ENABLE
        defaultCropsShouldNotBeFound("enable.equals=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllCropsByEnableIsInShouldWork() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where enable in DEFAULT_ENABLE or UPDATED_ENABLE
        defaultCropsShouldBeFound("enable.in=" + DEFAULT_ENABLE + "," + UPDATED_ENABLE);

        // Get all the cropsList where enable equals to UPDATED_ENABLE
        defaultCropsShouldNotBeFound("enable.in=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllCropsByEnableIsNullOrNotNull() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where enable is not null
        defaultCropsShouldBeFound("enable.specified=true");

        // Get all the cropsList where enable is null
        defaultCropsShouldNotBeFound("enable.specified=false");
    }

    @Test
    @Transactional
    void getAllCropsByEnableIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where enable is greater than or equal to DEFAULT_ENABLE
        defaultCropsShouldBeFound("enable.greaterThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the cropsList where enable is greater than or equal to UPDATED_ENABLE
        defaultCropsShouldNotBeFound("enable.greaterThanOrEqual=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllCropsByEnableIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where enable is less than or equal to DEFAULT_ENABLE
        defaultCropsShouldBeFound("enable.lessThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the cropsList where enable is less than or equal to SMALLER_ENABLE
        defaultCropsShouldNotBeFound("enable.lessThanOrEqual=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllCropsByEnableIsLessThanSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where enable is less than DEFAULT_ENABLE
        defaultCropsShouldNotBeFound("enable.lessThan=" + DEFAULT_ENABLE);

        // Get all the cropsList where enable is less than UPDATED_ENABLE
        defaultCropsShouldBeFound("enable.lessThan=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllCropsByEnableIsGreaterThanSomething() throws Exception {
        // Initialize the database
        cropsRepository.saveAndFlush(crops);

        // Get all the cropsList where enable is greater than DEFAULT_ENABLE
        defaultCropsShouldNotBeFound("enable.greaterThan=" + DEFAULT_ENABLE);

        // Get all the cropsList where enable is greater than SMALLER_ENABLE
        defaultCropsShouldBeFound("enable.greaterThan=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllCropsByWarehouseIsEqualToSomething() throws Exception {
        Warehouse warehouse;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            cropsRepository.saveAndFlush(crops);
            warehouse = WarehouseResourceIT.createEntity(em);
        } else {
            warehouse = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        em.persist(warehouse);
        em.flush();
        crops.setWarehouse(warehouse);
        cropsRepository.saveAndFlush(crops);
        Long warehouseId = warehouse.getId();

        // Get all the cropsList where warehouse equals to warehouseId
        defaultCropsShouldBeFound("warehouseId.equals=" + warehouseId);

        // Get all the cropsList where warehouse equals to (warehouseId + 1)
        defaultCropsShouldNotBeFound("warehouseId.equals=" + (warehouseId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCropsShouldBeFound(String filter) throws Exception {
        restCropsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(crops.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.doubleValue())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)));

        // Check, that the count call also returns 1
        restCropsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCropsShouldNotBeFound(String filter) throws Exception {
        restCropsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCropsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
        updatedCrops.name(UPDATED_NAME).volume(UPDATED_VOLUME).unit(UPDATED_UNIT).description(UPDATED_DESCRIPTION).enable(UPDATED_ENABLE);
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
        assertThat(testCrops.getEnable()).isEqualTo(UPDATED_ENABLE);
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
        assertThat(testCrops.getEnable()).isEqualTo(DEFAULT_ENABLE);
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

        partialUpdatedCrops
            .name(UPDATED_NAME)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .description(UPDATED_DESCRIPTION)
            .enable(UPDATED_ENABLE);

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
        assertThat(testCrops.getEnable()).isEqualTo(UPDATED_ENABLE);
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
