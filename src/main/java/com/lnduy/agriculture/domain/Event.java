package com.lnduy.agriculture.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;

/**
 * A Event.
 */
@Entity
@Table(name = "event")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "descriptions")
    private String descriptions;

    @Column(name = "content")
    private String content;

    @Column(name = "start_at")
    private LocalDate startAt;

    @Column(name = "end_at")
    private LocalDate endAt;

    @ManyToOne
    private EventCategory category;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Event id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Event title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescriptions() {
        return this.descriptions;
    }

    public Event descriptions(String descriptions) {
        this.setDescriptions(descriptions);
        return this;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getContent() {
        return this.content;
    }

    public Event content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getStartAt() {
        return this.startAt;
    }

    public Event startAt(LocalDate startAt) {
        this.setStartAt(startAt);
        return this;
    }

    public void setStartAt(LocalDate startAt) {
        this.startAt = startAt;
    }

    public LocalDate getEndAt() {
        return this.endAt;
    }

    public Event endAt(LocalDate endAt) {
        this.setEndAt(endAt);
        return this;
    }

    public void setEndAt(LocalDate endAt) {
        this.endAt = endAt;
    }

    public EventCategory getCategory() {
        return this.category;
    }

    public void setCategory(EventCategory eventCategory) {
        this.category = eventCategory;
    }

    public Event category(EventCategory eventCategory) {
        this.setCategory(eventCategory);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        return id != null && id.equals(((Event) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Event{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", descriptions='" + getDescriptions() + "'" +
            ", content='" + getContent() + "'" +
            ", startAt='" + getStartAt() + "'" +
            ", endAt='" + getEndAt() + "'" +
            "}";
    }
}
