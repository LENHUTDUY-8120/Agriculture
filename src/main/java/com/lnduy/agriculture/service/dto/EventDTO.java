package com.lnduy.agriculture.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.lnduy.agriculture.domain.Event} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventDTO implements Serializable {

    private Long id;

    private String title;

    private String descriptions;

    private String content;

    private LocalDate startAt;

    private LocalDate endAt;

    private EventCategoryDTO category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDate startAt) {
        this.startAt = startAt;
    }

    public LocalDate getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDate endAt) {
        this.endAt = endAt;
    }

    public EventCategoryDTO getCategory() {
        return category;
    }

    public void setCategory(EventCategoryDTO category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventDTO)) {
            return false;
        }

        EventDTO eventDTO = (EventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", descriptions='" + getDescriptions() + "'" +
            ", content='" + getContent() + "'" +
            ", startAt='" + getStartAt() + "'" +
            ", endAt='" + getEndAt() + "'" +
            ", category=" + getCategory() +
            "}";
    }
}
