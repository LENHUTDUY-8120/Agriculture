package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.EventCategory;
import com.lnduy.agriculture.repository.EventCategoryRepository;
import com.lnduy.agriculture.service.dto.EventCategoryDTO;
import com.lnduy.agriculture.service.mapper.EventCategoryMapper;
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
 * Integration tests for the {@link EventCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EventCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/event-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EventCategoryRepository eventCategoryRepository;

    @Autowired
    private EventCategoryMapper eventCategoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventCategoryMockMvc;

    private EventCategory eventCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventCategory createEntity(EntityManager em) {
        EventCategory eventCategory = new EventCategory().name(DEFAULT_NAME);
        return eventCategory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventCategory createUpdatedEntity(EntityManager em) {
        EventCategory eventCategory = new EventCategory().name(UPDATED_NAME);
        return eventCategory;
    }

    @BeforeEach
    public void initTest() {
        eventCategory = createEntity(em);
    }

    @Test
    @Transactional
    void createEventCategory() throws Exception {
        int databaseSizeBeforeCreate = eventCategoryRepository.findAll().size();
        // Create the EventCategory
        EventCategoryDTO eventCategoryDTO = eventCategoryMapper.toDto(eventCategory);
        restEventCategoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventCategoryDTO))
            )
            .andExpect(status().isCreated());

        // Validate the EventCategory in the database
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAll();
        assertThat(eventCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        EventCategory testEventCategory = eventCategoryList.get(eventCategoryList.size() - 1);
        assertThat(testEventCategory.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createEventCategoryWithExistingId() throws Exception {
        // Create the EventCategory with an existing ID
        eventCategory.setId(1L);
        EventCategoryDTO eventCategoryDTO = eventCategoryMapper.toDto(eventCategory);

        int databaseSizeBeforeCreate = eventCategoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventCategoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventCategory in the database
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAll();
        assertThat(eventCategoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEventCategories() throws Exception {
        // Initialize the database
        eventCategoryRepository.saveAndFlush(eventCategory);

        // Get all the eventCategoryList
        restEventCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getEventCategory() throws Exception {
        // Initialize the database
        eventCategoryRepository.saveAndFlush(eventCategory);

        // Get the eventCategory
        restEventCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, eventCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(eventCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingEventCategory() throws Exception {
        // Get the eventCategory
        restEventCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEventCategory() throws Exception {
        // Initialize the database
        eventCategoryRepository.saveAndFlush(eventCategory);

        int databaseSizeBeforeUpdate = eventCategoryRepository.findAll().size();

        // Update the eventCategory
        EventCategory updatedEventCategory = eventCategoryRepository.findById(eventCategory.getId()).get();
        // Disconnect from session so that the updates on updatedEventCategory are not directly saved in db
        em.detach(updatedEventCategory);
        updatedEventCategory.name(UPDATED_NAME);
        EventCategoryDTO eventCategoryDTO = eventCategoryMapper.toDto(updatedEventCategory);

        restEventCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventCategoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the EventCategory in the database
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAll();
        assertThat(eventCategoryList).hasSize(databaseSizeBeforeUpdate);
        EventCategory testEventCategory = eventCategoryList.get(eventCategoryList.size() - 1);
        assertThat(testEventCategory.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingEventCategory() throws Exception {
        int databaseSizeBeforeUpdate = eventCategoryRepository.findAll().size();
        eventCategory.setId(count.incrementAndGet());

        // Create the EventCategory
        EventCategoryDTO eventCategoryDTO = eventCategoryMapper.toDto(eventCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventCategory in the database
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAll();
        assertThat(eventCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEventCategory() throws Exception {
        int databaseSizeBeforeUpdate = eventCategoryRepository.findAll().size();
        eventCategory.setId(count.incrementAndGet());

        // Create the EventCategory
        EventCategoryDTO eventCategoryDTO = eventCategoryMapper.toDto(eventCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventCategory in the database
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAll();
        assertThat(eventCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEventCategory() throws Exception {
        int databaseSizeBeforeUpdate = eventCategoryRepository.findAll().size();
        eventCategory.setId(count.incrementAndGet());

        // Create the EventCategory
        EventCategoryDTO eventCategoryDTO = eventCategoryMapper.toDto(eventCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventCategoryMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventCategoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventCategory in the database
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAll();
        assertThat(eventCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEventCategoryWithPatch() throws Exception {
        // Initialize the database
        eventCategoryRepository.saveAndFlush(eventCategory);

        int databaseSizeBeforeUpdate = eventCategoryRepository.findAll().size();

        // Update the eventCategory using partial update
        EventCategory partialUpdatedEventCategory = new EventCategory();
        partialUpdatedEventCategory.setId(eventCategory.getId());

        partialUpdatedEventCategory.name(UPDATED_NAME);

        restEventCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEventCategory))
            )
            .andExpect(status().isOk());

        // Validate the EventCategory in the database
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAll();
        assertThat(eventCategoryList).hasSize(databaseSizeBeforeUpdate);
        EventCategory testEventCategory = eventCategoryList.get(eventCategoryList.size() - 1);
        assertThat(testEventCategory.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateEventCategoryWithPatch() throws Exception {
        // Initialize the database
        eventCategoryRepository.saveAndFlush(eventCategory);

        int databaseSizeBeforeUpdate = eventCategoryRepository.findAll().size();

        // Update the eventCategory using partial update
        EventCategory partialUpdatedEventCategory = new EventCategory();
        partialUpdatedEventCategory.setId(eventCategory.getId());

        partialUpdatedEventCategory.name(UPDATED_NAME);

        restEventCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEventCategory))
            )
            .andExpect(status().isOk());

        // Validate the EventCategory in the database
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAll();
        assertThat(eventCategoryList).hasSize(databaseSizeBeforeUpdate);
        EventCategory testEventCategory = eventCategoryList.get(eventCategoryList.size() - 1);
        assertThat(testEventCategory.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingEventCategory() throws Exception {
        int databaseSizeBeforeUpdate = eventCategoryRepository.findAll().size();
        eventCategory.setId(count.incrementAndGet());

        // Create the EventCategory
        EventCategoryDTO eventCategoryDTO = eventCategoryMapper.toDto(eventCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventCategoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eventCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventCategory in the database
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAll();
        assertThat(eventCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEventCategory() throws Exception {
        int databaseSizeBeforeUpdate = eventCategoryRepository.findAll().size();
        eventCategory.setId(count.incrementAndGet());

        // Create the EventCategory
        EventCategoryDTO eventCategoryDTO = eventCategoryMapper.toDto(eventCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eventCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventCategory in the database
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAll();
        assertThat(eventCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEventCategory() throws Exception {
        int databaseSizeBeforeUpdate = eventCategoryRepository.findAll().size();
        eventCategory.setId(count.incrementAndGet());

        // Create the EventCategory
        EventCategoryDTO eventCategoryDTO = eventCategoryMapper.toDto(eventCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eventCategoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventCategory in the database
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAll();
        assertThat(eventCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEventCategory() throws Exception {
        // Initialize the database
        eventCategoryRepository.saveAndFlush(eventCategory);

        int databaseSizeBeforeDelete = eventCategoryRepository.findAll().size();

        // Delete the eventCategory
        restEventCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, eventCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAll();
        assertThat(eventCategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
