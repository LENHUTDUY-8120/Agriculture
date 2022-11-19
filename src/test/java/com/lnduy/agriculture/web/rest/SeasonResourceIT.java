package com.lnduy.agriculture.web.rest;

import static com.lnduy.agriculture.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Season;
import com.lnduy.agriculture.repository.SeasonRepository;
import com.lnduy.agriculture.service.dto.SeasonDTO;
import com.lnduy.agriculture.service.mapper.SeasonMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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

    private static final Double DEFAULT_CROP_YIELDS = 1D;
    private static final Double UPDATED_CROP_YIELDS = 2D;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final Integer DEFAULT_DONE = 1;
    private static final Integer UPDATED_DONE = 2;

    private static final ZonedDateTime DEFAULT_START_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

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
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].done").value(hasItem(DEFAULT_DONE)))
            .andExpect(jsonPath("$.[*].startAt").value(hasItem(sameInstant(DEFAULT_START_AT))))
            .andExpect(jsonPath("$.[*].endAt").value(hasItem(sameInstant(DEFAULT_END_AT))));
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
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.done").value(DEFAULT_DONE))
            .andExpect(jsonPath("$.startAt").value(sameInstant(DEFAULT_START_AT)))
            .andExpect(jsonPath("$.endAt").value(sameInstant(DEFAULT_END_AT)));
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

        partialUpdatedSeason.totalCost(UPDATED_TOTAL_COST).endAt(UPDATED_END_AT);

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
        assertThat(testSeason.getUnit()).isEqualTo(DEFAULT_UNIT);
        assertThat(testSeason.getDone()).isEqualTo(DEFAULT_DONE);
        assertThat(testSeason.getStartAt()).isEqualTo(DEFAULT_START_AT);
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
