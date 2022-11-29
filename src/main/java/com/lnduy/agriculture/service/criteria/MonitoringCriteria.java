package com.lnduy.agriculture.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lnduy.agriculture.domain.Monitoring} entity. This class is used
 * in {@link com.lnduy.agriculture.web.rest.MonitoringResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /monitorings?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MonitoringCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DoubleFilter value;

    private LocalDateFilter createdAt;

    private LongFilter fieldId;

    private LongFilter deviceId;

    private Boolean distinct;

    public MonitoringCriteria() {}

    public MonitoringCriteria(MonitoringCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.value = other.value == null ? null : other.value.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.fieldId = other.fieldId == null ? null : other.fieldId.copy();
        this.deviceId = other.deviceId == null ? null : other.deviceId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public MonitoringCriteria copy() {
        return new MonitoringCriteria(this);
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

    public DoubleFilter getValue() {
        return value;
    }

    public void setValue(DoubleFilter value) {
        this.value = value;
    }

    public DoubleFilter value() {
        if (value == null) {
            value = new DoubleFilter();
        }
        return value;
    }

    public LocalDateFilter getCreatedAt() {
        return createdAt;
    }

    public LocalDateFilter createdAt() {
        if (createdAt == null) {
            createdAt = new LocalDateFilter();
        }
        return createdAt;
    }

    public void setCreatedAt(LocalDateFilter createdAt) {
        this.createdAt = createdAt;
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

    public LongFilter getDeviceId() {
        return deviceId;
    }

    public LongFilter deviceId() {
        if (deviceId == null) {
            deviceId = new LongFilter();
        }
        return deviceId;
    }

    public void setDeviceId(LongFilter deviceId) {
        this.deviceId = deviceId;
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
        final MonitoringCriteria that = (MonitoringCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(value, that.value) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(fieldId, that.fieldId) &&
            Objects.equals(deviceId, that.deviceId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, createdAt, fieldId, deviceId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MonitoringCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (value != null ? "value=" + value + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (fieldId != null ? "fieldId=" + fieldId + ", " : "") +
            (deviceId != null ? "deviceId=" + deviceId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
