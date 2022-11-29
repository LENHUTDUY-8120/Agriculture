package com.lnduy.agriculture.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.lnduy.agriculture.domain.Season} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SeasonDTO implements Serializable {

    private Long id;

    private String crops;

    private Double totalCost;

    private Double cropYields;

    private Integer enable;

    private Float volume;

    private String unit;

    private Integer done;

    private LocalDate startAt;

    private LocalDate endAt;

    private CropsDTO crop;

    private FieldDTO field;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCrops() {
        return crops;
    }

    public void setCrops(String crops) {
        this.crops = crops;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Double getCropYields() {
        return cropYields;
    }

    public void setCropYields(Double cropYields) {
        this.cropYields = cropYields;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
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

    public Integer getDone() {
        return done;
    }

    public void setDone(Integer done) {
        this.done = done;
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

    public CropsDTO getCrop() {
        return crop;
    }

    public void setCrop(CropsDTO crop) {
        this.crop = crop;
    }

    public FieldDTO getField() {
        return field;
    }

    public void setField(FieldDTO field) {
        this.field = field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SeasonDTO)) {
            return false;
        }

        SeasonDTO seasonDTO = (SeasonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, seasonDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SeasonDTO{" +
            "id=" + getId() +
            ", crops='" + getCrops() + "'" +
            ", totalCost=" + getTotalCost() +
            ", cropYields=" + getCropYields() +
            ", enable=" + getEnable() +
            ", volume=" + getVolume() +
            ", unit='" + getUnit() + "'" +
            ", done=" + getDone() +
            ", startAt='" + getStartAt() + "'" +
            ", endAt='" + getEndAt() + "'" +
            ", crop=" + getCrop() +
            ", field=" + getField() +
            "}";
    }
}
