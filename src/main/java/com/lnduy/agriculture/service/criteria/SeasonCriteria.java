package com.lnduy.agriculture.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lnduy.agriculture.domain.Season} entity. This class is used
 * in {@link com.lnduy.agriculture.web.rest.SeasonResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /seasons?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SeasonCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter crops;

    private DoubleFilter totalCost;

    private DoubleFilter cropYields;

    private IntegerFilter enable;

    private StringFilter unit;

    private IntegerFilter done;

    private ZonedDateTimeFilter startAt;

    private ZonedDateTimeFilter endAt;

    private LongFilter transactionId;

    private LongFilter cropId;

    private LongFilter fieldId;

    private Boolean distinct;

    public SeasonCriteria() {}

    public SeasonCriteria(SeasonCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.crops = other.crops == null ? null : other.crops.copy();
        this.totalCost = other.totalCost == null ? null : other.totalCost.copy();
        this.cropYields = other.cropYields == null ? null : other.cropYields.copy();
        this.enable = other.enable == null ? null : other.enable.copy();
        this.unit = other.unit == null ? null : other.unit.copy();
        this.done = other.done == null ? null : other.done.copy();
        this.startAt = other.startAt == null ? null : other.startAt.copy();
        this.endAt = other.endAt == null ? null : other.endAt.copy();
        this.transactionId = other.transactionId == null ? null : other.transactionId.copy();
        this.cropId = other.cropId == null ? null : other.cropId.copy();
        this.fieldId = other.fieldId == null ? null : other.fieldId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SeasonCriteria copy() {
        return new SeasonCriteria(this);
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

    public StringFilter getCrops() {
        return crops;
    }

    public StringFilter crops() {
        if (crops == null) {
            crops = new StringFilter();
        }
        return crops;
    }

    public void setCrops(StringFilter crops) {
        this.crops = crops;
    }

    public DoubleFilter getTotalCost() {
        return totalCost;
    }

    public DoubleFilter totalCost() {
        if (totalCost == null) {
            totalCost = new DoubleFilter();
        }
        return totalCost;
    }

    public void setTotalCost(DoubleFilter totalCost) {
        this.totalCost = totalCost;
    }

    public DoubleFilter getCropYields() {
        return cropYields;
    }

    public DoubleFilter cropYields() {
        if (cropYields == null) {
            cropYields = new DoubleFilter();
        }
        return cropYields;
    }

    public void setCropYields(DoubleFilter cropYields) {
        this.cropYields = cropYields;
    }

    public IntegerFilter getEnable() {
        return enable;
    }

    public IntegerFilter enable() {
        if (enable == null) {
            enable = new IntegerFilter();
        }
        return enable;
    }

    public void setEnable(IntegerFilter enable) {
        this.enable = enable;
    }

    public StringFilter getUnit() {
        return unit;
    }

    public StringFilter unit() {
        if (unit == null) {
            unit = new StringFilter();
        }
        return unit;
    }

    public void setUnit(StringFilter unit) {
        this.unit = unit;
    }

    public IntegerFilter getDone() {
        return done;
    }

    public IntegerFilter done() {
        if (done == null) {
            done = new IntegerFilter();
        }
        return done;
    }

    public void setDone(IntegerFilter done) {
        this.done = done;
    }

    public ZonedDateTimeFilter getStartAt() {
        return startAt;
    }

    public ZonedDateTimeFilter startAt() {
        if (startAt == null) {
            startAt = new ZonedDateTimeFilter();
        }
        return startAt;
    }

    public void setStartAt(ZonedDateTimeFilter startAt) {
        this.startAt = startAt;
    }

    public ZonedDateTimeFilter getEndAt() {
        return endAt;
    }

    public ZonedDateTimeFilter endAt() {
        if (endAt == null) {
            endAt = new ZonedDateTimeFilter();
        }
        return endAt;
    }

    public void setEndAt(ZonedDateTimeFilter endAt) {
        this.endAt = endAt;
    }

    public LongFilter getTransactionId() {
        return transactionId;
    }

    public LongFilter transactionId() {
        if (transactionId == null) {
            transactionId = new LongFilter();
        }
        return transactionId;
    }

    public void setTransactionId(LongFilter transactionId) {
        this.transactionId = transactionId;
    }

    public LongFilter getCropId() {
        return cropId;
    }

    public LongFilter cropId() {
        if (cropId == null) {
            cropId = new LongFilter();
        }
        return cropId;
    }

    public void setCropId(LongFilter cropId) {
        this.cropId = cropId;
    }

    public LongFilter getFieldId() {
        return fieldId;
    }

    public LongFilter fieldId() {
        if (fieldId == null) {
            fieldId = new LongFilter();
        }
        return fieldId;
    }

    public void setFieldId(LongFilter fieldId) {
        this.fieldId = fieldId;
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
        final SeasonCriteria that = (SeasonCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(crops, that.crops) &&
            Objects.equals(totalCost, that.totalCost) &&
            Objects.equals(cropYields, that.cropYields) &&
            Objects.equals(enable, that.enable) &&
            Objects.equals(unit, that.unit) &&
            Objects.equals(done, that.done) &&
            Objects.equals(startAt, that.startAt) &&
            Objects.equals(endAt, that.endAt) &&
            Objects.equals(transactionId, that.transactionId) &&
            Objects.equals(cropId, that.cropId) &&
            Objects.equals(fieldId, that.fieldId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, crops, totalCost, cropYields, enable, unit, done, startAt, endAt, transactionId, cropId, fieldId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SeasonCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (crops != null ? "crops=" + crops + ", " : "") +
            (totalCost != null ? "totalCost=" + totalCost + ", " : "") +
            (cropYields != null ? "cropYields=" + cropYields + ", " : "") +
            (enable != null ? "enable=" + enable + ", " : "") +
            (unit != null ? "unit=" + unit + ", " : "") +
            (done != null ? "done=" + done + ", " : "") +
            (startAt != null ? "startAt=" + startAt + ", " : "") +
            (endAt != null ? "endAt=" + endAt + ", " : "") +
            (transactionId != null ? "transactionId=" + transactionId + ", " : "") +
            (cropId != null ? "cropId=" + cropId + ", " : "") +
            (fieldId != null ? "fieldId=" + fieldId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
