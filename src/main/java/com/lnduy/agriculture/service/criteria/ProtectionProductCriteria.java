package com.lnduy.agriculture.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lnduy.agriculture.domain.ProtectionProduct} entity. This class is used
 * in {@link com.lnduy.agriculture.web.rest.ProtectionProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /protection-products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProtectionProductCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private StringFilter type;

    private FloatFilter volume;

    private StringFilter unit;

    private IntegerFilter enable;

    private LongFilter warehouseId;

    private LongFilter taskId;

    private Boolean distinct;

    public ProtectionProductCriteria() {}

    public ProtectionProductCriteria(ProtectionProductCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.volume = other.volume == null ? null : other.volume.copy();
        this.unit = other.unit == null ? null : other.unit.copy();
        this.enable = other.enable == null ? null : other.enable.copy();
        this.warehouseId = other.warehouseId == null ? null : other.warehouseId.copy();
        this.taskId = other.taskId == null ? null : other.taskId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProtectionProductCriteria copy() {
        return new ProtectionProductCriteria(this);
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
        final ProtectionProductCriteria that = (ProtectionProductCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
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
        return Objects.hash(id, name, description, type, volume, unit, enable, warehouseId, taskId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProtectionProductCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
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
