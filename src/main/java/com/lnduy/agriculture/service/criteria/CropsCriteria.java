package com.lnduy.agriculture.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lnduy.agriculture.domain.Crops} entity. This class is used
 * in {@link com.lnduy.agriculture.web.rest.CropsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /crops?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CropsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private FloatFilter volume;

    private StringFilter unit;

    private StringFilter description;

    private IntegerFilter enable;

    private LongFilter warehouseId;

    private Boolean distinct;

    public CropsCriteria() {}

    public CropsCriteria(CropsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.volume = other.volume == null ? null : other.volume.copy();
        this.unit = other.unit == null ? null : other.unit.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.enable = other.enable == null ? null : other.enable.copy();
        this.warehouseId = other.warehouseId == null ? null : other.warehouseId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CropsCriteria copy() {
        return new CropsCriteria(this);
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

    public FloatFilter getVolume() {
        return volume;
    }

    public FloatFilter volume() {
        if (volume == null) {
            volume = new FloatFilter();
        }
        return volume;
    }

    public void setVolume(FloatFilter volume) {
        this.volume = volume;
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

    public LongFilter getWarehouseId() {
        return warehouseId;
    }

    public LongFilter warehouseId() {
        if (warehouseId == null) {
            warehouseId = new LongFilter();
        }
        return warehouseId;
    }

    public void setWarehouseId(LongFilter warehouseId) {
        this.warehouseId = warehouseId;
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
        final CropsCriteria that = (CropsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(volume, that.volume) &&
            Objects.equals(unit, that.unit) &&
            Objects.equals(description, that.description) &&
            Objects.equals(enable, that.enable) &&
            Objects.equals(warehouseId, that.warehouseId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, volume, unit, description, enable, warehouseId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CropsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (volume != null ? "volume=" + volume + ", " : "") +
            (unit != null ? "unit=" + unit + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (enable != null ? "enable=" + enable + ", " : "") +
            (warehouseId != null ? "warehouseId=" + warehouseId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
