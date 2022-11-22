package com.lnduy.agriculture.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A Crops.
 */
@Entity
@Table(name = "crops")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Crops implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "volume")
    private Float volume;

    @Column(name = "unit")
    private String unit;

    @Column(name = "description")
    private String description;

    @Column(name = "enable")
    private Integer enable;

    @ManyToOne
    @JsonIgnoreProperties(value = { "supplies", "protectionProducts", "fertilizers", "crops" }, allowSetters = true)
    private Warehouse warehouse;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Crops id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Crops name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getVolume() {
        return this.volume;
    }

    public Crops volume(Float volume) {
        this.setVolume(volume);
        return this;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public String getUnit() {
        return this.unit;
    }

    public Crops unit(String unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return this.description;
    }

    public Crops description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEnable() {
        return this.enable;
    }

    public Crops enable(Integer enable) {
        this.setEnable(enable);
        return this;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Warehouse getWarehouse() {
        return this.warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Crops warehouse(Warehouse warehouse) {
        this.setWarehouse(warehouse);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Crops)) {
            return false;
        }
        return id != null && id.equals(((Crops) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Crops{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", volume=" + getVolume() +
            ", unit='" + getUnit() + "'" +
            ", description='" + getDescription() + "'" +
            ", enable=" + getEnable() +
            "}";
    }
}
