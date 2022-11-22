package com.lnduy.agriculture.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lnduy.agriculture.domain.Warehouse} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseDTO implements Serializable {

    private Long id;

    private String name;

    private Double area;

    private String description;

    private Double latitude;

    private Double longitude;

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

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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
        if (!(o instanceof WarehouseDTO)) {
            return false;
        }

        WarehouseDTO warehouseDTO = (WarehouseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, warehouseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", area=" + getArea() +
            ", description='" + getDescription() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", enable=" + getEnable() +
            "}";
    }
}
