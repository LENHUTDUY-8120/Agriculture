package com.lnduy.agriculture.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lnduy.agriculture.domain.Field} entity. This class is used
 * in {@link com.lnduy.agriculture.web.rest.FieldResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /fields?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FieldCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter name;

    private StringFilter geoJson;

    private DoubleFilter area;

    private StringFilter descriptions;

    private IntegerFilter enable;

    private DoubleFilter latitude;

    private DoubleFilter longitude;

    private LongFilter seasonId;

    private LongFilter deviceId;

    private LongFilter soilId;

    private Boolean distinct;

    public FieldCriteria() {}

    public FieldCriteria(FieldCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.geoJson = other.geoJson == null ? null : other.geoJson.copy();
        this.area = other.area == null ? null : other.area.copy();
        this.descriptions = other.descriptions == null ? null : other.descriptions.copy();
        this.enable = other.enable == null ? null : other.enable.copy();
        this.latitude = other.latitude == null ? null : other.latitude.copy();
        this.longitude = other.longitude == null ? null : other.longitude.copy();
        this.seasonId = other.seasonId == null ? null : other.seasonId.copy();
        this.deviceId = other.deviceId == null ? null : other.deviceId.copy();
        this.soilId = other.soilId == null ? null : other.soilId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public FieldCriteria copy() {
        return new FieldCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public StringFilter code() {
        if (code == null) {
            code = new StringFilter();
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getGeoJson() {
        return geoJson;
    }

    public StringFilter geoJson() {
        if (geoJson == null) {
            geoJson = new StringFilter();
        }
        return geoJson;
    }

    public void setGeoJson(StringFilter geoJson) {
        this.geoJson = geoJson;
    }

    public DoubleFilter getArea() {
        return area;
    }

    public DoubleFilter area() {
        if (area == null) {
            area = new DoubleFilter();
        }
        return area;
    }

    public void setArea(DoubleFilter area) {
        this.area = area;
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

    public DoubleFilter getLatitude() {
        return latitude;
    }

    public DoubleFilter latitude() {
        if (latitude == null) {
            latitude = new DoubleFilter();
        }
        return latitude;
    }

    public void setLatitude(DoubleFilter latitude) {
        this.latitude = latitude;
    }

    public DoubleFilter getLongitude() {
        return longitude;
    }

    public DoubleFilter longitude() {
        if (longitude == null) {
            longitude = new DoubleFilter();
        }
        return longitude;
    }

    public void setLongitude(DoubleFilter longitude) {
        this.longitude = longitude;
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

    public LongFilter getSoilId() {
        return soilId;
    }

    public LongFilter soilId() {
        if (soilId == null) {
            soilId = new LongFilter();
        }
        return soilId;
    }

    public void setSoilId(LongFilter soilId) {
        this.soilId = soilId;
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
        final FieldCriteria that = (FieldCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(name, that.name) &&
            Objects.equals(geoJson, that.geoJson) &&
            Objects.equals(area, that.area) &&
            Objects.equals(descriptions, that.descriptions) &&
            Objects.equals(enable, that.enable) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(seasonId, that.seasonId) &&
            Objects.equals(deviceId, that.deviceId) &&
            Objects.equals(soilId, that.soilId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, geoJson, area, descriptions, enable, latitude, longitude, seasonId, deviceId, soilId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FieldCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (geoJson != null ? "geoJson=" + geoJson + ", " : "") +
            (area != null ? "area=" + area + ", " : "") +
            (descriptions != null ? "descriptions=" + descriptions + ", " : "") +
            (enable != null ? "enable=" + enable + ", " : "") +
            (latitude != null ? "latitude=" + latitude + ", " : "") +
            (longitude != null ? "longitude=" + longitude + ", " : "") +
            (seasonId != null ? "seasonId=" + seasonId + ", " : "") +
            (deviceId != null ? "deviceId=" + deviceId + ", " : "") +
            (soilId != null ? "soilId=" + soilId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
