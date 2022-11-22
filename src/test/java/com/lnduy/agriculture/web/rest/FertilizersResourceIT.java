package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Fertilizers;
import com.lnduy.agriculture.domain.Task;
import com.lnduy.agriculture.domain.Warehouse;
import com.lnduy.agriculture.repository.FertilizersRepository;
import com.lnduy.agriculture.service.criteria.FertilizersCriteria;
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
    private static final Float SMALLER_VOLUME = 1F - 1F;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final Integer DEFAULT_ENABLE = 1;
    private static final Integer UPDATED_ENABLE = 2;
    private static final Integer SMALLER_ENABLE = 1 - 1;

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
    void getFertilizersByIdFiltering() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        Long id = fertilizers.getId();

        defaultFertilizersShouldBeFound("id.equals=" + id);
        defaultFertilizersShouldNotBeFound("id.notEquals=" + id);

        defaultFertilizersShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFertilizersShouldNotBeFound("id.greaterThan=" + id);

        defaultFertilizersShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFertilizersShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFertilizersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where name equals to DEFAULT_NAME
        defaultFertilizersShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the fertilizersList where name equals to UPDATED_NAME
        defaultFertilizersShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFertilizersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where name in DEFAULT_NAME or UPDATED_NAME
        defaultFertilizersShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the fertilizersList where name equals to UPDATED_NAME
        defaultFertilizersShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFertilizersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where name is not null
        defaultFertilizersShouldBeFound("name.specified=true");

        // Get all the fertilizersList where name is null
        defaultFertilizersShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllFertilizersByNameContainsSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where name contains DEFAULT_NAME
        defaultFertilizersShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the fertilizersList where name contains UPDATED_NAME
        defaultFertilizersShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFertilizersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where name does not contain DEFAULT_NAME
        defaultFertilizersShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the fertilizersList where name does not contain UPDATED_NAME
        defaultFertilizersShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFertilizersByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where description equals to DEFAULT_DESCRIPTION
        defaultFertilizersShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the fertilizersList where description equals to UPDATED_DESCRIPTION
        defaultFertilizersShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFertilizersByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultFertilizersShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the fertilizersList where description equals to UPDATED_DESCRIPTION
        defaultFertilizersShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFertilizersByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where description is not null
        defaultFertilizersShouldBeFound("description.specified=true");

        // Get all the fertilizersList where description is null
        defaultFertilizersShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllFertilizersByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where description contains DEFAULT_DESCRIPTION
        defaultFertilizersShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the fertilizersList where description contains UPDATED_DESCRIPTION
        defaultFertilizersShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFertilizersByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where description does not contain DEFAULT_DESCRIPTION
        defaultFertilizersShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the fertilizersList where description does not contain UPDATED_DESCRIPTION
        defaultFertilizersShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFertilizersByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where type equals to DEFAULT_TYPE
        defaultFertilizersShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the fertilizersList where type equals to UPDATED_TYPE
        defaultFertilizersShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllFertilizersByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultFertilizersShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the fertilizersList where type equals to UPDATED_TYPE
        defaultFertilizersShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllFertilizersByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where type is not null
        defaultFertilizersShouldBeFound("type.specified=true");

        // Get all the fertilizersList where type is null
        defaultFertilizersShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllFertilizersByTypeContainsSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where type contains DEFAULT_TYPE
        defaultFertilizersShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the fertilizersList where type contains UPDATED_TYPE
        defaultFertilizersShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllFertilizersByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where type does not contain DEFAULT_TYPE
        defaultFertilizersShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the fertilizersList where type does not contain UPDATED_TYPE
        defaultFertilizersShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllFertilizersByVolumeIsEqualToSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where volume equals to DEFAULT_VOLUME
        defaultFertilizersShouldBeFound("volume.equals=" + DEFAULT_VOLUME);

        // Get all the fertilizersList where volume equals to UPDATED_VOLUME
        defaultFertilizersShouldNotBeFound("volume.equals=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllFertilizersByVolumeIsInShouldWork() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where volume in DEFAULT_VOLUME or UPDATED_VOLUME
        defaultFertilizersShouldBeFound("volume.in=" + DEFAULT_VOLUME + "," + UPDATED_VOLUME);

        // Get all the fertilizersList where volume equals to UPDATED_VOLUME
        defaultFertilizersShouldNotBeFound("volume.in=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllFertilizersByVolumeIsNullOrNotNull() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where volume is not null
        defaultFertilizersShouldBeFound("volume.specified=true");

        // Get all the fertilizersList where volume is null
        defaultFertilizersShouldNotBeFound("volume.specified=false");
    }

    @Test
    @Transactional
    void getAllFertilizersByVolumeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where volume is greater than or equal to DEFAULT_VOLUME
        defaultFertilizersShouldBeFound("volume.greaterThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the fertilizersList where volume is greater than or equal to UPDATED_VOLUME
        defaultFertilizersShouldNotBeFound("volume.greaterThanOrEqual=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllFertilizersByVolumeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where volume is less than or equal to DEFAULT_VOLUME
        defaultFertilizersShouldBeFound("volume.lessThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the fertilizersList where volume is less than or equal to SMALLER_VOLUME
        defaultFertilizersShouldNotBeFound("volume.lessThanOrEqual=" + SMALLER_VOLUME);
    }

    @Test
    @Transactional
    void getAllFertilizersByVolumeIsLessThanSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where volume is less than DEFAULT_VOLUME
        defaultFertilizersShouldNotBeFound("volume.lessThan=" + DEFAULT_VOLUME);

        // Get all the fertilizersList where volume is less than UPDATED_VOLUME
        defaultFertilizersShouldBeFound("volume.lessThan=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllFertilizersByVolumeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where volume is greater than DEFAULT_VOLUME
        defaultFertilizersShouldNotBeFound("volume.greaterThan=" + DEFAULT_VOLUME);

        // Get all the fertilizersList where volume is greater than SMALLER_VOLUME
        defaultFertilizersShouldBeFound("volume.greaterThan=" + SMALLER_VOLUME);
    }

    @Test
    @Transactional
    void getAllFertilizersByUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where unit equals to DEFAULT_UNIT
        defaultFertilizersShouldBeFound("unit.equals=" + DEFAULT_UNIT);

        // Get all the fertilizersList where unit equals to UPDATED_UNIT
        defaultFertilizersShouldNotBeFound("unit.equals=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllFertilizersByUnitIsInShouldWork() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where unit in DEFAULT_UNIT or UPDATED_UNIT
        defaultFertilizersShouldBeFound("unit.in=" + DEFAULT_UNIT + "," + UPDATED_UNIT);

        // Get all the fertilizersList where unit equals to UPDATED_UNIT
        defaultFertilizersShouldNotBeFound("unit.in=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllFertilizersByUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where unit is not null
        defaultFertilizersShouldBeFound("unit.specified=true");

        // Get all the fertilizersList where unit is null
        defaultFertilizersShouldNotBeFound("unit.specified=false");
    }

    @Test
    @Transactional
    void getAllFertilizersByUnitContainsSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where unit contains DEFAULT_UNIT
        defaultFertilizersShouldBeFound("unit.contains=" + DEFAULT_UNIT);

        // Get all the fertilizersList where unit contains UPDATED_UNIT
        defaultFertilizersShouldNotBeFound("unit.contains=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllFertilizersByUnitNotContainsSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where unit does not contain DEFAULT_UNIT
        defaultFertilizersShouldNotBeFound("unit.doesNotContain=" + DEFAULT_UNIT);

        // Get all the fertilizersList where unit does not contain UPDATED_UNIT
        defaultFertilizersShouldBeFound("unit.doesNotContain=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllFertilizersByEnableIsEqualToSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where enable equals to DEFAULT_ENABLE
        defaultFertilizersShouldBeFound("enable.equals=" + DEFAULT_ENABLE);

        // Get all the fertilizersList where enable equals to UPDATED_ENABLE
        defaultFertilizersShouldNotBeFound("enable.equals=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllFertilizersByEnableIsInShouldWork() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where enable in DEFAULT_ENABLE or UPDATED_ENABLE
        defaultFertilizersShouldBeFound("enable.in=" + DEFAULT_ENABLE + "," + UPDATED_ENABLE);

        // Get all the fertilizersList where enable equals to UPDATED_ENABLE
        defaultFertilizersShouldNotBeFound("enable.in=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllFertilizersByEnableIsNullOrNotNull() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where enable is not null
        defaultFertilizersShouldBeFound("enable.specified=true");

        // Get all the fertilizersList where enable is null
        defaultFertilizersShouldNotBeFound("enable.specified=false");
    }

    @Test
    @Transactional
    void getAllFertilizersByEnableIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where enable is greater than or equal to DEFAULT_ENABLE
        defaultFertilizersShouldBeFound("enable.greaterThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the fertilizersList where enable is greater than or equal to UPDATED_ENABLE
        defaultFertilizersShouldNotBeFound("enable.greaterThanOrEqual=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllFertilizersByEnableIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where enable is less than or equal to DEFAULT_ENABLE
        defaultFertilizersShouldBeFound("enable.lessThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the fertilizersList where enable is less than or equal to SMALLER_ENABLE
        defaultFertilizersShouldNotBeFound("enable.lessThanOrEqual=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllFertilizersByEnableIsLessThanSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where enable is less than DEFAULT_ENABLE
        defaultFertilizersShouldNotBeFound("enable.lessThan=" + DEFAULT_ENABLE);

        // Get all the fertilizersList where enable is less than UPDATED_ENABLE
        defaultFertilizersShouldBeFound("enable.lessThan=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllFertilizersByEnableIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fertilizersRepository.saveAndFlush(fertilizers);

        // Get all the fertilizersList where enable is greater than DEFAULT_ENABLE
        defaultFertilizersShouldNotBeFound("enable.greaterThan=" + DEFAULT_ENABLE);

        // Get all the fertilizersList where enable is greater than SMALLER_ENABLE
        defaultFertilizersShouldBeFound("enable.greaterThan=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllFertilizersByWarehouseIsEqualToSomething() throws Exception {
        Warehouse warehouse;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            fertilizersRepository.saveAndFlush(fertilizers);
            warehouse = WarehouseResourceIT.createEntity(em);
        } else {
            warehouse = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        em.persist(warehouse);
        em.flush();
        fertilizers.setWarehouse(warehouse);
        fertilizersRepository.saveAndFlush(fertilizers);
        Long warehouseId = warehouse.getId();

        // Get all the fertilizersList where warehouse equals to warehouseId
        defaultFertilizersShouldBeFound("warehouseId.equals=" + warehouseId);

        // Get all the fertilizersList where warehouse equals to (warehouseId + 1)
        defaultFertilizersShouldNotBeFound("warehouseId.equals=" + (warehouseId + 1));
    }

    @Test
    @Transactional
    void getAllFertilizersByTaskIsEqualToSomething() throws Exception {
        Task task;
        if (TestUtil.findAll(em, Task.class).isEmpty()) {
            fertilizersRepository.saveAndFlush(fertilizers);
            task = TaskResourceIT.createEntity(em);
        } else {
            task = TestUtil.findAll(em, Task.class).get(0);
        }
        em.persist(task);
        em.flush();
        fertilizers.addTask(task);
        fertilizersRepository.saveAndFlush(fertilizers);
        Long taskId = task.getId();

        // Get all the fertilizersList where task equals to taskId
        defaultFertilizersShouldBeFound("taskId.equals=" + taskId);

        // Get all the fertilizersList where task equals to (taskId + 1)
        defaultFertilizersShouldNotBeFound("taskId.equals=" + (taskId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFertilizersShouldBeFound(String filter) throws Exception {
        restFertilizersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fertilizers.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.doubleValue())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)));

        // Check, that the count call also returns 1
        restFertilizersMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFertilizersShouldNotBeFound(String filter) throws Exception {
        restFertilizersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFertilizersMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
