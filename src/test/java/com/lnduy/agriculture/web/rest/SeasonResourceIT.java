package com.lnduy.agriculture.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Crops;
import com.lnduy.agriculture.domain.Field;
import com.lnduy.agriculture.domain.Season;
import com.lnduy.agriculture.domain.Transaction;
import com.lnduy.agriculture.repository.SeasonRepository;
import com.lnduy.agriculture.service.criteria.SeasonCriteria;
import com.lnduy.agriculture.service.dto.SeasonDTO;
import com.lnduy.agriculture.service.mapper.SeasonMapper;
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
 * Integration tests for the {@link SeasonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SeasonResourceIT {

    private static final String DEFAULT_CROPS = "AAAAAAAAAA";
    private static final String UPDATED_CROPS = "BBBBBBBBBB";

    private static final Double DEFAULT_TOTAL_COST = 1D;
    private static final Double UPDATED_TOTAL_COST = 2D;
    private static final Double SMALLER_TOTAL_COST = 1D - 1D;

    private static final Double DEFAULT_CROP_YIELDS = 1D;
    private static final Double UPDATED_CROP_YIELDS = 2D;
    private static final Double SMALLER_CROP_YIELDS = 1D - 1D;

    private static final Integer DEFAULT_ENABLE = 1;
    private static final Integer UPDATED_ENABLE = 2;
    private static final Integer SMALLER_ENABLE = 1 - 1;

    private static final Float DEFAULT_VOLUME = 1F;
    private static final Float UPDATED_VOLUME = 2F;
    private static final Float SMALLER_VOLUME = 1F - 1F;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final Integer DEFAULT_DONE = 1;
    private static final Integer UPDATED_DONE = 2;
    private static final Integer SMALLER_DONE = 1 - 1;

    private static final LocalDate DEFAULT_START_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_AT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_AT = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_AT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_AT = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/seasons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private SeasonMapper seasonMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSeasonMockMvc;

    private Season season;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Season createEntity(EntityManager em) {
        Season season = new Season()
            .crops(DEFAULT_CROPS)
            .totalCost(DEFAULT_TOTAL_COST)
            .cropYields(DEFAULT_CROP_YIELDS)
            .enable(DEFAULT_ENABLE)
            .volume(DEFAULT_VOLUME)
            .unit(DEFAULT_UNIT)
            .done(DEFAULT_DONE)
            .startAt(DEFAULT_START_AT)
            .endAt(DEFAULT_END_AT);
        return season;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Season createUpdatedEntity(EntityManager em) {
        Season season = new Season()
            .crops(UPDATED_CROPS)
            .totalCost(UPDATED_TOTAL_COST)
            .cropYields(UPDATED_CROP_YIELDS)
            .enable(UPDATED_ENABLE)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .done(UPDATED_DONE)
            .startAt(UPDATED_START_AT)
            .endAt(UPDATED_END_AT);
        return season;
    }

    @BeforeEach
    public void initTest() {
        season = createEntity(em);
    }

    @Test
    @Transactional
    void createSeason() throws Exception {
        int databaseSizeBeforeCreate = seasonRepository.findAll().size();
        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);
        restSeasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(seasonDTO)))
            .andExpect(status().isCreated());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeCreate + 1);
        Season testSeason = seasonList.get(seasonList.size() - 1);
        assertThat(testSeason.getCrops()).isEqualTo(DEFAULT_CROPS);
        assertThat(testSeason.getTotalCost()).isEqualTo(DEFAULT_TOTAL_COST);
        assertThat(testSeason.getCropYields()).isEqualTo(DEFAULT_CROP_YIELDS);
        assertThat(testSeason.getEnable()).isEqualTo(DEFAULT_ENABLE);
        assertThat(testSeason.getVolume()).isEqualTo(DEFAULT_VOLUME);
        assertThat(testSeason.getUnit()).isEqualTo(DEFAULT_UNIT);
        assertThat(testSeason.getDone()).isEqualTo(DEFAULT_DONE);
        assertThat(testSeason.getStartAt()).isEqualTo(DEFAULT_START_AT);
        assertThat(testSeason.getEndAt()).isEqualTo(DEFAULT_END_AT);
    }

    @Test
    @Transactional
    void createSeasonWithExistingId() throws Exception {
        // Create the Season with an existing ID
        season.setId(1L);
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        int databaseSizeBeforeCreate = seasonRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSeasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(seasonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSeasons() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList
        restSeasonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(season.getId().intValue())))
            .andExpect(jsonPath("$.[*].crops").value(hasItem(DEFAULT_CROPS)))
            .andExpect(jsonPath("$.[*].totalCost").value(hasItem(DEFAULT_TOTAL_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].cropYields").value(hasItem(DEFAULT_CROP_YIELDS.doubleValue())))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.doubleValue())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].done").value(hasItem(DEFAULT_DONE)))
            .andExpect(jsonPath("$.[*].startAt").value(hasItem(DEFAULT_START_AT.toString())))
            .andExpect(jsonPath("$.[*].endAt").value(hasItem(DEFAULT_END_AT.toString())));
    }

    @Test
    @Transactional
    void getSeason() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get the season
        restSeasonMockMvc
            .perform(get(ENTITY_API_URL_ID, season.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(season.getId().intValue()))
            .andExpect(jsonPath("$.crops").value(DEFAULT_CROPS))
            .andExpect(jsonPath("$.totalCost").value(DEFAULT_TOTAL_COST.doubleValue()))
            .andExpect(jsonPath("$.cropYields").value(DEFAULT_CROP_YIELDS.doubleValue()))
            .andExpect(jsonPath("$.enable").value(DEFAULT_ENABLE))
            .andExpect(jsonPath("$.volume").value(DEFAULT_VOLUME.doubleValue()))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.done").value(DEFAULT_DONE))
            .andExpect(jsonPath("$.startAt").value(DEFAULT_START_AT.toString()))
            .andExpect(jsonPath("$.endAt").value(DEFAULT_END_AT.toString()));
    }

    @Test
    @Transactional
    void getSeasonsByIdFiltering() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        Long id = season.getId();

        defaultSeasonShouldBeFound("id.equals=" + id);
        defaultSeasonShouldNotBeFound("id.notEquals=" + id);

        defaultSeasonShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSeasonShouldNotBeFound("id.greaterThan=" + id);

        defaultSeasonShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSeasonShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSeasonsByCropsIsEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where crops equals to DEFAULT_CROPS
        defaultSeasonShouldBeFound("crops.equals=" + DEFAULT_CROPS);

        // Get all the seasonList where crops equals to UPDATED_CROPS
        defaultSeasonShouldNotBeFound("crops.equals=" + UPDATED_CROPS);
    }

    @Test
    @Transactional
    void getAllSeasonsByCropsIsInShouldWork() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where crops in DEFAULT_CROPS or UPDATED_CROPS
        defaultSeasonShouldBeFound("crops.in=" + DEFAULT_CROPS + "," + UPDATED_CROPS);

        // Get all the seasonList where crops equals to UPDATED_CROPS
        defaultSeasonShouldNotBeFound("crops.in=" + UPDATED_CROPS);
    }

    @Test
    @Transactional
    void getAllSeasonsByCropsIsNullOrNotNull() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where crops is not null
        defaultSeasonShouldBeFound("crops.specified=true");

        // Get all the seasonList where crops is null
        defaultSeasonShouldNotBeFound("crops.specified=false");
    }

    @Test
    @Transactional
    void getAllSeasonsByCropsContainsSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where crops contains DEFAULT_CROPS
        defaultSeasonShouldBeFound("crops.contains=" + DEFAULT_CROPS);

        // Get all the seasonList where crops contains UPDATED_CROPS
        defaultSeasonShouldNotBeFound("crops.contains=" + UPDATED_CROPS);
    }

    @Test
    @Transactional
    void getAllSeasonsByCropsNotContainsSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where crops does not contain DEFAULT_CROPS
        defaultSeasonShouldNotBeFound("crops.doesNotContain=" + DEFAULT_CROPS);

        // Get all the seasonList where crops does not contain UPDATED_CROPS
        defaultSeasonShouldBeFound("crops.doesNotContain=" + UPDATED_CROPS);
    }

    @Test
    @Transactional
    void getAllSeasonsByTotalCostIsEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where totalCost equals to DEFAULT_TOTAL_COST
        defaultSeasonShouldBeFound("totalCost.equals=" + DEFAULT_TOTAL_COST);

        // Get all the seasonList where totalCost equals to UPDATED_TOTAL_COST
        defaultSeasonShouldNotBeFound("totalCost.equals=" + UPDATED_TOTAL_COST);
    }

    @Test
    @Transactional
    void getAllSeasonsByTotalCostIsInShouldWork() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where totalCost in DEFAULT_TOTAL_COST or UPDATED_TOTAL_COST
        defaultSeasonShouldBeFound("totalCost.in=" + DEFAULT_TOTAL_COST + "," + UPDATED_TOTAL_COST);

        // Get all the seasonList where totalCost equals to UPDATED_TOTAL_COST
        defaultSeasonShouldNotBeFound("totalCost.in=" + UPDATED_TOTAL_COST);
    }

    @Test
    @Transactional
    void getAllSeasonsByTotalCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where totalCost is not null
        defaultSeasonShouldBeFound("totalCost.specified=true");

        // Get all the seasonList where totalCost is null
        defaultSeasonShouldNotBeFound("totalCost.specified=false");
    }

    @Test
    @Transactional
    void getAllSeasonsByTotalCostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where totalCost is greater than or equal to DEFAULT_TOTAL_COST
        defaultSeasonShouldBeFound("totalCost.greaterThanOrEqual=" + DEFAULT_TOTAL_COST);

        // Get all the seasonList where totalCost is greater than or equal to UPDATED_TOTAL_COST
        defaultSeasonShouldNotBeFound("totalCost.greaterThanOrEqual=" + UPDATED_TOTAL_COST);
    }

    @Test
    @Transactional
    void getAllSeasonsByTotalCostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where totalCost is less than or equal to DEFAULT_TOTAL_COST
        defaultSeasonShouldBeFound("totalCost.lessThanOrEqual=" + DEFAULT_TOTAL_COST);

        // Get all the seasonList where totalCost is less than or equal to SMALLER_TOTAL_COST
        defaultSeasonShouldNotBeFound("totalCost.lessThanOrEqual=" + SMALLER_TOTAL_COST);
    }

    @Test
    @Transactional
    void getAllSeasonsByTotalCostIsLessThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where totalCost is less than DEFAULT_TOTAL_COST
        defaultSeasonShouldNotBeFound("totalCost.lessThan=" + DEFAULT_TOTAL_COST);

        // Get all the seasonList where totalCost is less than UPDATED_TOTAL_COST
        defaultSeasonShouldBeFound("totalCost.lessThan=" + UPDATED_TOTAL_COST);
    }

    @Test
    @Transactional
    void getAllSeasonsByTotalCostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where totalCost is greater than DEFAULT_TOTAL_COST
        defaultSeasonShouldNotBeFound("totalCost.greaterThan=" + DEFAULT_TOTAL_COST);

        // Get all the seasonList where totalCost is greater than SMALLER_TOTAL_COST
        defaultSeasonShouldBeFound("totalCost.greaterThan=" + SMALLER_TOTAL_COST);
    }

    @Test
    @Transactional
    void getAllSeasonsByCropYieldsIsEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where cropYields equals to DEFAULT_CROP_YIELDS
        defaultSeasonShouldBeFound("cropYields.equals=" + DEFAULT_CROP_YIELDS);

        // Get all the seasonList where cropYields equals to UPDATED_CROP_YIELDS
        defaultSeasonShouldNotBeFound("cropYields.equals=" + UPDATED_CROP_YIELDS);
    }

    @Test
    @Transactional
    void getAllSeasonsByCropYieldsIsInShouldWork() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where cropYields in DEFAULT_CROP_YIELDS or UPDATED_CROP_YIELDS
        defaultSeasonShouldBeFound("cropYields.in=" + DEFAULT_CROP_YIELDS + "," + UPDATED_CROP_YIELDS);

        // Get all the seasonList where cropYields equals to UPDATED_CROP_YIELDS
        defaultSeasonShouldNotBeFound("cropYields.in=" + UPDATED_CROP_YIELDS);
    }

    @Test
    @Transactional
    void getAllSeasonsByCropYieldsIsNullOrNotNull() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where cropYields is not null
        defaultSeasonShouldBeFound("cropYields.specified=true");

        // Get all the seasonList where cropYields is null
        defaultSeasonShouldNotBeFound("cropYields.specified=false");
    }

    @Test
    @Transactional
    void getAllSeasonsByCropYieldsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where cropYields is greater than or equal to DEFAULT_CROP_YIELDS
        defaultSeasonShouldBeFound("cropYields.greaterThanOrEqual=" + DEFAULT_CROP_YIELDS);

        // Get all the seasonList where cropYields is greater than or equal to UPDATED_CROP_YIELDS
        defaultSeasonShouldNotBeFound("cropYields.greaterThanOrEqual=" + UPDATED_CROP_YIELDS);
    }

    @Test
    @Transactional
    void getAllSeasonsByCropYieldsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where cropYields is less than or equal to DEFAULT_CROP_YIELDS
        defaultSeasonShouldBeFound("cropYields.lessThanOrEqual=" + DEFAULT_CROP_YIELDS);

        // Get all the seasonList where cropYields is less than or equal to SMALLER_CROP_YIELDS
        defaultSeasonShouldNotBeFound("cropYields.lessThanOrEqual=" + SMALLER_CROP_YIELDS);
    }

    @Test
    @Transactional
    void getAllSeasonsByCropYieldsIsLessThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where cropYields is less than DEFAULT_CROP_YIELDS
        defaultSeasonShouldNotBeFound("cropYields.lessThan=" + DEFAULT_CROP_YIELDS);

        // Get all the seasonList where cropYields is less than UPDATED_CROP_YIELDS
        defaultSeasonShouldBeFound("cropYields.lessThan=" + UPDATED_CROP_YIELDS);
    }

    @Test
    @Transactional
    void getAllSeasonsByCropYieldsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where cropYields is greater than DEFAULT_CROP_YIELDS
        defaultSeasonShouldNotBeFound("cropYields.greaterThan=" + DEFAULT_CROP_YIELDS);

        // Get all the seasonList where cropYields is greater than SMALLER_CROP_YIELDS
        defaultSeasonShouldBeFound("cropYields.greaterThan=" + SMALLER_CROP_YIELDS);
    }

    @Test
    @Transactional
    void getAllSeasonsByEnableIsEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where enable equals to DEFAULT_ENABLE
        defaultSeasonShouldBeFound("enable.equals=" + DEFAULT_ENABLE);

        // Get all the seasonList where enable equals to UPDATED_ENABLE
        defaultSeasonShouldNotBeFound("enable.equals=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllSeasonsByEnableIsInShouldWork() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where enable in DEFAULT_ENABLE or UPDATED_ENABLE
        defaultSeasonShouldBeFound("enable.in=" + DEFAULT_ENABLE + "," + UPDATED_ENABLE);

        // Get all the seasonList where enable equals to UPDATED_ENABLE
        defaultSeasonShouldNotBeFound("enable.in=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllSeasonsByEnableIsNullOrNotNull() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where enable is not null
        defaultSeasonShouldBeFound("enable.specified=true");

        // Get all the seasonList where enable is null
        defaultSeasonShouldNotBeFound("enable.specified=false");
    }

    @Test
    @Transactional
    void getAllSeasonsByEnableIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where enable is greater than or equal to DEFAULT_ENABLE
        defaultSeasonShouldBeFound("enable.greaterThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the seasonList where enable is greater than or equal to UPDATED_ENABLE
        defaultSeasonShouldNotBeFound("enable.greaterThanOrEqual=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllSeasonsByEnableIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where enable is less than or equal to DEFAULT_ENABLE
        defaultSeasonShouldBeFound("enable.lessThanOrEqual=" + DEFAULT_ENABLE);

        // Get all the seasonList where enable is less than or equal to SMALLER_ENABLE
        defaultSeasonShouldNotBeFound("enable.lessThanOrEqual=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllSeasonsByEnableIsLessThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where enable is less than DEFAULT_ENABLE
        defaultSeasonShouldNotBeFound("enable.lessThan=" + DEFAULT_ENABLE);

        // Get all the seasonList where enable is less than UPDATED_ENABLE
        defaultSeasonShouldBeFound("enable.lessThan=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    void getAllSeasonsByEnableIsGreaterThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where enable is greater than DEFAULT_ENABLE
        defaultSeasonShouldNotBeFound("enable.greaterThan=" + DEFAULT_ENABLE);

        // Get all the seasonList where enable is greater than SMALLER_ENABLE
        defaultSeasonShouldBeFound("enable.greaterThan=" + SMALLER_ENABLE);
    }

    @Test
    @Transactional
    void getAllSeasonsByVolumeIsEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where volume equals to DEFAULT_VOLUME
        defaultSeasonShouldBeFound("volume.equals=" + DEFAULT_VOLUME);

        // Get all the seasonList where volume equals to UPDATED_VOLUME
        defaultSeasonShouldNotBeFound("volume.equals=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllSeasonsByVolumeIsInShouldWork() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where volume in DEFAULT_VOLUME or UPDATED_VOLUME
        defaultSeasonShouldBeFound("volume.in=" + DEFAULT_VOLUME + "," + UPDATED_VOLUME);

        // Get all the seasonList where volume equals to UPDATED_VOLUME
        defaultSeasonShouldNotBeFound("volume.in=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllSeasonsByVolumeIsNullOrNotNull() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where volume is not null
        defaultSeasonShouldBeFound("volume.specified=true");

        // Get all the seasonList where volume is null
        defaultSeasonShouldNotBeFound("volume.specified=false");
    }

    @Test
    @Transactional
    void getAllSeasonsByVolumeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where volume is greater than or equal to DEFAULT_VOLUME
        defaultSeasonShouldBeFound("volume.greaterThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the seasonList where volume is greater than or equal to UPDATED_VOLUME
        defaultSeasonShouldNotBeFound("volume.greaterThanOrEqual=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllSeasonsByVolumeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where volume is less than or equal to DEFAULT_VOLUME
        defaultSeasonShouldBeFound("volume.lessThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the seasonList where volume is less than or equal to SMALLER_VOLUME
        defaultSeasonShouldNotBeFound("volume.lessThanOrEqual=" + SMALLER_VOLUME);
    }

    @Test
    @Transactional
    void getAllSeasonsByVolumeIsLessThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where volume is less than DEFAULT_VOLUME
        defaultSeasonShouldNotBeFound("volume.lessThan=" + DEFAULT_VOLUME);

        // Get all the seasonList where volume is less than UPDATED_VOLUME
        defaultSeasonShouldBeFound("volume.lessThan=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllSeasonsByVolumeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where volume is greater than DEFAULT_VOLUME
        defaultSeasonShouldNotBeFound("volume.greaterThan=" + DEFAULT_VOLUME);

        // Get all the seasonList where volume is greater than SMALLER_VOLUME
        defaultSeasonShouldBeFound("volume.greaterThan=" + SMALLER_VOLUME);
    }

    @Test
    @Transactional
    void getAllSeasonsByUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where unit equals to DEFAULT_UNIT
        defaultSeasonShouldBeFound("unit.equals=" + DEFAULT_UNIT);

        // Get all the seasonList where unit equals to UPDATED_UNIT
        defaultSeasonShouldNotBeFound("unit.equals=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllSeasonsByUnitIsInShouldWork() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where unit in DEFAULT_UNIT or UPDATED_UNIT
        defaultSeasonShouldBeFound("unit.in=" + DEFAULT_UNIT + "," + UPDATED_UNIT);

        // Get all the seasonList where unit equals to UPDATED_UNIT
        defaultSeasonShouldNotBeFound("unit.in=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllSeasonsByUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where unit is not null
        defaultSeasonShouldBeFound("unit.specified=true");

        // Get all the seasonList where unit is null
        defaultSeasonShouldNotBeFound("unit.specified=false");
    }

    @Test
    @Transactional
    void getAllSeasonsByUnitContainsSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where unit contains DEFAULT_UNIT
        defaultSeasonShouldBeFound("unit.contains=" + DEFAULT_UNIT);

        // Get all the seasonList where unit contains UPDATED_UNIT
        defaultSeasonShouldNotBeFound("unit.contains=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllSeasonsByUnitNotContainsSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where unit does not contain DEFAULT_UNIT
        defaultSeasonShouldNotBeFound("unit.doesNotContain=" + DEFAULT_UNIT);

        // Get all the seasonList where unit does not contain UPDATED_UNIT
        defaultSeasonShouldBeFound("unit.doesNotContain=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllSeasonsByDoneIsEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where done equals to DEFAULT_DONE
        defaultSeasonShouldBeFound("done.equals=" + DEFAULT_DONE);

        // Get all the seasonList where done equals to UPDATED_DONE
        defaultSeasonShouldNotBeFound("done.equals=" + UPDATED_DONE);
    }

    @Test
    @Transactional
    void getAllSeasonsByDoneIsInShouldWork() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where done in DEFAULT_DONE or UPDATED_DONE
        defaultSeasonShouldBeFound("done.in=" + DEFAULT_DONE + "," + UPDATED_DONE);

        // Get all the seasonList where done equals to UPDATED_DONE
        defaultSeasonShouldNotBeFound("done.in=" + UPDATED_DONE);
    }

    @Test
    @Transactional
    void getAllSeasonsByDoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where done is not null
        defaultSeasonShouldBeFound("done.specified=true");

        // Get all the seasonList where done is null
        defaultSeasonShouldNotBeFound("done.specified=false");
    }

    @Test
    @Transactional
    void getAllSeasonsByDoneIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where done is greater than or equal to DEFAULT_DONE
        defaultSeasonShouldBeFound("done.greaterThanOrEqual=" + DEFAULT_DONE);

        // Get all the seasonList where done is greater than or equal to UPDATED_DONE
        defaultSeasonShouldNotBeFound("done.greaterThanOrEqual=" + UPDATED_DONE);
    }

    @Test
    @Transactional
    void getAllSeasonsByDoneIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where done is less than or equal to DEFAULT_DONE
        defaultSeasonShouldBeFound("done.lessThanOrEqual=" + DEFAULT_DONE);

        // Get all the seasonList where done is less than or equal to SMALLER_DONE
        defaultSeasonShouldNotBeFound("done.lessThanOrEqual=" + SMALLER_DONE);
    }

    @Test
    @Transactional
    void getAllSeasonsByDoneIsLessThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where done is less than DEFAULT_DONE
        defaultSeasonShouldNotBeFound("done.lessThan=" + DEFAULT_DONE);

        // Get all the seasonList where done is less than UPDATED_DONE
        defaultSeasonShouldBeFound("done.lessThan=" + UPDATED_DONE);
    }

    @Test
    @Transactional
    void getAllSeasonsByDoneIsGreaterThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where done is greater than DEFAULT_DONE
        defaultSeasonShouldNotBeFound("done.greaterThan=" + DEFAULT_DONE);

        // Get all the seasonList where done is greater than SMALLER_DONE
        defaultSeasonShouldBeFound("done.greaterThan=" + SMALLER_DONE);
    }

    @Test
    @Transactional
    void getAllSeasonsByStartAtIsEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where startAt equals to DEFAULT_START_AT
        defaultSeasonShouldBeFound("startAt.equals=" + DEFAULT_START_AT);

        // Get all the seasonList where startAt equals to UPDATED_START_AT
        defaultSeasonShouldNotBeFound("startAt.equals=" + UPDATED_START_AT);
    }

    @Test
    @Transactional
    void getAllSeasonsByStartAtIsInShouldWork() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where startAt in DEFAULT_START_AT or UPDATED_START_AT
        defaultSeasonShouldBeFound("startAt.in=" + DEFAULT_START_AT + "," + UPDATED_START_AT);

        // Get all the seasonList where startAt equals to UPDATED_START_AT
        defaultSeasonShouldNotBeFound("startAt.in=" + UPDATED_START_AT);
    }

    @Test
    @Transactional
    void getAllSeasonsByStartAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where startAt is not null
        defaultSeasonShouldBeFound("startAt.specified=true");

        // Get all the seasonList where startAt is null
        defaultSeasonShouldNotBeFound("startAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSeasonsByStartAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where startAt is greater than or equal to DEFAULT_START_AT
        defaultSeasonShouldBeFound("startAt.greaterThanOrEqual=" + DEFAULT_START_AT);

        // Get all the seasonList where startAt is greater than or equal to UPDATED_START_AT
        defaultSeasonShouldNotBeFound("startAt.greaterThanOrEqual=" + UPDATED_START_AT);
    }

    @Test
    @Transactional
    void getAllSeasonsByStartAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where startAt is less than or equal to DEFAULT_START_AT
        defaultSeasonShouldBeFound("startAt.lessThanOrEqual=" + DEFAULT_START_AT);

        // Get all the seasonList where startAt is less than or equal to SMALLER_START_AT
        defaultSeasonShouldNotBeFound("startAt.lessThanOrEqual=" + SMALLER_START_AT);
    }

    @Test
    @Transactional
    void getAllSeasonsByStartAtIsLessThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where startAt is less than DEFAULT_START_AT
        defaultSeasonShouldNotBeFound("startAt.lessThan=" + DEFAULT_START_AT);

        // Get all the seasonList where startAt is less than UPDATED_START_AT
        defaultSeasonShouldBeFound("startAt.lessThan=" + UPDATED_START_AT);
    }

    @Test
    @Transactional
    void getAllSeasonsByStartAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where startAt is greater than DEFAULT_START_AT
        defaultSeasonShouldNotBeFound("startAt.greaterThan=" + DEFAULT_START_AT);

        // Get all the seasonList where startAt is greater than SMALLER_START_AT
        defaultSeasonShouldBeFound("startAt.greaterThan=" + SMALLER_START_AT);
    }

    @Test
    @Transactional
    void getAllSeasonsByEndAtIsEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where endAt equals to DEFAULT_END_AT
        defaultSeasonShouldBeFound("endAt.equals=" + DEFAULT_END_AT);

        // Get all the seasonList where endAt equals to UPDATED_END_AT
        defaultSeasonShouldNotBeFound("endAt.equals=" + UPDATED_END_AT);
    }

    @Test
    @Transactional
    void getAllSeasonsByEndAtIsInShouldWork() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where endAt in DEFAULT_END_AT or UPDATED_END_AT
        defaultSeasonShouldBeFound("endAt.in=" + DEFAULT_END_AT + "," + UPDATED_END_AT);

        // Get all the seasonList where endAt equals to UPDATED_END_AT
        defaultSeasonShouldNotBeFound("endAt.in=" + UPDATED_END_AT);
    }

    @Test
    @Transactional
    void getAllSeasonsByEndAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where endAt is not null
        defaultSeasonShouldBeFound("endAt.specified=true");

        // Get all the seasonList where endAt is null
        defaultSeasonShouldNotBeFound("endAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSeasonsByEndAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where endAt is greater than or equal to DEFAULT_END_AT
        defaultSeasonShouldBeFound("endAt.greaterThanOrEqual=" + DEFAULT_END_AT);

        // Get all the seasonList where endAt is greater than or equal to UPDATED_END_AT
        defaultSeasonShouldNotBeFound("endAt.greaterThanOrEqual=" + UPDATED_END_AT);
    }

    @Test
    @Transactional
    void getAllSeasonsByEndAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where endAt is less than or equal to DEFAULT_END_AT
        defaultSeasonShouldBeFound("endAt.lessThanOrEqual=" + DEFAULT_END_AT);

        // Get all the seasonList where endAt is less than or equal to SMALLER_END_AT
        defaultSeasonShouldNotBeFound("endAt.lessThanOrEqual=" + SMALLER_END_AT);
    }

    @Test
    @Transactional
    void getAllSeasonsByEndAtIsLessThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where endAt is less than DEFAULT_END_AT
        defaultSeasonShouldNotBeFound("endAt.lessThan=" + DEFAULT_END_AT);

        // Get all the seasonList where endAt is less than UPDATED_END_AT
        defaultSeasonShouldBeFound("endAt.lessThan=" + UPDATED_END_AT);
    }

    @Test
    @Transactional
    void getAllSeasonsByEndAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where endAt is greater than DEFAULT_END_AT
        defaultSeasonShouldNotBeFound("endAt.greaterThan=" + DEFAULT_END_AT);

        // Get all the seasonList where endAt is greater than SMALLER_END_AT
        defaultSeasonShouldBeFound("endAt.greaterThan=" + SMALLER_END_AT);
    }

    @Test
    @Transactional
    void getAllSeasonsByTransactionIsEqualToSomething() throws Exception {
        Transaction transaction;
        if (TestUtil.findAll(em, Transaction.class).isEmpty()) {
            seasonRepository.saveAndFlush(season);
            transaction = TransactionResourceIT.createEntity(em);
        } else {
            transaction = TestUtil.findAll(em, Transaction.class).get(0);
        }
        em.persist(transaction);
        em.flush();
        season.addTransaction(transaction);
        seasonRepository.saveAndFlush(season);
        Long transactionId = transaction.getId();

        // Get all the seasonList where transaction equals to transactionId
        defaultSeasonShouldBeFound("transactionId.equals=" + transactionId);

        // Get all the seasonList where transaction equals to (transactionId + 1)
        defaultSeasonShouldNotBeFound("transactionId.equals=" + (transactionId + 1));
    }

    @Test
    @Transactional
    void getAllSeasonsByCropIsEqualToSomething() throws Exception {
        Crops crop;
        if (TestUtil.findAll(em, Crops.class).isEmpty()) {
            seasonRepository.saveAndFlush(season);
            crop = CropsResourceIT.createEntity(em);
        } else {
            crop = TestUtil.findAll(em, Crops.class).get(0);
        }
        em.persist(crop);
        em.flush();
        season.setCrop(crop);
        seasonRepository.saveAndFlush(season);
        Long cropId = crop.getId();

        // Get all the seasonList where crop equals to cropId
        defaultSeasonShouldBeFound("cropId.equals=" + cropId);

        // Get all the seasonList where crop equals to (cropId + 1)
        defaultSeasonShouldNotBeFound("cropId.equals=" + (cropId + 1));
    }

    @Test
    @Transactional
    void getAllSeasonsByFieldIsEqualToSomething() throws Exception {
        Field field;
        if (TestUtil.findAll(em, Field.class).isEmpty()) {
            seasonRepository.saveAndFlush(season);
            field = FieldResourceIT.createEntity(em);
        } else {
            field = TestUtil.findAll(em, Field.class).get(0);
        }
        em.persist(field);
        em.flush();
        season.setField(field);
        seasonRepository.saveAndFlush(season);
        Long fieldId = field.getId();

        // Get all the seasonList where field equals to fieldId
        defaultSeasonShouldBeFound("fieldId.equals=" + fieldId);

        // Get all the seasonList where field equals to (fieldId + 1)
        defaultSeasonShouldNotBeFound("fieldId.equals=" + (fieldId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSeasonShouldBeFound(String filter) throws Exception {
        restSeasonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(season.getId().intValue())))
            .andExpect(jsonPath("$.[*].crops").value(hasItem(DEFAULT_CROPS)))
            .andExpect(jsonPath("$.[*].totalCost").value(hasItem(DEFAULT_TOTAL_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].cropYields").value(hasItem(DEFAULT_CROP_YIELDS.doubleValue())))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE)))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.doubleValue())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].done").value(hasItem(DEFAULT_DONE)))
            .andExpect(jsonPath("$.[*].startAt").value(hasItem(DEFAULT_START_AT.toString())))
            .andExpect(jsonPath("$.[*].endAt").value(hasItem(DEFAULT_END_AT.toString())));

        // Check, that the count call also returns 1
        restSeasonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSeasonShouldNotBeFound(String filter) throws Exception {
        restSeasonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSeasonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSeason() throws Exception {
        // Get the season
        restSeasonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSeason() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        int databaseSizeBeforeUpdate = seasonRepository.findAll().size();

        // Update the season
        Season updatedSeason = seasonRepository.findById(season.getId()).get();
        // Disconnect from session so that the updates on updatedSeason are not directly saved in db
        em.detach(updatedSeason);
        updatedSeason
            .crops(UPDATED_CROPS)
            .totalCost(UPDATED_TOTAL_COST)
            .cropYields(UPDATED_CROP_YIELDS)
            .enable(UPDATED_ENABLE)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .done(UPDATED_DONE)
            .startAt(UPDATED_START_AT)
            .endAt(UPDATED_END_AT);
        SeasonDTO seasonDTO = seasonMapper.toDto(updatedSeason);

        restSeasonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, seasonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(seasonDTO))
            )
            .andExpect(status().isOk());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeUpdate);
        Season testSeason = seasonList.get(seasonList.size() - 1);
        assertThat(testSeason.getCrops()).isEqualTo(UPDATED_CROPS);
        assertThat(testSeason.getTotalCost()).isEqualTo(UPDATED_TOTAL_COST);
        assertThat(testSeason.getCropYields()).isEqualTo(UPDATED_CROP_YIELDS);
        assertThat(testSeason.getEnable()).isEqualTo(UPDATED_ENABLE);
        assertThat(testSeason.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testSeason.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testSeason.getDone()).isEqualTo(UPDATED_DONE);
        assertThat(testSeason.getStartAt()).isEqualTo(UPDATED_START_AT);
        assertThat(testSeason.getEndAt()).isEqualTo(UPDATED_END_AT);
    }

    @Test
    @Transactional
    void putNonExistingSeason() throws Exception {
        int databaseSizeBeforeUpdate = seasonRepository.findAll().size();
        season.setId(count.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSeasonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, seasonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(seasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSeason() throws Exception {
        int databaseSizeBeforeUpdate = seasonRepository.findAll().size();
        season.setId(count.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeasonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(seasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSeason() throws Exception {
        int databaseSizeBeforeUpdate = seasonRepository.findAll().size();
        season.setId(count.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeasonMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(seasonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSeasonWithPatch() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        int databaseSizeBeforeUpdate = seasonRepository.findAll().size();

        // Update the season using partial update
        Season partialUpdatedSeason = new Season();
        partialUpdatedSeason.setId(season.getId());

        partialUpdatedSeason.totalCost(UPDATED_TOTAL_COST).done(UPDATED_DONE).startAt(UPDATED_START_AT).endAt(UPDATED_END_AT);

        restSeasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeason.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSeason))
            )
            .andExpect(status().isOk());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeUpdate);
        Season testSeason = seasonList.get(seasonList.size() - 1);
        assertThat(testSeason.getCrops()).isEqualTo(DEFAULT_CROPS);
        assertThat(testSeason.getTotalCost()).isEqualTo(UPDATED_TOTAL_COST);
        assertThat(testSeason.getCropYields()).isEqualTo(DEFAULT_CROP_YIELDS);
        assertThat(testSeason.getEnable()).isEqualTo(DEFAULT_ENABLE);
        assertThat(testSeason.getVolume()).isEqualTo(DEFAULT_VOLUME);
        assertThat(testSeason.getUnit()).isEqualTo(DEFAULT_UNIT);
        assertThat(testSeason.getDone()).isEqualTo(UPDATED_DONE);
        assertThat(testSeason.getStartAt()).isEqualTo(UPDATED_START_AT);
        assertThat(testSeason.getEndAt()).isEqualTo(UPDATED_END_AT);
    }

    @Test
    @Transactional
    void fullUpdateSeasonWithPatch() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        int databaseSizeBeforeUpdate = seasonRepository.findAll().size();

        // Update the season using partial update
        Season partialUpdatedSeason = new Season();
        partialUpdatedSeason.setId(season.getId());

        partialUpdatedSeason
            .crops(UPDATED_CROPS)
            .totalCost(UPDATED_TOTAL_COST)
            .cropYields(UPDATED_CROP_YIELDS)
            .enable(UPDATED_ENABLE)
            .volume(UPDATED_VOLUME)
            .unit(UPDATED_UNIT)
            .done(UPDATED_DONE)
            .startAt(UPDATED_START_AT)
            .endAt(UPDATED_END_AT);

        restSeasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeason.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSeason))
            )
            .andExpect(status().isOk());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeUpdate);
        Season testSeason = seasonList.get(seasonList.size() - 1);
        assertThat(testSeason.getCrops()).isEqualTo(UPDATED_CROPS);
        assertThat(testSeason.getTotalCost()).isEqualTo(UPDATED_TOTAL_COST);
        assertThat(testSeason.getCropYields()).isEqualTo(UPDATED_CROP_YIELDS);
        assertThat(testSeason.getEnable()).isEqualTo(UPDATED_ENABLE);
        assertThat(testSeason.getVolume()).isEqualTo(UPDATED_VOLUME);
        assertThat(testSeason.getUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(testSeason.getDone()).isEqualTo(UPDATED_DONE);
        assertThat(testSeason.getStartAt()).isEqualTo(UPDATED_START_AT);
        assertThat(testSeason.getEndAt()).isEqualTo(UPDATED_END_AT);
    }

    @Test
    @Transactional
    void patchNonExistingSeason() throws Exception {
        int databaseSizeBeforeUpdate = seasonRepository.findAll().size();
        season.setId(count.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSeasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, seasonDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(seasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSeason() throws Exception {
        int databaseSizeBeforeUpdate = seasonRepository.findAll().size();
        season.setId(count.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(seasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSeason() throws Exception {
        int databaseSizeBeforeUpdate = seasonRepository.findAll().size();
        season.setId(count.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeasonMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(seasonDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSeason() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        int databaseSizeBeforeDelete = seasonRepository.findAll().size();

        // Delete the season
        restSeasonMockMvc
            .perform(delete(ENTITY_API_URL_ID, season.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
