package com.lnduy.agriculture.web.rest;

import static com.lnduy.agriculture.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Employee;
import com.lnduy.agriculture.domain.Fertilizers;
import com.lnduy.agriculture.domain.ProtectionProduct;
import com.lnduy.agriculture.domain.Season;
import com.lnduy.agriculture.domain.Supplies;
import com.lnduy.agriculture.domain.Task;
import com.lnduy.agriculture.repository.TaskRepository;
import com.lnduy.agriculture.service.TaskService;
import com.lnduy.agriculture.service.criteria.TaskCriteria;
import com.lnduy.agriculture.service.dto.TaskDTO;
import com.lnduy.agriculture.service.mapper.TaskMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TaskResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TaskResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_ENABLE = 1;
    private static final Integer UPDATED_ENABLE = 2;
    private static final Integer SMALLER_ENABLE = 1 - 1;

    private static final ZonedDateTime DEFAULT_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TaskRepository taskRepository;

    @Mock
    private TaskRepository taskRepositoryMock;

    @Autowired
    private TaskMapper taskMapper;

    @Mock
    private TaskService taskServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskMockMvc;

    private Task task;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createEntity(EntityManager em) {
        Task task = new Task()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .enable(DEFAULT_ENABLE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
        return task;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createUpdatedEntity(EntityManager em) {
        Task task = new Task()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .enable(UPDATED_ENABLE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        return task;
    }

    @BeforeEach
    public void initTest() {
        task = createEntity(em);
    }

    @Test
    @Transactional
    void createTask() throws Exception {
        int databaseSizeBeforeCreate = taskRepository.findAll().size();
        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);
        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isCreated());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate + 1);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTask.getEnable()).isEqualTo(DEFAULT_ENABLE);
        assertThat(testTask.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testTask.getEndDate()).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void createTaskWithExistingId() throws Exception {
        // Create the Task with an existing ID
        task.setId(1L);
        TaskDTO taskDTO = taskMapper.toDto(task);

        int databaseSizeBeforeCreate = taskRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTasks() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTasksWithEagerRelationshipsIsEnabled() throws Exception {
        when(taskServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTaskMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(taskServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTasksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(taskServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTaskMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(taskRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get the task
        restTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, task.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(task.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.enable").value(DEFAULT_ENABLE))
            .andExpect(jsonPath("$.startDate").value(sameInstant(DEFAULT_START_DATE)))
            .andExpect(jsonPath("$.endDate").value(sameInstant(DEFAULT_END_DATE)));
    }

    @Test
    @Transactional
    void getTasksByIdFiltering() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        Long id = task.getId();

        defaultTaskShouldBeFound("id.equals=" + id);
        defaultTaskShouldNotBeFound("id.notEquals=" + id);

        defaultTaskShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.greaterThan=" + id);

        defaultTaskShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTasksByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where title equals to DEFAULT_TITLE
        defaultTaskShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the taskList where title equals to UPDATED_TITLE
        defaultTaskShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllTasksByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultTaskShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the taskList where title equals to UPDATED_TITLE
        defaultTaskShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllTasksByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where title is not null
        defaultTaskShouldBeFound("title.specified=true");

        // Get all the taskList where title is null
        defaultTaskShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByTitleContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where title contains DEFAULT_TITLE
        defaultTaskShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the taskList where title contains UPDATED_TITLE
        defaultTaskShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllTasksByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where title does not contain DEFAULT_TITLE
        defaultTaskShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the taskList where title does not contain UPDATED_TITLE
        defaultTaskShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description equals to DEFAULT_DESCRIPTION
        defaultTaskShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description equals to UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTaskShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the taskList where description equals to UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description is not null
        defaultTaskShouldBeFound("description.specified=true");

        // Get all the taskList where description is null
        defaultTaskShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description contains DEFAULT_DESCRIPTION
        defaultTaskShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description contains UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description does not contain DEFAULT_DESCRIPTION
        defaultTaskShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description does not contain UPDATED_DESCRIPTION
        defaultTaskShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByEnableIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where enable equals to DEFAULT_ENABLE
        defaultTaskShouldBeFound("enable.equals=" + DEFAULT_ENABLE);

        // Get all the taskList where enable equals to UPDATED_ENABLE
        defaultTaskShouldNotBeFound("enable.equals=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllTasksByEnableIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where enable in DEFAULT_ENABLE or UPDATED_ENABLE
        defaultTaskShouldBeFound("enable.in=" + DEFAULT_ENABLE + "," + UPDATED_ENABLE);

        // Get all the taskList where enable equals to UPDATED_ENABLE
        defaultTaskShouldNotBeFound("enable.in=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllTasksByEnableIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where enable is not null
        defaultTaskShouldBeFound("enable.specified=true");

        // Get all the taskList where enable is null
        defaultTaskShouldNotBeFound("enable.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByEnableIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where enable is greater than or equal to DEFAULT_ENABLE
        defaultTaskShouldBeFound("enable.greaterThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the taskList where enable is greater than or equal to UPDATED_ENABLE
        defaultTaskShouldNotBeFound("enable.greaterThanOrEqual=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllTasksByEnableIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where enable is less than or equal to DEFAULT_ENABLE
        defaultTaskShouldBeFound("enable.lessThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the taskList where enable is less than or equal to SMALLER_ENABLE
        defaultTaskShouldNotBeFound("enable.lessThanOrEqual=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllTasksByEnableIsLessThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where enable is less than DEFAULT_ENABLE
        defaultTaskShouldNotBeFound("enable.lessThan=" + DEFAULT_ENABLE);

        // Get all the taskList where enable is less than UPDATED_ENABLE
        defaultTaskShouldBeFound("enable.lessThan=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllTasksByEnableIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where enable is greater than DEFAULT_ENABLE
        defaultTaskShouldNotBeFound("enable.greaterThan=" + DEFAULT_ENABLE);

        // Get all the taskList where enable is greater than SMALLER_ENABLE
        defaultTaskShouldBeFound("enable.greaterThan=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllTasksByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate equals to DEFAULT_START_DATE
        defaultTaskShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the taskList where startDate equals to UPDATED_START_DATE
        defaultTaskShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultTaskShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the taskList where startDate equals to UPDATED_START_DATE
        defaultTaskShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate is not null
        defaultTaskShouldBeFound("startDate.specified=true");

        // Get all the taskList where startDate is null
        defaultTaskShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate is greater than or equal to DEFAULT_START_DATE
        defaultTaskShouldBeFound("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the taskList where startDate is greater than or equal to UPDATED_START_DATE
        defaultTaskShouldNotBeFound("startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate is less than or equal to DEFAULT_START_DATE
        defaultTaskShouldBeFound("startDate.lessThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the taskList where startDate is less than or equal to SMALLER_START_DATE
        defaultTaskShouldNotBeFound("startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate is less than DEFAULT_START_DATE
        defaultTaskShouldNotBeFound("startDate.lessThan=" + DEFAULT_START_DATE);

        // Get all the taskList where startDate is less than UPDATED_START_DATE
        defaultTaskShouldBeFound("startDate.lessThan=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate is greater than DEFAULT_START_DATE
        defaultTaskShouldNotBeFound("startDate.greaterThan=" + DEFAULT_START_DATE);

        // Get all the taskList where startDate is greater than SMALLER_START_DATE
        defaultTaskShouldBeFound("startDate.greaterThan=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate equals to DEFAULT_END_DATE
        defaultTaskShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the taskList where endDate equals to UPDATED_END_DATE
        defaultTaskShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultTaskShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the taskList where endDate equals to UPDATED_END_DATE
        defaultTaskShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate is not null
        defaultTaskShouldBeFound("endDate.specified=true");

        // Get all the taskList where endDate is null
        defaultTaskShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate is greater than or equal to DEFAULT_END_DATE
        defaultTaskShouldBeFound("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the taskList where endDate is greater than or equal to UPDATED_END_DATE
        defaultTaskShouldNotBeFound("endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByEndDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate is less than or equal to DEFAULT_END_DATE
        defaultTaskShouldBeFound("endDate.lessThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the taskList where endDate is less than or equal to SMALLER_END_DATE
        defaultTaskShouldNotBeFound("endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate is less than DEFAULT_END_DATE
        defaultTaskShouldNotBeFound("endDate.lessThan=" + DEFAULT_END_DATE);

        // Get all the taskList where endDate is less than UPDATED_END_DATE
        defaultTaskShouldBeFound("endDate.lessThan=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByEndDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate is greater than DEFAULT_END_DATE
        defaultTaskShouldNotBeFound("endDate.greaterThan=" + DEFAULT_END_DATE);

        // Get all the taskList where endDate is greater than SMALLER_END_DATE
        defaultTaskShouldBeFound("endDate.greaterThan=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllTasksBySeasonIsEqualToSomething() throws Exception {
        Season season;
        if (TestUtil.findAll(em, Season.class).isEmpty()) {
            taskRepository.saveAndFlush(task);
            season = SeasonResourceIT.createEntity(em);
        } else {
            season = TestUtil.findAll(em, Season.class).get(0);
        }
        em.persist(season);
        em.flush();
        task.setSeason(season);
        taskRepository.saveAndFlush(task);
        Long seasonId = season.getId();

        // Get all the taskList where season equals to seasonId
        defaultTaskShouldBeFound("seasonId.equals=" + seasonId);

        // Get all the taskList where season equals to (seasonId + 1)
        defaultTaskShouldNotBeFound("seasonId.equals=" + (seasonId + 1));
    }

    @Test
    @Transactional
    void getAllTasksByEmployeeIsEqualToSomething() throws Exception {
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            taskRepository.saveAndFlush(task);
            employee = EmployeeResourceIT.createEntity(em);
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(employee);
        em.flush();
        task.addEmployee(employee);
        taskRepository.saveAndFlush(task);
        Long employeeId = employee.getId();

        // Get all the taskList where employee equals to employeeId
        defaultTaskShouldBeFound("employeeId.equals=" + employeeId);

        // Get all the taskList where employee equals to (employeeId + 1)
        defaultTaskShouldNotBeFound("employeeId.equals=" + (employeeId + 1));
    }

    @Test
    @Transactional
    void getAllTasksBySuppliesIsEqualToSomething() throws Exception {
        Supplies supplies;
        if (TestUtil.findAll(em, Supplies.class).isEmpty()) {
            taskRepository.saveAndFlush(task);
            supplies = SuppliesResourceIT.createEntity(em);
        } else {
            supplies = TestUtil.findAll(em, Supplies.class).get(0);
        }
        em.persist(supplies);
        em.flush();
        task.addSupplies(supplies);
        taskRepository.saveAndFlush(task);
        Long suppliesId = supplies.getId();

        // Get all the taskList where supplies equals to suppliesId
        defaultTaskShouldBeFound("suppliesId.equals=" + suppliesId);

        // Get all the taskList where supplies equals to (suppliesId + 1)
        defaultTaskShouldNotBeFound("suppliesId.equals=" + (suppliesId + 1));
    }

    @Test
    @Transactional
    void getAllTasksByProtectionproductIsEqualToSomething() throws Exception {
        ProtectionProduct protectionproduct;
        if (TestUtil.findAll(em, ProtectionProduct.class).isEmpty()) {
            taskRepository.saveAndFlush(task);
            protectionproduct = ProtectionProductResourceIT.createEntity(em);
        } else {
            protectionproduct = TestUtil.findAll(em, ProtectionProduct.class).get(0);
        }
        em.persist(protectionproduct);
        em.flush();
        task.addProtectionproduct(protectionproduct);
        taskRepository.saveAndFlush(task);
        Long protectionproductId = protectionproduct.getId();

        // Get all the taskList where protectionproduct equals to protectionproductId
        defaultTaskShouldBeFound("protectionproductId.equals=" + protectionproductId);

        // Get all the taskList where protectionproduct equals to (protectionproductId + 1)
        defaultTaskShouldNotBeFound("protectionproductId.equals=" + (protectionproductId + 1));
    }

    @Test
    @Transactional
    void getAllTasksByFertilizersIsEqualToSomething() throws Exception {
        Fertilizers fertilizers;
        if (TestUtil.findAll(em, Fertilizers.class).isEmpty()) {
            taskRepository.saveAndFlush(task);
            fertilizers = FertilizersResourceIT.createEntity(em);
        } else {
            fertilizers = TestUtil.findAll(em, Fertilizers.class).get(0);
        }
        em.persist(fertilizers);
        em.flush();
        task.addFertilizers(fertilizers);
        taskRepository.saveAndFlush(task);
        Long fertilizersId = fertilizers.getId();

        // Get all the taskList where fertilizers equals to fertilizersId
        defaultTaskShouldBeFound("fertilizersId.equals=" + fertilizersId);

        // Get all the taskList where fertilizers equals to (fertilizersId + 1)
        defaultTaskShouldNotBeFound("fertilizersId.equals=" + (fertilizersId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskShouldBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))));

        // Check, that the count call also returns 1
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskShouldNotBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTask() throws Exception {
        // Get the task
        restTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task
        Task updatedTask = taskRepository.findById(task.getId()).get();
        // Disconnect from session so that the updates on updatedTask are not directly saved in db
        em.detach(updatedTask);
        updatedTask
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .enable(UPDATED_ENABLE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        TaskDTO taskDTO = taskMapper.toDto(updatedTask);

        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getEnable()).isEqualTo(UPDATED_ENABLE);
        assertThat(testTask.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTask.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void putNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask.description(UPDATED_DESCRIPTION).startDate(UPDATED_START_DATE);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getEnable()).isEqualTo(DEFAULT_ENABLE);
        assertThat(testTask.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTask.getEndDate()).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void fullUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .enable(UPDATED_ENABLE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getEnable()).isEqualTo(UPDATED_ENABLE);
        assertThat(testTask.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTask.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeDelete = taskRepository.findAll().size();

        // Delete the task
        restTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, task.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
