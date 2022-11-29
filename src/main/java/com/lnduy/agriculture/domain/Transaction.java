package com.lnduy.agriculture.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "total", precision = 21, scale = 2)
    private BigDecimal total;

    @Column(name = "price", precision = 21, scale = 2)
    private BigDecimal price;

    @Column(name = "type")
    private String type;

    @Column(name = "descriptions")
    private String descriptions;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @ManyToOne
    @JsonIgnoreProperties(value = { "transactions", "crop", "field" }, allowSetters = true)
    private Season season;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public Transaction total(BigDecimal total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Transaction price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getType() {
        return this.type;
    }

    public Transaction type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescriptions() {
        return this.descriptions;
    }

    public Transaction descriptions(String descriptions) {
        this.setDescriptions(descriptions);
        return this;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public LocalDate getCreatedAt() {
        return this.createdAt;
    }

    public Transaction createdAt(LocalDate createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Season getSeason() {
        return this.season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Transaction season(Season season) {
        this.setSeason(season);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return id != null && id.equals(((Transaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", total=" + getTotal() +
            ", price=" + getPrice() +
            ", type='" + getType() + "'" +
            ", descriptions='" + getDescriptions() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
