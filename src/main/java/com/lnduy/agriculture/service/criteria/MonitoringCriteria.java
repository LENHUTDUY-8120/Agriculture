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

    private StringFilter dataJson;

    private ZonedDateTimeFilter createdAt;

    private LongFilter fieldId;

    private LongFilter deviceId;

    private Boolean distinct;

    public MonitoringCriteria() {}

    public MonitoringCriteria(MonitoringCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.dataJson = other.dataJson == null ? null : other.dataJson.copy();
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

    public StringFilter getDataJson() {
        return dataJson;
    }

    public StringFilter dataJson() {
        if (dataJson == null) {
            dataJson = new StringFilter();
        }
        return dataJson;
    }

    public void setDataJson(StringFilter dataJson) {
        this.dataJson = dataJson;
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
            Objects.equals(dataJson, that.dataJson) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(fieldId, that.fieldId) &&
            Objects.equals(deviceId, that.deviceId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataJson, createdAt, fieldId, deviceId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MonitoringCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (dataJson != null ? "dataJson=" + dataJson + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (fieldId != null ? "fieldId=" + fieldId + ", " : "") +
            (deviceId != null ? "deviceId=" + deviceId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
