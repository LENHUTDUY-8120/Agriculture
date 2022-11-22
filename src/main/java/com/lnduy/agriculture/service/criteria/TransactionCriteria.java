package com.lnduy.agriculture.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lnduy.agriculture.domain.Transaction} entity. This class is used
 * in {@link com.lnduy.agriculture.web.rest.TransactionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter total;

    private BigDecimalFilter price;

    private StringFilter type;

    private StringFilter descriptions;

    private ZonedDateTimeFilter createdAt;

    private LongFilter seasonId;

    private Boolean distinct;

    public TransactionCriteria() {}

    public TransactionCriteria(TransactionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.total = other.total == null ? null : other.total.copy();
        this.price = other.price == null ? null : other.price.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.descriptions = other.descriptions == null ? null : other.descriptions.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.seasonId = other.seasonId == null ? null : other.seasonId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TransactionCriteria copy() {
        return new TransactionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public BigDecimalFilter getTotal() {
        return total;
    }

    public BigDecimalFilter total() {
        if (total == null) {
            total = new BigDecimalFilter();
        }
        return total;
    }

    public void setTotal(BigDecimalFilter total) {
        this.total = total;
    }

    public BigDecimalFilter getPrice() {
        return price;
    }

    public BigDecimalFilter price() {
        if (price == null) {
            price = new BigDecimalFilter();
        }
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public StringFilter getType() {
        return type;
    }

    public StringFilter type() {
        if (type == null) {
            type = new StringFilter();
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public StringFilter getDescriptions() {
        return descriptions;
    }

    public StringFilter descriptions() {
        if (descriptions == null) {
            descriptions = new StringFilter();
        }
        return descriptions;
    }

    public void setDescriptions(StringFilter descriptions) {
        this.descriptions = descriptions;
    }

    public ZonedDateTimeFilter getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTimeFilter createdAt() {
        if (createdAt == null) {
            createdAt = new ZonedDateTimeFilter();
        }
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTimeFilter createdAt) {
        this.createdAt = createdAt;
    }

    public LongFilter getSeasonId() {
        return seasonId;
    }

    public LongFilter seasonId() {
        if (seasonId == null) {
            seasonId = new LongFilter();
        }
        return seasonId;
    }

    public void setSeasonId(LongFilter seasonId) {
        this.seasonId = seasonId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionCriteria that = (TransactionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(total, that.total) &&
            Objects.equals(price, that.price) &&
            Objects.equals(type, that.type) &&
            Objects.equals(descriptions, that.descriptions) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(seasonId, that.seasonId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, total, price, type, descriptions, createdAt, seasonId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (total != null ? "total=" + total + ", " : "") +
            (price != null ? "price=" + price + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (descriptions != null ? "descriptions=" + descriptions + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (seasonId != null ? "seasonId=" + seasonId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
