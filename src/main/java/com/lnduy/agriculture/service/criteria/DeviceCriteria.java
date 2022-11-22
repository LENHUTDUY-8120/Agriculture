package com.lnduy.agriculture.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lnduy.agriculture.domain.Device} entity. This class is used
 * in {@link com.lnduy.agriculture.web.rest.DeviceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /devices?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeviceCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter code;

    private StringFilter ip;

    private StringFilter property;

    private StringFilter type;

    private IntegerFilter enable;

    private LongFilter categoryId;

    private LongFilter fieldId;

    private Boolean distinct;

    public DeviceCriteria() {}

    public DeviceCriteria(DeviceCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.ip = other.ip == null ? null : other.ip.copy();
        this.property = other.property == null ? null : other.property.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.enable = other.enable == null ? null : other.enable.copy();
        this.categoryId = other.categoryId == null ? null : other.categoryId.copy();
        this.fieldId = other.fieldId == null ? null : other.fieldId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DeviceCriteria copy() {
        return new DeviceCriteria(this);
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

    public StringFilter getIp() {
        return ip;
    }

    public StringFilter ip() {
        if (ip == null) {
            ip = new StringFilter();
        }
        return ip;
    }

    public void setIp(StringFilter ip) {
        this.ip = ip;
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

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            categoryId = new LongFilter();
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
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
        final DeviceCriteria that = (DeviceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(code, that.code) &&
            Objects.equals(ip, that.ip) &&
            Objects.equals(property, that.property) &&
            Objects.equals(type, that.type) &&
            Objects.equals(enable, that.enable) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(fieldId, that.fieldId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code, ip, property, type, enable, categoryId, fieldId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeviceCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (ip != null ? "ip=" + ip + ", " : "") +
            (property != null ? "property=" + property + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (enable != null ? "enable=" + enable + ", " : "") +
            (categoryId != null ? "categoryId=" + categoryId + ", " : "") +
            (fieldId != null ? "fieldId=" + fieldId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
