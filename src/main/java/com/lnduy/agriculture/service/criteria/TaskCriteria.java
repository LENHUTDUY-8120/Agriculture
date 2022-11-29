package com.lnduy.agriculture.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lnduy.agriculture.domain.Task} entity. This class is used
 * in {@link com.lnduy.agriculture.web.rest.TaskResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tasks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaskCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter description;

    private IntegerFilter enable;

    private LocalDateFilter startDate;

    private LocalDateFilter endDate;

    private LongFilter seasonId;

    private LongFilter employeeId;

    private LongFilter suppliesId;

    private LongFilter protectionproductId;

    private LongFilter fertilizersId;

    private Boolean distinct;

    public TaskCriteria() {}

    public TaskCriteria(TaskCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.enable = other.enable == null ? null : other.enable.copy();
        this.startDate = other.startDate == null ? null : other.startDate.copy();
        this.endDate = other.endDate == null ? null : other.endDate.copy();
        this.seasonId = other.seasonId == null ? null : other.seasonId.copy();
        this.employeeId = other.employeeId == null ? null : other.employeeId.copy();
        this.suppliesId = other.suppliesId == null ? null : other.suppliesId.copy();
        this.protectionproductId = other.protectionproductId == null ? null : other.protectionproductId.copy();
        this.fertilizersId = other.fertilizersId == null ? null : other.fertilizersId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TaskCriteria copy() {
        return new TaskCriteria(this);
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

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
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

    public LocalDateFilter getStartDate() {
        return startDate;
    }

    public LocalDateFilter startDate() {
        if (startDate == null) {
            startDate = new LocalDateFilter();
        }
        return startDate;
    }

    public void setStartDate(LocalDateFilter startDate) {
        this.startDate = startDate;
    }

    public LocalDateFilter getEndDate() {
        return endDate;
    }

    public LocalDateFilter endDate() {
        if (endDate == null) {
            endDate = new LocalDateFilter();
        }
        return endDate;
    }

    public void setEndDate(LocalDateFilter endDate) {
        this.endDate = endDate;
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

    public LongFilter getEmployeeId() {
        return employeeId;
    }

    public LongFilter employeeId() {
        if (employeeId == null) {
            employeeId = new LongFilter();
        }
        return employeeId;
    }

    public void setEmployeeId(LongFilter employeeId) {
        this.employeeId = employeeId;
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

    public LongFilter getProtectionproductId() {
        return protectionproductId;
    }

    public LongFilter protectionproductId() {
        if (protectionproductId == null) {
            protectionproductId = new LongFilter();
        }
        return protectionproductId;
    }

    public void setProtectionproductId(LongFilter protectionproductId) {
        this.protectionproductId = protectionproductId;
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
        final TaskCriteria that = (TaskCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(enable, that.enable) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(seasonId, that.seasonId) &&
            Objects.equals(employeeId, that.employeeId) &&
            Objects.equals(suppliesId, that.suppliesId) &&
            Objects.equals(protectionproductId, that.protectionproductId) &&
            Objects.equals(fertilizersId, that.fertilizersId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            description,
            enable,
            startDate,
            endDate,
            seasonId,
            employeeId,
            suppliesId,
            protectionproductId,
            fertilizersId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (enable != null ? "enable=" + enable + ", " : "") +
            (startDate != null ? "startDate=" + startDate + ", " : "") +
            (endDate != null ? "endDate=" + endDate + ", " : "") +
            (seasonId != null ? "seasonId=" + seasonId + ", " : "") +
            (employeeId != null ? "employeeId=" + employeeId + ", " : "") +
            (suppliesId != null ? "suppliesId=" + suppliesId + ", " : "") +
            (protectionproductId != null ? "protectionproductId=" + protectionproductId + ", " : "") +
            (fertilizersId != null ? "fertilizersId=" + fertilizersId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
