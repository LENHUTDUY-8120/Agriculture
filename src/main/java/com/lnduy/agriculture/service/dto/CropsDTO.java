package com.lnduy.agriculture.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lnduy.agriculture.domain.Crops} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CropsDTO implements Serializable {

    private Long id;

    private String name;

    private Float volume;

    private String unit;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CropsDTO)) {
            return false;
        }

        CropsDTO cropsDTO = (CropsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cropsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CropsDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", volume=" + getVolume() +
            ", unit='" + getUnit() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
