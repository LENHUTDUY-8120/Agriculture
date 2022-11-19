package com.lnduy.agriculture.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lnduy.agriculture.domain.Field} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FieldDTO implements Serializable {

    private Long id;

    private String code;

    private String name;

    private String geoJson;

    private Double area;

    private String descriptions;

    private Integer enable;

    private Double latitude;

    private Double longitude;

    private SoilTypeDTO soil;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeoJson() {
        return geoJson;
    }

    public void setGeoJson(String geoJson) {
        this.geoJson = geoJson;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
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

    public SoilTypeDTO getSoil() {
        return soil;
    }

    public void setSoil(SoilTypeDTO soil) {
        this.soil = soil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FieldDTO)) {
            return false;
        }

        FieldDTO fieldDTO = (FieldDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, fieldDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FieldDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", geoJson='" + getGeoJson() + "'" +
            ", area=" + getArea() +
            ", descriptions='" + getDescriptions() + "'" +
            ", enable=" + getEnable() +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", soil=" + getSoil() +
            "}";
    }
}
