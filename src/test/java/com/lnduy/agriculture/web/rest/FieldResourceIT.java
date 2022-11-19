package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Field;
import com.lnduy.agriculture.repository.FieldRepository;
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

    private static final String DEFAULT_DESCRIPTIONS = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTIONS = "BBBBBBBBBB";

    private static final Integer DEFAULT_ENABLE = 1;
    private static final Integer UPDATED_ENABLE = 2;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

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
