package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Device;
import com.lnduy.agriculture.domain.Field;
import com.lnduy.agriculture.domain.Season;
import com.lnduy.agriculture.domain.SoilType;
import com.lnduy.agriculture.repository.FieldRepository;
import com.lnduy.agriculture.service.criteria.FieldCriteria;
import com.lnduy.agriculture.service.dto.FieldDTO;
import com.lnduy.agriculture.service.mapper.FieldMapper;
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
 * Integration tests for the {@link FieldResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FieldResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_GEO_JSON = "AAAAAAAAAA";
    private static final String UPDATED_GEO_JSON = "BBBBBBBBBB";

    private static final Double DEFAULT_AREA = 1D;
    private static final Double UPDATED_AREA = 2D;
    private static final Double SMALLER_AREA = 1D - 1D;

    private static final String DEFAULT_DESCRIPTIONS = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTIONS = "BBBBBBBBBB";

    private static final Integer DEFAULT_ENABLE = 1;
    private static final Integer UPDATED_ENABLE = 2;
    private static final Integer SMALLER_ENABLE = 1 - 1;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;
    private static final Double SMALLER_LATITUDE = 1D - 1D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;
    private static final Double SMALLER_LONGITUDE = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/fields";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private FieldMapper fieldMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFieldMockMvc;

    private Field field;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Field createEntity(EntityManager em) {
        Field field = new Field()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .geoJson(DEFAULT_GEO_JSON)
            .area(DEFAULT_AREA)
            .descriptions(DEFAULT_DESCRIPTIONS)
            .enable(DEFAULT_ENABLE)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE);
        return field;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Field createUpdatedEntity(EntityManager em) {
        Field field = new Field()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .geoJson(UPDATED_GEO_JSON)
            .area(UPDATED_AREA)
            .descriptions(UPDATED_DESCRIPTIONS)
            .enable(UPDATED_ENABLE)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);
        return field;
    }

    @BeforeEach
    public void initTest() {
        field = createEntity(em);
    }

    @Test
    @Transactional
    void createField() throws Exception {
        int databaseSizeBeforeCreate = fieldRepository.findAll().size();
        // Create the Field
        FieldDTO fieldDTO = fieldMapper.toDto(field);
        restFieldMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fieldDTO)))
            .andExpect(status().isCreated());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeCreate + 1);
        Field testField = fieldList.get(fieldList.size() - 1);
        assertThat(testField.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testField.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testField.getGeoJson()).isEqualTo(DEFAULT_GEO_JSON);
        assertThat(testField.getArea()).isEqualTo(DEFAULT_AREA);
        assertThat(testField.getDescriptions()).isEqualTo(DEFAULT_DESCRIPTIONS);
        assertThat(testField.getEnable()).isEqualTo(DEFAULT_ENABLE);
        assertThat(testField.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testField.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void createFieldWithExistingId() throws Exception {
        // Create the Field with an existing ID
        field.setId(1L);
        FieldDTO fieldDTO = fieldMapper.toDto(field);

        int databaseSizeBeforeCreate = fieldRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFieldMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fieldDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFields() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList
        restFieldMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(field.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].geoJson").value(hasItem(DEFAULT_GEO_JSON)))
            .andExpect(jsonPath("$.[*].area").value(hasItem(DEFAULT_AREA.doubleValue())))
            .andExpect(jsonPath("$.[*].descriptions").value(hasItem(DEFAULT_DESCRIPTIONS)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())));
    }

    @Test
    @Transactional
    void getField() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get the field
        restFieldMockMvc
            .perform(get(ENTITY_API_URL_ID, field.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(field.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.geoJson").value(DEFAULT_GEO_JSON))
            .andExpect(jsonPath("$.area").value(DEFAULT_AREA.doubleValue()))
            .andExpect(jsonPath("$.descriptions").value(DEFAULT_DESCRIPTIONS))
            .andExpect(jsonPath("$.enable").value(DEFAULT_ENABLE))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()));
    }

    @Test
    @Transactional
    void getFieldsByIdFiltering() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        Long id = field.getId();

        defaultFieldShouldBeFound("id.equals=" + id);
        defaultFieldShouldNotBeFound("id.notEquals=" + id);

        defaultFieldShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFieldShouldNotBeFound("id.greaterThan=" + id);

        defaultFieldShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFieldShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFieldsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where code equals to DEFAULT_CODE
        defaultFieldShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the fieldList where code equals to UPDATED_CODE
        defaultFieldShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllFieldsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where code in DEFAULT_CODE or UPDATED_CODE
        defaultFieldShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the fieldList where code equals to UPDATED_CODE
        defaultFieldShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllFieldsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where code is not null
        defaultFieldShouldBeFound("code.specified=true");

        // Get all the fieldList where code is null
        defaultFieldShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllFieldsByCodeContainsSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where code contains DEFAULT_CODE
        defaultFieldShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the fieldList where code contains UPDATED_CODE
        defaultFieldShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllFieldsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where code does not contain DEFAULT_CODE
        defaultFieldShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the fieldList where code does not contain UPDATED_CODE
        defaultFieldShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllFieldsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where name equals to DEFAULT_NAME
        defaultFieldShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the fieldList where name equals to UPDATED_NAME
        defaultFieldShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFieldsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where name in DEFAULT_NAME or UPDATED_NAME
        defaultFieldShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the fieldList where name equals to UPDATED_NAME
        defaultFieldShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFieldsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where name is not null
        defaultFieldShouldBeFound("name.specified=true");

        // Get all the fieldList where name is null
        defaultFieldShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllFieldsByNameContainsSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where name contains DEFAULT_NAME
        defaultFieldShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the fieldList where name contains UPDATED_NAME
        defaultFieldShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFieldsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where name does not contain DEFAULT_NAME
        defaultFieldShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the fieldList where name does not contain UPDATED_NAME
        defaultFieldShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFieldsByGeoJsonIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where geoJson equals to DEFAULT_GEO_JSON
        defaultFieldShouldBeFound("geoJson.equals=" + DEFAULT_GEO_JSON);

        // Get all the fieldList where geoJson equals to UPDATED_GEO_JSON
        defaultFieldShouldNotBeFound("geoJson.equals=" + UPDATED_GEO_JSON);
    }

    @Test
    @Transactional
    void getAllFieldsByGeoJsonIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where geoJson in DEFAULT_GEO_JSON or UPDATED_GEO_JSON
        defaultFieldShouldBeFound("geoJson.in=" + DEFAULT_GEO_JSON + "," + UPDATED_GEO_JSON);

        // Get all the fieldList where geoJson equals to UPDATED_GEO_JSON
        defaultFieldShouldNotBeFound("geoJson.in=" + UPDATED_GEO_JSON);
    }

    @Test
    @Transactional
    void getAllFieldsByGeoJsonIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where geoJson is not null
        defaultFieldShouldBeFound("geoJson.specified=true");

        // Get all the fieldList where geoJson is null
        defaultFieldShouldNotBeFound("geoJson.specified=false");
    }

    @Test
    @Transactional
    void getAllFieldsByGeoJsonContainsSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where geoJson contains DEFAULT_GEO_JSON
        defaultFieldShouldBeFound("geoJson.contains=" + DEFAULT_GEO_JSON);

        // Get all the fieldList where geoJson contains UPDATED_GEO_JSON
        defaultFieldShouldNotBeFound("geoJson.contains=" + UPDATED_GEO_JSON);
    }

    @Test
    @Transactional
    void getAllFieldsByGeoJsonNotContainsSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where geoJson does not contain DEFAULT_GEO_JSON
        defaultFieldShouldNotBeFound("geoJson.doesNotContain=" + DEFAULT_GEO_JSON);

        // Get all the fieldList where geoJson does not contain UPDATED_GEO_JSON
        defaultFieldShouldBeFound("geoJson.doesNotContain=" + UPDATED_GEO_JSON);
    }

    @Test
    @Transactional
    void getAllFieldsByAreaIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where area equals to DEFAULT_AREA
        defaultFieldShouldBeFound("area.equals=" + DEFAULT_AREA);

        // Get all the fieldList where area equals to UPDATED_AREA
        defaultFieldShouldNotBeFound("area.equals=" + UPDATED_AREA);
    }

    @Test
    @Transactional
    void getAllFieldsByAreaIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where area in DEFAULT_AREA or UPDATED_AREA
        defaultFieldShouldBeFound("area.in=" + DEFAULT_AREA + "," + UPDATED_AREA);

        // Get all the fieldList where area equals to UPDATED_AREA
        defaultFieldShouldNotBeFound("area.in=" + UPDATED_AREA);
    }

    @Test
    @Transactional
    void getAllFieldsByAreaIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where area is not null
        defaultFieldShouldBeFound("area.specified=true");

        // Get all the fieldList where area is null
        defaultFieldShouldNotBeFound("area.specified=false");
    }

    @Test
    @Transactional
    void getAllFieldsByAreaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where area is greater than or equal to DEFAULT_AREA
        defaultFieldShouldBeFound("area.greaterThanOrEqual=" + DEFAULT_AREA);

        // Get all the fieldList where area is greater than or equal to UPDATED_AREA
        defaultFieldShouldNotBeFound("area.greaterThanOrEqual=" + UPDATED_AREA);
    }

    @Test
    @Transactional
    void getAllFieldsByAreaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where area is less than or equal to DEFAULT_AREA
        defaultFieldShouldBeFound("area.lessThanOrEqual=" + DEFAULT_AREA);

        // Get all the fieldList where area is less than or equal to SMALLER_AREA
        defaultFieldShouldNotBeFound("area.lessThanOrEqual=" + SMALLER_AREA);
    }

    @Test
    @Transactional
    void getAllFieldsByAreaIsLessThanSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where area is less than DEFAULT_AREA
        defaultFieldShouldNotBeFound("area.lessThan=" + DEFAULT_AREA);

        // Get all the fieldList where area is less than UPDATED_AREA
        defaultFieldShouldBeFound("area.lessThan=" + UPDATED_AREA);
    }

    @Test
    @Transactional
    void getAllFieldsByAreaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where area is greater than DEFAULT_AREA
        defaultFieldShouldNotBeFound("area.greaterThan=" + DEFAULT_AREA);

        // Get all the fieldList where area is greater than SMALLER_AREA
        defaultFieldShouldBeFound("area.greaterThan=" + SMALLER_AREA);
    }

    @Test
    @Transactional
    void getAllFieldsByDescriptionsIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where descriptions equals to DEFAULT_DESCRIPTIONS
        defaultFieldShouldBeFound("descriptions.equals=" + DEFAULT_DESCRIPTIONS);

        // Get all the fieldList where descriptions equals to UPDATED_DESCRIPTIONS
        defaultFieldShouldNotBeFound("descriptions.equals=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllFieldsByDescriptionsIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where descriptions in DEFAULT_DESCRIPTIONS or UPDATED_DESCRIPTIONS
        defaultFieldShouldBeFound("descriptions.in=" + DEFAULT_DESCRIPTIONS + "," + UPDATED_DESCRIPTIONS);

        // Get all the fieldList where descriptions equals to UPDATED_DESCRIPTIONS
        defaultFieldShouldNotBeFound("descriptions.in=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllFieldsByDescriptionsIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where descriptions is not null
        defaultFieldShouldBeFound("descriptions.specified=true");

        // Get all the fieldList where descriptions is null
        defaultFieldShouldNotBeFound("descriptions.specified=false");
    }

    @Test
    @Transactional
    void getAllFieldsByDescriptionsContainsSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where descriptions contains DEFAULT_DESCRIPTIONS
        defaultFieldShouldBeFound("descriptions.contains=" + DEFAULT_DESCRIPTIONS);

        // Get all the fieldList where descriptions contains UPDATED_DESCRIPTIONS
        defaultFieldShouldNotBeFound("descriptions.contains=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllFieldsByDescriptionsNotContainsSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where descriptions does not contain DEFAULT_DESCRIPTIONS
        defaultFieldShouldNotBeFound("descriptions.doesNotContain=" + DEFAULT_DESCRIPTIONS);

        // Get all the fieldList where descriptions does not contain UPDATED_DESCRIPTIONS
        defaultFieldShouldBeFound("descriptions.doesNotContain=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllFieldsByEnableIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where enable equals to DEFAULT_ENABLE
        defaultFieldShouldBeFound("enable.equals=" + DEFAULT_ENABLE);

        // Get all the fieldList where enable equals to UPDATED_ENABLE
        defaultFieldShouldNotBeFound("enable.equals=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllFieldsByEnableIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where enable in DEFAULT_ENABLE or UPDATED_ENABLE
        defaultFieldShouldBeFound("enable.in=" + DEFAULT_ENABLE + "," + UPDATED_ENABLE);

        // Get all the fieldList where enable equals to UPDATED_ENABLE
        defaultFieldShouldNotBeFound("enable.in=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllFieldsByEnableIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where enable is not null
        defaultFieldShouldBeFound("enable.specified=true");

        // Get all the fieldList where enable is null
        defaultFieldShouldNotBeFound("enable.specified=false");
    }

    @Test
    @Transactional
    void getAllFieldsByEnableIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where enable is greater than or equal to DEFAULT_ENABLE
        defaultFieldShouldBeFound("enable.greaterThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the fieldList where enable is greater than or equal to UPDATED_ENABLE
        defaultFieldShouldNotBeFound("enable.greaterThanOrEqual=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllFieldsByEnableIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where enable is less than or equal to DEFAULT_ENABLE
        defaultFieldShouldBeFound("enable.lessThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the fieldList where enable is less than or equal to SMALLER_ENABLE
        defaultFieldShouldNotBeFound("enable.lessThanOrEqual=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllFieldsByEnableIsLessThanSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where enable is less than DEFAULT_ENABLE
        defaultFieldShouldNotBeFound("enable.lessThan=" + DEFAULT_ENABLE);

        // Get all the fieldList where enable is less than UPDATED_ENABLE
        defaultFieldShouldBeFound("enable.lessThan=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllFieldsByEnableIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where enable is greater than DEFAULT_ENABLE
        defaultFieldShouldNotBeFound("enable.greaterThan=" + DEFAULT_ENABLE);

        // Get all the fieldList where enable is greater than SMALLER_ENABLE
        defaultFieldShouldBeFound("enable.greaterThan=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllFieldsByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where latitude equals to DEFAULT_LATITUDE
        defaultFieldShouldBeFound("latitude.equals=" + DEFAULT_LATITUDE);

        // Get all the fieldList where latitude equals to UPDATED_LATITUDE
        defaultFieldShouldNotBeFound("latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllFieldsByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where latitude in DEFAULT_LATITUDE or UPDATED_LATITUDE
        defaultFieldShouldBeFound("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE);

        // Get all the fieldList where latitude equals to UPDATED_LATITUDE
        defaultFieldShouldNotBeFound("latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllFieldsByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where latitude is not null
        defaultFieldShouldBeFound("latitude.specified=true");

        // Get all the fieldList where latitude is null
        defaultFieldShouldNotBeFound("latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllFieldsByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where latitude is greater than or equal to DEFAULT_LATITUDE
        defaultFieldShouldBeFound("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the fieldList where latitude is greater than or equal to UPDATED_LATITUDE
        defaultFieldShouldNotBeFound("latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllFieldsByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where latitude is less than or equal to DEFAULT_LATITUDE
        defaultFieldShouldBeFound("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the fieldList where latitude is less than or equal to SMALLER_LATITUDE
        defaultFieldShouldNotBeFound("latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllFieldsByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where latitude is less than DEFAULT_LATITUDE
        defaultFieldShouldNotBeFound("latitude.lessThan=" + DEFAULT_LATITUDE);

        // Get all the fieldList where latitude is less than UPDATED_LATITUDE
        defaultFieldShouldBeFound("latitude.lessThan=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllFieldsByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where latitude is greater than DEFAULT_LATITUDE
        defaultFieldShouldNotBeFound("latitude.greaterThan=" + DEFAULT_LATITUDE);

        // Get all the fieldList where latitude is greater than SMALLER_LATITUDE
        defaultFieldShouldBeFound("latitude.greaterThan=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllFieldsByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where longitude equals to DEFAULT_LONGITUDE
        defaultFieldShouldBeFound("longitude.equals=" + DEFAULT_LONGITUDE);

        // Get all the fieldList where longitude equals to UPDATED_LONGITUDE
        defaultFieldShouldNotBeFound("longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllFieldsByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where longitude in DEFAULT_LONGITUDE or UPDATED_LONGITUDE
        defaultFieldShouldBeFound("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE);

        // Get all the fieldList where longitude equals to UPDATED_LONGITUDE
        defaultFieldShouldNotBeFound("longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllFieldsByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where longitude is not null
        defaultFieldShouldBeFound("longitude.specified=true");

        // Get all the fieldList where longitude is null
        defaultFieldShouldNotBeFound("longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllFieldsByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where longitude is greater than or equal to DEFAULT_LONGITUDE
        defaultFieldShouldBeFound("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the fieldList where longitude is greater than or equal to UPDATED_LONGITUDE
        defaultFieldShouldNotBeFound("longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllFieldsByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where longitude is less than or equal to DEFAULT_LONGITUDE
        defaultFieldShouldBeFound("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the fieldList where longitude is less than or equal to SMALLER_LONGITUDE
        defaultFieldShouldNotBeFound("longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllFieldsByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where longitude is less than DEFAULT_LONGITUDE
        defaultFieldShouldNotBeFound("longitude.lessThan=" + DEFAULT_LONGITUDE);

        // Get all the fieldList where longitude is less than UPDATED_LONGITUDE
        defaultFieldShouldBeFound("longitude.lessThan=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllFieldsByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where longitude is greater than DEFAULT_LONGITUDE
        defaultFieldShouldNotBeFound("longitude.greaterThan=" + DEFAULT_LONGITUDE);

        // Get all the fieldList where longitude is greater than SMALLER_LONGITUDE
        defaultFieldShouldBeFound("longitude.greaterThan=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllFieldsBySeasonIsEqualToSomething() throws Exception {
        Season season;
        if (TestUtil.findAll(em, Season.class).isEmpty()) {
            fieldRepository.saveAndFlush(field);
            season = SeasonResourceIT.createEntity(em);
        } else {
            season = TestUtil.findAll(em, Season.class).get(0);
        }
        em.persist(season);
        em.flush();
        field.addSeason(season);
        fieldRepository.saveAndFlush(field);
        Long seasonId = season.getId();

        // Get all the fieldList where season equals to seasonId
        defaultFieldShouldBeFound("seasonId.equals=" + seasonId);

        // Get all the fieldList where season equals to (seasonId + 1)
        defaultFieldShouldNotBeFound("seasonId.equals=" + (seasonId + 1));
    }

    @Test
    @Transactional
    void getAllFieldsByDeviceIsEqualToSomething() throws Exception {
        Device device;
        if (TestUtil.findAll(em, Device.class).isEmpty()) {
            fieldRepository.saveAndFlush(field);
            device = DeviceResourceIT.createEntity(em);
        } else {
            device = TestUtil.findAll(em, Device.class).get(0);
        }
        em.persist(device);
        em.flush();
        field.addDevice(device);
        fieldRepository.saveAndFlush(field);
        Long deviceId = device.getId();

        // Get all the fieldList where device equals to deviceId
        defaultFieldShouldBeFound("deviceId.equals=" + deviceId);

        // Get all the fieldList where device equals to (deviceId + 1)
        defaultFieldShouldNotBeFound("deviceId.equals=" + (deviceId + 1));
    }

    @Test
    @Transactional
    void getAllFieldsBySoilIsEqualToSomething() throws Exception {
        SoilType soil;
        if (TestUtil.findAll(em, SoilType.class).isEmpty()) {
            fieldRepository.saveAndFlush(field);
            soil = SoilTypeResourceIT.createEntity(em);
        } else {
            soil = TestUtil.findAll(em, SoilType.class).get(0);
        }
        em.persist(soil);
        em.flush();
        field.setSoil(soil);
        fieldRepository.saveAndFlush(field);
        Long soilId = soil.getId();

        // Get all the fieldList where soil equals to soilId
        defaultFieldShouldBeFound("soilId.equals=" + soilId);

        // Get all the fieldList where soil equals to (soilId + 1)
        defaultFieldShouldNotBeFound("soilId.equals=" + (soilId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFieldShouldBeFound(String filter) throws Exception {
        restFieldMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(field.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].geoJson").value(hasItem(DEFAULT_GEO_JSON)))
            .andExpect(jsonPath("$.[*].area").value(hasItem(DEFAULT_AREA.doubleValue())))
            .andExpect(jsonPath("$.[*].descriptions").value(hasItem(DEFAULT_DESCRIPTIONS)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())));

        // Check, that the count call also returns 1
        restFieldMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFieldShouldNotBeFound(String filter) throws Exception {
        restFieldMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFieldMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingField() throws Exception {
        // Get the field
        restFieldMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingField() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        int databaseSizeBeforeUpdate = fieldRepository.findAll().size();

        // Update the field
        Field updatedField = fieldRepository.findById(field.getId()).get();
        // Disconnect from session so that the updates on updatedField are not directly saved in db
        em.detach(updatedField);
        updatedField
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .geoJson(UPDATED_GEO_JSON)
            .area(UPDATED_AREA)
            .descriptions(UPDATED_DESCRIPTIONS)
            .enable(UPDATED_ENABLE)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);
        FieldDTO fieldDTO = fieldMapper.toDto(updatedField);

        restFieldMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fieldDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fieldDTO))
            )
            .andExpect(status().isOk());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeUpdate);
        Field testField = fieldList.get(fieldList.size() - 1);
        assertThat(testField.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testField.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testField.getGeoJson()).isEqualTo(UPDATED_GEO_JSON);
        assertThat(testField.getArea()).isEqualTo(UPDATED_AREA);
        assertThat(testField.getDescriptions()).isEqualTo(UPDATED_DESCRIPTIONS);
        assertThat(testField.getEnable()).isEqualTo(UPDATED_ENABLE);
        assertThat(testField.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testField.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void putNonExistingField() throws Exception {
        int databaseSizeBeforeUpdate = fieldRepository.findAll().size();
        field.setId(count.incrementAndGet());

        // Create the Field
        FieldDTO fieldDTO = fieldMapper.toDto(field);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFieldMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fieldDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fieldDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchField() throws Exception {
        int databaseSizeBeforeUpdate = fieldRepository.findAll().size();
        field.setId(count.incrementAndGet());

        // Create the Field
        FieldDTO fieldDTO = fieldMapper.toDto(field);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFieldMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fieldDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamField() throws Exception {
        int databaseSizeBeforeUpdate = fieldRepository.findAll().size();
        field.setId(count.incrementAndGet());

        // Create the Field
        FieldDTO fieldDTO = fieldMapper.toDto(field);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFieldMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fieldDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFieldWithPatch() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        int databaseSizeBeforeUpdate = fieldRepository.findAll().size();

        // Update the field using partial update
        Field partialUpdatedField = new Field();
        partialUpdatedField.setId(field.getId());

        partialUpdatedField.latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);

        restFieldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedField.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedField))
            )
            .andExpect(status().isOk());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeUpdate);
        Field testField = fieldList.get(fieldList.size() - 1);
        assertThat(testField.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testField.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testField.getGeoJson()).isEqualTo(DEFAULT_GEO_JSON);
        assertThat(testField.getArea()).isEqualTo(DEFAULT_AREA);
        assertThat(testField.getDescriptions()).isEqualTo(DEFAULT_DESCRIPTIONS);
        assertThat(testField.getEnable()).isEqualTo(DEFAULT_ENABLE);
        assertThat(testField.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testField.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void fullUpdateFieldWithPatch() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        int databaseSizeBeforeUpdate = fieldRepository.findAll().size();

        // Update the field using partial update
        Field partialUpdatedField = new Field();
        partialUpdatedField.setId(field.getId());

        partialUpdatedField
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .geoJson(UPDATED_GEO_JSON)
            .area(UPDATED_AREA)
            .descriptions(UPDATED_DESCRIPTIONS)
            .enable(UPDATED_ENABLE)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);

        restFieldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedField.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedField))
            )
            .andExpect(status().isOk());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeUpdate);
        Field testField = fieldList.get(fieldList.size() - 1);
        assertThat(testField.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testField.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testField.getGeoJson()).isEqualTo(UPDATED_GEO_JSON);
        assertThat(testField.getArea()).isEqualTo(UPDATED_AREA);
        assertThat(testField.getDescriptions()).isEqualTo(UPDATED_DESCRIPTIONS);
        assertThat(testField.getEnable()).isEqualTo(UPDATED_ENABLE);
        assertThat(testField.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testField.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void patchNonExistingField() throws Exception {
        int databaseSizeBeforeUpdate = fieldRepository.findAll().size();
        field.setId(count.incrementAndGet());

        // Create the Field
        FieldDTO fieldDTO = fieldMapper.toDto(field);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFieldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fieldDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fieldDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchField() throws Exception {
        int databaseSizeBeforeUpdate = fieldRepository.findAll().size();
        field.setId(count.incrementAndGet());

        // Create the Field
        FieldDTO fieldDTO = fieldMapper.toDto(field);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFieldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fieldDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamField() throws Exception {
        int databaseSizeBeforeUpdate = fieldRepository.findAll().size();
        field.setId(count.incrementAndGet());

        // Create the Field
        FieldDTO fieldDTO = fieldMapper.toDto(field);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFieldMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(fieldDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteField() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        int databaseSizeBeforeDelete = fieldRepository.findAll().size();

        // Delete the field
        restFieldMockMvc
            .perform(delete(ENTITY_API_URL_ID, field.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
