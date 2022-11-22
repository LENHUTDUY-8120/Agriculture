package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.ProtectionProduct;
import com.lnduy.agriculture.domain.Task;
import com.lnduy.agriculture.domain.Warehouse;
import com.lnduy.agriculture.repository.ProtectionProductRepository;
import com.lnduy.agriculture.service.criteria.ProtectionProductCriteria;
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
    private static final Float SMALLER_VOLUME = 1F - 1F;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final Integer DEFAULT_ENABLE = 1;
    private static final Integer UPDATED_ENABLE = 2;
    private static final Integer SMALLER_ENABLE = 1 - 1;

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
    void getProtectionProductsByIdFiltering() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        Long id = protectionProduct.getId();

        defaultProtectionProductShouldBeFound("id.equals=" + id);
        defaultProtectionProductShouldNotBeFound("id.notEquals=" + id);

        defaultProtectionProductShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProtectionProductShouldNotBeFound("id.greaterThan=" + id);

        defaultProtectionProductShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProtectionProductShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where name equals to DEFAULT_NAME
        defaultProtectionProductShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the protectionProductList where name equals to UPDATED_NAME
        defaultProtectionProductShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where name in DEFAULT_NAME or UPDATED_NAME
        defaultProtectionProductShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the protectionProductList where name equals to UPDATED_NAME
        defaultProtectionProductShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where name is not null
        defaultProtectionProductShouldBeFound("name.specified=true");

        // Get all the protectionProductList where name is null
        defaultProtectionProductShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllProtectionProductsByNameContainsSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where name contains DEFAULT_NAME
        defaultProtectionProductShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the protectionProductList where name contains UPDATED_NAME
        defaultProtectionProductShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where name does not contain DEFAULT_NAME
        defaultProtectionProductShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the protectionProductList where name does not contain UPDATED_NAME
        defaultProtectionProductShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where description equals to DEFAULT_DESCRIPTION
        defaultProtectionProductShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the protectionProductList where description equals to UPDATED_DESCRIPTION
        defaultProtectionProductShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultProtectionProductShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the protectionProductList where description equals to UPDATED_DESCRIPTION
        defaultProtectionProductShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where description is not null
        defaultProtectionProductShouldBeFound("description.specified=true");

        // Get all the protectionProductList where description is null
        defaultProtectionProductShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllProtectionProductsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where description contains DEFAULT_DESCRIPTION
        defaultProtectionProductShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the protectionProductList where description contains UPDATED_DESCRIPTION
        defaultProtectionProductShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where description does not contain DEFAULT_DESCRIPTION
        defaultProtectionProductShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the protectionProductList where description does not contain UPDATED_DESCRIPTION
        defaultProtectionProductShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where type equals to DEFAULT_TYPE
        defaultProtectionProductShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the protectionProductList where type equals to UPDATED_TYPE
        defaultProtectionProductShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultProtectionProductShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the protectionProductList where type equals to UPDATED_TYPE
        defaultProtectionProductShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where type is not null
        defaultProtectionProductShouldBeFound("type.specified=true");

        // Get all the protectionProductList where type is null
        defaultProtectionProductShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllProtectionProductsByTypeContainsSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where type contains DEFAULT_TYPE
        defaultProtectionProductShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the protectionProductList where type contains UPDATED_TYPE
        defaultProtectionProductShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where type does not contain DEFAULT_TYPE
        defaultProtectionProductShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the protectionProductList where type does not contain UPDATED_TYPE
        defaultProtectionProductShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByVolumeIsEqualToSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where volume equals to DEFAULT_VOLUME
        defaultProtectionProductShouldBeFound("volume.equals=" + DEFAULT_VOLUME);

        // Get all the protectionProductList where volume equals to UPDATED_VOLUME
        defaultProtectionProductShouldNotBeFound("volume.equals=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByVolumeIsInShouldWork() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where volume in DEFAULT_VOLUME or UPDATED_VOLUME
        defaultProtectionProductShouldBeFound("volume.in=" + DEFAULT_VOLUME + "," + UPDATED_VOLUME);

        // Get all the protectionProductList where volume equals to UPDATED_VOLUME
        defaultProtectionProductShouldNotBeFound("volume.in=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByVolumeIsNullOrNotNull() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where volume is not null
        defaultProtectionProductShouldBeFound("volume.specified=true");

        // Get all the protectionProductList where volume is null
        defaultProtectionProductShouldNotBeFound("volume.specified=false");
    }

    @Test
    @Transactional
    void getAllProtectionProductsByVolumeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where volume is greater than or equal to DEFAULT_VOLUME
        defaultProtectionProductShouldBeFound("volume.greaterThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the protectionProductList where volume is greater than or equal to UPDATED_VOLUME
        defaultProtectionProductShouldNotBeFound("volume.greaterThanOrEqual=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByVolumeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where volume is less than or equal to DEFAULT_VOLUME
        defaultProtectionProductShouldBeFound("volume.lessThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the protectionProductList where volume is less than or equal to SMALLER_VOLUME
        defaultProtectionProductShouldNotBeFound("volume.lessThanOrEqual=" + SMALLER_VOLUME);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByVolumeIsLessThanSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where volume is less than DEFAULT_VOLUME
        defaultProtectionProductShouldNotBeFound("volume.lessThan=" + DEFAULT_VOLUME);

        // Get all the protectionProductList where volume is less than UPDATED_VOLUME
        defaultProtectionProductShouldBeFound("volume.lessThan=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByVolumeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where volume is greater than DEFAULT_VOLUME
        defaultProtectionProductShouldNotBeFound("volume.greaterThan=" + DEFAULT_VOLUME);

        // Get all the protectionProductList where volume is greater than SMALLER_VOLUME
        defaultProtectionProductShouldBeFound("volume.greaterThan=" + SMALLER_VOLUME);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where unit equals to DEFAULT_UNIT
        defaultProtectionProductShouldBeFound("unit.equals=" + DEFAULT_UNIT);

        // Get all the protectionProductList where unit equals to UPDATED_UNIT
        defaultProtectionProductShouldNotBeFound("unit.equals=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByUnitIsInShouldWork() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where unit in DEFAULT_UNIT or UPDATED_UNIT
        defaultProtectionProductShouldBeFound("unit.in=" + DEFAULT_UNIT + "," + UPDATED_UNIT);

        // Get all the protectionProductList where unit equals to UPDATED_UNIT
        defaultProtectionProductShouldNotBeFound("unit.in=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where unit is not null
        defaultProtectionProductShouldBeFound("unit.specified=true");

        // Get all the protectionProductList where unit is null
        defaultProtectionProductShouldNotBeFound("unit.specified=false");
    }

    @Test
    @Transactional
    void getAllProtectionProductsByUnitContainsSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where unit contains DEFAULT_UNIT
        defaultProtectionProductShouldBeFound("unit.contains=" + DEFAULT_UNIT);

        // Get all the protectionProductList where unit contains UPDATED_UNIT
        defaultProtectionProductShouldNotBeFound("unit.contains=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByUnitNotContainsSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where unit does not contain DEFAULT_UNIT
        defaultProtectionProductShouldNotBeFound("unit.doesNotContain=" + DEFAULT_UNIT);

        // Get all the protectionProductList where unit does not contain UPDATED_UNIT
        defaultProtectionProductShouldBeFound("unit.doesNotContain=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByEnableIsEqualToSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where enable equals to DEFAULT_ENABLE
        defaultProtectionProductShouldBeFound("enable.equals=" + DEFAULT_ENABLE);

        // Get all the protectionProductList where enable equals to UPDATED_ENABLE
        defaultProtectionProductShouldNotBeFound("enable.equals=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByEnableIsInShouldWork() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where enable in DEFAULT_ENABLE or UPDATED_ENABLE
        defaultProtectionProductShouldBeFound("enable.in=" + DEFAULT_ENABLE + "," + UPDATED_ENABLE);

        // Get all the protectionProductList where enable equals to UPDATED_ENABLE
        defaultProtectionProductShouldNotBeFound("enable.in=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByEnableIsNullOrNotNull() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where enable is not null
        defaultProtectionProductShouldBeFound("enable.specified=true");

        // Get all the protectionProductList where enable is null
        defaultProtectionProductShouldNotBeFound("enable.specified=false");
    }

    @Test
    @Transactional
    void getAllProtectionProductsByEnableIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where enable is greater than or equal to DEFAULT_ENABLE
        defaultProtectionProductShouldBeFound("enable.greaterThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the protectionProductList where enable is greater than or equal to UPDATED_ENABLE
        defaultProtectionProductShouldNotBeFound("enable.greaterThanOrEqual=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByEnableIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where enable is less than or equal to DEFAULT_ENABLE
        defaultProtectionProductShouldBeFound("enable.lessThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the protectionProductList where enable is less than or equal to SMALLER_ENABLE
        defaultProtectionProductShouldNotBeFound("enable.lessThanOrEqual=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByEnableIsLessThanSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where enable is less than DEFAULT_ENABLE
        defaultProtectionProductShouldNotBeFound("enable.lessThan=" + DEFAULT_ENABLE);

        // Get all the protectionProductList where enable is less than UPDATED_ENABLE
        defaultProtectionProductShouldBeFound("enable.lessThan=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByEnableIsGreaterThanSomething() throws Exception {
        // Initialize the database
        protectionProductRepository.saveAndFlush(protectionProduct);

        // Get all the protectionProductList where enable is greater than DEFAULT_ENABLE
        defaultProtectionProductShouldNotBeFound("enable.greaterThan=" + DEFAULT_ENABLE);

        // Get all the protectionProductList where enable is greater than SMALLER_ENABLE
        defaultProtectionProductShouldBeFound("enable.greaterThan=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllProtectionProductsByWarehouseIsEqualToSomething() throws Exception {
        Warehouse warehouse;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            protectionProductRepository.saveAndFlush(protectionProduct);
            warehouse = WarehouseResourceIT.createEntity(em);
        } else {
            warehouse = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        em.persist(warehouse);
        em.flush();
        protectionProduct.setWarehouse(warehouse);
        protectionProductRepository.saveAndFlush(protectionProduct);
        Long warehouseId = warehouse.getId();

        // Get all the protectionProductList where warehouse equals to warehouseId
        defaultProtectionProductShouldBeFound("warehouseId.equals=" + warehouseId);

        // Get all the protectionProductList where warehouse equals to (warehouseId + 1)
        defaultProtectionProductShouldNotBeFound("warehouseId.equals=" + (warehouseId + 1));
    }

    @Test
    @Transactional
    void getAllProtectionProductsByTaskIsEqualToSomething() throws Exception {
        Task task;
        if (TestUtil.findAll(em, Task.class).isEmpty()) {
            protectionProductRepository.saveAndFlush(protectionProduct);
            task = TaskResourceIT.createEntity(em);
        } else {
            task = TestUtil.findAll(em, Task.class).get(0);
        }
        em.persist(task);
        em.flush();
        protectionProduct.addTask(task);
        protectionProductRepository.saveAndFlush(protectionProduct);
        Long taskId = task.getId();

        // Get all the protectionProductList where task equals to taskId
        defaultProtectionProductShouldBeFound("taskId.equals=" + taskId);

        // Get all the protectionProductList where task equals to (taskId + 1)
        defaultProtectionProductShouldNotBeFound("taskId.equals=" + (taskId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProtectionProductShouldBeFound(String filter) throws Exception {
        restProtectionProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(protectionProduct.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.doubleValue())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)));

        // Check, that the count call also returns 1
        restProtectionProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProtectionProductShouldNotBeFound(String filter) throws Exception {
        restProtectionProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProtectionProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
