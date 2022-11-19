package com.lnduy.agriculture.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;

/**
 * A Monitoring.
 */
@Entity
@Table(name = "monitoring")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Monitoring implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "data_json")
    private String dataJson;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @ManyToOne
    @JsonIgnoreProperties(value = { "seasons", "devices", "soil" }, allowSetters = true)
    private Field field;

    @ManyToOne
    @JsonIgnoreProperties(value = { "category", "field" }, allowSetters = true)
    private Device device;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Monitoring id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataJson() {
        return this.dataJson;
    }

    public Monitoring dataJson(String dataJson) {
        this.setDataJson(dataJson);
        return this;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Monitoring createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Field getField() {
        return this.field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Monitoring field(Field field) {
        this.setField(field);
        return this;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Monitoring device(Device device) {
        this.setDevice(device);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Monitoring)) {
            return false;
        }
        return id != null && id.equals(((Monitoring) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Monitoring{" +
            "id=" + getId() +
            ", dataJson='" + getDataJson() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
