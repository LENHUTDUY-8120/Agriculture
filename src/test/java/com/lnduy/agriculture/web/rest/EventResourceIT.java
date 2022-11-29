package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Event;
import com.lnduy.agriculture.domain.EventCategory;
import com.lnduy.agriculture.repository.EventRepository;
import com.lnduy.agriculture.service.criteria.EventCriteria;
import com.lnduy.agriculture.service.dto.EventDTO;
import com.lnduy.agriculture.service.mapper.EventMapper;
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
 * Integration tests for the {@link EventResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EventResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTIONS = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTIONS = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_AT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_AT = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_AT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_AT = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventMockMvc;

    private Event event;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createEntity(EntityManager em) {
        Event event = new Event()
            .title(DEFAULT_TITLE)
            .descriptions(DEFAULT_DESCRIPTIONS)
            .content(DEFAULT_CONTENT)
            .startAt(DEFAULT_START_AT)
            .endAt(DEFAULT_END_AT);
        return event;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createUpdatedEntity(EntityManager em) {
        Event event = new Event()
            .title(UPDATED_TITLE)
            .descriptions(UPDATED_DESCRIPTIONS)
            .content(UPDATED_CONTENT)
            .startAt(UPDATED_START_AT)
            .endAt(UPDATED_END_AT);
        return event;
    }

    @BeforeEach
    public void initTest() {
        event = createEntity(em);
    }

    @Test
    @Transactional
    void createEvent() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();
        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);
        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isCreated());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate + 1);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testEvent.getDescriptions()).isEqualTo(DEFAULT_DESCRIPTIONS);
        assertThat(testEvent.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testEvent.getStartAt()).isEqualTo(DEFAULT_START_AT);
        assertThat(testEvent.getEndAt()).isEqualTo(DEFAULT_END_AT);
    }

    @Test
    @Transactional
    void createEventWithExistingId() throws Exception {
        // Create the Event with an existing ID
        event.setId(1L);
        EventDTO eventDTO = eventMapper.toDto(event);

        int databaseSizeBeforeCreate = eventRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEvents() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].descriptions").value(hasItem(DEFAULT_DESCRIPTIONS)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].startAt").value(hasItem(DEFAULT_START_AT.toString())))
            .andExpect(jsonPath("$.[*].endAt").value(hasItem(DEFAULT_END_AT.toString())));
    }

    @Test
    @Transactional
    void getEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get the event
        restEventMockMvc
            .perform(get(ENTITY_API_URL_ID, event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(event.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.descriptions").value(DEFAULT_DESCRIPTIONS))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.startAt").value(DEFAULT_START_AT.toString()))
            .andExpect(jsonPath("$.endAt").value(DEFAULT_END_AT.toString()));
    }

    @Test
    @Transactional
    void getEventsByIdFiltering() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        Long id = event.getId();

        defaultEventShouldBeFound("id.equals=" + id);
        defaultEventShouldNotBeFound("id.notEquals=" + id);

        defaultEventShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEventShouldNotBeFound("id.greaterThan=" + id);

        defaultEventShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEventShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEventsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where title equals to DEFAULT_TITLE
        defaultEventShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the eventList where title equals to UPDATED_TITLE
        defaultEventShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEventsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultEventShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the eventList where title equals to UPDATED_TITLE
        defaultEventShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEventsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where title is not null
        defaultEventShouldBeFound("title.specified=true");

        // Get all the eventList where title is null
        defaultEventShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByTitleContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where title contains DEFAULT_TITLE
        defaultEventShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the eventList where title contains UPDATED_TITLE
        defaultEventShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEventsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where title does not contain DEFAULT_TITLE
        defaultEventShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the eventList where title does not contain UPDATED_TITLE
        defaultEventShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEventsByDescriptionsIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where descriptions equals to DEFAULT_DESCRIPTIONS
        defaultEventShouldBeFound("descriptions.equals=" + DEFAULT_DESCRIPTIONS);

        // Get all the eventList where descriptions equals to UPDATED_DESCRIPTIONS
        defaultEventShouldNotBeFound("descriptions.equals=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllEventsByDescriptionsIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where descriptions in DEFAULT_DESCRIPTIONS or UPDATED_DESCRIPTIONS
        defaultEventShouldBeFound("descriptions.in=" + DEFAULT_DESCRIPTIONS + "," + UPDATED_DESCRIPTIONS);

        // Get all the eventList where descriptions equals to UPDATED_DESCRIPTIONS
        defaultEventShouldNotBeFound("descriptions.in=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllEventsByDescriptionsIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where descriptions is not null
        defaultEventShouldBeFound("descriptions.specified=true");

        // Get all the eventList where descriptions is null
        defaultEventShouldNotBeFound("descriptions.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByDescriptionsContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where descriptions contains DEFAULT_DESCRIPTIONS
        defaultEventShouldBeFound("descriptions.contains=" + DEFAULT_DESCRIPTIONS);

        // Get all the eventList where descriptions contains UPDATED_DESCRIPTIONS
        defaultEventShouldNotBeFound("descriptions.contains=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllEventsByDescriptionsNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where descriptions does not contain DEFAULT_DESCRIPTIONS
        defaultEventShouldNotBeFound("descriptions.doesNotContain=" + DEFAULT_DESCRIPTIONS);

        // Get all the eventList where descriptions does not contain UPDATED_DESCRIPTIONS
        defaultEventShouldBeFound("descriptions.doesNotContain=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllEventsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where content equals to DEFAULT_CONTENT
        defaultEventShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the eventList where content equals to UPDATED_CONTENT
        defaultEventShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllEventsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultEventShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the eventList where content equals to UPDATED_CONTENT
        defaultEventShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllEventsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where content is not null
        defaultEventShouldBeFound("content.specified=true");

        // Get all the eventList where content is null
        defaultEventShouldNotBeFound("content.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByContentContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where content contains DEFAULT_CONTENT
        defaultEventShouldBeFound("content.contains=" + DEFAULT_CONTENT);

        // Get all the eventList where content contains UPDATED_CONTENT
        defaultEventShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllEventsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where content does not contain DEFAULT_CONTENT
        defaultEventShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

        // Get all the eventList where content does not contain UPDATED_CONTENT
        defaultEventShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllEventsByStartAtIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where startAt equals to DEFAULT_START_AT
        defaultEventShouldBeFound("startAt.equals=" + DEFAULT_START_AT);

        // Get all the eventList where startAt equals to UPDATED_START_AT
        defaultEventShouldNotBeFound("startAt.equals=" + UPDATED_START_AT);
    }

    @Test
    @Transactional
    void getAllEventsByStartAtIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where startAt in DEFAULT_START_AT or UPDATED_START_AT
        defaultEventShouldBeFound("startAt.in=" + DEFAULT_START_AT + "," + UPDATED_START_AT);

        // Get all the eventList where startAt equals to UPDATED_START_AT
        defaultEventShouldNotBeFound("startAt.in=" + UPDATED_START_AT);
    }

    @Test
    @Transactional
    void getAllEventsByStartAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where startAt is not null
        defaultEventShouldBeFound("startAt.specified=true");

        // Get all the eventList where startAt is null
        defaultEventShouldNotBeFound("startAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByStartAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where startAt is greater than or equal to DEFAULT_START_AT
        defaultEventShouldBeFound("startAt.greaterThanOrEqual=" + DEFAULT_START_AT);

        // Get all the eventList where startAt is greater than or equal to UPDATED_START_AT
        defaultEventShouldNotBeFound("startAt.greaterThanOrEqual=" + UPDATED_START_AT);
    }

    @Test
    @Transactional
    void getAllEventsByStartAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where startAt is less than or equal to DEFAULT_START_AT
        defaultEventShouldBeFound("startAt.lessThanOrEqual=" + DEFAULT_START_AT);

        // Get all the eventList where startAt is less than or equal to SMALLER_START_AT
        defaultEventShouldNotBeFound("startAt.lessThanOrEqual=" + SMALLER_START_AT);
    }

    @Test
    @Transactional
    void getAllEventsByStartAtIsLessThanSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where startAt is less than DEFAULT_START_AT
        defaultEventShouldNotBeFound("startAt.lessThan=" + DEFAULT_START_AT);

        // Get all the eventList where startAt is less than UPDATED_START_AT
        defaultEventShouldBeFound("startAt.lessThan=" + UPDATED_START_AT);
    }

    @Test
    @Transactional
    void getAllEventsByStartAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where startAt is greater than DEFAULT_START_AT
        defaultEventShouldNotBeFound("startAt.greaterThan=" + DEFAULT_START_AT);

        // Get all the eventList where startAt is greater than SMALLER_START_AT
        defaultEventShouldBeFound("startAt.greaterThan=" + SMALLER_START_AT);
    }

    @Test
    @Transactional
    void getAllEventsByEndAtIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where endAt equals to DEFAULT_END_AT
        defaultEventShouldBeFound("endAt.equals=" + DEFAULT_END_AT);

        // Get all the eventList where endAt equals to UPDATED_END_AT
        defaultEventShouldNotBeFound("endAt.equals=" + UPDATED_END_AT);
    }

    @Test
    @Transactional
    void getAllEventsByEndAtIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where endAt in DEFAULT_END_AT or UPDATED_END_AT
        defaultEventShouldBeFound("endAt.in=" + DEFAULT_END_AT + "," + UPDATED_END_AT);

        // Get all the eventList where endAt equals to UPDATED_END_AT
        defaultEventShouldNotBeFound("endAt.in=" + UPDATED_END_AT);
    }

    @Test
    @Transactional
    void getAllEventsByEndAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where endAt is not null
        defaultEventShouldBeFound("endAt.specified=true");

        // Get all the eventList where endAt is null
        defaultEventShouldNotBeFound("endAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByEndAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where endAt is greater than or equal to DEFAULT_END_AT
        defaultEventShouldBeFound("endAt.greaterThanOrEqual=" + DEFAULT_END_AT);

        // Get all the eventList where endAt is greater than or equal to UPDATED_END_AT
        defaultEventShouldNotBeFound("endAt.greaterThanOrEqual=" + UPDATED_END_AT);
    }

    @Test
    @Transactional
    void getAllEventsByEndAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where endAt is less than or equal to DEFAULT_END_AT
        defaultEventShouldBeFound("endAt.lessThanOrEqual=" + DEFAULT_END_AT);

        // Get all the eventList where endAt is less than or equal to SMALLER_END_AT
        defaultEventShouldNotBeFound("endAt.lessThanOrEqual=" + SMALLER_END_AT);
    }

    @Test
    @Transactional
    void getAllEventsByEndAtIsLessThanSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where endAt is less than DEFAULT_END_AT
        defaultEventShouldNotBeFound("endAt.lessThan=" + DEFAULT_END_AT);

        // Get all the eventList where endAt is less than UPDATED_END_AT
        defaultEventShouldBeFound("endAt.lessThan=" + UPDATED_END_AT);
    }

    @Test
    @Transactional
    void getAllEventsByEndAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where endAt is greater than DEFAULT_END_AT
        defaultEventShouldNotBeFound("endAt.greaterThan=" + DEFAULT_END_AT);

        // Get all the eventList where endAt is greater than SMALLER_END_AT
        defaultEventShouldBeFound("endAt.greaterThan=" + SMALLER_END_AT);
    }

    @Test
    @Transactional
    void getAllEventsByCategoryIsEqualToSomething() throws Exception {
        EventCategory category;
        if (TestUtil.findAll(em, EventCategory.class).isEmpty()) {
            eventRepository.saveAndFlush(event);
            category = EventCategoryResourceIT.createEntity(em);
        } else {
            category = TestUtil.findAll(em, EventCategory.class).get(0);
        }
        em.persist(category);
        em.flush();
        event.setCategory(category);
        eventRepository.saveAndFlush(event);
        Long categoryId = category.getId();

        // Get all the eventList where category equals to categoryId
        defaultEventShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the eventList where category equals to (categoryId + 1)
        defaultEventShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEventShouldBeFound(String filter) throws Exception {
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].descriptions").value(hasItem(DEFAULT_DESCRIPTIONS)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].startAt").value(hasItem(DEFAULT_START_AT.toString())))
            .andExpect(jsonPath("$.[*].endAt").value(hasItem(DEFAULT_END_AT.toString())));

        // Check, that the count call also returns 1
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEventShouldNotBeFound(String filter) throws Exception {
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEvent() throws Exception {
        // Get the event
        restEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event
        Event updatedEvent = eventRepository.findById(event.getId()).get();
        // Disconnect from session so that the updates on updatedEvent are not directly saved in db
        em.detach(updatedEvent);
        updatedEvent
            .title(UPDATED_TITLE)
            .descriptions(UPDATED_DESCRIPTIONS)
            .content(UPDATED_CONTENT)
            .startAt(UPDATED_START_AT)
            .endAt(UPDATED_END_AT);
        EventDTO eventDTO = eventMapper.toDto(updatedEvent);

        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEvent.getDescriptions()).isEqualTo(UPDATED_DESCRIPTIONS);
        assertThat(testEvent.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testEvent.getStartAt()).isEqualTo(UPDATED_START_AT);
        assertThat(testEvent.getEndAt()).isEqualTo(UPDATED_END_AT);
    }

    @Test
    @Transactional
    void putNonExistingEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        event.setId(count.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        event.setId(count.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        event.setId(count.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEventWithPatch() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event using partial update
        Event partialUpdatedEvent = new Event();
        partialUpdatedEvent.setId(event.getId());

        partialUpdatedEvent.descriptions(UPDATED_DESCRIPTIONS);

        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEvent))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testEvent.getDescriptions()).isEqualTo(UPDATED_DESCRIPTIONS);
        assertThat(testEvent.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testEvent.getStartAt()).isEqualTo(DEFAULT_START_AT);
        assertThat(testEvent.getEndAt()).isEqualTo(DEFAULT_END_AT);
    }

    @Test
    @Transactional
    void fullUpdateEventWithPatch() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event using partial update
        Event partialUpdatedEvent = new Event();
        partialUpdatedEvent.setId(event.getId());

        partialUpdatedEvent
            .title(UPDATED_TITLE)
            .descriptions(UPDATED_DESCRIPTIONS)
            .content(UPDATED_CONTENT)
            .startAt(UPDATED_START_AT)
            .endAt(UPDATED_END_AT);

        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEvent))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEvent.getDescriptions()).isEqualTo(UPDATED_DESCRIPTIONS);
        assertThat(testEvent.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testEvent.getStartAt()).isEqualTo(UPDATED_START_AT);
        assertThat(testEvent.getEndAt()).isEqualTo(UPDATED_END_AT);
    }

    @Test
    @Transactional
    void patchNonExistingEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        event.setId(count.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        event.setId(count.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        event.setId(count.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeDelete = eventRepository.findAll().size();

        // Delete the event
        restEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, event.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
