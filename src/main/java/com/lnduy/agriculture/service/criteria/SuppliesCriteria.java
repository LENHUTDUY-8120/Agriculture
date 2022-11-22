package com.lnduy.agriculture.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lnduy.agriculture.domain.Supplies} entity. This class is used
 * in {@link com.lnduy.agriculture.web.rest.SuppliesResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /supplies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SuppliesCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter property;

    private StringFilter type;

    private FloatFilter volume;

    private StringFilter unit;

    private IntegerFilter enable;

    private LongFilter warehouseId;

    private LongFilter taskId;

    private Boolean distinct;

    public SuppliesCriteria() {}

    public SuppliesCriteria(SuppliesCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.property = other.property == null ? null : other.property.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.volume = other.volume == null ? null : other.volume.copy();
        this.unit = other.unit == null ? null : other.unit.copy();
        this.enable = other.enable == null ? null : other.enable.copy();
        this.warehouseId = other.warehouseId == null ? null : other.warehouseId.copy();
        this.taskId = other.taskId == null ? null : other.taskId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SuppliesCriteria copy() {
        return new SuppliesCriteria(this);
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

    public StringFilter getProperty() {
        return property;
    }

    public StringFilter property() {
        if (property == null) {
            property = new StringFilter();
        }
        return property;
    }

    public void setProperty(StringFilter property) {
        this.property = property;
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

    public LongFilter getTaskId() {
        return taskId;
    }

    public LongFilter taskId() {
        if (taskId == null) {
            taskId = new LongFilter();
        }
        return taskId;
    }

    public void setTaskId(LongFilter taskId) {
        this.taskId = taskId;
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
        final SuppliesCriteria that = (SuppliesCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(property, that.property) &&
            Objects.equals(type, that.type) &&
            Objects.equals(volume, that.volume) &&
            Objects.equals(unit, that.unit) &&
            Objects.equals(enable, that.enable) &&
            Objects.equals(warehouseId, that.warehouseId) &&
            Objects.equals(taskId, that.taskId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, property, type, volume, unit, enable, warehouseId, taskId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SuppliesCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (property != null ? "property=" + property + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (volume != null ? "volume=" + volume + ", " : "") +
            (unit != null ? "unit=" + unit + ", " : "") +
            (enable != null ? "enable=" + enable + ", " : "") +
            (warehouseId != null ? "warehouseId=" + warehouseId + ", " : "") +
            (taskId != null ? "taskId=" + taskId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
