package com.lnduy.agriculture.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lnduy.agriculture.domain.SoilType} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SoilTypeDTO implements Serializable {

    private Long id;

    private String name;

    private String descriptions;

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

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SoilTypeDTO)) {
            return false;
        }

        SoilTypeDTO soilTypeDTO = (SoilTypeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, soilTypeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SoilTypeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", descriptions='" + getDescriptions() + "'" +
            "}";
    }
}
