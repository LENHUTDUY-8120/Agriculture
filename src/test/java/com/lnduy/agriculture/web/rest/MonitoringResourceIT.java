package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Device;
import com.lnduy.agriculture.domain.Field;
import com.lnduy.agriculture.domain.Monitoring;
import com.lnduy.agriculture.repository.MonitoringRepository;
import com.lnduy.agriculture.service.criteria.MonitoringCriteria;
import com.lnduy.agriculture.service.dto.MonitoringDTO;
import com.lnduy.agriculture.service.mapper.MonitoringMapper;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link MonitoringResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MonitoringResourceIT {

    private static final String DEFAULT_DATA_JSON = "AAAAAAAAAA";
    private static final String UPDATED_DATA_JSON = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATED_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_AT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATED_AT = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/monitorings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MonitoringRepository monitoringRepository;

    @Autowired
    private MonitoringMapper monitoringMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMonitoringMockMvc;

    private Monitoring monitoring;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Monitoring createEntity(EntityManager em) {
        Monitoring monitoring = new Monitoring().dataJson(DEFAULT_DATA_JSON).createdAt(DEFAULT_CREATED_AT);
        return monitoring;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Monitoring createUpdatedEntity(EntityManager em) {
        Monitoring monitoring = new Monitoring().dataJson(UPDATED_DATA_JSON).createdAt(UPDATED_CREATED_AT);
        return monitoring;
    }

    @BeforeEach
    public void initTest() {
        monitoring = createEntity(em);
    }

    @Test
    @Transactional
    void createMonitoring() throws Exception {
        int databaseSizeBeforeCreate = monitoringRepository.findAll().size();
        // Create the Monitoring
        MonitoringDTO monitoringDTO = monitoringMapper.toDto(monitoring);
        restMonitoringMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(monitoringDTO)))
            .andExpect(status().isCreated());

        // Validate the Monitoring in the database
        List<Monitoring> monitoringList = monitoringRepository.findAll();
        assertThat(monitoringList).hasSize(databaseSizeBeforeCreate + 1);
        Monitoring testMonitoring = monitoringList.get(monitoringList.size() - 1);
        assertThat(testMonitoring.getDataJson()).isEqualTo(DEFAULT_DATA_JSON);
        assertThat(testMonitoring.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createMonitoringWithExistingId() throws Exception {
        // Create the Monitoring with an existing ID
        monitoring.setId(1L);
        MonitoringDTO monitoringDTO = monitoringMapper.toDto(monitoring);

        int databaseSizeBeforeCreate = monitoringRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMonitoringMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(monitoringDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Monitoring in the database
        List<Monitoring> monitoringList = monitoringRepository.findAll();
        assertThat(monitoringList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMonitorings() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get all the monitoringList
        restMonitoringMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(monitoring.getId().intValue())))
            .andExpect(jsonPath("$.[*].dataJson").value(hasItem(DEFAULT_DATA_JSON)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getMonitoring() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get the monitoring
        restMonitoringMockMvc
            .perform(get(ENTITY_API_URL_ID, monitoring.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(monitoring.getId().intValue()))
            .andExpect(jsonPath("$.dataJson").value(DEFAULT_DATA_JSON))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getMonitoringsByIdFiltering() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        Long id = monitoring.getId();

        defaultMonitoringShouldBeFound("id.equals=" + id);
        defaultMonitoringShouldNotBeFound("id.notEquals=" + id);

        defaultMonitoringShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMonitoringShouldNotBeFound("id.greaterThan=" + id);

        defaultMonitoringShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMonitoringShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMonitoringsByDataJsonIsEqualToSomething() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get all the monitoringList where dataJson equals to DEFAULT_DATA_JSON
        defaultMonitoringShouldBeFound("dataJson.equals=" + DEFAULT_DATA_JSON);

        // Get all the monitoringList where dataJson equals to UPDATED_DATA_JSON
        defaultMonitoringShouldNotBeFound("dataJson.equals=" + UPDATED_DATA_JSON);
    }

    @Test
    @Transactional
    void getAllMonitoringsByDataJsonIsInShouldWork() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get all the monitoringList where dataJson in DEFAULT_DATA_JSON or UPDATED_DATA_JSON
        defaultMonitoringShouldBeFound("dataJson.in=" + DEFAULT_DATA_JSON + "," + UPDATED_DATA_JSON);

        // Get all the monitoringList where dataJson equals to UPDATED_DATA_JSON
        defaultMonitoringShouldNotBeFound("dataJson.in=" + UPDATED_DATA_JSON);
    }

    @Test
    @Transactional
    void getAllMonitoringsByDataJsonIsNullOrNotNull() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get all the monitoringList where dataJson is not null
        defaultMonitoringShouldBeFound("dataJson.specified=true");

        // Get all the monitoringList where dataJson is null
        defaultMonitoringShouldNotBeFound("dataJson.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoringsByDataJsonContainsSomething() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get all the monitoringList where dataJson contains DEFAULT_DATA_JSON
        defaultMonitoringShouldBeFound("dataJson.contains=" + DEFAULT_DATA_JSON);

        // Get all the monitoringList where dataJson contains UPDATED_DATA_JSON
        defaultMonitoringShouldNotBeFound("dataJson.contains=" + UPDATED_DATA_JSON);
    }

    @Test
    @Transactional
    void getAllMonitoringsByDataJsonNotContainsSomething() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get all the monitoringList where dataJson does not contain DEFAULT_DATA_JSON
        defaultMonitoringShouldNotBeFound("dataJson.doesNotContain=" + DEFAULT_DATA_JSON);

        // Get all the monitoringList where dataJson does not contain UPDATED_DATA_JSON
        defaultMonitoringShouldBeFound("dataJson.doesNotContain=" + UPDATED_DATA_JSON);
    }

    @Test
    @Transactional
    void getAllMonitoringsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get all the monitoringList where createdAt equals to DEFAULT_CREATED_AT
        defaultMonitoringShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the monitoringList where createdAt equals to UPDATED_CREATED_AT
        defaultMonitoringShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllMonitoringsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get all the monitoringList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultMonitoringShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the monitoringList where createdAt equals to UPDATED_CREATED_AT
        defaultMonitoringShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllMonitoringsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get all the monitoringList where createdAt is not null
        defaultMonitoringShouldBeFound("createdAt.specified=true");

        // Get all the monitoringList where createdAt is null
        defaultMonitoringShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoringsByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get all the monitoringList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultMonitoringShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the monitoringList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultMonitoringShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllMonitoringsByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get all the monitoringList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultMonitoringShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the monitoringList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultMonitoringShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllMonitoringsByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get all the monitoringList where createdAt is less than DEFAULT_CREATED_AT
        defaultMonitoringShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the monitoringList where createdAt is less than UPDATED_CREATED_AT
        defaultMonitoringShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllMonitoringsByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        // Get all the monitoringList where createdAt is greater than DEFAULT_CREATED_AT
        defaultMonitoringShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the monitoringList where createdAt is greater than SMALLER_CREATED_AT
        defaultMonitoringShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllMonitoringsByFieldIsEqualToSomething() throws Exception {
        Field field;
        if (TestUtil.findAll(em, Field.class).isEmpty()) {
            monitoringRepository.saveAndFlush(monitoring);
            field = FieldResourceIT.createEntity(em);
        } else {
            field = TestUtil.findAll(em, Field.class).get(0);
        }
        em.persist(field);
        em.flush();
        monitoring.setField(field);
        monitoringRepository.saveAndFlush(monitoring);
        Long fieldId = field.getId();

        // Get all the monitoringList where field equals to fieldId
        defaultMonitoringShouldBeFound("fieldId.equals=" + fieldId);

        // Get all the monitoringList where field equals to (fieldId + 1)
        defaultMonitoringShouldNotBeFound("fieldId.equals=" + (fieldId + 1));
    }

    @Test
    @Transactional
    void getAllMonitoringsByDeviceIsEqualToSomething() throws Exception {
        Device device;
        if (TestUtil.findAll(em, Device.class).isEmpty()) {
            monitoringRepository.saveAndFlush(monitoring);
            device = DeviceResourceIT.createEntity(em);
        } else {
            device = TestUtil.findAll(em, Device.class).get(0);
        }
        em.persist(device);
        em.flush();
        monitoring.setDevice(device);
        monitoringRepository.saveAndFlush(monitoring);
        Long deviceId = device.getId();

        // Get all the monitoringList where device equals to deviceId
        defaultMonitoringShouldBeFound("deviceId.equals=" + deviceId);

        // Get all the monitoringList where device equals to (deviceId + 1)
        defaultMonitoringShouldNotBeFound("deviceId.equals=" + (deviceId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMonitoringShouldBeFound(String filter) throws Exception {
        restMonitoringMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(monitoring.getId().intValue())))
            .andExpect(jsonPath("$.[*].dataJson").value(hasItem(DEFAULT_DATA_JSON)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restMonitoringMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMonitoringShouldNotBeFound(String filter) throws Exception {
        restMonitoringMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMonitoringMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMonitoring() throws Exception {
        // Get the monitoring
        restMonitoringMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMonitoring() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        int databaseSizeBeforeUpdate = monitoringRepository.findAll().size();

        // Update the monitoring
        Monitoring updatedMonitoring = monitoringRepository.findById(monitoring.getId()).get();
        // Disconnect from session so that the updates on updatedMonitoring are not directly saved in db
        em.detach(updatedMonitoring);
        updatedMonitoring.dataJson(UPDATED_DATA_JSON).createdAt(UPDATED_CREATED_AT);
        MonitoringDTO monitoringDTO = monitoringMapper.toDto(updatedMonitoring);

        restMonitoringMockMvc
            .perform(
                put(ENTITY_API_URL_ID, monitoringDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(monitoringDTO))
            )
            .andExpect(status().isOk());

        // Validate the Monitoring in the database
        List<Monitoring> monitoringList = monitoringRepository.findAll();
        assertThat(monitoringList).hasSize(databaseSizeBeforeUpdate);
        Monitoring testMonitoring = monitoringList.get(monitoringList.size() - 1);
        assertThat(testMonitoring.getDataJson()).isEqualTo(UPDATED_DATA_JSON);
        assertThat(testMonitoring.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingMonitoring() throws Exception {
        int databaseSizeBeforeUpdate = monitoringRepository.findAll().size();
        monitoring.setId(count.incrementAndGet());

        // Create the Monitoring
        MonitoringDTO monitoringDTO = monitoringMapper.toDto(monitoring);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMonitoringMockMvc
            .perform(
                put(ENTITY_API_URL_ID, monitoringDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(monitoringDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Monitoring in the database
        List<Monitoring> monitoringList = monitoringRepository.findAll();
        assertThat(monitoringList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMonitoring() throws Exception {
        int databaseSizeBeforeUpdate = monitoringRepository.findAll().size();
        monitoring.setId(count.incrementAndGet());

        // Create the Monitoring
        MonitoringDTO monitoringDTO = monitoringMapper.toDto(monitoring);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMonitoringMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(monitoringDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Monitoring in the database
        List<Monitoring> monitoringList = monitoringRepository.findAll();
        assertThat(monitoringList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMonitoring() throws Exception {
        int databaseSizeBeforeUpdate = monitoringRepository.findAll().size();
        monitoring.setId(count.incrementAndGet());

        // Create the Monitoring
        MonitoringDTO monitoringDTO = monitoringMapper.toDto(monitoring);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMonitoringMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(monitoringDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Monitoring in the database
        List<Monitoring> monitoringList = monitoringRepository.findAll();
        assertThat(monitoringList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMonitoringWithPatch() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        int databaseSizeBeforeUpdate = monitoringRepository.findAll().size();

        // Update the monitoring using partial update
        Monitoring partialUpdatedMonitoring = new Monitoring();
        partialUpdatedMonitoring.setId(monitoring.getId());

        partialUpdatedMonitoring.dataJson(UPDATED_DATA_JSON).createdAt(UPDATED_CREATED_AT);

        restMonitoringMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMonitoring.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMonitoring))
            )
            .andExpect(status().isOk());

        // Validate the Monitoring in the database
        List<Monitoring> monitoringList = monitoringRepository.findAll();
        assertThat(monitoringList).hasSize(databaseSizeBeforeUpdate);
        Monitoring testMonitoring = monitoringList.get(monitoringList.size() - 1);
        assertThat(testMonitoring.getDataJson()).isEqualTo(UPDATED_DATA_JSON);
        assertThat(testMonitoring.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateMonitoringWithPatch() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        int databaseSizeBeforeUpdate = monitoringRepository.findAll().size();

        // Update the monitoring using partial update
        Monitoring partialUpdatedMonitoring = new Monitoring();
        partialUpdatedMonitoring.setId(monitoring.getId());

        partialUpdatedMonitoring.dataJson(UPDATED_DATA_JSON).createdAt(UPDATED_CREATED_AT);

        restMonitoringMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMonitoring.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMonitoring))
            )
            .andExpect(status().isOk());

        // Validate the Monitoring in the database
        List<Monitoring> monitoringList = monitoringRepository.findAll();
        assertThat(monitoringList).hasSize(databaseSizeBeforeUpdate);
        Monitoring testMonitoring = monitoringList.get(monitoringList.size() - 1);
        assertThat(testMonitoring.getDataJson()).isEqualTo(UPDATED_DATA_JSON);
        assertThat(testMonitoring.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingMonitoring() throws Exception {
        int databaseSizeBeforeUpdate = monitoringRepository.findAll().size();
        monitoring.setId(count.incrementAndGet());

        // Create the Monitoring
        MonitoringDTO monitoringDTO = monitoringMapper.toDto(monitoring);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMonitoringMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, monitoringDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(monitoringDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Monitoring in the database
        List<Monitoring> monitoringList = monitoringRepository.findAll();
        assertThat(monitoringList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMonitoring() throws Exception {
        int databaseSizeBeforeUpdate = monitoringRepository.findAll().size();
        monitoring.setId(count.incrementAndGet());

        // Create the Monitoring
        MonitoringDTO monitoringDTO = monitoringMapper.toDto(monitoring);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMonitoringMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(monitoringDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Monitoring in the database
        List<Monitoring> monitoringList = monitoringRepository.findAll();
        assertThat(monitoringList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMonitoring() throws Exception {
        int databaseSizeBeforeUpdate = monitoringRepository.findAll().size();
        monitoring.setId(count.incrementAndGet());

        // Create the Monitoring
        MonitoringDTO monitoringDTO = monitoringMapper.toDto(monitoring);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMonitoringMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(monitoringDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Monitoring in the database
        List<Monitoring> monitoringList = monitoringRepository.findAll();
        assertThat(monitoringList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMonitoring() throws Exception {
        // Initialize the database
        monitoringRepository.saveAndFlush(monitoring);

        int databaseSizeBeforeDelete = monitoringRepository.findAll().size();

        // Delete the monitoring
        restMonitoringMockMvc
            .perform(delete(ENTITY_API_URL_ID, monitoring.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Monitoring> monitoringList = monitoringRepository.findAll();
        assertThat(monitoringList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
