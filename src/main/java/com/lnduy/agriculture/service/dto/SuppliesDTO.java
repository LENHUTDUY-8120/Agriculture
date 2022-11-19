package com.lnduy.agriculture.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lnduy.agriculture.domain.Supplies} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SuppliesDTO implements Serializable {

    private Long id;

    private String name;

    private String property;

    private String type;

    private Integer enable;

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

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SuppliesDTO)) {
            return false;
        }

        SuppliesDTO suppliesDTO = (SuppliesDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, suppliesDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SuppliesDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", property='" + getProperty() + "'" +
            ", type='" + getType() + "'" +
            ", enable=" + getEnable() +
            "}";
    }
}
