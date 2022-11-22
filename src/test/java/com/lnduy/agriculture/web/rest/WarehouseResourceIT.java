package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Crops;
import com.lnduy.agriculture.domain.Fertilizers;
import com.lnduy.agriculture.domain.ProtectionProduct;
import com.lnduy.agriculture.domain.Supplies;
import com.lnduy.agriculture.domain.Warehouse;
import com.lnduy.agriculture.repository.WarehouseRepository;
import com.lnduy.agriculture.service.criteria.WarehouseCriteria;
import com.lnduy.agriculture.service.dto.WarehouseDTO;
import com.lnduy.agriculture.service.mapper.WarehouseMapper;
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
 * Integration tests for the {@link WarehouseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WarehouseResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_AREA = 1D;
    private static final Double UPDATED_AREA = 2D;
    private static final Double SMALLER_AREA = 1D - 1D;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;
    private static final Double SMALLER_LATITUDE = 1D - 1D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;
    private static final Double SMALLER_LONGITUDE = 1D - 1D;

    private static final Integer DEFAULT_ENABLE = 1;
    private static final Integer UPDATED_ENABLE = 2;
    private static final Integer SMALLER_ENABLE = 1 - 1;

    private static final String ENTITY_API_URL = "/api/warehouses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWarehouseMockMvc;

    private Warehouse warehouse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Warehouse createEntity(EntityManager em) {
        Warehouse warehouse = new Warehouse()
            .name(DEFAULT_NAME)
            .area(DEFAULT_AREA)
            .description(DEFAULT_DESCRIPTION)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .enable(DEFAULT_ENABLE);
        return warehouse;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Warehouse createUpdatedEntity(EntityManager em) {
        Warehouse warehouse = new Warehouse()
            .name(UPDATED_NAME)
            .area(UPDATED_AREA)
            .description(UPDATED_DESCRIPTION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .enable(UPDATED_ENABLE);
        return warehouse;
    }

    @BeforeEach
    public void initTest() {
        warehouse = createEntity(em);
    }

    @Test
    @Transactional
    void createWarehouse() throws Exception {
        int databaseSizeBeforeCreate = warehouseRepository.findAll().size();
        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);
        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isCreated());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeCreate + 1);
        Warehouse testWarehouse = warehouseList.get(warehouseList.size() - 1);
        assertThat(testWarehouse.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWarehouse.getArea()).isEqualTo(DEFAULT_AREA);
        assertThat(testWarehouse.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testWarehouse.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testWarehouse.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testWarehouse.getEnable()).isEqualTo(DEFAULT_ENABLE);
    }

    @Test
    @Transactional
    void createWarehouseWithExistingId() throws Exception {
        // Create the Warehouse with an existing ID
        warehouse.setId(1L);
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        int databaseSizeBeforeCreate = warehouseRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWarehouses() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(warehouse.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].area").value(hasItem(DEFAULT_AREA.doubleValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)));
    }

    @Test
    @Transactional
    void getWarehouse() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get the warehouse
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL_ID, warehouse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(warehouse.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.area").value(DEFAULT_AREA.doubleValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.enable").value(DEFAULT_ENABLE));
    }

    @Test
    @Transactional
    void getWarehousesByIdFiltering() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        Long id = warehouse.getId();

        defaultWarehouseShouldBeFound("id.equals=" + id);
        defaultWarehouseShouldNotBeFound("id.notEquals=" + id);

        defaultWarehouseShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWarehouseShouldNotBeFound("id.greaterThan=" + id);

        defaultWarehouseShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWarehouseShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWarehousesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name equals to DEFAULT_NAME
        defaultWarehouseShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the warehouseList where name equals to UPDATED_NAME
        defaultWarehouseShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name in DEFAULT_NAME or UPDATED_NAME
        defaultWarehouseShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the warehouseList where name equals to UPDATED_NAME
        defaultWarehouseShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name is not null
        defaultWarehouseShouldBeFound("name.specified=true");

        // Get all the warehouseList where name is null
        defaultWarehouseShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByNameContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name contains DEFAULT_NAME
        defaultWarehouseShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the warehouseList where name contains UPDATED_NAME
        defaultWarehouseShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name does not contain DEFAULT_NAME
        defaultWarehouseShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the warehouseList where name does not contain UPDATED_NAME
        defaultWarehouseShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByAreaIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where area equals to DEFAULT_AREA
        defaultWarehouseShouldBeFound("area.equals=" + DEFAULT_AREA);

        // Get all the warehouseList where area equals to UPDATED_AREA
        defaultWarehouseShouldNotBeFound("area.equals=" + UPDATED_AREA);
    }

    @Test
    @Transactional
    void getAllWarehousesByAreaIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where area in DEFAULT_AREA or UPDATED_AREA
        defaultWarehouseShouldBeFound("area.in=" + DEFAULT_AREA + "," + UPDATED_AREA);

        // Get all the warehouseList where area equals to UPDATED_AREA
        defaultWarehouseShouldNotBeFound("area.in=" + UPDATED_AREA);
    }

    @Test
    @Transactional
    void getAllWarehousesByAreaIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where area is not null
        defaultWarehouseShouldBeFound("area.specified=true");

        // Get all the warehouseList where area is null
        defaultWarehouseShouldNotBeFound("area.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByAreaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where area is greater than or equal to DEFAULT_AREA
        defaultWarehouseShouldBeFound("area.greaterThanOrEqual=" + DEFAULT_AREA);

        // Get all the warehouseList where area is greater than or equal to UPDATED_AREA
        defaultWarehouseShouldNotBeFound("area.greaterThanOrEqual=" + UPDATED_AREA);
    }

    @Test
    @Transactional
    void getAllWarehousesByAreaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where area is less than or equal to DEFAULT_AREA
        defaultWarehouseShouldBeFound("area.lessThanOrEqual=" + DEFAULT_AREA);

        // Get all the warehouseList where area is less than or equal to SMALLER_AREA
        defaultWarehouseShouldNotBeFound("area.lessThanOrEqual=" + SMALLER_AREA);
    }

    @Test
    @Transactional
    void getAllWarehousesByAreaIsLessThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where area is less than DEFAULT_AREA
        defaultWarehouseShouldNotBeFound("area.lessThan=" + DEFAULT_AREA);

        // Get all the warehouseList where area is less than UPDATED_AREA
        defaultWarehouseShouldBeFound("area.lessThan=" + UPDATED_AREA);
    }

    @Test
    @Transactional
    void getAllWarehousesByAreaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where area is greater than DEFAULT_AREA
        defaultWarehouseShouldNotBeFound("area.greaterThan=" + DEFAULT_AREA);

        // Get all the warehouseList where area is greater than SMALLER_AREA
        defaultWarehouseShouldBeFound("area.greaterThan=" + SMALLER_AREA);
    }

    @Test
    @Transactional
    void getAllWarehousesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where description equals to DEFAULT_DESCRIPTION
        defaultWarehouseShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the warehouseList where description equals to UPDATED_DESCRIPTION
        defaultWarehouseShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllWarehousesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultWarehouseShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the warehouseList where description equals to UPDATED_DESCRIPTION
        defaultWarehouseShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllWarehousesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where description is not null
        defaultWarehouseShouldBeFound("description.specified=true");

        // Get all the warehouseList where description is null
        defaultWarehouseShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where description contains DEFAULT_DESCRIPTION
        defaultWarehouseShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the warehouseList where description contains UPDATED_DESCRIPTION
        defaultWarehouseShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllWarehousesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where description does not contain DEFAULT_DESCRIPTION
        defaultWarehouseShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the warehouseList where description does not contain UPDATED_DESCRIPTION
        defaultWarehouseShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllWarehousesByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where latitude equals to DEFAULT_LATITUDE
        defaultWarehouseShouldBeFound("latitude.equals=" + DEFAULT_LATITUDE);

        // Get all the warehouseList where latitude equals to UPDATED_LATITUDE
        defaultWarehouseShouldNotBeFound("latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllWarehousesByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where latitude in DEFAULT_LATITUDE or UPDATED_LATITUDE
        defaultWarehouseShouldBeFound("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE);

        // Get all the warehouseList where latitude equals to UPDATED_LATITUDE
        defaultWarehouseShouldNotBeFound("latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllWarehousesByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where latitude is not null
        defaultWarehouseShouldBeFound("latitude.specified=true");

        // Get all the warehouseList where latitude is null
        defaultWarehouseShouldNotBeFound("latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where latitude is greater than or equal to DEFAULT_LATITUDE
        defaultWarehouseShouldBeFound("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the warehouseList where latitude is greater than or equal to UPDATED_LATITUDE
        defaultWarehouseShouldNotBeFound("latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllWarehousesByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where latitude is less than or equal to DEFAULT_LATITUDE
        defaultWarehouseShouldBeFound("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the warehouseList where latitude is less than or equal to SMALLER_LATITUDE
        defaultWarehouseShouldNotBeFound("latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllWarehousesByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where latitude is less than DEFAULT_LATITUDE
        defaultWarehouseShouldNotBeFound("latitude.lessThan=" + DEFAULT_LATITUDE);

        // Get all the warehouseList where latitude is less than UPDATED_LATITUDE
        defaultWarehouseShouldBeFound("latitude.lessThan=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllWarehousesByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where latitude is greater than DEFAULT_LATITUDE
        defaultWarehouseShouldNotBeFound("latitude.greaterThan=" + DEFAULT_LATITUDE);

        // Get all the warehouseList where latitude is greater than SMALLER_LATITUDE
        defaultWarehouseShouldBeFound("latitude.greaterThan=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllWarehousesByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where longitude equals to DEFAULT_LONGITUDE
        defaultWarehouseShouldBeFound("longitude.equals=" + DEFAULT_LONGITUDE);

        // Get all the warehouseList where longitude equals to UPDATED_LONGITUDE
        defaultWarehouseShouldNotBeFound("longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllWarehousesByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where longitude in DEFAULT_LONGITUDE or UPDATED_LONGITUDE
        defaultWarehouseShouldBeFound("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE);

        // Get all the warehouseList where longitude equals to UPDATED_LONGITUDE
        defaultWarehouseShouldNotBeFound("longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllWarehousesByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where longitude is not null
        defaultWarehouseShouldBeFound("longitude.specified=true");

        // Get all the warehouseList where longitude is null
        defaultWarehouseShouldNotBeFound("longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where longitude is greater than or equal to DEFAULT_LONGITUDE
        defaultWarehouseShouldBeFound("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the warehouseList where longitude is greater than or equal to UPDATED_LONGITUDE
        defaultWarehouseShouldNotBeFound("longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllWarehousesByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where longitude is less than or equal to DEFAULT_LONGITUDE
        defaultWarehouseShouldBeFound("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the warehouseList where longitude is less than or equal to SMALLER_LONGITUDE
        defaultWarehouseShouldNotBeFound("longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllWarehousesByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where longitude is less than DEFAULT_LONGITUDE
        defaultWarehouseShouldNotBeFound("longitude.lessThan=" + DEFAULT_LONGITUDE);

        // Get all the warehouseList where longitude is less than UPDATED_LONGITUDE
        defaultWarehouseShouldBeFound("longitude.lessThan=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllWarehousesByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where longitude is greater than DEFAULT_LONGITUDE
        defaultWarehouseShouldNotBeFound("longitude.greaterThan=" + DEFAULT_LONGITUDE);

        // Get all the warehouseList where longitude is greater than SMALLER_LONGITUDE
        defaultWarehouseShouldBeFound("longitude.greaterThan=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllWarehousesByEnableIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where enable equals to DEFAULT_ENABLE
        defaultWarehouseShouldBeFound("enable.equals=" + DEFAULT_ENABLE);

        // Get all the warehouseList where enable equals to UPDATED_ENABLE
        defaultWarehouseShouldNotBeFound("enable.equals=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllWarehousesByEnableIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where enable in DEFAULT_ENABLE or UPDATED_ENABLE
        defaultWarehouseShouldBeFound("enable.in=" + DEFAULT_ENABLE + "," + UPDATED_ENABLE);

        // Get all the warehouseList where enable equals to UPDATED_ENABLE
        defaultWarehouseShouldNotBeFound("enable.in=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllWarehousesByEnableIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where enable is not null
        defaultWarehouseShouldBeFound("enable.specified=true");

        // Get all the warehouseList where enable is null
        defaultWarehouseShouldNotBeFound("enable.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByEnableIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where enable is greater than or equal to DEFAULT_ENABLE
        defaultWarehouseShouldBeFound("enable.greaterThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the warehouseList where enable is greater than or equal to UPDATED_ENABLE
        defaultWarehouseShouldNotBeFound("enable.greaterThanOrEqual=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllWarehousesByEnableIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where enable is less than or equal to DEFAULT_ENABLE
        defaultWarehouseShouldBeFound("enable.lessThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the warehouseList where enable is less than or equal to SMALLER_ENABLE
        defaultWarehouseShouldNotBeFound("enable.lessThanOrEqual=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllWarehousesByEnableIsLessThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where enable is less than DEFAULT_ENABLE
        defaultWarehouseShouldNotBeFound("enable.lessThan=" + DEFAULT_ENABLE);

        // Get all the warehouseList where enable is less than UPDATED_ENABLE
        defaultWarehouseShouldBeFound("enable.lessThan=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllWarehousesByEnableIsGreaterThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where enable is greater than DEFAULT_ENABLE
        defaultWarehouseShouldNotBeFound("enable.greaterThan=" + DEFAULT_ENABLE);

        // Get all the warehouseList where enable is greater than SMALLER_ENABLE
        defaultWarehouseShouldBeFound("enable.greaterThan=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllWarehousesBySuppliesIsEqualToSomething() throws Exception {
        Supplies supplies;
        if (TestUtil.findAll(em, Supplies.class).isEmpty()) {
            warehouseRepository.saveAndFlush(warehouse);
            supplies = SuppliesResourceIT.createEntity(em);
        } else {
            supplies = TestUtil.findAll(em, Supplies.class).get(0);
        }
        em.persist(supplies);
        em.flush();
        warehouse.addSupplies(supplies);
        warehouseRepository.saveAndFlush(warehouse);
        Long suppliesId = supplies.getId();

        // Get all the warehouseList where supplies equals to suppliesId
        defaultWarehouseShouldBeFound("suppliesId.equals=" + suppliesId);

        // Get all the warehouseList where supplies equals to (suppliesId + 1)
        defaultWarehouseShouldNotBeFound("suppliesId.equals=" + (suppliesId + 1));
    }

    @Test
    @Transactional
    void getAllWarehousesByProtectionProductIsEqualToSomething() throws Exception {
        ProtectionProduct protectionProduct;
        if (TestUtil.findAll(em, ProtectionProduct.class).isEmpty()) {
            warehouseRepository.saveAndFlush(warehouse);
            protectionProduct = ProtectionProductResourceIT.createEntity(em);
        } else {
            protectionProduct = TestUtil.findAll(em, ProtectionProduct.class).get(0);
        }
        em.persist(protectionProduct);
        em.flush();
        warehouse.addProtectionProduct(protectionProduct);
        warehouseRepository.saveAndFlush(warehouse);
        Long protectionProductId = protectionProduct.getId();

        // Get all the warehouseList where protectionProduct equals to protectionProductId
        defaultWarehouseShouldBeFound("protectionProductId.equals=" + protectionProductId);

        // Get all the warehouseList where protectionProduct equals to (protectionProductId + 1)
        defaultWarehouseShouldNotBeFound("protectionProductId.equals=" + (protectionProductId + 1));
    }

    @Test
    @Transactional
    void getAllWarehousesByFertilizersIsEqualToSomething() throws Exception {
        Fertilizers fertilizers;
        if (TestUtil.findAll(em, Fertilizers.class).isEmpty()) {
            warehouseRepository.saveAndFlush(warehouse);
            fertilizers = FertilizersResourceIT.createEntity(em);
        } else {
            fertilizers = TestUtil.findAll(em, Fertilizers.class).get(0);
        }
        em.persist(fertilizers);
        em.flush();
        warehouse.addFertilizers(fertilizers);
        warehouseRepository.saveAndFlush(warehouse);
        Long fertilizersId = fertilizers.getId();

        // Get all the warehouseList where fertilizers equals to fertilizersId
        defaultWarehouseShouldBeFound("fertilizersId.equals=" + fertilizersId);

        // Get all the warehouseList where fertilizers equals to (fertilizersId + 1)
        defaultWarehouseShouldNotBeFound("fertilizersId.equals=" + (fertilizersId + 1));
    }

    @Test
    @Transactional
    void getAllWarehousesByCropsIsEqualToSomething() throws Exception {
        Crops crops;
        if (TestUtil.findAll(em, Crops.class).isEmpty()) {
            warehouseRepository.saveAndFlush(warehouse);
            crops = CropsResourceIT.createEntity(em);
        } else {
            crops = TestUtil.findAll(em, Crops.class).get(0);
        }
        em.persist(crops);
        em.flush();
        warehouse.addCrops(crops);
        warehouseRepository.saveAndFlush(warehouse);
        Long cropsId = crops.getId();

        // Get all the warehouseList where crops equals to cropsId
        defaultWarehouseShouldBeFound("cropsId.equals=" + cropsId);

        // Get all the warehouseList where crops equals to (cropsId + 1)
        defaultWarehouseShouldNotBeFound("cropsId.equals=" + (cropsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWarehouseShouldBeFound(String filter) throws Exception {
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(warehouse.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].area").value(hasItem(DEFAULT_AREA.doubleValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)));

        // Check, that the count call also returns 1
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWarehouseShouldNotBeFound(String filter) throws Exception {
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWarehouse() throws Exception {
        // Get the warehouse
        restWarehouseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWarehouse() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();

        // Update the warehouse
        Warehouse updatedWarehouse = warehouseRepository.findById(warehouse.getId()).get();
        // Disconnect from session so that the updates on updatedWarehouse are not directly saved in db
        em.detach(updatedWarehouse);
        updatedWarehouse
            .name(UPDATED_NAME)
            .area(UPDATED_AREA)
            .description(UPDATED_DESCRIPTION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .enable(UPDATED_ENABLE);
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(updatedWarehouse);

        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
        Warehouse testWarehouse = warehouseList.get(warehouseList.size() - 1);
        assertThat(testWarehouse.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWarehouse.getArea()).isEqualTo(UPDATED_AREA);
        assertThat(testWarehouse.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testWarehouse.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testWarehouse.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testWarehouse.getEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void putNonExistingWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWarehouseWithPatch() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();

        // Update the warehouse using partial update
        Warehouse partialUpdatedWarehouse = new Warehouse();
        partialUpdatedWarehouse.setId(warehouse.getId());

        partialUpdatedWarehouse.area(UPDATED_AREA);

        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWarehouse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWarehouse))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
        Warehouse testWarehouse = warehouseList.get(warehouseList.size() - 1);
        assertThat(testWarehouse.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWarehouse.getArea()).isEqualTo(UPDATED_AREA);
        assertThat(testWarehouse.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testWarehouse.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testWarehouse.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testWarehouse.getEnable()).isEqualTo(DEFAULT_ENABLE);
    }

    @Test
    @Transactional
    void fullUpdateWarehouseWithPatch() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();

        // Update the warehouse using partial update
        Warehouse partialUpdatedWarehouse = new Warehouse();
        partialUpdatedWarehouse.setId(warehouse.getId());

        partialUpdatedWarehouse
            .name(UPDATED_NAME)
            .area(UPDATED_AREA)
            .description(UPDATED_DESCRIPTION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .enable(UPDATED_ENABLE);

        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWarehouse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWarehouse))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
        Warehouse testWarehouse = warehouseList.get(warehouseList.size() - 1);
        assertThat(testWarehouse.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWarehouse.getArea()).isEqualTo(UPDATED_AREA);
        assertThat(testWarehouse.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testWarehouse.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testWarehouse.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testWarehouse.getEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void patchNonExistingWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWarehouse() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        int databaseSizeBeforeDelete = warehouseRepository.findAll().size();

        // Delete the warehouse
        restWarehouseMockMvc
            .perform(delete(ENTITY_API_URL_ID, warehouse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
