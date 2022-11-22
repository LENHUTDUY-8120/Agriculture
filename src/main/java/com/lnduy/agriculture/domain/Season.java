package com.lnduy.agriculture.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Season.
 */
@Entity
@Table(name = "season")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Season implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "crops")
    private String crops;

    @Column(name = "total_cost")
    private Double totalCost;

    @Column(name = "crop_yields")
    private Double cropYields;

    @Column(name = "enable")
    private Integer enable;

    @Column(name = "unit")
    private String unit;

    @Column(name = "done")
    private Integer done;

    @Column(name = "start_at")
    private ZonedDateTime startAt;

    @Column(name = "end_at")
    private ZonedDateTime endAt;

    @OneToMany(mappedBy = "season")
    @JsonIgnoreProperties(value = { "season" }, allowSetters = true)
    private Set<Transaction> transactions = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "warehouse" }, allowSetters = true)
    private Crops crop;

    @ManyToOne
    @JsonIgnoreProperties(value = { "seasons", "devices", "soil" }, allowSetters = true)
    private Field field;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Season id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCrops() {
        return this.crops;
    }

    public Season crops(String crops) {
        this.setCrops(crops);
        return this;
    }

    public void setCrops(String crops) {
        this.crops = crops;
    }

    public Double getTotalCost() {
        return this.totalCost;
    }

    public Season totalCost(Double totalCost) {
        this.setTotalCost(totalCost);
        return this;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Double getCropYields() {
        return this.cropYields;
    }

    public Season cropYields(Double cropYields) {
        this.setCropYields(cropYields);
        return this;
    }

    public void setCropYields(Double cropYields) {
        this.cropYields = cropYields;
    }

    public Integer getEnable() {
        return this.enable;
    }

    public Season enable(Integer enable) {
        this.setEnable(enable);
        return this;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public String getUnit() {
        return this.unit;
    }

    public Season unit(String unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getDone() {
        return this.done;
    }

    public Season done(Integer done) {
        this.setDone(done);
        return this;
    }

    public void setDone(Integer done) {
        this.done = done;
    }

    public ZonedDateTime getStartAt() {
        return this.startAt;
    }

    public Season startAt(ZonedDateTime startAt) {
        this.setStartAt(startAt);
        return this;
    }

    public void setStartAt(ZonedDateTime startAt) {
        this.startAt = startAt;
    }

    public ZonedDateTime getEndAt() {
        return this.endAt;
    }

    public Season endAt(ZonedDateTime endAt) {
        this.setEndAt(endAt);
        return this;
    }

    public void setEndAt(ZonedDateTime endAt) {
        this.endAt = endAt;
    }

    public Set<Transaction> getTransactions() {
        return this.transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        if (this.transactions != null) {
            this.transactions.forEach(i -> i.setSeason(null));
        }
        if (transactions != null) {
            transactions.forEach(i -> i.setSeason(this));
        }
        this.transactions = transactions;
    }

    public Season transactions(Set<Transaction> transactions) {
        this.setTransactions(transactions);
        return this;
    }

    public Season addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setSeason(this);
        return this;
    }

    public Season removeTransaction(Transaction transaction) {
        this.transactions.remove(transaction);
        transaction.setSeason(null);
        return this;
    }

    public Crops getCrop() {
        return this.crop;
    }

    public void setCrop(Crops crops) {
        this.crop = crops;
    }

    public Season crop(Crops crops) {
        this.setCrop(crops);
        return this;
    }

    public Field getField() {
        return this.field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Season field(Field field) {
        this.setField(field);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Season)) {
            return false;
        }
        return id != null && id.equals(((Season) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Season{" +
            "id=" + getId() +
            ", crops='" + getCrops() + "'" +
            ", totalCost=" + getTotalCost() +
            ", cropYields=" + getCropYields() +
            ", enable=" + getEnable() +
            ", unit='" + getUnit() + "'" +
            ", done=" + getDone() +
            ", startAt='" + getStartAt() + "'" +
            ", endAt='" + getEndAt() + "'" +
            "}";
    }
}
