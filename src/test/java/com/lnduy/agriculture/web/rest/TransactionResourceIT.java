package com.lnduy.agriculture.web.rest;

import static com.lnduy.agriculture.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lnduy.agriculture.IntegrationTest;
import com.lnduy.agriculture.domain.Season;
import com.lnduy.agriculture.domain.Transaction;
import com.lnduy.agriculture.repository.TransactionRepository;
import com.lnduy.agriculture.service.criteria.TransactionCriteria;
import com.lnduy.agriculture.service.dto.TransactionDTO;
import com.lnduy.agriculture.service.mapper.TransactionMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link TransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionResourceIT {

    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(1 - 1);

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTIONS = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTIONS = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATED_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_AT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATED_AT = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionMockMvc;

    private Transaction transaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .total(DEFAULT_TOTAL)
            .price(DEFAULT_PRICE)
            .type(DEFAULT_TYPE)
            .descriptions(DEFAULT_DESCRIPTIONS)
            .createdAt(DEFAULT_CREATED_AT);
        return transaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createUpdatedEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .total(UPDATED_TOTAL)
            .price(UPDATED_PRICE)
            .type(UPDATED_TYPE)
            .descriptions(UPDATED_DESCRIPTIONS)
            .createdAt(UPDATED_CREATED_AT);
        return transaction;
    }

    @BeforeEach
    public void initTest() {
        transaction = createEntity(em);
    }

    @Test
    @Transactional
    void createTransaction() throws Exception {
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getTotal()).isEqualByComparingTo(DEFAULT_TOTAL);
        assertThat(testTransaction.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testTransaction.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTransaction.getDescriptions()).isEqualTo(DEFAULT_DESCRIPTIONS);
        assertThat(testTransaction.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createTransactionWithExistingId() throws Exception {
        // Create the Transaction with an existing ID
        transaction.setId(1L);
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTransactions() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].descriptions").value(hasItem(DEFAULT_DESCRIPTIONS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get the transaction
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.descriptions").value(DEFAULT_DESCRIPTIONS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        Long id = transaction.getId();

        defaultTransactionShouldBeFound("id.equals=" + id);
        defaultTransactionShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where total equals to DEFAULT_TOTAL
        defaultTransactionShouldBeFound("total.equals=" + DEFAULT_TOTAL);

        // Get all the transactionList where total equals to UPDATED_TOTAL
        defaultTransactionShouldNotBeFound("total.equals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where total in DEFAULT_TOTAL or UPDATED_TOTAL
        defaultTransactionShouldBeFound("total.in=" + DEFAULT_TOTAL + "," + UPDATED_TOTAL);

        // Get all the transactionList where total equals to UPDATED_TOTAL
        defaultTransactionShouldNotBeFound("total.in=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where total is not null
        defaultTransactionShouldBeFound("total.specified=true");

        // Get all the transactionList where total is null
        defaultTransactionShouldNotBeFound("total.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where total is greater than or equal to DEFAULT_TOTAL
        defaultTransactionShouldBeFound("total.greaterThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the transactionList where total is greater than or equal to UPDATED_TOTAL
        defaultTransactionShouldNotBeFound("total.greaterThanOrEqual=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where total is less than or equal to DEFAULT_TOTAL
        defaultTransactionShouldBeFound("total.lessThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the transactionList where total is less than or equal to SMALLER_TOTAL
        defaultTransactionShouldNotBeFound("total.lessThanOrEqual=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where total is less than DEFAULT_TOTAL
        defaultTransactionShouldNotBeFound("total.lessThan=" + DEFAULT_TOTAL);

        // Get all the transactionList where total is less than UPDATED_TOTAL
        defaultTransactionShouldBeFound("total.lessThan=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where total is greater than DEFAULT_TOTAL
        defaultTransactionShouldNotBeFound("total.greaterThan=" + DEFAULT_TOTAL);

        // Get all the transactionList where total is greater than SMALLER_TOTAL
        defaultTransactionShouldBeFound("total.greaterThan=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllTransactionsByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where price equals to DEFAULT_PRICE
        defaultTransactionShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the transactionList where price equals to UPDATED_PRICE
        defaultTransactionShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllTransactionsByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultTransactionShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the transactionList where price equals to UPDATED_PRICE
        defaultTransactionShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllTransactionsByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where price is not null
        defaultTransactionShouldBeFound("price.specified=true");

        // Get all the transactionList where price is null
        defaultTransactionShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where price is greater than or equal to DEFAULT_PRICE
        defaultTransactionShouldBeFound("price.greaterThanOrEqual=" + DEFAULT_PRICE);

        // Get all the transactionList where price is greater than or equal to UPDATED_PRICE
        defaultTransactionShouldNotBeFound("price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllTransactionsByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where price is less than or equal to DEFAULT_PRICE
        defaultTransactionShouldBeFound("price.lessThanOrEqual=" + DEFAULT_PRICE);

        // Get all the transactionList where price is less than or equal to SMALLER_PRICE
        defaultTransactionShouldNotBeFound("price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllTransactionsByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where price is less than DEFAULT_PRICE
        defaultTransactionShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);

        // Get all the transactionList where price is less than UPDATED_PRICE
        defaultTransactionShouldBeFound("price.lessThan=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllTransactionsByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where price is greater than DEFAULT_PRICE
        defaultTransactionShouldNotBeFound("price.greaterThan=" + DEFAULT_PRICE);

        // Get all the transactionList where price is greater than SMALLER_PRICE
        defaultTransactionShouldBeFound("price.greaterThan=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where type equals to DEFAULT_TYPE
        defaultTransactionShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the transactionList where type equals to UPDATED_TYPE
        defaultTransactionShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultTransactionShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the transactionList where type equals to UPDATED_TYPE
        defaultTransactionShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where type is not null
        defaultTransactionShouldBeFound("type.specified=true");

        // Get all the transactionList where type is null
        defaultTransactionShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByTypeContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where type contains DEFAULT_TYPE
        defaultTransactionShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the transactionList where type contains UPDATED_TYPE
        defaultTransactionShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where type does not contain DEFAULT_TYPE
        defaultTransactionShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the transactionList where type does not contain UPDATED_TYPE
        defaultTransactionShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionsByDescriptionsIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where descriptions equals to DEFAULT_DESCRIPTIONS
        defaultTransactionShouldBeFound("descriptions.equals=" + DEFAULT_DESCRIPTIONS);

        // Get all the transactionList where descriptions equals to UPDATED_DESCRIPTIONS
        defaultTransactionShouldNotBeFound("descriptions.equals=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllTransactionsByDescriptionsIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where descriptions in DEFAULT_DESCRIPTIONS or UPDATED_DESCRIPTIONS
        defaultTransactionShouldBeFound("descriptions.in=" + DEFAULT_DESCRIPTIONS + "," + UPDATED_DESCRIPTIONS);

        // Get all the transactionList where descriptions equals to UPDATED_DESCRIPTIONS
        defaultTransactionShouldNotBeFound("descriptions.in=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllTransactionsByDescriptionsIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where descriptions is not null
        defaultTransactionShouldBeFound("descriptions.specified=true");

        // Get all the transactionList where descriptions is null
        defaultTransactionShouldNotBeFound("descriptions.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByDescriptionsContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where descriptions contains DEFAULT_DESCRIPTIONS
        defaultTransactionShouldBeFound("descriptions.contains=" + DEFAULT_DESCRIPTIONS);

        // Get all the transactionList where descriptions contains UPDATED_DESCRIPTIONS
        defaultTransactionShouldNotBeFound("descriptions.contains=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllTransactionsByDescriptionsNotContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where descriptions does not contain DEFAULT_DESCRIPTIONS
        defaultTransactionShouldNotBeFound("descriptions.doesNotContain=" + DEFAULT_DESCRIPTIONS);

        // Get all the transactionList where descriptions does not contain UPDATED_DESCRIPTIONS
        defaultTransactionShouldBeFound("descriptions.doesNotContain=" + UPDATED_DESCRIPTIONS);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdAt equals to DEFAULT_CREATED_AT
        defaultTransactionShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the transactionList where createdAt equals to UPDATED_CREATED_AT
        defaultTransactionShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultTransactionShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the transactionList where createdAt equals to UPDATED_CREATED_AT
        defaultTransactionShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdAt is not null
        defaultTransactionShouldBeFound("createdAt.specified=true");

        // Get all the transactionList where createdAt is null
        defaultTransactionShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultTransactionShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the transactionList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultTransactionShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultTransactionShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the transactionList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultTransactionShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdAt is less than DEFAULT_CREATED_AT
        defaultTransactionShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the transactionList where createdAt is less than UPDATED_CREATED_AT
        defaultTransactionShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdAt is greater than DEFAULT_CREATED_AT
        defaultTransactionShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the transactionList where createdAt is greater than SMALLER_CREATED_AT
        defaultTransactionShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTransactionsBySeasonIsEqualToSomething() throws Exception {
        Season season;
        if (TestUtil.findAll(em, Season.class).isEmpty()) {
            transactionRepository.saveAndFlush(transaction);
            season = SeasonResourceIT.createEntity(em);
        } else {
            season = TestUtil.findAll(em, Season.class).get(0);
        }
        em.persist(season);
        em.flush();
        transaction.setSeason(season);
        transactionRepository.saveAndFlush(transaction);
        Long seasonId = season.getId();

        // Get all the transactionList where season equals to seasonId
        defaultTransactionShouldBeFound("seasonId.equals=" + seasonId);

        // Get all the transactionList where season equals to (seasonId + 1)
        defaultTransactionShouldNotBeFound("seasonId.equals=" + (seasonId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionShouldBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].descriptions").value(hasItem(DEFAULT_DESCRIPTIONS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionShouldNotBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findById(transaction.getId()).get();
        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
        em.detach(updatedTransaction);
        updatedTransaction
            .total(UPDATED_TOTAL)
            .price(UPDATED_PRICE)
            .type(UPDATED_TYPE)
            .descriptions(UPDATED_DESCRIPTIONS)
            .createdAt(UPDATED_CREATED_AT);
        TransactionDTO transactionDTO = transactionMapper.toDto(updatedTransaction);

        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getTotal()).isEqualByComparingTo(UPDATED_TOTAL);
        assertThat(testTransaction.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testTransaction.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTransaction.getDescriptions()).isEqualTo(UPDATED_DESCRIPTIONS);
        assertThat(testTransaction.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction.price(UPDATED_PRICE);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getTotal()).isEqualByComparingTo(DEFAULT_TOTAL);
        assertThat(testTransaction.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testTransaction.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTransaction.getDescriptions()).isEqualTo(DEFAULT_DESCRIPTIONS);
        assertThat(testTransaction.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction
            .total(UPDATED_TOTAL)
            .price(UPDATED_PRICE)
            .type(UPDATED_TYPE)
            .descriptions(UPDATED_DESCRIPTIONS)
            .createdAt(UPDATED_CREATED_AT);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getTotal()).isEqualByComparingTo(UPDATED_TOTAL);
        assertThat(testTransaction.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testTransaction.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTransaction.getDescriptions()).isEqualTo(UPDATED_DESCRIPTIONS);
        assertThat(testTransaction.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(transactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeDelete = transactionRepository.findAll().size();

        // Delete the transaction
        restTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, transaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
