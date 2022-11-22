package com.lnduy.agriculture.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lnduy.agriculture.domain.Warehouse} entity. This class is used
 * in {@link com.lnduy.agriculture.web.rest.WarehouseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /warehouses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private DoubleFilter area;

    private StringFilter description;

    private DoubleFilter latitude;

    private DoubleFilter longitude;

    private IntegerFilter enable;

    private LongFilter suppliesId;

    private LongFilter protectionProductId;

    private LongFilter fertilizersId;

    private LongFilter cropsId;

    private Boolean distinct;

    public WarehouseCriteria() {}

    public WarehouseCriteria(WarehouseCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.area = other.area == null ? null : other.area.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.latitude = other.latitude == null ? null : other.latitude.copy();
        this.longitude = other.longitude == null ? null : other.longitude.copy();
        this.enable = other.enable == null ? null : other.enable.copy();
        this.suppliesId = other.suppliesId == null ? null : other.suppliesId.copy();
        this.protectionProductId = other.protectionProductId == null ? null : other.protectionProductId.copy();
        this.fertilizersId = other.fertilizersId == null ? null : other.fertilizersId.copy();
        this.cropsId = other.cropsId == null ? null : other.cropsId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public WarehouseCriteria copy() {
        return new WarehouseCriteria(this);
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

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
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

    public LongFilter getSuppliesId() {
        return suppliesId;
    }

    public LongFilter suppliesId() {
        if (suppliesId == null) {
            suppliesId = new LongFilter();
        }
        return suppliesId;
    }

    public void setSuppliesId(LongFilter suppliesId) {
        this.suppliesId = suppliesId;
    }

    public LongFilter getProtectionProductId() {
        return protectionProductId;
    }

    public LongFilter protectionProductId() {
        if (protectionProductId == null) {
            protectionProductId = new LongFilter();
        }
        return protectionProductId;
    }

    public void setProtectionProductId(LongFilter protectionProductId) {
        this.protectionProductId = protectionProductId;
    }

    public LongFilter getFertilizersId() {
        return fertilizersId;
    }

    public LongFilter fertilizersId() {
        if (fertilizersId == null) {
            fertilizersId = new LongFilter();
        }
        return fertilizersId;
    }

    public void setFertilizersId(LongFilter fertilizersId) {
        this.fertilizersId = fertilizersId;
    }

    public LongFilter getCropsId() {
        return cropsId;
    }

    public LongFilter cropsId() {
        if (cropsId == null) {
            cropsId = new LongFilter();
        }
        return cropsId;
    }

    public void setCropsId(LongFilter cropsId) {
        this.cropsId = cropsId;
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
        final WarehouseCriteria that = (WarehouseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(area, that.area) &&
            Objects.equals(description, that.description) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(enable, that.enable) &&
            Objects.equals(suppliesId, that.suppliesId) &&
            Objects.equals(protectionProductId, that.protectionProductId) &&
            Objects.equals(fertilizersId, that.fertilizersId) &&
            Objects.equals(cropsId, that.cropsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            area,
            description,
            latitude,
            longitude,
            enable,
            suppliesId,
            protectionProductId,
            fertilizersId,
            cropsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (area != null ? "area=" + area + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (latitude != null ? "latitude=" + latitude + ", " : "") +
            (longitude != null ? "longitude=" + longitude + ", " : "") +
            (enable != null ? "enable=" + enable + ", " : "") +
            (suppliesId != null ? "suppliesId=" + suppliesId + ", " : "") +
            (protectionProductId != null ? "protectionProductId=" + protectionProductId + ", " : "") +
            (fertilizersId != null ? "fertilizersId=" + fertilizersId + ", " : "") +
            (cropsId != null ? "cropsId=" + cropsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
