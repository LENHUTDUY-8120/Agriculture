package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Supplies;
import com.lnduy.agriculture.domain.Task;
import com.lnduy.agriculture.domain.Warehouse;
import com.lnduy.agriculture.repository.SuppliesRepository;
import com.lnduy.agriculture.service.criteria.SuppliesCriteria;
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

    private static final Float DEFAULT_VOLUME = 1F;
    private static final Float UPDATED_VOLUME = 2F;
    private static final Float SMALLER_VOLUME = 1F - 1F;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final Integer DEFAULT_ENABLE = 1;
    private static final Integer UPDATED_ENABLE = 2;
    private static final Integer SMALLER_ENABLE = 1 - 1;

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
        Supplies supplies = new Supplies()
            .name(DEFAULT_NAME)
            .property(DEFAULT_PROPERTY)
            .type(DEFAULT_TYPE)
            .volume(DEFAULT_VOLUME)
            .unit(DEFAULT_UNIT)
            .enable(DEFAULT_ENABLE);
        return supplies;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Supplies createUpdatedEntity(EntityManager em) {
        Supplies supplies = new Supplies()
            .name(UPDATED_NAME)
            .property(UPDATED_PROPERTY)
            .type(UPDATED_TYPE)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .enable(UPDATED_ENABLE);
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
        assertThat(testSupplies.getVolume()).isEqualTo(DEFAULT_VOLUME);
        assertThat(testSupplies.getUnit()).isEqualTo(DEFAULT_UNIT);
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
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.doubleValue())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
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
            .andExpect(jsonPath("$.volume").value(DEFAULT_VOLUME.doubleValue()))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.enable").value(DEFAULT_ENABLE));
    }

    @Test
    @Transactional
    void getSuppliesByIdFiltering() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        Long id = supplies.getId();

        defaultSuppliesShouldBeFound("id.equals=" + id);
        defaultSuppliesShouldNotBeFound("id.notEquals=" + id);

        defaultSuppliesShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSuppliesShouldNotBeFound("id.greaterThan=" + id);

        defaultSuppliesShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSuppliesShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSuppliesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where name equals to DEFAULT_NAME
        defaultSuppliesShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the suppliesList where name equals to UPDATED_NAME
        defaultSuppliesShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSuppliesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSuppliesShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the suppliesList where name equals to UPDATED_NAME
        defaultSuppliesShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSuppliesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where name is not null
        defaultSuppliesShouldBeFound("name.specified=true");

        // Get all the suppliesList where name is null
        defaultSuppliesShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliesByNameContainsSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where name contains DEFAULT_NAME
        defaultSuppliesShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the suppliesList where name contains UPDATED_NAME
        defaultSuppliesShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSuppliesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where name does not contain DEFAULT_NAME
        defaultSuppliesShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the suppliesList where name does not contain UPDATED_NAME
        defaultSuppliesShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSuppliesByPropertyIsEqualToSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where property equals to DEFAULT_PROPERTY
        defaultSuppliesShouldBeFound("property.equals=" + DEFAULT_PROPERTY);

        // Get all the suppliesList where property equals to UPDATED_PROPERTY
        defaultSuppliesShouldNotBeFound("property.equals=" + UPDATED_PROPERTY);
    }

    @Test
    @Transactional
    void getAllSuppliesByPropertyIsInShouldWork() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where property in DEFAULT_PROPERTY or UPDATED_PROPERTY
        defaultSuppliesShouldBeFound("property.in=" + DEFAULT_PROPERTY + "," + UPDATED_PROPERTY);

        // Get all the suppliesList where property equals to UPDATED_PROPERTY
        defaultSuppliesShouldNotBeFound("property.in=" + UPDATED_PROPERTY);
    }

    @Test
    @Transactional
    void getAllSuppliesByPropertyIsNullOrNotNull() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where property is not null
        defaultSuppliesShouldBeFound("property.specified=true");

        // Get all the suppliesList where property is null
        defaultSuppliesShouldNotBeFound("property.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliesByPropertyContainsSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where property contains DEFAULT_PROPERTY
        defaultSuppliesShouldBeFound("property.contains=" + DEFAULT_PROPERTY);

        // Get all the suppliesList where property contains UPDATED_PROPERTY
        defaultSuppliesShouldNotBeFound("property.contains=" + UPDATED_PROPERTY);
    }

    @Test
    @Transactional
    void getAllSuppliesByPropertyNotContainsSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where property does not contain DEFAULT_PROPERTY
        defaultSuppliesShouldNotBeFound("property.doesNotContain=" + DEFAULT_PROPERTY);

        // Get all the suppliesList where property does not contain UPDATED_PROPERTY
        defaultSuppliesShouldBeFound("property.doesNotContain=" + UPDATED_PROPERTY);
    }

    @Test
    @Transactional
    void getAllSuppliesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where type equals to DEFAULT_TYPE
        defaultSuppliesShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the suppliesList where type equals to UPDATED_TYPE
        defaultSuppliesShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllSuppliesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultSuppliesShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the suppliesList where type equals to UPDATED_TYPE
        defaultSuppliesShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllSuppliesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where type is not null
        defaultSuppliesShouldBeFound("type.specified=true");

        // Get all the suppliesList where type is null
        defaultSuppliesShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliesByTypeContainsSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where type contains DEFAULT_TYPE
        defaultSuppliesShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the suppliesList where type contains UPDATED_TYPE
        defaultSuppliesShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllSuppliesByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where type does not contain DEFAULT_TYPE
        defaultSuppliesShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the suppliesList where type does not contain UPDATED_TYPE
        defaultSuppliesShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllSuppliesByVolumeIsEqualToSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where volume equals to DEFAULT_VOLUME
        defaultSuppliesShouldBeFound("volume.equals=" + DEFAULT_VOLUME);

        // Get all the suppliesList where volume equals to UPDATED_VOLUME
        defaultSuppliesShouldNotBeFound("volume.equals=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllSuppliesByVolumeIsInShouldWork() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where volume in DEFAULT_VOLUME or UPDATED_VOLUME
        defaultSuppliesShouldBeFound("volume.in=" + DEFAULT_VOLUME + "," + UPDATED_VOLUME);

        // Get all the suppliesList where volume equals to UPDATED_VOLUME
        defaultSuppliesShouldNotBeFound("volume.in=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllSuppliesByVolumeIsNullOrNotNull() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where volume is not null
        defaultSuppliesShouldBeFound("volume.specified=true");

        // Get all the suppliesList where volume is null
        defaultSuppliesShouldNotBeFound("volume.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliesByVolumeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where volume is greater than or equal to DEFAULT_VOLUME
        defaultSuppliesShouldBeFound("volume.greaterThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the suppliesList where volume is greater than or equal to UPDATED_VOLUME
        defaultSuppliesShouldNotBeFound("volume.greaterThanOrEqual=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllSuppliesByVolumeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where volume is less than or equal to DEFAULT_VOLUME
        defaultSuppliesShouldBeFound("volume.lessThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the suppliesList where volume is less than or equal to SMALLER_VOLUME
        defaultSuppliesShouldNotBeFound("volume.lessThanOrEqual=" + SMALLER_VOLUME);
    }

    @Test
    @Transactional
    void getAllSuppliesByVolumeIsLessThanSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where volume is less than DEFAULT_VOLUME
        defaultSuppliesShouldNotBeFound("volume.lessThan=" + DEFAULT_VOLUME);

        // Get all the suppliesList where volume is less than UPDATED_VOLUME
        defaultSuppliesShouldBeFound("volume.lessThan=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllSuppliesByVolumeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where volume is greater than DEFAULT_VOLUME
        defaultSuppliesShouldNotBeFound("volume.greaterThan=" + DEFAULT_VOLUME);

        // Get all the suppliesList where volume is greater than SMALLER_VOLUME
        defaultSuppliesShouldBeFound("volume.greaterThan=" + SMALLER_VOLUME);
    }

    @Test
    @Transactional
    void getAllSuppliesByUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where unit equals to DEFAULT_UNIT
        defaultSuppliesShouldBeFound("unit.equals=" + DEFAULT_UNIT);

        // Get all the suppliesList where unit equals to UPDATED_UNIT
        defaultSuppliesShouldNotBeFound("unit.equals=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllSuppliesByUnitIsInShouldWork() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where unit in DEFAULT_UNIT or UPDATED_UNIT
        defaultSuppliesShouldBeFound("unit.in=" + DEFAULT_UNIT + "," + UPDATED_UNIT);

        // Get all the suppliesList where unit equals to UPDATED_UNIT
        defaultSuppliesShouldNotBeFound("unit.in=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllSuppliesByUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where unit is not null
        defaultSuppliesShouldBeFound("unit.specified=true");

        // Get all the suppliesList where unit is null
        defaultSuppliesShouldNotBeFound("unit.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliesByUnitContainsSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where unit contains DEFAULT_UNIT
        defaultSuppliesShouldBeFound("unit.contains=" + DEFAULT_UNIT);

        // Get all the suppliesList where unit contains UPDATED_UNIT
        defaultSuppliesShouldNotBeFound("unit.contains=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllSuppliesByUnitNotContainsSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where unit does not contain DEFAULT_UNIT
        defaultSuppliesShouldNotBeFound("unit.doesNotContain=" + DEFAULT_UNIT);

        // Get all the suppliesList where unit does not contain UPDATED_UNIT
        defaultSuppliesShouldBeFound("unit.doesNotContain=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllSuppliesByEnableIsEqualToSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where enable equals to DEFAULT_ENABLE
        defaultSuppliesShouldBeFound("enable.equals=" + DEFAULT_ENABLE);

        // Get all the suppliesList where enable equals to UPDATED_ENABLE
        defaultSuppliesShouldNotBeFound("enable.equals=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllSuppliesByEnableIsInShouldWork() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where enable in DEFAULT_ENABLE or UPDATED_ENABLE
        defaultSuppliesShouldBeFound("enable.in=" + DEFAULT_ENABLE + "," + UPDATED_ENABLE);

        // Get all the suppliesList where enable equals to UPDATED_ENABLE
        defaultSuppliesShouldNotBeFound("enable.in=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllSuppliesByEnableIsNullOrNotNull() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where enable is not null
        defaultSuppliesShouldBeFound("enable.specified=true");

        // Get all the suppliesList where enable is null
        defaultSuppliesShouldNotBeFound("enable.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliesByEnableIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where enable is greater than or equal to DEFAULT_ENABLE
        defaultSuppliesShouldBeFound("enable.greaterThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the suppliesList where enable is greater than or equal to UPDATED_ENABLE
        defaultSuppliesShouldNotBeFound("enable.greaterThanOrEqual=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllSuppliesByEnableIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where enable is less than or equal to DEFAULT_ENABLE
        defaultSuppliesShouldBeFound("enable.lessThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the suppliesList where enable is less than or equal to SMALLER_ENABLE
        defaultSuppliesShouldNotBeFound("enable.lessThanOrEqual=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllSuppliesByEnableIsLessThanSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where enable is less than DEFAULT_ENABLE
        defaultSuppliesShouldNotBeFound("enable.lessThan=" + DEFAULT_ENABLE);

        // Get all the suppliesList where enable is less than UPDATED_ENABLE
        defaultSuppliesShouldBeFound("enable.lessThan=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllSuppliesByEnableIsGreaterThanSomething() throws Exception {
        // Initialize the database
        suppliesRepository.saveAndFlush(supplies);

        // Get all the suppliesList where enable is greater than DEFAULT_ENABLE
        defaultSuppliesShouldNotBeFound("enable.greaterThan=" + DEFAULT_ENABLE);

        // Get all the suppliesList where enable is greater than SMALLER_ENABLE
        defaultSuppliesShouldBeFound("enable.greaterThan=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllSuppliesByWarehouseIsEqualToSomething() throws Exception {
        Warehouse warehouse;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            suppliesRepository.saveAndFlush(supplies);
            warehouse = WarehouseResourceIT.createEntity(em);
        } else {
            warehouse = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        em.persist(warehouse);
        em.flush();
        supplies.setWarehouse(warehouse);
        suppliesRepository.saveAndFlush(supplies);
        Long warehouseId = warehouse.getId();

        // Get all the suppliesList where warehouse equals to warehouseId
        defaultSuppliesShouldBeFound("warehouseId.equals=" + warehouseId);

        // Get all the suppliesList where warehouse equals to (warehouseId + 1)
        defaultSuppliesShouldNotBeFound("warehouseId.equals=" + (warehouseId + 1));
    }

    @Test
    @Transactional
    void getAllSuppliesByTaskIsEqualToSomething() throws Exception {
        Task task;
        if (TestUtil.findAll(em, Task.class).isEmpty()) {
            suppliesRepository.saveAndFlush(supplies);
            task = TaskResourceIT.createEntity(em);
        } else {
            task = TestUtil.findAll(em, Task.class).get(0);
        }
        em.persist(task);
        em.flush();
        supplies.addTask(task);
        suppliesRepository.saveAndFlush(supplies);
        Long taskId = task.getId();

        // Get all the suppliesList where task equals to taskId
        defaultSuppliesShouldBeFound("taskId.equals=" + taskId);

        // Get all the suppliesList where task equals to (taskId + 1)
        defaultSuppliesShouldNotBeFound("taskId.equals=" + (taskId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSuppliesShouldBeFound(String filter) throws Exception {
        restSuppliesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplies.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].property").value(hasItem(DEFAULT_PROPERTY)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.doubleValue())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)));

        // Check, that the count call also returns 1
        restSuppliesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSuppliesShouldNotBeFound(String filter) throws Exception {
        restSuppliesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSuppliesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
        updatedSupplies
            .name(UPDATED_NAME)
            .property(UPDATED_PROPERTY)
            .type(UPDATED_TYPE)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .enable(UPDATED_ENABLE);
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
        assertThat(testSupplies.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testSupplies.getUnit()).isEqualTo(UPDATED_UNIT);
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

        partialUpdatedSupplies.property(UPDATED_PROPERTY).unit(UPDATED_UNIT).enable(UPDATED_ENABLE);

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
        assertThat(testSupplies.getVolume()).isEqualTo(DEFAULT_VOLUME);
        assertThat(testSupplies.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testSupplies.getEnable()).isEqualTo(UPDATED_ENABLE);
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

        partialUpdatedSupplies
            .name(UPDATED_NAME)
            .property(UPDATED_PROPERTY)
            .type(UPDATED_TYPE)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .enable(UPDATED_ENABLE);

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
        assertThat(testSupplies.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testSupplies.getUnit()).isEqualTo(UPDATED_UNIT);
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
