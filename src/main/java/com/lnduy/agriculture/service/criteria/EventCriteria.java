package com.lnduy.agriculture.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lnduy.agriculture.domain.Event} entity. This class is used
 * in {@link com.lnduy.agriculture.web.rest.EventResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /events?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter startDate;

    private ZonedDateTimeFilter endDate;

    private StringFilter title;

    private StringFilter descriptions;

    private StringFilter content;

    private ZonedDateTimeFilter startAt;

    private ZonedDateTimeFilter endAt;

    private LongFilter categoryId;

    private Boolean distinct;

    public EventCriteria() {}

    public EventCriteria(EventCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.startDate = other.startDate == null ? null : other.startDate.copy();
        this.endDate = other.endDate == null ? null : other.endDate.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.descriptions = other.descriptions == null ? null : other.descriptions.copy();
        this.content = other.content == null ? null : other.content.copy();
        this.startAt = other.startAt == null ? null : other.startAt.copy();
        this.endAt = other.endAt == null ? null : other.endAt.copy();
        this.categoryId = other.categoryId == null ? null : other.categoryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EventCriteria copy() {
        return new EventCriteria(this);
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

    public ZonedDateTimeFilter getStartDate() {
        return startDate;
    }

    public ZonedDateTimeFilter startDate() {
        if (startDate == null) {
            startDate = new ZonedDateTimeFilter();
        }
        return startDate;
    }

    public void setStartDate(ZonedDateTimeFilter startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTimeFilter getEndDate() {
        return endDate;
    }

    public ZonedDateTimeFilter endDate() {
        if (endDate == null) {
            endDate = new ZonedDateTimeFilter();
        }
        return endDate;
    }

    public void setEndDate(ZonedDateTimeFilter endDate) {
        this.endDate = endDate;
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

    public StringFilter getContent() {
        return content;
    }

    public StringFilter content() {
        if (content == null) {
            content = new StringFilter();
        }
        return content;
    }

    public void setContent(StringFilter content) {
        this.content = content;
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
        final EventCriteria that = (EventCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(title, that.title) &&
            Objects.equals(descriptions, that.descriptions) &&
            Objects.equals(content, that.content) &&
            Objects.equals(startAt, that.startAt) &&
            Objects.equals(endAt, that.endAt) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, endDate, title, descriptions, content, startAt, endAt, categoryId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (startDate != null ? "startDate=" + startDate + ", " : "") +
            (endDate != null ? "endDate=" + endDate + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (descriptions != null ? "descriptions=" + descriptions + ", " : "") +
            (content != null ? "content=" + content + ", " : "") +
            (startAt != null ? "startAt=" + startAt + ", " : "") +
            (endAt != null ? "endAt=" + endAt + ", " : "") +
            (categoryId != null ? "categoryId=" + categoryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
