package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Device;
import com.lnduy.agriculture.domain.DeviceCategory;
import com.lnduy.agriculture.domain.Field;
import com.lnduy.agriculture.repository.DeviceRepository;
import com.lnduy.agriculture.service.criteria.DeviceCriteria;
import com.lnduy.agriculture.service.dto.DeviceDTO;
import com.lnduy.agriculture.service.mapper.DeviceMapper;
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
 * Integration tests for the {@link DeviceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DeviceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_IP = "AAAAAAAAAA";
    private static final String UPDATED_IP = "BBBBBBBBBB";

    private static final String DEFAULT_PROPERTY = "AAAAAAAAAA";
    private static final String UPDATED_PROPERTY = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_ENABLE = 1;
    private static final Integer UPDATED_ENABLE = 2;
    private static final Integer SMALLER_ENABLE = 1 - 1;

    private static final String ENTITY_API_URL = "/api/devices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeviceMockMvc;

    private Device device;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Device createEntity(EntityManager em) {
        Device device = new Device()
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .ip(DEFAULT_IP)
            .property(DEFAULT_PROPERTY)
            .type(DEFAULT_TYPE)
            .enable(DEFAULT_ENABLE);
        return device;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Device createUpdatedEntity(EntityManager em) {
        Device device = new Device()
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .ip(UPDATED_IP)
            .property(UPDATED_PROPERTY)
            .type(UPDATED_TYPE)
            .enable(UPDATED_ENABLE);
        return device;
    }

    @BeforeEach
    public void initTest() {
        device = createEntity(em);
    }

    @Test
    @Transactional
    void createDevice() throws Exception {
        int databaseSizeBeforeCreate = deviceRepository.findAll().size();
        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);
        restDeviceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deviceDTO)))
            .andExpect(status().isCreated());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeCreate + 1);
        Device testDevice = deviceList.get(deviceList.size() - 1);
        assertThat(testDevice.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDevice.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testDevice.getIp()).isEqualTo(DEFAULT_IP);
        assertThat(testDevice.getProperty()).isEqualTo(DEFAULT_PROPERTY);
        assertThat(testDevice.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testDevice.getEnable()).isEqualTo(DEFAULT_ENABLE);
    }

    @Test
    @Transactional
    void createDeviceWithExistingId() throws Exception {
        // Create the Device with an existing ID
        device.setId(1L);
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        int databaseSizeBeforeCreate = deviceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeviceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deviceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDevices() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList
        restDeviceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(device.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].ip").value(hasItem(DEFAULT_IP)))
            .andExpect(jsonPath("$.[*].property").value(hasItem(DEFAULT_PROPERTY)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)));
    }

    @Test
    @Transactional
    void getDevice() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get the device
        restDeviceMockMvc
            .perform(get(ENTITY_API_URL_ID, device.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(device.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.ip").value(DEFAULT_IP))
            .andExpect(jsonPath("$.property").value(DEFAULT_PROPERTY))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.enable").value(DEFAULT_ENABLE));
    }

    @Test
    @Transactional
    void getDevicesByIdFiltering() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        Long id = device.getId();

        defaultDeviceShouldBeFound("id.equals=" + id);
        defaultDeviceShouldNotBeFound("id.notEquals=" + id);

        defaultDeviceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDeviceShouldNotBeFound("id.greaterThan=" + id);

        defaultDeviceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDeviceShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDevicesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where name equals to DEFAULT_NAME
        defaultDeviceShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the deviceList where name equals to UPDATED_NAME
        defaultDeviceShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDevicesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where name in DEFAULT_NAME or UPDATED_NAME
        defaultDeviceShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the deviceList where name equals to UPDATED_NAME
        defaultDeviceShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDevicesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where name is not null
        defaultDeviceShouldBeFound("name.specified=true");

        // Get all the deviceList where name is null
        defaultDeviceShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllDevicesByNameContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where name contains DEFAULT_NAME
        defaultDeviceShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the deviceList where name contains UPDATED_NAME
        defaultDeviceShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDevicesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where name does not contain DEFAULT_NAME
        defaultDeviceShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the deviceList where name does not contain UPDATED_NAME
        defaultDeviceShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDevicesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where code equals to DEFAULT_CODE
        defaultDeviceShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the deviceList where code equals to UPDATED_CODE
        defaultDeviceShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDevicesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where code in DEFAULT_CODE or UPDATED_CODE
        defaultDeviceShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the deviceList where code equals to UPDATED_CODE
        defaultDeviceShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDevicesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where code is not null
        defaultDeviceShouldBeFound("code.specified=true");

        // Get all the deviceList where code is null
        defaultDeviceShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllDevicesByCodeContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where code contains DEFAULT_CODE
        defaultDeviceShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the deviceList where code contains UPDATED_CODE
        defaultDeviceShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDevicesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where code does not contain DEFAULT_CODE
        defaultDeviceShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the deviceList where code does not contain UPDATED_CODE
        defaultDeviceShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDevicesByIpIsEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where ip equals to DEFAULT_IP
        defaultDeviceShouldBeFound("ip.equals=" + DEFAULT_IP);

        // Get all the deviceList where ip equals to UPDATED_IP
        defaultDeviceShouldNotBeFound("ip.equals=" + UPDATED_IP);
    }

    @Test
    @Transactional
    void getAllDevicesByIpIsInShouldWork() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where ip in DEFAULT_IP or UPDATED_IP
        defaultDeviceShouldBeFound("ip.in=" + DEFAULT_IP + "," + UPDATED_IP);

        // Get all the deviceList where ip equals to UPDATED_IP
        defaultDeviceShouldNotBeFound("ip.in=" + UPDATED_IP);
    }

    @Test
    @Transactional
    void getAllDevicesByIpIsNullOrNotNull() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where ip is not null
        defaultDeviceShouldBeFound("ip.specified=true");

        // Get all the deviceList where ip is null
        defaultDeviceShouldNotBeFound("ip.specified=false");
    }

    @Test
    @Transactional
    void getAllDevicesByIpContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where ip contains DEFAULT_IP
        defaultDeviceShouldBeFound("ip.contains=" + DEFAULT_IP);

        // Get all the deviceList where ip contains UPDATED_IP
        defaultDeviceShouldNotBeFound("ip.contains=" + UPDATED_IP);
    }

    @Test
    @Transactional
    void getAllDevicesByIpNotContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where ip does not contain DEFAULT_IP
        defaultDeviceShouldNotBeFound("ip.doesNotContain=" + DEFAULT_IP);

        // Get all the deviceList where ip does not contain UPDATED_IP
        defaultDeviceShouldBeFound("ip.doesNotContain=" + UPDATED_IP);
    }

    @Test
    @Transactional
    void getAllDevicesByPropertyIsEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where property equals to DEFAULT_PROPERTY
        defaultDeviceShouldBeFound("property.equals=" + DEFAULT_PROPERTY);

        // Get all the deviceList where property equals to UPDATED_PROPERTY
        defaultDeviceShouldNotBeFound("property.equals=" + UPDATED_PROPERTY);
    }

    @Test
    @Transactional
    void getAllDevicesByPropertyIsInShouldWork() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where property in DEFAULT_PROPERTY or UPDATED_PROPERTY
        defaultDeviceShouldBeFound("property.in=" + DEFAULT_PROPERTY + "," + UPDATED_PROPERTY);

        // Get all the deviceList where property equals to UPDATED_PROPERTY
        defaultDeviceShouldNotBeFound("property.in=" + UPDATED_PROPERTY);
    }

    @Test
    @Transactional
    void getAllDevicesByPropertyIsNullOrNotNull() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where property is not null
        defaultDeviceShouldBeFound("property.specified=true");

        // Get all the deviceList where property is null
        defaultDeviceShouldNotBeFound("property.specified=false");
    }

    @Test
    @Transactional
    void getAllDevicesByPropertyContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where property contains DEFAULT_PROPERTY
        defaultDeviceShouldBeFound("property.contains=" + DEFAULT_PROPERTY);

        // Get all the deviceList where property contains UPDATED_PROPERTY
        defaultDeviceShouldNotBeFound("property.contains=" + UPDATED_PROPERTY);
    }

    @Test
    @Transactional
    void getAllDevicesByPropertyNotContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where property does not contain DEFAULT_PROPERTY
        defaultDeviceShouldNotBeFound("property.doesNotContain=" + DEFAULT_PROPERTY);

        // Get all the deviceList where property does not contain UPDATED_PROPERTY
        defaultDeviceShouldBeFound("property.doesNotContain=" + UPDATED_PROPERTY);
    }

    @Test
    @Transactional
    void getAllDevicesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where type equals to DEFAULT_TYPE
        defaultDeviceShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the deviceList where type equals to UPDATED_TYPE
        defaultDeviceShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllDevicesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultDeviceShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the deviceList where type equals to UPDATED_TYPE
        defaultDeviceShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllDevicesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where type is not null
        defaultDeviceShouldBeFound("type.specified=true");

        // Get all the deviceList where type is null
        defaultDeviceShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllDevicesByTypeContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where type contains DEFAULT_TYPE
        defaultDeviceShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the deviceList where type contains UPDATED_TYPE
        defaultDeviceShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllDevicesByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where type does not contain DEFAULT_TYPE
        defaultDeviceShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the deviceList where type does not contain UPDATED_TYPE
        defaultDeviceShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllDevicesByEnableIsEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where enable equals to DEFAULT_ENABLE
        defaultDeviceShouldBeFound("enable.equals=" + DEFAULT_ENABLE);

        // Get all the deviceList where enable equals to UPDATED_ENABLE
        defaultDeviceShouldNotBeFound("enable.equals=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllDevicesByEnableIsInShouldWork() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where enable in DEFAULT_ENABLE or UPDATED_ENABLE
        defaultDeviceShouldBeFound("enable.in=" + DEFAULT_ENABLE + "," + UPDATED_ENABLE);

        // Get all the deviceList where enable equals to UPDATED_ENABLE
        defaultDeviceShouldNotBeFound("enable.in=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllDevicesByEnableIsNullOrNotNull() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where enable is not null
        defaultDeviceShouldBeFound("enable.specified=true");

        // Get all the deviceList where enable is null
        defaultDeviceShouldNotBeFound("enable.specified=false");
    }

    @Test
    @Transactional
    void getAllDevicesByEnableIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where enable is greater than or equal to DEFAULT_ENABLE
        defaultDeviceShouldBeFound("enable.greaterThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the deviceList where enable is greater than or equal to UPDATED_ENABLE
        defaultDeviceShouldNotBeFound("enable.greaterThanOrEqual=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllDevicesByEnableIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where enable is less than or equal to DEFAULT_ENABLE
        defaultDeviceShouldBeFound("enable.lessThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the deviceList where enable is less than or equal to SMALLER_ENABLE
        defaultDeviceShouldNotBeFound("enable.lessThanOrEqual=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllDevicesByEnableIsLessThanSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where enable is less than DEFAULT_ENABLE
        defaultDeviceShouldNotBeFound("enable.lessThan=" + DEFAULT_ENABLE);

        // Get all the deviceList where enable is less than UPDATED_ENABLE
        defaultDeviceShouldBeFound("enable.lessThan=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllDevicesByEnableIsGreaterThanSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where enable is greater than DEFAULT_ENABLE
        defaultDeviceShouldNotBeFound("enable.greaterThan=" + DEFAULT_ENABLE);

        // Get all the deviceList where enable is greater than SMALLER_ENABLE
        defaultDeviceShouldBeFound("enable.greaterThan=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllDevicesByCategoryIsEqualToSomething() throws Exception {
        DeviceCategory category;
        if (TestUtil.findAll(em, DeviceCategory.class).isEmpty()) {
            deviceRepository.saveAndFlush(device);
            category = DeviceCategoryResourceIT.createEntity(em);
        } else {
            category = TestUtil.findAll(em, DeviceCategory.class).get(0);
        }
        em.persist(category);
        em.flush();
        device.setCategory(category);
        deviceRepository.saveAndFlush(device);
        Long categoryId = category.getId();

        // Get all the deviceList where category equals to categoryId
        defaultDeviceShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the deviceList where category equals to (categoryId + 1)
        defaultDeviceShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    @Test
    @Transactional
    void getAllDevicesByFieldIsEqualToSomething() throws Exception {
        Field field;
        if (TestUtil.findAll(em, Field.class).isEmpty()) {
            deviceRepository.saveAndFlush(device);
            field = FieldResourceIT.createEntity(em);
        } else {
            field = TestUtil.findAll(em, Field.class).get(0);
        }
        em.persist(field);
        em.flush();
        device.setField(field);
        deviceRepository.saveAndFlush(device);
        Long fieldId = field.getId();

        // Get all the deviceList where field equals to fieldId
        defaultDeviceShouldBeFound("fieldId.equals=" + fieldId);

        // Get all the deviceList where field equals to (fieldId + 1)
        defaultDeviceShouldNotBeFound("fieldId.equals=" + (fieldId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDeviceShouldBeFound(String filter) throws Exception {
        restDeviceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(device.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].ip").value(hasItem(DEFAULT_IP)))
            .andExpect(jsonPath("$.[*].property").value(hasItem(DEFAULT_PROPERTY)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)));

        // Check, that the count call also returns 1
        restDeviceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDeviceShouldNotBeFound(String filter) throws Exception {
        restDeviceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDeviceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDevice() throws Exception {
        // Get the device
        restDeviceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDevice() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();

        // Update the device
        Device updatedDevice = deviceRepository.findById(device.getId()).get();
        // Disconnect from session so that the updates on updatedDevice are not directly saved in db
        em.detach(updatedDevice);
        updatedDevice
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .ip(UPDATED_IP)
            .property(UPDATED_PROPERTY)
            .type(UPDATED_TYPE)
            .enable(UPDATED_ENABLE);
        DeviceDTO deviceDTO = deviceMapper.toDto(updatedDevice);

        restDeviceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deviceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deviceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
        Device testDevice = deviceList.get(deviceList.size() - 1);
        assertThat(testDevice.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDevice.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testDevice.getIp()).isEqualTo(UPDATED_IP);
        assertThat(testDevice.getProperty()).isEqualTo(UPDATED_PROPERTY);
        assertThat(testDevice.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testDevice.getEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void putNonExistingDevice() throws Exception {
        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();
        device.setId(count.incrementAndGet());

        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeviceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deviceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deviceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDevice() throws Exception {
        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();
        device.setId(count.incrementAndGet());

        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeviceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deviceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDevice() throws Exception {
        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();
        device.setId(count.incrementAndGet());

        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeviceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deviceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDeviceWithPatch() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();

        // Update the device using partial update
        Device partialUpdatedDevice = new Device();
        partialUpdatedDevice.setId(device.getId());

        partialUpdatedDevice.property(UPDATED_PROPERTY).type(UPDATED_TYPE).enable(UPDATED_ENABLE);

        restDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDevice.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDevice))
            )
            .andExpect(status().isOk());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
        Device testDevice = deviceList.get(deviceList.size() - 1);
        assertThat(testDevice.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDevice.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testDevice.getIp()).isEqualTo(DEFAULT_IP);
        assertThat(testDevice.getProperty()).isEqualTo(UPDATED_PROPERTY);
        assertThat(testDevice.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testDevice.getEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void fullUpdateDeviceWithPatch() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();

        // Update the device using partial update
        Device partialUpdatedDevice = new Device();
        partialUpdatedDevice.setId(device.getId());

        partialUpdatedDevice
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .ip(UPDATED_IP)
            .property(UPDATED_PROPERTY)
            .type(UPDATED_TYPE)
            .enable(UPDATED_ENABLE);

        restDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDevice.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDevice))
            )
            .andExpect(status().isOk());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
        Device testDevice = deviceList.get(deviceList.size() - 1);
        assertThat(testDevice.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDevice.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testDevice.getIp()).isEqualTo(UPDATED_IP);
        assertThat(testDevice.getProperty()).isEqualTo(UPDATED_PROPERTY);
        assertThat(testDevice.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testDevice.getEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void patchNonExistingDevice() throws Exception {
        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();
        device.setId(count.incrementAndGet());

        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, deviceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(deviceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDevice() throws Exception {
        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();
        device.setId(count.incrementAndGet());

        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(deviceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDevice() throws Exception {
        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();
        device.setId(count.incrementAndGet());

        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(deviceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDevice() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        int databaseSizeBeforeDelete = deviceRepository.findAll().size();

        // Delete the device
        restDeviceMockMvc
            .perform(delete(ENTITY_API_URL_ID, device.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
